package org.folio.edge.sip2.repositories;

import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;
import static io.vertx.core.MultiMap.caseInsensitiveMultiMap;
import static io.vertx.core.http.HttpMethod.GET;
import static io.vertx.core.http.HttpMethod.POST;
import static io.vertx.core.http.HttpVersion.HTTP_1_1;
import static io.vertx.core.http.impl.headers.HeadersMultiMap.httpHeaders;
import static io.vertx.ext.web.codec.BodyCodec.jsonObject;
import static java.util.Collections.emptyList;
import static org.folio.okapi.common.XOkapiHeaders.REQUEST_ID;
import static org.folio.okapi.common.XOkapiHeaders.TENANT;
import static org.folio.okapi.common.XOkapiHeaders.TOKEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.impl.HttpResponseImpl;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.Map;
import org.folio.edge.sip2.exception.MissingAccessTokenThrowable;
import org.folio.edge.sip2.session.SessionData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({ VertxExtension.class, MockitoExtension.class })
class FolioResourceProviderTest {

  private static final String OKAPI_URL = "http://okapi:9130";
  private static final String TENANT_ID = "testtenant";
  private static final String ACCESS_TOKEN = "test-access-token";
  private static final String REQUEST_PATH = "/test-path";

  private FolioResourceProvider provider;
  @Mock private WebClient webClient;
  @Mock private LoginRepository loginRepository;
  @Mock private HttpRequest<Buffer> httpRequest;
  @Mock private HttpRequest<JsonObject> jsonRequest;

  @BeforeEach
  void setUp() {
    provider = new FolioResourceProvider(loginRepository, OKAPI_URL, webClient);
  }

  @Test
  void canConstructFolioResourceProvider() {
    var folioResourceProvider = new FolioResourceProvider(loginRepository, OKAPI_URL, webClient);
    assertNotNull(folioResourceProvider);
  }

  @Test
  void retrieveResource_positive(VertxTestContext testContext) {
    var requestData = testRequestData();
    var expectedResponse = new JsonObject().put("test", "value");
    var httpResponse = httpResponse(200, "OK", expectedResponse);

    prepareRequestMocks(GET, requestData);
    when(jsonRequest.send()).thenReturn(succeededFuture(httpResponse));
    when(loginRepository.getSessionAccessToken(any())).thenReturn(succeededFuture(ACCESS_TOKEN));

    var resultFuture = provider.retrieveResource(requestData);

    resultFuture.onComplete(testContext.succeeding(resource -> {
      assertNotNull(resource);
      var jsonResponse = resource.getResource();
      assertTrue(jsonResponse.containsKey("test"));
      assertEquals("value", jsonResponse.getString("test"));
      testContext.completeNow();
    }));
  }

  @Test
  void retrieveResource_negative_accessTokenMissing(VertxTestContext testContext) {
    var requestData = testRequestData();

    when(loginRepository.getSessionAccessToken(any()))
        .thenReturn(failedFuture(new MissingAccessTokenThrowable()));

    var resultFuture = provider.retrieveResource(requestData);

    resultFuture.onComplete(testContext.failing(error -> {
      var expectedMessage = "Access token is missing. "
          + "Please login to Folio to obtain a valid access token.";
      var actualErrorMessage = requestData.getSessionData().getErrorResponseMessage();

      assertInstanceOf(MissingAccessTokenThrowable.class, error);
      assertEquals(expectedMessage, error.getMessage());
      assertEquals("Headers not set correctly: " + expectedMessage, actualErrorMessage);
      testContext.completeNow();
    }));
  }

  @Test
  void retrieveResource_negative_httpError(VertxTestContext testContext) {
    var requestData = testRequestData();
    var validationError = new IllegalStateException("Invalid http status");

    prepareRequestMocks(GET, requestData);
    when(jsonRequest.send()).thenReturn(failedFuture(validationError));
    when(loginRepository.getSessionAccessToken(any())).thenReturn(succeededFuture(ACCESS_TOKEN));

    var resultFuture = provider.retrieveResource(requestData);

    resultFuture.onComplete(testContext.failing(error -> {
      assertInstanceOf(IllegalStateException.class, error);
      var errorMsg = validationError.getMessage();
      assertEquals(errorMsg, error.getMessage());
      var expectedMessage = "Headers not set correctly: " + errorMsg;
      assertEquals(expectedMessage, requestData.getSessionData().getErrorResponseMessage());
      testContext.completeNow();
    }));
  }

