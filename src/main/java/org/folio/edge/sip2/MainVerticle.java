package org.folio.edge.sip2;

import static java.lang.Boolean.FALSE;
import static org.apache.commons.lang3.StringEscapeUtils.ESCAPE_JAVA;
import static org.folio.edge.sip2.domain.TenantResolutionContext.createContextForConnectPhase;
import static org.folio.edge.sip2.parser.Command.LOGIN;
import static org.folio.edge.sip2.parser.Command.REQUEST_ACS_RESEND;
import static org.folio.edge.sip2.parser.Command.UNKNOWN;
import static org.folio.edge.sip2.utils.LogUtils.callWithContext;
import static org.folio.edge.sip2.utils.Utils.getEnvOrDefault;

import com.google.inject.Guice;
import com.google.inject.Key;
import io.micrometer.core.instrument.Timer;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.pointer.JsonPointer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.StringUtils;
import org.folio.edge.sip2.cache.TokenCacheFactory;
import org.folio.edge.sip2.domain.ConnectionDetails;
import org.folio.edge.sip2.domain.PreviousMessage;
import org.folio.edge.sip2.handlers.ISip2RequestHandler;
import org.folio.edge.sip2.metrics.Metrics;
import org.folio.edge.sip2.modules.ApplicationModule;
import org.folio.edge.sip2.modules.FolioResourceProviderModule;
import org.folio.edge.sip2.modules.RequestHandlerModule;
import org.folio.edge.sip2.parser.Command;
import org.folio.edge.sip2.parser.Message;
import org.folio.edge.sip2.parser.Parser;
import org.folio.edge.sip2.service.config.TenantConfigurationService;
import org.folio.edge.sip2.service.tenant.Sip2TenantService;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.Sip2LogAdapter;
import org.folio.edge.sip2.utils.WebClientUtils;

public class MainVerticle extends AbstractVerticle {

  private static final String HEALTH_CHECK_PORT_ENV_VAR = "HEALTH_CHECK_PORT";
  private static final String HEALTH_CHECK_PORT_PROPERTY = "healthCheckPort";
  private static final String HEALTH_CHECK_PATH = "/admin/health";
  private static final int HEALTH_CHECK_DEFAULT_PORT = 8081;

  private static final Sip2LogAdapter log = Sip2LogAdapter.getLogger(MainVerticle.class);
  private final Map<Integer, Metrics> metricsMap = new HashMap<>();

  private Sip2TenantService tenantResolver;
  private TenantConfigurationService tenantConfigurationService;
  private Map<Command, ISip2RequestHandler> handlers;

  private List<NetServer> servers = new ArrayList<>();
  private ConfigRetriever configRetriever;

  public static final int DEFAULT_TOKEN_CACHE_CAPACITY = 100;
  public static final String SYS_TOKEN_CACHE_CAPACITY = "token_cache_capacity";

  /**
   * Construct the {@code MainVerticle}.
   */
  public MainVerticle() {
    super();
  }

  public MainVerticle(Map<Command, ISip2RequestHandler> handlers) {
    this.handlers = handlers;
  }

  private String getSanitizedConfig() {
    JsonObject sc = config().copy();
    JsonPointer.from("/tenantConfigRetrieverOptions/stores/0/config").writeJson(sc, "******");
    return sc.encode();
  }

