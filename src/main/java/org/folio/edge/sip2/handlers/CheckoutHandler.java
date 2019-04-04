package org.folio.edge.sip2.handlers;

public class CheckoutHandler implements ISip2RequestHandler {

  @Override
  public String execute(String sipInputMessage) {

    //call FOLIO

    //format into SIP message
    return "Successfully checked out " + sipInputMessage;

  }
}
