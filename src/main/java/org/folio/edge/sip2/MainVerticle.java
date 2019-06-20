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
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import java.nio.charset.Charset;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
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

public class MainVerticle extends AbstractVerticle {
  private Map<Command, ISip2RequestHandler> handlers;
  private NetServer server;
  private final Logger log = LogManager.getLogger();
  private final Map<Integer, Metrics> metricsMap = new HashMap<>();

  /**
   * Construct the {@code MainVerticle}.
   */
  public MainVerticle() {
    super();
  }

  public MainVerticle(Map<Command, ISip2RequestHandler> handlers) {
    this.handlers = handlers;
  }

  @Override
  public void start(Future<Void> startFuture) {
    log.debug("Startup configuration: {}", () -> config().encodePrettily());

    // We need to reduce the complexity of this method...
    if (handlers == null) {
      String okapiUrl = config().getString("okapiUrl");

      final Injector injector = Guice.createInjector(
          new FolioResourceProviderModule(okapiUrl, vertx),
          new ApplicationModule());
      handlers = new EnumMap<>(Command.class);
      handlers.put(CHECKOUT, injector.getInstance(CheckoutHandler.class));
      handlers.put(CHECKIN, injector.getInstance(CheckinHandler.class));
      handlers.put(SC_STATUS, HandlersFactory.getScStatusHandlerInstance(null, null,
                null, null, okapiUrl, vertx));
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
      final SessionData sessionData = SessionData.createSession(
          config().getString("tenant"),
          config().getString("fieldDelimiter", "|").charAt(0),
          config().getBoolean("errorDetectionEnabled", FALSE),
          config().getString("charset", "IBM850"));
      final String messageDelimiter = config().getString("messageDelimiter", "\r");

      socket.handler(RecordParser.newDelimited(messageDelimiter, buffer -> {
        final Timer.Sample sample = metrics.sample();

        final String messageString = buffer.getString(0, buffer.length(), sessionData.getCharset());

        log.debug("Received message: {}", messageString);

        Command command = UNKNOWN;

        try {
          // At some point we will need to have these be
          // tenant specific. This will be difficult considering that we need
          // to parse the login message before we know which tenant it is.
          // We may need another mechanism to obtain this configuration...
          final Parser parser = Parser.builder()
              .delimiter(sessionData.getFieldDelimiter())
              .charset(Charset.forName(sessionData.getCharset()))
              .errorDetectionEnaled(sessionData.isErrorDetectionEnabled())
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
            socket.write(prvMessage, sessionData.getCharset());
            sample.stop(metrics.commandTimer(command));
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
              .setHandler(ar -> {
                if (ar.succeeded()) {
                  final String responseMsg;
                  if (message.getCommand() == REQUEST_ACS_RESEND) {
                    // we don't want to modify the response
                    responseMsg = ar.result();
                  } else {
                    responseMsg = formatResponse(ar.result(), message, sessionData,
                        messageDelimiter);
                  }
                  handler.writeHistory(sessionData, message, responseMsg);
                  log.info("Sip response {}", responseMsg);
                  socket.write(responseMsg, sessionData.getCharset());
                } else {
                  String errorMsg = "Failed to respond to request";
                  log.error(errorMsg, ar.cause());
                  socket.write(ar.cause().getMessage() + messageDelimiter,
                      sessionData.getCharset());
                  metrics.responseError();
                }

                sample.stop(metrics.commandTimer(message.getCommand()));
              });
        } catch (Exception ex) {
          String message = "Problems handling the request: " + ex.getMessage();
          log.error(message, ex);
          // Return an error message for now for the sake of negative testing.
          // Will find a better way to handle negative test cases.
          socket.write(message + messageDelimiter, sessionData.getCharset());

          sample.stop(metrics.commandTimer(command));
          metrics.requestError();
        }
      }));

      socket.exceptionHandler(t -> {
        log.info("Socket exceptionHandler caught an issue, see error logs for more details");
        log.error("Socket exception", t);
        metrics.socketError();
      });
    });

    server.listen(result -> {
      if (result.succeeded()) {
        log.info("MainVerticle deeployed successfuly, server is now listening!");
        startFuture.complete();
      } else {
        log.error("Failed to deploy MainVerticle", result.cause());
        startFuture.fail(result.cause());
      }
    });
  }

  @Override
  public void stop(Future<Void> stopFuture) {
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
    if (message.isErrorDetectionEnabled()) {
      //resends validation if checksum string does not match
      ISip2RequestHandler handler = handlers.get(Command.REQUEST_SC_RESEND);
      handler.execute(message.getRequest(), sessionData)
          .setHandler(ar -> {
            if (ar.succeeded()) {
              socket.write(formatResponse(ar.result(), message, sessionData,
                  messageDelimiter, true), sessionData.getCharset());
            } else {
              log.error("Failed to send SC resend", ar.cause());
              metrics.scResendError();
            }
          });
    } else {
      socket.write("Problems handling the request: " + messageDelimiter, sessionData.getCharset());
      metrics.invalidMessageError();
    }

    sample.stop(metrics.commandTimer(message.getCommand()));
  }

  private String formatResponse(String response, Message<Object> message, SessionData sessionData,
      String messageDelimiter) {
    return formatResponse(response, message, sessionData, messageDelimiter, false);
  }

  private String formatResponse(String response, Message<Object> message, SessionData sessionData,
      String messageDelimiter, boolean isSCResend) {
    final String result;

    if (message.isErrorDetectionEnabled()) {
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

    if (prevMessage == null || !currentMessage.isErrorDetectionEnabled()) {
      log.debug("requiredResending is FALSE");
      return false;
    } else {
      log.debug("requiredResending is TRUE");
      return currentMessage.getChecksumsString().equals(prevMessage.getPreviousRequestChecksum())
        && currentMessage.getSequenceNumber() == prevMessage.getPreviousRequestSequenceNo();
    }
  }
}
