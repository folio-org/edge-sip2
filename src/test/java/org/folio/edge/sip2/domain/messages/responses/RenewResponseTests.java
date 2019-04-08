package org.folio.edge.sip2.domain.messages.responses;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.folio.edge.sip2.domain.messages.enumerations.CurrencyType.GBP;
import static org.folio.edge.sip2.domain.messages.enumerations.CurrencyType.USD;
import static org.folio.edge.sip2.domain.messages.enumerations.FeeType.DAMAGE;
import static org.folio.edge.sip2.domain.messages.enumerations.FeeType.RENTAL;
import static org.folio.edge.sip2.domain.messages.enumerations.MediaType.OTHER;
import static org.folio.edge.sip2.domain.messages.enumerations.MediaType.VIDEO_TAPE;
import static org.folio.edge.sip2.domain.messages.responses.RenewResponse.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;

import org.folio.edge.sip2.domain.messages.enumerations.CurrencyType;
import org.folio.edge.sip2.domain.messages.enumerations.FeeType;
import org.folio.edge.sip2.domain.messages.enumerations.MediaType;
import org.junit.jupiter.api.Test;

class RenewResponseTests {
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
  final String screenMessage = "Please rewind";
  final static String printLine = "Enjoy!";

  @Test
  void testGetOk() {
    final RenewResponse rr = builder().ok(ok).build();
    assertEquals(ok, rr.getOk());
    assertNull(rr.getRenewalOk());
    assertNull(rr.getMagneticMedia());
    assertNull(rr.getDesensitize());
    assertNull(rr.getTransactionDate());
    assertNull(rr.getInstitutionId());
    assertNull(rr.getPatronIdentifier());
    assertNull(rr.getItemIdentifier());
    assertNull(rr.getTitleIdentifier());
    assertNull(rr.getDueDate());
    assertNull(rr.getFeeType());
    assertNull(rr.getSecurityInhibit());
    assertNull(rr.getCurrencyType());
    assertNull(rr.getFeeAmount());
    assertNull(rr.getMediaType());
    assertNull(rr.getItemProperties());
    assertNull(rr.getTransactionId());
    assertNull(rr.getScreenMessage());
    assertNull(rr.getPrintLine());
  }

  @Test
  void testGetRenewalOk() {
    final RenewResponse rr = builder().renewalOk(renewalOk).build();
    assertNull(rr.getOk());
    assertEquals(renewalOk, rr.getRenewalOk());
    assertNull(rr.getMagneticMedia());
    assertNull(rr.getDesensitize());
    assertNull(rr.getTransactionDate());
    assertNull(rr.getInstitutionId());
    assertNull(rr.getPatronIdentifier());
    assertNull(rr.getItemIdentifier());
    assertNull(rr.getTitleIdentifier());
    assertNull(rr.getDueDate());
    assertNull(rr.getFeeType());
    assertNull(rr.getSecurityInhibit());
    assertNull(rr.getCurrencyType());
    assertNull(rr.getFeeAmount());
    assertNull(rr.getMediaType());
    assertNull(rr.getItemProperties());
    assertNull(rr.getTransactionId());
    assertNull(rr.getScreenMessage());
    assertNull(rr.getPrintLine());
  }

  @Test
  void testGetMagneticMedia() {
    final RenewResponse rr = builder().magneticMedia(magneticMedia).build();
    assertNull(rr.getOk());
    assertNull(rr.getRenewalOk());
    assertEquals(magneticMedia, rr.getMagneticMedia());
    assertNull(rr.getDesensitize());
    assertNull(rr.getTransactionDate());
    assertNull(rr.getInstitutionId());
    assertNull(rr.getPatronIdentifier());
    assertNull(rr.getItemIdentifier());
    assertNull(rr.getTitleIdentifier());
    assertNull(rr.getDueDate());
    assertNull(rr.getFeeType());
    assertNull(rr.getSecurityInhibit());
    assertNull(rr.getCurrencyType());
    assertNull(rr.getFeeAmount());
    assertNull(rr.getMediaType());
    assertNull(rr.getItemProperties());
    assertNull(rr.getTransactionId());
    assertNull(rr.getScreenMessage());
    assertNull(rr.getPrintLine());
  }

