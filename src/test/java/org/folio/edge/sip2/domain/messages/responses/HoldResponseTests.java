package org.folio.edge.sip2.domain.messages.responses;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static org.folio.edge.sip2.domain.messages.responses.HoldResponse.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class HoldResponseTests {
  final Boolean ok = TRUE;
  final Boolean available = TRUE;
  final OffsetDateTime transactionDate = OffsetDateTime.now();
  final OffsetDateTime expirationDate = transactionDate.plusDays(30);
  final Integer queuePosition = Integer.valueOf(2);
  final String pickupLocation = "lobby";
  final String institutionId = "diku";
  final String patronIdentifier = "1234567890";
  final String itemIdentifier = "8675309";
  final String titleIdentifier = "5551212";
  final List<String> screenMessage = asList("Please rewind");
  final List<String> printLine = asList("Enjoy!");

  @Test
  void testGetOk() {
    final HoldResponse hr = builder().ok(ok).build();
    assertEquals(ok, hr.getOk());
    assertNull(hr.getAvailable());
    assertNull(hr.getTransactionDate());
    assertNull(hr.getExpirationDate());
    assertNull(hr.getQueuePosition());
    assertNull(hr.getPickupLocation());
    assertNull(hr.getInstitutionId());
    assertNull(hr.getPatronIdentifier());
    assertNull(hr.getItemIdentifier());
    assertNull(hr.getTitleIdentifier());
    assertNull(hr.getScreenMessage());
    assertNull(hr.getPrintLine());
  }

  @Test
  void testGetAvailable() {
    final HoldResponse hr = builder().available(available).build();
    assertNull(hr.getOk());
    assertEquals(available, hr.getAvailable());
    assertNull(hr.getTransactionDate());
    assertNull(hr.getExpirationDate());
    assertNull(hr.getQueuePosition());
    assertNull(hr.getPickupLocation());
    assertNull(hr.getInstitutionId());
    assertNull(hr.getPatronIdentifier());
    assertNull(hr.getItemIdentifier());
    assertNull(hr.getTitleIdentifier());
    assertNull(hr.getScreenMessage());
    assertNull(hr.getPrintLine());
  }

  @Test
  void testGetTransactionDate() {
    final HoldResponse hr = builder()
        .transactionDate(transactionDate)
        .build();
    assertNull(hr.getOk());
    assertNull(hr.getAvailable());
    assertEquals(transactionDate, hr.getTransactionDate());
    assertNull(hr.getExpirationDate());
    assertNull(hr.getQueuePosition());
    assertNull(hr.getPickupLocation());
    assertNull(hr.getInstitutionId());
    assertNull(hr.getPatronIdentifier());
    assertNull(hr.getItemIdentifier());
    assertNull(hr.getTitleIdentifier());
    assertNull(hr.getScreenMessage());
    assertNull(hr.getPrintLine());
  }

  @Test
  void testGetExpirationDate() {
    final HoldResponse hr = builder()
        .expirationDate(expirationDate)
        .build();
    assertNull(hr.getOk());
    assertNull(hr.getAvailable());
    assertNull(hr.getTransactionDate());
    assertEquals(expirationDate, hr.getExpirationDate());
    assertNull(hr.getQueuePosition());
    assertNull(hr.getPickupLocation());
    assertNull(hr.getInstitutionId());
    assertNull(hr.getPatronIdentifier());
    assertNull(hr.getItemIdentifier());
    assertNull(hr.getTitleIdentifier());
    assertNull(hr.getScreenMessage());
    assertNull(hr.getPrintLine());
  }

  @Test
  void testGetQueuePosition() {
    final HoldResponse hr = builder()
        .queuePosition(queuePosition)
        .build();
    assertNull(hr.getOk());
    assertNull(hr.getAvailable());
    assertNull(hr.getTransactionDate());
    assertNull(hr.getExpirationDate());
    assertEquals(queuePosition, hr.getQueuePosition());
    assertNull(hr.getPickupLocation());
    assertNull(hr.getInstitutionId());
    assertNull(hr.getPatronIdentifier());
    assertNull(hr.getItemIdentifier());
    assertNull(hr.getTitleIdentifier());
    assertNull(hr.getScreenMessage());
    assertNull(hr.getPrintLine());
  }

  @Test
  void testGetPickupLocation() {
    final HoldResponse hr = builder()
        .pickupLocation(pickupLocation)
        .build();
    assertNull(hr.getOk());
    assertNull(hr.getAvailable());
    assertNull(hr.getTransactionDate());
    assertNull(hr.getExpirationDate());
    assertNull(hr.getQueuePosition());
    assertEquals(pickupLocation, hr.getPickupLocation());
    assertNull(hr.getInstitutionId());
    assertNull(hr.getPatronIdentifier());
    assertNull(hr.getItemIdentifier());
    assertNull(hr.getTitleIdentifier());
    assertNull(hr.getScreenMessage());
    assertNull(hr.getPrintLine());
  }

  @Test
  void testGetInstitutionId() {
    final HoldResponse hr = builder().institutionId(institutionId).build();
    assertNull(hr.getOk());
    assertNull(hr.getAvailable());
    assertNull(hr.getTransactionDate());
    assertNull(hr.getExpirationDate());
    assertNull(hr.getQueuePosition());
    assertNull(hr.getPickupLocation());
    assertEquals(institutionId, hr.getInstitutionId());
    assertNull(hr.getPatronIdentifier());
    assertNull(hr.getItemIdentifier());
    assertNull(hr.getTitleIdentifier());
    assertNull(hr.getScreenMessage());
    assertNull(hr.getPrintLine());
  }

  @Test
  void testGetPatronIdentifier() {
    final HoldResponse hr = builder()
        .patronIdentifier(patronIdentifier)
        .build();
    assertNull(hr.getOk());
    assertNull(hr.getAvailable());
    assertNull(hr.getTransactionDate());
    assertNull(hr.getExpirationDate());
    assertNull(hr.getQueuePosition());
    assertNull(hr.getPickupLocation());
    assertNull(hr.getInstitutionId());
    assertEquals(patronIdentifier, hr.getPatronIdentifier());
    assertNull(hr.getItemIdentifier());
    assertNull(hr.getTitleIdentifier());
    assertNull(hr.getScreenMessage());
    assertNull(hr.getPrintLine());
  }

  @Test
  void testGetItemIdentifier() {
    final HoldResponse hr = builder().itemIdentifier(itemIdentifier).build();
    assertNull(hr.getOk());
    assertNull(hr.getAvailable());
    assertNull(hr.getTransactionDate());
    assertNull(hr.getExpirationDate());
    assertNull(hr.getQueuePosition());
    assertNull(hr.getPickupLocation());
    assertNull(hr.getInstitutionId());
    assertNull(hr.getPatronIdentifier());
    assertEquals(itemIdentifier, hr.getItemIdentifier());
    assertNull(hr.getTitleIdentifier());
    assertNull(hr.getScreenMessage());
    assertNull(hr.getPrintLine());
  }

  @Test
  void testGetTitleIdentifier() {
    final HoldResponse hr = builder().titleIdentifier(titleIdentifier).build();
    assertNull(hr.getOk());
    assertNull(hr.getAvailable());
    assertNull(hr.getTransactionDate());
    assertNull(hr.getExpirationDate());
    assertNull(hr.getQueuePosition());
    assertNull(hr.getPickupLocation());
    assertNull(hr.getInstitutionId());
    assertNull(hr.getPatronIdentifier());
    assertNull(hr.getItemIdentifier());
    assertEquals(titleIdentifier, hr.getTitleIdentifier());
    assertNull(hr.getScreenMessage());
    assertNull(hr.getPrintLine());
  }

  @Test
  void testGetScreenMessage() {
    final HoldResponse hr = builder().screenMessage(screenMessage).build();
    assertNull(hr.getOk());
    assertNull(hr.getAvailable());
    assertNull(hr.getTransactionDate());
    assertNull(hr.getExpirationDate());
    assertNull(hr.getQueuePosition());
    assertNull(hr.getPickupLocation());
    assertNull(hr.getInstitutionId());
    assertNull(hr.getPatronIdentifier());
    assertNull(hr.getItemIdentifier());
    assertNull(hr.getTitleIdentifier());
    assertEquals(screenMessage, hr.getScreenMessage());
    assertNull(hr.getPrintLine());
  }

  @Test
  void testGetPrintLine() {
    final HoldResponse hr = builder().printLine(printLine).build();
    assertNull(hr.getOk());
    assertNull(hr.getAvailable());
    assertNull(hr.getTransactionDate());
    assertNull(hr.getExpirationDate());
    assertNull(hr.getQueuePosition());
    assertNull(hr.getPickupLocation());
    assertNull(hr.getInstitutionId());
    assertNull(hr.getPatronIdentifier());
    assertNull(hr.getItemIdentifier());
    assertNull(hr.getTitleIdentifier());
    assertNull(hr.getScreenMessage());
    assertEquals(printLine, hr.getPrintLine());
  }

  @Test
  void testCompleteHoldResponse() {
    final HoldResponse hr = builder()
        .ok(ok)
        .available(available)
        .transactionDate(transactionDate)
        .expirationDate(expirationDate)
        .queuePosition(queuePosition)
        .pickupLocation(pickupLocation)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertAll("HoldResponse",
        () -> assertEquals(ok, hr.getOk()),
        () -> assertEquals(available, hr.getAvailable()),
        () -> assertEquals(transactionDate, hr.getTransactionDate()),
        () -> assertEquals(expirationDate, hr.getExpirationDate()),
        () -> assertEquals(queuePosition, hr.getQueuePosition()),
        () -> assertEquals(pickupLocation, hr.getPickupLocation()),
        () -> assertEquals(institutionId, hr.getInstitutionId()),
        () -> assertEquals(patronIdentifier, hr.getPatronIdentifier()),
        () -> assertEquals(itemIdentifier, hr.getItemIdentifier()),
        () -> assertEquals(titleIdentifier, hr.getTitleIdentifier()),
        () -> assertEquals(screenMessage, hr.getScreenMessage()),
        () -> assertEquals(printLine, hr.getPrintLine())
    );
  }

  @Test
  void testEqualsObject() {
    final HoldResponse hr1 = builder()
        .ok(ok)
        .available(available)
        .transactionDate(transactionDate)
        .expirationDate(expirationDate)
        .queuePosition(queuePosition)
        .pickupLocation(pickupLocation)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    final HoldResponse hr2 = builder()
        .ok(ok)
        .available(available)
        .transactionDate(transactionDate)
        .expirationDate(expirationDate)
        .queuePosition(queuePosition)
        .pickupLocation(pickupLocation)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertTrue(hr1.equals(hr2));
    assertTrue(hr1.equals(hr2));
  }

  @Test
  void testNotEqualsObject() {
    final HoldResponse hr1 = builder()
        .ok(ok)
        .available(available)
        .transactionDate(transactionDate)
        .expirationDate(expirationDate)
        .queuePosition(queuePosition)
        .pickupLocation(pickupLocation)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    final HoldResponse hr2 = builder()
        .ok(FALSE)
        .available(FALSE)
        .transactionDate(OffsetDateTime.now())
        .expirationDate(OffsetDateTime.now().plusDays(5))
        .queuePosition(10)
        .pickupLocation("mars")
        .institutionId("xyzzy")
        .patronIdentifier("111111111")
        .itemIdentifier("222222222")
        .titleIdentifier("ou812")
        .screenMessage(asList("This is a test"))
        .printLine(asList("This is a print test"))
        .build();
    assertFalse(hr1.equals(hr2));
    assertFalse(hr1.equals(hr2));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("HoldResponse [ok=").append(ok)
        .append(", available=").append(available)
        .append(", transactionDate=").append(transactionDate)
        .append(", expirationDate=").append(expirationDate)
        .append(", queuePosition=").append(queuePosition)
        .append(", pickupLocation=").append(pickupLocation)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", itemIdentifier=").append(itemIdentifier)
        .append(", titleIdentifier=").append(titleIdentifier)
        .append(", screenMessage=").append(screenMessage)
        .append(", printLine=").append(printLine)
        .append(']').toString();
    final HoldResponse hr = builder()
        .ok(ok)
        .available(available)
        .transactionDate(transactionDate)
        .expirationDate(expirationDate)
        .queuePosition(queuePosition)
        .pickupLocation(pickupLocation)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertEquals(expectedString, hr.toString());
  }
}
