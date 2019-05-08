package org.folio.edge.sip2.domain.messages.responses;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static org.folio.edge.sip2.domain.messages.enumerations.CurrencyType.GBP;
import static org.folio.edge.sip2.domain.messages.enumerations.CurrencyType.USD;
import static org.folio.edge.sip2.domain.messages.enumerations.FeeType.DAMAGE;
import static org.folio.edge.sip2.domain.messages.enumerations.FeeType.RENTAL;
import static org.folio.edge.sip2.domain.messages.enumerations.MediaType.OTHER;
import static org.folio.edge.sip2.domain.messages.enumerations.MediaType.VIDEO_TAPE;
import static org.folio.edge.sip2.domain.messages.responses.CheckoutResponse.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;
import java.util.List;
import org.folio.edge.sip2.domain.messages.enumerations.CurrencyType;
import org.folio.edge.sip2.domain.messages.enumerations.FeeType;
import org.folio.edge.sip2.domain.messages.enumerations.MediaType;
import org.junit.jupiter.api.Test;

class CheckoutResponseTests {
  final Boolean ok = TRUE;
  final Boolean renewalOk = TRUE;
  final Boolean magneticMedia = FALSE;
  final Boolean desensitize = TRUE;
  final ZonedDateTime transactionDate = ZonedDateTime.now();
  final String institutionId = "diku";
  final String patronIdentifier = "1234567890";
  final String itemIdentifier = "8675309";
  final String titleIdentifier = "5551212";
  final ZonedDateTime dueDate = transactionDate.plusDays(30);
  final FeeType feeType = RENTAL;
  final Boolean securityInhibit = FALSE;
  final CurrencyType currencyType = USD;
  final String feeAmount = "2.50";
  final MediaType mediaType = VIDEO_TAPE;
  final String itemProperties = "Directors Cut";
  final String transactionId = "01928374675";
  final List<String> screenMessage = asList("Please rewind");
  final List<String> printLine = asList("Enjoy!");

  @Test
  void testGetOk() {
    final CheckoutResponse cor = builder().ok(ok).build();
    assertEquals(ok, cor.getOk());
    assertNull(cor.getRenewalOk());
    assertNull(cor.getMagneticMedia());
    assertNull(cor.getDesensitize());
    assertNull(cor.getTransactionDate());
    assertNull(cor.getInstitutionId());
    assertNull(cor.getPatronIdentifier());
    assertNull(cor.getItemIdentifier());
    assertNull(cor.getTitleIdentifier());
    assertNull(cor.getDueDate());
    assertNull(cor.getFeeType());
    assertNull(cor.getSecurityInhibit());
    assertNull(cor.getCurrencyType());
    assertNull(cor.getFeeAmount());
    assertNull(cor.getMediaType());
    assertNull(cor.getItemProperties());
    assertNull(cor.getTransactionId());
    assertNull(cor.getScreenMessage());
    assertNull(cor.getPrintLine());
  }

  @Test
  void testGetRenewalOk() {
    final CheckoutResponse cor = builder().renewalOk(renewalOk).build();
    assertNull(cor.getOk());
    assertEquals(renewalOk, cor.getRenewalOk());
    assertNull(cor.getMagneticMedia());
    assertNull(cor.getDesensitize());
    assertNull(cor.getTransactionDate());
    assertNull(cor.getInstitutionId());
    assertNull(cor.getPatronIdentifier());
    assertNull(cor.getItemIdentifier());
    assertNull(cor.getTitleIdentifier());
    assertNull(cor.getDueDate());
    assertNull(cor.getFeeType());
    assertNull(cor.getSecurityInhibit());
    assertNull(cor.getCurrencyType());
    assertNull(cor.getFeeAmount());
    assertNull(cor.getMediaType());
    assertNull(cor.getItemProperties());
    assertNull(cor.getTransactionId());
    assertNull(cor.getScreenMessage());
    assertNull(cor.getPrintLine());
  }

