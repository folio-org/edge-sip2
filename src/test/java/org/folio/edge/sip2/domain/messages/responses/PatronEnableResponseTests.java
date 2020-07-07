package org.folio.edge.sip2.domain.messages.responses;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static org.folio.edge.sip2.domain.messages.enumerations.Language.ENGLISH;
import static org.folio.edge.sip2.domain.messages.enumerations.Language.UNKNOWN;
import static org.folio.edge.sip2.domain.messages.enumerations.PatronStatus.CARD_REPORTED_LOST;
import static org.folio.edge.sip2.domain.messages.enumerations.PatronStatus.HOLD_PRIVILEGES_DENIED;
import static org.folio.edge.sip2.domain.messages.enumerations.PatronStatus.TOO_MANY_ITEMS_BILLED;
import static org.folio.edge.sip2.domain.messages.enumerations.PatronStatus.TOO_MANY_ITEMS_LOST;
import static org.folio.edge.sip2.domain.messages.enumerations.PatronStatus.TOO_MANY_RENEWALS;
import static org.folio.edge.sip2.domain.messages.responses.PatronEnableResponse.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.folio.edge.sip2.domain.messages.enumerations.Language;
import org.folio.edge.sip2.domain.messages.enumerations.PatronStatus;
import org.junit.jupiter.api.Test;

class PatronEnableResponseTests {
  final Set<PatronStatus> patronStatus =
      EnumSet.of(TOO_MANY_ITEMS_BILLED, HOLD_PRIVILEGES_DENIED);
  final Language language = ENGLISH;
  final OffsetDateTime transactionDate = OffsetDateTime.now();
  final String institutionId = "diku";
  final String patronIdentifier = "1234567890";
  final String personalName = "John Smith";
  final Boolean validPatron = TRUE;
  final Boolean validPatronPassword = TRUE;
  final List<String> screenMessage = asList("Hello, world!");
  final List<String> printLine = asList("Dot matrix");

  @Test
  void testGetPatronStatus() {
    final PatronEnableResponse per = builder()
        .patronStatus(patronStatus)
        .build();
    assertEquals(patronStatus, per.getPatronStatus());
    assertNull(per.getLanguage());
    assertNull(per.getTransactionDate());
    assertNull(per.getInstitutionId());
    assertNull(per.getPatronIdentifier());
    assertNull(per.getPersonalName());
    assertNull(per.getValidPatron());
    assertNull(per.getValidPatronPassword());
    assertNull(per.getScreenMessage());
    assertNull(per.getPrintLine());
  }

  @Test
  void testGetLanguage() {
    final PatronEnableResponse per = builder().language(language).build();
    assertTrue(per.getPatronStatus().isEmpty());
    assertEquals(language, per.getLanguage());
    assertNull(per.getTransactionDate());
    assertNull(per.getInstitutionId());
    assertNull(per.getPatronIdentifier());
    assertNull(per.getPersonalName());
    assertNull(per.getValidPatron());
    assertNull(per.getValidPatronPassword());
    assertNull(per.getScreenMessage());
    assertNull(per.getPrintLine());
  }

  @Test
  void testGetTransactionDate() {
    final PatronEnableResponse per = builder().transactionDate(transactionDate).build();
    assertTrue(per.getPatronStatus().isEmpty());
    assertNull(per.getLanguage());
    assertEquals(transactionDate, per.getTransactionDate());
    assertNull(per.getInstitutionId());
    assertNull(per.getPatronIdentifier());
    assertNull(per.getPersonalName());
    assertNull(per.getValidPatron());
    assertNull(per.getValidPatronPassword());
    assertNull(per.getScreenMessage());
    assertNull(per.getPrintLine());
  }

  @Test
  void testGetInstitutionId() {
    final PatronEnableResponse per = builder()
        .institutionId(institutionId)
        .build();
    assertTrue(per.getPatronStatus().isEmpty());
    assertNull(per.getLanguage());
    assertNull(per.getTransactionDate());
    assertEquals(institutionId, per.getInstitutionId());
    assertNull(per.getPatronIdentifier());
    assertNull(per.getPersonalName());
    assertNull(per.getValidPatron());
    assertNull(per.getValidPatronPassword());
    assertNull(per.getScreenMessage());
    assertNull(per.getPrintLine());
  }

  @Test
  void testGetPatronIdentifier() {
    final PatronEnableResponse per = builder()
        .patronIdentifier(patronIdentifier)
        .build();
    assertTrue(per.getPatronStatus().isEmpty());
    assertNull(per.getLanguage());
    assertNull(per.getTransactionDate());
    assertNull(per.getInstitutionId());
    assertEquals(patronIdentifier, per.getPatronIdentifier());
    assertNull(per.getPersonalName());
    assertNull(per.getValidPatron());
    assertNull(per.getValidPatronPassword());
    assertNull(per.getScreenMessage());
    assertNull(per.getPrintLine());
  }

  @Test
  void testGetPersonalName() {
    final PatronEnableResponse per = builder()
        .personalName(personalName)
        .build();
    assertTrue(per.getPatronStatus().isEmpty());
    assertNull(per.getLanguage());
    assertNull(per.getTransactionDate());
    assertNull(per.getInstitutionId());
    assertNull(per.getPatronIdentifier());
    assertEquals(personalName, per.getPersonalName());
    assertNull(per.getValidPatron());
    assertNull(per.getValidPatronPassword());
    assertNull(per.getScreenMessage());
    assertNull(per.getPrintLine());
  }

