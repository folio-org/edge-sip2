package org.folio.edge.sip2.support.model;

public interface Sip2Command {

  /**
   * Generates the SIP2 message string for this command, using the specified field delimiter.
   *
   * @param fieldDelimiter - the character to use as a field delimiter in the SIP2 message
   * @return the formatted SIP2 message string
   */
  String getMessage(char fieldDelimiter);

  /**
   * Indicates whether to ignore error detection for this command.
   *
   * @return - true if error detection should be ignored, false otherwise
   */
  default boolean ignoreErrorDetection() {
    return false;
  }
}
