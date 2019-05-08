package org.folio.edge.sip2.domain.messages.requests;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.folio.edge.sip2.domain.messages.requests.Renew.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

class RenewTests {
  final Boolean thirdPartyAllowed = TRUE;
  final Boolean noBlock = TRUE;
  final OffsetDateTime transactionDate = OffsetDateTime.now();
  final OffsetDateTime nbDueDate = transactionDate.plusDays(30);
  final String institutionId = "diku";
  final String patronIdentifier = "1234567890";
  final String patronPassword = "2112";
  final String itemIdentifier = "8675309";
  final String titleIdentifier = "5551212";
  final String terminalPassword = "12345";
  final String itemProperties = "The autographed copy";
  final Boolean feeAcknowledged = TRUE;

  @Test
  void testGetThirdPartyAllowed() {
    final Renew r = builder().thirdPartyAllowed(thirdPartyAllowed).build();
    assertEquals(thirdPartyAllowed, r.getThirdPartyAllowed());
    assertNull(r.getNoBlock());
    assertNull(r.getTransactionDate());
    assertNull(r.getNbDueDate());
    assertNull(r.getInstitutionId());
    assertNull(r.getPatronIdentifier());
    assertNull(r.getPatronPassword());
    assertNull(r.getItemIdentifier());
    assertNull(r.getTitleIdentifier());
    assertNull(r.getTerminalPassword());
    assertNull(r.getItemProperties());
    assertNull(r.getFeeAcknowledged());
  }

  @Test
  void testGetNoBlock() {
    final Renew r = builder().noBlock(noBlock).build();
    assertNull(r.getThirdPartyAllowed());
    assertEquals(noBlock, r.getNoBlock());
    assertNull(r.getTransactionDate());
    assertNull(r.getNbDueDate());
    assertNull(r.getInstitutionId());
    assertNull(r.getPatronIdentifier());
    assertNull(r.getPatronPassword());
    assertNull(r.getItemIdentifier());
    assertNull(r.getTitleIdentifier());
    assertNull(r.getTerminalPassword());
    assertNull(r.getItemProperties());
    assertNull(r.getFeeAcknowledged());
  }

  @Test
  void testGetTransactionDate() {
    final Renew r = builder().transactionDate(transactionDate).build();
    assertNull(r.getThirdPartyAllowed());
    assertNull(r.getNoBlock());
    assertEquals(transactionDate, r.getTransactionDate());
    assertNull(r.getNbDueDate());
    assertNull(r.getInstitutionId());
    assertNull(r.getPatronIdentifier());
    assertNull(r.getPatronPassword());
    assertNull(r.getItemIdentifier());
    assertNull(r.getTitleIdentifier());
    assertNull(r.getTerminalPassword());
    assertNull(r.getItemProperties());
    assertNull(r.getFeeAcknowledged());
  }

  @Test
  void testGetNbDueDate() {
    final Renew r = builder().nbDueDate(nbDueDate).build();
    assertNull(r.getThirdPartyAllowed());
    assertNull(r.getNoBlock());
    assertNull(r.getTransactionDate());
    assertEquals(nbDueDate, r.getNbDueDate());
    assertNull(r.getInstitutionId());
    assertNull(r.getPatronIdentifier());
    assertNull(r.getPatronPassword());
    assertNull(r.getItemIdentifier());
    assertNull(r.getTitleIdentifier());
    assertNull(r.getTerminalPassword());
    assertNull(r.getItemProperties());
    assertNull(r.getFeeAcknowledged());
  }

  @Test
  void testGetInstitutionId() {
    final Renew r = builder().institutionId(institutionId).build();
    assertNull(r.getThirdPartyAllowed());
    assertNull(r.getNoBlock());
    assertNull(r.getTransactionDate());
    assertNull(r.getNbDueDate());
    assertEquals(institutionId, r.getInstitutionId());
    assertNull(r.getPatronIdentifier());
    assertNull(r.getPatronPassword());
    assertNull(r.getItemIdentifier());
    assertNull(r.getTitleIdentifier());
    assertNull(r.getTerminalPassword());
    assertNull(r.getItemProperties());
    assertNull(r.getFeeAcknowledged());
  }

