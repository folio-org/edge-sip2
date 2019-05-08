package org.folio.edge.sip2.parser;

import static java.lang.Character.valueOf;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.folio.edge.sip2.domain.messages.requests.ItemStatusUpdate;
import org.junit.jupiter.api.Test;

class ItemStatusUpdateMessageParserTests {
  @Test
  void testParse() {
    ItemStatusUpdateMessageParser parser =
        new ItemStatusUpdateMessageParser(valueOf('|'));
    final OffsetDateTime transactionDate =
        OffsetDateTime.now().truncatedTo(SECONDS);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    final String transactionDateString = formatter.format(transactionDate);
    final ItemStatusUpdate itemStatusUpdate = parser.parse(
        transactionDateString
        + "ABSomeBook|AOuniversity_id|CHSpilled coffee on the book|");

    assertEquals(transactionDate.getOffset(),
        itemStatusUpdate.getTransactionDate().getOffset());
    assertEquals("university_id", itemStatusUpdate.getInstitutionId());
    assertEquals("SomeBook", itemStatusUpdate.getItemIdentifier());
    assertNull(itemStatusUpdate.getTerminalPassword());
    assertEquals("Spilled coffee on the book",
        itemStatusUpdate.getItemProperties());
  }
}
