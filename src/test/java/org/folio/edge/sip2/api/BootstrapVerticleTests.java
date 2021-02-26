package org.folio.edge.sip2.api;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.core.impl.Deployment;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class BootstrapVerticleTests {

  private static final String BOOTSTRAP_VERTICLE = "org.folio.edge.sip2.BootstrapVerticle";
  private static final String MAIN_VERTICLE = "org.folio.edge.sip2.MainVerticle";
  private static final String SIP2_CONF = "sip2.conf";
  private static final String SIP2_TENANTS_CONF = "sip2-tenants.conf";
  private static final String BAD_SIP2_CONF1 = "sip2-bad1.conf";
  private static final String BAD_SIP2_CONF2 = "sip2-bad2.conf";
  private static final String DUMMY_PROP = "dummy";
  
  @Test
  public void testHappyPath(Vertx vertx, VertxTestContext tstCtxt) {

    vertx.fileSystem().readFile(SIP2_CONF, tstCtxt.succeeding(buffer -> {

      DeploymentOptions opt = new DeploymentOptions().setConfig(buffer.toJsonObject());
      vertx.deployVerticle(BOOTSTRAP_VERTICLE, opt, tstCtxt.succeeding(id -> {
        tstCtxt.completeNow();
      }));
    }));
  }

  @Test
  public void testBadConf1(Vertx vertx, VertxTestContext tstCtxt) {

    vertx.fileSystem().readFile(BAD_SIP2_CONF1, tstCtxt.succeeding(buffer -> {
      
      DeploymentOptions opt = new DeploymentOptions().setConfig(buffer.toJsonObject());
      vertx.deployVerticle(BOOTSTRAP_VERTICLE, opt, tstCtxt.failing(id -> {
        tstCtxt.completeNow();
      }));
    }));
  }

  @Test
  public void testBadConf2(Vertx vertx, VertxTestContext tstCtxt) {

    vertx.fileSystem().readFile(BAD_SIP2_CONF2, tstCtxt.succeeding(buffer -> {
      
      DeploymentOptions opt = new DeploymentOptions().setConfig(buffer.toJsonObject());
      vertx.deployVerticle(BOOTSTRAP_VERTICLE, opt, tstCtxt.failing(id -> {
        tstCtxt.completeNow();
      }));
    }));
  }

  @Test
  public void testSip2RuntimeReload(Vertx vertx, VertxTestContext tstCtxt) {
    
    // test runtime config change reload by adding a dummy property to SIP2_TENANTS_CONF
    // wait 2 seconds and assert that verticle picked up new config with dummy property
    
    FileSystem fs = vertx.fileSystem();
    fs.readFile(SIP2_CONF, tstCtxt.succeeding(buffer -> {
      
      DeploymentOptions opt = new DeploymentOptions().setConfig(buffer.toJsonObject());
      vertx.deployVerticle(BOOTSTRAP_VERTICLE, opt, tstCtxt.succeeding(id -> {
        
        JsonObject jo = fs.readFileBlocking(SIP2_TENANTS_CONF).toJsonObject();
        jo.put(DUMMY_PROP, true);
        fs.writeFileBlocking(SIP2_TENANTS_CONF, jo.toBuffer());
        vertx.setTimer(2000, tid -> {
          JsonObject conf = getVerticle(vertx, MAIN_VERTICLE).config();
          assertTrue(conf.getBoolean(DUMMY_PROP) == true);
          tstCtxt.completeNow();
        });
      }));
    }));
  }
  
  private AbstractVerticle getVerticle(Vertx vertx, String name) {
    Verticle verticle = null;
    for (String id : vertx.deploymentIDs()) {
      Deployment d = ((VertxInternal)vertx).getDeployment(id);
      if (d.verticleIdentifier().equals(name)) {
        verticle = d.getVerticles().iterator().next();
      }
    }    
    return (AbstractVerticle) verticle;
  }

}
