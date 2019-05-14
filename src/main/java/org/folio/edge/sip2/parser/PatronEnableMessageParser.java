package org.folio.edge.sip2.parser;

import static org.folio.edge.sip2.domain.messages.requests.PatronEnable.builder;

import java.time.OffsetDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.requests.PatronEnable;
import org.folio.edge.sip2.domain.messages.requests.PatronEnable.PatronEnableBuilder;

/**
 * Parser for the Patron Enable message.
 *
 * @author mreno-EBSCO
 *
 */
public class PatronEnableMessageParser extends MessageParser {
  private static final Logger log = LogManager.getLogger();

  public PatronEnableMessageParser(Character delimiter, String timezone) {
    super(delimiter, timezone);
  }

  /**
   * Parses the Patron Enable message from a SIP string.
   *
   * @param message the SIP string.
   * @return the decoded Patron Enable message.
   */
  public PatronEnable parse(String message) {
    final PatronEnableBuilder peBuilder = builder();
    final char [] messageChars = message.toCharArray();

    // transaction date: 18-char, fixed-length required field
    final OffsetDateTime transactionDate = parseDateTime(messageChars);
    peBuilder.transactionDate(transactionDate);

    // Variable length fields
    do {
      final Field field = parseFieldIdentifier(messageChars);
      final String valueString = parseVariableLengthField(messageChars, field);

      switch (field) {
        case AO:
          // institution id: variable-length required field
          peBuilder.institutionId(valueString);
          break;
        case AA:
          // patron identifier: variable-length required field
          peBuilder.patronIdentifier(valueString);
          break;
        case AC:
          // terminal password: variable-length optional field
          peBuilder.terminalPassword(valueString);
          break;
        case AD:
          // patron password: variable-length optional field
          peBuilder.patronPassword(valueString);
          break;
        default:
          log.warn("Unknown Patron Enable field with value {}",
              valueString);
      }

      position++;
    } while (position != messageChars.length);

    return peBuilder.build();
  }
}
