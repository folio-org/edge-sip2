package org.folio.edge.sip2.domain.messages.responses;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static org.folio.edge.sip2.domain.messages.responses.RenewAllResponse.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class RenewAllResponseTests {
  final Boolean ok = TRUE;
  final Integer renewedCount = Integer.valueOf(2);
  final Integer unrenewedCount = Integer.valueOf(1);
  final OffsetDateTime transactionDate = OffsetDateTime.now();
  final String institutionId = "diku";
  final List<String> renewedItems = asList("renewed_item1", "renewed_item2");
  final List<String> unrenewedItems =
      asList("unrenewed_item1 - renewal limit exceeded");
  final List<String> screenMessage = asList("Hello, world!");
  final List<String> printLine = asList("Dot matrix");

  @Test
  void testGetOk() {
    final RenewAllResponse rar = builder().ok(ok).build();
    assertEquals(ok, rar.getOk());
    assertNull(rar.getRenewedCount());
    assertNull(rar.getUnrenewedCount());
    assertNull(rar.getTransactionDate());
    assertNull(rar.getInstitutionId());
    assertTrue(rar.getRenewedItems().isEmpty());
    assertTrue(rar.getUnrenewedItems().isEmpty());
    assertNull(rar.getScreenMessage());
    assertNull(rar.getPrintLine());
  }

  @Test
  void testGetRenewedCount() {
    final RenewAllResponse rar = builder().renewedCount(renewedCount).build();
    assertNull(rar.getOk());
    assertEquals(renewedCount, rar.getRenewedCount());
    assertNull(rar.getUnrenewedCount());
    assertNull(rar.getTransactionDate());
    assertNull(rar.getInstitutionId());
    assertTrue(rar.getRenewedItems().isEmpty());
    assertTrue(rar.getUnrenewedItems().isEmpty());
    assertNull(rar.getScreenMessage());
    assertNull(rar.getPrintLine());
  }

  @Test
  void testGetUnrenewedCount() {
    final RenewAllResponse rar = builder()
        .unrenewedCount(unrenewedCount)
        .build();
    assertNull(rar.getOk());
    assertNull(rar.getRenewedCount());
    assertEquals(unrenewedCount, rar.getUnrenewedCount());
    assertNull(rar.getTransactionDate());
    assertNull(rar.getInstitutionId());
    assertTrue(rar.getRenewedItems().isEmpty());
    assertTrue(rar.getUnrenewedItems().isEmpty());
    assertNull(rar.getScreenMessage());
    assertNull(rar.getPrintLine());
  }

  @Test
  void testGetTransactionDate() {
    final RenewAllResponse rar = builder()
        .transactionDate(transactionDate)
        .build();
    assertNull(rar.getOk());
    assertNull(rar.getRenewedCount());
    assertNull(rar.getUnrenewedCount());
    assertEquals(transactionDate, rar.getTransactionDate());
    assertNull(rar.getInstitutionId());
    assertTrue(rar.getRenewedItems().isEmpty());
    assertTrue(rar.getUnrenewedItems().isEmpty());
    assertNull(rar.getScreenMessage());
    assertNull(rar.getPrintLine());
  }

  @Test
  void testGetInstitutionId() {
    final RenewAllResponse rar = builder()
        .institutionId(institutionId)
        .build();
    assertNull(rar.getOk());
    assertNull(rar.getRenewedCount());
    assertNull(rar.getUnrenewedCount());
    assertNull(rar.getTransactionDate());
    assertEquals(institutionId, rar.getInstitutionId());
    assertTrue(rar.getRenewedItems().isEmpty());
    assertTrue(rar.getUnrenewedItems().isEmpty());
    assertNull(rar.getScreenMessage());
    assertNull(rar.getPrintLine());
  }

  @Test
  void testGetRenewedItems() {
    final RenewAllResponse rar = builder()
        .renewedItems(renewedItems)
        .build();
    assertNull(rar.getOk());
    assertNull(rar.getRenewedCount());
    assertNull(rar.getUnrenewedCount());
    assertNull(rar.getTransactionDate());
    assertNull(rar.getInstitutionId());
    assertEquals(renewedItems, rar.getRenewedItems());
    assertTrue(rar.getUnrenewedItems().isEmpty());
    assertNull(rar.getScreenMessage());
    assertNull(rar.getPrintLine());
  }

  @Test
  void testGetUnrenewedItems() {
    final RenewAllResponse rar = builder()
        .unrenewedItems(unrenewedItems)
        .build();
    assertNull(rar.getOk());
    assertNull(rar.getRenewedCount());
    assertNull(rar.getUnrenewedCount());
    assertNull(rar.getTransactionDate());
    assertNull(rar.getInstitutionId());
    assertTrue(rar.getRenewedItems().isEmpty());
    assertEquals(unrenewedItems, rar.getUnrenewedItems());
    assertNull(rar.getScreenMessage());
    assertNull(rar.getPrintLine());
  }

  @Test
  void testGetScreenMessage() {
    final RenewAllResponse rar = builder().screenMessage(screenMessage).build();
    assertNull(rar.getOk());
    assertNull(rar.getRenewedCount());
    assertNull(rar.getUnrenewedCount());
    assertNull(rar.getTransactionDate());
    assertNull(rar.getInstitutionId());
    assertTrue(rar.getRenewedItems().isEmpty());
    assertTrue(rar.getUnrenewedItems().isEmpty());
    assertEquals(screenMessage, rar.getScreenMessage());
    assertNull(rar.getPrintLine());
  }

  @Test
  void testGetPrintLine() {
    final RenewAllResponse rar = builder().printLine(printLine).build();
    assertNull(rar.getOk());
    assertNull(rar.getRenewedCount());
    assertNull(rar.getUnrenewedCount());
    assertNull(rar.getTransactionDate());
    assertNull(rar.getInstitutionId());
    assertTrue(rar.getRenewedItems().isEmpty());
    assertTrue(rar.getUnrenewedItems().isEmpty());
    assertNull(rar.getScreenMessage());
    assertEquals(printLine, rar.getPrintLine());
  }

  @Test
  void testCompleteRenewAllResponse() {
    final RenewAllResponse rar = builder()
        .ok(ok)
        .renewedCount(renewedCount)
        .unrenewedCount(unrenewedCount)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .renewedItems(renewedItems)
        .unrenewedItems(unrenewedItems)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertAll("RenewAllResponse",
        () -> assertEquals(ok, rar.getOk()),
        () -> assertEquals(renewedCount, rar.getRenewedCount()),
        () -> assertEquals(unrenewedCount, rar.getUnrenewedCount()),
        () -> assertEquals(transactionDate, rar.getTransactionDate()),
        () -> assertEquals(institutionId, rar.getInstitutionId()),
        () -> assertEquals(renewedItems, rar.getRenewedItems()),
        () -> assertEquals(unrenewedItems, rar.getUnrenewedItems()),
        () -> assertEquals(screenMessage, rar.getScreenMessage()),
        () -> assertEquals(printLine, rar.getPrintLine())
    );
  }

  @Test
  void testEquals() {
    final RenewAllResponse rar1 = builder()
        .ok(ok)
        .renewedCount(renewedCount)
        .unrenewedCount(unrenewedCount)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .renewedItems(renewedItems)
        .unrenewedItems(unrenewedItems)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    final RenewAllResponse rar2 = builder()
        .ok(ok)
        .renewedCount(renewedCount)
        .unrenewedCount(unrenewedCount)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .renewedItems(renewedItems)
        .unrenewedItems(unrenewedItems)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertTrue(rar1.equals(rar2));
    assertTrue(rar2.equals(rar1));
  }

  @Test
  void testNotEquals() {
    final RenewAllResponse rar1 = builder()
        .ok(ok)
        .renewedCount(renewedCount)
        .unrenewedCount(unrenewedCount)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .renewedItems(renewedItems)
        .unrenewedItems(unrenewedItems)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    final RenewAllResponse rar2 = builder()
        .ok(FALSE)
        .renewedCount(Integer.valueOf(1))
        .unrenewedCount(Integer.valueOf(2))
        .transactionDate(OffsetDateTime.now())
        .institutionId("test")
        .renewedItems(asList("renewed_item1"))
        .unrenewedItems(asList("unrenewed_item1 - item recalled",
            "unrenewed_item2 - overdue"))
        .screenMessage(asList("Welcome to the jungle."))
        .printLine(asList("Print print print"))
        .build();
    assertFalse(rar1.equals(rar2));
    assertFalse(rar2.equals(rar1));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("RenewAllResponse [ok=").append(ok)
        .append(", renewedCount=").append(renewedCount)
        .append(", unrenewedCount=").append(unrenewedCount)
        .append(", transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", renewedItems=").append(renewedItems)
        .append(", unrenewedItems=").append(unrenewedItems)
        .append(", screenMessage=").append(screenMessage)
        .append(", printLine=").append(printLine)
        .append(']').toString();
    final RenewAllResponse rar = builder()
        .ok(ok)
        .renewedCount(renewedCount)
        .unrenewedCount(unrenewedCount)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .renewedItems(renewedItems)
        .unrenewedItems(unrenewedItems)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertEquals(expectedString, rar.toString());
  }
}
