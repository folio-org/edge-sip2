package org.folio.edge.sip2;

import java.lang.invoke.MethodHandles;
import java.util.EnumMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.handlers.CheckoutHandler;
import org.folio.edge.sip2.handlers.LoginHandler;
import org.folio.edge.sip2.handlers.Sip2RequestHandler;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;

public class MainVerticle extends AbstractVerticle {

  private EnumMap<Sip2HandlerCommandTypes, Sip2RequestHandler> handlers;
  private NetServer server;
  private final Logger log;

  public MainVerticle(){
    handlers = new EnumMap<>(Sip2HandlerCommandTypes.class);
    handlers.put(Sip2HandlerCommandTypes.LOGIN, new LoginHandler());
    handlers.put(Sip2HandlerCommandTypes.CHECKOUT, new CheckoutHandler());

    log = LogManager.getLogger(MethodHandles.lookup().lookupClass());
  }

  public MainVerticle(EnumMap<Sip2HandlerCommandTypes, Sip2RequestHandler> handlers){
    this.handlers = handlers;
    log = LogManager.getLogger(MethodHandles.lookup().lookupClass());
  }

  @Override
  public void start(Future<Void> startFuture) {
    //set Config object's defaults
    int port = config().getInteger("port");
    NetServerOptions options = new NetServerOptions().setPort(port);
    server = vertx.createNetServer(options);

    log.info("Deployed verticle at port " + port);

    server.connectHandler( socket -> {

      socket.handler(buffer -> {
        log.info("Received message: " + buffer.getString(0, buffer.length()));
        String actionCode = buffer.getString(0, 2);
        try {
          Integer actionCodeInt = Integer.parseInt(actionCode);
          String incomingMsg = buffer.getString(2, buffer.length());

          //process validation results
          //resends validation
          //parsing

          Sip2RequestHandler handler = handlers.get(Sip2HandlerCommandTypes.from(actionCodeInt));
          socket.write(handler.execute(incomingMsg));  //call FOLIO
        }
        catch (Exception ex){
          log.error("Problems handling the request {}", ex.getMessage());
          startFuture.fail(ex.getMessage());
        }
      });

      socket.exceptionHandler( handler -> {
        log.info("Socket exceptionHandler caught an issue, see error logs for more details");
        log.error(handler.getMessage());
        log.error(handler.getCause());
        startFuture.fail(handler.getCause());
      });
    });

    server.listen( result -> {
      if (result.succeeded()) {
        log.info("MainVerticle deeployed successfuly, server is now listening!");
        startFuture.complete();
      } else {
        log.error("Failed to deploy MainVerticle");
        startFuture.fail(result.cause());
      }
    });
  }

  @Override
  public void stop(Future stopFuture) {
    server.close( result -> {
      if (result.succeeded()) {
        stopFuture.complete();
        log.info("MainVerticle stopped successfully!");
      } else {
        stopFuture.fail(result.cause());
      }
    });
  }
}
