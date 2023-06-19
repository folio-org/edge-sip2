package org.folio.edge.sip2.domain.messages.responses;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static org.folio.edge.sip2.domain.messages.enumerations.CurrencyType.GBP;
import static org.folio.edge.sip2.domain.messages.enumerations.CurrencyType.USD;
import static org.folio.edge.sip2.domain.messages.enumerations.Language.ENGLISH;
import static org.folio.edge.sip2.domain.messages.enumerations.Language.UNKNOWN;
import static org.folio.edge.sip2.domain.messages.enumerations.PatronStatus.CARD_REPORTED_LOST;
import static org.folio.edge.sip2.domain.messages.enumerations.PatronStatus.HOLD_PRIVILEGES_DENIED;
import static org.folio.edge.sip2.domain.messages.enumerations.PatronStatus.TOO_MANY_ITEMS_BILLED;
import static org.folio.edge.sip2.domain.messages.enumerations.PatronStatus.TOO_MANY_ITEMS_LOST;
import static org.folio.edge.sip2.domain.messages.enumerations.PatronStatus.TOO_MANY_RENEWALS;
import static org.folio.edge.sip2.domain.messages.responses.PatronStatusResponse.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.folio.edge.sip2.domain.messages.enumerations.CurrencyType;
import org.folio.edge.sip2.domain.messages.enumerations.Language;
import org.folio.edge.sip2.domain.messages.enumerations.PatronStatus;
import org.junit.jupiter.api.Test;

class PatronStatusResponseTests {
  final Set<PatronStatus> patronStatus =
      EnumSet.of(TOO_MANY_ITEMS_BILLED, HOLD_PRIVILEGES_DENIED);
  final Language language = ENGLISH;
  final OffsetDateTime transactionDate = OffsetDateTime.now();
  final String institutionId = "diku";
  final String patronIdentifier = "1234567890";
  final String personalName = "John Smith";
  final Boolean validPatron = TRUE;
  final Boolean validPatronPassword = TRUE;
  final CurrencyType currencyType = USD;
  final String feeAmount = "25.00";
  final List<String> screenMessage = asList("Hello, world!");
  final List<String> printLine = asList("Dot matrix");

  @Test
  void testGetPatronStatus() {
    final PatronStatusResponse psr = builder()
        .patronStatus(patronStatus)
        .build();
    assertEquals(patronStatus, psr.getPatronStatus());
    assertNull(psr.getLanguage());
    assertNull(psr.getTransactionDate());
    assertNull(psr.getInstitutionId());
    assertNull(psr.getPatronIdentifier());
    assertNull(psr.getPersonalName());
    assertNull(psr.getValidPatron());
    assertNull(psr.getValidPatronPassword());
    assertNull(psr.getCurrencyType());
    assertNull(psr.getFeeAmount());
    assertNull(psr.getScreenMessage());
    assertNull(psr.getPrintLine());
  }

  @Test
  void testGetLanguage() {
    final PatronStatusResponse psr = builder().language(language).build();
    assertTrue(psr.getPatronStatus().isEmpty());
    assertEquals(language, psr.getLanguage());
    assertNull(psr.getTransactionDate());
    assertNull(psr.getInstitutionId());
    assertNull(psr.getPatronIdentifier());
    assertNull(psr.getPersonalName());
    assertNull(psr.getValidPatron());
    assertNull(psr.getValidPatronPassword());
    assertNull(psr.getCurrencyType());
    assertNull(psr.getFeeAmount());
    assertNull(psr.getScreenMessage());
    assertNull(psr.getPrintLine());
  }

  @Test
  void testGetTransactionDate() {
    final PatronStatusResponse psr = builder().transactionDate(transactionDate).build();
    assertTrue(psr.getPatronStatus().isEmpty());
    assertNull(psr.getLanguage());
    assertEquals(transactionDate, psr.getTransactionDate());
    assertNull(psr.getInstitutionId());
    assertNull(psr.getPatronIdentifier());
    assertNull(psr.getPersonalName());
    assertNull(psr.getValidPatron());
    assertNull(psr.getValidPatronPassword());
    assertNull(psr.getCurrencyType());
    assertNull(psr.getFeeAmount());
    assertNull(psr.getScreenMessage());
    assertNull(psr.getPrintLine());
  }

