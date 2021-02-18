package org.folio.edge.sip2.api;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class BootstrapVerticleTests {
  
  @Test
  public void testHappyPath(Vertx vertx, VertxTestContext tstCtxt) {

    vertx.fileSystem().readFile("fileStoreBootstrap.conf", tstCtxt.succeeding(buffer -> {

      DeploymentOptions opt = new DeploymentOptions().setConfig(buffer.toJsonObject());
      vertx.deployVerticle("org.folio.edge.sip2.BootstrapVerticle", opt, tstCtxt.succeeding(id -> {
        tstCtxt.completeNow();
      }));
    }));
  }

  @Test
  public void testBadBootstrapConf1(Vertx vertx, VertxTestContext tstCtxt) {

    DeploymentOptions opt = new DeploymentOptions().setConfig(new JsonObject());
    vertx.deployVerticle("org.folio.edge.sip2.BootstrapVerticle", opt, tstCtxt.failing(id -> {
      tstCtxt.completeNow();
    }));
  }

  @Test
  public void testBadBootstrapConf2(Vertx vertx, VertxTestContext tstCtxt) {

    vertx.fileSystem().readFile("badBootstrap.conf", tstCtxt.succeeding(buffer -> {
      
      DeploymentOptions opt = new DeploymentOptions().setConfig(buffer.toJsonObject());
      vertx.deployVerticle("org.folio.edge.sip2.BootstrapVerticle", opt, tstCtxt.failing(id -> {
        tstCtxt.completeNow();
      }));
    }));
  }

}
