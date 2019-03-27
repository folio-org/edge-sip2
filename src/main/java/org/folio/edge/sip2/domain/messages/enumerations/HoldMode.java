package org.folio.edge.sip2.domain.messages.enumerations;

/**
 * Hold operations supported by the SC.
 * 
 * @author mreno-EBSCO
 *
 */
public enum HoldMode {
  /** Add patron the the hold queue for the item. */
  ADD,
  /** Delete patron from the hold queue for the item. */
  DELETE,
  /**
   * Change the hold to match the {@code Hold} message parameters.
   */
  CHANGE;
}