  @Override
  public void start(Promise<Void> startFuture) {
    log.debug("Startup configuration: {}", this::getSanitizedConfig);

    callAdminHealthCheckService();

    // Initialize the TokenCache
    TokenCacheFactory.initialize(config()
        .getInteger(SYS_TOKEN_CACHE_CAPACITY, DEFAULT_TOKEN_CACHE_CAPACITY));

    setupGuiceContext();

    var portList = determinePorts();

    AtomicInteger remainingServers = new AtomicInteger(portList.size());

    for (int port : portList) {
      NetServerOptions options = new NetServerOptions(
            config().getJsonObject("netServerOptions", new JsonObject()))
            .setPort(port)
            .setUseProxyProtocol(config().getBoolean("haProxy", false));

      NetServer server = vertx.createNetServer(options);
      servers.add(server);

      final Metrics metrics = Metrics.getMetrics(port);
      metricsMap.putIfAbsent(port, metrics);

      server.connectHandler(socket -> {
        var clientAddress = socket.remoteAddress().host();
        var connectionDetails = ConnectionDetails.of(port, clientAddress);
        var multiTenantConfig = tenantConfigurationService.getConfiguration();
        var context = createContextForConnectPhase(multiTenantConfig, connectionDetails);
        var tenantConfig = tenantResolver.findConfiguration(context).orElseGet(() -> {
          log.debug("Multi-tenant configuration not found, using: {}", multiTenantConfig::encode);
          return multiTenantConfig;
        });

        var sessionData = getSessionData(tenantConfig);
        var messageDelimiter = tenantConfig.getString("messageDelimiter", "\r");

        logNewConnectionDetails(connectionDetails, sessionData, messageDelimiter);
        socket.handler(RecordParser.newDelimited(messageDelimiter, buffer ->
            handleBuffer(buffer, socket, sessionData, messageDelimiter, metrics)));

        socket.exceptionHandler(t -> {
          log.error(sessionData, "Socket exception", t);
          metrics.socketError();
        });
      });

      JsonObject crOptionsJson = config().getJsonObject("tenantConfigRetrieverOptions");
      ConfigRetrieverOptions crOptions = new ConfigRetrieverOptions(crOptionsJson);
      configRetriever = ConfigRetriever.create(vertx, crOptions);

      // After tenant config is loaded, start listening for messages
      listenToMessages(Promise.promise(), server).onComplete(ar -> {
        if (ar.failed()) {
          startFuture.fail(ar.cause());
        } else if (remainingServers.decrementAndGet() == 0) {
          startFuture.complete();
        }
      });
    }
  }

  /**
   * Handles the processing of incoming buffer data from the socket connection.
   *
   * @param buffer The buffer containing the incoming message.
   * @param socket The NetSocket connection used for communication.
   * @param sessionData The session-specific data used for processing.
   * @param messageDelimiter The delimiter used to separate messages.
   * @param metrics The metrics object used for recording performance data.
   */
  private void handleBuffer(Buffer buffer, NetSocket socket, SessionData sessionData,
                            String messageDelimiter, Metrics metrics) {
    final Timer.Sample sample = metrics.sample();

    final String messageString = buffer.getString(0, buffer.length(),
        sessionData.getCharset());

    Command command = UNKNOWN;

    try {
      final Parser parser = getParser(sessionData);

      // parsing
      final Message<Object> message = callWithContext(
          sessionData, () -> parser.parseMessage(messageString));

      command = message.getCommand();

      if (command != LOGIN && Objects.isNull(sessionData.getTenant())) {
        String clientAddress = socket.remoteAddress().host();
        log.error(sessionData, "No tenant configured for address: {}  message ignored.",
            clientAddress);
        return;
      }

      // process validation results
      if (!message.isValid()) {
        log.error(sessionData, "Message is invalid: {}", messageString);
        handleInvalidMessage(message, socket, sessionData, messageDelimiter, sample, metrics);
        return;
      }

      // check if the previous message needs resending
      if (requiredResending(sessionData, message)) {
        resendPreviousMessage(sessionData, sample, metrics, socket, command);
        return;
      }

      ISip2RequestHandler handler = handlers.get(command);

      if (handler == null) {
        String commandName = command.name();
        log.error(sessionData, "Error locating handler for command {}", commandName);
        sample.stop(metrics.commandTimer(command));
        return;
      }

      executeHandler(message, sessionData, messageDelimiter, handler, sample, socket, metrics);
    } catch (Exception ex) {
      String errorMessage = "Problems handling the request: " + ex.getMessage();
      log.error(sessionData, errorMessage, ex);
      sample.stop(metrics.commandTimer(command));
      socket.write(errorMessage + messageDelimiter, sessionData.getCharset());
      metrics.requestError();
    }
  }

