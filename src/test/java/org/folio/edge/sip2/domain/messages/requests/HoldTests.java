package org.folio.edge.sip2.domain.messages.requests;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.folio.edge.sip2.domain.messages.enumerations.HoldMode.ADD;
import static org.folio.edge.sip2.domain.messages.enumerations.HoldMode.CHANGE;
import static org.folio.edge.sip2.domain.messages.enumerations.HoldType.ANY_COPY_LOCATION;
import static org.folio.edge.sip2.domain.messages.enumerations.HoldType.SPECIFIC_COPY_TITLE;
import static org.folio.edge.sip2.domain.messages.requests.Hold.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;

import org.folio.edge.sip2.domain.messages.enumerations.HoldMode;
import org.folio.edge.sip2.domain.messages.enumerations.HoldType;
import org.junit.jupiter.api.Test;

class HoldTests {
  final HoldMode holdMode = ADD;
  final OffsetDateTime transactionDate = OffsetDateTime.now();
  final OffsetDateTime expirationDate = transactionDate.plusDays(30);
  final String pickupLocation = "circ_desk";
  final HoldType holdType = SPECIFIC_COPY_TITLE;
  final String institutionId = "diku";
  final String patronIdentifier = "1234567890";
  final String patronPassword = "2112";
  final String itemIdentifier = "8675309";
  final String titleIdentifier = "5551212";
  final String terminalPassword = "12345";
  final Boolean feeAcknowledged = TRUE;

  @Test
  void testGetHoldMode() {
    final Hold h = builder().holdMode(holdMode).build();
    assertEquals(holdMode, h.getHoldMode());
    assertNull(h.getTransactionDate());
    assertNull(h.getExpirationDate());
    assertNull(h.getPickupLocation());
    assertNull(h.getHoldType());
    assertNull(h.getInstitutionId());
    assertNull(h.getPatronIdentifier());
    assertNull(h.getPatronPassword());
    assertNull(h.getItemIdentifier());
    assertNull(h.getTitleIdentifier());
    assertNull(h.getTerminalPassword());
    assertNull(h.getFeeAcknowledged());
  }

  @Test
  void testGetTransactionDate() {
    final Hold h = builder().transactionDate(transactionDate).build();
    assertNull(h.getHoldMode());
    assertEquals(transactionDate, h.getTransactionDate());
    assertNull(h.getExpirationDate());
    assertNull(h.getPickupLocation());
    assertNull(h.getHoldType());
    assertNull(h.getInstitutionId());
    assertNull(h.getPatronIdentifier());
    assertNull(h.getPatronPassword());
    assertNull(h.getItemIdentifier());
    assertNull(h.getTitleIdentifier());
    assertNull(h.getTerminalPassword());
    assertNull(h.getFeeAcknowledged());
  }

  @Test
  void testGetExpirationDate() {
    final Hold h = builder().expirationDate(expirationDate).build();
    assertNull(h.getHoldMode());
    assertNull(h.getTransactionDate());
    assertEquals(expirationDate, h.getExpirationDate());
    assertNull(h.getPickupLocation());
    assertNull(h.getHoldType());
    assertNull(h.getInstitutionId());
    assertNull(h.getPatronIdentifier());
    assertNull(h.getPatronPassword());
    assertNull(h.getItemIdentifier());
    assertNull(h.getTitleIdentifier());
    assertNull(h.getTerminalPassword());
    assertNull(h.getFeeAcknowledged());
  }

  @Test
  void testGetPickupLocation() {
    final Hold h = builder().pickupLocation(pickupLocation).build();
    assertNull(h.getHoldMode());
    assertNull(h.getTransactionDate());
    assertNull(h.getExpirationDate());
    assertEquals(pickupLocation, h.getPickupLocation());
    assertNull(h.getHoldType());
    assertNull(h.getInstitutionId());
    assertNull(h.getPatronIdentifier());
    assertNull(h.getPatronPassword());
    assertNull(h.getItemIdentifier());
    assertNull(h.getTitleIdentifier());
    assertNull(h.getTerminalPassword());
    assertNull(h.getFeeAcknowledged());
  }


  @Test
  void testGetHoldType() {
    final Hold h = builder().holdType(holdType).build();
    assertNull(h.getHoldMode());
    assertNull(h.getTransactionDate());
    assertNull(h.getExpirationDate());
    assertNull(h.getPickupLocation());
    assertEquals(holdType, h.getHoldType());
    assertNull(h.getInstitutionId());
    assertNull(h.getPatronIdentifier());
    assertNull(h.getPatronPassword());
    assertNull(h.getItemIdentifier());
    assertNull(h.getTitleIdentifier());
    assertNull(h.getTerminalPassword());
    assertNull(h.getFeeAcknowledged());
  }

