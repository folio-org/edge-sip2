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
import static org.folio.edge.sip2.domain.messages.responses.PatronInformationResponse.builder;
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

class PatronInformationResponseTests {
  final Set<PatronStatus> patronStatus =
      EnumSet.of(TOO_MANY_ITEMS_BILLED, HOLD_PRIVILEGES_DENIED);
  final Language language = ENGLISH;
  final OffsetDateTime transactionDate = OffsetDateTime.now();
  final String institutionId = "diku";
  final Integer holdItemsCount = Integer.valueOf(3);
  final Integer overdueItemsCount = Integer.valueOf(5);
  final Integer chargedItemsCount = Integer.valueOf(2);
  final Integer fineItemsCount = Integer.valueOf(1);
  final Integer recallItemsCount = Integer.valueOf(4);
  final Integer unavailableHoldsCount = Integer.valueOf(6);
  final String patronIdentifier = "1234567890";
  final String personalName = "John Smith";
  final Integer holdItemsLimit = Integer.valueOf(9999);
  final Integer overdueItemsLimit = Integer.valueOf(9998);
  final Integer chargedItemsLimit = Integer.valueOf(9997);
  final Boolean validPatron = TRUE;
  final Boolean validPatronPassword = TRUE;
  final CurrencyType currencyType = USD;
  final String feeAmount = "25.00";
  final String feeLimit = "50.00";
  final List<String> holdItems = asList("hold1", "hold2", "hold3");
  final List<String> overdueItems =
      asList("overdue1", "overdue2", "overdue3", "overdue4", "overdue5");
  final List<String> chargedItems = asList("charged1", "charged2", "charged3");
  final List<String> fineItems = asList("fine1");
  final List<String> recallItems =
      asList("recall1", "recall2", "recall3", "recall4");
  final List<String> unavailableHoldItems = asList("unavailableHolds1",
      "unavailableHolds2", "unavailableHolds3", "unavailableHolds4",
      "unavailableHolds5", "unavailableHolds6");
  final String homeAddress = "123 Fake St. Anytown, USA";
  final String emailAddress = "folio@example.com";
  final String homePhoneNumber = "555-1212";
  final List<String> screenMessage = asList("Hello, world!");
  final List<String> printLine = asList("Dot matrix");
  final String borrowerType = "patron";
  final String borrowerTypeDescription = "the library patrons";