  @Test
  void testGetMagneticMedia() {
    final CheckoutResponse cor = builder().magneticMedia(magneticMedia).build();
    assertNull(cor.getOk());
    assertNull(cor.getRenewalOk());
    assertEquals(magneticMedia, cor.getMagneticMedia());
    assertNull(cor.getDesensitize());
    assertNull(cor.getTransactionDate());
    assertNull(cor.getInstitutionId());
    assertNull(cor.getPatronIdentifier());
    assertNull(cor.getItemIdentifier());
    assertNull(cor.getTitleIdentifier());
    assertNull(cor.getDueDate());
    assertNull(cor.getFeeType());
    assertNull(cor.getSecurityInhibit());
    assertNull(cor.getCurrencyType());
    assertNull(cor.getFeeAmount());
    assertNull(cor.getMediaType());
    assertNull(cor.getItemProperties());
    assertNull(cor.getTransactionId());
    assertNull(cor.getScreenMessage());
    assertNull(cor.getPrintLine());
  }

  @Test
  void testGetDesensitize() {
    final CheckoutResponse cor = builder().desensitize(desensitize).build();
    assertNull(cor.getOk());
    assertNull(cor.getRenewalOk());
    assertNull(cor.getMagneticMedia());
    assertEquals(desensitize, cor.getDesensitize());
    assertNull(cor.getTransactionDate());
    assertNull(cor.getInstitutionId());
    assertNull(cor.getPatronIdentifier());
    assertNull(cor.getItemIdentifier());
    assertNull(cor.getTitleIdentifier());
    assertNull(cor.getDueDate());
    assertNull(cor.getFeeType());
    assertNull(cor.getSecurityInhibit());
    assertNull(cor.getCurrencyType());
    assertNull(cor.getFeeAmount());
    assertNull(cor.getMediaType());
    assertNull(cor.getItemProperties());
    assertNull(cor.getTransactionId());
    assertNull(cor.getScreenMessage());
    assertNull(cor.getPrintLine());
  }

  @Test
  void testGetTransactionDate() {
    final CheckoutResponse cor = builder()
        .transactionDate(transactionDate)
        .build();
    assertNull(cor.getOk());
    assertNull(cor.getRenewalOk());
    assertNull(cor.getMagneticMedia());
    assertNull(cor.getDesensitize());
    assertEquals(transactionDate, cor.getTransactionDate());
    assertNull(cor.getInstitutionId());
    assertNull(cor.getPatronIdentifier());
    assertNull(cor.getItemIdentifier());
    assertNull(cor.getTitleIdentifier());
    assertNull(cor.getDueDate());
    assertNull(cor.getFeeType());
    assertNull(cor.getSecurityInhibit());
    assertNull(cor.getCurrencyType());
    assertNull(cor.getFeeAmount());
    assertNull(cor.getMediaType());
    assertNull(cor.getItemProperties());
    assertNull(cor.getTransactionId());
    assertNull(cor.getScreenMessage());
    assertNull(cor.getPrintLine());
  }

  @Test
  void testGetInstitutionId() {
    final CheckoutResponse cor = builder().institutionId(institutionId).build();
    assertNull(cor.getOk());
    assertNull(cor.getRenewalOk());
    assertNull(cor.getMagneticMedia());
    assertNull(cor.getDesensitize());
    assertNull(cor.getTransactionDate());
    assertEquals(institutionId, cor.getInstitutionId());
    assertNull(cor.getPatronIdentifier());
    assertNull(cor.getItemIdentifier());
    assertNull(cor.getTitleIdentifier());
    assertNull(cor.getDueDate());
    assertNull(cor.getFeeType());
    assertNull(cor.getSecurityInhibit());
    assertNull(cor.getCurrencyType());
    assertNull(cor.getFeeAmount());
    assertNull(cor.getMediaType());
    assertNull(cor.getItemProperties());
    assertNull(cor.getTransactionId());
    assertNull(cor.getScreenMessage());
    assertNull(cor.getPrintLine());
  }

  @Test
  void testGetPatronIdentifier() {
    final CheckoutResponse cor = builder()
        .patronIdentifier(patronIdentifier)
        .build();
    assertNull(cor.getOk());
    assertNull(cor.getRenewalOk());
    assertNull(cor.getMagneticMedia());
    assertNull(cor.getDesensitize());
    assertNull(cor.getTransactionDate());
    assertNull(cor.getInstitutionId());
    assertEquals(patronIdentifier, cor.getPatronIdentifier());
    assertNull(cor.getItemIdentifier());
    assertNull(cor.getTitleIdentifier());
    assertNull(cor.getDueDate());
    assertNull(cor.getFeeType());
    assertNull(cor.getSecurityInhibit());
    assertNull(cor.getCurrencyType());
    assertNull(cor.getFeeAmount());
    assertNull(cor.getMediaType());
    assertNull(cor.getItemProperties());
    assertNull(cor.getTransactionId());
    assertNull(cor.getScreenMessage());
    assertNull(cor.getPrintLine());
  }

