package org.folio.edge.sip2.domain.messages.requests;

import static org.folio.edge.sip2.domain.messages.requests.EndPatronSession.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class EndPatronSessionTests {
  final OffsetDateTime transactionDate = OffsetDateTime.now();
  final String institutionId = "diku";
  final String patronIdentifier = "1234567890";
  final String terminalPassword = "12345";
  final String patronPassword = "2112";

  @Test
  void testGetTransactionDate() {
    final EndPatronSession eps = builder()
        .transactionDate(transactionDate)
        .build();
    assertEquals(transactionDate, eps.getTransactionDate());
    assertNull(eps.getInstitutionId());
    assertNull(eps.getPatronIdentifier());
    assertNull(eps.getTerminalPassword());
    assertNull(eps.getPatronPassword());
  }

  @Test
  void testGetInstitutionId() {
    final EndPatronSession eps = builder()
        .institutionId(institutionId)
        .build();
    assertNull(eps.getTransactionDate());
    assertEquals(institutionId, eps.getInstitutionId());
    assertNull(eps.getPatronIdentifier());
    assertNull(eps.getTerminalPassword());
    assertNull(eps.getPatronPassword());
  }

  @Test
  void testGetPatronIdentifier() {
    final EndPatronSession eps = builder()
        .patronIdentifier(patronIdentifier)
        .build();
    assertNull(eps.getTransactionDate());
    assertNull(eps.getInstitutionId());
    assertEquals(patronIdentifier, eps.getPatronIdentifier());
    assertNull(eps.getTerminalPassword());
    assertNull(eps.getPatronPassword());
  }

  @Test
  void testGetTerminalPassword() {
    final EndPatronSession eps = builder()
        .terminalPassword(terminalPassword)
        .build();
    assertNull(eps.getTransactionDate());
    assertNull(eps.getInstitutionId());
    assertNull(eps.getPatronIdentifier());
    assertEquals(terminalPassword, eps.getTerminalPassword());
    assertNull(eps.getPatronPassword());
  }

  @Test
  void testGetPatronPassword() {
    final EndPatronSession eps = builder()
        .patronPassword(patronPassword)
        .build();
    assertNull(eps.getTransactionDate());
    assertNull(eps.getInstitutionId());
    assertNull(eps.getPatronIdentifier());
    assertNull(eps.getTerminalPassword());
    assertEquals(patronPassword, eps.getPatronPassword());
  }

  @Test
  void testCompleteEndPatronSession() {
    final EndPatronSession eps = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .build();
    assertAll("EndPatronSession",
        () -> assertEquals(transactionDate, eps.getTransactionDate()),
        () -> assertEquals(institutionId, eps.getInstitutionId()),
        () -> assertEquals(patronIdentifier, eps.getPatronIdentifier()),
        () -> assertEquals(terminalPassword, eps.getTerminalPassword()),
        () -> assertEquals(patronPassword, eps.getPatronPassword())
    );
  }

  @Test
  void testEquals() {
    final EndPatronSession eps1 = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .build();
    final EndPatronSession eps2 = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .build();
    assertTrue(eps1.equals(eps2));
    assertTrue(eps2.equals(eps1));
  }

  @Test
  void testNotEquals() {
    final EndPatronSession eps1 = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .build();
    final EndPatronSession eps2 = builder()
        .transactionDate(OffsetDateTime.now())
        .institutionId("test")
        .patronIdentifier("0987654321")
        .terminalPassword("0000")
        .patronPassword("9999")
        .build();
    assertFalse(eps1.equals(eps2));
    assertFalse(eps2.equals(eps1));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("EndPatronSession [transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", terminalPassword=").append(terminalPassword)
        .append(", patronPassword=").append(patronPassword)
        .append(']')
        .toString();
    final EndPatronSession eps = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .build();
    assertEquals(expectedString, eps.toString());
  }
}
