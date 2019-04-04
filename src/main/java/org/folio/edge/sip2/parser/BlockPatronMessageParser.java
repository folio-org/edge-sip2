package org.folio.edge.sip2.parser;

import static org.folio.edge.sip2.domain.messages.requests.BlockPatron.builder;

import java.time.ZonedDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.requests.BlockPatron;
import org.folio.edge.sip2.domain.messages.requests.BlockPatron.BlockPatronBuilder;

/**
 * Parser for the Block Patron message.
 *
 * @author mreno-EBSCO
 *
 */
public class BlockPatronMessageParser extends MessageParser {
  private static final Logger log = LogManager.getLogger();

  public BlockPatronMessageParser(Character delimiter) {
    super(delimiter);
  }

  /**
   * Parses the Block Patron message from a SIP string.
   *
   * @param message the SIP string.
   * @return the decoded Block Patron message.
   */
  public BlockPatron parse(String message) {
    final BlockPatronBuilder builder = builder();
    final char [] messageChars = message.toCharArray();

    // card retained: 1-char, fixed-length required field
    builder.cardRetained(parseBoolean(messageChars));

    // transaction date: 18-char, fixed-length required field
    final ZonedDateTime transactionDate = parseDateTime(messageChars);
    builder.transactionDate(transactionDate);

    // Variable length fields
    do {
      final Field field = parseFieldIdentifier(messageChars);
      final String valueString = parseVariableLengthField(messageChars, field);

      switch (field) {
        case AO:
          // institution id: variable-length required field
          builder.institutionId(valueString);
          break;
        case AL:
          // blocked card msg: variable-length required field
          builder.blockedCardMsg(valueString);
          break;
        case AA:
          // patron identifier: variable-length required field
          builder.patronIdentifier(valueString);
          break;
        case AC:
          // terminal password: variable-length required field
          builder.terminalPassword(valueString);
          break;
        default:
          log.warn("Unknown Block Patron field with value {}",
              valueString);
      }

      position++;
    } while (position != messageChars.length);

    return builder.build();
  }
}