  @Test
  void testGetInstitutionId() {
    final Hold h = builder().institutionId(institutionId).build();
    assertNull(h.getHoldMode());
    assertNull(h.getTransactionDate());
    assertNull(h.getExpirationDate());
    assertNull(h.getPickupLocation());
    assertNull(h.getHoldType());
    assertEquals(institutionId, h.getInstitutionId());
    assertNull(h.getPatronIdentifier());
    assertNull(h.getPatronPassword());
    assertNull(h.getItemIdentifier());
    assertNull(h.getTitleIdentifier());
    assertNull(h.getTerminalPassword());
    assertNull(h.getFeeAcknowledged());
  }

  @Test
  void testGetPatronIdentifier() {
    final Hold h = builder().patronIdentifier(patronIdentifier).build();
    assertNull(h.getHoldMode());
    assertNull(h.getTransactionDate());
    assertNull(h.getExpirationDate());
    assertNull(h.getPickupLocation());
    assertNull(h.getHoldType());
    assertNull(h.getInstitutionId());
    assertEquals(patronIdentifier, h.getPatronIdentifier());
    assertNull(h.getPatronPassword());
    assertNull(h.getItemIdentifier());
    assertNull(h.getTitleIdentifier());
    assertNull(h.getTerminalPassword());
    assertNull(h.getFeeAcknowledged());
  }

  @Test
  void testGetPatronPassword() {
    final Hold h = builder().patronPassword(patronPassword).build();
    assertNull(h.getHoldMode());
    assertNull(h.getTransactionDate());
    assertNull(h.getExpirationDate());
    assertNull(h.getPickupLocation());
    assertNull(h.getHoldType());
    assertNull(h.getInstitutionId());
    assertNull(h.getPatronIdentifier());
    assertEquals(patronPassword, h.getPatronPassword());
    assertNull(h.getItemIdentifier());
    assertNull(h.getTitleIdentifier());
    assertNull(h.getTerminalPassword());
    assertNull(h.getFeeAcknowledged());
  }

  @Test
  void testGetItemIdentifier() {
    final Hold h = builder().itemIdentifier(itemIdentifier).build();
    assertNull(h.getHoldMode());
    assertNull(h.getTransactionDate());
    assertNull(h.getExpirationDate());
    assertNull(h.getPickupLocation());
    assertNull(h.getHoldType());
    assertNull(h.getInstitutionId());
    assertNull(h.getPatronIdentifier());
    assertNull(h.getPatronPassword());
    assertEquals(itemIdentifier, h.getItemIdentifier());
    assertNull(h.getTitleIdentifier());
    assertNull(h.getTerminalPassword());
    assertNull(h.getFeeAcknowledged());
  }

  @Test
  void testGetTitleIdentifier() {
    final Hold h = builder().titleIdentifier(titleIdentifier).build();
    assertNull(h.getHoldMode());
    assertNull(h.getTransactionDate());
    assertNull(h.getExpirationDate());
    assertNull(h.getPickupLocation());
    assertNull(h.getHoldType());
    assertNull(h.getInstitutionId());
    assertNull(h.getPatronIdentifier());
    assertNull(h.getPatronPassword());
    assertNull(h.getItemIdentifier());
    assertEquals(titleIdentifier, h.getTitleIdentifier());
    assertNull(h.getTerminalPassword());
    assertNull(h.getFeeAcknowledged());
  }

  @Test
  void testGetTerminalPassword() {
    final Hold h = builder().terminalPassword(terminalPassword).build();
    assertNull(h.getHoldMode());
    assertNull(h.getTransactionDate());
    assertNull(h.getExpirationDate());
    assertNull(h.getPickupLocation());
    assertNull(h.getHoldType());
    assertNull(h.getInstitutionId());
    assertNull(h.getPatronIdentifier());
    assertNull(h.getPatronPassword());
    assertNull(h.getItemIdentifier());
    assertNull(h.getTitleIdentifier());
    assertEquals(terminalPassword, h.getTerminalPassword());
    assertNull(h.getFeeAcknowledged());
  }

