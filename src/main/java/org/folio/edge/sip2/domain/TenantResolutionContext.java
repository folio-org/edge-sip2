package org.folio.edge.sip2.domain;

import static org.folio.edge.sip2.domain.type.TenantResolutionPhase.CONNECT;
import static org.folio.edge.sip2.domain.type.TenantResolutionPhase.LOGIN;

import io.vertx.core.json.JsonObject;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.folio.edge.sip2.domain.type.TenantResolutionPhase;
import org.folio.edge.sip2.session.SessionData;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TenantResolutionContext {

  private final TenantResolutionPhase resolutionPhase;
  private final SessionData sessionData;
  private final JsonObject sip2TenantsConfig;
  private final ConnectionDetails connectionDetails;

  /**
   * Creates a TenantResolutionContext object.
   *
   * @param config - the sip2 tenant configuration
   * @param connectionDetails - the connection details
   * @return a new TenantResolutionContext instance
   */
  public static TenantResolutionContext createContextForConnectPhase(
      JsonObject config, ConnectionDetails connectionDetails) {
    return new TenantResolutionContext(CONNECT, null, config, connectionDetails);
  }

  /**
   * Creates a TenantResolutionContext object.
   *
   * @param config      - the multitenant configuration
   * @param sessionData - the session data
   * @return a new TenantResolutionContext instance
   */
  public static TenantResolutionContext createContextForLoginPhase(
      JsonObject config, SessionData sessionData) {
    return new TenantResolutionContext(LOGIN, sessionData, config, null);
  }
}