  @Test
  void testGetItemIdentifier() {
    final CheckoutResponse cor = builder()
        .itemIdentifier(itemIdentifier)
        .build();
    assertNull(cor.getOk());
    assertNull(cor.getRenewalOk());
    assertNull(cor.getMagneticMedia());
    assertNull(cor.getDesensitize());
    assertNull(cor.getTransactionDate());
    assertNull(cor.getInstitutionId());
    assertNull(cor.getPatronIdentifier());
    assertEquals(itemIdentifier, cor.getItemIdentifier());
    assertNull(cor.getTitleIdentifier());
    assertNull(cor.getDueDate());
    assertNull(cor.getFeeType());
    assertNull(cor.getSecurityInhibit());
    assertNull(cor.getCurrencyType());
    assertNull(cor.getFeeAmount());
    assertNull(cor.getMediaType());
    assertNull(cor.getItemProperties());
    assertNull(cor.getTransactionId());
    assertNull(cor.getScreenMessage());
    assertNull(cor.getPrintLine());
  }

  @Test
  void testGetTitleIdentifier() {
    final CheckoutResponse cor = builder()
        .titleIdentifier(titleIdentifier)
        .build();
    assertNull(cor.getOk());
    assertNull(cor.getRenewalOk());
    assertNull(cor.getMagneticMedia());
    assertNull(cor.getDesensitize());
    assertNull(cor.getTransactionDate());
    assertNull(cor.getInstitutionId());
    assertNull(cor.getPatronIdentifier());
    assertNull(cor.getItemIdentifier());
    assertEquals(titleIdentifier, cor.getTitleIdentifier());
    assertNull(cor.getDueDate());
    assertNull(cor.getFeeType());
    assertNull(cor.getSecurityInhibit());
    assertNull(cor.getCurrencyType());
    assertNull(cor.getFeeAmount());
    assertNull(cor.getMediaType());
    assertNull(cor.getItemProperties());
    assertNull(cor.getTransactionId());
    assertNull(cor.getScreenMessage());
    assertNull(cor.getPrintLine());
  }

  @Test
  void testGetDueDate() {
    final CheckoutResponse cor = builder().dueDate(dueDate).build();
    assertNull(cor.getOk());
    assertNull(cor.getRenewalOk());
    assertNull(cor.getMagneticMedia());
    assertNull(cor.getDesensitize());
    assertNull(cor.getTransactionDate());
    assertNull(cor.getInstitutionId());
    assertNull(cor.getPatronIdentifier());
    assertNull(cor.getItemIdentifier());
    assertNull(cor.getTitleIdentifier());
    assertEquals(dueDate, cor.getDueDate());
    assertNull(cor.getFeeType());
    assertNull(cor.getSecurityInhibit());
    assertNull(cor.getCurrencyType());
    assertNull(cor.getFeeAmount());
    assertNull(cor.getMediaType());
    assertNull(cor.getItemProperties());
    assertNull(cor.getTransactionId());
    assertNull(cor.getScreenMessage());
    assertNull(cor.getPrintLine());
  }

  @Test
  void testGetFeeType() {
    final CheckoutResponse cor = builder().feeType(feeType).build();
    assertNull(cor.getOk());
    assertNull(cor.getRenewalOk());
    assertNull(cor.getMagneticMedia());
    assertNull(cor.getDesensitize());
    assertNull(cor.getTransactionDate());
    assertNull(cor.getInstitutionId());
    assertNull(cor.getPatronIdentifier());
    assertNull(cor.getItemIdentifier());
    assertNull(cor.getTitleIdentifier());
    assertNull(cor.getDueDate());
    assertEquals(feeType, cor.getFeeType());
    assertNull(cor.getSecurityInhibit());
    assertNull(cor.getCurrencyType());
    assertNull(cor.getFeeAmount());
    assertNull(cor.getMediaType());
    assertNull(cor.getItemProperties());
    assertNull(cor.getTransactionId());
    assertNull(cor.getScreenMessage());
    assertNull(cor.getPrintLine());
  }

