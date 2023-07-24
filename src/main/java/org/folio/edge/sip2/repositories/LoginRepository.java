package org.folio.edge.sip2.repositories;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import io.vertx.core.Future;
import java.util.Objects;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.requests.Login;
import org.folio.edge.sip2.domain.messages.responses.LoginResponse;
import org.folio.edge.sip2.session.SessionData;
import org.folio.okapi.common.refreshtoken.client.ClientException;

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
    sessionData.setUsername(user);
    sessionData.setPassword(password);
    Future<String> authToken = null;
    authToken = resourceProvider.loginWithSupplier(user,
        () -> Future.succeededFuture(password), sessionData)
        .onFailure(e -> {
          log.error("Login does not have a valid authentication token");
          sessionData.setAuthenticationToken(null);
        });
    if (authToken == null) {
      // Can't continue without an auth token
      log.error("Login does not have a valid authentication token");
      sessionData.setAuthenticationToken(null);
      return Future.succeededFuture(LoginResponse.builder().ok(FALSE).build());
    }

    return authToken
     .compose(token -> {
       sessionData.setAuthenticationToken(token);
       sessionData.setScLocation(locationCode);
       return Future.succeededFuture(
        LoginResponse.builder()
          .ok(token == null ? FALSE : TRUE)
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
  public Future<String> patronLogin(String patronUserName, String patronPassword,
      SessionData sessionData) {
    return resourceProvider.loginWithSupplier(patronUserName,
      () -> Future.succeededFuture(patronPassword), sessionData);
  }
}
