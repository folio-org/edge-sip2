package org.folio.edge.sip2.service.tenant;

import static org.folio.edge.sip2.service.tenant.TenantResolverNames.USERNAME_PREFIX_RESOLVER_NAME;

import io.vertx.core.json.JsonObject;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import org.folio.edge.sip2.domain.TenantResolutionContext;
import org.folio.edge.sip2.domain.type.TenantResolutionPhase;
import org.folio.edge.sip2.utils.Utils;

/**
 * Resolves a tenant based on a prefix in the SIP2 username.
 * <p>
 * This resolver extracts the tenant identifier from the username by splitting it
 * using a configurable delimiter. If the username contains the delimiter, the prefix
 * before the delimiter is used as the tenant identifier.
 * </p>
 * <ul>
 *   <li>Implements {@link TenantResolver}.</li>
 *   <li>Resolution phase: {@link TenantResolutionPhase#LOGIN}.</li>
 *   <li>Returns the tenant identifier as a {@link JsonObject} if a prefix is found.</li>
 *   <li>Uses the delimiter from the system variable:
 *     {@code sip2TenantUsernamePrefixDelimiter} or
 *     env variable: {@code SIP2_TENANT_USERNAME_PREFIX_DELIMITER}.
 *     defaulting to {@code __} if not set.
 *   </li>
 * </ul>
 */
public class UsernamePrefixTenantResolver implements TenantResolver {

  private static final String PREFIX_DELIMITER = getPrefixDelimiter();

  @Override
  public Optional<JsonObject> resolve(TenantResolutionContext context) {
    var sessionData = context.getSessionData();
    var username = sessionData.getUsername();
    if (StringUtils.isBlank(username) || !username.contains(PREFIX_DELIMITER)) {
      return Optional.empty();
    }

    return Arrays.stream(StringUtils.split(username, PREFIX_DELIMITER))
        .findFirst()
        .map(resolvedTenant -> new JsonObject().put("tenant", resolvedTenant));
  }

  @Override
  public String getName() {
    return USERNAME_PREFIX_RESOLVER_NAME;
  }

  @Override
  public TenantResolutionPhase getPhase() {
    return TenantResolutionPhase.LOGIN;
  }

  private static String getPrefixDelimiter() {
    return Utils.getEnvOrDefault(
        "sip2TenantUsernamePrefixDelimiter",
        "SIP2_TENANT_USERNAME_PREFIX_DELIMITER",
        "__", Function.identity());
  }
}
