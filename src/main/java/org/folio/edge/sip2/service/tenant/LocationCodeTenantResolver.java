package org.folio.edge.sip2.service.tenant;

import static org.folio.edge.sip2.service.tenant.TenantResolverNames.LOCATION_CODE_RESOLVER_NAME;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.folio.edge.sip2.domain.TenantResolutionContext;
import org.folio.edge.sip2.domain.type.TenantResolutionPhase;

/**
 * Resolves a tenant based on the SIP2 login location code.
 * <p>
 * This resolver checks the session data for a location code and attempts to match it
 * against the configured tenant location codes. If a match is found, the corresponding
 * tenant configuration is returned.
 * </p>
 * <ul>
 *   <li>Implements {@link TenantResolver}.</li>
 *   <li>Resolution phase: {@link TenantResolutionPhase#LOGIN}.</li>
 *   <li>Returns the tenant configuration as a {@link JsonObject} if a match is found.</li>
 * </ul>
 */
public class LocationCodeTenantResolver implements TenantResolver {

  @Override
  public Optional<JsonObject> resolve(TenantResolutionContext context) {
    var loginLocationCode = context.getSessionData().getScLocation();
    if (StringUtils.isBlank(loginLocationCode)) {
      return Optional.empty();
    }

    return getScTenantsArray(context.getSip2TenantsConfig()).stream()
        .filter(config -> getConfigLocationCodes(config).contains(loginLocationCode))
        .findFirst();
  }

  @Override
  public String getName() {
    return LOCATION_CODE_RESOLVER_NAME;
  }

  @Override
  public TenantResolutionPhase getPhase() {
    return TenantResolutionPhase.LOGIN;
  }

  private List<String> getConfigLocationCodes(JsonObject jo) {
    return jo.getJsonArray("locationCodes", new JsonArray()).stream()
        .filter(String.class::isInstance)
        .map(String.class::cast)
        .toList();
  }
}
