package org.folio.edge.sip2.handlers;

public class InvalidMessageHandler implements ISip2RequestHandler {
  @Override
  public String execute(Object message) {
    return "96\r";
  }
}