  @Test
  void testGetSecurityInhibit() {
    final CheckoutResponse cor = builder()
        .securityInhibit(securityInhibit)
        .build();
    assertNull(cor.getOk());
    assertNull(cor.getRenewalOk());
    assertNull(cor.getMagneticMedia());
    assertNull(cor.getDesensitize());
    assertNull(cor.getTransactionDate());
    assertNull(cor.getInstitutionId());
    assertNull(cor.getPatronIdentifier());
    assertNull(cor.getItemIdentifier());
    assertNull(cor.getTitleIdentifier());
    assertNull(cor.getDueDate());
    assertNull(cor.getFeeType());
    assertEquals(securityInhibit, cor.getSecurityInhibit());
    assertNull(cor.getFeeAmount());
    assertNull(cor.getMediaType());
    assertNull(cor.getItemProperties());
    assertNull(cor.getTransactionId());
    assertNull(cor.getScreenMessage());
    assertNull(cor.getPrintLine());
  }

  @Test
  void testGetCurrencyType() {
    final CheckoutResponse cor = builder().currencyType(currencyType).build();
    assertNull(cor.getOk());
    assertNull(cor.getRenewalOk());
    assertNull(cor.getMagneticMedia());
    assertNull(cor.getDesensitize());
    assertNull(cor.getTransactionDate());
    assertNull(cor.getInstitutionId());
    assertNull(cor.getPatronIdentifier());
    assertNull(cor.getItemIdentifier());
    assertNull(cor.getTitleIdentifier());
    assertNull(cor.getDueDate());
    assertNull(cor.getFeeType());
    assertNull(cor.getSecurityInhibit());
    assertEquals(currencyType, cor.getCurrencyType());
    assertNull(cor.getFeeAmount());
    assertNull(cor.getMediaType());
    assertNull(cor.getItemProperties());
    assertNull(cor.getTransactionId());
    assertNull(cor.getScreenMessage());
    assertNull(cor.getPrintLine());
  }

  @Test
  void testGetFeeAmount() {
    final CheckoutResponse cor = builder().feeAmount(feeAmount).build();
    assertNull(cor.getOk());
    assertNull(cor.getRenewalOk());
    assertNull(cor.getMagneticMedia());
    assertNull(cor.getDesensitize());
    assertNull(cor.getTransactionDate());
    assertNull(cor.getInstitutionId());
    assertNull(cor.getPatronIdentifier());
    assertNull(cor.getItemIdentifier());
    assertNull(cor.getTitleIdentifier());
    assertNull(cor.getDueDate());
    assertNull(cor.getFeeType());
    assertNull(cor.getSecurityInhibit());
    assertNull(cor.getCurrencyType());
    assertEquals(feeAmount, cor.getFeeAmount());
    assertNull(cor.getMediaType());
    assertNull(cor.getItemProperties());
    assertNull(cor.getTransactionId());
    assertNull(cor.getScreenMessage());
    assertNull(cor.getPrintLine());
  }

  @Test
  void testGetMediaType() {
    final CheckoutResponse cor = builder().mediaType(mediaType).build();
    assertNull(cor.getOk());
    assertNull(cor.getRenewalOk());
    assertNull(cor.getMagneticMedia());
    assertNull(cor.getDesensitize());
    assertNull(cor.getTransactionDate());
    assertNull(cor.getInstitutionId());
    assertNull(cor.getPatronIdentifier());
    assertNull(cor.getItemIdentifier());
    assertNull(cor.getTitleIdentifier());
    assertNull(cor.getDueDate());
    assertNull(cor.getFeeType());
    assertNull(cor.getSecurityInhibit());
    assertNull(cor.getCurrencyType());
    assertNull(cor.getFeeAmount());
    assertEquals(mediaType, cor.getMediaType());
    assertNull(cor.getItemProperties());
    assertNull(cor.getTransactionId());
    assertNull(cor.getScreenMessage());
    assertNull(cor.getPrintLine());
  }

  @Test
  void testGetItemProperties() {
    final CheckoutResponse cor = builder()
        .itemProperties(itemProperties)
        .build();
    assertNull(cor.getOk());
    assertNull(cor.getRenewalOk());
    assertNull(cor.getMagneticMedia());
    assertNull(cor.getDesensitize());
    assertNull(cor.getTransactionDate());
    assertNull(cor.getInstitutionId());
    assertNull(cor.getPatronIdentifier());
    assertNull(cor.getItemIdentifier());
    assertNull(cor.getTitleIdentifier());
    assertNull(cor.getDueDate());
    assertNull(cor.getFeeType());
    assertNull(cor.getSecurityInhibit());
    assertNull(cor.getCurrencyType());
    assertNull(cor.getFeeAmount());
    assertNull(cor.getMediaType());
    assertEquals(itemProperties, cor.getItemProperties());
    assertNull(cor.getTransactionId());
    assertNull(cor.getScreenMessage());
    assertNull(cor.getPrintLine());
  }

