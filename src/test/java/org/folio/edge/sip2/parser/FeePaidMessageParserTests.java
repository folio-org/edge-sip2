package org.folio.edge.sip2.parser;

import static java.lang.Character.valueOf;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.folio.edge.sip2.domain.messages.enumerations.CurrencyType.USD;
import static org.folio.edge.sip2.domain.messages.enumerations.FeeType.DAMAGE;
import static org.folio.edge.sip2.domain.messages.enumerations.PaymentType.CASH;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.requests.FeePaid;
import org.junit.jupiter.api.Test;

class FeePaidMessageParserTests {
  @Test
  void testParse() {
    FeePaidMessageParser parser =
        new FeePaidMessageParser(valueOf('|'));
    parser.setTimezone(TestUtils.UTCTimeZone);
    final OffsetDateTime transactionDate =
        TestUtils.getOffsetDateTimeUtc().truncatedTo(SECONDS);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    final String transactionDateString = formatter.format(transactionDate);
    final FeePaid feePaid = parser.parse(
        transactionDateString + "0300USD"
        + "BV100.25|AApatron_id|AD1234|AC|"
        + "AOuniversity_id|CGTorn page|BKa1b2c3d4e5|");

    assertEquals(transactionDate.withOffsetSameInstant(ZoneOffset.UTC),
        feePaid.getTransactionDate());
    assertEquals(DAMAGE, feePaid.getFeeType());
    assertEquals(CASH, feePaid.getPaymentType());
    assertEquals(USD, feePaid.getCurrencyType());
    assertEquals("100.25", feePaid.getFeeAmount());
    assertEquals("university_id", feePaid.getInstitutionId());
    assertEquals("patron_id", feePaid.getPatronIdentifier());
    assertEquals("", feePaid.getTerminalPassword());
    assertEquals("1234", feePaid.getPatronPassword());
    assertEquals("Torn page", feePaid.getFeeIdentifier());
    assertEquals("a1b2c3d4e5", feePaid.getTransactionId());
  }
}
