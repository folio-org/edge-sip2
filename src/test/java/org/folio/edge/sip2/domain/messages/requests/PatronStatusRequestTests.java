package org.folio.edge.sip2.domain.messages.requests;

import static org.folio.edge.sip2.domain.messages.enumerations.Language.ENGLISH;
import static org.folio.edge.sip2.domain.messages.enumerations.Language.UNKNOWN;
import static org.folio.edge.sip2.domain.messages.requests.PatronStatusRequest.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import org.folio.edge.sip2.domain.messages.enumerations.Language;
import org.junit.jupiter.api.Test;

class PatronStatusRequestTests {
  final Language language = ENGLISH;
  final OffsetDateTime transactionDate = OffsetDateTime.now();
  final String institutionId = "diku";
  final String patronIdentifier = "1234567890";
  final String terminalPassword = "12345";
  final String patronPassword = "2112";

  @Test
  void testGetLanguage() {
    final PatronStatusRequest psr = builder().language(language).build();
    assertEquals(language, psr.getLanguage());
    assertNull(psr.getTransactionDate());
    assertNull(psr.getInstitutionId());
    assertNull(psr.getPatronIdentifier());
    assertNull(psr.getTerminalPassword());
    assertNull(psr.getPatronPassword());
  }

  @Test
  void testGetTransactionDate() {
    final PatronStatusRequest psr = builder().transactionDate(transactionDate).build();
    assertNull(psr.getLanguage());
    assertEquals(transactionDate, psr.getTransactionDate());
    assertNull(psr.getInstitutionId());
    assertNull(psr.getPatronIdentifier());
    assertNull(psr.getTerminalPassword());
    assertNull(psr.getPatronPassword());
  }

  @Test
  void testGetInstitutionId() {
    final PatronStatusRequest psr = builder()
        .institutionId(institutionId)
        .build();
    assertNull(psr.getLanguage());
    assertNull(psr.getTransactionDate());
    assertEquals(institutionId, psr.getInstitutionId());
    assertNull(psr.getPatronIdentifier());
    assertNull(psr.getTerminalPassword());
    assertNull(psr.getPatronPassword());
  }

  @Test
  void testGetPatronIdentifier() {
    final PatronStatusRequest psr = builder()
        .patronIdentifier(patronIdentifier)
        .build();
    assertNull(psr.getLanguage());
    assertNull(psr.getTransactionDate());
    assertNull(psr.getInstitutionId());
    assertEquals(patronIdentifier, psr.getPatronIdentifier());
    assertNull(psr.getTerminalPassword());
    assertNull(psr.getPatronPassword());
  }

  @Test
  void testGetTerminalPassword() {
    final PatronStatusRequest psr = builder()
        .terminalPassword(terminalPassword)
        .build();
    assertNull(psr.getLanguage());
    assertNull(psr.getTransactionDate());
    assertNull(psr.getInstitutionId());
    assertNull(psr.getPatronIdentifier());
    assertEquals(terminalPassword, psr.getTerminalPassword());
    assertNull(psr.getPatronPassword());
  }

  @Test
  void testGetPatronPassword() {
    final PatronStatusRequest psr = builder()
        .patronPassword(patronPassword)
        .build();
    assertNull(psr.getLanguage());
    assertNull(psr.getTransactionDate());
    assertNull(psr.getInstitutionId());
    assertNull(psr.getPatronIdentifier());
    assertNull(psr.getTerminalPassword());
    assertEquals(patronPassword, psr.getPatronPassword());
  }

  @Test
  void testCompletePatronStatusRequest() {
    final PatronStatusRequest psr = builder()
        .language(language)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .build();
    assertAll("PatronStatusRequest",
        () -> assertEquals(language, psr.getLanguage()),
        () -> assertEquals(transactionDate, psr.getTransactionDate()),
        () -> assertEquals(institutionId, psr.getInstitutionId()),
        () -> assertEquals(patronIdentifier, psr.getPatronIdentifier()),
        () -> assertEquals(terminalPassword, psr.getTerminalPassword()),
        () -> assertEquals(patronPassword, psr.getPatronPassword())
    );
  }

  @Test
  void testEquals() {
    final PatronStatusRequest psr1 = builder()
        .language(language)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .build();
    final PatronStatusRequest psr2 = builder()
        .language(language)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .build();
    assertTrue(psr1.equals(psr2));
    assertTrue(psr2.equals(psr1));
  }

  @Test
  void testNotEquals() {
    final PatronStatusRequest psr1 = builder()
        .language(language)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .build();
    final PatronStatusRequest psr2 = builder()
        .language(UNKNOWN)
        .transactionDate(OffsetDateTime.now())
        .institutionId("test")
        .patronIdentifier("0987654321")
        .terminalPassword("0000")
        .patronPassword("9999")
        .build();
    assertFalse(psr1.equals(psr2));
    assertFalse(psr2.equals(psr1));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("PatronStatusRequest [language=").append(language)
        .append(", transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", terminalPassword=").append(terminalPassword)
        .append(", patronPassword=").append(patronPassword)
        .append(']').toString();
    final PatronStatusRequest psr = builder()
        .language(language)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .build();
    assertEquals(expectedString, psr.toString());
  }
}
