package org.folio.edge.sip2.exception;

import org.folio.edge.sip2.repositories.FolioRequestThrowable;

public class TenantNotResolvedException extends FolioRequestThrowable {

  /**
   * Constructs a new TenantNotResolvedException with a message indicating
   * that the tenant configuration could not be resolved for the given session.
   *
   * @param sessionId the session identifier for which the tenant could not be resolved
   */
  public TenantNotResolvedException(String sessionId) {
    super("Tenant configuration is not resolved for session: " + sessionId);
  }
}
