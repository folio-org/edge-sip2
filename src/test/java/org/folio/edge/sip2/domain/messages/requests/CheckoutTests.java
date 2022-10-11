package org.folio.edge.sip2.domain.messages.requests;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.folio.edge.sip2.domain.messages.requests.Checkout.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class CheckoutTests {
  final Boolean scRenewalPolicy = TRUE;
  final Boolean noBlock = TRUE;
  final OffsetDateTime transactionDate = OffsetDateTime.now();
  final OffsetDateTime nbDueDate = transactionDate.plusDays(30);
  final String institutionId = "diku";
  final String patronIdentifier = "1234567890";
  final String itemIdentifier = "8675309";
  final String terminalPassword = "12345";
  final String patronPassword = "2112";
  final String itemProperties = "The autographed copy";
  final Boolean feeAcknowledged = TRUE;
  final Boolean cancel = TRUE;

  @Test
  void testGetScRenewalPolicy() {
    final Checkout co = builder().scRenewalPolicy(scRenewalPolicy).build();
    assertEquals(scRenewalPolicy, co.getScRenewalPolicy());
    assertNull(co.getNoBlock());
    assertNull(co.getTransactionDate());
    assertNull(co.getNbDueDate());
    assertNull(co.getInstitutionId());
    assertNull(co.getPatronIdentifier());
    assertNull(co.getItemIdentifier());
    assertNull(co.getTerminalPassword());
    assertNull(co.getItemProperties());
    assertNull(co.getPatronPassword());
    assertNull(co.getFeeAcknowledged());
    assertNull(co.getCancel());
  }

  @Test
  void testGetNoBlock() {
    final Checkout co = builder().noBlock(noBlock).build();
    assertNull(co.getScRenewalPolicy());
    assertEquals(noBlock, co.getNoBlock());
    assertNull(co.getTransactionDate());
    assertNull(co.getNbDueDate());
    assertNull(co.getInstitutionId());
    assertNull(co.getPatronIdentifier());
    assertNull(co.getItemIdentifier());
    assertNull(co.getTerminalPassword());
    assertNull(co.getItemProperties());
    assertNull(co.getPatronPassword());
    assertNull(co.getFeeAcknowledged());
    assertNull(co.getCancel());
  }

  @Test
  void testGetTransactionDate() {
    final Checkout co = builder().transactionDate(transactionDate).build();
    assertNull(co.getScRenewalPolicy());
    assertNull(co.getNoBlock());
    assertEquals(transactionDate, co.getTransactionDate());
    assertNull(co.getNbDueDate());
    assertNull(co.getInstitutionId());
    assertNull(co.getPatronIdentifier());
    assertNull(co.getItemIdentifier());
    assertNull(co.getTerminalPassword());
    assertNull(co.getItemProperties());
    assertNull(co.getPatronPassword());
    assertNull(co.getFeeAcknowledged());
    assertNull(co.getCancel());
  }

  @Test
  void testGetNbDueDate() {
    final Checkout co = builder().nbDueDate(nbDueDate).build();
    assertNull(co.getScRenewalPolicy());
    assertNull(co.getNoBlock());
    assertNull(co.getTransactionDate());
    assertEquals(nbDueDate, co.getNbDueDate());
    assertNull(co.getInstitutionId());
    assertNull(co.getPatronIdentifier());
    assertNull(co.getItemIdentifier());
    assertNull(co.getTerminalPassword());
    assertNull(co.getItemProperties());
    assertNull(co.getPatronPassword());
    assertNull(co.getFeeAcknowledged());
    assertNull(co.getCancel());
  }

  @Test
  void testGetInstitutionId() {
    final Checkout co = builder().institutionId(institutionId).build();
    assertNull(co.getScRenewalPolicy());
    assertNull(co.getNoBlock());
    assertNull(co.getTransactionDate());
    assertNull(co.getNbDueDate());
    assertEquals(institutionId, co.getInstitutionId());
    assertNull(co.getPatronIdentifier());
    assertNull(co.getItemIdentifier());
    assertNull(co.getTerminalPassword());
    assertNull(co.getItemProperties());
    assertNull(co.getPatronPassword());
    assertNull(co.getFeeAcknowledged());
    assertNull(co.getCancel());
  }

  @Test
  void testGetPatronIdentifier() {
    final Checkout co = builder().patronIdentifier(patronIdentifier).build();
    assertNull(co.getScRenewalPolicy());
    assertNull(co.getNoBlock());
    assertNull(co.getTransactionDate());
    assertNull(co.getNbDueDate());
    assertNull(co.getInstitutionId());
    assertEquals(patronIdentifier, co.getPatronIdentifier());
    assertNull(co.getItemIdentifier());
    assertNull(co.getTerminalPassword());
    assertNull(co.getItemProperties());
    assertNull(co.getPatronPassword());
    assertNull(co.getFeeAcknowledged());
    assertNull(co.getCancel());
  }

  @Test
  void testGetItemIdentifier() {
    final Checkout co = builder().itemIdentifier(itemIdentifier).build();
    assertNull(co.getScRenewalPolicy());
    assertNull(co.getNoBlock());
    assertNull(co.getTransactionDate());
    assertNull(co.getNbDueDate());
    assertNull(co.getInstitutionId());
    assertNull(co.getPatronIdentifier());
    assertEquals(itemIdentifier, co.getItemIdentifier());
    assertNull(co.getTerminalPassword());
    assertNull(co.getItemProperties());
    assertNull(co.getPatronPassword());
    assertNull(co.getFeeAcknowledged());
    assertNull(co.getCancel());
  }

  @Test
  void testGetTerminalPassword() {
    final Checkout co = builder().terminalPassword(terminalPassword).build();
    assertNull(co.getScRenewalPolicy());
    assertNull(co.getNoBlock());
    assertNull(co.getTransactionDate());
    assertNull(co.getNbDueDate());
    assertNull(co.getInstitutionId());
    assertNull(co.getPatronIdentifier());
    assertNull(co.getItemIdentifier());
    assertEquals(terminalPassword, co.getTerminalPassword());
    assertNull(co.getItemProperties());
    assertNull(co.getPatronPassword());
    assertNull(co.getFeeAcknowledged());
    assertNull(co.getCancel());
  }

  @Test
  void testGetItemProperties() {
    final Checkout co = builder().itemProperties(itemProperties).build();
    assertNull(co.getScRenewalPolicy());
    assertNull(co.getNoBlock());
    assertNull(co.getTransactionDate());
    assertNull(co.getNbDueDate());
    assertNull(co.getInstitutionId());
    assertNull(co.getPatronIdentifier());
    assertNull(co.getItemIdentifier());
    assertNull(co.getTerminalPassword());
    assertEquals(itemProperties, co.getItemProperties());
    assertNull(co.getPatronPassword());
    assertNull(co.getFeeAcknowledged());
    assertNull(co.getCancel());
  }

  @Test
  void testGetPatronPassword() {
    final Checkout co = builder().patronPassword(patronPassword).build();
    assertNull(co.getScRenewalPolicy());
    assertNull(co.getNoBlock());
    assertNull(co.getTransactionDate());
    assertNull(co.getNbDueDate());
    assertNull(co.getInstitutionId());
    assertNull(co.getPatronIdentifier());
    assertNull(co.getItemIdentifier());
    assertNull(co.getTerminalPassword());
    assertNull(co.getItemProperties());
    assertEquals(patronPassword, co.getPatronPassword());
    assertNull(co.getFeeAcknowledged());
    assertNull(co.getCancel());
  }

  @Test
  void testGetFeeAcknowledged() {
    final Checkout co = builder().feeAcknowledged(feeAcknowledged).build();
    assertNull(co.getScRenewalPolicy());
    assertNull(co.getNoBlock());
    assertNull(co.getTransactionDate());
    assertNull(co.getNbDueDate());
    assertNull(co.getInstitutionId());
    assertNull(co.getPatronIdentifier());
    assertNull(co.getItemIdentifier());
    assertNull(co.getTerminalPassword());
    assertNull(co.getItemProperties());
    assertNull(co.getPatronPassword());
    assertEquals(feeAcknowledged, co.getFeeAcknowledged());
    assertNull(co.getCancel());
  }

  @Test
  void testGetCancel() {
    final Checkout co = builder().cancel(cancel).build();
    assertNull(co.getScRenewalPolicy());
    assertNull(co.getNoBlock());
    assertNull(co.getTransactionDate());
    assertNull(co.getNbDueDate());
    assertNull(co.getInstitutionId());
    assertNull(co.getPatronIdentifier());
    assertNull(co.getItemIdentifier());
    assertNull(co.getTerminalPassword());
    assertNull(co.getItemProperties());
    assertNull(co.getPatronPassword());
    assertNull(co.getFeeAcknowledged());
    assertEquals(cancel, co.getCancel());
  }

  @Test
  void testCompleteCheckout() {
    final Checkout co = builder()
        .scRenewalPolicy(scRenewalPolicy)
        .noBlock(noBlock)
        .transactionDate(transactionDate)
        .nbDueDate(nbDueDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .terminalPassword(terminalPassword)
        .itemProperties(itemProperties)
        .patronPassword(patronPassword)
        .feeAcknowledged(feeAcknowledged)
        .cancel(cancel)
        .build();
    assertAll("Checkout",
        () -> assertEquals(scRenewalPolicy, co.getScRenewalPolicy()),
        () -> assertEquals(noBlock, co.getNoBlock()),
        () -> assertEquals(transactionDate, co.getTransactionDate()),
        () -> assertEquals(nbDueDate, co.getNbDueDate()),
        () -> assertEquals(institutionId, co.getInstitutionId()),
        () -> assertEquals(patronIdentifier, co.getPatronIdentifier()),
        () -> assertEquals(itemIdentifier, co.getItemIdentifier()),
        () -> assertEquals(terminalPassword, co.getTerminalPassword()),
        () -> assertEquals(itemProperties, co.getItemProperties()),
        () -> assertEquals(patronPassword, co.getPatronPassword()),
        () -> assertEquals(feeAcknowledged, co.getFeeAcknowledged()),
        () -> assertEquals(cancel, co.getCancel())
    );
  }

  @Test
  void testEqualsObject() {
    final Checkout co1 = builder()
        .scRenewalPolicy(scRenewalPolicy)
        .noBlock(noBlock)
        .transactionDate(transactionDate)
        .nbDueDate(nbDueDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .terminalPassword(terminalPassword)
        .itemProperties(itemProperties)
        .patronPassword(patronPassword)
        .feeAcknowledged(feeAcknowledged)
        .cancel(cancel)
        .build();
    final Checkout co2 = builder()
        .scRenewalPolicy(scRenewalPolicy)
        .noBlock(noBlock)
        .transactionDate(transactionDate)
        .nbDueDate(nbDueDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .terminalPassword(terminalPassword)
        .itemProperties(itemProperties)
        .patronPassword(patronPassword)
        .feeAcknowledged(feeAcknowledged)
        .cancel(cancel)
        .build();
    assertTrue(co1.equals(co2));
    assertTrue(co1.equals(co2));
  }

  @Test
  void testNotEqualsObject() {
    final Checkout co1 = builder()
        .scRenewalPolicy(scRenewalPolicy)
        .noBlock(noBlock)
        .transactionDate(transactionDate)
        .nbDueDate(nbDueDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .terminalPassword(terminalPassword)
        .itemProperties(itemProperties)
        .patronPassword(patronPassword)
        .feeAcknowledged(feeAcknowledged)
        .cancel(cancel)
        .build();
    final Checkout co2 = builder()
        .scRenewalPolicy(FALSE)
        .noBlock(FALSE)
        .transactionDate(transactionDate.minusDays(100))
        .nbDueDate(nbDueDate.minusDays(50))
        .institutionId("xyzzy")
        .patronIdentifier("111111111")
        .itemIdentifier("222222222")
        .terminalPassword("88888888")
        .itemProperties("Give me a book!")
        .patronPassword("0000000000")
        .feeAcknowledged(FALSE)
        .cancel(FALSE)
        .build();
    assertFalse(co1.equals(co2));
    assertFalse(co1.equals(co2));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("Checkout [scRenewalPolicy=").append(scRenewalPolicy)
        .append(", noBlock=").append(noBlock)
        .append(", transactionDate=").append(transactionDate)
        .append(", nbDueDate=").append(nbDueDate)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", itemIdentifier=").append(itemIdentifier)
        .append(", terminalPassword=").append(terminalPassword)
        .append(", itemProperties=").append(itemProperties)
        .append(", patronPassword=").append(patronPassword)
        .append(", feeAcknowledged=").append(feeAcknowledged)
        .append(", cancel=").append(cancel)
        .append(']')
        .toString();
    final Checkout co = builder()
        .scRenewalPolicy(scRenewalPolicy)
        .noBlock(noBlock)
        .transactionDate(transactionDate)
        .nbDueDate(nbDueDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .terminalPassword(terminalPassword)
        .itemProperties(itemProperties)
        .patronPassword(patronPassword)
        .feeAcknowledged(feeAcknowledged)
        .cancel(cancel)
        .build();
    assertEquals(expectedString, co.toString());
    assertNotNull(co.getCheckOutLogInfo());
  }
}
