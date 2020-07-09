package org.folio.edge.sip2.parser;

import static java.lang.Boolean.FALSE;
import static java.lang.Character.valueOf;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.requests.BlockPatron;
import org.junit.jupiter.api.Test;

class BlockPatronMessageParserTests {
  @Test
  void testParse() {
    BlockPatronMessageParser parser =
        new BlockPatronMessageParser(valueOf('|'), TestUtils.UTCTimeZone);
    final OffsetDateTime transactionDate = TestUtils.getOffsetDateTimeUtc().truncatedTo(SECONDS);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    final String transactionDateString = formatter.format(transactionDate);
    final BlockPatron blockPatron = parser.parse(
        "N" + transactionDateString
        + "ALCard retained due to excessive fee violations|"
        + "AApatron_id|AC|AOuniversity_id|");

    assertEquals(FALSE, blockPatron.getCardRetained());
    assertEquals(transactionDate, blockPatron.getTransactionDate());
    assertEquals("university_id", blockPatron.getInstitutionId());
    assertEquals("Card retained due to excessive fee violations",
        blockPatron.getBlockedCardMsg());
    assertEquals("patron_id", blockPatron.getPatronIdentifier());
    assertEquals("", blockPatron.getTerminalPassword());
  }
}
