package org.folio.edge.sip2.parser;

import static org.folio.edge.sip2.domain.messages.requests.Checkin.builder;

import java.time.OffsetDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.requests.Checkin;
import org.folio.edge.sip2.domain.messages.requests.Checkin.CheckinBuilder;

/**
 * Parser for the Checkin message.
 *
 * @author mreno-EBSCO
 *
 */
public class CheckinMessageParser extends MessageParser {
  private static final Logger log = LogManager.getLogger();

  public CheckinMessageParser(Character delimiter, String timezone) {
    super(delimiter, timezone);
  }

  /**
   * Parses the Checkin message from a SIP string.
   *
   * @param message the SIP string.
   * @return the decoded Checkin message.
   */
  public Checkin parse(String message) {
    final CheckinBuilder builder = builder();
    final char [] messageChars = message.toCharArray();

    // no block: 1-char, fixed-length required field
    builder.noBlock(parseBoolean(messageChars));

    // transaction date: 18-char, fixed-length required field
    final OffsetDateTime transactionDate = parseDateTime(messageChars);
    builder.transactionDate(transactionDate);

    // return date: 18-char, fixed-length required field
    final OffsetDateTime returnDate = parseDateTime(messageChars);
    builder.returnDate(returnDate);

    // Variable length fields
    do {
      final Field field = parseFieldIdentifier(messageChars);
      final String valueString = parseVariableLengthField(messageChars, field);

      switch (field) {
        case AP:
          // current location: variable-length required field
          builder.currentLocation(valueString);
          break;
        case AO:
          // institution id: variable-length required field
          builder.institutionId(valueString);
          break;
        case AB:
          // item identifier: variable-length required field
          builder.itemIdentifier(valueString);
          break;
        case AC:
          // terminal password: variable-length required field
          builder.terminalPassword(valueString);
          break;
        case CH:
          // item properties: variable-length optional field
          builder.itemProperties(valueString);
          break;
        case BI:
          // cancel: 1-char, optional field field
          builder.cancel(convertFieldToBoolean(valueString));
          break;
        default:
          log.warn("Unknown Checkin field with value {}",
              valueString);
      }

      position++;
    } while (position != messageChars.length);

    return builder.build();
  }
}
