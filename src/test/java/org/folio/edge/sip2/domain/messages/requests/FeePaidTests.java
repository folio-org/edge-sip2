package org.folio.edge.sip2.domain.messages.requests;

import static org.folio.edge.sip2.domain.messages.enumerations.CurrencyType.CAD;
import static org.folio.edge.sip2.domain.messages.enumerations.CurrencyType.USD;
import static org.folio.edge.sip2.domain.messages.enumerations.FeeType.ADMINISTRATIVE;
import static org.folio.edge.sip2.domain.messages.enumerations.FeeType.OVERDUE;
import static org.folio.edge.sip2.domain.messages.enumerations.PaymentType.CASH;
import static org.folio.edge.sip2.domain.messages.enumerations.PaymentType.CREDIT_CARD;
import static org.folio.edge.sip2.domain.messages.requests.FeePaid.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;

import org.folio.edge.sip2.domain.messages.enumerations.CurrencyType;
import org.folio.edge.sip2.domain.messages.enumerations.FeeType;
import org.folio.edge.sip2.domain.messages.enumerations.PaymentType;
import org.junit.jupiter.api.Test;

class FeePaidTests {
  final OffsetDateTime transactionDate = OffsetDateTime.now();
  final FeeType feeType = OVERDUE;
  final PaymentType paymentType = CASH;
  final CurrencyType currencyType = USD;
  final String feeAmount = "100.00";
  final String institutionId = "diku";
  final String patronIdentifier = "1234567890";
  final String terminalPassword = "12345";
  final String patronPassword = "2112";
  final String feeIdentifier = "Return the book already!";
  final String transactionId = "102938478576";

  @Test
  void testGetTransactionDate() {
    final FeePaid fp = builder().transactionDate(transactionDate).build();
    assertEquals(transactionDate, fp.getTransactionDate());
    assertNull(fp.getFeeType());
    assertNull(fp.getPaymentType());
    assertNull(fp.getCurrencyType());
    assertNull(fp.getFeeAmount());
    assertNull(fp.getInstitutionId());
    assertNull(fp.getPatronIdentifier());
    assertNull(fp.getTerminalPassword());
    assertNull(fp.getPatronPassword());
    assertNull(fp.getFeeIdentifier());
    assertNull(fp.getTransactionId());
  }

  @Test
  void testGetFeeType() {
    final FeePaid fp = builder().feeType(feeType).build();
    assertNull(fp.getTransactionDate());
    assertEquals(feeType, fp.getFeeType());
    assertNull(fp.getPaymentType());
    assertNull(fp.getCurrencyType());
    assertNull(fp.getFeeAmount());
    assertNull(fp.getInstitutionId());
    assertNull(fp.getPatronIdentifier());
    assertNull(fp.getTerminalPassword());
    assertNull(fp.getPatronPassword());
    assertNull(fp.getFeeIdentifier());
    assertNull(fp.getTransactionId());
  }

  @Test
  void testGetPaymentType() {
    final FeePaid fp = builder().paymentType(paymentType).build();
    assertNull(fp.getTransactionDate());
    assertNull(fp.getFeeType());
    assertEquals(paymentType, fp.getPaymentType());
    assertNull(fp.getCurrencyType());
    assertNull(fp.getFeeAmount());
    assertNull(fp.getInstitutionId());
    assertNull(fp.getPatronIdentifier());
    assertNull(fp.getTerminalPassword());
    assertNull(fp.getPatronPassword());
    assertNull(fp.getFeeIdentifier());
    assertNull(fp.getTransactionId());
  }

  @Test
  void testGetCurrencyType() {
    final FeePaid fp = builder().currencyType(currencyType).build();
    assertNull(fp.getTransactionDate());
    assertNull(fp.getFeeType());
    assertNull(fp.getPaymentType());
    assertEquals(currencyType, fp.getCurrencyType());
    assertNull(fp.getFeeAmount());
    assertNull(fp.getInstitutionId());
    assertNull(fp.getPatronIdentifier());
    assertNull(fp.getTerminalPassword());
    assertNull(fp.getPatronPassword());
    assertNull(fp.getFeeIdentifier());
    assertNull(fp.getTransactionId());
  }

