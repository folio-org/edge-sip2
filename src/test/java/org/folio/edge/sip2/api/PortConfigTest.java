package org.folio.edge.sip2.api;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.stream.Stream;
import org.folio.edge.sip2.MainVerticle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PortConfigTest  {


  @Test
  @DisplayName("Should handle single port configuration correctly")
  void singlePortConfigTest(Vertx vertx, VertxTestContext testContext) {
    FileSystem fs = vertx.fileSystem();
    JsonObject sipConfig = fs.readFileBlocking("test-sip2.conf").toJsonObject();
    sipConfig.put("port", 54321);
    DeploymentOptions opt = new DeploymentOptions();
    opt.setConfig(sipConfig);
    MainVerticle myVerticle = new MainVerticle();

    vertx.deployVerticle(myVerticle, opt, res -> {
      if (res.succeeded()) {
        assertNotNull(myVerticle.deploymentID());
        testContext.completeNow();
      } else {
        testContext.failNow(res.cause());
      }
    });
  }

  @Test
  @DisplayName("Should handle multiple port configurations correctly")
  void multiplePortConfigTest(Vertx vertx, VertxTestContext testContext) {
    FileSystem fs = vertx.fileSystem();
    JsonObject sipConfig = fs.readFileBlocking("test-sip2.conf").toJsonObject();

    JsonArray ports = new JsonArray().add(54321).add(54322).add(54323);
    sipConfig.put("port", ports);

    DeploymentOptions opt = new DeploymentOptions();
    opt.setConfig(sipConfig);
    MainVerticle myVerticle = new MainVerticle();

    vertx.deployVerticle(myVerticle, opt, res -> {
      if (res.succeeded()) {
        assertNotNull(myVerticle.deploymentID());
        testContext.completeNow();
      } else {
        testContext.failNow(res.cause());
      }
    });
  }

  static Stream<String> invalidPortConfigs() {
    return Stream.of("invalidPort", "", null);
  }

  @ParameterizedTest
  @MethodSource("invalidPortConfigs")
  @DisplayName("Should fail with invalid port configurations")
  void invalidPortConfigTest(String portConfig, Vertx vertx, VertxTestContext testContext) {
    FileSystem fs = vertx.fileSystem();
    JsonObject sipConfig = fs.readFileBlocking("test-sip2.conf").toJsonObject();
    sipConfig.put("port", portConfig);

    DeploymentOptions opt = new DeploymentOptions();
    opt.setConfig(sipConfig);
    MainVerticle myVerticle = new MainVerticle();

    vertx.deployVerticle(myVerticle, opt, res -> {
      if (res.failed()) {
        Throwable cause = res.cause();
        testContext.verify(() -> {
          assertTrue(cause instanceof IllegalArgumentException);
          testContext.completeNow();
        });
      } else {
        testContext.failNow(new RuntimeException("Deployment should have "
            + "failed with invalid port"));
      }
    });
  }

  @Test
  @DisplayName("Should handle null port configuration gracefully")
  void nullValueInPortConfigTest(Vertx vertx, VertxTestContext testContext) {
    FileSystem fs = vertx.fileSystem();
    JsonObject sipConfig = fs.readFileBlocking("test-sip2.conf").toJsonObject();
    sipConfig.put("port", null);

    DeploymentOptions opt = new DeploymentOptions();
    opt.setConfig(sipConfig);
    MainVerticle myVerticle = new MainVerticle();

    vertx.deployVerticle(myVerticle, opt, res -> {
      if (res.failed()) {
        Throwable cause = res.cause();
        testContext.verify(() -> {
          assertTrue(cause instanceof IllegalArgumentException);
          testContext.completeNow();
        });
      } else {
        testContext.failNow(new RuntimeException("Deployment should have "
            + "failed with invalid port"));
      }
    });
  }

  @Test
  @DisplayName("Should handle null port in json object configuration gracefully")
    void nullValueInPortJsonConfigTest(Vertx vertx, VertxTestContext testContext) {
    FileSystem fs = vertx.fileSystem();
    JsonObject sipConfig = fs.readFileBlocking("test-sip2.conf").toJsonObject();
    JsonArray ports = new JsonArray().add(null);
    sipConfig.put("port", ports);

    DeploymentOptions opt = new DeploymentOptions();
    opt.setConfig(sipConfig);
    MainVerticle myVerticle = new MainVerticle();

    vertx.deployVerticle(myVerticle, opt, res -> {
      if (res.failed()) {
        Throwable cause = res.cause();
        testContext.verify(() -> {
          assertTrue(cause instanceof IllegalArgumentException);
          testContext.completeNow();
        });
      } else {
        testContext.failNow(new RuntimeException("Deployment should have "
              + "failed with invalid port"));
      }
    });

  }

}
