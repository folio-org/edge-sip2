package org.folio.edge.sip2.handlers;

import io.vertx.core.Future;
import org.folio.edge.sip2.domain.PreviousMessage;
import org.folio.edge.sip2.parser.Message;
import org.folio.edge.sip2.session.SessionData;

public interface ISip2RequestHandler {

  /**
   * Handle the request and prepare a SIP response to send back.
   * @param message  Request message that contains all the information needed to fill the response.
   * @return  SIP response string
   */
  Future<String> execute(Object message, SessionData sessionData);

  /**
   * Save the current request/response as a history item (for the next request).
   * @param sessionData Session object to store the history
   * @param request A parsed SIP request object
   * @param response SIP response for the passed in request
   */
  default void writeHistory(SessionData sessionData, Message<Object> request, String response) {
    sessionData.setPreviousMessage(new PreviousMessage(request, response));
  }
}
