package org.folio.edge.sip2.domain.messages.responses;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.folio.edge.sip2.domain.messages.responses.ItemStatusUpdateResponse.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

class ItemStatusUpdateResponseTests {
  final Boolean itemPropertiesOk = TRUE;
  final ZonedDateTime transactionDate = ZonedDateTime.now();
  final String itemIdentifier = "8675309";
  final String titleIdentifier = "5551212";
  final String itemProperties = "Directors Cut";
  final String screenMessage = "Please rewind";
  final String printLine = "Enjoy!";

  @Test
  void testGetItemPropertiesOk() {
    final ItemStatusUpdateResponse isur = builder()
        .itemPropertiesOk(itemPropertiesOk)
        .build();
    assertEquals(itemPropertiesOk, isur.getItemPropertiesOk());
    assertNull(isur.getTransactionDate());
    assertNull(isur.getItemIdentifier());
    assertNull(isur.getTitleIdentifier());
    assertNull(isur.getItemProperties());
    assertNull(isur.getScreenMessage());
    assertNull(isur.getPrintLine());
  }

  @Test
  void testGetTransactionDate() {
    final ItemStatusUpdateResponse isur = builder()
        .transactionDate(transactionDate)
        .build();
    assertNull(isur.getItemPropertiesOk());
    assertEquals(transactionDate, isur.getTransactionDate());
    assertNull(isur.getItemIdentifier());
    assertNull(isur.getTitleIdentifier());
    assertNull(isur.getItemProperties());
    assertNull(isur.getScreenMessage());
    assertNull(isur.getPrintLine());
  }

  @Test
  void testGetItemIdentifier() {
    final ItemStatusUpdateResponse isur = builder()
        .itemIdentifier(itemIdentifier)
        .build();
    assertNull(isur.getItemPropertiesOk());
    assertNull(isur.getTransactionDate());
    assertEquals(itemIdentifier, isur.getItemIdentifier());
    assertNull(isur.getTitleIdentifier());
    assertNull(isur.getItemProperties());
    assertNull(isur.getScreenMessage());
    assertNull(isur.getPrintLine());
  }

  @Test
  void testGetTitleIdentifier() {
    final ItemStatusUpdateResponse isur = builder()
        .titleIdentifier(titleIdentifier)
        .build();
    assertNull(isur.getItemPropertiesOk());
    assertNull(isur.getTransactionDate());
    assertNull(isur.getItemIdentifier());
    assertEquals(titleIdentifier, isur.getTitleIdentifier());
    assertNull(isur.getItemProperties());
    assertNull(isur.getScreenMessage());
    assertNull(isur.getPrintLine());
  }

  @Test
  void testGetItemProperties() {
    final ItemStatusUpdateResponse isur = builder()
        .itemProperties(itemProperties)
        .build();
    assertNull(isur.getItemPropertiesOk());
    assertNull(isur.getTransactionDate());
    assertNull(isur.getItemIdentifier());
    assertNull(isur.getTitleIdentifier());
    assertEquals(itemProperties, isur.getItemProperties());
    assertNull(isur.getScreenMessage());
    assertNull(isur.getPrintLine());
  }

  @Test
  void testGetScreenMessage() {
    final ItemStatusUpdateResponse isur = builder()
        .screenMessage(screenMessage)
        .build();
    assertNull(isur.getItemPropertiesOk());
    assertNull(isur.getTransactionDate());
    assertNull(isur.getItemIdentifier());
    assertNull(isur.getTitleIdentifier());
    assertNull(isur.getItemProperties());
    assertEquals(screenMessage, isur.getScreenMessage());
    assertNull(isur.getPrintLine());
  }

  @Test
  void testGetPrintLine() {
    final ItemStatusUpdateResponse isur = builder()
        .printLine(printLine)
        .build();
    assertNull(isur.getItemPropertiesOk());
    assertNull(isur.getTransactionDate());
    assertNull(isur.getItemIdentifier());
    assertNull(isur.getTitleIdentifier());
    assertNull(isur.getItemProperties());
    assertNull(isur.getScreenMessage());
    assertEquals(printLine, isur.getPrintLine());
  }

  @Test
  void testCompleteItemStatusUpdateResponse() {
    final ItemStatusUpdateResponse isur = builder()
        .itemPropertiesOk(itemPropertiesOk)
        .transactionDate(transactionDate)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .itemProperties(itemProperties)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertAll("ItemStatusUpdateResponse",
        () -> assertEquals(itemPropertiesOk, isur.getItemPropertiesOk()),
        () -> assertEquals(transactionDate, isur.getTransactionDate()),
        () -> assertEquals(itemIdentifier, isur.getItemIdentifier()),
        () -> assertEquals(titleIdentifier, isur.getTitleIdentifier()),
        () -> assertEquals(itemProperties, isur.getItemProperties()),
        () -> assertEquals(screenMessage, isur.getScreenMessage()),
        () -> assertEquals(printLine, isur.getPrintLine())
    );
  }

  @Test
  void testEqualsObject() {
    final ItemStatusUpdateResponse isur1 = builder()
        .itemPropertiesOk(itemPropertiesOk)
        .transactionDate(transactionDate)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .itemProperties(itemProperties)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    final ItemStatusUpdateResponse isur2 = builder()
        .itemPropertiesOk(itemPropertiesOk)
        .transactionDate(transactionDate)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .itemProperties(itemProperties)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertTrue(isur1.equals(isur2));
    assertTrue(isur1.equals(isur2));
  }

  @Test
  void testNotEqualsObject() {
    final ItemStatusUpdateResponse isur1 = builder()
        .itemPropertiesOk(itemPropertiesOk)
        .transactionDate(transactionDate)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .itemProperties(itemProperties)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    final ItemStatusUpdateResponse isur2 = builder()
        .itemPropertiesOk(FALSE)
        .transactionDate(ZonedDateTime.now())
        .itemIdentifier("222222222")
        .titleIdentifier("ou812")
        .itemProperties("Testing")
        .screenMessage("This is a test")
        .printLine("This is a print test")
        .build();
    assertFalse(isur1.equals(isur2));
    assertFalse(isur1.equals(isur2));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("ItemStatusUpdateResponse [itemPropertiesOk=")
        .append(itemPropertiesOk)
        .append(", transactionDate=").append(transactionDate)
        .append(", itemIdentifier=").append(itemIdentifier)
        .append(", titleIdentifier=").append(titleIdentifier)
        .append(", itemProperties=").append(itemProperties)
        .append(", screenMessage=").append(screenMessage)
        .append(", printLine=").append(printLine)
        .append(']').toString();
    final ItemStatusUpdateResponse isur = builder()
        .itemPropertiesOk(itemPropertiesOk)
        .transactionDate(transactionDate)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .itemProperties(itemProperties)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertEquals(expectedString, isur.toString());
  }
}
