package org.folio.edge.sip2.exception;

import org.folio.edge.sip2.repositories.FolioRequestThrowable;

public class MissingAccessTokenException extends FolioRequestThrowable {

  public MissingAccessTokenException() {
    super("Access token is missing. Please login to Folio to obtain a valid access token.");
  }
}
