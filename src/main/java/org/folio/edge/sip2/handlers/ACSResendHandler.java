package org.folio.edge.sip2.handlers;

import io.vertx.core.Future;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.PreviousMessage;
import org.folio.edge.sip2.parser.Message;
import org.folio.edge.sip2.session.SessionData;


public class ACSResendHandler implements ISip2RequestHandler {

  private static final Logger log = LogManager.getLogger();

  @Override
  public Future<String> execute(Object message, SessionData sessionData) {
    log.debug("ACSResendHandler :: execute message:{} sessionData:{}",message,sessionData);
    PreviousMessage prevMessage = sessionData.getPreviousMessage();

    if (prevMessage == null) {
      return Future.failedFuture("PreviousMessage is NULL");
    }
    log.info("ACSResendHandler :: execute prevMessage:{}",prevMessage);
    return Future.succeededFuture(prevMessage.getPreviousMessageResponse());
  }

  @Override
  public void writeHistory(SessionData sessionData, Message<Object> request, String response) {
    //Do nothing. No need to save a response for the 97 message.
  }
}
