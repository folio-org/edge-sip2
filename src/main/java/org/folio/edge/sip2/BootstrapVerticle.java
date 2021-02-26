package org.folio.edge.sip2;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BootstrapVerticle extends AbstractVerticle {
  private final Logger log = LogManager.getLogger();
  private String deploymentID = null;
  
  @Override
  public void start(Promise<Void> startFuture) {

    log.info("Bootstrapping sip2, retrieving config from:\n {}", () -> config().encodePrettily());
    JsonObject crOptionsJson = config().getJsonObject("tenantConfigRetrieverOptions");
    ConfigRetrieverOptions crOptions = new ConfigRetrieverOptions(crOptionsJson);
    ConfigRetriever configRetriever = ConfigRetriever.create(vertx, crOptions);

    configRetriever.listen(change -> {
      log.info("config change detected for sip2, redeploying MainVerticle");
      deployMainVerticle(change.getNewConfiguration(), Promise.promise());
    });    

    configRetriever.getConfig(ar -> {
      if (ar.succeeded()) {
        log.info("Deploying sip2 MainVerticle");
        deployMainVerticle(ar.result(), startFuture);
      } else {
        log.error("Unable to getConfig - {}", ar.cause().getMessage());
        startFuture.fail(ar.cause());
      }
    });

  }
  
  private void deployMainVerticle(JsonObject config, Promise<Void> completion) {
    if (Objects.nonNull(deploymentID)) {
      vertx.undeploy(deploymentID);
    }
    // deploy verticle with config merged with this BootstrapVerticle config so it includes  
    // port & okapiUrl properties, which originate from BootstrapVerticle config
    DeploymentOptions opt = new DeploymentOptions().setConfig(config.copy().mergeIn(config()));
    vertx.deployVerticle("org.folio.edge.sip2.MainVerticle", opt, ar -> {
      if (ar.succeeded()) {
        deploymentID = ar.result();
        completion.complete();
      } else {
        completion.fail(ar.cause());
      }
    });    
  }
}
