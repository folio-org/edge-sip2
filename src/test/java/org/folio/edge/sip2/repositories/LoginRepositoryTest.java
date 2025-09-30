package org.folio.edge.sip2.repositories;

import static io.vertx.core.Future.succeededFuture;
import static io.vertx.core.MultiMap.caseInsensitiveMultiMap;
import static io.vertx.core.http.impl.headers.HeadersMultiMap.httpHeaders;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.emptyList;
import static org.apache.hc.core5.http.HttpHeaders.COOKIE;
import static org.folio.okapi.common.XOkapiHeaders.REQUEST_ID;
import static org.folio.okapi.common.XOkapiHeaders.TENANT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.impl.HttpResponseImpl;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.folio.edge.sip2.domain.integration.login.FolioLoginResponse;
import org.folio.edge.sip2.domain.messages.enumerations.PWDAlgorithm;
import org.folio.edge.sip2.domain.messages.enumerations.UIDAlgorithm;
import org.folio.edge.sip2.domain.messages.requests.Login;
import org.folio.edge.sip2.domain.messages.responses.LoginResponse;
import org.folio.edge.sip2.exception.MissingAccessTokenThrowable;
import org.folio.edge.sip2.session.SessionData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith({ VertxExtension.class, MockitoExtension.class })
class LoginRepositoryTest {

  private static final String OKAPI_URL = "http://okapi:9130";
  private static final String TENANT_ID = "testtenant";
  private static final String JWT = "FAKE_ACCESS_TOKEN";
  private static final String EXPIRED_JWT = "EXPIRED_ACCESS_TOKEN";
  private static final String SERVICE_POINT = "library";

  private static final String USERNAME = "test";
  private static final String PASSWORD = "xyzzy";

  private LoginRepository loginRepository;
  @Mock private WebClient webClient;
  @Mock private HttpRequest<Buffer> httpRequest;
  @Mock private HttpRequest<JsonObject> jsonRequest;

  @BeforeEach
  void setUp() {
    loginRepository = new LoginRepository(OKAPI_URL, webClient);
  }

  @Test
  void canCreateLoginRepository() {
    var newLoginRepository = new LoginRepository(OKAPI_URL, webClient);
    assertNotNull(newLoginRepository);
  }

  @Test
  void login_positive(VertxTestContext testContext) {
    var sessionData = sessionData();
    var httpResponse = loginResponse201(List.of(accessTokenCookie()));

    prepareLoginRequestMocks(sessionData, httpResponse);
    var resultFuture = loginRepository.login(loginBody(), sessionData);

    resultFuture.onComplete(testContext.succeeding(result -> {
      assertEquals(LoginResponse.of(TRUE), result);

      var lr = sessionData.getLoginResponse();
      assertNotNull(lr);
      assertEquals(JWT, lr.getAccessToken());
      assertEquals(OffsetDateTime.parse("2025-01-01T00:00:00Z"), lr.getAccessTokenExpiration());
      assertEquals(OffsetDateTime.parse("2025-01-01T00:00:00Z"), lr.getRefreshTokenExpiration());
      testContext.completeNow();
    }));
  }

  @Test
  void login_negative_invalidCredentials(VertxTestContext testContext) {
    var sessionData = sessionData();

    prepareLoginRequestMocks(sessionData, loginResponse401());
    var resultFuture = loginRepository.login(loginBody(), sessionData);

    resultFuture.onComplete(testContext.succeeding(result -> {
      var expectedErrorMessage = "Failed to perform login request: 401 Unauthorized";
      assertNull(sessionData.getLoginResponse());
      assertEquals(expectedErrorMessage, sessionData.getLoginErrorMessage());
      assertEquals(LoginResponse.of(FALSE), result);
      testContext.completeNow();
    }));
  }

  @Test
  void login_negative_missingCookieValue(VertxTestContext testContext) {
    var sessionData = sessionData();
    var httpResponse = loginResponse201(List.of("test_cookie=test_value"));

    prepareLoginRequestMocks(sessionData, httpResponse);
    var resultFuture = loginRepository.login(loginBody(), sessionData);

    resultFuture.onComplete(testContext.succeeding(result -> {
      var expectedErrorMessage = "Access token not found in cookies";
      assertEquals(expectedErrorMessage, sessionData.getLoginErrorMessage());
      assertEquals(LoginResponse.of(FALSE), result);
      assertNull(sessionData.getLoginResponse());
      testContext.completeNow();
    }));
  }

