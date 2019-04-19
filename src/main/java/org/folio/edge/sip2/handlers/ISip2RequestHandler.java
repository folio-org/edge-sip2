package org.folio.edge.sip2.handlers;

import io.vertx.core.Future;

import org.folio.edge.sip2.parser.Message;
import org.folio.edge.sip2.repositories.HistoricalMessageRepository;


public interface ISip2RequestHandler {

  /**
   * Handle the request and prepare a SIP response to send back.
   * @param message  Request message that contains all the information needed to fill the response.
   * @return  SIP response string
   */
  Future<String> execute(Object message);

  /**
   * Save the current request/response as a history item (for the next request).
   * @param request A parsed SIP request object
   * @param response SIP response for the passed in request
   */
  default void writeHistory(Message<Object> request, String response) {
    HistoricalMessageRepository.setPreviousMessage(request, response);
  }
}
