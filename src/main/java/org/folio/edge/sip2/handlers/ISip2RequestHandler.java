package org.folio.edge.sip2.handlers;

import java.io.IOException;

public interface ISip2RequestHandler {
  String execute(String sipInputMessage) throws IOException;
}
