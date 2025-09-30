package org.folio.edge.sip2.service.tenant;

import static org.folio.edge.sip2.service.tenant.TenantResolverNames.PORT_RESOLVER_NAME;

import io.vertx.core.json.JsonObject;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.folio.edge.sip2.domain.TenantResolutionContext;
import org.folio.edge.sip2.domain.type.TenantResolutionPhase;

/**
 * Resolves a tenant based on the client port from the SIP2 connection.
 * <p>
 * This resolver inspects the client port from the connection details and attempts to match it
 * against the configured tenant port values. If a match is found, the corresponding
 * tenant configuration is returned.
 * </p>
 * <ul>
 *   <li>Implements {@link TenantResolver}.</li>
 *   <li>Resolution phase: {@link TenantResolutionPhase#CONNECT}.</li>
 *   <li>Returns the tenant configuration as a {@link JsonObject} if a matching port is found.</li>
 * </ul>
 */
@Log4j2
public class PortTenantResolver implements TenantResolver {

  @Override
  public Optional<JsonObject> resolve(TenantResolutionContext context) {
    var port = context.getConnectionDetails().getClientPort();
    return getScTenantsArray(context.getSip2TenantsConfig()).stream()
        .filter(configurationObject -> Objects.equals(port, getPort(configurationObject)))
        .findFirst();
  }

  @Override
  public TenantResolutionPhase getPhase() {
    return TenantResolutionPhase.CONNECT;
  }

  @Override
  public String getName() {
    return PORT_RESOLVER_NAME;
  }

  private Integer getPort(JsonObject jo) {
    if (!jo.containsKey("port")) {
      return null;
    }

    try {
      return Integer.parseInt(jo.getString("port"));
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
