package org.folio.edge.sip2.handlers;

import org.folio.edge.sip2.domain.messages.requests.Checkout;

public class CheckoutHandler implements ISip2RequestHandler {

  @Override
  public String execute(Object message) {
    final Checkout checkout = (Checkout) message;
    //call FOLIO

    //format into SIP message
    return "Successfully checked out " + checkout.toString();

  }
}