  @Test
  void testGetDesensitize() {
    final RenewResponse rr = builder().desensitize(desensitize).build();
    assertNull(rr.getOk());
    assertNull(rr.getRenewalOk());
    assertNull(rr.getMagneticMedia());
    assertEquals(desensitize, rr.getDesensitize());
    assertNull(rr.getTransactionDate());
    assertNull(rr.getInstitutionId());
    assertNull(rr.getPatronIdentifier());
    assertNull(rr.getItemIdentifier());
    assertNull(rr.getTitleIdentifier());
    assertNull(rr.getDueDate());
    assertNull(rr.getFeeType());
    assertNull(rr.getSecurityInhibit());
    assertNull(rr.getCurrencyType());
    assertNull(rr.getFeeAmount());
    assertNull(rr.getMediaType());
    assertNull(rr.getItemProperties());
    assertNull(rr.getTransactionId());
    assertNull(rr.getScreenMessage());
    assertNull(rr.getPrintLine());
  }

  @Test
  void testGetTransactionDate() {
    final RenewResponse rr = builder()
        .transactionDate(transactionDate)
        .build();
    assertNull(rr.getOk());
    assertNull(rr.getRenewalOk());
    assertNull(rr.getMagneticMedia());
    assertNull(rr.getDesensitize());
    assertEquals(transactionDate, rr.getTransactionDate());
    assertNull(rr.getInstitutionId());
    assertNull(rr.getPatronIdentifier());
    assertNull(rr.getItemIdentifier());
    assertNull(rr.getTitleIdentifier());
    assertNull(rr.getDueDate());
    assertNull(rr.getFeeType());
    assertNull(rr.getSecurityInhibit());
    assertNull(rr.getCurrencyType());
    assertNull(rr.getFeeAmount());
    assertNull(rr.getMediaType());
    assertNull(rr.getItemProperties());
    assertNull(rr.getTransactionId());
    assertNull(rr.getScreenMessage());
    assertNull(rr.getPrintLine());
  }

  @Test
  void testGetInstitutionId() {
    final RenewResponse rr = builder().institutionId(institutionId).build();
    assertNull(rr.getOk());
    assertNull(rr.getRenewalOk());
    assertNull(rr.getMagneticMedia());
    assertNull(rr.getDesensitize());
    assertNull(rr.getTransactionDate());
    assertEquals(institutionId, rr.getInstitutionId());
    assertNull(rr.getPatronIdentifier());
    assertNull(rr.getItemIdentifier());
    assertNull(rr.getTitleIdentifier());
    assertNull(rr.getDueDate());
    assertNull(rr.getFeeType());
    assertNull(rr.getSecurityInhibit());
    assertNull(rr.getCurrencyType());
    assertNull(rr.getFeeAmount());
    assertNull(rr.getMediaType());
    assertNull(rr.getItemProperties());
    assertNull(rr.getTransactionId());
    assertNull(rr.getScreenMessage());
    assertNull(rr.getPrintLine());
  }

  @Test
  void testGetPatronIdentifier() {
    final RenewResponse rr = builder()
        .patronIdentifier(patronIdentifier)
        .build();
    assertNull(rr.getOk());
    assertNull(rr.getRenewalOk());
    assertNull(rr.getMagneticMedia());
    assertNull(rr.getDesensitize());
    assertNull(rr.getTransactionDate());
    assertNull(rr.getInstitutionId());
    assertEquals(patronIdentifier, rr.getPatronIdentifier());
    assertNull(rr.getItemIdentifier());
    assertNull(rr.getTitleIdentifier());
    assertNull(rr.getDueDate());
    assertNull(rr.getFeeType());
    assertNull(rr.getSecurityInhibit());
    assertNull(rr.getCurrencyType());
    assertNull(rr.getFeeAmount());
    assertNull(rr.getMediaType());
    assertNull(rr.getItemProperties());
    assertNull(rr.getTransactionId());
    assertNull(rr.getScreenMessage());
    assertNull(rr.getPrintLine());
  }

  @Test
  void testGetItemIdentifier() {
    final RenewResponse rr = builder()
        .itemIdentifier(itemIdentifier)
        .build();
    assertNull(rr.getOk());
    assertNull(rr.getRenewalOk());
    assertNull(rr.getMagneticMedia());
    assertNull(rr.getDesensitize());
    assertNull(rr.getTransactionDate());
    assertNull(rr.getInstitutionId());
    assertNull(rr.getPatronIdentifier());
    assertEquals(itemIdentifier, rr.getItemIdentifier());
    assertNull(rr.getTitleIdentifier());
    assertNull(rr.getDueDate());
    assertNull(rr.getFeeType());
    assertNull(rr.getSecurityInhibit());
    assertNull(rr.getCurrencyType());
    assertNull(rr.getFeeAmount());
    assertNull(rr.getMediaType());
    assertNull(rr.getItemProperties());
    assertNull(rr.getTransactionId());
    assertNull(rr.getScreenMessage());
    assertNull(rr.getPrintLine());
  }

