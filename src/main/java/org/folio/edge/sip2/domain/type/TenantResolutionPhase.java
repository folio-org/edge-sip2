package org.folio.edge.sip2.domain.type;

public enum TenantResolutionPhase {
  /**
   * Resolvers with this type will be applied during the connection opening phase.
   */
  CONNECT,

  /**
   * Resolvers with this type will be applied during the login operation.
   */
  LOGIN
}
