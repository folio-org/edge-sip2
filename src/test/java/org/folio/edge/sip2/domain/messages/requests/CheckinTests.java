package org.folio.edge.sip2.domain.messages.requests;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.folio.edge.sip2.domain.messages.requests.Checkin.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class CheckinTests {
  final Boolean noBlock = TRUE;
  final OffsetDateTime transactionDate = OffsetDateTime.now();
  final OffsetDateTime returnDate = transactionDate.plusSeconds(30);
  final String currentLocation = "circ_desk1";
  final String institutionId = "diku";
  final String itemIdentifier = "8675309";
  final String terminalPassword = "12345";
  final String itemProperties = "The autographed copy";
  final Boolean cancel = TRUE;

  @Test
  void testGetNoBlock() {
    final Checkin ci = builder().noBlock(noBlock).build();
    assertEquals(noBlock, ci.getNoBlock());
    assertNull(ci.getTransactionDate());
    assertNull(ci.getReturnDate());
    assertNull(ci.getCurrentLocation());
    assertNull(ci.getInstitutionId());
    assertNull(ci.getItemIdentifier());
    assertNull(ci.getTerminalPassword());
    assertNull(ci.getItemProperties());
    assertNull(ci.getCancel());
  }

  @Test
  void testGetTransactionDate() {
    final Checkin ci = builder().transactionDate(transactionDate).build();
    assertNull(ci.getNoBlock());
    assertEquals(transactionDate, ci.getTransactionDate());
    assertNull(ci.getReturnDate());
    assertNull(ci.getCurrentLocation());
    assertNull(ci.getInstitutionId());
    assertNull(ci.getItemIdentifier());
    assertNull(ci.getTerminalPassword());
    assertNull(ci.getItemProperties());
    assertNull(ci.getCancel());

  }

  @Test
  void testGetReturnDate() {
    final Checkin ci = builder().returnDate(returnDate).build();
    assertNull(ci.getNoBlock());
    assertNull(ci.getTransactionDate());
    assertEquals(returnDate, ci.getReturnDate());
    assertNull(ci.getCurrentLocation());
    assertNull(ci.getInstitutionId());
    assertNull(ci.getItemIdentifier());
    assertNull(ci.getTerminalPassword());
    assertNull(ci.getItemProperties());
    assertNull(ci.getCancel());
  }

  @Test
  void testGetCurrentLocation() {
    final Checkin ci = builder().currentLocation(currentLocation).build();
    assertNull(ci.getNoBlock());
    assertNull(ci.getTransactionDate());
    assertNull(ci.getReturnDate());
    assertEquals(currentLocation, ci.getCurrentLocation());
    assertNull(ci.getInstitutionId());
    assertNull(ci.getItemIdentifier());
    assertNull(ci.getTerminalPassword());
    assertNull(ci.getItemProperties());
    assertNull(ci.getCancel());
  }

  @Test
  void testGetInstitutionId() {
    final Checkin ci = builder().institutionId(institutionId).build();
    assertNull(ci.getNoBlock());
    assertNull(ci.getTransactionDate());
    assertNull(ci.getReturnDate());
    assertNull(ci.getCurrentLocation());
    assertEquals(institutionId, ci.getInstitutionId());
    assertNull(ci.getItemIdentifier());
    assertNull(ci.getTerminalPassword());
    assertNull(ci.getItemProperties());
    assertNull(ci.getCancel());
  }

  @Test
  void testGetItemIdentifier() {
    final Checkin ci = builder().itemIdentifier(itemIdentifier).build();
    assertNull(ci.getNoBlock());
    assertNull(ci.getTransactionDate());
    assertNull(ci.getReturnDate());
    assertNull(ci.getCurrentLocation());
    assertNull(ci.getInstitutionId());
    assertEquals(itemIdentifier, ci.getItemIdentifier());
    assertNull(ci.getTerminalPassword());
    assertNull(ci.getItemProperties());
    assertNull(ci.getCancel());
  }

  @Test
  void testGetTerminalPassword() {
    final Checkin ci = builder().terminalPassword(terminalPassword).build();
    assertNull(ci.getNoBlock());
    assertNull(ci.getTransactionDate());
    assertNull(ci.getReturnDate());
    assertNull(ci.getCurrentLocation());
    assertNull(ci.getInstitutionId());
    assertNull(ci.getItemIdentifier());
    assertEquals(terminalPassword, ci.getTerminalPassword());
    assertNull(ci.getItemProperties());
    assertNull(ci.getCancel());
  }

  @Test
  void testGetItemProperties() {
    final Checkin ci = builder().itemProperties(itemProperties).build();
    assertNull(ci.getNoBlock());
    assertNull(ci.getTransactionDate());
    assertNull(ci.getReturnDate());
    assertNull(ci.getCurrentLocation());
    assertNull(ci.getInstitutionId());
    assertNull(ci.getItemIdentifier());
    assertNull(ci.getTerminalPassword());
    assertEquals(itemProperties, ci.getItemProperties());
    assertNull(ci.getCancel());
  }

  @Test
  void testGetCancel() {
    final Checkin ci = builder().cancel(cancel).build();
    assertNull(ci.getNoBlock());
    assertNull(ci.getTransactionDate());
    assertNull(ci.getReturnDate());
    assertNull(ci.getCurrentLocation());
    assertNull(ci.getInstitutionId());
    assertNull(ci.getItemIdentifier());
    assertNull(ci.getTerminalPassword());
    assertNull(ci.getItemProperties());
    assertEquals(cancel, ci.getCancel());
  }

  @Test
  void testCompleteCheckin() {
    final Checkin ci = builder()
        .transactionDate(transactionDate)
        .returnDate(returnDate)
        .currentLocation(currentLocation)
        .institutionId(institutionId)
        .itemIdentifier(itemIdentifier)
        .terminalPassword(terminalPassword)
        .itemProperties(itemProperties)
        .cancel(cancel)
        .build();
    assertAll("Checkin",
        () -> assertEquals(transactionDate, ci.getTransactionDate()),
        () -> assertEquals(returnDate, ci.getReturnDate()),
        () -> assertEquals(currentLocation, ci.getCurrentLocation()),
        () -> assertEquals(institutionId, ci.getInstitutionId()),
        () -> assertEquals(itemIdentifier, ci.getItemIdentifier()),
        () -> assertEquals(terminalPassword, ci.getTerminalPassword()),
        () -> assertEquals(itemProperties, ci.getItemProperties()),
        () -> assertEquals(cancel, ci.getCancel())
    );
  }

  @Test
  void testEqualsObject() {
    final Checkin ci1 = builder()
        .transactionDate(transactionDate)
        .returnDate(returnDate)
        .currentLocation(currentLocation)
        .institutionId(institutionId)
        .itemIdentifier(itemIdentifier)
        .terminalPassword(terminalPassword)
        .itemProperties(itemProperties)
        .cancel(cancel)
        .build();
    final Checkin ci2 = builder()
        .transactionDate(transactionDate)
        .returnDate(returnDate)
        .currentLocation(currentLocation)
        .institutionId(institutionId)
        .itemIdentifier(itemIdentifier)
        .terminalPassword(terminalPassword)
        .itemProperties(itemProperties)
        .cancel(cancel)
        .build();
    assertTrue(ci1.equals(ci2));
    assertTrue(ci1.equals(ci2));
  }

  @Test
  void testNotEqualsObject() {
    final Checkin ci1 = builder()
        .noBlock(noBlock)
        .transactionDate(transactionDate)
        .returnDate(returnDate)
        .currentLocation(currentLocation)
        .institutionId(institutionId)
        .itemIdentifier(itemIdentifier)
        .terminalPassword(terminalPassword)
        .itemProperties(itemProperties)
        .cancel(cancel)
        .build();
    final Checkin ci2 = builder()
        .noBlock(FALSE)
        .transactionDate(transactionDate.minusDays(100))
        .returnDate(returnDate.minusDays(50))
        .currentLocation("111111111")
        .institutionId("xyzzy")
        .itemIdentifier("222222222")
        .terminalPassword("88888888")
        .itemProperties("Give me a book!")
        .cancel(FALSE)
        .build();
    assertFalse(ci1.equals(ci2));
    assertFalse(ci1.equals(ci2));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("Checkin [noBlock=").append(noBlock)
        .append(", transactionDate=").append(transactionDate)
        .append(", returnDate=").append(returnDate)
        .append(", currentLocation=").append(currentLocation)
        .append(", institutionId=").append(institutionId)
        .append(", itemIdentifier=").append(itemIdentifier)
        .append(", terminalPassword=").append(terminalPassword)
        .append(", itemProperties=").append(itemProperties)
        .append(", cancel=").append(cancel)
        .append(']')
        .toString();
    final Checkin ci = builder()
        .noBlock(noBlock)
        .transactionDate(transactionDate)
        .returnDate(returnDate)
        .currentLocation(currentLocation)
        .institutionId(institutionId)
        .itemIdentifier(itemIdentifier)
        .terminalPassword(terminalPassword)
        .itemProperties(itemProperties)
        .cancel(cancel)
        .build();
    assertEquals(expectedString, ci.toString());
  }
}
