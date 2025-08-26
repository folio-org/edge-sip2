package org.folio.edge.sip2.repositories;

import static com.google.common.net.HttpHeaders.COOKIE;
import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;
import static io.vertx.core.http.HttpResponseExpectation.SC_CREATED;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.time.ZoneOffset.UTC;
import static org.folio.okapi.common.Constants.COOKIE_ACCESS_TOKEN;
import static org.folio.okapi.common.XOkapiHeaders.REQUEST_ID;
import static org.folio.okapi.common.XOkapiHeaders.TENANT;

import io.netty.handler.codec.http.cookie.ClientCookieDecoder;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.vertx.core.Expectation;
import io.vertx.core.Future;
import io.vertx.core.http.HttpResponseHead;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.folio.edge.sip2.domain.integration.login.FolioLoginResponse;
import org.folio.edge.sip2.domain.messages.requests.Login;
import org.folio.edge.sip2.domain.messages.responses.LoginResponse;
import org.folio.edge.sip2.exception.MissingAccessTokenThrowable;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.Sip2LogAdapter;

/**
 * Provides interaction with the login service.
 *
 * @author mreno-EBSCO
 *
 */
public class LoginRepository {

  private static final Sip2LogAdapter log = Sip2LogAdapter.getLogger(LoginRepository.class);
  public static final int TOKEN_OFFSET_SECONDS = 90;

  private final String okapiUrl;
  private final WebClient client;

  /**
   * Construct a FOLIO resource provider with the specified parameters.
   *
   * @param okapiUrl  the URL for okapi
   * @param webClient the WebClient instance
   */
  @Inject
  LoginRepository(@Named("okapiUrl") String okapiUrl, @Named("webClient") WebClient webClient) {
    this.okapiUrl = okapiUrl;
    this.client = webClient;
  }

  /**
   * Perform a login.
   *
   * @param login       the login domain object
   * @param sessionData shared session data
   * @return the login response domain object
   */
  public Future<LoginResponse> login(Login login, SessionData sessionData) {
    var user = login.getLoginUserId();
    var password = login.getLoginPassword();
    sessionData.setUsername(user);
    sessionData.setPassword(password);
    sessionData.setScLocation(login.getLocationCode());

    return performLogin(sessionData, user, password, true)
        .map(loginResponse -> setLoginResponseToSessionData(sessionData, loginResponse))
        .map(FolioLoginResponse::getAccessToken)
        .recover(error -> recoverFailedLogin(sessionData))
        .compose(token -> succeededFuture(LoginResponse.of(token == null ? FALSE : TRUE)));
  }

  /**
   * Retrieves the current session's access token from the provided SessionData.
   * If the access token is expired, attempts to refresh it or perform a new login.
   *
   * @param sd the session data containing authentication information
   * @return a Future containing the access token as a String
   */
  public Future<String> getSessionAccessToken(SessionData sd) {
    var lr = sd.getLoginResponse();
    if (lr == null) {
      return failedFuture(new MissingAccessTokenThrowable());
    }

    var now = OffsetDateTime.now(UTC);
    var approxExpiration = lr.getAccessTokenExpiration().minusSeconds(TOKEN_OFFSET_SECONDS);
    if (now.isAfter(approxExpiration)) {
      var approxRefreshExp = lr.getRefreshTokenExpiration().minusSeconds(TOKEN_OFFSET_SECONDS);
      var folioLoginResponse = now.isAfter(approxRefreshExp)
          ? performLogin(sd, sd.getUsername(), sd.getPassword(), false)
          : refreshToken(sd);
      return folioLoginResponse.map(FolioLoginResponse::getAccessToken);
    }

    return succeededFuture(lr.getAccessToken());
  }

  /**
   * Performs a login for a patron without using cached credentials.
   *
   * @param username the patron's username
   * @param password the patron's password
   * @param sd       the session data
   * @return a Future containing the access token as a String
   */
  public Future<String> patronLoginNoCache(String username, String password, SessionData sd) {
    return performLogin(sd, username, password, true)
        .map(FolioLoginResponse::getAccessToken);
  }

  /**
   * Performs a login request to the FOLIO system using the provided session data,
   * username, and password. Sends a POST request to the /login-with-expiry endpoint,
   * expects a JSON response, and extracts the access token from the response.
   *
   * @param sd       the session data containing tenant and request information
   * @param username the username to authenticate
   * @param password the password to authenticate
   * @return a Future containing the FolioLoginResponse with access and refresh tokens
   */
  private Future<FolioLoginResponse> performLogin(SessionData sd,
      String username, String password, boolean isPatron) {
    log.debug(sd, "login:: performing login for user");
    return client.postAbs(okapiUrl + "/authn/login-with-expiry")
        .as(BodyCodec.jsonObject())
        .putHeader(TENANT, sd.getTenant())
        .putHeader(REQUEST_ID, sd.getRequestId())
        .sendJsonObject(getLoginRequestBody(username, password))
        .expecting(getLoginExpectations(sd))
        .map(LoginRepository::extractFolioAccessToken)
        .onSuccess(loginResponse -> handleSuccessLogin(sd, isPatron))
        .onFailure(err -> handleErrorResponse(sd, "login:: Unable to get the access token", err));
  }

