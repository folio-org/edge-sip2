package org.folio.edge.sip2.handlers;

import io.vertx.core.Future;

import org.folio.edge.sip2.domain.PreviousMessage;
import org.folio.edge.sip2.parser.Message;
import org.folio.edge.sip2.repositories.HistoricalMessageRepository;
import org.folio.edge.sip2.session.SessionData;


public class ACSResendHandler implements ISip2RequestHandler {

  @Override
  public Future<String> execute(Object message, SessionData sessionData) {
    PreviousMessage prevMessage = HistoricalMessageRepository.getPreviousMessage();

    if (prevMessage == null) {
      return Future.failedFuture("PreviousMessage is NULL");
    }
    return Future.succeededFuture(prevMessage.getPreviousMessageResponse());
  }

  @Override
  public void writeHistory(Message<Object> request, String response) {
    //Do nothing. No need to save a response for the 97 message.
  }
}