  @Test
  void testGetTransactionId() {
    final CheckoutResponse cor = builder()
        .transactionId(transactionId)
        .build();
    assertNull(cor.getOk());
    assertNull(cor.getRenewalOk());
    assertNull(cor.getMagneticMedia());
    assertNull(cor.getDesensitize());
    assertNull(cor.getTransactionDate());
    assertNull(cor.getInstitutionId());
    assertNull(cor.getPatronIdentifier());
    assertNull(cor.getItemIdentifier());
    assertNull(cor.getTitleIdentifier());
    assertNull(cor.getDueDate());
    assertNull(cor.getFeeType());
    assertNull(cor.getSecurityInhibit());
    assertNull(cor.getCurrencyType());
    assertNull(cor.getFeeAmount());
    assertNull(cor.getMediaType());
    assertNull(cor.getItemProperties());
    assertEquals(transactionId, cor.getTransactionId());
    assertNull(cor.getScreenMessage());
    assertNull(cor.getPrintLine());
  }

  @Test
  void testGetScreenMessage() {
    final CheckoutResponse cor = builder().screenMessage(screenMessage).build();
    assertNull(cor.getOk());
    assertNull(cor.getRenewalOk());
    assertNull(cor.getMagneticMedia());
    assertNull(cor.getDesensitize());
    assertNull(cor.getTransactionDate());
    assertNull(cor.getInstitutionId());
    assertNull(cor.getPatronIdentifier());
    assertNull(cor.getItemIdentifier());
    assertNull(cor.getTitleIdentifier());
    assertNull(cor.getDueDate());
    assertNull(cor.getFeeType());
    assertNull(cor.getSecurityInhibit());
    assertNull(cor.getCurrencyType());
    assertNull(cor.getFeeAmount());
    assertNull(cor.getMediaType());
    assertNull(cor.getItemProperties());
    assertNull(cor.getTransactionId());
    assertEquals(screenMessage, cor.getScreenMessage());
    assertNull(cor.getPrintLine());
  }

  @Test
  void testGetPrintLine() {
    final CheckoutResponse cor = builder().printLine(printLine).build();
    assertNull(cor.getOk());
    assertNull(cor.getRenewalOk());
    assertNull(cor.getMagneticMedia());
    assertNull(cor.getDesensitize());
    assertNull(cor.getTransactionDate());
    assertNull(cor.getInstitutionId());
    assertNull(cor.getPatronIdentifier());
    assertNull(cor.getItemIdentifier());
    assertNull(cor.getTitleIdentifier());
    assertNull(cor.getDueDate());
    assertNull(cor.getFeeType());
    assertNull(cor.getSecurityInhibit());
    assertNull(cor.getCurrencyType());
    assertNull(cor.getFeeAmount());
    assertNull(cor.getMediaType());
    assertNull(cor.getItemProperties());
    assertNull(cor.getTransactionId());
    assertNull(cor.getScreenMessage());
    assertEquals(printLine, cor.getPrintLine());
  }

  @Test
  void testCompleteCheckoutResponse() {
    final CheckoutResponse cor = builder()
        .ok(ok)
        .renewalOk(renewalOk)
        .magneticMedia(magneticMedia)
        .desensitize(desensitize)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .dueDate(dueDate)
        .feeType(feeType)
        .securityInhibit(securityInhibit)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .mediaType(mediaType)
        .itemProperties(itemProperties)
        .transactionId(transactionId)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertAll("CheckoutResponse",
        () -> assertEquals(ok, cor.getOk()),
        () -> assertEquals(renewalOk, cor.getRenewalOk()),
        () -> assertEquals(magneticMedia, cor.getMagneticMedia()),
        () -> assertEquals(desensitize, cor.getDesensitize()),
        () -> assertEquals(transactionDate, cor.getTransactionDate()),
        () -> assertEquals(institutionId, cor.getInstitutionId()),
        () -> assertEquals(patronIdentifier, cor.getPatronIdentifier()),
        () -> assertEquals(itemIdentifier, cor.getItemIdentifier()),
        () -> assertEquals(titleIdentifier, cor.getTitleIdentifier()),
        () -> assertEquals(feeType, cor.getFeeType()),
        () -> assertEquals(securityInhibit, cor.getSecurityInhibit()),
        () -> assertEquals(currencyType, cor.getCurrencyType()),
        () -> assertEquals(feeAmount, cor.getFeeAmount()),
        () -> assertEquals(mediaType, cor.getMediaType()),
        () -> assertEquals(itemProperties, cor.getItemProperties()),
        () -> assertEquals(transactionId, cor.getTransactionId()),
        () -> assertEquals(screenMessage, cor.getScreenMessage()),
        () -> assertEquals(printLine, cor.getPrintLine())
    );
  }

