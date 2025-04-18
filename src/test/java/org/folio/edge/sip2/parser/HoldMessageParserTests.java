package org.folio.edge.sip2.parser;

import static java.lang.Boolean.FALSE;
import static java.lang.Character.valueOf;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.folio.edge.sip2.domain.messages.enumerations.HoldMode.ADD;
import static org.folio.edge.sip2.domain.messages.enumerations.HoldType.SPECIFIC_COPY_TITLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.requests.Hold;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HoldMessageParserTests {

  @DisplayName("testParse")
  @ParameterizedTest(name = "[{index}] itemIdentifier=''{0}''")
  @ValueSource(strings = { "Some Book", "  Some Book", "Some Book ", "  Some Book  " })
  void testParse(String itemIdentifier) {
    HoldMessageParser parser =
        new HoldMessageParser(valueOf('|'), TestUtils.UTCTimeZone);
    final OffsetDateTime transactionDate =
        TestUtils.getOffsetDateTimeUtc().truncatedTo(SECONDS);
    final OffsetDateTime expirationDate = transactionDate.plusDays(30);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    final String transactionDateString = formatter.format(transactionDate);
    final String expirationDateString = formatter.format(expirationDate);
    final Hold hold = parser.parse(
        "+" + transactionDateString + "BW" + expirationDateString
        + "|BScirc_desk|BY3|AApatron_id|AC|"
        + "AD1234|AOuniversity_id|AB" + itemIdentifier + "|AJSome Title|BON|");

    assertEquals(ADD, hold.getHoldMode());
    assertEquals(transactionDate, hold.getTransactionDate());
    assertEquals(expirationDate, hold.getExpirationDate());
    assertEquals("circ_desk", hold.getPickupLocation());
    assertEquals(SPECIFIC_COPY_TITLE, hold.getHoldType());
    assertEquals("university_id", hold.getInstitutionId());
    assertEquals("patron_id", hold.getPatronIdentifier());
    assertEquals("1234", hold.getPatronPassword());
    assertEquals("Some Book", hold.getItemIdentifier());
    assertEquals("Some Title", hold.getTitleIdentifier());
    assertEquals("", hold.getTerminalPassword());
    assertEquals(FALSE, hold.getFeeAcknowledged());
  }
}
