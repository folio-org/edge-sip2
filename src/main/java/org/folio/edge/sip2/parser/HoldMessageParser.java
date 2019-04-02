package org.folio.edge.sip2.parser;

import static org.folio.edge.sip2.domain.messages.enumerations.HoldMode.ADD;
import static org.folio.edge.sip2.domain.messages.enumerations.HoldMode.CHANGE;
import static org.folio.edge.sip2.domain.messages.enumerations.HoldMode.DELETE;
import static org.folio.edge.sip2.domain.messages.enumerations.HoldType.ANY_COPY_LOCATION;
import static org.folio.edge.sip2.domain.messages.enumerations.HoldType.ANY_COPY_TITLE;
import static org.folio.edge.sip2.domain.messages.enumerations.HoldType.OTHER;
import static org.folio.edge.sip2.domain.messages.enumerations.HoldType.SPECIFIC_COPY_TITLE;
import static org.folio.edge.sip2.domain.messages.requests.Hold.builder;

import java.time.ZonedDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.enumerations.HoldMode;
import org.folio.edge.sip2.domain.messages.enumerations.HoldType;
import org.folio.edge.sip2.domain.messages.requests.Hold;
import org.folio.edge.sip2.domain.messages.requests.Hold.HoldBuilder;

/**
 * Parser for the Hold message.
 *
 * @author mreno-EBSCO
 *
 */
public class HoldMessageParser extends MessageParser {
  private static final Logger log = LogManager.getLogger();

  public HoldMessageParser(Character delimiter) {
    super(delimiter);
  }

  /**
   * Parses the Hold message from a SIP string.
   *
   * @param message the SIP string.
   * @return the decoded Hold message.
   */
  public Hold parse(String message) {
    final HoldBuilder builder = builder();
    final char [] messageChars = message.toCharArray();

    // hold mode: 1-char, fixed-length required field
    builder.holdMode(parseHoldMode(messageChars));

    // transaction date: 18-char, fixed-length required field
    final ZonedDateTime transactionDate = parseDateTime(messageChars);
    builder.transactionDate(transactionDate);

    // Variable length fields
    do {
      final Field field = parseFieldIdentifier(messageChars);
      final String valueString = parseVariableLengthField(messageChars, field);

      switch (field) {
        case BW:
          // expiration date: 18-char, fixed-length optional field
          builder.expirationDate(convertFieldToDateTime(valueString));
          break;
        case BS:
          // pickup location: variable-length optional field
          builder.pickupLocation(valueString);
          break;
        case BY:
          // hold type: 1-char, optional field
          builder.holdType(convertFieldToHoldType(valueString));
          break;
        case AO:
          // institution id: variable-length required field
          builder.institutionId(valueString);
          break;
        case AA:
          // patron identifier: variable-length required field
          builder.patronIdentifier(valueString);
          break;
        case AD:
          // patron password: variable-length optional field
          builder.patronPassword(valueString);
          break;
        case AB:
          // item identifier: variable-length optional field
          builder.itemIdentifier(valueString);
          break;
        case AJ:
          // title identifier: variable-length optional field
          builder.titleIdentifier(valueString);
          break;
        case AC:
          // terminal password: variable-length required field
          builder.terminalPassword(valueString);
          break;
        case BO:
          // fee acknowledged: 1-char, optional field field
          builder.feeAcknowledged(convertFieldToBoolean(valueString));
          break;
        default:
          log.warn("Unknown Hold field with value {}", valueString);
      }

      position++;
    } while (position != messageChars.length);

    return builder.build();
  }

  private HoldMode parseHoldMode(char [] messageChars) {
    final HoldMode result;

    switch (messageChars[position]) {
      case '+':
        result = ADD;
        break;
      case '-':
        result = DELETE;
        break;
      case '*':
        result = CHANGE;
        break;
      default:
        log.error("Unsupported hold mode {}", messageChars[position]);
        result = null;
    }

    position++;

    return result;
  }

  private HoldType convertFieldToHoldType(String valueString) {
    final HoldType result;

    switch (valueString) {
      case "1":
        result = OTHER;
        break;
      case "2":
        result = ANY_COPY_TITLE;
        break;
      case "3":
        result = SPECIFIC_COPY_TITLE;
        break;
      case "4":
        result = ANY_COPY_LOCATION;
        break;
      default:
        log.error("Unsupported hold type {}", valueString);
        result = null;
    }

    return result;
  }
}