  /**
   * Creates and configures a Parser based on the given session data.
   *
   * @param sessionData The session data containing configuration details.
   * @return A configured Parser instance.
   */
  private Parser getParser(SessionData sessionData) {
    return Parser.builder()
      .delimiter(sessionData.getFieldDelimiter())
      .charset(Charset.forName(sessionData.getCharset()))
      .errorDetectionEnabled(sessionData.isErrorDetectionEnabled())
      .timezone(sessionData.getTimeZone())
      .build();
  }

  /**
   * Returns a new {@link SessionData} object.
   *
   * <p>
   * {@code tenant} in configuration is nullable, and then it can be resolved during the login
   * operation based on username/location code (if enabled).
   * </p>
   *
   * @param tenantConfig - sip config details
   * @return new {@link SessionData} instance
   */
  private static SessionData getSessionData(JsonObject tenantConfig) {
    return SessionData.createSession(
      tenantConfig.getString("tenant", ""),
      tenantConfig.getString("fieldDelimiter", "|").charAt(0),
      tenantConfig.getBoolean("errorDetectionEnabled", FALSE),
      tenantConfig.getString("charset", "IBM850"));
  }

  private List<Integer> determinePorts() {

    Object portObject = config().getValue("port");
    List<Integer> portList = new ArrayList<>();

    if (portObject == null) {
      throw new IllegalArgumentException("Port configuration cannot be null");
    }

    if (portObject instanceof Integer integer) {
      portList.add(integer);
    } else if (portObject instanceof JsonArray portsArray) {
      if (portsArray.isEmpty()) {
        throw new IllegalArgumentException("Port configuration list cannot be empty");
      }

      portsArray.forEach(port -> {
        if (!(port instanceof Integer integerPort)) {
          throw new IllegalArgumentException("Port value " + port + " is not an integer");
        }
        portList.add(integerPort);
      });
    } else {
      throw new IllegalArgumentException("Port configuration must be an integer "
        + "or a list of integers");
    }

    return portList;

  }

  private Future<Void> listenToMessages(Promise<Void> promise, NetServer server) {
    configRetriever.getConfig(ar -> {
      if (ar.succeeded()) {
        var multiTenantConfig = ar.result();
        tenantConfigurationService.updateConfiguration(multiTenantConfig);
        log.info("Tenant config loaded: {}", multiTenantConfig::encode);

        server.listen(result -> {
          if (result.succeeded()) {
            log.info("Server is now listening!");
            promise.complete();
          } else {
            log.error("Failed to start server", result.cause());
            promise.fail(result.cause());
          }
        });

      } else {
        log.error("Failed to load tenant config", ar.cause());
        promise.fail(ar.cause());
      }
    });

    configRetriever.listen(change -> {
      var multiTenantConfig = change.getNewConfiguration();
      tenantConfigurationService.updateConfiguration(multiTenantConfig);
      log.info("Tenant config changed: {}", multiTenantConfig::encode);
    });

    return promise.future();
  }

  /**
   * Execute the command.
   * @param message message
   * @param sessionData sessionData
   * @param messageDelimiter messageDelimiter
   * @param handler handler
   * @param sample sample
   * @param socket socket
   * @param metrics metrics
   */
  private void executeHandler(Message<Object> message,
                              SessionData sessionData,
                              String messageDelimiter,
                              ISip2RequestHandler handler,
                              Timer.Sample sample,
                              NetSocket socket,
                              Metrics metrics) {
    handler
        .execute(message.getRequest(), sessionData)
        .onSuccess(result -> {
          final String responseMsg;
          if (message.getCommand() == REQUEST_ACS_RESEND) {
            // we don't want to modify the response
            responseMsg = result;
          } else {
            responseMsg = formatResponse(result, message, sessionData,
            messageDelimiter);
          }
          handler.writeHistory(sessionData, message, responseMsg);
          log.info(sessionData, "Sip response {}", () -> getEscapedString(responseMsg));
          sample.stop(metrics.commandTimer(message.getCommand()));
          socket.write(responseMsg, sessionData.getCharset());
        }).onFailure(e -> {
          String errorMsg = "Failed to respond to request";
          log.error(sessionData, errorMsg, e);
          String responseMessage = (String) sessionData.getErrorResponseMessage();
          if (responseMessage != null) {
            handler.writeHistory(sessionData, message, responseMessage);
            responseMessage += messageDelimiter;
          }
          sample.stop(metrics.commandTimer(message.getCommand()));
          socket.write(responseMessage != null ? responseMessage
              : e.getMessage() + messageDelimiter,
              sessionData.getCharset());
          metrics.responseError();
        });
  }

