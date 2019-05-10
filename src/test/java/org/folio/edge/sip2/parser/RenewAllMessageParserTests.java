package org.folio.edge.sip2.parser;

import static java.lang.Boolean.FALSE;
import static java.lang.Character.valueOf;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.folio.edge.sip2.domain.messages.requests.RenewAll;
import org.junit.jupiter.api.Test;

class RenewAllMessageParserTests {
  @Test
  void testParse() {
    RenewAllMessageParser parser = new RenewAllMessageParser(valueOf('|'));
    final OffsetDateTime transactionDate =
        OffsetDateTime.now().truncatedTo(SECONDS);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    final String transactionDateString = formatter.format(transactionDate);
    final RenewAll renewAll = parser.parse(
        transactionDateString + "AApatron_id|AC|AD1234|AOuniversity_id|BON|");

    assertEquals(transactionDate.withOffsetSameInstant(ZoneOffset.UTC),
        renewAll.getTransactionDate());
    assertEquals("university_id", renewAll.getInstitutionId());
    assertEquals("patron_id", renewAll.getPatronIdentifier());
    assertEquals("1234", renewAll.getPatronPassword());
    assertEquals("", renewAll.getTerminalPassword());
    assertEquals(FALSE, renewAll.getFeeAcknowledged());
  }
}