  @Test
  void testGetFeeAmount() {
    final FeePaid fp = builder().feeAmount(feeAmount).build();
    assertNull(fp.getTransactionDate());
    assertNull(fp.getFeeType());
    assertNull(fp.getPaymentType());
    assertNull(fp.getCurrencyType());
    assertEquals(feeAmount, fp.getFeeAmount());
    assertNull(fp.getInstitutionId());
    assertNull(fp.getPatronIdentifier());
    assertNull(fp.getTerminalPassword());
    assertNull(fp.getPatronPassword());
    assertNull(fp.getFeeIdentifier());
    assertNull(fp.getTransactionId());

  }

  @Test
  void testGetInstitutionId() {
    final FeePaid fp = builder().institutionId(institutionId).build();
    assertNull(fp.getTransactionDate());
    assertNull(fp.getFeeType());
    assertNull(fp.getPaymentType());
    assertNull(fp.getCurrencyType());
    assertNull(fp.getFeeAmount());
    assertEquals(institutionId, fp.getInstitutionId());
    assertNull(fp.getPatronIdentifier());
    assertNull(fp.getTerminalPassword());
    assertNull(fp.getPatronPassword());
    assertNull(fp.getFeeIdentifier());
    assertNull(fp.getTransactionId());
  }

  @Test
  void testGetPatronIdentifier() {
    final FeePaid fp = builder().patronIdentifier(patronIdentifier).build();
    assertNull(fp.getTransactionDate());
    assertNull(fp.getFeeType());
    assertNull(fp.getPaymentType());
    assertNull(fp.getCurrencyType());
    assertNull(fp.getFeeAmount());
    assertNull(fp.getInstitutionId());
    assertEquals(patronIdentifier, fp.getPatronIdentifier());
    assertNull(fp.getTerminalPassword());
    assertNull(fp.getPatronPassword());
    assertNull(fp.getFeeIdentifier());
    assertNull(fp.getTransactionId());
  }

  @Test
  void testGetTerminalPassword() {
    final FeePaid fp = builder().terminalPassword(terminalPassword).build();
    assertNull(fp.getTransactionDate());
    assertNull(fp.getFeeType());
    assertNull(fp.getPaymentType());
    assertNull(fp.getCurrencyType());
    assertNull(fp.getFeeAmount());
    assertNull(fp.getInstitutionId());
    assertNull(fp.getPatronIdentifier());
    assertEquals(terminalPassword, fp.getTerminalPassword());
    assertNull(fp.getPatronPassword());
    assertNull(fp.getFeeIdentifier());
    assertNull(fp.getTransactionId());
  }

  @Test
  void testGetPatronPassword() {
    final FeePaid fp = builder().patronPassword(patronPassword).build();
    assertNull(fp.getTransactionDate());
    assertNull(fp.getFeeType());
    assertNull(fp.getPaymentType());
    assertNull(fp.getCurrencyType());
    assertNull(fp.getFeeAmount());
    assertNull(fp.getInstitutionId());
    assertNull(fp.getPatronIdentifier());
    assertNull(fp.getTerminalPassword());
    assertEquals(patronPassword, fp.getPatronPassword());
    assertNull(fp.getFeeIdentifier());
    assertNull(fp.getTransactionId());
  }

  @Test
  void testGetFeeIdentifier() {
    final FeePaid fp = builder().feeIdentifier(feeIdentifier).build();
    assertNull(fp.getTransactionDate());
    assertNull(fp.getFeeType());
    assertNull(fp.getPaymentType());
    assertNull(fp.getCurrencyType());
    assertNull(fp.getFeeAmount());
    assertNull(fp.getInstitutionId());
    assertNull(fp.getPatronIdentifier());
    assertNull(fp.getTerminalPassword());
    assertNull(fp.getPatronPassword());
    assertEquals(feeIdentifier, fp.getFeeIdentifier());
    assertNull(fp.getTransactionId());

  }

