package org.folio.edge.sip2.parser;

import static org.folio.edge.sip2.domain.messages.enumerations.CurrencyType.CAD;
import static org.folio.edge.sip2.domain.messages.enumerations.CurrencyType.DEM;
import static org.folio.edge.sip2.domain.messages.enumerations.CurrencyType.ESP;
import static org.folio.edge.sip2.domain.messages.enumerations.CurrencyType.FRF;
import static org.folio.edge.sip2.domain.messages.enumerations.CurrencyType.GBP;
import static org.folio.edge.sip2.domain.messages.enumerations.CurrencyType.ITL;
import static org.folio.edge.sip2.domain.messages.enumerations.CurrencyType.JPY;
import static org.folio.edge.sip2.domain.messages.enumerations.CurrencyType.USD;
import static org.folio.edge.sip2.domain.messages.enumerations.FeeType.ADMINISTRATIVE;
import static org.folio.edge.sip2.domain.messages.enumerations.FeeType.COMPUTER_ACCESS_CHARGE;
import static org.folio.edge.sip2.domain.messages.enumerations.FeeType.DAMAGE;
import static org.folio.edge.sip2.domain.messages.enumerations.FeeType.HOLD_FEE;
import static org.folio.edge.sip2.domain.messages.enumerations.FeeType.OTHER_UNKNOWN;
import static org.folio.edge.sip2.domain.messages.enumerations.FeeType.OVERDUE;
import static org.folio.edge.sip2.domain.messages.enumerations.FeeType.PROCESSING;
import static org.folio.edge.sip2.domain.messages.enumerations.FeeType.RENTAL;
import static org.folio.edge.sip2.domain.messages.enumerations.FeeType.REPLACEMENT;
import static org.folio.edge.sip2.domain.messages.enumerations.PaymentType.CASH;
import static org.folio.edge.sip2.domain.messages.enumerations.PaymentType.CREDIT_CARD;
import static org.folio.edge.sip2.domain.messages.enumerations.PaymentType.VISA;
import static org.folio.edge.sip2.domain.messages.requests.FeePaid.builder;

import java.time.OffsetDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.enumerations.CurrencyType;
import org.folio.edge.sip2.domain.messages.enumerations.FeeType;
import org.folio.edge.sip2.domain.messages.enumerations.PaymentType;
import org.folio.edge.sip2.domain.messages.requests.FeePaid;
import org.folio.edge.sip2.domain.messages.requests.FeePaid.FeePaidBuilder;

/**
 * Parser for the Fee Paid message.
 *
 * @author mreno-EBSCO
 *
 */
public class FeePaidMessageParser extends MessageParser {
  private static final Logger log = LogManager.getLogger();

  public FeePaidMessageParser(Character delimiter, String timezone) {
    super(delimiter, timezone);
  }

  /**
   * Parses the Fee Paid message from a SIP string.
   *
   * @param message the SIP string.
   * @return the decoded Fee Paid message.
   */
  public FeePaid parse(String message) {
    final FeePaidBuilder builder = builder();
    final char [] messageChars = message.toCharArray();

    // transaction date: 18-char, fixed-length required field
    final OffsetDateTime transactionDate = parseDateTime(messageChars);
    builder.transactionDate(transactionDate);

    // fee type: 2-char, fixed-length required field
    final FeeType feeType = parseFeeType(messageChars);
    builder.feeType(feeType);

    // payment type: 2-char, fixed-length required field
    final PaymentType paymentType = parsePaymentType(messageChars);
    builder.paymentType(paymentType);

    // currency type: 3-char, fixed-length required field
    final CurrencyType currencyType = parseCurrencyType(messageChars);
    builder.currencyType(currencyType);

    // Variable length fields
    do {
      final Field field = parseFieldIdentifier(messageChars);
      final String valueString = parseVariableLengthField(messageChars, field);

      switch (field) {
        case BV:
          // fee amount: variable-length required field
          builder.feeAmount(valueString);
          break;
        case AO:
          // institution id: variable-length required field
          builder.institutionId(valueString);
          break;
        case AA:
          // patron identifier: variable-length required field
          builder.patronIdentifier(valueString);
          break;
        case AC:
          // terminal password: variable-length optional field
          builder.terminalPassword(valueString);
          break;
        case AD:
          // patron password: variable-length optional field
          builder.patronPassword(valueString);
          break;
        case CG:
          // fee identifier: variable-length optional field
          builder.feeIdentifier(valueString);
          break;
        case BK:
          // transaction id: variable-length optional field
          builder.transactionId(valueString);
          break;
        default:
          log.warn("Unknown Fee Paid field with value {}",
              valueString);
      }

      position++;
    } while (position != messageChars.length);

    return builder.build();
  }

  private FeeType parseFeeType(char [] messageChars) {
    final String feeTypeString = new String(messageChars, position, 2);
    final FeeType result;

    switch (feeTypeString) {
      case "01":
        result = OTHER_UNKNOWN;
        break;
      case "02":
        result = ADMINISTRATIVE;
        break;
      case "03":
        result = DAMAGE;
        break;
      case "04":
        result = OVERDUE;
        break;
      case "05":
        result = PROCESSING;
        break;
      case "06":
        result = RENTAL;
        break;
      case "07":
        result = REPLACEMENT;
        break;
      case "08":
        result = COMPUTER_ACCESS_CHARGE;
        break;
      case "09":
        result = HOLD_FEE;
        break;
      default:
        log.error("Unknown fee type {}, defaulting to {}",
            feeTypeString, OTHER_UNKNOWN);
        result = OTHER_UNKNOWN;
    }

    position += 2;
    return result;
  }

  private PaymentType parsePaymentType(char [] messageChars) {
    final String paymentTypeString = new String(messageChars, position, 2);
    final PaymentType result;

    switch (paymentTypeString) {
      case "00":
        result = CASH;
        break;
      case "01":
        result = VISA;
        break;
      case "02":
        result = CREDIT_CARD;
        break;
      default:
        log.error("Unknown payment type {}, defaulting to null",
            paymentTypeString);
        result = null;
    }

    position += 2;
    return result;
  }

  private CurrencyType parseCurrencyType(char [] messageChars) {
    final String currencyTypeString = new String(messageChars, position, 3);
    final CurrencyType result;

    // Should add full mapping someday: https://en.wikipedia.org/wiki/ISO_4217
    switch (currencyTypeString) {
      case "USD":
        result = USD;
        break;
      case "CAD":
        result = CAD;
        break;
      case "GPB":
        result = GBP;
        break;
      case "FRF":
        result = FRF;
        break;
      case "DEM":
        result = DEM;
        break;
      case "ITL":
        result = ITL;
        break;
      case "ESP":
        result = ESP;
        break;
      case "JPY":
        result = JPY;
        break;
      default:
        log.error("Unknown currency type {}, defaulting to null",
            currencyTypeString);
        result = null;
    }

    position += 3;
    return result;
  }
}
