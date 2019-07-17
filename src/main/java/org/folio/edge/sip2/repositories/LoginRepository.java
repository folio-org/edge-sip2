package org.folio.edge.sip2.repositories;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.requests.Login;
import org.folio.edge.sip2.domain.messages.responses.LoginResponse;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.Utils;

/**
 * Provides interaction with the login service.
 *
 * @author mreno-EBSCO
 *
 */
public class LoginRepository {
  private static final Logger log = LogManager.getLogger();
  private final IResourceProvider<IRequestData> resourceProvider;

  @Inject
  LoginRepository(IResourceProvider<IRequestData> resourceProvider) {
    this.resourceProvider = Objects.requireNonNull(resourceProvider,
        "Resource provider cannot be null");
  }

  /**
   * Perform a login.
   *
   * @param login the login domain object
   * @param sessionData shared session data
   * @return the login response domain object
   */
  public Future<LoginResponse> login(Login login, SessionData sessionData) {
    final String user = login.getLoginUserId();
    final String password = login.getLoginPassword();
    final String locationCode = login.getLocationCode();

    final JsonObject credentials = new JsonObject()
        .put("username", user)
        .put("password", password);

    final Future<IResource> result = resourceProvider
        .createResource(new LoginRequestData(credentials, sessionData));

    return result
        .otherwise(() -> null)
        .compose(resource -> {
          final String authenticationToken = resource.getAuthenticationToken();
          if (authenticationToken == null) {
            // Can't continue without an auth token
            log.error("Login does not have a valid authentication token");
            return Future.succeededFuture(LoginResponse.builder().ok(FALSE).build());
          }
          sessionData.setAuthenticationToken(authenticationToken);
          sessionData.setScLocation(locationCode);
          return Future.succeededFuture(
            LoginResponse.builder()
              .ok(resource.getResource() == null ? FALSE : TRUE)
              .build());
        });
  }

  /**
   * Perform a login.
   *
   * @param patronUserName the patron's user name
   * @param patronPassword the patron's password
   * @param sessionData shared session data
   * @return the login response domain object
   */
  public Future<IResource> patronLogin(String patronUserName, String patronPassword,
      SessionData sessionData) {
    final JsonObject credentials = new JsonObject()
        .put("username", patronUserName)
        .put("password", patronPassword);

    final Future<IResource> result = resourceProvider
        .createResource(new LoginRequestData(credentials, sessionData));

    return result
        .otherwise(Utils::handleErrors);
  }

  private class LoginRequestData implements IRequestData {
    private final JsonObject body;
    private final SessionData sessionData;

    private LoginRequestData(JsonObject body, SessionData sessionData) {
      this.body = body;
      this.sessionData = sessionData;
    }

    @Override
    public String getPath() {
      return "/authn/login";
    }

    @Override
    public Map<String, String> getHeaders() {
      Map<String, String> headers = new HashMap<>();
      headers.put("accept", "application/json");
      return headers;
    }

    @Override
    public JsonObject getBody() {
      return body;
    }

    @Override
    public SessionData getSessionData() {
      return sessionData;
    }
  }
}
