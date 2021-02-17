package org.folio.edge.sip2;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BootstrapVerticle extends AbstractVerticle {
  private final Logger log = LogManager.getLogger();

  @Override
  public void start(Future<Void> startFuture) {

    log.info("Starting to bootstrap sip2");
    
    JsonObject crOptionsJson = config().getJsonObject("configRetrieverOptions");
    ConfigRetrieverOptions crOptions = new ConfigRetrieverOptions(crOptionsJson);
    ConfigRetriever configRetriever = ConfigRetriever.create(vertx, crOptions);

    log.info("Retrieving sip2 config");

    configRetriever.getConfig(ar -> {
      if (ar.succeeded()) {
        DeploymentOptions opt = new DeploymentOptions().setConfig(ar.result());
        log.info("Deploying sip2 MainVerticle");
        vertx.deployVerticle("org.folio.edge.sip2.MainVerticle", opt);
      } else {
        log.error("Unable to getConfig - {}", ar.cause().getMessage());
        startFuture.fail(ar.cause());
      }
    });
  }
}
