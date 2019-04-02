package org.folio.edge.sip2.parser;

import static java.lang.Boolean.FALSE;
import static java.lang.Character.valueOf;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.folio.edge.sip2.domain.messages.enumerations.HoldMode.ADD;
import static org.folio.edge.sip2.domain.messages.enumerations.HoldType.SPECIFIC_COPY_TITLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.folio.edge.sip2.domain.messages.requests.Hold;
import org.junit.jupiter.api.Test;

class HoldMessageParserTests {
  @Test
  void testParse() {
    HoldMessageParser parser = new HoldMessageParser(valueOf('|'));
    final ZonedDateTime transactionDate =
        ZonedDateTime.now().truncatedTo(SECONDS);
    final ZonedDateTime expirationDate = transactionDate.plusDays(30);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    final String transactionDateString = formatter.format(transactionDate);
    final String expirationDateString = formatter.format(expirationDate);
    final Hold hold = parser.parse(
        "+" + transactionDateString + "BW" + expirationDateString
        + "|BScirc_desk|BY3|AApatron_id|AC|"
        + "AD1234|AOuniversity_id|ABSome Book|AJSome Title|BON|");

    assertEquals(ADD, hold.getHoldMode());
    assertEquals(transactionDate.getOffset(),
        hold.getTransactionDate().getOffset());
    assertEquals(expirationDate.getOffset(),
        hold.getExpirationDate().getOffset());
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
