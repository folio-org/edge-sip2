package org.folio.edge.sip2.handlers;

import io.vertx.core.Future;
import org.folio.edge.sip2.domain.messages.requests.Checkin;

public class CheckinHandler implements ISip2RequestHandler {
  @Override
  public Future<String> execute(Object message) {
    final Checkin checkin = (Checkin) message;
    //call FOLIO

    //format into SIP message
    return Future.succeededFuture("Successfully checked in "
        + checkin.toString());

  }
}
