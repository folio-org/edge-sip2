package org.folio.edge.sip2.parser;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Character.valueOf;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.folio.edge.sip2.domain.messages.requests.Checkout;
import org.junit.jupiter.api.Test;

class CheckoutMessageParserTests {
  @Test
  void testParse() {
    CheckoutMessageParser parser = new CheckoutMessageParser(valueOf('|'));
    final ZonedDateTime transactionDate =
        ZonedDateTime.now().truncatedTo(SECONDS);
    final ZonedDateTime nbDueDate = transactionDate.plusDays(30);
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
    assertEquals(transactionDate.getOffset(),
        checkout.getTransactionDate().getOffset());
    assertEquals(nbDueDate.getOffset(),
        checkout.getNbDueDate().getOffset());
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