  @Test
  void testGetTransactionId() {
    final FeePaid fp = builder().transactionId(transactionId).build();
    assertNull(fp.getTransactionDate());
    assertNull(fp.getFeeType());
    assertNull(fp.getPaymentType());
    assertNull(fp.getCurrencyType());
    assertNull(fp.getFeeAmount());
    assertNull(fp.getInstitutionId());
    assertNull(fp.getPatronIdentifier());
    assertNull(fp.getTerminalPassword());
    assertNull(fp.getPatronPassword());
    assertNull(fp.getFeeIdentifier());
    assertEquals(transactionId, fp.getTransactionId());
  }

  @Test
  void testCompleteFeePaid() {
    final FeePaid fp = builder()
        .transactionDate(transactionDate)
        .feeType(feeType)
        .paymentType(paymentType)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .feeIdentifier(feeIdentifier)
        .transactionId(transactionId)
        .build();
    assertAll("FeePaid",
        () -> assertEquals(transactionDate, fp.getTransactionDate()),
        () -> assertEquals(feeType, fp.getFeeType()),
        () -> assertEquals(paymentType, fp.getPaymentType()),
        () -> assertEquals(currencyType, fp.getCurrencyType()),
        () -> assertEquals(feeAmount, fp.getFeeAmount()),
        () -> assertEquals(institutionId, fp.getInstitutionId()),
        () -> assertEquals(patronIdentifier, fp.getPatronIdentifier()),
        () -> assertEquals(terminalPassword, fp.getTerminalPassword()),
        () -> assertEquals(patronPassword, fp.getPatronPassword()),
        () -> assertEquals(feeIdentifier, fp.getFeeIdentifier()),
        () -> assertEquals(transactionId, fp.getTransactionId())
    );
  }

  @Test
  void testEquals() {
    final FeePaid fp1 = builder()
        .transactionDate(transactionDate)
        .feeType(feeType)
        .paymentType(paymentType)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .feeIdentifier(feeIdentifier)
        .transactionId(transactionId)
        .build();
    final FeePaid fp2 = builder()
        .transactionDate(transactionDate)
        .feeType(feeType)
        .paymentType(paymentType)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .feeIdentifier(feeIdentifier)
        .transactionId(transactionId)
        .build();
    assertTrue(fp1.equals(fp2));
    assertTrue(fp2.equals(fp1));
  }

  @Test
  void testNotEquals() {
    final FeePaid fp1 = builder()
        .transactionDate(transactionDate)
        .feeType(feeType)
        .paymentType(paymentType)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .feeIdentifier(feeIdentifier)
        .transactionId(transactionId)
        .build();
    final FeePaid fp2 = builder()
        .transactionDate(OffsetDateTime.now())
        .feeType(ADMINISTRATIVE)
        .paymentType(CREDIT_CARD)
        .currencyType(CAD)
        .feeAmount("25.00")
        .institutionId("test")
        .patronIdentifier("0987654321")
        .terminalPassword("0000")
        .patronPassword("9999")
        .feeIdentifier("admin charge")
        .transactionId("000000000000")
        .build();
    assertFalse(fp1.equals(fp2));
    assertFalse(fp2.equals(fp1));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("FeePaid [transactionDate=").append(transactionDate)
        .append(", feeType=").append(feeType)
        .append(", paymentType=").append(paymentType)
        .append(", currencyType=").append(currencyType)
        .append(", feeAmount=").append(feeAmount)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", terminalPassword=").append(terminalPassword)
        .append(", patronPassword=").append(patronPassword)
        .append(", feeIdentifier=").append(feeIdentifier)
        .append(", transactionId=").append(transactionId)
        .append(']').toString();
    final FeePaid fp = builder()
        .transactionDate(transactionDate)
        .feeType(feeType)
        .paymentType(paymentType)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .patronPassword(patronPassword)
        .feeIdentifier(feeIdentifier)
        .transactionId(transactionId)
        .build();
    assertEquals(expectedString, fp.toString());
  }
}
