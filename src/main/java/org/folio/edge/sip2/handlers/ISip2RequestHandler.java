package org.folio.edge.sip2.handlers;

import io.vertx.core.Future;

public interface ISip2RequestHandler {
  Future<String> execute(Object message);
}