  @Test
  void testEqualsObject() {
    final CheckoutResponse cor1 = builder()
        .ok(ok)
        .renewalOk(renewalOk)
        .magneticMedia(magneticMedia)
        .desensitize(desensitize)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .dueDate(dueDate)
        .feeType(feeType)
        .securityInhibit(securityInhibit)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .mediaType(mediaType)
        .itemProperties(itemProperties)
        .transactionId(transactionId)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    final CheckoutResponse cor2 = builder()
        .ok(ok)
        .renewalOk(renewalOk)
        .magneticMedia(magneticMedia)
        .desensitize(desensitize)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .dueDate(dueDate)
        .feeType(feeType)
        .securityInhibit(securityInhibit)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .mediaType(mediaType)
        .itemProperties(itemProperties)
        .transactionId(transactionId)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertTrue(cor1.equals(cor2));
    assertTrue(cor1.equals(cor2));
  }

  @Test
  void testNotEqualsObject() {
    final CheckoutResponse cor1 = builder()
        .ok(ok)
        .renewalOk(renewalOk)
        .magneticMedia(magneticMedia)
        .desensitize(desensitize)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .dueDate(dueDate)
        .feeType(feeType)
        .securityInhibit(securityInhibit)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .mediaType(mediaType)
        .itemProperties(itemProperties)
        .transactionId(transactionId)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    final CheckoutResponse cor2 = builder()
        .ok(FALSE)
        .renewalOk(FALSE)
        .magneticMedia(TRUE)
        .desensitize(FALSE)
        .transactionDate(ZonedDateTime.now())
        .institutionId("xyzzy")
        .patronIdentifier("111111111")
        .itemIdentifier("222222222")
        .titleIdentifier("ou812")
        .dueDate(ZonedDateTime.now().plusDays(5))
        .feeType(DAMAGE)
        .securityInhibit(TRUE)
        .currencyType(GBP)
        .feeAmount("125.00")
        .mediaType(OTHER)
        .itemProperties("Testing")
        .transactionId("000000000000000000000")
        .screenMessage(asList("This is a test"))
        .printLine(asList("This is a print test"))
        .build();
    assertFalse(cor1.equals(cor2));
    assertFalse(cor1.equals(cor2));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("CheckoutResponse [ok=").append(ok)
        .append(", renewalOk=").append(renewalOk)
        .append(", magneticMedia=").append(magneticMedia)
        .append(", desensitize=").append(desensitize)
        .append(", transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", itemIdentifier=").append(itemIdentifier)
        .append(", titleIdentifier=").append(titleIdentifier)
        .append(", dueDate=").append(dueDate)
        .append(", feeType=").append(feeType)
        .append(", securityInhibit=").append(securityInhibit)
        .append(", currencyType=").append(currencyType)
        .append(", feeAmount=").append(feeAmount)
        .append(", mediaType=").append(mediaType)
        .append(", itemProperties=").append(itemProperties)
        .append(", transactionId=").append(transactionId)
        .append(", screenMessage=").append(screenMessage)
        .append(", printLine=").append(printLine)
        .append(']').toString();
    final CheckoutResponse cor = builder()
        .ok(ok)
        .renewalOk(renewalOk)
        .magneticMedia(magneticMedia)
        .desensitize(desensitize)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .dueDate(dueDate)
        .feeType(feeType)
        .securityInhibit(securityInhibit)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .mediaType(mediaType)
        .itemProperties(itemProperties)
        .transactionId(transactionId)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertEquals(expectedString, cor.toString());
  }
}
