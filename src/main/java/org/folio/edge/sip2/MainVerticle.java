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
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.pointer.JsonPointer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import java.nio.charset.Charset;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.folio.edge.core.Constants;
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

public class MainVerticle extends AbstractVerticle {

  private static final int HEALTH_CHECK_PORT = 8081;
  private static final String  HEALTH_CHECK_PATH = "/admin/health";
  private static final String IPADDRESS = "ipAddress";
  private static final String SYS_PORT = "port";
  private Map<Command, ISip2RequestHandler> handlers;
  private NetServer server;
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

    // initialize the TokenCache
    TokenCacheFactory.initialize(config()
        .getInteger(SYS_TOKEN_CACHE_CAPACITY, DEFAULT_TOKEN_CACHE_CAPACITY));

    // We need to reduce the complexity of this method...
    setupHanlders();

    //set Config object's defaults
    final int port = config().getInteger(SYS_PORT);
    log.info("Using port: {}", port);
    NetServerOptions options = new NetServerOptions(config()
          .getJsonObject("netServerOptions", new JsonObject()));
    options.setPort(port);

    // move port to httpServerOptions
    // initialize response compression

    log.info("Deployed verticle at port {}", port);

    final Metrics metrics = Metrics.getMetrics(port);
    metricsMap.putIfAbsent(port, metrics);

    server = vertx.createNetServer(options);

    server.connectHandler(socket -> {

      String clientAddress = socket.remoteAddress().host();
      ThreadContext.put(IPADDRESS, clientAddress);
      JsonObject tenantConfig = TenantUtils.lookupTenantConfigForIPaddress(multiTenantConfig,
          clientAddress);

      final SessionData sessionData = SessionData.createSession(
          tenantConfig.getString("tenant"),
          tenantConfig.getString("fieldDelimiter", "|").charAt(0),
          tenantConfig.getBoolean("errorDetectionEnabled", FALSE),
          tenantConfig.getString("charset", "IBM850"));
      final String messageDelimiter = tenantConfig.getString("messageDelimiter", "\r");

      socket.handler(RecordParser.newDelimited(messageDelimiter, buffer -> {
        final Timer.Sample sample = metrics.sample();

        if (Objects.isNull(sessionData.getTenant())) {
          log.error("No tenant configured for address: {}  message ignored.", clientAddress);
          return;
        }

        final String messageString = buffer.getString(0, buffer.length(), sessionData.getCharset());

        log.debug("Received message: {}", messageString);

        Command command = UNKNOWN;

        try {
          final Parser parser = Parser.builder()
              .delimiter(sessionData.getFieldDelimiter())
              .charset(Charset.forName(sessionData.getCharset()))
              .errorDetectionEnabled(sessionData.isErrorDetectionEnabled())
              .timezone(sessionData.getTimeZone())
              .build();

          //parsing
          final Message<Object> message = parser.parseMessage(messageString);

          command = message.getCommand();

          //process validation results
          if (!message.isValid()) {
            log.error("Message is invalid: {}", messageString);
            handleInvalidMessage(message, socket, sessionData, messageDelimiter, sample,
                metrics);
            return;
          }

          //check if the previous message needs resending
          if (requiredResending(sessionData, message)) {
            resendPreviousMessage(sessionData, sample,
                metrics, socket, command);
            return;
          }

          ISip2RequestHandler handler = handlers.get(command);

          if (handler == null) {
            log.error("Error locating handler for command {}", command.name());
            sample.stop(metrics.commandTimer(command));
            return;
          }

          executeHandler(message,
              sessionData, messageDelimiter,
              handler, sample,
              socket, metrics);
        } catch (Exception ex) {
          String message = "Problems handling the request: " + ex.getMessage();
          log.error(message, ex);
          // Return an error message for now for the sake of negative testing.
          // Will find a better way to handle negative test cases.
          sample.stop(metrics.commandTimer(command));
          socket.write(message + messageDelimiter, sessionData.getCharset());
          metrics.requestError();
        }
      }));
      socket.exceptionHandler(t -> {
        log.info("Socket exceptionHandler caught an issue, see error logs for more details");
        log.error("Socket exception", t);
        metrics.socketError();
      });
    });

    JsonObject crOptionsJson = config().getJsonObject("tenantConfigRetrieverOptions");
    ConfigRetrieverOptions crOptions = new ConfigRetrieverOptions(crOptionsJson);
    configRetriever = ConfigRetriever.create(vertx, crOptions);

    // after tenant config is loaded, start listening for messages
    lsitenToMessages(startFuture);

  }

  private void lsitenToMessages(Promise<Void> startFuture) {

    configRetriever.getConfig(ar -> {
      if (ar.succeeded()) {
        multiTenantConfig = ar.result();
        log.info("Tenant config loaded: {}", () -> multiTenantConfig.encodePrettily());

        server.listen(result -> {
          if (result.succeeded()) {
            log.info("MainVerticle deployed successfuly, server is now listening!");
            startFuture.complete();
          } else {
            log.error("Failed to deploy MainVerticle", result.cause());
            startFuture.fail(result.cause());
          }
        });

      } else {
        log.error("Failed to load tenant config", ar.cause());
        startFuture.fail(ar.cause());
      }
    });

    configRetriever.listen(change -> {
      multiTenantConfig = change.getNewConfiguration();
      log.info("Tenant config changed: {}", () -> multiTenantConfig.encodePrettily());
    });
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
      Integer timeout = config().getInteger(Constants.SYS_REQUEST_TIMEOUT_MS);
      WebClientOptions webClientOptions = initDefaultWebClientOptions(timeout);
      final WebClient webClient = WebClient.create(vertx, webClientOptions);
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

  private WebClientOptions initDefaultWebClientOptions(int timeout) {
    return new WebClientOptions()
      .setIdleTimeoutUnit(TimeUnit.MILLISECONDS).setIdleTimeout(timeout)
      .setConnectTimeout(timeout);
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

    httpServer.listen(HEALTH_CHECK_PORT)
        .onSuccess(x -> log.info("Health endpoint is listening now"))
        .onFailure(e -> log.error("The call to admin health check service "
          + "failed due to : {}", e.getMessage(), e));
  }

  @Override
  public void stop(Promise<Void> stopFuture) {
    configRetriever.close();
    server.close(result -> {
      if (result.succeeded()) {
        metricsMap.values().stream().forEach(Metrics::stop);
        stopFuture.complete();
        log.info("MainVerticle stopped successfully!");
      } else {
        log.error("Failed to stop MainVerticle", result.cause());
        stopFuture.fail(result.cause());
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
