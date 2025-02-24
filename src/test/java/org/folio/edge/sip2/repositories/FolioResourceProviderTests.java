package org.folio.edge.sip2.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.Cookie;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.session.SessionData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
public class FolioResourceProviderTests {
  private static int port;
  @Mock
  WebClient client;

  @InjectMocks
  FolioResourceProvider provider;

  @Mock
  HttpRequest<Buffer> httpRequest;

  @Mock
  HttpResponse<Buffer> httpResponse;

  @Mock
  private HttpResponse<JsonObject> jsonObjectHttpResponse;

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
        } else if (req.path().equals("/authn/login-with-expiry")) {
          req.response()
              .setStatusCode(201)
              .putHeader("content-type", "application/json")
              .putHeader("x-okapi-token", "token-value")
              .addCookie(Cookie.cookie("folioAccessToken", "FAKETOKEN"))
              .end("{}");
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
          assertEquals("Unexpected call: /test_create_bad",
              throwable.getMessage());

          testContext.completeNow();
        })));
  }

  @Test
  public void canLogin(
      Vertx vertx,
      VertxTestContext testContext) {
    Future<String> passwordFuture = Future.succeededFuture("password");
    SessionData sessionData = TestUtils.getMockedSessionData();
    final FolioResourceProvider folioResourceProvider =
        new FolioResourceProvider("http://localhost:" + port, WebClient.create(vertx));
    folioResourceProvider.loginWithSupplier("dummy", () -> passwordFuture, sessionData, false)
        .onComplete(
        testContext.succeeding(response -> testContext.verify(() -> {
          testContext.completeNow();
        })));
  }

  @Test
  void canLoginWithCache(
      Vertx vertx,
      VertxTestContext testContext) {
    Future<String> passwordFuture = Future.succeededFuture("password");
    SessionData sessionData = TestUtils.getMockedSessionData();
    final FolioResourceProvider folioResourceProvider =
        new FolioResourceProvider("http://localhost:" + port, WebClient.create(vertx));
    folioResourceProvider.loginWithSupplier("dummy", () -> passwordFuture, sessionData, true)
        .onComplete(
        testContext.succeeding(response -> testContext.verify(() -> {
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

  @Test
  void loginWithSupplier_Success(VertxTestContext testContext) {
    final String username = "testUser";
    final SessionData sessionData = SessionData.createSession("testTenant", '|', false, "IBM850");

    when(client.postAbs(anyString())).thenReturn(httpRequest);
    when(httpRequest.putHeader(anyString(), anyString())).thenReturn(httpRequest);
    List<String> cookies = new ArrayList<>();
    JsonObject responseBodyJson = new JsonObject().put("test", "value");
    cookies.add("folioAccessToken=cookieValue");
    when(httpResponse.cookies()).thenReturn(cookies);
    when(httpResponse.statusCode()).thenReturn(201);
    when(httpRequest.sendJsonObject(any())).thenReturn(Future.succeededFuture(httpResponse));

    AtomicInteger counter = new AtomicInteger();
    when(httpRequest.sendJsonObject(any())).thenAnswer(invocation -> {
      if (invocation.getMethod().getName().equals("sendJsonObject") && counter.get() == 2) {
        HttpResponse<JsonObject> httpResponse = mock(HttpResponse.class);
        when(httpResponse.body()).thenReturn(new JsonObject());
        MultiMap headers = MultiMap.caseInsensitiveMultiMap();
        when(httpResponse.headers()).thenReturn(headers);
        return Future.succeededFuture(httpResponse);
      } else {
        counter.getAndIncrement();
        return Future.succeededFuture(httpResponse);
      }
    });

    doReturn(httpRequest).when(httpRequest).expect(any());
    doReturn(httpRequest).when(httpRequest).as(any());

    Future<String> result = provider.loginWithSupplier(username, ()
        -> Future.succeededFuture("testPassword"), sessionData, true);

    assertTrue(result.succeeded());
    assertEquals("cookieValue", result.result());

    provider.createResource((FolioRequestData) () -> "/test_create").onComplete(
        testContext.succeeding(resource -> testContext.verify(() -> {
          final JsonObject jo = resource.getResource();
          assertNotNull(jo);
          testContext.completeNow();
        })));
  }

  @Test
  void loginWithSupplier_Failure() {
    final String username = "testUser";
    final SessionData sessionData = SessionData.createSession("testTenant", '|', false, "IBM850");
    when(client.postAbs(anyString())).thenReturn(httpRequest);
    when(httpRequest.putHeader(anyString(), anyString())).thenReturn(httpRequest);
    List<String> cookies = new ArrayList<>();
    cookies.add("folioAccessToken=cookieValue");

    Future<String> result = provider.loginWithSupplier(username, ()
        -> Future.succeededFuture("testPassword"), sessionData, true);

    assertTrue(result.failed());
    assertNull(sessionData.getAuthenticationToken());
  }
}
