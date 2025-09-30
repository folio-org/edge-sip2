package org.folio.edge.sip2.service.tenant;

import static org.folio.edge.sip2.service.tenant.TenantResolverNames.IP_SUBNET_RESOLVER_NAME;

import inet.ipaddr.IPAddressString;
import io.vertx.core.json.JsonObject;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.folio.edge.sip2.domain.TenantResolutionContext;
import org.folio.edge.sip2.domain.type.TenantResolutionPhase;

/**
 * Resolves the tenant based on the client's IP address subnet.
 * <p>
 * This resolver checks if the client's IP address falls within any configured subnet
 * in the SIP2 tenants configuration. If a match is found, the corresponding tenant
 * configuration is returned.
 * </p>
 * <ul>
 *   <li>Implements {@link TenantResolver}.</li>
 *   <li>Resolution phase: {@link TenantResolutionPhase#CONNECT}.</li>
 *   <li>Uses {@code IPAddressString} for IP validation and subnet matching.</li>
 *   <li>Logs warnings for invalid client IPs or subnet values.</li>
 *   <li>Returns the tenant configuration as a {@link JsonObject} if a match is found.</li>
 * </ul>
 */
@Log4j2
public class IpTenantResolver implements TenantResolver {

  private static final String SC_SUBNET = "scSubnet";

  @Override
  public Optional<JsonObject> resolve(TenantResolutionContext context) {
    var clientIP = context.getConnectionDetails().getClientAddress();
    var clientIpString = new IPAddressString(clientIP);

    if (!clientIpString.isValid()) {
      log.warn("Invalid client IP address, returning default tenant: {}", clientIP);
      return Optional.empty();
    }

    var sip2TenantsConfig = context.getSip2TenantsConfig();
    return getScTenantsArray(sip2TenantsConfig).stream()
        .filter(jo -> isInRange(jo, clientIpString))
        .findFirst();
  }

  @Override
  public String getName() {
    return IP_SUBNET_RESOLVER_NAME;
  }

  @Override
  public TenantResolutionPhase getPhase() {
    return TenantResolutionPhase.CONNECT;
  }

  private static boolean isInRange(JsonObject jo, IPAddressString clientIpAddress) {
    var subnetValues = jo.getString(SC_SUBNET);
    if (StringUtils.isBlank(subnetValues)) {
      return false;
    }

    var subnet = new IPAddressString(subnetValues);
    if (!subnet.isValid()) {
      log.warn("Invalid subnet value in tenant config: {}", jo);
      return false;
    }

    return subnet.contains(clientIpAddress);
  }
}
