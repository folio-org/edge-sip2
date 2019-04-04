package org.folio.edge.sip2.parser;

import static java.lang.Character.valueOf;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.folio.edge.sip2.domain.messages.enumerations.Language.ENGLISH;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.HOLD_ITEMS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.folio.edge.sip2.domain.messages.requests.PatronInformation;
import org.junit.jupiter.api.Test;

class PatronInformationMessageParserTests {
  @Test
  void testParse() {
    PatronInformationMessageParser parser =
        new PatronInformationMessageParser(valueOf('|'));
    final ZonedDateTime transactionDate =
        ZonedDateTime.now().truncatedTo(SECONDS);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    final String transactionDateString = formatter.format(transactionDate);
    final PatronInformation patronInformation = parser.parse(
        "001" + transactionDateString + "Y         "
        + "AApatron_id|AD1234|AC|"
        + "AOuniversity_id|BP1|BQ10|");

    assertEquals(ENGLISH, patronInformation.getLanguage());
    assertEquals(transactionDate.getOffset(),
        patronInformation.getTransactionDate().getOffset());
    assertEquals(HOLD_ITEMS, patronInformation.getSummary());
    assertEquals("university_id", patronInformation.getInstitutionId());
    assertEquals("patron_id", patronInformation.getPatronIdentifier());
    assertEquals("", patronInformation.getTerminalPassword());
    assertEquals("1234", patronInformation.getPatronPassword());
    assertEquals(Integer.valueOf(1), patronInformation.getStartItem());
    assertEquals(Integer.valueOf(10), patronInformation.getEndItem());
  }
}
