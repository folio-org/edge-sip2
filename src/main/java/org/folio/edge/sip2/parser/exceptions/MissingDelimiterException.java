package org.folio.edge.sip2.parser.exceptions;

/**
 * Exception throws if there is a missing delimiter when parsing variable
 * length fields.
 *
 * @author mreno-EBSCO
 *
 */
public class MissingDelimiterException extends RuntimeException {
  private static final long serialVersionUID = -5233466641392972549L;

  public MissingDelimiterException() {
    super();
  }

  public MissingDelimiterException(String message) {
    super(message);
  }

  public MissingDelimiterException(Throwable cause) {
    super(cause);
  }

  public MissingDelimiterException(String message, Throwable cause) {
    super(message, cause);
  }

  public MissingDelimiterException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
