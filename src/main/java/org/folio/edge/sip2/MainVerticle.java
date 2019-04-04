package org.folio.edge.sip2;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;

import java.lang.invoke.MethodHandles;
import java.util.EnumMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.handlers.CheckoutHandler;
import org.folio.edge.sip2.handlers.ISip2RequestHandler;
import org.folio.edge.sip2.handlers.LoginHandler;
import org.folio.edge.sip2.handlers.SCStatusHandler;
import org.folio.edge.sip2.repositories.ConfigurationRepository;
import org.folio.edge.sip2.repositories.DefaultConfigurationProvider;

public class MainVerticle extends AbstractVerticle {

  private Map<Sip2HandlerCommandTypes, ISip2RequestHandler> handlers;
  private NetServer server;
  private final Logger log;

  /**
   * Construct the {@code MainVerticle}.
   */
  public MainVerticle() {
    handlers = new EnumMap<>(Sip2HandlerCommandTypes.class);
    handlers.put(Sip2HandlerCommandTypes.LOGIN, new LoginHandler());
    handlers.put(Sip2HandlerCommandTypes.CHECKOUT, new CheckoutHandler());
    handlers.put(Sip2HandlerCommandTypes.SCSTATUS,
      new SCStatusHandler(
        new ConfigurationRepository(
          new DefaultConfigurationProvider("./")), "./templates"));

    log = LogManager.getLogger(MethodHandles.lookup().lookupClass());
  }

  public MainVerticle(Map<Sip2HandlerCommandTypes, ISip2RequestHandler> handlers) {
    this.handlers = handlers;
    log = LogManager.getLogger(MethodHandles.lookup().lookupClass());
  }

  @Override
  public void start() {
    //set Config object's defaults
    int port = config().getInteger("port");
    NetServerOptions options = new NetServerOptions().setPort(port);
    server = vertx.createNetServer(options);

    log.info("Deployed verticle at port " + port);

    server.connectHandler(socket -> {

      socket.handler(buffer -> {
        log.info("Received message: " + buffer.getString(0, buffer.length()));
        String actionCode = buffer.getString(0, 2);
        try {
          Integer actionCodeInt = Integer.parseInt(actionCode);
          String incomingMsg = buffer.getString(2, buffer.length());

          //process validation results
          //resends validation
          //parsing

          ISip2RequestHandler handler = handlers.get(Sip2HandlerCommandTypes.from(actionCodeInt));
          socket.write(handler.execute(incomingMsg));  //call FOLIO
        } catch (Exception ex) {
          String message = "Problems handling the request " + ex.getMessage();
          log.error(message);
          // Return an error message for now for the sake of negative testing.
          // Will find a better way to handle negative test cases.
          socket.write(message);
        }
      });

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
