package org.folio.edge.sip2.domain.messages.requests;

import static org.folio.edge.sip2.domain.messages.requests.ItemInformation.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class ItemInformationTests {
  final OffsetDateTime transactionDate = OffsetDateTime.now();
  final String institutionId = "diku";
  final String itemIdentifier = "1234567890";
  final String terminalPassword = "12345";
  final String patronPassword = "2112";

  @Test
  void testGetTransactionDate() {
    final ItemInformation ii = builder()
        .transactionDate(transactionDate)
        .build();
    assertEquals(transactionDate, ii.getTransactionDate());
    assertNull(ii.getInstitutionId());
    assertNull(ii.getItemIdentifier());
    assertNull(ii.getTerminalPassword());
  }

  @Test
  void testGetInstitutionId() {
    final ItemInformation ii = builder().institutionId(institutionId).build();
    assertNull(ii.getTransactionDate());
    assertEquals(institutionId, ii.getInstitutionId());
    assertNull(ii.getItemIdentifier());
    assertNull(ii.getTerminalPassword());
  }

  @Test
  void testGetItemIdentifier() {
    final ItemInformation ii = builder().itemIdentifier(itemIdentifier).build();
    assertNull(ii.getTransactionDate());
    assertNull(ii.getInstitutionId());
    assertEquals(itemIdentifier, ii.getItemIdentifier());
    assertNull(ii.getTerminalPassword());
  }

  @Test
  void testGetTerminalPassword() {
    final ItemInformation ii = builder()
        .terminalPassword(terminalPassword)
        .build();
    assertNull(ii.getTransactionDate());
    assertNull(ii.getInstitutionId());
    assertNull(ii.getItemIdentifier());
    assertEquals(terminalPassword, ii.getTerminalPassword());
  }

  @Test
  void testCompleteItemInformation() {
    final ItemInformation ii = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .itemIdentifier(itemIdentifier)
        .terminalPassword(terminalPassword)
        .build();
    assertAll("ItemInformation",
        () -> assertEquals(transactionDate, ii.getTransactionDate()),
        () -> assertEquals(institutionId, ii.getInstitutionId()),
        () -> assertEquals(itemIdentifier, ii.getItemIdentifier()),
        () -> assertEquals(terminalPassword, ii.getTerminalPassword())
    );
  }

  @Test
  void testEquals() {
    final ItemInformation ii1 = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .itemIdentifier(itemIdentifier)
        .terminalPassword(terminalPassword)
        .build();
    final ItemInformation ii2 = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .itemIdentifier(itemIdentifier)
        .terminalPassword(terminalPassword)
        .build();
    assertEquals(ii1,ii2);
    assertEquals(ii2,ii1);
  }

  @Test
  void testNotEquals() {
    final ItemInformation ii1 = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .itemIdentifier(itemIdentifier)
        .terminalPassword(terminalPassword)
        .build();
    final ItemInformation ii2 = builder()
        .transactionDate(OffsetDateTime.now())
        .institutionId("test")
        .itemIdentifier("0987654321")
        .terminalPassword("0000")
        .build();
    assertNotEquals(ii1,ii2);
    assertNotEquals(ii2,ii1);
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("ItemInformation [transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", itemIdentifier=").append(itemIdentifier)
        .append(", terminalPassword=").append(terminalPassword)
        .append(']').toString();
    final ItemInformation ii = builder()
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .itemIdentifier(itemIdentifier)
        .terminalPassword(terminalPassword)
        .build();
    assertEquals(expectedString, ii.toString());
  }
}
