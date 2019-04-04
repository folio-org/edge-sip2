package org.folio.edge.sip2.handlers;

public class LoginHandler implements ISip2RequestHandler {
  @Override
  public String execute(String sipInputMessage) {
    return "Logged " + sipInputMessage + " in";
  }
}
