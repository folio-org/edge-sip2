package org.folio.edge.sip2;

import static java.lang.Boolean.FALSE;
import static org.folio.edge.sip2.parser.Command.CHECKIN;
import static org.folio.edge.sip2.parser.Command.CHECKOUT;
import static org.folio.edge.sip2.parser.Command.END_PATRON_SESSION;
import static org.folio.edge.sip2.parser.Command.FEE_PAID;
import static org.folio.edge.sip2.parser.Command.ITEM_INFORMATION;
import static org.folio.edge.sip2.parser.Command.LOGIN;
import static org.folio.edge.sip2.parser.Command.PATRON_INFORMATION;
import static org.folio.edge.sip2.parser.Command.PATRON_STATUS_REQUEST;
import static org.folio.edge.sip2.parser.Command.RENEW;
import static org.folio.edge.sip2.parser.Command.RENEW_ALL;
import static org.folio.edge.sip2.parser.Command.REQUEST_ACS_RESEND;
import static org.folio.edge.sip2.parser.Command.REQUEST_SC_RESEND;
import static org.folio.edge.sip2.parser.Command.SC_STATUS;
import static org.folio.edge.sip2.parser.Command.UNKNOWN;

import com.google.inject.Guice;
import com.google.inject.Injector;
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
import io.vertx.ext.web.client.WebClient;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.folio.edge.sip2.cache.TokenCacheFactory;
import org.folio.edge.sip2.domain.PreviousMessage;
import org.folio.edge.sip2.handlers.CheckinHandler;
import org.folio.edge.sip2.handlers.CheckoutHandler;
import org.folio.edge.sip2.handlers.EndPatronSessionHandler;
import org.folio.edge.sip2.handlers.FeePaidHandler;
import org.folio.edge.sip2.handlers.HandlersFactory;
import org.folio.edge.sip2.handlers.ISip2RequestHandler;
import org.folio.edge.sip2.handlers.ItemInformationHandler;
import org.folio.edge.sip2.handlers.LoginHandler;
import org.folio.edge.sip2.handlers.PatronInformationHandler;
import org.folio.edge.sip2.handlers.PatronStatusHandler;
import org.folio.edge.sip2.handlers.RenewAllHandler;
import org.folio.edge.sip2.handlers.RenewHandler;
import org.folio.edge.sip2.metrics.Metrics;
import org.folio.edge.sip2.modules.ApplicationModule;
import org.folio.edge.sip2.modules.FolioResourceProviderModule;
import org.folio.edge.sip2.parser.Command;
import org.folio.edge.sip2.parser.Message;
import org.folio.edge.sip2.parser.Parser;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.TenantUtils;
import org.folio.edge.sip2.utils.WebClientUtils;

public class MainVerticle extends AbstractVerticle {

