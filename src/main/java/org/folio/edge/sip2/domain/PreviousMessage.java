package org.folio.edge.sip2.domain;

import org.folio.edge.sip2.parser.Message;

/**
 * Class that defines the data structure for the previous message's request + response pair.
 */
public class PreviousMessage {
  private int previousRequestSequenceNo;
  private String previousRequestChecksum;
  private String previousMessageResponse;

  /**
   * Constructor that constructs the PreviousMessage instance.
   *
   * @param message - The parsed request object
   * @param response - the SIP response that corresponds to the @message.
   */
  public PreviousMessage(Message<Object> message, String response) {
    previousRequestSequenceNo = message.getSequenceNumber();
    previousRequestChecksum = message.getChecksumsString();
    previousMessageResponse = response;
  }

  public String getPreviousMessageResponse() {
    return previousMessageResponse;
  }

  public String getPreviousRequestChecksum() {
    return previousRequestChecksum;
  }

  public int getPreviousRequestSequenceNo() {
    return previousRequestSequenceNo;
  }
}