  @Test
  void testGetInstitutionId() {
    final PatronStatusResponse psr = builder()
        .institutionId(institutionId)
        .build();
    assertTrue(psr.getPatronStatus().isEmpty());
    assertNull(psr.getLanguage());
    assertNull(psr.getTransactionDate());
    assertEquals(institutionId, psr.getInstitutionId());
    assertNull(psr.getPatronIdentifier());
    assertNull(psr.getPersonalName());
    assertNull(psr.getValidPatron());
    assertNull(psr.getValidPatronPassword());
    assertNull(psr.getCurrencyType());
    assertNull(psr.getFeeAmount());
    assertNull(psr.getScreenMessage());
    assertNull(psr.getPrintLine());
  }

  @Test
  void testGetPatronIdentifier() {
    final PatronStatusResponse psr = builder()
        .patronIdentifier(patronIdentifier)
        .build();
    assertTrue(psr.getPatronStatus().isEmpty());
    assertNull(psr.getLanguage());
    assertNull(psr.getTransactionDate());
    assertNull(psr.getInstitutionId());
    assertEquals(patronIdentifier, psr.getPatronIdentifier());
    assertNull(psr.getPersonalName());
    assertNull(psr.getValidPatron());
    assertNull(psr.getValidPatronPassword());
    assertNull(psr.getCurrencyType());
    assertNull(psr.getFeeAmount());
    assertNull(psr.getScreenMessage());
    assertNull(psr.getPrintLine());
  }

  @Test
  void testGetPersonalName() {
    final PatronStatusResponse psr = builder()
        .personalName(personalName)
        .build();
    assertTrue(psr.getPatronStatus().isEmpty());
    assertNull(psr.getLanguage());
    assertNull(psr.getTransactionDate());
    assertNull(psr.getInstitutionId());
    assertNull(psr.getPatronIdentifier());
    assertEquals(personalName, psr.getPersonalName());
    assertNull(psr.getValidPatron());
    assertNull(psr.getValidPatronPassword());
    assertNull(psr.getCurrencyType());
    assertNull(psr.getFeeAmount());
    assertNull(psr.getScreenMessage());
    assertNull(psr.getPrintLine());
  }

  @Test
  void testGetValidPatron() {
    final PatronStatusResponse psr = builder()
        .validPatron(validPatron)
        .build();
    assertTrue(psr.getPatronStatus().isEmpty());
    assertNull(psr.getLanguage());
    assertNull(psr.getTransactionDate());
    assertNull(psr.getInstitutionId());
    assertNull(psr.getPatronIdentifier());
    assertNull(psr.getPersonalName());
    assertEquals(validPatron, psr.getValidPatron());
    assertNull(psr.getValidPatronPassword());
    assertNull(psr.getCurrencyType());
    assertNull(psr.getFeeAmount());
    assertNull(psr.getScreenMessage());
    assertNull(psr.getPrintLine());
  }

  @Test
  void testGetValidPatronPassword() {
    final PatronStatusResponse psr = builder()
        .validPatronPassword(validPatronPassword)
        .build();
    assertTrue(psr.getPatronStatus().isEmpty());
    assertNull(psr.getLanguage());
    assertNull(psr.getTransactionDate());
    assertNull(psr.getInstitutionId());
    assertNull(psr.getPatronIdentifier());
    assertNull(psr.getPersonalName());
    assertNull(psr.getValidPatron());
    assertEquals(validPatronPassword, psr.getValidPatronPassword());
    assertNull(psr.getCurrencyType());
    assertNull(psr.getFeeAmount());
    assertNull(psr.getScreenMessage());
    assertNull(psr.getPrintLine());
  }

  @Test
  void testGetCurrencyType() {
    final PatronStatusResponse psr = builder()
        .currencyType(currencyType)
        .build();
    assertTrue(psr.getPatronStatus().isEmpty());
    assertNull(psr.getLanguage());
    assertNull(psr.getTransactionDate());
    assertNull(psr.getInstitutionId());
    assertNull(psr.getPatronIdentifier());
    assertNull(psr.getPersonalName());
    assertNull(psr.getValidPatron());
    assertNull(psr.getValidPatronPassword());
    assertEquals(currencyType, psr.getCurrencyType());
    assertNull(psr.getFeeAmount());
    assertNull(psr.getScreenMessage());
    assertNull(psr.getPrintLine());
  }

  @Test
  void testGetFeeAmount() {
    final PatronStatusResponse psr = builder().feeAmount(feeAmount).build();
    assertTrue(psr.getPatronStatus().isEmpty());
    assertNull(psr.getLanguage());
    assertNull(psr.getTransactionDate());
    assertNull(psr.getInstitutionId());
    assertNull(psr.getPatronIdentifier());
    assertNull(psr.getPersonalName());
    assertNull(psr.getValidPatron());
    assertNull(psr.getValidPatronPassword());
    assertNull(psr.getCurrencyType());
    assertEquals(feeAmount, psr.getFeeAmount());
    assertNull(psr.getScreenMessage());
    assertNull(psr.getPrintLine());
  }

