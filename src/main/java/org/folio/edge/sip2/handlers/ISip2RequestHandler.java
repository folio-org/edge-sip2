package org.folio.edge.sip2.handlers;

import io.vertx.core.Future;
import org.folio.edge.sip2.session.SessionData;

public interface ISip2RequestHandler {
  Future<String> execute(Object message, SessionData sessionData);
}
