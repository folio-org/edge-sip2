package org.folio.edge.sip2.domain.messages.enumerations;

/**
 * Indicates the type of the {@code Hold} message.
 * @author mreno-EBSCO
 *
 */
public enum HoldType {
  /** Another hold mechanism. */
  OTHER,
  /** Any copy or a title. */
  ANY_COPY_TITLE,
  /** A specific copy of a title. */
  SPECIFIC_COPY_TITLE,
  /** Any copy at a single branch or sublocation. */
  ANY_COPY_LOCATION
}
