package org.folio.edge.sip2.repositories;

import org.folio.edge.sip2.domain.PreviousMessage;
import org.folio.edge.sip2.parser.Message;

/**
 * Class that manages the previous message's request + response pair.
 */
public class HistoricalMessageRepository {

  private static PreviousMessage previousMessage;

  /**
   * A getter that returns the previous pair of request and response messages.
   * @return Returns previous message stored.
   */
  public static PreviousMessage getPreviousMessage() {
    return previousMessage;
  }

  /**
   * Method to set the previous message.  The "previous" message is the current outgoing message.
   *
   * @param message The parsed request message
   * @param response the entire response message that corresponds to the request message.
   *
   **/
  public static void setPreviousMessage(Message<Object> message, String response) {
    previousMessage = new PreviousMessage(message, response);
  }
}
