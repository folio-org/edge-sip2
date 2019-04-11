package org.folio.edge.sip2;

import static org.folio.edge.sip2.parser.Command.CHECKOUT;
import static org.folio.edge.sip2.parser.Command.LOGIN;
import static org.folio.edge.sip2.parser.Command.SC_STATUS;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.parsetools.RecordParser;
import java.lang.invoke.MethodHandles;
import java.util.EnumMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.handlers.HandlersFactory;
import org.folio.edge.sip2.handlers.ISip2RequestHandler;
import org.folio.edge.sip2.parser.Command;
import org.folio.edge.sip2.parser.Message;
import org.folio.edge.sip2.parser.Parser;
import org.folio.edge.sip2.repositories.FolioResourceProvider;
import org.folio.edge.sip2.repositories.LoginRepository;


public class MainVerticle extends AbstractVerticle {

  private Map<Command, ISip2RequestHandler> handlers;
  private NetServer server;
  private final Logger log;

  /**
   * Construct the {@code MainVerticle}.
   */
  public MainVerticle() {
    log = LogManager.getLogger(MethodHandles.lookup().lookupClass());
  }

  public MainVerticle(Map<Command, ISip2RequestHandler> handlers) {
    this.handlers = handlers;
    log = LogManager.getLogger(MethodHandles.lookup().lookupClass());
  }

  @Override
  public void start() {
    if (handlers == null) {
      final FolioResourceProvider folioResourceProvider =
          new FolioResourceProvider(config().getString("okapiUrl"),
              config().getString("tenant"), vertx);
      handlers = new EnumMap<>(Command.class);
      handlers.put(LOGIN, HandlersFactory.getLoginHandlerInstance(
          new LoginRepository(folioResourceProvider),
          folioResourceProvider, null));
      handlers.put(CHECKOUT, HandlersFactory.getCheckoutHandlerIntance());
      handlers.put(SC_STATUS, HandlersFactory.getScStatusHandlerInstance(null, null, null));
    }

    //set Config object's defaults
    int port = config().getInteger("port");
    String messageDelimiter = config().getString("messageDelimiter", "\r");
    NetServerOptions options = new NetServerOptions().setPort(port);
    server = vertx.createNetServer(options);

    log.info("Deployed verticle at port " + port);

    server.connectHandler(socket -> {

      socket.handler(socketBuffer ->
          RecordParser.newDelimited(messageDelimiter, buffer -> {
            final String messageString = buffer.getString(0, buffer.length());
            log.info("Received message: {}",  messageString);

            try {
              // Create a parser with the default delimiter '|' and the default
              // charset 'IMB850'. At some point we will need to have these be
              // tenant specific. This will be difficult considering that we need
              // to parse the login message before we know which tenant it is.
              // We may need another mechanism to obtain this configuration...
              final Parser parser = Parser.builder().build();

              //parsing
              final Message<Object> message = parser.parseMessage(messageString);

              //process validation results
              if (!message.isValid()) {
                log.error("Message is invalid: {}", messageString);
                //resends validation
              }

              ISip2RequestHandler handler = handlers.get(message.getCommand());

              if (handler == null) {
                log.error("Error locating handler for command; " + message.getCommand().name());
              }

              handler
                  .execute(message.getRequest())
                  .setHandler(ar -> {
                    if (ar.succeeded()) {
                      socket.write(ar.result());  //call FOLIO
                    } else {
                      log.error("Failed to respond to request", ar.cause());
                    }
                  });
            } catch (Exception ex) {
              String message = "Problems handling the request: " + ex.getMessage();
              log.error(message, ex);
              // Return an error message for now for the sake of negative testing.
              // Will find a better way to handle negative test cases.
              socket.write(message);
            }
          }).handle(socketBuffer));

      socket.exceptionHandler(handler -> {
        log.info("Socket exceptionHandler caught an issue, see error logs for more details");
        log.error(handler.getMessage());
        log.error(handler.getCause());
      });
    });

    server.listen(result -> {
      if (result.succeeded()) {
        log.info("MainVerticle deeployed successfuly, server is now listening!");
      } else {
        log.error("Failed to deploy MainVerticle");
      }
    });
  }

  @Override
  public void stop(Future<Void> stopFuture) {
    server.close(result -> {
      if (result.succeeded()) {
        stopFuture.complete();
        log.info("MainVerticle stopped successfully!");
      } else {
        stopFuture.fail(result.cause());
      }
    });
  }
}
