package org.folio.edge.sip2.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.folio.edge.sip2.session.SessionData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class FolioResourceProviderTests {
  private static int port;

  @Timeout(5000)
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
        .listen(0)
        .onSuccess(httpServer -> port = httpServer.actualPort())
        .onComplete(testContext.succeedingThenComplete());
  }

  @AfterAll
  static void tearDown(Vertx vertx, VertxTestContext testContext) {
    vertx.close(testContext.succeedingThenComplete());
  }

  @Test
  public void canConstructDefaultConfigurationProvider(
      Vertx vertx,
      VertxTestContext testContext) {
    final FolioResourceProvider folioResourceProvider =
        new FolioResourceProvider("http://example.com", WebClient.create(vertx));

    assertNotNull(folioResourceProvider);

    testContext.completeNow();
  }

  @Test
  public void canRetrieveSomething(
      Vertx vertx,
      VertxTestContext testContext) {
    FolioResourceProvider folioResourceProvider = mock(FolioResourceProvider.class);

    final JsonObject response = new JsonObject()
        .put("test", "value");

    when(folioResourceProvider.retrieveResource(any(FolioRequestData.class)))
        .thenReturn(Future.succeededFuture(new FolioResource(response,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    folioResourceProvider.retrieveResource((FolioRequestData)() -> "/test_retrieve").onComplete(
        testContext.succeeding(resource -> testContext.verify(() -> {
          final JsonObject jo = resource.getResource();

          assertNotNull(jo);
          assertTrue(jo.containsKey("test"));
          assertEquals("value", jo.getString("test"));

          testContext.completeNow();
        })));
  }

  @Test
  public void canRetrieveFail(
      VertxTestContext testContext) {

    FolioResourceProvider folioResourceProvider = mock(FolioResourceProvider.class);
    when(folioResourceProvider.retrieveResource(any(FolioRequestData.class)))
        .thenReturn(Future.failedFuture(new FolioRequestThrowable(
          "Unexpected call: /test_retrieve_bad")));

    folioResourceProvider.retrieveResource((FolioRequestData)() -> "/test_retrieve_bad")
        .onComplete(testContext.failing(throwable -> testContext.verify(() -> {
          assertNotNull(throwable);
          assertEquals("Unexpected call: /test_retrieve_bad",
              throwable.getMessage());

          testContext.completeNow();
        })));
  }

  @Test
  public void canCreateSomething(
      Vertx vertx,
      VertxTestContext testContext) {

    FolioResourceProvider folioResourceProvider = mock(FolioResourceProvider.class);

    final JsonObject response = new JsonObject()
          .put("test", "value");

    when(folioResourceProvider.createResource(any(FolioRequestData.class)))
        .thenReturn(Future.succeededFuture(new FolioResource(response,
          MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    folioResourceProvider.createResource((FolioRequestData)() -> "/test_create").onComplete(
        testContext.succeeding(resource -> testContext.verify(() -> {
          final JsonObject jo = resource.getResource();

          assertNotNull(jo);
          assertTrue(jo.containsKey("test"));
          assertEquals("value", jo.getString("test"));

          testContext.completeNow();
        })));
  }

  @Test
  public void canCreateFail(
      VertxTestContext testContext) {

    FolioResourceProvider folioResourceProvider = mock(FolioResourceProvider.class);
    when(folioResourceProvider.createResource(any(FolioRequestData.class)))
        .thenReturn(Future.failedFuture(new FolioRequestThrowable(
          "Unexpected call: /test_create_bad")));

    folioResourceProvider.createResource((FolioRequestData)() -> "/test_create_bad")
        .onComplete(testContext.failing(throwable -> testContext.verify(() -> {
          assertNotNull(throwable);
          assertEquals("Unexpected call: /test_create_bad", throwable.getMessage());

          testContext.completeNow();
        })));
  }

  private interface FolioRequestData extends IRequestData {
    @Override
    default SessionData getSessionData() {

      SessionData sessionData = SessionData.createSession("diku", '|', true, "IBM850");
      sessionData.setUsername("testUser");
      sessionData.setPassword("testpassword");
      return sessionData;
    }
  }
}