  @Test
  void testGetPatronStatus() {
    final PatronInformationResponse pir = builder()
        .patronStatus(patronStatus)
        .build();
    assertEquals(patronStatus, pir.getPatronStatus());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetLanguage() {
    final PatronInformationResponse pir = builder().language(language).build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertEquals(language, pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetTransactionDate() {
    final PatronInformationResponse pir = builder().transactionDate(transactionDate).build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertEquals(transactionDate, pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetHoldItemsCount() {
    final PatronInformationResponse pir = builder()
        .holdItemsCount(holdItemsCount)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertEquals(holdItemsCount, pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetOverdueItemsCount() {
    final PatronInformationResponse pir = builder()
        .overdueItemsCount(overdueItemsCount)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertEquals(overdueItemsCount, pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetChargedItemsCount() {
    final PatronInformationResponse pir = builder()
        .chargedItemsCount(chargedItemsCount)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertEquals(chargedItemsCount, pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetFineItemsCount() {
    final PatronInformationResponse pir = builder()
        .fineItemsCount(fineItemsCount)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertEquals(fineItemsCount, pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetRecallItemsCount() {
    final PatronInformationResponse pir = builder()
        .recallItemsCount(recallItemsCount)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertEquals(recallItemsCount, pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetUnavailableHoldsCount() {
    final PatronInformationResponse pir = builder()
        .unavailableHoldsCount(unavailableHoldsCount)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertEquals(unavailableHoldsCount, pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetInstitutionId() {
    final PatronInformationResponse pir = builder()
        .institutionId(institutionId)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertEquals(institutionId, pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetPatronIdentifier() {
    final PatronInformationResponse pir = builder()
        .patronIdentifier(patronIdentifier)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertEquals(patronIdentifier, pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetPersonalName() {
    final PatronInformationResponse pir = builder()
        .personalName(personalName)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertEquals(personalName, pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetHoldItemsLimit() {
    final PatronInformationResponse pir = builder()
        .holdItemsLimit(holdItemsLimit)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertEquals(holdItemsLimit, pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetOverdueItemsLimit() {
    final PatronInformationResponse pir = builder()
        .overdueItemsLimit(overdueItemsLimit)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertEquals(overdueItemsLimit, pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetChargedItemsLimit() {
    final PatronInformationResponse pir = builder()
        .chargedItemsLimit(chargedItemsLimit)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertEquals(chargedItemsLimit, pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetValidPatron() {
    final PatronInformationResponse pir = builder()
        .validPatron(validPatron)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertEquals(validPatron, pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetValidPatronPassword() {
    final PatronInformationResponse pir = builder()
        .validPatronPassword(validPatronPassword)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertEquals(validPatronPassword, pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetCurrencyType() {
    final PatronInformationResponse pir = builder()
        .currencyType(currencyType)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertEquals(currencyType, pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetFeeAmount() {
    final PatronInformationResponse pir = builder().feeAmount(feeAmount).build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertEquals(feeAmount, pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetFeelimit() {
    final PatronInformationResponse pir = builder().feeLimit(feeLimit).build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertEquals(feeLimit, pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetHoldItems() {
    final PatronInformationResponse pir = builder()
        .holdItems(holdItems)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertEquals(holdItems, pir.getHoldItems());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetOverdueItems() {
    final PatronInformationResponse pir = builder()
        .overdueItems(overdueItems)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertEquals(overdueItems, pir.getOverdueItems());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetChargedItems() {
    final PatronInformationResponse pir = builder()
        .chargedItems(chargedItems)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertEquals(chargedItems, pir.getChargedItems());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetFineItems() {
    final PatronInformationResponse pir = builder()
        .fineItems(fineItems)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertEquals(fineItems, pir.getFineItems());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetRecallItems() {
    final PatronInformationResponse pir = builder()
        .recallItems(recallItems)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertEquals(recallItems, pir.getRecallItems());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetUnavailableHoldItems() {
    final PatronInformationResponse pir = builder()
        .unavailableHoldItems(unavailableHoldItems)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertEquals(unavailableHoldItems, pir.getUnavailableHoldItems());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetHomeAddress() {
    final PatronInformationResponse pir = builder()
        .homeAddress(homeAddress)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertEquals(homeAddress, pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetEmailAddress() {
    final PatronInformationResponse pir = builder()
        .emailAddress(emailAddress)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertEquals(emailAddress, pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetHomePhoneNumber() {
    final PatronInformationResponse pir = builder()
        .homePhoneNumber(homePhoneNumber)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertEquals(homePhoneNumber, pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetScreenMessage() {
    final PatronInformationResponse pir = builder()
        .screenMessage(screenMessage)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertEquals(screenMessage, pir.getScreenMessage());
    assertNull(pir.getPrintLine());
  }

  @Test
  void testGetPrintLine() {
    final PatronInformationResponse pir = builder()
        .printLine(printLine)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertEquals(printLine, pir.getPrintLine());
  }

  @Test
  void testGetBorrowerType() {
    final PatronInformationResponse pir = builder()
        .borrowerType(borrowerType)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
    assertEquals(pir.getBorrowerType(), borrowerType);
    assertNull(pir.getBorrowerTypeDescription());
  }

  @Test
  void testGetBorrowerTypeDescription() {
    final PatronInformationResponse pir = builder()
        .borrowerTypeDescription(borrowerTypeDescription)
        .build();
    assertTrue(pir.getPatronStatus().isEmpty());
    assertNull(pir.getLanguage());
    assertNull(pir.getTransactionDate());
    assertNull(pir.getHoldItemsCount());
    assertNull(pir.getOverdueItemsCount());
    assertNull(pir.getChargedItemsCount());
    assertNull(pir.getFineItemsCount());
    assertNull(pir.getRecallItemsCount());
    assertNull(pir.getUnavailableHoldsCount());
    assertNull(pir.getInstitutionId());
    assertNull(pir.getPatronIdentifier());
    assertNull(pir.getPersonalName());
    assertNull(pir.getHoldItemsLimit());
    assertNull(pir.getOverdueItemsLimit());
    assertNull(pir.getChargedItemsLimit());
    assertNull(pir.getValidPatron());
    assertNull(pir.getValidPatronPassword());
    assertNull(pir.getCurrencyType());
    assertNull(pir.getFeeAmount());
    assertNull(pir.getFeeLimit());
    assertTrue(pir.getHoldItems().isEmpty());
    assertTrue(pir.getOverdueItems().isEmpty());
    assertTrue(pir.getChargedItems().isEmpty());
    assertTrue(pir.getFineItems().isEmpty());
    assertTrue(pir.getRecallItems().isEmpty());
    assertTrue(pir.getUnavailableHoldItems().isEmpty());
    assertNull(pir.getHomeAddress());
    assertNull(pir.getEmailAddress());
    assertNull(pir.getHomePhoneNumber());
    assertNull(pir.getScreenMessage());
    assertNull(pir.getPrintLine());
    assertNull(pir.getBorrowerType());
    assertEquals(pir.getBorrowerTypeDescription(), borrowerTypeDescription);
  }

  @Test
  void testCompletePatronInformationResponse() {
    final PatronInformationResponse pir = builder()
        .patronStatus(patronStatus)
        .language(language)
        .transactionDate(transactionDate)
        .holdItemsCount(holdItemsCount)
        .overdueItemsCount(overdueItemsCount)
        .chargedItemsCount(chargedItemsCount)
        .fineItemsCount(fineItemsCount)
        .recallItemsCount(recallItemsCount)
        .unavailableHoldsCount(unavailableHoldsCount)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .personalName(personalName)
        .holdItemsLimit(holdItemsLimit)
        .overdueItemsLimit(overdueItemsLimit)
        .chargedItemsLimit(chargedItemsLimit)
        .validPatron(validPatron)
        .validPatronPassword(validPatronPassword)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .feeLimit(feeLimit)
        .holdItems(holdItems)
        .overdueItems(overdueItems)
        .chargedItems(chargedItems)
        .fineItems(fineItems)
        .recallItems(recallItems)
        .unavailableHoldItems(unavailableHoldItems)
        .homeAddress(homeAddress)
        .emailAddress(emailAddress)
        .homePhoneNumber(homePhoneNumber)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertAll("PatronInformationResponse",
        () -> assertEquals(patronStatus, pir.getPatronStatus()),
        () -> assertEquals(language, pir.getLanguage()),
        () -> assertEquals(transactionDate, pir.getTransactionDate()),
        () -> assertEquals(holdItemsCount, pir.getHoldItemsCount()),
        () -> assertEquals(overdueItemsCount, pir.getOverdueItemsCount()),
        () -> assertEquals(chargedItemsCount, pir.getChargedItemsCount()),
        () -> assertEquals(fineItemsCount, pir.getFineItemsCount()),
        () -> assertEquals(recallItemsCount, pir.getRecallItemsCount()),
        () -> assertEquals(unavailableHoldsCount,
            pir.getUnavailableHoldsCount()),
        () -> assertEquals(institutionId, pir.getInstitutionId()),
        () -> assertEquals(patronIdentifier, pir.getPatronIdentifier()),
        () -> assertEquals(holdItemsLimit, pir.getHoldItemsLimit()),
        () -> assertEquals(overdueItemsLimit, pir.getOverdueItemsLimit()),
        () -> assertEquals(chargedItemsLimit, pir.getChargedItemsLimit()),
        () -> assertEquals(personalName, pir.getPersonalName()),
        () -> assertEquals(validPatron, pir.getValidPatron()),
        () -> assertEquals(validPatronPassword, pir.getValidPatronPassword()),
        () -> assertEquals(currencyType, pir.getCurrencyType()),
        () -> assertEquals(feeAmount, pir.getFeeAmount()),
        () -> assertEquals(feeLimit, pir.getFeeLimit()),
        () -> assertEquals(holdItems, pir.getHoldItems()),
        () -> assertEquals(overdueItems, pir.getOverdueItems()),
        () -> assertEquals(chargedItems, pir.getChargedItems()),
        () -> assertEquals(fineItems, pir.getFineItems()),
        () -> assertEquals(recallItems, pir.getRecallItems()),
        () -> assertEquals(unavailableHoldItems, pir.getUnavailableHoldItems()),
        () -> assertEquals(homeAddress, pir.getHomeAddress()),
        () -> assertEquals(emailAddress, pir.getEmailAddress()),
        () -> assertEquals(homePhoneNumber, pir.getHomePhoneNumber()),
        () -> assertEquals(screenMessage, pir.getScreenMessage()),
        () -> assertEquals(printLine, pir.getPrintLine())
    );
  }

  @Test
  void testEquals() {
    final PatronInformationResponse pir1 = builder()
        .patronStatus(patronStatus)
        .language(language)
        .transactionDate(transactionDate)
        .holdItemsCount(holdItemsCount)
        .overdueItemsCount(overdueItemsCount)
        .chargedItemsCount(chargedItemsCount)
        .fineItemsCount(fineItemsCount)
        .recallItemsCount(recallItemsCount)
        .unavailableHoldsCount(unavailableHoldsCount)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .personalName(personalName)
        .holdItemsLimit(holdItemsLimit)
        .overdueItemsLimit(overdueItemsLimit)
        .chargedItemsLimit(chargedItemsLimit)
        .validPatron(validPatron)
        .validPatronPassword(validPatronPassword)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .feeLimit(feeLimit)
        .holdItems(holdItems)
        .overdueItems(overdueItems)
        .chargedItems(chargedItems)
        .fineItems(fineItems)
        .recallItems(recallItems)
        .unavailableHoldItems(unavailableHoldItems)
        .homeAddress(homeAddress)
        .emailAddress(emailAddress)
        .homePhoneNumber(homePhoneNumber)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    final PatronInformationResponse pir2 = builder()
        .patronStatus(patronStatus)
        .language(language)
        .transactionDate(transactionDate)
        .holdItemsCount(holdItemsCount)
        .overdueItemsCount(overdueItemsCount)
        .chargedItemsCount(chargedItemsCount)
        .fineItemsCount(fineItemsCount)
        .recallItemsCount(recallItemsCount)
        .unavailableHoldsCount(unavailableHoldsCount)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .personalName(personalName)
        .holdItemsLimit(holdItemsLimit)
        .overdueItemsLimit(overdueItemsLimit)
        .chargedItemsLimit(chargedItemsLimit)
        .validPatron(validPatron)
        .validPatronPassword(validPatronPassword)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .feeLimit(feeLimit)
        .holdItems(holdItems)
        .overdueItems(overdueItems)
        .chargedItems(chargedItems)
        .fineItems(fineItems)
        .recallItems(recallItems)
        .unavailableHoldItems(unavailableHoldItems)
        .homeAddress(homeAddress)
        .emailAddress(emailAddress)
        .homePhoneNumber(homePhoneNumber)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertTrue(pir1.equals(pir2));
    assertTrue(pir2.equals(pir1));
  }

  @Test
  void testNotEquals() {
    final PatronInformationResponse pir1 = builder()
        .patronStatus(patronStatus)
        .language(language)
        .transactionDate(transactionDate)
        .holdItemsCount(holdItemsCount)
        .overdueItemsCount(overdueItemsCount)
        .chargedItemsCount(chargedItemsCount)
        .fineItemsCount(fineItemsCount)
        .recallItemsCount(recallItemsCount)
        .unavailableHoldsCount(unavailableHoldsCount)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .personalName(personalName)
        .holdItemsLimit(holdItemsLimit)
        .overdueItemsLimit(overdueItemsLimit)
        .chargedItemsLimit(chargedItemsLimit)
        .validPatron(validPatron)
        .validPatronPassword(validPatronPassword)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .feeLimit(feeLimit)
        .holdItems(holdItems)
        .overdueItems(overdueItems)
        .chargedItems(chargedItems)
        .fineItems(fineItems)
        .recallItems(recallItems)
        .unavailableHoldItems(unavailableHoldItems)
        .homeAddress(homeAddress)
        .emailAddress(emailAddress)
        .homePhoneNumber(homePhoneNumber)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    final PatronInformationResponse pir2 = builder()
        .patronStatus(EnumSet.of(
            CARD_REPORTED_LOST,
            TOO_MANY_ITEMS_LOST,
            TOO_MANY_RENEWALS))
        .language(UNKNOWN)
        .transactionDate(OffsetDateTime.now())
        .holdItemsCount(Integer.valueOf(6))
        .overdueItemsCount(Integer.valueOf(2))
        .chargedItemsCount(Integer.valueOf(3))
        .fineItemsCount(Integer.valueOf(4))
        .recallItemsCount(Integer.valueOf(5))
        .unavailableHoldsCount(Integer.valueOf(1))
        .institutionId("test")
        .patronIdentifier("0987654321")
        .personalName("Jane Doe")
        .holdItemsLimit(Integer.valueOf(25))
        .overdueItemsLimit(Integer.valueOf(26))
        .chargedItemsLimit(Integer.valueOf(27))
        .validPatron(FALSE)
        .validPatronPassword(FALSE)
        .currencyType(GBP)
        .feeAmount("10.00")
        .feeLimit("250.00")
        .holdItems(asList("hold1", "hold2", "hold3", "hold4", "hold5", "hold6"))
        .overdueItems(asList("overdue1", "overdue2"))
        .chargedItems(asList("charged1", "charged2", "charged3"))
        .fineItems(asList("fine1", "fine2", "fine3", "fine4"))
        .recallItems(asList("recall1", "recall2", "recall3", "recall4",
            "recall5"))
        .unavailableHoldItems(asList("unavailableHoldItems1"))
        .homeAddress("8672 Evergreen Terrace, Springfield USA")
        .emailAddress("admin@example.com")
        .homePhoneNumber("555-5555")
        .screenMessage(asList("Welcome to the jungle."))
        .printLine(asList("Print print print"))
        .build();
    assertFalse(pir1.equals(pir2));
    assertFalse(pir2.equals(pir1));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("PatronInformationResponse [patronStatus=").append(patronStatus)
        .append(", language=").append(language)
        .append(", transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", holdItemsCount=").append(holdItemsCount)
        .append(", overdueItemsCount=").append(overdueItemsCount)
        .append(", chargedItemsCount=").append(chargedItemsCount)
        .append(", fineItemsCount=").append(fineItemsCount)
        .append(", recallItemsCount=").append(recallItemsCount)
        .append(", unavailableHoldsCount=").append(unavailableHoldsCount)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", personalName=").append(personalName)
        .append(", holdItemsLimit=").append(holdItemsLimit)
        .append(", overdueItemsLimit=").append(overdueItemsLimit)
        .append(", chargedItemsLimit=").append(chargedItemsLimit)
        .append(", validPatron=").append(validPatron)
        .append(", validPatronPassword=").append(validPatronPassword)
        .append(", currencyType=").append(currencyType)
        .append(", feeAmount=").append(feeAmount)
        .append(", feeLimit=").append(feeLimit)
        .append(", holdItems=").append(holdItems)
        .append(", overdueItems=").append(overdueItems)
        .append(", chargedItems=").append(chargedItems)
        .append(", fineItems=").append(fineItems)
        .append(", recallItems=").append(recallItems)
        .append(", unavailableHoldItems=").append(unavailableHoldItems)
        .append(", homeAddress=").append(homeAddress)
        .append(", emailAddress=").append(emailAddress)
        .append(", homePhoneNumber=").append(homePhoneNumber)
        .append(", screenMessage=").append(screenMessage)
        .append(", printLine=").append(printLine)
        .append(", borrowerType=").append(borrowerType)
        .append(", borrowerTypeDescription=").append(borrowerTypeDescription)
        .append(']').toString();
    final PatronInformationResponse pir = builder()
        .patronStatus(patronStatus)
        .language(language)
        .transactionDate(transactionDate)
        .holdItemsCount(holdItemsCount)
        .overdueItemsCount(overdueItemsCount)
        .chargedItemsCount(chargedItemsCount)
        .fineItemsCount(fineItemsCount)
        .recallItemsCount(recallItemsCount)
        .unavailableHoldsCount(unavailableHoldsCount)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .personalName(personalName)
        .holdItemsLimit(holdItemsLimit)
        .overdueItemsLimit(overdueItemsLimit)
        .chargedItemsLimit(chargedItemsLimit)
        .validPatron(validPatron)
        .validPatronPassword(validPatronPassword)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .feeLimit(feeLimit)
        .holdItems(holdItems)
        .overdueItems(overdueItems)
        .chargedItems(chargedItems)
        .fineItems(fineItems)
        .recallItems(recallItems)
        .unavailableHoldItems(unavailableHoldItems)
        .homeAddress(homeAddress)
        .emailAddress(emailAddress)
        .homePhoneNumber(homePhoneNumber)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .borrowerType(borrowerType)
        .borrowerTypeDescription(borrowerTypeDescription)
        .build();
    assertEquals(expectedString, pir.toString());
  }
}
