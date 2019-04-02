package org.folio.edge.sip2.domain.messages.requests;

import static org.folio.edge.sip2.domain.messages.requests.ItemStatusUpdate.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

class ItemStatusUpdateTests {
  final ZonedDateTime transactionDate = ZonedDateTime.now();
  final String institutionId = "diku";
  final String itemIdentifier = "1234567890";
  final String terminalPassword = "12345";
  final String patronPassword = "2112";
  final String itemProperties = "A book";

  @Test
  void testGetTransactionDate() {
    final ItemStatusUpdate isu = builder()
        .transactionDate(transactionDate)
        .build();
    assertEquals(transactionDate, isu.getTransactionDate());
    assertNull(isu.getInstitutionId());
    assertNull(isu.getItemIdentifier());
    assertNull(isu.getTerminalPassword());
    assertNull(isu.getItemProperties());
  }

  @Test
  void testGetInstitutionId() {
    final ItemStatusUpdate isu = builder().institutionId(institutionId).build();
    assertNull(isu.getTransactionDate());
    assertEquals(institutionId, isu.getInstitutionId());
    assertNull(isu.getItemIdentifier());
    assertNull(isu.getTerminalPassword());
    assertNull(isu.getItemProperties());
  }

  @Test
  void testGetItemIdentifier() {
    final ItemStatusUpdate isu = builder().itemIdentifier(itemIdentifier).build();
    assertNull(isu.getTransactionDate());
    assertNull(isu.getInstitutionId());
    assertEquals(itemIdentifier, isu.getItemIdentifier());
    assertNull(isu.getTerminalPassword());
    assertNull(isu.getItemProperties());
  }

  @Test
  void testGetTerminalPassword() {
    final ItemStatusUpdate isu = builder()
        .terminalPassword(terminalPassword)
        .build();
    assertNull(isu.getTransactionDate());
    assertNull(isu.getInstitutionId());
    assertNull(isu.getItemIdentifier());
    assertEquals(terminalPassword, isu.getTerminalPassword());
    assertNull(isu.getItemProperties());
  }

  @Test
  void testGetItemProperties() {
    final ItemStatusUpdate isu = builder()
        .itemProperties(itemProperties)
        .build();
    assertNull(isu.getTransactionDate());
    assertNull(isu.getInstitutionId());
    assertNull(isu.getItemIdentifier());
    assertNull(isu.getTerminalPassword());
    assertEquals(itemProperties, isu.getItemProperties());
  }

  @Test
  void testCompleteItemStatusUpdate() {
    final ItemStatusUpdate isu = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .itemIdentifier(itemIdentifier)
        .terminalPassword(terminalPassword)
        .itemProperties(itemProperties)
        .build();
    assertAll("ItemStatusUpdate",
        () -> assertEquals(transactionDate, isu.getTransactionDate()),
        () -> assertEquals(institutionId, isu.getInstitutionId()),
        () -> assertEquals(itemIdentifier, isu.getItemIdentifier()),
        () -> assertEquals(terminalPassword, isu.getTerminalPassword()),
        () -> assertEquals(itemProperties, isu.getItemProperties())
    );
  }

  @Test
  void testEquals() {
    final ItemStatusUpdate isu1 = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .itemIdentifier(itemIdentifier)
        .terminalPassword(terminalPassword)
        .itemProperties(itemProperties)
        .build();
    final ItemStatusUpdate isu2 = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .itemIdentifier(itemIdentifier)
        .terminalPassword(terminalPassword)
        .itemProperties(itemProperties)
        .build();
    assertTrue(isu1.equals(isu2));
    assertTrue(isu2.equals(isu1));
  }

  @Test
  void testNotEquals() {
    final ItemStatusUpdate isu1 = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .itemIdentifier(itemIdentifier)
        .terminalPassword(terminalPassword)
        .itemProperties(itemProperties)
        .build();
    final ItemStatusUpdate isu2 = builder()
        .transactionDate(ZonedDateTime.now())
        .institutionId("test")
        .itemIdentifier("0987654321")
        .terminalPassword("0000")
        .itemProperties("A CD")
        .build();
    assertFalse(isu1.equals(isu2));
    assertFalse(isu2.equals(isu1));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("ItemStatusUpdate [transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", itemIdentifier=").append(itemIdentifier)
        .append(", terminalPassword=").append(terminalPassword)
        .append(", itemProperties=").append(itemProperties)
        .append(']').toString();
    final ItemStatusUpdate isu = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .itemIdentifier(itemIdentifier)
        .terminalPassword(terminalPassword)
        .itemProperties(itemProperties)
        .build();
    assertEquals(expectedString, isu.toString());
  }
}