  @Test
  void testGetTitleIdentifier() {
    final RenewResponse rr = builder()
        .titleIdentifier(titleIdentifier)
        .build();
    assertNull(rr.getOk());
    assertNull(rr.getRenewalOk());
    assertNull(rr.getMagneticMedia());
    assertNull(rr.getDesensitize());
    assertNull(rr.getTransactionDate());
    assertNull(rr.getInstitutionId());
    assertNull(rr.getPatronIdentifier());
    assertNull(rr.getItemIdentifier());
    assertEquals(titleIdentifier, rr.getTitleIdentifier());
    assertNull(rr.getDueDate());
    assertNull(rr.getFeeType());
    assertNull(rr.getSecurityInhibit());
    assertNull(rr.getCurrencyType());
    assertNull(rr.getFeeAmount());
    assertNull(rr.getMediaType());
    assertNull(rr.getItemProperties());
    assertNull(rr.getTransactionId());
    assertNull(rr.getScreenMessage());
    assertNull(rr.getPrintLine());
  }

  @Test
  void testGetDueDate() {
    final RenewResponse rr = builder().dueDate(dueDate).build();
    assertNull(rr.getOk());
    assertNull(rr.getRenewalOk());
    assertNull(rr.getMagneticMedia());
    assertNull(rr.getDesensitize());
    assertNull(rr.getTransactionDate());
    assertNull(rr.getInstitutionId());
    assertNull(rr.getPatronIdentifier());
    assertNull(rr.getItemIdentifier());
    assertNull(rr.getTitleIdentifier());
    assertEquals(dueDate, rr.getDueDate());
    assertNull(rr.getFeeType());
    assertNull(rr.getSecurityInhibit());
    assertNull(rr.getCurrencyType());
    assertNull(rr.getFeeAmount());
    assertNull(rr.getMediaType());
    assertNull(rr.getItemProperties());
    assertNull(rr.getTransactionId());
    assertNull(rr.getScreenMessage());
    assertNull(rr.getPrintLine());
  }

  @Test
  void testGetFeeType() {
    final RenewResponse rr = builder().feeType(feeType).build();
    assertNull(rr.getOk());
    assertNull(rr.getRenewalOk());
    assertNull(rr.getMagneticMedia());
    assertNull(rr.getDesensitize());
    assertNull(rr.getTransactionDate());
    assertNull(rr.getInstitutionId());
    assertNull(rr.getPatronIdentifier());
    assertNull(rr.getItemIdentifier());
    assertNull(rr.getTitleIdentifier());
    assertNull(rr.getDueDate());
    assertEquals(feeType, rr.getFeeType());
    assertNull(rr.getSecurityInhibit());
    assertNull(rr.getCurrencyType());
    assertNull(rr.getFeeAmount());
    assertNull(rr.getMediaType());
    assertNull(rr.getItemProperties());
    assertNull(rr.getTransactionId());
    assertNull(rr.getScreenMessage());
    assertNull(rr.getPrintLine());
  }

  @Test
  void testGetSecurityInhibit() {
    final RenewResponse rr = builder()
        .securityInhibit(securityInhibit)
        .build();
    assertNull(rr.getOk());
    assertNull(rr.getRenewalOk());
    assertNull(rr.getMagneticMedia());
    assertNull(rr.getDesensitize());
    assertNull(rr.getTransactionDate());
    assertNull(rr.getInstitutionId());
    assertNull(rr.getPatronIdentifier());
    assertNull(rr.getItemIdentifier());
    assertNull(rr.getTitleIdentifier());
    assertNull(rr.getDueDate());
    assertNull(rr.getFeeType());
    assertEquals(securityInhibit, rr.getSecurityInhibit());
    assertNull(rr.getFeeAmount());
    assertNull(rr.getMediaType());
    assertNull(rr.getItemProperties());
    assertNull(rr.getTransactionId());
    assertNull(rr.getScreenMessage());
    assertNull(rr.getPrintLine());
  }