  @Test
  void testGetPatronIdentifier() {
    final Renew r = builder().patronIdentifier(patronIdentifier).build();
    assertNull(r.getThirdPartyAllowed());
    assertNull(r.getNoBlock());
    assertNull(r.getTransactionDate());
    assertNull(r.getNbDueDate());
    assertNull(r.getInstitutionId());
    assertEquals(patronIdentifier, r.getPatronIdentifier());
    assertNull(r.getPatronPassword());
    assertNull(r.getItemIdentifier());
    assertNull(r.getTitleIdentifier());
    assertNull(r.getTerminalPassword());
    assertNull(r.getItemProperties());
    assertNull(r.getFeeAcknowledged());
  }

  @Test
  void testGetPatronPassword() {
    final Renew r = builder().patronPassword(patronPassword).build();
    assertNull(r.getThirdPartyAllowed());
    assertNull(r.getNoBlock());
    assertNull(r.getTransactionDate());
    assertNull(r.getNbDueDate());
    assertNull(r.getInstitutionId());
    assertNull(r.getPatronIdentifier());
    assertEquals(patronPassword, r.getPatronPassword());
    assertNull(r.getItemIdentifier());
    assertNull(r.getTitleIdentifier());
    assertNull(r.getTerminalPassword());
    assertNull(r.getItemProperties());
    assertNull(r.getFeeAcknowledged());
  }

  @Test
  void testGetItemIdentifier() {
    final Renew r = builder().itemIdentifier(itemIdentifier).build();
    assertNull(r.getThirdPartyAllowed());
    assertNull(r.getNoBlock());
    assertNull(r.getTransactionDate());
    assertNull(r.getNbDueDate());
    assertNull(r.getInstitutionId());
    assertNull(r.getPatronIdentifier());
    assertNull(r.getPatronPassword());
    assertEquals(itemIdentifier, r.getItemIdentifier());
    assertNull(r.getTitleIdentifier());
    assertNull(r.getTerminalPassword());
    assertNull(r.getItemProperties());
    assertNull(r.getFeeAcknowledged());
  }

  @Test
  void testGetTitleIdentifier() {
    final Renew r = builder().titleIdentifier(titleIdentifier).build();
    assertNull(r.getThirdPartyAllowed());
    assertNull(r.getNoBlock());
    assertNull(r.getTransactionDate());
    assertNull(r.getNbDueDate());
    assertNull(r.getInstitutionId());
    assertNull(r.getPatronIdentifier());
    assertNull(r.getPatronPassword());
    assertNull(r.getItemIdentifier());
    assertEquals(titleIdentifier, r.getTitleIdentifier());
    assertNull(r.getTerminalPassword());
    assertNull(r.getItemProperties());
    assertNull(r.getFeeAcknowledged());
  }

  @Test
  void testGetTerminalPassword() {
    final Renew r = builder().terminalPassword(terminalPassword).build();
    assertNull(r.getThirdPartyAllowed());
    assertNull(r.getNoBlock());
    assertNull(r.getTransactionDate());
    assertNull(r.getNbDueDate());
    assertNull(r.getInstitutionId());
    assertNull(r.getPatronIdentifier());
    assertNull(r.getPatronPassword());
    assertNull(r.getItemIdentifier());
    assertNull(r.getTitleIdentifier());
    assertEquals(terminalPassword, r.getTerminalPassword());
    assertNull(r.getItemProperties());
    assertNull(r.getFeeAcknowledged());
  }

  @Test
  void testGetItemProperties() {
    final Renew r = builder().itemProperties(itemProperties).build();
    assertNull(r.getThirdPartyAllowed());
    assertNull(r.getNoBlock());
    assertNull(r.getTransactionDate());
    assertNull(r.getNbDueDate());
    assertNull(r.getInstitutionId());
    assertNull(r.getPatronIdentifier());
    assertNull(r.getPatronPassword());
    assertNull(r.getItemIdentifier());
    assertNull(r.getTitleIdentifier());
    assertNull(r.getTerminalPassword());
    assertEquals(itemProperties, r.getItemProperties());
    assertNull(r.getFeeAcknowledged());
  }

  @Test
  void testGetFeeAcknowledged() {
    final Renew r = builder().feeAcknowledged(feeAcknowledged).build();
    assertNull(r.getThirdPartyAllowed());
    assertNull(r.getNoBlock());
    assertNull(r.getTransactionDate());
    assertNull(r.getNbDueDate());
    assertNull(r.getInstitutionId());
    assertNull(r.getPatronIdentifier());
    assertNull(r.getPatronPassword());
    assertNull(r.getItemIdentifier());
    assertNull(r.getTitleIdentifier());
    assertNull(r.getTerminalPassword());
    assertNull(r.getItemProperties());
    assertEquals(feeAcknowledged, r.getFeeAcknowledged());
  }

