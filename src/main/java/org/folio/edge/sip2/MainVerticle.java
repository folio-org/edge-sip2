package org.folio.edge.sip2;

import static java.lang.Boolean.FALSE;
import static org.folio.edge.sip2.parser.Command.CHECKIN;
import static org.folio.edge.sip2.parser.Command.CHECKOUT;
import static org.folio.edge.sip2.parser.Command.END_PATRON_SESSION;
import static org.folio.edge.sip2.parser.Command.LOGIN;
import static org.folio.edge.sip2.parser.Command.PATRON_INFORMATION;
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
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.pointer.JsonPointer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import io.vertx.ext.web.client.WebClient;
import java.nio.charset.Charset;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.PreviousMessage;
import org.folio.edge.sip2.handlers.CheckinHandler;
import org.folio.edge.sip2.handlers.CheckoutHandler;
import org.folio.edge.sip2.handlers.EndPatronSessionHandler;
import org.folio.edge.sip2.handlers.HandlersFactory;
import org.folio.edge.sip2.handlers.ISip2RequestHandler;
import org.folio.edge.sip2.handlers.LoginHandler;
import org.folio.edge.sip2.handlers.PatronInformationHandler;
import org.folio.edge.sip2.metrics.Metrics;
import org.folio.edge.sip2.modules.ApplicationModule;
import org.folio.edge.sip2.modules.FolioResourceProviderModule;
import org.folio.edge.sip2.parser.Command;
import org.folio.edge.sip2.parser.Message;
import org.folio.edge.sip2.parser.Parser;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.TenantUtils;

public class MainVerticle extends AbstractVerticle {
  private Map<Command, ISip2RequestHandler> handlers;
  private NetServer server;
  private final Logger log = LogManager.getLogger();
  private final Map<Integer, Metrics> metricsMap = new HashMap<>();
  private JsonObject multiTenantConfig = new JsonObject();
  private ConfigRetriever configRetriever;

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
    log.debug("Startup configuration: {}", () -> getSanitizedConfig());

    // We need to reduce the complexity of this method...
    if (handlers == null) {
      String okapiUrl = config().getString("okapiUrl");
      final WebClient webClient = WebClient.create(vertx);
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
      handlers.put(REQUEST_SC_RESEND, HandlersFactory.getInvalidMessageHandler());
      handlers.put(END_PATRON_SESSION, injector.getInstance(EndPatronSessionHandler.class));
    }

    //set Config object's defaults
    int port = config().getInteger("port"); // move port to netServerOptions
    NetServerOptions options = new NetServerOptions(
        config().getJsonObject("netServerOptions", new JsonObject()))
        .setPort(port);

    server = vertx.createNetServer(options);

    log.info("Deployed verticle at port {}", port);

    final Metrics metrics = Metrics.getMetrics(port);
    metricsMap.putIfAbsent(port, metrics);

    server.connectHandler(socket -> {

      String clientAddress = socket.remoteAddress().host();
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
            String prvMessage = sessionData
                .getPreviousMessage()
                .getPreviousMessageResponse();
            log.info("Sending previous Sip response {}", prvMessage);
            sample.stop(metrics.commandTimer(command));
            socket.write(prvMessage, sessionData.getCharset());
            return;
          }

          ISip2RequestHandler handler = handlers.get(command);

          if (handler == null) {
            log.error("Error locating handler for command; " + command.name());
            sample.stop(metrics.commandTimer(command));
            return;
          }

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
                sample.stop(metrics.commandTimer(message.getCommand()));
                socket.write(e.getMessage() + messageDelimiter,
                    sessionData.getCharset());
                metrics.responseError();
              });
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
