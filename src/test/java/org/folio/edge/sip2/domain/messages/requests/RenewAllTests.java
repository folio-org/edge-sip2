package org.folio.edge.sip2.domain.messages.requests;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.folio.edge.sip2.domain.messages.requests.RenewAll.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

class RenewAllTests {
  final ZonedDateTime transactionDate = ZonedDateTime.now();
  final String institutionId = "diku";
  final String patronIdentifier = "1234567890";
  final String patronPassword = "2112";
  final String terminalPassword = "12345";
  final Boolean feeAcknowledged = TRUE;

  @Test
  void testGetTransactionDate() {
    final RenewAll ra = builder().transactionDate(transactionDate).build();
    assertEquals(transactionDate, ra.getTransactionDate());
    assertNull(ra.getInstitutionId());
    assertNull(ra.getPatronIdentifier());
    assertNull(ra.getPatronPassword());
    assertNull(ra.getTerminalPassword());
    assertNull(ra.getFeeAcknowledged());
  }

  @Test
  void testGetInstitutionId() {
    final RenewAll ra = builder().institutionId(institutionId).build();
    assertNull(ra.getTransactionDate());
    assertEquals(institutionId, ra.getInstitutionId());
    assertNull(ra.getPatronIdentifier());
    assertNull(ra.getPatronPassword());
    assertNull(ra.getTerminalPassword());
    assertNull(ra.getFeeAcknowledged());
  }

  @Test
  void testGetPatronIdentifier() {
    final RenewAll ra = builder().patronIdentifier(patronIdentifier).build();
    assertNull(ra.getTransactionDate());
    assertNull(ra.getInstitutionId());
    assertEquals(patronIdentifier, ra.getPatronIdentifier());
    assertNull(ra.getPatronPassword());
    assertNull(ra.getTerminalPassword());
    assertNull(ra.getFeeAcknowledged());
  }

  @Test
  void testGetPatronPassword() {
    final RenewAll ra = builder().patronPassword(patronPassword).build();
    assertNull(ra.getTransactionDate());
    assertNull(ra.getInstitutionId());
    assertNull(ra.getPatronIdentifier());
    assertEquals(patronPassword, ra.getPatronPassword());
    assertNull(ra.getTerminalPassword());
    assertNull(ra.getFeeAcknowledged());
  }

  @Test
  void testGetTerminalPassword() {
    final RenewAll ra = builder().terminalPassword(terminalPassword).build();
    assertNull(ra.getTransactionDate());
    assertNull(ra.getInstitutionId());
    assertNull(ra.getPatronIdentifier());
    assertNull(ra.getPatronPassword());
    assertEquals(terminalPassword, ra.getTerminalPassword());
    assertNull(ra.getFeeAcknowledged());
  }

  @Test
  void testGetFeeAcknowledged() {
    final RenewAll ra = builder().feeAcknowledged(feeAcknowledged).build();
    assertNull(ra.getTransactionDate());
    assertNull(ra.getInstitutionId());
    assertNull(ra.getPatronIdentifier());
    assertNull(ra.getPatronPassword());
    assertNull(ra.getTerminalPassword());
    assertEquals(feeAcknowledged, ra.getFeeAcknowledged());
  }

  @Test
  void testCompleteRenew() {
    final RenewAll ra = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .patronPassword(patronPassword)
        .terminalPassword(terminalPassword)
        .feeAcknowledged(feeAcknowledged)
        .build();
    assertAll("Renew",
        () -> assertEquals(transactionDate, ra.getTransactionDate()),
        () -> assertEquals(institutionId, ra.getInstitutionId()),
        () -> assertEquals(patronIdentifier, ra.getPatronIdentifier()),
        () -> assertEquals(patronPassword, ra.getPatronPassword()),
        () -> assertEquals(terminalPassword, ra.getTerminalPassword()),
        () -> assertEquals(feeAcknowledged, ra.getFeeAcknowledged())
    );
  }

  @Test
  void testEqualsObject() {
    final RenewAll ra1 = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .patronPassword(patronPassword)
        .terminalPassword(terminalPassword)
        .feeAcknowledged(feeAcknowledged)
        .build();
    final RenewAll ra2 = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .patronPassword(patronPassword)
        .terminalPassword(terminalPassword)
        .feeAcknowledged(feeAcknowledged)
        .build();
    assertTrue(ra1.equals(ra2));
    assertTrue(ra1.equals(ra2));
  }

  @Test
  void testNotEqualsObject() {
    final RenewAll ra1 = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .patronPassword(patronPassword)
        .terminalPassword(terminalPassword)
        .feeAcknowledged(feeAcknowledged)
        .build();
    final RenewAll ra2 = builder()
        .transactionDate(transactionDate.minusDays(100))
        .institutionId("xyzzy")
        .patronIdentifier("111111111")
        .patronPassword("0000000000")
        .terminalPassword("88888888")
        .feeAcknowledged(FALSE)
        .build();
    assertFalse(ra1.equals(ra2));
    assertFalse(ra1.equals(ra2));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("RenewAll [transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", patronPassword=").append(patronPassword)
        .append(", terminalPassword=").append(terminalPassword)
        .append(", feeAcknowledged=").append(feeAcknowledged)
        .append(']').toString();
    final RenewAll ra = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .patronPassword(patronPassword)
        .terminalPassword(terminalPassword)
        .feeAcknowledged(feeAcknowledged)
        .build();
    assertEquals(expectedString, ra.toString());
  }
}
