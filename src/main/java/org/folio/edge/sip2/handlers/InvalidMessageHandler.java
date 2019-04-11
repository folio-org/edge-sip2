package org.folio.edge.sip2.handlers;

import io.vertx.core.Future;

public class InvalidMessageHandler implements ISip2RequestHandler {
  @Override
  public Future<String> execute(Object message) {
    return Future.succeededFuture("96\r");
  }
}
