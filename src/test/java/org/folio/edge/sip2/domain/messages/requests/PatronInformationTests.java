package org.folio.edge.sip2.domain.messages.requests;

import static org.folio.edge.sip2.domain.messages.enumerations.Language.ENGLISH;
import static org.folio.edge.sip2.domain.messages.enumerations.Language.UNKNOWN;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.FINE_ITEMS;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.RECALL_ITEMS;
import static org.folio.edge.sip2.domain.messages.requests.PatronInformation.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import org.folio.edge.sip2.domain.messages.enumerations.Language;
import org.folio.edge.sip2.domain.messages.enumerations.Summary;
import org.junit.jupiter.api.Test;

class PatronInformationTests {
  final Language language = ENGLISH;
  final OffsetDateTime transactionDate = OffsetDateTime.now();
  final Summary summary = FINE_ITEMS;
  final String institutionId = "diku";
  final String patronIdentifier = "1234567890";
  final String terminalPassword = "12345";
  final String patronPassword = "2112";
  final Integer startItem = Integer.valueOf(1);
  final Integer endItem = Integer.valueOf(10);

  @Test
  void testGetLanguage() {
    final PatronInformation pi = builder().language(language).build();
    assertEquals(language, pi.getLanguage());
    assertNull(pi.getTransactionDate());
    assertNull(pi.getSummary());
    assertNull(pi.getInstitutionId());
    assertNull(pi.getPatronIdentifier());
    assertNull(pi.getTerminalPassword());
    assertNull(pi.getPatronPassword());
    assertNull(pi.getStartItem());
    assertNull(pi.getEndItem());
  }

  @Test
  void testGetTransactionDate() {
    final PatronInformation pi = builder()
        .transactionDate(transactionDate)
        .build();
    assertNull(pi.getLanguage());
    assertEquals(transactionDate, pi.getTransactionDate());
    assertNull(pi.getSummary());
    assertNull(pi.getInstitutionId());
    assertNull(pi.getPatronIdentifier());
    assertNull(pi.getTerminalPassword());
    assertNull(pi.getPatronPassword());
    assertNull(pi.getStartItem());
    assertNull(pi.getEndItem());
  }

  @Test
  void testGetSummary() {
    final PatronInformation pi = builder()
        .summary(summary)
        .build();
    assertNull(pi.getLanguage());
    assertNull(pi.getTransactionDate());
    assertEquals(summary, pi.getSummary());
    assertNull(pi.getInstitutionId());
    assertNull(pi.getPatronIdentifier());
    assertNull(pi.getTerminalPassword());
    assertNull(pi.getPatronPassword());
    assertNull(pi.getStartItem());
    assertNull(pi.getEndItem());
  }

  @Test
  void testGetInstitutionId() {
    final PatronInformation pi = builder()
        .institutionId(institutionId)
        .build();
    assertNull(pi.getLanguage());
    assertNull(pi.getTransactionDate());
    assertNull(pi.getSummary());
    assertEquals(institutionId, pi.getInstitutionId());
    assertNull(pi.getPatronIdentifier());
    assertNull(pi.getTerminalPassword());
    assertNull(pi.getPatronPassword());
    assertNull(pi.getStartItem());
    assertNull(pi.getEndItem());
  }

  @Test
  void testGetPatronIdentifier() {
    final PatronInformation pi = builder()
        .patronIdentifier(patronIdentifier)
        .build();
    assertNull(pi.getLanguage());
    assertNull(pi.getTransactionDate());
    assertNull(pi.getSummary());
    assertNull(pi.getInstitutionId());
    assertEquals(patronIdentifier, pi.getPatronIdentifier());
    assertNull(pi.getTerminalPassword());
    assertNull(pi.getPatronPassword());
    assertNull(pi.getStartItem());
    assertNull(pi.getEndItem());
  }

  @Test
  void testGetTerminalPassword() {
    final PatronInformation pi = builder()
        .terminalPassword(terminalPassword)
        .build();
    assertNull(pi.getLanguage());
    assertNull(pi.getTransactionDate());
    assertNull(pi.getSummary());
    assertNull(pi.getInstitutionId());
    assertNull(pi.getPatronIdentifier());
    assertEquals(terminalPassword, pi.getTerminalPassword());
    assertNull(pi.getPatronPassword());
    assertNull(pi.getStartItem());
    assertNull(pi.getEndItem());
  }