  @Test
  void testGetFeeAcknowledged() {
    final Hold h = builder().feeAcknowledged(feeAcknowledged).build();
    assertNull(h.getHoldMode());
    assertNull(h.getTransactionDate());
    assertNull(h.getExpirationDate());
    assertNull(h.getPickupLocation());
    assertNull(h.getHoldType());
    assertNull(h.getInstitutionId());
    assertNull(h.getPatronIdentifier());
    assertNull(h.getPatronPassword());
    assertNull(h.getItemIdentifier());
    assertNull(h.getTitleIdentifier());
    assertNull(h.getTerminalPassword());
    assertEquals(feeAcknowledged, h.getFeeAcknowledged());
  }

  @Test
  void testCompleteHold() {
    final Hold h = builder()
        .holdMode(holdMode)
        .transactionDate(transactionDate)
        .expirationDate(expirationDate)
        .pickupLocation(pickupLocation)
        .holdType(holdType)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .patronPassword(patronPassword)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .terminalPassword(terminalPassword)
        .feeAcknowledged(feeAcknowledged)
        .build();
    assertAll("Hold",
        () -> assertEquals(holdMode, h.getHoldMode()),
        () -> assertEquals(transactionDate, h.getTransactionDate()),
        () -> assertEquals(expirationDate, h.getExpirationDate()),
        () -> assertEquals(pickupLocation, h.getPickupLocation()),
        () -> assertEquals(holdType, h.getHoldType()),
        () -> assertEquals(institutionId, h.getInstitutionId()),
        () -> assertEquals(patronIdentifier, h.getPatronIdentifier()),
        () -> assertEquals(patronPassword, h.getPatronPassword()),
        () -> assertEquals(itemIdentifier, h.getItemIdentifier()),
        () -> assertEquals(titleIdentifier, h.getTitleIdentifier()),
        () -> assertEquals(terminalPassword, h.getTerminalPassword()),
        () -> assertEquals(feeAcknowledged, h.getFeeAcknowledged())
    );
  }

  @Test
  void testEqualsObject() {
    final Hold h1 = builder()
        .holdMode(holdMode)
        .transactionDate(transactionDate)
        .expirationDate(expirationDate)
        .pickupLocation(pickupLocation)
        .holdType(holdType)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .patronPassword(patronPassword)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .terminalPassword(terminalPassword)
        .feeAcknowledged(feeAcknowledged)
        .build();
    final Hold h2 = builder()
        .holdMode(holdMode)
        .transactionDate(transactionDate)
        .expirationDate(expirationDate)
        .pickupLocation(pickupLocation)
        .holdType(holdType)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .patronPassword(patronPassword)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .terminalPassword(terminalPassword)
        .feeAcknowledged(feeAcknowledged)
        .build();
    assertTrue(h1.equals(h2));
    assertTrue(h1.equals(h2));
  }

  @Test
  void testNotEqualsObject() {
    final Hold h1 = builder()
        .holdMode(holdMode)
        .transactionDate(transactionDate)
        .expirationDate(expirationDate)
        .pickupLocation(pickupLocation)
        .holdType(holdType)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .patronPassword(patronPassword)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .terminalPassword(terminalPassword)
        .feeAcknowledged(feeAcknowledged)
        .build();
    final Hold h2 = builder()
        .holdMode(CHANGE)
        .transactionDate(transactionDate.minusDays(100))
        .expirationDate(expirationDate.minusDays(50))
        .pickupLocation("circ_desk_basement")
        .holdType(ANY_COPY_LOCATION)
        .institutionId("xyzzy")
        .patronIdentifier("111111111")
        .patronPassword("0000000000")
        .itemIdentifier("222222222")
        .titleIdentifier("777777777")
        .terminalPassword("88888888")
        .feeAcknowledged(FALSE)
        .build();
    assertFalse(h1.equals(h2));
    assertFalse(h1.equals(h2));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("Hold [holdMode=").append(holdMode)
        .append(", transactionDate=").append(transactionDate)
        .append(", expirationDate=").append(expirationDate)
        .append(", pickupLocation=").append(pickupLocation)
        .append(", holdType=").append(holdType)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", patronPassword=").append(patronPassword)
        .append(", itemIdentifier=").append(itemIdentifier)
        .append(", titleIdentifier=").append(titleIdentifier)
        .append(", terminalPassword=").append(terminalPassword)
        .append(", feeAcknowledged=").append(feeAcknowledged)
        .append(']').toString();
    final Hold h = builder()
        .holdMode(holdMode)
        .transactionDate(transactionDate)
        .expirationDate(expirationDate)
        .pickupLocation(pickupLocation)
        .holdType(holdType)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .patronPassword(patronPassword)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .terminalPassword(terminalPassword)
        .feeAcknowledged(feeAcknowledged)
        .build();
    assertEquals(expectedString, h.toString());
  }
}
