package org.folio.edge.sip2.parser;

import static org.folio.edge.sip2.domain.messages.requests.EndPatronSession.builder;

import java.time.OffsetDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.requests.EndPatronSession;
import org.folio.edge.sip2.domain.messages.requests.EndPatronSession.EndPatronSessionBuilder;

/**
 * Parser for the End Patron Session message.
 *
 * @author mreno-EBSCO
 *
 */
public class EndPatronSessionMessageParser extends MessageParser {
  private static final Logger log = LogManager.getLogger();

  public EndPatronSessionMessageParser(Character delimiter, String timezone) {
    super(delimiter, timezone);
  }

  /**
   * Parses the End Patron Session message from a SIP string.
   *
   * @param message the SIP string.
   * @return the decoded End Patron Session message.
   */
  public EndPatronSession parse(String message) {
    final EndPatronSessionBuilder epsBuilder = builder();
    final char [] messageChars = message.toCharArray();

    // transaction date: 18-char, fixed-length required field
    final OffsetDateTime transactionDate = parseDateTime(messageChars);
    epsBuilder.transactionDate(transactionDate);

    // Variable length fields
    do {
      final Field field = parseFieldIdentifier(messageChars);
      final String valueString = parseVariableLengthField(messageChars, field);

      switch (field) {
        case AO:
          // institution id: variable-length required field
          epsBuilder.institutionId(valueString);
          break;
        case AA:
          // patron identifier: variable-length required field
          epsBuilder.patronIdentifier(valueString);
          break;
        case AC:
          // terminal password: variable-length optional field
          epsBuilder.terminalPassword(valueString);
          break;
        case AD:
          // patron password: variable-length optional field
          epsBuilder.patronPassword(valueString);
          break;
        default:
          log.warn("Unknown End Patron Session field with value {}",
              valueString);
      }

      position++;
    } while (position != messageChars.length);

    return epsBuilder.build();
  }
}
