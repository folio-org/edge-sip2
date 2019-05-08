package org.folio.edge.sip2.domain.messages.responses;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static org.folio.edge.sip2.domain.messages.responses.FeePaidResponse.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class FeePaidResponseTests {
  final Boolean paymentAccepted = TRUE;
  final OffsetDateTime transactionDate = OffsetDateTime.now();
  final String institutionId = "diku";
  final String patronIdentifier = "1234567890";
  final String transactionId = "029282747585";
  final List<String> screenMessage = asList("Hello, world!");
  final List<String> printLine = asList("Dot matrix");

  @Test
  void testGetPaymentAccepted() {
    final FeePaidResponse fpr = builder()
        .paymentAccepted(paymentAccepted)
        .build();
    assertEquals(paymentAccepted, fpr.getPaymentAccepted());
    assertNull(fpr.getTransactionDate());
    assertNull(fpr.getInstitutionId());
    assertNull(fpr.getPatronIdentifier());
    assertNull(fpr.getTransactionId());
    assertNull(fpr.getScreenMessage());
    assertNull(fpr.getPrintLine());
  }

  @Test
  void testGetTransactionDate() {
    final FeePaidResponse fpr = builder()
        .transactionDate(transactionDate)
        .build();
    assertNull(fpr.getPaymentAccepted());
    assertEquals(transactionDate, fpr.getTransactionDate());
    assertNull(fpr.getInstitutionId());
    assertNull(fpr.getPatronIdentifier());
    assertNull(fpr.getTransactionId());
    assertNull(fpr.getScreenMessage());
    assertNull(fpr.getPrintLine());
  }

  @Test
  void testGetInstitutionId() {
    final FeePaidResponse fpr = builder()
        .institutionId(institutionId)
        .build();
    assertNull(fpr.getPaymentAccepted());
    assertNull(fpr.getTransactionDate());
    assertEquals(institutionId, fpr.getInstitutionId());
    assertNull(fpr.getPatronIdentifier());
    assertNull(fpr.getTransactionId());
    assertNull(fpr.getScreenMessage());
    assertNull(fpr.getPrintLine());
  }

  @Test
  void testGetPatronIdentifier() {
    final FeePaidResponse fpr = builder()
        .patronIdentifier(patronIdentifier)
        .build();
    assertNull(fpr.getPaymentAccepted());
    assertNull(fpr.getTransactionDate());
    assertNull(fpr.getInstitutionId());
    assertEquals(patronIdentifier, fpr.getPatronIdentifier());
    assertNull(fpr.getTransactionId());
    assertNull(fpr.getScreenMessage());
    assertNull(fpr.getPrintLine());
  }

  @Test
  void testGetTransactionId() {
    final FeePaidResponse fpr = builder().transactionId(transactionId).build();
    assertNull(fpr.getPaymentAccepted());
    assertNull(fpr.getTransactionDate());
    assertNull(fpr.getInstitutionId());
    assertNull(fpr.getPatronIdentifier());
    assertEquals(transactionId, fpr.getTransactionId());
    assertNull(fpr.getScreenMessage());
    assertNull(fpr.getPrintLine());
  }

  @Test
  void testGetScreenMessage() {
    final FeePaidResponse fpr = builder().screenMessage(screenMessage).build();
    assertNull(fpr.getPaymentAccepted());
    assertNull(fpr.getTransactionDate());
    assertNull(fpr.getInstitutionId());
    assertNull(fpr.getPatronIdentifier());
    assertNull(fpr.getTransactionId());
    assertEquals(screenMessage, fpr.getScreenMessage());
    assertNull(fpr.getPrintLine());
  }

  @Test
  void testGetPrintLine() {
    final FeePaidResponse fpr = builder().printLine(printLine).build();
    assertNull(fpr.getPaymentAccepted());
    assertNull(fpr.getTransactionDate());
    assertNull(fpr.getInstitutionId());
    assertNull(fpr.getPatronIdentifier());
    assertNull(fpr.getTransactionId());
    assertNull(fpr.getScreenMessage());
    assertEquals(printLine, fpr.getPrintLine());
  }

  @Test
  void testCompleteFeePaidResponse() {
    final FeePaidResponse fpr = builder()
        .paymentAccepted(paymentAccepted)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .transactionId(transactionId)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertAll("FeePaidResponse",
        () -> assertEquals(paymentAccepted, fpr.getPaymentAccepted()),
        () -> assertEquals(transactionDate, fpr.getTransactionDate()),
        () -> assertEquals(institutionId, fpr.getInstitutionId()),
        () -> assertEquals(patronIdentifier, fpr.getPatronIdentifier()),
        () -> assertEquals(transactionId, fpr.getTransactionId()),
        () -> assertEquals(screenMessage, fpr.getScreenMessage()),
        () -> assertEquals(printLine, fpr.getPrintLine())
    );
  }

  @Test
  void testEquals() {
    final FeePaidResponse fpr1 = builder()
        .paymentAccepted(paymentAccepted)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .transactionId(transactionId)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    final FeePaidResponse fpr2 = builder()
        .paymentAccepted(paymentAccepted)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .transactionId(transactionId)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertTrue(fpr1.equals(fpr2));
    assertTrue(fpr2.equals(fpr1));
  }

  @Test
  void testNotEquals() {
    final FeePaidResponse fpr1 = builder()
        .paymentAccepted(paymentAccepted)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .transactionId(transactionId)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    final FeePaidResponse fpr2 = builder()
        .paymentAccepted(FALSE)
        .transactionDate(OffsetDateTime.now())
        .institutionId("test")
        .patronIdentifier("0987654321")
        .transactionId("893457893475893475")
        .screenMessage(asList("Welcome to the jungle."))
        .printLine(asList("Print print print"))
        .build();
    assertFalse(fpr1.equals(fpr2));
    assertFalse(fpr2.equals(fpr1));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("FeePaidResponse [paymentAccepted=").append(paymentAccepted)
        .append(", transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", transactionId=").append(transactionId)
        .append(", screenMessage=").append(screenMessage)
        .append(", printLine=").append(printLine)
        .append(']').toString();
    final FeePaidResponse fpr = builder()
        .paymentAccepted(paymentAccepted)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .transactionId(transactionId)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertEquals(expectedString, fpr.toString());
  }
}