  /**
   * Resend the previous message.
   * @param sessionData sessionData
   * @param sample sample
   * @param metrics metrics
   * @param socket socket
   * @param command command
   */
  private void resendPreviousMessage(SessionData sessionData,
                                     Timer.Sample sample,
                                     Metrics metrics,
                                     NetSocket socket,
                                     Command command) {
    String prvMessage = sessionData
        .getPreviousMessage()
        .getPreviousMessageResponse();
    log.info(sessionData, "Sending previous Sip response {}", () -> getEscapedString(prvMessage));
    sample.stop(metrics.commandTimer(command));
    socket.write(prvMessage, sessionData.getCharset());
  }

  /**
   * Initialize the handlers.
   */
  private void setupGuiceContext() {
    if (handlers == null) {
      var okapiUrl = config().getString("okapiUrl");
      var webClient = WebClientUtils.create(vertx, config());
      var injector = Guice.createInjector(
          new FolioResourceProviderModule(okapiUrl, webClient),
          new ApplicationModule(),
          new RequestHandlerModule()
      );

      handlers = injector.getInstance(new Key<>() {});
      tenantResolver = injector.getInstance(Sip2TenantService.class);
      tenantConfigurationService = injector.getInstance(TenantConfigurationService.class);
    }
  }

  private void callAdminHealthCheckService() {
    HttpServer httpServer = vertx.createHttpServer();

    httpServer.requestHandler(request -> {
      log.debug("path : {}", request.path());
      HttpServerResponse response = request.response();
      if (request.path().equals(HEALTH_CHECK_PATH)) {
        response.setStatusCode(200);
        response.putHeader("Content-Type", "text/plain");
        response.end("OK");
        log.info("Admin health check service response message : {}", response.getStatusMessage());
      } else {
        response.setStatusCode(404).end();
      }
    });

    var healthCheckPort = getEnvOrDefault(HEALTH_CHECK_PORT_PROPERTY,
        HEALTH_CHECK_PORT_ENV_VAR, HEALTH_CHECK_DEFAULT_PORT, Integer::parseInt);
    var msg = "Listening for /admin/health requests at port " + healthCheckPort;
    httpServer.listen(healthCheckPort)
        .onSuccess(x -> log.info("{} now", msg))
        .onFailure(e -> log.error("{} failed: {}", msg, e.getMessage(), e));
  }

  @Override
  public void stop(Promise<Void> stopFuture) {
    configRetriever.close();

    List<Future<Void>> stopFutures = servers.stream()
        .map(NetServer::close)
        .toList();

    Future.all(stopFutures).onComplete(ar -> {
      if (ar.succeeded()) {
        metricsMap.values().forEach(Metrics::stop);
        stopFuture.complete();
        log.info("MainVerticle stopped successfully!");
      } else {
        log.error("Failed to stop MainVerticle", ar.cause());
        stopFuture.fail(ar.cause());
      }
    });
  }

