package org.folio.edge.sip2.support.vertx;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.folio.edge.sip2.MainVerticle;

public class VertxModule {

  private final Vertx vertx;
  private final JsonObject sip2Configuration;

  /**
   * Constructor for VertxModule.
   *
   * @param vertx             - Vertx instance
   * @param sip2Configuration - JsonObject containing SIP2 configuration
   */
  public VertxModule(Vertx vertx, JsonObject sip2Configuration) {
    this.vertx = vertx;
    this.sip2Configuration = sip2Configuration;
  }

  /**
   * Deploys the MainVerticle with the given configuration.
   *
   * @return A Future that completes with the deployment ID when the verticle is deployed.
   */
  public Future<String> deployModule() {
    var options = new DeploymentOptions().setConfig(sip2Configuration);

    return vertx.deployVerticle(MainVerticle.class, options);
  }
}
