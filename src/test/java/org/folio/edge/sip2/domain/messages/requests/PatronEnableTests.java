package org.folio.edge.sip2.domain.messages.requests;

import static org.folio.edge.sip2.domain.messages.requests.PatronEnable.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

class PatronEnableTests {
  final ZonedDateTime transactionDate = ZonedDateTime.now();
  final String institutionId = "diku";
  final String patronIdentifier = "1234567890";
  final String terminalPassword = "12345";
  final String patronPassword = "2112";

  @Test
  void testGetTransactionDate() {
    final PatronEnable pe = builder().transactionDate(transactionDate).build();
    assertEquals(transactionDate, pe.getTransactionDate());
    assertNull(pe.getInstitutionId());
    assertNull(pe.getPatronIdentifier());
    assertNull(pe.getTerminalPassword());
    assertNull(pe.getPatronPassword());
  }

  @Test
  void testGetInstitutionId() {
    final PatronEnable pe = builder()
        .institutionId(institutionId)
        .build();
    assertNull(pe.getTransactionDate());
    assertEquals(institutionId, pe.getInstitutionId());
    assertNull(pe.getPatronIdentifier());
    assertNull(pe.getTerminalPassword());
    assertNull(pe.getPatronPassword());
  }

  @Test
  void testGetPatronIdentifier() {
    final PatronEnable pe = builder()
        .patronIdentifier(patronIdentifier)
        .build();
    assertNull(pe.getTransactionDate());
    assertNull(pe.getInstitutionId());
    assertEquals(patronIdentifier, pe.getPatronIdentifier());
    assertNull(pe.getTerminalPassword());
    assertNull(pe.getPatronPassword());
  }

  @Test
  void testGetTerminalPassword() {
    final PatronEnable pe = builder()
        .terminalPassword(terminalPassword)
        .build();
    assertNull(pe.getTransactionDate());
    assertNull(pe.getInstitutionId());
    assertNull(pe.getPatronIdentifier());
    assertEquals(terminalPassword, pe.getTerminalPassword());
    assertNull(pe.getPatronPassword());
  }

  @Test
  void testGetPatronPassword() {
    final PatronEnable pe = builder()
        .patronPassword(patronPassword)
        .build();
    assertNull(pe.getTransactionDate());
    assertNull(pe.getInstitutionId());
    assertNull(pe.getPatronIdentifier());
    assertNull(pe.getTerminalPassword());
    assertEquals(patronPassword, pe.getPatronPassword());
  }

  @Test
  void testCompletePatronEnable() {
    final PatronEnable pe = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .build();
    assertAll("PatronEnable",
        () -> assertEquals(transactionDate, pe.getTransactionDate()),
        () -> assertEquals(institutionId, pe.getInstitutionId()),
        () -> assertEquals(patronIdentifier, pe.getPatronIdentifier()),
        () -> assertEquals(terminalPassword, pe.getTerminalPassword()),
        () -> assertEquals(patronPassword, pe.getPatronPassword())
    );
  }

  @Test
  void testEquals() {
    final PatronEnable pe1 = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .build();
    final PatronEnable pe2 = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .build();
    assertTrue(pe1.equals(pe2));
    assertTrue(pe2.equals(pe1));
  }

  @Test
  void testNotEquals() {
    final PatronEnable pe1 = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .build();
    final PatronEnable pe2 = builder()
        .transactionDate(ZonedDateTime.now())
        .institutionId("test")
        .patronIdentifier("0987654321")
        .terminalPassword("0000")
        .patronPassword("9999")
        .build();
    assertFalse(pe1.equals(pe2));
    assertFalse(pe2.equals(pe1));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("PatronEnable [transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", terminalPassword=").append(terminalPassword)
        .append(", patronPassword=").append(patronPassword)
        .append(']').toString();
    final PatronEnable pe = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .build();
    assertEquals(expectedString, pe.toString());
  }
}
