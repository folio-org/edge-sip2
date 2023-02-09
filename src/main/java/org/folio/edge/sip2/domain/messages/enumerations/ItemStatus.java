package org.folio.edge.sip2.domain.messages.enumerations;

import java.util.Arrays;

/**
 * Defined item statuses.
 *
 * @author mreno-EBSCO
 *
 */
public enum ItemStatus {
  NONE(""),
  AVAILABLE("Available"),
  AWAITING_PICKUP("Awaiting pickup"),
  AWAITING_DELIVERY("Awaiting delivery"),
  CHECKED_OUT("Checked out"),
  IN_TRANSIT("In transit"),
  MISSING("Missing"),
  PAGED("Paged"),
  ON_ORDER("On order"),
  IN_PROCESS("In process"),
  DECLARED_LOST("Declared lost"),
  CLAIMED_RETURNED("Claimed returned"),
  WITHDRAWN("Withdrawn"),
  LOST_AND_PAID("Lost and paid"),
  INTELLECTUAL_ITEM("Intellectual item"),
  IN_PROCESS_NON_REQUESTABLE("In process (non-requestable)"),
  LONG_MISSING("Long missing"),
  UNAVAILABLE("Unavailable"),
  UNKNOWN("Unknown"),
  RESTRICTED("Restricted"),
  AGED_TO_LOST("Aged to lost");

  /**
   * Lookup SIP CirculationStatus by FOLIO Item Status.
   *
   * @param value the itemStatus string from the FOLIO JSON respons
   * @return the CirculationStatus enum item
   */
  public static ItemStatus from(String value) {
    return Arrays.stream(values())
      .filter(status -> status.valueMatches(value))
      .findFirst()
      .orElse(NONE);
  }

  private final String value;

  ItemStatus(String value) {
    this.value = value;
  }

  /**
   * Return enum value.
   *
   * @return the ItemStatus String value
   */
  public String getValue() {
    return value;
  }

  /**
   * Compare values of a String and Enum contents.
   *
   * @param value the string 
   * @return the answer
   */
  private boolean valueMatches(String value) {
    return getValue().equalsIgnoreCase(value);
  }

  /**
   * isLostNotResolved.
   *
  * @return boolean answer
   */
  public boolean isLostNotResolved() {
    return this == DECLARED_LOST || this == AGED_TO_LOST;
  }
}
