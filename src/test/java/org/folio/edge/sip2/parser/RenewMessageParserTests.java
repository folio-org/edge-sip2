package org.folio.edge.sip2.parser;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Character.valueOf;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.requests.Renew;
import org.junit.jupiter.api.Test;

class RenewMessageParserTests {
  @Test
  void testParse() {
    RenewMessageParser parser =
        new RenewMessageParser(valueOf('|'), TestUtils.UTCTimeZone);
    final OffsetDateTime transactionDate =
        TestUtils.getOffsetDateTimeUtc().truncatedTo(SECONDS);
    final OffsetDateTime nbDueDate = transactionDate.plusDays(30);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    final String transactionDateString = formatter.format(transactionDate);
    final String nbDueDateString = formatter.format(nbDueDate);
    final Renew renew = parser.parse(
        "YY" + transactionDateString + nbDueDateString
        + "AApatron_id|AC|AD1234|AOuniversity_id|ABSome Book|"
        + "AJSome Title|CHAutographed|BON|");

    assertEquals(TRUE, renew.getThirdPartyAllowed());
    assertEquals(TRUE, renew.getNoBlock());
    assertEquals(transactionDate.withOffsetSameInstant(ZoneOffset.UTC),
        renew.getTransactionDate());
    assertEquals(nbDueDate.withOffsetSameInstant(ZoneOffset.UTC),
        renew.getNbDueDate());
    assertEquals("university_id", renew.getInstitutionId());
    assertEquals("patron_id", renew.getPatronIdentifier());
    assertEquals("1234", renew.getPatronPassword());
    assertEquals("Some Book", renew.getItemIdentifier());
    assertEquals("Some Title", renew.getTitleIdentifier());
    assertEquals("", renew.getTerminalPassword());
    assertEquals("Autographed", renew.getItemProperties());
    assertEquals(FALSE, renew.getFeeAcknowledged());
  }
}
