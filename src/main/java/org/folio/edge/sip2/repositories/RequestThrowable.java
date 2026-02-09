package org.folio.edge.sip2.repositories;

import io.vertx.core.VertxException;
import java.io.Serial;
import java.util.List;

/**
 * Throwable that occurs when a Request fails in some way.
 *
 * @author mreno-EBSCO
 *
 */
public abstract class RequestThrowable extends VertxException {

  @Serial private static final long serialVersionUID = 252204234554468581L;

  public RequestThrowable(String message) {
    super(message, true);
  }

  public abstract List<String> getErrorMessages();
}