  @Test
  void patronLoginNoCache_positive(VertxTestContext testContext) {
    var sessionData = sessionData();
    var expiration = OffsetDateTime.now().plusMinutes(5);
    var folioLoginResponse = new FolioLoginResponse("FAKE_JWT_TOKEN", expiration, expiration);
    sessionData.setLoginResponse(folioLoginResponse);

    prepareLoginRequestMocks(sessionData, loginResponse201(List.of(accessTokenCookie())));
    var resultFuture = loginRepository.patronLoginNoCache(USERNAME, PASSWORD, sessionData);

    resultFuture.onComplete(testContext.succeeding(result -> {
      assertEquals(JWT, result);
      testContext.completeNow();
    }));
  }

  @Test
  void patronLoginNoCache_negative(VertxTestContext testContext) {
    var sessionData = sessionData();
    var expiration = OffsetDateTime.now().plusMinutes(5);
    var folioLoginResponse = new FolioLoginResponse("FAKE_JWT_TOKEN", expiration, expiration);
    sessionData.setLoginResponse(folioLoginResponse);

    prepareLoginRequestMocks(sessionData, loginResponse401());
    var resultFuture = loginRepository.patronLoginNoCache(USERNAME, PASSWORD, sessionData);

    resultFuture.onComplete(testContext.failing(error -> {
      assertInstanceOf(FolioRequestThrowable.class, error);
      assertEquals("Failed to perform login request: 401 Unauthorized", error.getMessage());
      testContext.completeNow();
    }));
  }

  @Test
  void getSessionAccessToken_positive(VertxTestContext testContext) {
    var sessionData = sessionData();
    var expiration = OffsetDateTime.now().plusMinutes(5);
    sessionData.setLoginResponse(new FolioLoginResponse(JWT, expiration, expiration));

    var resultFuture = loginRepository.getSessionAccessToken(sessionData);
    resultFuture.onComplete(testContext.succeeding(result -> {
      assertEquals(JWT, result);
      testContext.completeNow();
    }));
  }

  @Test
  void getSessionAccessToken_positive_expiredAccessToken(VertxTestContext testContext) {
    var sessionData = sessionData();
    var accessExp = OffsetDateTime.now().minusMinutes(5);
    var refreshExp = OffsetDateTime.now().plusMinutes(15);
    sessionData.setLoginResponse(new FolioLoginResponse(EXPIRED_JWT, accessExp, refreshExp));

    prepareRefreshRequestMocks(sessionData, loginResponse201(List.of(accessTokenCookie())));
    var resultFuture = loginRepository.getSessionAccessToken(sessionData);

    resultFuture.onComplete(testContext.succeeding(result -> {
      assertEquals(JWT, result);
      testContext.completeNow();
    }));
  }

  @Test
  void getSessionAccessToken_negative_expiredAccessToken(VertxTestContext testContext) {
    var sessionData = sessionData(USERNAME, PASSWORD);
    var accessExp = OffsetDateTime.now().minusMinutes(5);
    var refreshExp = OffsetDateTime.now().plusMinutes(15);
    sessionData.setLoginResponse(new FolioLoginResponse(EXPIRED_JWT, accessExp, refreshExp));

    prepareRefreshRequestMocks(sessionData, loginResponse401());
    prepareLoginRequestMocks(sessionData, loginResponse201(List.of(accessTokenCookie())));
    var resultFuture = loginRepository.getSessionAccessToken(sessionData);

    resultFuture.onComplete(testContext.succeeding(result -> {
      assertEquals(JWT, result);
      testContext.completeNow();
    }));
  }

  @Test
  void getSessionAccessToken_positive_expiredRefreshToken(VertxTestContext testContext) {
    var sessionData = sessionData(USERNAME, PASSWORD);
    var accessExp = OffsetDateTime.now().minusMinutes(5);
    var refreshExp = OffsetDateTime.now().minusMinutes(5);
    sessionData.setLoginResponse(new FolioLoginResponse(EXPIRED_JWT, accessExp, refreshExp));

    prepareLoginRequestMocks(sessionData, loginResponse201(List.of(accessTokenCookie())));
    var resultFuture = loginRepository.getSessionAccessToken(sessionData);

    resultFuture.onComplete(testContext.succeeding(result -> {
      assertEquals(JWT, result);
      testContext.completeNow();
    }));
  }

  @Test
  void getSessionAccessToken_negative_expiredRefreshToken(VertxTestContext testContext) {
    var sessionData = sessionData(USERNAME, PASSWORD);
    var accessExp = OffsetDateTime.now().minusMinutes(5);
    var refreshExp = OffsetDateTime.now().minusMinutes(5);
    sessionData.setLoginResponse(new FolioLoginResponse(EXPIRED_JWT, accessExp, refreshExp));

    prepareLoginRequestMocks(sessionData, loginResponse401());
    var resultFuture = loginRepository.getSessionAccessToken(sessionData);

    resultFuture.onComplete(testContext.failing(error -> {
      assertInstanceOf(FolioRequestThrowable.class, error);
      assertEquals("Failed to perform login request: 401 Unauthorized", error.getMessage());
      testContext.completeNow();
    }));
  }

