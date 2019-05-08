package org.folio.edge.sip2.parser;

import static java.lang.Character.valueOf;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.folio.edge.sip2.domain.messages.requests.PatronEnable;
import org.junit.jupiter.api.Test;

class PatronEnableMessageParserTests {
  @Test
  void testParse() {
    PatronEnableMessageParser parser =
        new PatronEnableMessageParser(valueOf('|'));
    final OffsetDateTime transactionDate =
        OffsetDateTime.now().truncatedTo(SECONDS);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    final String transactionDateString = formatter.format(transactionDate);
    final PatronEnable patronEnable = parser.parse(
        transactionDateString + "AApatron_id|AD1234|AC|AOuniversity_id|");

    assertEquals(transactionDate.getOffset(),
        patronEnable.getTransactionDate().getOffset());
    assertEquals("university_id", patronEnable.getInstitutionId());
    assertEquals("patron_id", patronEnable.getPatronIdentifier());
    assertEquals("", patronEnable.getTerminalPassword());
    assertEquals("1234", patronEnable.getPatronPassword());
  }
}