  @Test
  void retrieveResource_negative_internalServerError(VertxTestContext testContext) {
    var requestData = testRequestData();
    var responseBody = new JsonObject().put("error", "error");
    var response = httpResponse(500, "Internal Server Error", responseBody);

    prepareRequestMocks(GET, requestData);
    when(jsonRequest.send()).thenReturn(succeededFuture(response));
    when(loginRepository.getSessionAccessToken(any())).thenReturn(succeededFuture(ACCESS_TOKEN));

    var resultFuture = provider.retrieveResource(requestData);

    resultFuture.onComplete(testContext.failing(error -> {
      assertInstanceOf(FolioRequestThrowable.class, error);
      var errorMsg = "Failed to perform request: 500 Internal Server Error";
      assertEquals(errorMsg, error.getMessage());
      var expectedMessage = "Error communicating with FOLIO: " + errorMsg;
      assertEquals(expectedMessage, requestData.getSessionData().getErrorResponseMessage());
      testContext.completeNow();
    }));
  }

  @Test
  void retrieveResource_negative_resourceNotFound(VertxTestContext testContext) {
    var requestData = testRequestData();
    var responseBody = new JsonObject().put("error", "not_found");
    var response = httpResponse(404, "Not found", responseBody);

    prepareRequestMocks(GET, requestData);
    when(jsonRequest.send()).thenReturn(succeededFuture(response));
    when(loginRepository.getSessionAccessToken(any())).thenReturn(succeededFuture(ACCESS_TOKEN));

    var resultFuture = provider.retrieveResource(requestData);

    resultFuture.onComplete(testContext.failing(error -> {
      assertInstanceOf(FolioRequestThrowable.class, error);
      var errorMsg = "Failed to perform request: 404 Not found";
      assertEquals(errorMsg, error.getMessage());
      var expectedMessage = "Error communicating with FOLIO: " + errorMsg;
      assertEquals(expectedMessage, requestData.getSessionData().getErrorResponseMessage());
      testContext.completeNow();
    }));
  }

  @Test
  void createResource_positive(VertxTestContext testContext) {
    var requestData = testRequestDataWithBody(REQUEST_PATH, new JsonObject().put("name", "test"));
    var expectedResponse = new JsonObject().put("id", "123");
    var httpResponse = httpResponse(201, "Created", expectedResponse);

    prepareRequestMocks(POST, requestData);
    when(jsonRequest.sendJsonObject(any())).thenReturn(succeededFuture(httpResponse));
    when(loginRepository.getSessionAccessToken(any())).thenReturn(succeededFuture(ACCESS_TOKEN));

    var resultFuture = provider.createResource(requestData);

    resultFuture.onComplete(testContext.succeeding(resource -> {
      assertNotNull(resource);
      var jsonResponse = resource.getResource();
      assertTrue(jsonResponse.containsKey("id"));
      assertEquals("123", jsonResponse.getString("id"));
      testContext.completeNow();
    }));
  }

  @Test
  void createResource_negative_accessTokenMissing(VertxTestContext testContext) {
    var requestData = testRequestDataWithBody(REQUEST_PATH, new JsonObject());

    when(loginRepository.getSessionAccessToken(any(SessionData.class)))
        .thenReturn(failedFuture(new MissingAccessTokenThrowable()));

    var resultFuture = provider.createResource(requestData);

    resultFuture.onComplete(testContext.failing(error -> {
      var msg = "Access token is missing. Please login to Folio to obtain a valid access token.";
      assertEquals(msg, error.getMessage());
      testContext.completeNow();
    }));
  }

  @Test
  void doPinCheck_positive(VertxTestContext testContext) {
    var requestData = testRequestDataWithBody("/pin-verify", new JsonObject().put("pin", "1234"));
    var httpResponse = httpResponse(200, "OK", new JsonObject());

    prepareRequestMocks(POST, requestData);
    when(jsonRequest.sendJsonObject(any())).thenReturn(succeededFuture(httpResponse));
    when(loginRepository.getSessionAccessToken(any(SessionData.class)))
        .thenReturn(succeededFuture(ACCESS_TOKEN));

    var resultFuture = provider.doPinCheck(requestData);

    resultFuture.onComplete(testContext.succeeding(result -> {
      assertTrue(result);
      testContext.completeNow();
    }));
  }