  @Test
  void testGetCurrencyType() {
    final RenewResponse rr = builder().currencyType(currencyType).build();
    assertNull(rr.getOk());
    assertNull(rr.getRenewalOk());
    assertNull(rr.getMagneticMedia());
    assertNull(rr.getDesensitize());
    assertNull(rr.getTransactionDate());
    assertNull(rr.getInstitutionId());
    assertNull(rr.getPatronIdentifier());
    assertNull(rr.getItemIdentifier());
    assertNull(rr.getTitleIdentifier());
    assertNull(rr.getDueDate());
    assertNull(rr.getFeeType());
    assertNull(rr.getSecurityInhibit());
    assertEquals(currencyType, rr.getCurrencyType());
    assertNull(rr.getFeeAmount());
    assertNull(rr.getMediaType());
    assertNull(rr.getItemProperties());
    assertNull(rr.getTransactionId());
    assertNull(rr.getScreenMessage());
    assertNull(rr.getPrintLine());
  }

  @Test
  void testGetFeeAmount() {
    final RenewResponse rr = builder().feeAmount(feeAmount).build();
    assertNull(rr.getOk());
    assertNull(rr.getRenewalOk());
    assertNull(rr.getMagneticMedia());
    assertNull(rr.getDesensitize());
    assertNull(rr.getTransactionDate());
    assertNull(rr.getInstitutionId());
    assertNull(rr.getPatronIdentifier());
    assertNull(rr.getItemIdentifier());
    assertNull(rr.getTitleIdentifier());
    assertNull(rr.getDueDate());
    assertNull(rr.getFeeType());
    assertNull(rr.getSecurityInhibit());
    assertNull(rr.getCurrencyType());
    assertEquals(feeAmount, rr.getFeeAmount());
    assertNull(rr.getMediaType());
    assertNull(rr.getItemProperties());
    assertNull(rr.getTransactionId());
    assertNull(rr.getScreenMessage());
    assertNull(rr.getPrintLine());
  }

  @Test
  void testGetMediaType() {
    final RenewResponse rr = builder().mediaType(mediaType).build();
    assertNull(rr.getOk());
    assertNull(rr.getRenewalOk());
    assertNull(rr.getMagneticMedia());
    assertNull(rr.getDesensitize());
    assertNull(rr.getTransactionDate());
    assertNull(rr.getInstitutionId());
    assertNull(rr.getPatronIdentifier());
    assertNull(rr.getItemIdentifier());
    assertNull(rr.getTitleIdentifier());
    assertNull(rr.getDueDate());
    assertNull(rr.getFeeType());
    assertNull(rr.getSecurityInhibit());
    assertNull(rr.getCurrencyType());
    assertNull(rr.getFeeAmount());
    assertEquals(mediaType, rr.getMediaType());
    assertNull(rr.getItemProperties());
    assertNull(rr.getTransactionId());
    assertNull(rr.getScreenMessage());
    assertNull(rr.getPrintLine());
  }

  @Test
  void testGetItemProperties() {
    final RenewResponse rr = builder()
        .itemProperties(itemProperties)
        .build();
    assertNull(rr.getOk());
    assertNull(rr.getRenewalOk());
    assertNull(rr.getMagneticMedia());
    assertNull(rr.getDesensitize());
    assertNull(rr.getTransactionDate());
    assertNull(rr.getInstitutionId());
    assertNull(rr.getPatronIdentifier());
    assertNull(rr.getItemIdentifier());
    assertNull(rr.getTitleIdentifier());
    assertNull(rr.getDueDate());
    assertNull(rr.getFeeType());
    assertNull(rr.getSecurityInhibit());
    assertNull(rr.getCurrencyType());
    assertNull(rr.getFeeAmount());
    assertNull(rr.getMediaType());
    assertEquals(itemProperties, rr.getItemProperties());
    assertNull(rr.getTransactionId());
    assertNull(rr.getScreenMessage());
    assertNull(rr.getPrintLine());
  }

  @Test
  void testGetTransactionId() {
    final RenewResponse rr = builder()
        .transactionId(transactionId)
        .build();
    assertNull(rr.getOk());
    assertNull(rr.getRenewalOk());
    assertNull(rr.getMagneticMedia());
    assertNull(rr.getDesensitize());
    assertNull(rr.getTransactionDate());
    assertNull(rr.getInstitutionId());
    assertNull(rr.getPatronIdentifier());
    assertNull(rr.getItemIdentifier());
    assertNull(rr.getTitleIdentifier());
    assertNull(rr.getDueDate());
    assertNull(rr.getFeeType());
    assertNull(rr.getSecurityInhibit());
    assertNull(rr.getCurrencyType());
    assertNull(rr.getFeeAmount());
    assertNull(rr.getMediaType());
    assertNull(rr.getItemProperties());
    assertEquals(transactionId, rr.getTransactionId());
    assertNull(rr.getScreenMessage());
    assertNull(rr.getPrintLine());
  }

