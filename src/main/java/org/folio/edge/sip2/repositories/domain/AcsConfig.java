package org.folio.edge.sip2.repositories.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AcsConfig {

  private final AcsTenantConfig acsTenantConfig;
  private final ScStationConfig scStationConfig;
  private final TenantLocaleConfig tenantLocaleConfig;
}
