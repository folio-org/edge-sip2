package org.folio.edge.sip2.parser;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Character.valueOf;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.requests.Checkout;
import org.junit.jupiter.api.Test;

class CheckoutMessageParserTests {
  @Test
  void testParse() {
    CheckoutMessageParser parser =
        new CheckoutMessageParser(valueOf('|'), TestUtils.UTCTimeZone);
    final OffsetDateTime transactionDate = TestUtils.getOffsetDateTimeUtc().truncatedTo(SECONDS);
    final OffsetDateTime nbDueDate = transactionDate.plusDays(30);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    final String transactionDateString = formatter.format(transactionDate);
    final String nbDueDateString = formatter.format(nbDueDate);
    final Checkout checkout = parser.parse(
        "YY" + transactionDateString + nbDueDateString
        + "AApatron_id|ABSomeBook|AC|CHAutographed"
        + "|AD1234|AOuniversity_id|BON|BIN|");

    assertEquals(TRUE, checkout.getScRenewalPolicy());
    assertEquals(TRUE, checkout.getNoBlock());
    assertEquals(transactionDate, checkout.getTransactionDate());
    assertEquals(nbDueDate, checkout.getNbDueDate());
    assertEquals("university_id", checkout.getInstitutionId());
    assertEquals("patron_id", checkout.getPatronIdentifier());
    assertEquals("SomeBook", checkout.getItemIdentifier());
    assertEquals("", checkout.getTerminalPassword());
    assertEquals("Autographed", checkout.getItemProperties());
    assertEquals("1234", checkout.getPatronPassword());
    assertEquals(FALSE, checkout.getFeeAcknowledged());
    assertEquals(FALSE, checkout.getCancel());
  }

  @Test
  void testParseBlankNbDueDate() {
    CheckoutMessageParser parser =
        new CheckoutMessageParser(valueOf('|'), TestUtils.UTCTimeZone);
    final OffsetDateTime transactionDate = TestUtils.getOffsetDateTimeUtc().truncatedTo(SECONDS);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    final String transactionDateString = formatter.format(transactionDate);
    final String nbDueDateString = " ".repeat(18);
    final Checkout checkout = parser.parse(
        "YY" + transactionDateString + nbDueDateString
        + "AApatron_id|ABSomeBook|AC|CHAutographed"
        + "|AD1234|AOuniversity_id|BON|BIN|");

    assertEquals(TRUE, checkout.getScRenewalPolicy());
    assertEquals(TRUE, checkout.getNoBlock());
    assertEquals(transactionDate, checkout.getTransactionDate());
    assertEquals(null, checkout.getNbDueDate());
    assertEquals("university_id", checkout.getInstitutionId());
    assertEquals("patron_id", checkout.getPatronIdentifier());
    assertEquals("SomeBook", checkout.getItemIdentifier());
    assertEquals("", checkout.getTerminalPassword());
    assertEquals("Autographed", checkout.getItemProperties());
    assertEquals("1234", checkout.getPatronPassword());
    assertEquals(FALSE, checkout.getFeeAcknowledged());
    assertEquals(FALSE, checkout.getCancel());
  }
}