  @Test
  void testGetScreenMessage() {
    final PatronStatusResponse psr = builder()
        .screenMessage(screenMessage)
        .build();
    assertTrue(psr.getPatronStatus().isEmpty());
    assertNull(psr.getLanguage());
    assertNull(psr.getTransactionDate());
    assertNull(psr.getInstitutionId());
    assertNull(psr.getPatronIdentifier());
    assertNull(psr.getPersonalName());
    assertNull(psr.getValidPatron());
    assertNull(psr.getValidPatronPassword());
    assertNull(psr.getCurrencyType());
    assertNull(psr.getFeeAmount());
    assertEquals(screenMessage, psr.getScreenMessage());
    assertNull(psr.getPrintLine());
  }

  @Test
  void testGetPrintLine() {
    final PatronStatusResponse psr = builder()
        .printLine(printLine)
        .build();
    assertTrue(psr.getPatronStatus().isEmpty());
    assertNull(psr.getLanguage());
    assertNull(psr.getTransactionDate());
    assertNull(psr.getInstitutionId());
    assertNull(psr.getPatronIdentifier());
    assertNull(psr.getPersonalName());
    assertNull(psr.getValidPatron());
    assertNull(psr.getValidPatronPassword());
    assertNull(psr.getCurrencyType());
    assertNull(psr.getFeeAmount());
    assertNull(psr.getScreenMessage());
    assertEquals(printLine, psr.getPrintLine());
  }





  @Test
  void testCompletePatronStatusResponse() {
    final PatronStatusResponse psr = builder()
        .patronStatus(patronStatus)
        .language(language)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .personalName(personalName)
        .validPatron(validPatron)
        .validPatronPassword(validPatronPassword)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertAll("PatronStatusResponse",
        () -> assertEquals(patronStatus, psr.getPatronStatus()),
        () -> assertEquals(language, psr.getLanguage()),
        () -> assertEquals(transactionDate, psr.getTransactionDate()),
        () -> assertEquals(institutionId, psr.getInstitutionId()),
        () -> assertEquals(patronIdentifier, psr.getPatronIdentifier()),
        () -> assertEquals(personalName, psr.getPersonalName()),
        () -> assertEquals(validPatron, psr.getValidPatron()),
        () -> assertEquals(validPatronPassword, psr.getValidPatronPassword()),
        () -> assertEquals(currencyType, psr.getCurrencyType()),
        () -> assertEquals(feeAmount, psr.getFeeAmount()),
        () -> assertEquals(screenMessage, psr.getScreenMessage()),
        () -> assertEquals(printLine, psr.getPrintLine())
    );
  }

  @Test
  void testEquals() {
    final PatronStatusResponse psr1 = builder()
        .patronStatus(patronStatus)
        .language(language)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .personalName(personalName)
        .validPatron(validPatron)
        .validPatronPassword(validPatronPassword)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    final PatronStatusResponse psr2 = builder()
        .patronStatus(patronStatus)
        .language(language)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .personalName(personalName)
        .validPatron(validPatron)
        .validPatronPassword(validPatronPassword)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertTrue(psr1.equals(psr2));
    assertTrue(psr2.equals(psr1));
  }

  @Test
  void testNotEquals() {
    final PatronStatusResponse psr1 = builder()
        .patronStatus(patronStatus)
        .language(language)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .personalName(personalName)
        .validPatron(validPatron)
        .validPatronPassword(validPatronPassword)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    final PatronStatusResponse psr2 = builder()
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
        .currencyType(GBP)
        .feeAmount("10.00")
        .screenMessage(asList("Welcome to the jungle."))
        .printLine(asList("Print print print"))
        .build();
    assertFalse(psr1.equals(psr2));
    assertFalse(psr2.equals(psr1));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("PatronStatusResponse [patronStatus=").append(patronStatus)
        .append(", language=").append(language)
        .append(", transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", personalName=").append(personalName)
        .append(", validPatron=").append(validPatron)
        .append(", validPatronPassword=").append(validPatronPassword)
        .append(", currencyType=").append(currencyType)
        .append(", feeAmount=").append(feeAmount)
        .append(", screenMessage=").append(screenMessage)
        .append(", printLine=").append(printLine)
        .append(']').toString();
    final PatronStatusResponse psr = builder()
        .patronStatus(patronStatus)
        .language(language)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .personalName(personalName)
        .validPatron(validPatron)
        .validPatronPassword(validPatronPassword)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertEquals(expectedString, psr.toString());
  }
}