  @Test
  void testGetValidPatron() {
    final PatronEnableResponse per = builder()
        .validPatron(validPatron)
        .build();
    assertTrue(per.getPatronStatus().isEmpty());
    assertNull(per.getLanguage());
    assertNull(per.getTransactionDate());
    assertNull(per.getInstitutionId());
    assertNull(per.getPatronIdentifier());
    assertNull(per.getPersonalName());
    assertEquals(validPatron, per.getValidPatron());
    assertNull(per.getValidPatronPassword());
    assertNull(per.getScreenMessage());
    assertNull(per.getPrintLine());
  }

  @Test
  void testGetValidPatronPassword() {
    final PatronEnableResponse per = builder()
        .validPatronPassword(validPatronPassword)
        .build();
    assertTrue(per.getPatronStatus().isEmpty());
    assertNull(per.getLanguage());
    assertNull(per.getTransactionDate());
    assertNull(per.getInstitutionId());
    assertNull(per.getPatronIdentifier());
    assertNull(per.getPersonalName());
    assertNull(per.getValidPatron());
    assertEquals(validPatronPassword, per.getValidPatronPassword());
    assertNull(per.getScreenMessage());
    assertNull(per.getPrintLine());
  }

  @Test
  void testGetScreenMessage() {
    final PatronEnableResponse per = builder()
        .screenMessage(screenMessage)
        .build();
    assertTrue(per.getPatronStatus().isEmpty());
    assertNull(per.getLanguage());
    assertNull(per.getTransactionDate());
    assertNull(per.getInstitutionId());
    assertNull(per.getPatronIdentifier());
    assertNull(per.getPersonalName());
    assertNull(per.getValidPatron());
    assertNull(per.getValidPatronPassword());
    assertEquals(screenMessage, per.getScreenMessage());
    assertNull(per.getPrintLine());
  }

  @Test
  void testGetPrintLine() {
    final PatronEnableResponse per = builder()
        .printLine(printLine)
        .build();
    assertTrue(per.getPatronStatus().isEmpty());
    assertNull(per.getLanguage());
    assertNull(per.getTransactionDate());
    assertNull(per.getInstitutionId());
    assertNull(per.getPatronIdentifier());
    assertNull(per.getPersonalName());
    assertNull(per.getValidPatron());
    assertNull(per.getValidPatronPassword());
    assertNull(per.getScreenMessage());
    assertEquals(printLine, per.getPrintLine());
  }

  @Test
  void testCompletePatronEnableResponse() {
    final PatronEnableResponse per = builder()
        .patronStatus(patronStatus)
        .language(language)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .personalName(personalName)
        .validPatron(validPatron)
        .validPatronPassword(validPatronPassword)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertAll("PatronEnableResponse",
        () -> assertEquals(patronStatus, per.getPatronStatus()),
        () -> assertEquals(language, per.getLanguage()),
        () -> assertEquals(transactionDate, per.getTransactionDate()),
        () -> assertEquals(institutionId, per.getInstitutionId()),
        () -> assertEquals(patronIdentifier, per.getPatronIdentifier()),
        () -> assertEquals(personalName, per.getPersonalName()),
        () -> assertEquals(validPatron, per.getValidPatron()),
        () -> assertEquals(validPatronPassword, per.getValidPatronPassword()),
        () -> assertEquals(screenMessage, per.getScreenMessage()),
        () -> assertEquals(printLine, per.getPrintLine())
    );
  }

  @Test
  void testEquals() {
    final PatronEnableResponse per1 = builder()
        .patronStatus(patronStatus)
        .language(language)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .personalName(personalName)
        .validPatron(validPatron)
        .validPatronPassword(validPatronPassword)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    final PatronEnableResponse per2 = builder()
        .patronStatus(patronStatus)
        .language(language)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .personalName(personalName)
        .validPatron(validPatron)
        .validPatronPassword(validPatronPassword)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertTrue(per1.equals(per2));
    assertTrue(per2.equals(per1));
  }

  @Test
  void testNotEquals() {
    final PatronEnableResponse per1 = builder()
        .patronStatus(patronStatus)
        .language(language)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .personalName(personalName)
        .validPatron(validPatron)
        .validPatronPassword(validPatronPassword)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    final PatronEnableResponse per2 = builder()
        .patronStatus(EnumSet.of(
            CARD_REPORTED_LOST,
            TOO_MANY_ITEMS_LOST,
            TOO_MANY_RENEWALS))
        .language(UNKNOWN)
        .transactionDate(OffsetDateTime.now())
        .institutionId("test")
        .patronIdentifier("0987654321")
        .personalName("Jane Doe")
        .validPatron(FALSE)
        .validPatronPassword(FALSE)
        .screenMessage(asList("Welcome to the jungle."))
        .printLine(asList("Print print print"))
        .build();
    assertFalse(per1.equals(per2));
    assertFalse(per2.equals(per1));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("PatronEnableResponse [patronStatus=").append(patronStatus)
        .append(", language=").append(language)
        .append(", transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", personalName=").append(personalName)
        .append(", validPatron=").append(validPatron)
        .append(", validPatronPassword=").append(validPatronPassword)
        .append(", screenMessage=").append(screenMessage)
        .append(", printLine=").append(printLine)
        .append(']').toString();
    final PatronEnableResponse per = builder()
        .patronStatus(patronStatus)
        .language(language)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .personalName(personalName)
        .validPatron(validPatron)
        .validPatronPassword(validPatronPassword)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertEquals(expectedString, per.toString());
  }
}
