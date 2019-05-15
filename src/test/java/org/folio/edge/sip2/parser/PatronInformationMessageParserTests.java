package org.folio.edge.sip2.parser;

import static java.lang.Character.valueOf;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.folio.edge.sip2.domain.messages.enumerations.Language.ENGLISH;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.CHARGED_ITEMS;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.FINE_ITEMS;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.HOLD_ITEMS;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.OVERDUE_ITEMS;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.RECALL_ITEMS;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.UNAVAILABLE_HOLDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.enumerations.Summary;
import org.folio.edge.sip2.domain.messages.requests.PatronInformation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PatronInformationMessageParserTests {
  @ParameterizedTest
  @MethodSource("providePatronInformationSummary")
  void testParse(String summaryString, Summary summary) {
    PatronInformationMessageParser parser =
        new PatronInformationMessageParser(valueOf('|'), TestUtils.UTCTimeZone);
    final OffsetDateTime transactionDate =
        TestUtils.getOffsetDateTimeUtc().truncatedTo(SECONDS);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    final String transactionDateString = formatter.format(transactionDate);
    final PatronInformation patronInformation = parser.parse(
        "001" + transactionDateString + summaryString
        + "AApatron_id|AD1234|AC|"
        + "AOuniversity_id|BP1|BQ10|");

    assertEquals(ENGLISH, patronInformation.getLanguage());
    assertEquals(transactionDate, patronInformation.getTransactionDate());
    assertEquals(summary, patronInformation.getSummary());
    assertEquals("university_id", patronInformation.getInstitutionId());
    assertEquals("patron_id", patronInformation.getPatronIdentifier());
    assertEquals("", patronInformation.getTerminalPassword());
    assertEquals("1234", patronInformation.getPatronPassword());
    assertEquals(Integer.valueOf(1), patronInformation.getStartItem());
    assertEquals(Integer.valueOf(10), patronInformation.getEndItem());
  }

  @Test
  void testParseIgnoresUnknownField() {
    PatronInformationMessageParser parser =
        new PatronInformationMessageParser(valueOf('|'), TestUtils.UTCTimeZone);
    final OffsetDateTime transactionDate =
        TestUtils.getOffsetDateTimeUtc().truncatedTo(SECONDS);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    final String transactionDateString = formatter.format(transactionDate);
    final PatronInformation patronInformation = parser.parse(
        "001" + transactionDateString + "          "
        + "AApatron_id|AD1234|AC|"
        + "AOuniversity_id|BP1|BQ10|XXInvalid|");

    assertEquals(ENGLISH, patronInformation.getLanguage());
    assertEquals(transactionDate, patronInformation.getTransactionDate());
    assertNull(patronInformation.getSummary());
    assertEquals("university_id", patronInformation.getInstitutionId());
    assertEquals("patron_id", patronInformation.getPatronIdentifier());
    assertEquals("", patronInformation.getTerminalPassword());
    assertEquals("1234", patronInformation.getPatronPassword());
    assertEquals(Integer.valueOf(1), patronInformation.getStartItem());
    assertEquals(Integer.valueOf(10), patronInformation.getEndItem());
  }

  private static Stream<Arguments> providePatronInformationSummary() {
    return Stream.of(
        Arguments.of("Y         ", HOLD_ITEMS),
        Arguments.of(" Y        ", OVERDUE_ITEMS),
        Arguments.of("  Y       ", CHARGED_ITEMS),
        Arguments.of("   Y      ", FINE_ITEMS),
        Arguments.of("    Y     ", RECALL_ITEMS),
        Arguments.of("     Y    ", UNAVAILABLE_HOLDS),
        Arguments.of("          ", null)
      );
  }
}
