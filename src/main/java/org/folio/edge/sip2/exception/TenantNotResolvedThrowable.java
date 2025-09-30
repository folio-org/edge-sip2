package org.folio.edge.sip2.exception;

import org.folio.edge.sip2.repositories.FolioRequestThrowable;

public class TenantNotResolvedThrowable extends FolioRequestThrowable {

  /**
   * Constructs a new {@link TenantNotResolvedThrowable} with a message indicating
   * that the tenant configuration could not be resolved for the given session.
   *
   * @param sessionId the session identifier for which the tenant could not be resolved
   */
  public TenantNotResolvedThrowable(String sessionId) {
    super("Tenant configuration is not resolved for session: " + sessionId);
  }
}
