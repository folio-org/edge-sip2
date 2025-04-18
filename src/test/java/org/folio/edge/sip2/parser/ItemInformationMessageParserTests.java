package org.folio.edge.sip2.parser;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.format.DateTimeFormatter;
import org.folio.edge.sip2.api.support.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ItemInformationMessageParserTests {

  @DisplayName("testParse")
  @ParameterizedTest(name = "[{index}] itemIdentifier=''{0}''")
  @ValueSource(strings = { "SomeBook", "  SomeBook", "SomeBook ", "  SomeBook  " })
  void testParse(String itemIdentifier) {
    var parser = new ItemInformationMessageParser('|', TestUtils.UTCTimeZone);
    var transactionDate = TestUtils.getOffsetDateTimeUtc().truncatedTo(SECONDS);
    var formatter = DateTimeFormatter.ofPattern("yyyyMMdd    HHmmss");
    var transactionDateString = formatter.format(transactionDate);
    var message = transactionDateString + "AB" + itemIdentifier + "|AOuniversity_id|";
    var itemInformation = parser.parse(message);

    assertEquals(transactionDate, itemInformation.getTransactionDate());
    assertEquals("university_id", itemInformation.getInstitutionId());
    assertEquals("SomeBook", itemInformation.getItemIdentifier());
    assertNull(itemInformation.getTerminalPassword());
  }
}
