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
   * @return the login response domain object
   */
  public Future<LoginResponse> login(Login login, SessionData sessionData) {
    final String user = login.getLoginUserId();
    final String password = login.getLoginPassword();
    // We'll need to figure out what to do with the location code

    final JsonObject credentials = new JsonObject()
        .put("username", user)
        .put("password", password);
    
    final Future<IResource> result = resourceProvider
        .createResource(new LoginRequestData(credentials));

    return result
        .otherwiseEmpty()
        .compose(resource -> {
          final String authenticationToken = resource.getAuthenticationToken();
          if (authenticationToken == null) {
            // Can't continue without an auth token
            log.error("Login does not have a valid authentication token");
            return Future.succeededFuture(LoginResponse.builder().ok(FALSE).build());
          }
          sessionData.setAuthenticationToken(authenticationToken);
          return Future.succeededFuture(
            LoginResponse.builder()
              .ok(resource.getResource() == null ? FALSE : TRUE)
              .build());
        });
  }

  private class LoginRequestData implements IRequestData {
    private final JsonObject body;

    private LoginRequestData(JsonObject body) {
      this.body = body;
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
  }
}
