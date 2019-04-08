package org.folio.edge.sip2.domain.messages.enumerations;

/**
 * Defined circulation statuses.
 *
 * @author mreno-EBSCO
 *
 */
public enum CirculationStatus {
  OTHER,
  ON_ORDER,
  AVAILABLE,
  CHARGED,
  CHARGED_NOT_TO_BE_RECALLED_UNTIL_EARLIEST_RECALL_DATE,
  IN_PROCESS,
  RECALLED,
  WAITING_ON_HOLD_SHELF,
  WAITING_TO_BE_RESHELVED,
  IN_TRANSIT_BETWEEN_LIBRARY_LOCATIONS,
  CLAIMED_RETURNED,
  LOST,
  MISSING
}
