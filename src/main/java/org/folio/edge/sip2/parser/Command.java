package org.folio.edge.sip2.parser;

import java.util.Arrays;

/**
 * Valid SIP commands.
 * @author mreno-EBSCO
 *
 */
public enum Command {
  UNKNOWN(""),
  // SC->ACS messages
  PATRON_STATUS_REQUEST("23"),
  CHECKOUT("11"),
  CHECKIN("09"),
  BLOCK_PATRON("01"),
  SC_STATUS("99"),
  REQUEST_ACS_RESEND("97"),
  LOGIN("93"),
  PATRON_INFORMATION("63"),
  END_PATRON_SESSION("35"),
  FEE_PAID("37"),
  ITEM_INFORMATION("17"),
  ITEM_STATUS_UPDATE("19"),
  PATRON_ENABLE("25"),
  HOLD("15"),
  RENEW("29"),
  RENEW_ALL("65"),
  // ACS->SC messages
  PATRON_STATUS_RESPONSE("24"),
  CHECKOUT_RESPONSE("12"),
  CHECKIN_RESPONSE("10"),
  ACS_STATUS("98"),
  REQUEST_SC_RESEND("96"),

  ITEM_INFORMATION_RESPONSE("18"),
  LOGIN_RESPONSE("94"),
  PATRON_INFORMATION_RESPONSE("64"),
  END_SESSION_RESPONSE("36"),
  FEE_PAID_RESPONSE("38"),
  ITEM_STATUS_UPDATE_RESPONSE("20"),
  PATRON_ENABLE_RESPONSE("26"),
  HOLD_RESPONSE("16"),
  RENEW_RESPONSE("30"),
  RENEW_ALL_RESPONSE("66");

  private final String identifier;

  private Command(String identifier) {
    this.identifier = identifier;
  }

  /**
   * Find an enum base on the command identifier.
   *
   * @param identifier the command identifier.
   * @return the found command enum or {@code UNKNOWN} if not found.
   */
  public static Command find(String identifier) {
    return Arrays.stream(values())
        .filter(status -> status.identifier.equals(identifier))
        .findFirst()
        .orElse(UNKNOWN);
  }
}
