package org.folio.edge.sip2.exception;

import org.folio.edge.sip2.repositories.FolioRequestThrowable;

public class MissingAccessTokenThrowable extends FolioRequestThrowable {

  public MissingAccessTokenThrowable() {
    super("Access token is missing. Please login to Folio to obtain a valid access token.");
  }
}