  /**
   * Refreshes the authentication token using the refresh token stored in the session data.
   * If the refresh operation fails, attempts to perform a new login using the username
   * and password.
   *
   * @param sessionData the session data containing authentication information
   * @return a Future containing the refreshed FolioLoginResponse
   */
  private Future<FolioLoginResponse> refreshToken(SessionData sessionData) {
    log.debug(sessionData, "refreshToken:: performing refresh token operation");
    var accessToken = sessionData.getLoginResponse().getAccessToken();
    return client.postAbs(okapiUrl + "/authn/refresh")
        .as(BodyCodec.jsonObject())
        .putHeader(COOKIE, new DefaultCookie(COOKIE_ACCESS_TOKEN, accessToken).toString())
        .putHeader(TENANT, sessionData.getTenant())
        .putHeader(REQUEST_ID, sessionData.getRequestId())
        .send()
        .expecting(getLoginExpectations(sessionData))
        .map(LoginRepository::extractFolioAccessToken)
        .recover(err -> recoverRefreshToken(sessionData, err))
        .onSuccess(loginResponse -> handleSuccessRefresh(sessionData, loginResponse))
        .onFailure(err -> handleErrorResponse(sessionData, "Unable to refresh token ", err));
  }

  private static JsonObject getLoginRequestBody(String username, String password) {
    var userCredentials = new JsonObject();
    userCredentials.put("username", username);
    userCredentials.put("password", password);
    return userCredentials;
  }

  private static Expectation<HttpResponseHead> getLoginExpectations(SessionData sd) {
    return SC_CREATED.wrappingFailure((head, err) -> getLoginRequestError(sd, head, err));
  }

  private static void handleSuccessRefresh(SessionData sessionData, FolioLoginResponse resp) {
    log.info(sessionData, "refreshToken:: Access token refreshed");
    sessionData.setLoginResponse(resp);
  }

  private static Future<String> recoverFailedLogin(SessionData sessionData) {
    sessionData.setLoginResponse(null);
    return succeededFuture(null);
  }

  private Future<FolioLoginResponse> recoverRefreshToken(SessionData sessionData, Throwable err) {
    log.warn(sessionData, "Unable to refresh token, trying to get new access token...", err);
    return performLogin(sessionData, sessionData.getUsername(), sessionData.getPassword(), false);
  }

  private static FolioRequestThrowable getLoginRequestError(SessionData sessionData,
      HttpResponseHead responseHead, Throwable e) {
    var status = responseHead.statusCode() + " " + responseHead.statusMessage();
    log.error(sessionData, "login:: Invalid response from FOLIO '{}': {}", status, e.getMessage());
    return new FolioRequestThrowable("Failed to perform login request: " + status);
  }

  private static FolioLoginResponse setLoginResponseToSessionData(
      SessionData sessionData, FolioLoginResponse loginResponse) {
    sessionData.setLoginResponse(loginResponse);
    return loginResponse;
  }

  private static FolioLoginResponse extractFolioAccessToken(HttpResponse<JsonObject> resp) {
    var body = resp.body();
    return getAccessTokenFromCookies(resp)
        .map(accessToken -> createLoginResponse(accessToken, body))
        .orElseThrow(() -> new IllegalStateException("Access token not found in cookies"));
  }

  private static FolioLoginResponse createLoginResponse(String token, JsonObject respBody) {
    var atExpiration = OffsetDateTime.parse(respBody.getString("accessTokenExpiration"));
    var rtExpiration = OffsetDateTime.parse(respBody.getString("refreshTokenExpiration"));
    return new FolioLoginResponse(token, atExpiration, rtExpiration);
  }

  private static Optional<String> getAccessTokenFromCookies(HttpResponse<JsonObject> response) {
    for (var cookieString : response.cookies()) {
      var cookie = ClientCookieDecoder.STRICT.decode(cookieString);
      if (COOKIE_ACCESS_TOKEN.equals(cookie.name())) {
        return Optional.ofNullable(cookie.value());
      }
    }

    return Optional.empty();
  }

  private static void handleErrorResponse(SessionData sd, String message, Throwable throwable) {
    log.error(sd, message, throwable);
    sd.setLoginErrorMessage(null);
    sd.setAuthenticationToken(null);
    sd.setLoginErrorMessage(throwable.getMessage());
  }

  private static void handleSuccessLogin(SessionData sd, boolean isPatron) {
    if (isPatron) {
      log.info(sd, "login:: Access token requested for patron");
      return;
    }
    log.info(sd, "login:: Session access token requested");
  }
}