  @Test
  void testGetPatronPassword() {
    final PatronInformation pi = builder()
        .patronPassword(patronPassword)
        .build();
    assertNull(pi.getLanguage());
    assertNull(pi.getTransactionDate());
    assertNull(pi.getSummary());
    assertNull(pi.getInstitutionId());
    assertNull(pi.getPatronIdentifier());
    assertNull(pi.getTerminalPassword());
    assertEquals(patronPassword, pi.getPatronPassword());
    assertNull(pi.getStartItem());
    assertNull(pi.getEndItem());
  }

  @Test
  void testGetStartItem() {
    final PatronInformation pi = builder().startItem(startItem).build();
    assertNull(pi.getLanguage());
    assertNull(pi.getTransactionDate());
    assertNull(pi.getSummary());
    assertNull(pi.getInstitutionId());
    assertNull(pi.getPatronIdentifier());
    assertNull(pi.getTerminalPassword());
    assertNull(pi.getPatronPassword());
    assertEquals(startItem, pi.getStartItem());
    assertNull(pi.getEndItem());
  }

  @Test
  void testGetEndItem() {
    final PatronInformation pi = builder().endItem(endItem).build();
    assertNull(pi.getLanguage());
    assertNull(pi.getTransactionDate());
    assertNull(pi.getSummary());
    assertNull(pi.getInstitutionId());
    assertNull(pi.getPatronIdentifier());
    assertNull(pi.getTerminalPassword());
    assertNull(pi.getPatronPassword());
    assertNull(pi.getStartItem());
    assertEquals(endItem, pi.getEndItem());
  }

  @Test
  void testCompletePatronInformation() {
    final PatronInformation pi = builder()
        .language(language)
        .transactionDate(transactionDate)
        .summary(summary)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .startItem(startItem)
        .endItem(endItem)
        .build();
    assertAll("PatronInformation",
        () -> assertEquals(language, pi.getLanguage()),
        () -> assertEquals(transactionDate, pi.getTransactionDate()),
        () -> assertEquals(summary, pi.getSummary()),
        () -> assertEquals(institutionId, pi.getInstitutionId()),
        () -> assertEquals(patronIdentifier, pi.getPatronIdentifier()),
        () -> assertEquals(terminalPassword, pi.getTerminalPassword()),
        () -> assertEquals(patronPassword, pi.getPatronPassword()),
        () -> assertEquals(startItem, pi.getStartItem()),
        () -> assertEquals(endItem, pi.getEndItem())
    );
  }

  @Test
  void testEquals() {
    final PatronInformation pi1 = builder()
        .language(language)
        .transactionDate(transactionDate)
        .summary(summary)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .startItem(startItem)
        .endItem(endItem)
        .build();
    final PatronInformation pi2 = builder()
        .language(language)
        .transactionDate(transactionDate)
        .summary(summary)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .startItem(startItem)
        .endItem(endItem)
        .build();
    assertTrue(pi1.equals(pi2));
    assertTrue(pi2.equals(pi1));
  }

  @Test
  void testNotEquals() {
    final PatronInformation pi1 = builder()
        .language(language)
        .transactionDate(transactionDate)
        .summary(summary)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .startItem(startItem)
        .endItem(endItem)
        .build();
    final PatronInformation pi2 = builder()
        .language(UNKNOWN)
        .transactionDate(OffsetDateTime.now())
        .summary(RECALL_ITEMS)
        .institutionId("test")
        .patronIdentifier("0987654321")
        .terminalPassword("0000")
        .patronPassword("9999")
        .startItem(Integer.valueOf(20))
        .endItem(Integer.valueOf(100))
        .build();
    assertFalse(pi1.equals(pi2));
    assertFalse(pi2.equals(pi1));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("PatronInformation [language=").append(language)
        .append(", transactionDate=").append(transactionDate)
        .append(", summary=").append(summary)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", terminalPassword=").append(terminalPassword)
        .append(", patronPassword=").append(patronPassword)
        .append(", startItem=").append(startItem)
        .append(", endItem=").append(endItem)
        .append(']').toString();
    final PatronInformation pi = builder()
        .language(language)
        .transactionDate(transactionDate)
        .summary(summary)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .startItem(startItem)
        .endItem(endItem)
        .build();
    assertEquals(expectedString, pi.toString());
    assertNotNull(pi.getPatronLogInfo());
  }
}