  @Test
  void testCompleteRenew() {
    final Renew r = builder()
        .thirdPartyAllowed(thirdPartyAllowed)
        .noBlock(noBlock)
        .transactionDate(transactionDate)
        .nbDueDate(nbDueDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .patronPassword(patronPassword)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .terminalPassword(terminalPassword)
        .itemProperties(itemProperties)
        .feeAcknowledged(feeAcknowledged)
        .build();
    assertAll("Renew",
        () -> assertEquals(thirdPartyAllowed, r.getThirdPartyAllowed()),
        () -> assertEquals(noBlock, r.getNoBlock()),
        () -> assertEquals(transactionDate, r.getTransactionDate()),
        () -> assertEquals(nbDueDate, r.getNbDueDate()),
        () -> assertEquals(institutionId, r.getInstitutionId()),
        () -> assertEquals(patronIdentifier, r.getPatronIdentifier()),
        () -> assertEquals(patronPassword, r.getPatronPassword()),
        () -> assertEquals(itemIdentifier, r.getItemIdentifier()),
        () -> assertEquals(titleIdentifier, r.getTitleIdentifier()),
        () -> assertEquals(terminalPassword, r.getTerminalPassword()),
        () -> assertEquals(itemProperties, r.getItemProperties()),
        () -> assertEquals(feeAcknowledged, r.getFeeAcknowledged())
    );
  }

  @Test
  void testEqualsObject() {
    final Renew r1 = builder()
        .thirdPartyAllowed(thirdPartyAllowed)
        .noBlock(noBlock)
        .transactionDate(transactionDate)
        .nbDueDate(nbDueDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .patronPassword(patronPassword)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .terminalPassword(terminalPassword)
        .itemProperties(itemProperties)
        .feeAcknowledged(feeAcknowledged)
        .build();
    final Renew r2 = builder()
        .thirdPartyAllowed(thirdPartyAllowed)
        .noBlock(noBlock)
        .transactionDate(transactionDate)
        .nbDueDate(nbDueDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .patronPassword(patronPassword)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .terminalPassword(terminalPassword)
        .itemProperties(itemProperties)
        .feeAcknowledged(feeAcknowledged)
        .build();
    assertTrue(r1.equals(r2));
    assertTrue(r1.equals(r2));
  }

  @Test
  void testNotEqualsObject() {
    final Renew r1 = builder()
        .thirdPartyAllowed(thirdPartyAllowed)
        .noBlock(noBlock)
        .transactionDate(transactionDate)
        .nbDueDate(nbDueDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .patronPassword(patronPassword)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .terminalPassword(terminalPassword)
        .itemProperties(itemProperties)
        .feeAcknowledged(feeAcknowledged)
        .build();
    final Renew r2 = builder()
        .thirdPartyAllowed(FALSE)
        .noBlock(FALSE)
        .transactionDate(transactionDate.minusDays(100))
        .nbDueDate(nbDueDate.minusDays(50))
        .institutionId("xyzzy")
        .patronIdentifier("111111111")
        .patronPassword("0000000000")
        .itemIdentifier("222222222")
        .itemIdentifier("777777777")
        .terminalPassword("88888888")
        .itemProperties("Give me a book!")
        .feeAcknowledged(FALSE)
        .build();
    assertFalse(r1.equals(r2));
    assertFalse(r1.equals(r2));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("Renew [thirdPartyAllowed=").append(thirdPartyAllowed)
        .append(", noBlock=").append(noBlock)
        .append(", transactionDate=").append(transactionDate)
        .append(", nbDueDate=").append(nbDueDate)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", patronPassword=").append(patronPassword)
        .append(", itemIdentifier=").append(itemIdentifier)
        .append(", titleIdentifier=").append(titleIdentifier)
        .append(", terminalPassword=").append(terminalPassword)
        .append(", itemProperties=").append(itemProperties)
        .append(", feeAcknowledged=").append(feeAcknowledged)
        .append(']').toString();
    final Renew r = builder()
        .thirdPartyAllowed(thirdPartyAllowed)
        .noBlock(noBlock)
        .transactionDate(transactionDate)
        .nbDueDate(nbDueDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .patronPassword(patronPassword)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .terminalPassword(terminalPassword)
        .itemProperties(itemProperties)
        .feeAcknowledged(feeAcknowledged)
        .build();
    assertEquals(expectedString, r.toString());
  }
}