  @Test
  void testGetScreenMessage() {
    final RenewResponse rr = builder().screenMessage(screenMessage).build();
    assertNull(rr.getOk());
    assertNull(rr.getRenewalOk());
    assertNull(rr.getMagneticMedia());
    assertNull(rr.getDesensitize());
    assertNull(rr.getTransactionDate());
    assertNull(rr.getInstitutionId());
    assertNull(rr.getPatronIdentifier());
    assertNull(rr.getItemIdentifier());
    assertNull(rr.getTitleIdentifier());
    assertNull(rr.getDueDate());
    assertNull(rr.getFeeType());
    assertNull(rr.getSecurityInhibit());
    assertNull(rr.getCurrencyType());
    assertNull(rr.getFeeAmount());
    assertNull(rr.getMediaType());
    assertNull(rr.getItemProperties());
    assertNull(rr.getTransactionId());
    assertEquals(screenMessage, rr.getScreenMessage());
    assertNull(rr.getPrintLine());
  }

  @Test
  void testGetPrintLine() {
    final RenewResponse rr = builder().printLine(printLine).build();
    assertNull(rr.getOk());
    assertNull(rr.getRenewalOk());
    assertNull(rr.getMagneticMedia());
    assertNull(rr.getDesensitize());
    assertNull(rr.getTransactionDate());
    assertNull(rr.getInstitutionId());
    assertNull(rr.getPatronIdentifier());
    assertNull(rr.getItemIdentifier());
    assertNull(rr.getTitleIdentifier());
    assertNull(rr.getDueDate());
    assertNull(rr.getFeeType());
    assertNull(rr.getSecurityInhibit());
    assertNull(rr.getCurrencyType());
    assertNull(rr.getFeeAmount());
    assertNull(rr.getMediaType());
    assertNull(rr.getItemProperties());
    assertNull(rr.getTransactionId());
    assertNull(rr.getScreenMessage());
    assertEquals(printLine, rr.getPrintLine());
  }

  @Test
  void testCompleteRenewResponse() {
    final RenewResponse rr = builder()
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
    assertAll("RenewResponse",
        () -> assertEquals(ok, rr.getOk()),
        () -> assertEquals(renewalOk, rr.getRenewalOk()),
        () -> assertEquals(magneticMedia, rr.getMagneticMedia()),
        () -> assertEquals(desensitize, rr.getDesensitize()),
        () -> assertEquals(transactionDate, rr.getTransactionDate()),
        () -> assertEquals(institutionId, rr.getInstitutionId()),
        () -> assertEquals(patronIdentifier, rr.getPatronIdentifier()),
        () -> assertEquals(itemIdentifier, rr.getItemIdentifier()),
        () -> assertEquals(titleIdentifier, rr.getTitleIdentifier()),
        () -> assertEquals(feeType, rr.getFeeType()),
        () -> assertEquals(securityInhibit, rr.getSecurityInhibit()),
        () -> assertEquals(currencyType, rr.getCurrencyType()),
        () -> assertEquals(feeAmount, rr.getFeeAmount()),
        () -> assertEquals(mediaType, rr.getMediaType()),
        () -> assertEquals(itemProperties, rr.getItemProperties()),
        () -> assertEquals(transactionId, rr.getTransactionId()),
        () -> assertEquals(screenMessage, rr.getScreenMessage()),
        () -> assertEquals(printLine, rr.getPrintLine())
    );
  }

  @Test
  void testEqualsObject() {
    final RenewResponse rr1 = builder()
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
    final RenewResponse rr2 = builder()
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
    assertTrue(rr1.equals(rr2));
    assertTrue(rr1.equals(rr2));
  }

  @Test
  void testNotEqualsObject() {
    final RenewResponse rr1 = builder()
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
    final RenewResponse rr2 = builder()
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
        .screenMessage("This is a test")
        .printLine("This is a print test")
        .build();
    assertFalse(rr1.equals(rr2));
    assertFalse(rr1.equals(rr2));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("RenewResponse [ok=").append(ok)
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
    final RenewResponse rr = builder()
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
    assertEquals(expectedString, rr.toString());
  }
}
