package org.folio.edge.sip2.service.tenant;

import static org.folio.edge.sip2.utils.TenantUtils.SC_TENANTS;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.Optional;
import org.folio.edge.sip2.domain.TenantResolutionContext;
import org.folio.edge.sip2.domain.type.TenantResolutionPhase;

public interface TenantResolver {

  /**
   * Resolves tenant information based on the provided context.
   *
   * @param context the tenant resolution context containing necessary data
   * @return an {@link Optional} containing the resolved tenant, or empty if not resolved
   */
  Optional<JsonObject> resolve(TenantResolutionContext context);

  /**
   * Name of the resolver implementation.
   *
   * @return the name of the resolver
   */
  String getName();

  /**
   * Phase of execution for the resolver implementation.
   *
   * @return - the phase when resolver must be executed
   */
  TenantResolutionPhase getPhase();

  /**
   * Retrieves the array of SC tenants from the given SIP2 configuration.
   *
   * @param sip2Config - the SIP2 configuration as a JsonObject
   * @return a list containing SC tenants JsonObjects, or an empty list if key is not present
   */
  default List<JsonObject> getScTenantsArray(JsonObject sip2Config) {
    return sip2Config.getJsonArray(SC_TENANTS, new JsonArray()).stream()
        .filter(JsonObject.class::isInstance)
        .map(JsonObject.class::cast)
        .toList();
  }
}