  private void handleInvalidMessage(
      Message<Object> message,
      NetSocket socket,
      SessionData sessionData,
      String messageDelimiter,
      Timer.Sample sample,
      Metrics metrics) {
    if (sessionData.isErrorDetectionEnabled()) {
      //resends validation if checksum string does not match
      ISip2RequestHandler handler = handlers.get(Command.REQUEST_SC_RESEND);
      handler.execute(message.getRequest(), sessionData)
          .onSuccess(result -> {
            sample.stop(metrics.commandTimer(message.getCommand()));
            socket.write(formatResponse(result, message, sessionData,
                messageDelimiter, true), sessionData.getCharset());
          })
          .onFailure(e -> {
            log.error("Failed to send SC resend", e);
            metrics.scResendError();
            sample.stop(metrics.commandTimer(message.getCommand()));
          });
    } else {
      sample.stop(metrics.commandTimer(message.getCommand()));
      socket.write("Problems handling the request: " + messageDelimiter, sessionData.getCharset());
      metrics.invalidMessageError();
    }
  }

  private String formatResponse(String response, Message<Object> message, SessionData sessionData,
      String messageDelimiter) {
    return formatResponse(response, message, sessionData, messageDelimiter, false);
  }

  private String formatResponse(String response, Message<Object> message, SessionData sessionData,
      String messageDelimiter, boolean isSCResend) {
    final String result;

    if (sessionData.isErrorDetectionEnabled()) {
      final StringBuilder sb = new StringBuilder(response.length() + (isSCResend ? 6 : 9)
          + messageDelimiter.length())
          .append(response);
      // SC Resend messages never include a sequence number, but will include the checksum
      if (!isSCResend) {
        sb.append("AY").append(message.getSequenceNumber());
      }

      sb.append("AZ");

      var charset = Charset.forName(sessionData.getCharset());
      int checksum = getChecksum(sessionData, sb.toString(), charset);

      sb.append(String.format("%04X", checksum)).append(messageDelimiter);

      result = sb.toString();
    } else {
      result = response + messageDelimiter;
    }

    return result;
  }

  protected int getChecksum(SessionData sessionData, String message, Charset charset) {
    log.debug(sessionData, "Calculating checksum for {} using charset {}",
        () -> getEscapedString(message), () -> charset);
    final byte[] bytes = message.getBytes(charset);
    int checksum = 0;
    for (final byte b : bytes) {
      checksum += b & 0xff;
    }
    checksum = -checksum & 0xffff;
    return checksum;
  }

  /**
   * Method that evaluates whether or not the previous message needs to be resent.
   * Resending happens when the current message's checksum and sequence number
   * match the previous message's checksum and sequence number.
   *
   * @param currentMessage current message to check against the previous message
   * @return boolean indicating whether the message needs to be resent.
   */
  private boolean requiredResending(SessionData sessionData, Message<Object> currentMessage) {

    PreviousMessage prevMessage = sessionData.getPreviousMessage();

    if (prevMessage == null || !sessionData.isErrorDetectionEnabled()) {
      log.debug(sessionData, "requiredResending is FALSE");
      return false;
    } else {
      log.debug(sessionData, "requiredResending is TRUE");
      return currentMessage.getChecksumsString().equals(prevMessage.getPreviousRequestChecksum())
          && currentMessage.getSequenceNumber() == prevMessage.getPreviousRequestSequenceNo();
    }
  }

  private static void logNewConnectionDetails(ConnectionDetails cd,
      SessionData sessionData, String messageDelimiter) {
    log.debug("Handling a new connection: {}", () -> new JsonObject()
        .put("clientAddress", cd.getClientAddress() + ":" + cd.getClientPort())
        .put("sessionId", sessionData.getRequestId())
        .put("tenant", sessionData.getTenant())
        .put("messageDelimiter", messageDelimiter)
        .put("errorDetectionEnabled", sessionData.isErrorDetectionEnabled())
        .put("fieldDelimiter", sessionData.getFieldDelimiter())
        .put("charset", sessionData.getCharset())
        .encode()
    );
  }

  private static String getEscapedString(String input) {
    return StringUtils.isNotBlank(input) ? ESCAPE_JAVA.translate(input) : input;
  }
}
