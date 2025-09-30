package org.folio.edge.sip2.service.tenant;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toMap;
import static org.folio.edge.sip2.service.tenant.TenantResolverNames.IP_SUBNET_RESOLVER_NAME;
import static org.folio.edge.sip2.service.tenant.TenantResolverNames.PORT_RESOLVER_NAME;
import static org.folio.edge.sip2.utils.TenantUtils.SC_TENANTS;

import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.function.Function;
import org.folio.edge.sip2.domain.TenantResolutionContext;
import org.folio.edge.sip2.utils.Sip2LogAdapter;
import org.folio.edge.sip2.utils.Utils;

public class Sip2TenantService {

  private final Sip2LogAdapter log = Sip2LogAdapter.getLogger(Sip2TenantService.class);

  private static final String SIP_2_TENANT_RESOLVERS_ENV_VAR = "SIP2_TENANT_RESOLVERS";
  private static final String SIP_2_TENANT_RESOLVERS_SYSTEM_PROPERTY = "sip2TenantResolvers";

  private static final String DEFAULT_SIP2_TENANT_RESOLVERS = new StringJoiner(",")
      .add(PORT_RESOLVER_NAME)
      .add(IP_SUBNET_RESOLVER_NAME)
      .toString();

  private final List<TenantResolver> tenantResolvers;

  @Inject
  public Sip2TenantService(Set<TenantResolver> tenantResolvers) {
    this.tenantResolvers = collectResolvers(tenantResolvers);
  }

  /**
   * Resolves the tenant configuration for the given resolution context.
   * Iterates through the configured tenant resolvers matching the resolution phase,
   * and returns the first successful tenant configuration as a JsonObject.
   *
   * @param context the tenant resolution context containing resolution phase and other data
   * @return an {@link Optional} containing the resolved tenant config, or empty if none found
   */
  public Optional<JsonObject> findConfiguration(TenantResolutionContext context) {
    var sessionData = context.getSessionData();
    var resolutionPhase = context.getResolutionPhase();
    var sip2TenantsConfig = context.getSip2TenantsConfig();

    if (!sip2TenantsConfig.containsKey(SC_TENANTS)) {
      log.debug(sessionData, "Sip2TenantService :: scTenants key not found in config, "
          + "support for muti-tenant not available");
      return Optional.empty();
    }

    for (var resolver : tenantResolvers) {
      var resolverPhase = resolver.getPhase();
      if (!Objects.equals(resolverPhase, resolutionPhase)) {
        continue;
      }

      var tenantConfig = resolver.resolve(context);
      var resolverClassName = resolver.getClass().getSimpleName();
      if (tenantConfig.isEmpty()) {
        log.debug(sessionData,
            "Sip2TenantService:: {} not resolved multi-tenant configuration.", resolverClassName);
        continue;
      }

      log.debug(sessionData, "Sip2TenantService :: Multi-tenant configuration found (by '{}'): {}",
          () -> resolverClassName, () -> tenantConfig.get().encode());
      return tenantConfig;
    }

    return Optional.empty();
  }

  private List<TenantResolver> collectResolvers(Set<TenantResolver> existingResolvers) {
    var resolversByName = existingResolvers.stream()
        .collect(toMap(TenantResolver::getName, Function.identity(), (o1, o2) -> o2));

    var resultResolvers = new ArrayList<TenantResolver>();
    var notFoundNames = new LinkedHashSet<String>();
    var visitedResolverNames = new LinkedHashSet<String>();
    for (var configurationValue : getConfiguration().split(",")) {
      var configName = configurationValue.trim();
      if (visitedResolverNames.contains(configName)) {
        continue;
      }

      var resolver = resolversByName.get(configName);
      visitedResolverNames.add(configName);
      if (resolver == null) {
        notFoundNames.add(configName);
        continue;
      }

      log.debug("Sip2TenantService :: Enabling tenant resolver: {}",
          () -> resolver.getClass().getSimpleName());
      resultResolvers.add(resolver);
    }

    if (!notFoundNames.isEmpty()) {
      log.warn("Sip2TenantService :: The following tenant resolvers "
              + "were not found by names: {}, allowed values are: {}",
          String.join(",", notFoundNames), new TreeSet<>(resolversByName.keySet()));
    }

    return unmodifiableList(resultResolvers);
  }

  /**
   * Retrieves the SIP2 tenant resolver configuration.
   * <p>LoginHandler
   * The configuration is determined by checking, in order:
   * <ul>
   *   <li>The system property {@code sip2TenantResolvers}</li>
   *   <li>The environment variable {@code SIP2_TENANT_RESOLVERS}</li>
   *   <li>The default value {@code LOCATION_CODE,USERNAME_PREFIX,PORT,IP_ADDRESS}</li>
   * </ul>
   * </p>
   *
   * @return the resolved SIP2 tenant resolver configuration string
   */
  private static String getConfiguration() {
    return Utils.getEnvOrDefault(
        SIP_2_TENANT_RESOLVERS_SYSTEM_PROPERTY,
        SIP_2_TENANT_RESOLVERS_ENV_VAR,
        DEFAULT_SIP2_TENANT_RESOLVERS,
        Function.identity());
  }
}
