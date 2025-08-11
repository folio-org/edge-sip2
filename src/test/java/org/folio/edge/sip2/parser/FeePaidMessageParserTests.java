package org.folio.edge.sip2.parser;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.folio.edge.sip2.domain.messages.enumerations.CurrencyType.MYR;
import static org.folio.edge.sip2.domain.messages.enumerations.CurrencyType.USD;
import static org.folio.edge.sip2.domain.messages.enumerations.FeeType.DAMAGE;
import static org.folio.edge.sip2.domain.messages.enumerations.FeeType.OTHER_UNKNOWN;
import static org.folio.edge.sip2.domain.messages.enumerations.PaymentType.CASH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.enumerations.CurrencyType;
import org.folio.edge.sip2.domain.messages.enumerations.FeeType;
import org.folio.edge.sip2.domain.messages.enumerations.PaymentType;
import org.folio.edge.sip2.domain.messages.requests.FeePaid;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class FeePaidMessageParserTests {

  private static final DateTimeFormatter DATE_TIME_FORMATTER = ofPattern("yyyyMMdd    HHmmss");

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("currencyVarDataProvider")
  @DisplayName("parse_parameterized_currencyType")
  void parse_parameterized_currencyType(@SuppressWarnings("unused") String name,
      String sipMessage, CurrencyType expected) {
    var parser = new FeePaidMessageParser('|', TestUtils.UTCTimeZone);
    var transactionDate = TestUtils.getOffsetDateTimeUtc().truncatedTo(SECONDS);
    var transactionDateStr = DATE_TIME_FORMATTER.format(transactionDate);

    var result = parser.parse(transactionDateStr + sipMessage);

    var expectedValue = feePaid(CASH, expected, DAMAGE);
    assertEquals(transactionDate, result.getTransactionDate());
    verifyParsedValue(expectedValue, result);
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("feePaidTypeDataProvider")
  @DisplayName("parse_parameterized_currencyType")
  void parse_parameterized_feeType(@SuppressWarnings("unused") String name,
      String sipMessage, FeeType feeType) {
    var parser = new FeePaidMessageParser('|', TestUtils.UTCTimeZone);
    var transactionDate = TestUtils.getOffsetDateTimeUtc().truncatedTo(SECONDS);
    var transactionDateStr = DATE_TIME_FORMATTER.format(transactionDate);

    var result = parser.parse(transactionDateStr + sipMessage);

    var expectedValue = feePaid(CASH, USD, feeType);
    assertEquals(transactionDate, result.getTransactionDate());
    verifyParsedValue(expectedValue, result);
  }

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("feePaymentTypeDataProvider")
  @DisplayName("parse_parameterized_paymentType")
  void parse_parameterized_paymentType(@SuppressWarnings("unused") String name,
      String sipMessage, PaymentType paymentType) {
    var parser = new FeePaidMessageParser('|', TestUtils.UTCTimeZone);
    var transactionDate = TestUtils.getOffsetDateTimeUtc().truncatedTo(SECONDS);
    var transactionDateStr = DATE_TIME_FORMATTER.format(transactionDate);

    var result = parser.parse(transactionDateStr + sipMessage);

    var expectedValue = feePaid(paymentType, USD, DAMAGE);
    assertEquals(transactionDate, result.getTransactionDate());
    verifyParsedValue(expectedValue, result);
  }

  private static Stream<Arguments> currencyVarDataProvider() {
    return Stream.of(
        arguments("USD", sipMsgCurrency("USD"), USD),
        arguments("MYR", sipMsgCurrency("MYR"), MYR),
        arguments("unknown currency", sipMsgCurrency("ZZZ"), null)
    );
  }

  private static Stream<Arguments> feePaidTypeDataProvider() {
    return Stream.of(
        arguments("OTHER_UNKNOWN", sipMsgFeeType("01"), FeeType.OTHER_UNKNOWN),
        arguments("ADMINISTRATIVE", sipMsgFeeType("02"), FeeType.ADMINISTRATIVE),
        arguments("DAMAGE", sipMsgFeeType("03"), FeeType.DAMAGE),
        arguments("OVERDUE", sipMsgFeeType("04"), FeeType.OVERDUE),
        arguments("PROCESSING", sipMsgFeeType("05"), FeeType.PROCESSING),
        arguments("RENTAL", sipMsgFeeType("06"), FeeType.RENTAL),
        arguments("REPLACEMENT", sipMsgFeeType("07"), FeeType.REPLACEMENT),
        arguments("COMPUTER_ACCESS_CHARGE", sipMsgFeeType("08"), FeeType.COMPUTER_ACCESS_CHARGE),
        arguments("HOLD_FEE", sipMsgFeeType("09"), FeeType.HOLD_FEE),
        arguments("UnknownValue", sipMsgFeeType("99"), FeeType.OTHER_UNKNOWN)
    );
  }

  private static Stream<Arguments> feePaymentTypeDataProvider() {
    return Stream.of(
        arguments("CASH", sipMsgPaymentType("01"), CASH),
        arguments("VIS", sipMsgPaymentType("02"), PaymentType.VISA),
        arguments("CREDIT_CARD", sipMsgPaymentType("03"), PaymentType.CREDIT_CARD),
        arguments("Unknown Value", sipMsgPaymentType("99"), null)
    );
  }

  private static void verifyParsedValue(FeePaid expected, FeePaid actual) {
    assertEquals(expected.getFeeType(), actual.getFeeType());
    assertEquals(expected.getPatronIdentifier(), actual.getPatronIdentifier());
    assertEquals(expected.getCurrencyType(), actual.getCurrencyType());
    assertEquals(expected.getFeeAmount(), actual.getFeeAmount());
    assertEquals(expected.getInstitutionId(), actual.getInstitutionId());
    assertEquals(expected.getPatronIdentifier(), actual.getPatronIdentifier());
    assertEquals(expected.getTerminalPassword(), actual.getTerminalPassword());
    assertEquals(expected.getPatronPassword(), actual.getPatronPassword());
    assertEquals(expected.getFeeIdentifier(), actual.getFeeIdentifier());
    assertEquals(expected.getTransactionId(), actual.getTransactionId());
  }

  private static String sipMsgCurrency(String currency) {
    return sipMessage(currency, "03", "01");
  }

  private static String sipMsgFeeType(String feeType) {
    return sipMessage(USD.name(), feeType, "01");
  }

  private static String sipMsgPaymentType(String paymentType) {
    return sipMessage(USD.name(), "03", paymentType);
  }

  private static String sipMessage(String currency, String feeType, String paymentType) {
    var feeAmount = "100.25";
    var universityId = "university_id";
    var patronIdentifier = "patron_id";
    return feeType
        + paymentType
        + (currency != null ? currency : "")
        + "BV" + feeAmount
        + "|AA" + patronIdentifier
        + "|AD1234"
        + "|AC"
        + "|AO" + universityId
        + "|CGTorn page"
        + "|BKa1b2c3d4e5|";
  }

  private static FeePaid feePaid(PaymentType paymentType, CurrencyType currency, FeeType feeType) {
    var feeAmount = "100.25";
    var universityId = "university_id";
    var patronIdentifier = "patron_id";
    return FeePaid.builder()
        .feeType(feeType)
        .currencyType(currency)
        .paymentType(paymentType)
        .feeAmount(feeAmount)
        .patronIdentifier(patronIdentifier)
        .institutionId(universityId)
        .terminalPassword("")
        .patronPassword("1234")
        .feeIdentifier("Torn page")
        .transactionId("a1b2c3d4e5")
        .build();
  }
}
