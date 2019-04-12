package org.folio.edge.sip2.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class FolioResourceProviderTests {
  private static final int port = getRandomPort();

  @BeforeAll
  static void setup(Vertx vertx, VertxTestContext testContext) throws Throwable {
    vertx.createHttpServer()
      .requestHandler(req -> {
        if (req.path().equals("/test_retrieve")) {
          req.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json")
            .putHeader("x-okapi-token", "token-value")
            .end("{\"test\":\"value\"}");
        } else if (req.path().equals("/test_create")) {
          req.response()
            .setStatusCode(201)
            .putHeader("content-type", "application/json")
            .putHeader("x-okapi-token", "token-value")
            .end("{\"test\":\"value\"}");
        } else {
          req.response()
            .setStatusCode(500)
            .end("Unexpected call: " + req.path());
        }
      })
      .listen(port, testContext.completing());

    assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));

    if (testContext.failed()) {
      throw testContext.causeOfFailure();
    }
  }

  @AfterAll
  static void tearDown(Vertx vertx, VertxTestContext testContext) {
    vertx.close(testContext.completing());
  }

  @Test
  public void canConstructDefaultConfigurationProvider(
      Vertx vertx,
      VertxTestContext testContext) {
    final FolioResourceProvider folioResourceProvider =
        new FolioResourceProvider("http://example.com", "diku", vertx);

    assertNotNull(folioResourceProvider);

    testContext.completeNow();
  }

  @Test
  public void canRetrieveSomething(
      Vertx vertx,
      VertxTestContext testContext) {
    final FolioResourceProvider folioResourceProvider =
        new FolioResourceProvider("http://localhost:" + port, "diku", vertx);
    folioResourceProvider.retrieveResource(() -> "/test_retrieve").setHandler(
        testContext.succeeding(jo -> testContext.verify(() -> {
          assertNotNull(jo);
          assertTrue(jo.containsKey("test"));
          assertEquals("value", jo.getString("test"));

          testContext.completeNow();
        })));
  }

  @Test
  public void canRetrieveFail(
      Vertx vertx,
      VertxTestContext testContext) {
    final FolioResourceProvider folioResourceProvider =
        new FolioResourceProvider("http://localhost:" + port, "diku", vertx);
    folioResourceProvider.retrieveResource(() -> "/test_retrieve_bad")
        .setHandler(testContext.failing(throwable -> testContext.verify(() -> {
          assertNotNull(throwable);
          assertEquals("Response status code 500 is not equal to 200",
              throwable.getMessage());

          testContext.completeNow();
        })));
  }

  @Test
  public void canCreateSomething(
      Vertx vertx,
      VertxTestContext testContext) {
    final FolioResourceProvider folioResourceProvider =
        new FolioResourceProvider("http://localhost:" + port, "diku", vertx);
    folioResourceProvider.createResource(() -> "/test_create").setHandler(
        testContext.succeeding(jo -> testContext.verify(() -> {
          assertNotNull(jo);
          assertTrue(jo.containsKey("test"));
          assertEquals("value", jo.getString("test"));

          testContext.completeNow();
        })));
  }

  @Test
  public void canCreateFail(
      Vertx vertx,
      VertxTestContext testContext) {
    final FolioResourceProvider folioResourceProvider =
        new FolioResourceProvider("http://localhost:" + port, "diku", vertx);
    folioResourceProvider.createResource(() -> "/test_create_bad")
        .setHandler(testContext.failing(throwable -> testContext.verify(() -> {
          assertNotNull(throwable);
          assertEquals("Response status code 500 is not equal to 201",
              throwable.getMessage());

          testContext.completeNow();
        })));
  }

  private static int getRandomPort() {
    int port = -1;
    do {
      // Use a random ephemeral port
      port = new Random().nextInt(16_384) + 49_152;
      try {
        final ServerSocket socket = new ServerSocket(port);
        socket.close();
      } catch (IOException e) {
        continue;
      }
      break;
    } while (true);

    return port;
  }
}
