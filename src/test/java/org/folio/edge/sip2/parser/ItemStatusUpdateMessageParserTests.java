package org.folio.edge.sip2.parser;

import static java.lang.Character.valueOf;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.requests.ItemStatusUpdate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ItemStatusUpdateMessageParserTests {

  @DisplayName("testParse")
  @ParameterizedTest(name = "[{index}] itemIdentifier=''{0}''")
  @ValueSource(strings = { "SomeBook", "  SomeBook", "SomeBook ", "  SomeBook  " })
  void testParse(String itemIdentifier) {
    ItemStatusUpdateMessageParser parser =
        new ItemStatusUpdateMessageParser(valueOf('|'), TestUtils.UTCTimeZone);
    final OffsetDateTime transactionDate =
        TestUtils.getOffsetDateTimeUtc().truncatedTo(SECONDS);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    final String transactionDateString = formatter.format(transactionDate);
    final ItemStatusUpdate itemStatusUpdate = parser.parse(
        transactionDateString
        + "AB" + itemIdentifier + "|AOuniversity_id|CHSpilled coffee on the book|");

    assertEquals(transactionDate, itemStatusUpdate.getTransactionDate());
    assertEquals("university_id", itemStatusUpdate.getInstitutionId());
    assertEquals("SomeBook", itemStatusUpdate.getItemIdentifier());
    assertNull(itemStatusUpdate.getTerminalPassword());
    assertEquals("Spilled coffee on the book",
        itemStatusUpdate.getItemProperties());
  }
}
