package org.folio.edge.sip2.domain.messages.enumerations;

/**
 * Enumeration of all SIP messages. Can be used to indicate which messages
 * are supported by the ACS.
 *
 * @author mreno-EBSCO
 *
 */
public enum Messages {
  PATRON_STATUS_REQUEST,
  CHECKOUT,
  CHECKIN,
  BLOCK_PATRON,
  SC_ACS_STATUS,
  REQUEST_SC_ACS_RESEND,
  LOGIN,
  PATRON_INFORMATION,
  END_PATRON_SESSION,
  FEE_PAID,
  ITEM_INFORMATION,
  ITEM_STATUS_UPDATE,
  PATRON_ENABLE,
  HOLD,
  RENEW,
  RENEW_ALL;
}