  @Test
  void getSessionAccessToken_negative_accessTokenIsNotIssued(VertxTestContext testContext) {
    var sessionData = sessionData();
    var resultFuture = loginRepository.getSessionAccessToken(sessionData);

    resultFuture.onComplete(testContext.failing(error -> {
      testContext.completeNow();
      assertInstanceOf(MissingAccessTokenThrowable.class, error);
      var expectedErrorMessage = "Access token is missing. "
          + "Please login to Folio to obtain a valid access token.";
      assertEquals(expectedErrorMessage, error.getMessage());
    }));
  }

  private void prepareLoginRequestMocks(SessionData sd, HttpResponse<JsonObject> httpResponse) {
    when(webClient.postAbs(OKAPI_URL + "/authn/login-with-expiry")).thenReturn(httpRequest);
    when(httpRequest.as(BodyCodec.jsonObject())).thenReturn(jsonRequest);
    when(jsonRequest.putHeader(TENANT, TENANT_ID)).thenReturn(jsonRequest);
    when(jsonRequest.putHeader(REQUEST_ID, sd.getRequestId())).thenReturn(jsonRequest);
    when(jsonRequest.sendJsonObject(loginRequestJson())).thenReturn(succeededFuture(httpResponse));
  }

  private void prepareRefreshRequestMocks(SessionData sd, HttpResponse<JsonObject> httpResponse) {
    when(webClient.postAbs(OKAPI_URL + "/authn/refresh")).thenReturn(httpRequest);
    when(httpRequest.as(BodyCodec.jsonObject())).thenReturn(jsonRequest);
    when(jsonRequest.putHeader(COOKIE, "folioAccessToken=" + EXPIRED_JWT)).thenReturn(jsonRequest);
    when(jsonRequest.putHeader(TENANT, TENANT_ID)).thenReturn(jsonRequest);
    when(jsonRequest.putHeader(REQUEST_ID, sd.getRequestId())).thenReturn(jsonRequest);
    when(jsonRequest.send()).thenReturn(succeededFuture(httpResponse));
  }

  private static Login loginBody() {
    return Login.builder()
        .uidAlgorithm(UIDAlgorithm.NO_ENCRYPTION)
        .pwdAlgorithm(PWDAlgorithm.NO_ENCRYPTION)
        .loginUserId(USERNAME)
        .loginPassword(PASSWORD)
        .locationCode(SERVICE_POINT)
        .build();
  }

  private static SessionData sessionData() {
    return SessionData.createSession(TENANT_ID, '|', true, "UTF-8");
  }

  @SuppressWarnings("SameParameterValue")
  private static SessionData sessionData(String username, String password) {
    var sessionData = SessionData.createSession(TENANT_ID, '|', true, "UTF-8");
    sessionData.setUsername(username);
    sessionData.setPassword(password);
    return sessionData;
  }

  private static JsonObject loginRequestJson() {
    var userCredentials = new JsonObject();
    userCredentials.put("username", USERNAME);
    userCredentials.put("password", PASSWORD);
    return userCredentials;
  }

  private static HttpResponse<JsonObject> loginResponse201(List<String> cookies) {
    var responseBody = new JsonObject();
    responseBody.put("accessTokenExpiration", "2025-01-01T00:00:00Z");
    responseBody.put("refreshTokenExpiration", "2025-01-01T00:00:00Z");

    var headers = httpHeaders();
    headers.add("content-type", "application/json");

    return new HttpResponseImpl<>(HttpVersion.HTTP_1_0, 201, "Created",
        headers, caseInsensitiveMultiMap(), cookies, responseBody, emptyList());
  }

  private static HttpResponse<JsonObject> loginResponse401() {
    var responseJson = new JsonObject("""
        {
          "errors": [
            {
              "message": "Unauthorized error",
              "type": "UnauthorizedException",
              "code": "unauthorized_error",
              "parameters": []
            }
          ],
          "total_records": 1
        }""");
    return new HttpResponseImpl<>(HttpVersion.HTTP_1_0, 401, "Unauthorized",
        httpHeaders(), caseInsensitiveMultiMap(), emptyList(), responseJson, emptyList());
  }

  private static String accessTokenCookie() {
    return "folioAccessToken=" + JWT + "; Path=/; "
        + "Expires=Wed, 01 Jan 2025 00:00:00 GMT; HttpOnly";
  }
}