  @Test
  void doPinCheck_negative_accessTokenMissing(VertxTestContext testContext) {
    var requestData = testRequestDataWithBody("/pin-verify", new JsonObject());

    when(loginRepository.getSessionAccessToken(any(SessionData.class)))
        .thenReturn(failedFuture(new RuntimeException("Access token missing")));

    var resultFuture = provider.doPinCheck(requestData);

    resultFuture.onComplete(testContext.failing(error -> {
      assertEquals("Access token missing", error.getMessage());
      testContext.completeNow();
    }));
  }

  @Test
  void editResource_negative(VertxTestContext testContext) {
    var requestData = testRequestDataWithBody("/entities", new JsonObject());
    var resultFuture = provider.editResource(requestData);

    resultFuture.onComplete(testContext.failing(error -> {
      assertInstanceOf(UnsupportedOperationException.class, error);
      assertEquals("Not implemented", error.getMessage());
      testContext.completeNow();
    }));
  }

  @Test
  void deleteResource_negative(VertxTestContext testContext) {
    var requestData = testRequestData();
    var resultFuture = provider.deleteResource(requestData);

    resultFuture.onComplete(testContext.failing(error -> {
      assertInstanceOf(UnsupportedOperationException.class, error);
      assertEquals("Not implemented", error.getMessage());
      testContext.completeNow();
    }));
  }

  @Test
  void getHttpRequestError() {
    var sessionData = SessionData.createSession(TENANT_ID, '|', true, "UTF-8");
    var error = new RuntimeException("Error");
    var httpClientResponse = mock(HttpClientResponse.class);
    when(httpClientResponse.statusCode()).thenReturn(500);
    when(httpClientResponse.statusMessage()).thenReturn("Internal Server Error");

    var result = FolioResourceProvider.getHttpRequestError(sessionData, httpClientResponse, error);
    assertInstanceOf(FolioRequestThrowable.class, result);
    assertEquals("Failed to perform request: 500 Internal Server Error", result.getMessage());
  }

  private void prepareRequestMocks(HttpMethod method, IRequestData requestData) {

    when(webClient.requestAbs(method, OKAPI_URL + requestData.getPath())).thenReturn(httpRequest);
    when(httpRequest.as(jsonObject())).thenReturn(jsonRequest);
    when(jsonRequest.putHeaders(any())).thenReturn(jsonRequest);
    when(jsonRequest.putHeader(TOKEN, ACCESS_TOKEN)).thenReturn(jsonRequest);
    when(jsonRequest.putHeader(TENANT, TENANT_ID)).thenReturn(jsonRequest);
    when(jsonRequest.putHeader(eq(REQUEST_ID), anyString())).thenReturn(jsonRequest);
  }

  private static IRequestData testRequestData() {
    return new TestRequestData(FolioResourceProviderTest.REQUEST_PATH, null);
  }

  private static IRequestData testRequestDataWithBody(String path, JsonObject body) {
    return new TestRequestData(path, body);
  }

  private static HttpResponse<JsonObject> httpResponse(
      int statusCode, String statusMessage, JsonObject body) {
    var headers = httpHeaders();
    headers.add("content-type", "application/json");
    return new HttpResponseImpl<>(HTTP_1_1, statusCode, statusMessage,
        headers, caseInsensitiveMultiMap(), emptyList(), body, emptyList());
  }

  private static class TestRequestData implements IRequestData {
    private final String path;
    private final JsonObject body;
    private final SessionData sessionData;

    TestRequestData(String path, JsonObject body) {
      this.path = path;
      this.body = body;
      this.sessionData = SessionData.createSession(TENANT_ID, '|', true, "UTF-8");
    }

    @Override
    public SessionData getSessionData() {
      return sessionData;
    }

    @Override
    public String getPath() {
      return path;
    }

    @Override
    public JsonObject getBody() {
      return body;
    }

    @Override
    public Map<String, String> getHeaders() {
      return Map.of("test-header", "test-value");
    }
  }
}