  private static final int HEALTH_CHECK_PORT = 8081;
  private static final String  HEALTH_CHECK_PATH = "/admin/health";
  private static final String IPADDRESS = "ipAddress";
  private Map<Command, ISip2RequestHandler> handlers;
  private List<NetServer> servers = new ArrayList<>();
  private final Logger log = LogManager.getLogger();
  private final Map<Integer, Metrics> metricsMap = new HashMap<>();
  private JsonObject multiTenantConfig = new JsonObject();
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
    return sc.encodePrettily();
  }

  @Override
  public void start(Promise<Void> startFuture) {
    log.debug("Startup configuration: {}", this::getSanitizedConfig);

    callAdminHealthCheckService();

    // Initialize the TokenCache
    TokenCacheFactory.initialize(config()
        .getInteger(SYS_TOKEN_CACHE_CAPACITY, DEFAULT_TOKEN_CACHE_CAPACITY));

    setupHanlders();

    var portList = determinePorts();

    AtomicInteger remainingServers = new AtomicInteger(portList.size());

    for (int port : portList) {
      NetServerOptions options = new NetServerOptions(
            config().getJsonObject("netServerOptions", new JsonObject()))
            .setPort(port);

      NetServer server = vertx.createNetServer(options);
      servers.add(server);

      final Metrics metrics = Metrics.getMetrics(port);
      metricsMap.putIfAbsent(port, metrics);

      server.connectHandler(socket -> {

        String clientAddress = socket.remoteAddress().host();

        ThreadContext.put(IPADDRESS, clientAddress);

        JsonObject tenantConfig = TenantUtils.lookupTenantConfigForIpAddress(multiTenantConfig,
              clientAddress, port);

        final SessionData sessionData = getSessionData(tenantConfig);
        final String messageDelimiter = tenantConfig.getString("messageDelimiter", "\r");

        socket.handler(RecordParser.newDelimited(messageDelimiter, buffer ->
            handleBuffer(buffer, socket, sessionData, messageDelimiter, metrics)));

        socket.exceptionHandler(t -> {
          log.error("Socket exception", t);
          metrics.socketError();
        });
      });

      JsonObject crOptionsJson = config().getJsonObject("tenantConfigRetrieverOptions");
      ConfigRetrieverOptions crOptions = new ConfigRetrieverOptions(crOptionsJson);
      configRetriever = ConfigRetriever.create(vertx, crOptions);

      // After tenant config is loaded, start listening for messages
      lsitenToMessages(Promise.promise(), server).onComplete(ar -> {
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

    if (Objects.isNull(sessionData.getTenant())) {
      String clientAddress = socket.remoteAddress().host();
      log.error("No tenant configured for address: {}  message ignored.",
          clientAddress);
      return;
    }

    final String messageString = buffer.getString(0, buffer.length(),
        sessionData.getCharset());

    Command command = UNKNOWN;

    try {
      final Parser parser = getParser(sessionData);

      // parsing
      final Message<Object> message = parser.parseMessage(messageString);

      command = message.getCommand();

      // process validation results
      if (!message.isValid()) {
        log.error("Message is invalid: {}", messageString);
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
        log.error("Error locating handler for command {}", commandName);
        sample.stop(metrics.commandTimer(command));
        return;
      }

      executeHandler(message, sessionData, messageDelimiter, handler, sample, socket, metrics);
    } catch (Exception ex) {
      String errorMessage = "Problems handling the request: " + ex.getMessage();
      log.error(errorMessage, ex);
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
   * Returns sessionData.
   * @param tenantConfig sip config details
   * @return sessionData
   */
  private SessionData getSessionData(JsonObject tenantConfig) {
    return SessionData.createSession(
      tenantConfig.getString("tenant"),
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

  private Future<Void> lsitenToMessages(Promise<Void> promise, NetServer server) {
    configRetriever.getConfig(ar -> {
      if (ar.succeeded()) {
        multiTenantConfig = ar.result();
        log.info("Tenant config loaded: {}", () -> multiTenantConfig.encodePrettily());

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
      multiTenantConfig = change.getNewConfiguration();
      log.info("Tenant config changed: {}", () -> multiTenantConfig.encodePrettily());
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
          log.info("Sip response {}", responseMsg);
          sample.stop(metrics.commandTimer(message.getCommand()));
          socket.write(responseMsg, sessionData.getCharset());
        }).onFailure(e -> {
          String errorMsg = "Failed to respond to request";
          log.error(errorMsg, e);
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
    log.info("Sending previous Sip response {}", prvMessage);
    sample.stop(metrics.commandTimer(command));
    socket.write(prvMessage, sessionData.getCharset());
  }

  /**
   * Initialize the handlers.
   */
  private void setupHanlders() {
    if (handlers == null) {
      String okapiUrl = config().getString("okapiUrl");
      final WebClient webClient = WebClientUtils.create(vertx, config());
      final Injector injector = Guice.createInjector(
          new FolioResourceProviderModule(okapiUrl, webClient),
          new ApplicationModule());
      handlers = new EnumMap<>(Command.class);
      handlers.put(CHECKOUT, injector.getInstance(CheckoutHandler.class));
      handlers.put(CHECKIN, injector.getInstance(CheckinHandler.class));
      handlers.put(SC_STATUS, HandlersFactory.getScStatusHandlerInstance(null, null,
          null, null, okapiUrl, webClient));
      handlers.put(REQUEST_ACS_RESEND, HandlersFactory.getACSResendHandler());
      handlers.put(LOGIN, injector.getInstance(LoginHandler.class));
      handlers.put(PATRON_INFORMATION, injector.getInstance(PatronInformationHandler.class));
      handlers.put(PATRON_STATUS_REQUEST, injector.getInstance(PatronStatusHandler.class));
      handlers.put(REQUEST_SC_RESEND, HandlersFactory.getInvalidMessageHandler());
      handlers.put(END_PATRON_SESSION, injector.getInstance(EndPatronSessionHandler.class));
      handlers.put(FEE_PAID, injector.getInstance(FeePaidHandler.class));
      handlers.put(ITEM_INFORMATION, injector.getInstance(ItemInformationHandler.class));
      handlers.put(RENEW,  injector.getInstance(RenewHandler.class));
      handlers.put(RENEW_ALL,  injector.getInstance(RenewAllHandler.class));
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

    var msg = "Listening for /admin/health requests at port " + HEALTH_CHECK_PORT;
    httpServer.listen(HEALTH_CHECK_PORT)
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

      final byte [] bytes = sb.toString().getBytes(Charset.forName(sessionData.getCharset()));
      int checksum = 0;
      for (final byte b : bytes) {
        checksum += b & 0xff;
      }

      checksum = -checksum & 0xffff;
      sb.append(String.format("%04X", checksum)).append(messageDelimiter);

      result = sb.toString();
    } else {
      result = response + messageDelimiter;
    }

    return result;
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
      log.debug("requiredResending is FALSE");
      return false;
    } else {
      log.debug("requiredResending is TRUE");
      return currentMessage.getChecksumsString().equals(prevMessage.getPreviousRequestChecksum())
        && currentMessage.getSequenceNumber() == prevMessage.getPreviousRequestSequenceNo();
    }
  }
}
