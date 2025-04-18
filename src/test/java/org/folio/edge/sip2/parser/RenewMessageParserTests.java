package org.folio.edge.sip2.parser;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Character.valueOf;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.requests.Renew;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RenewMessageParserTests {

  @DisplayName("testParse")
  @ParameterizedTest(name = "[{index}] itemIdentifier=''{0}''")
  @ValueSource(strings = { "Some Book", "  Some Book", "Some Book ", "  Some Book  " })
  void testParse(String itemIdentifier) {
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
        + "AApatron_id|AC|AD1234|AOuniversity_id|AB" + itemIdentifier + "|"
        + "AJSome Title|CHAutographed|BON|");

    assertEquals(TRUE, renew.getThirdPartyAllowed());
    assertEquals(TRUE, renew.getNoBlock());
    assertEquals(transactionDate, renew.getTransactionDate());
    assertEquals(nbDueDate, renew.getNbDueDate());
    assertEquals("university_id", renew.getInstitutionId());
    assertEquals("patron_id", renew.getPatronIdentifier());
    assertEquals("1234", renew.getPatronPassword());
    assertEquals("Some Book", renew.getItemIdentifier());
    assertEquals("Some Title", renew.getTitleIdentifier());
    assertEquals("", renew.getTerminalPassword());
    assertEquals("Autographed", renew.getItemProperties());
    assertEquals(FALSE, renew.getFeeAcknowledged());
  }

  @Test
  void testParseBlankNbDueDate() {
    RenewMessageParser parser =
        new RenewMessageParser(valueOf('|'), TestUtils.UTCTimeZone);
    final OffsetDateTime transactionDate =
        TestUtils.getOffsetDateTimeUtc().truncatedTo(SECONDS);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    final String transactionDateString = formatter.format(transactionDate);
    final String nbDueDateString = " ".repeat(18);
    final Renew renew = parser.parse(
        "YY" + transactionDateString + nbDueDateString
        + "AApatron_id|AC|AD1234|AOuniversity_id|ABSome Book|"
        + "AJSome Title|CHAutographed|BON|");

    assertEquals(TRUE, renew.getThirdPartyAllowed());
    assertEquals(TRUE, renew.getNoBlock());
    assertEquals(transactionDate, renew.getTransactionDate());
    assertEquals(null, renew.getNbDueDate());
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
