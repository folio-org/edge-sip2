package org.folio.edge.sip2.handlers;

import org.folio.edge.sip2.domain.messages.requests.Login;

public class LoginHandler implements ISip2RequestHandler {
  @Override
  public String execute(Object message) {
    final Login login = (Login) message;
    return "Logged " + login.toString() + " in";
  }
}
