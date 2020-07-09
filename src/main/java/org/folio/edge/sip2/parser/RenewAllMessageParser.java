package org.folio.edge.sip2.parser;

import static org.folio.edge.sip2.domain.messages.requests.RenewAll.builder;

import java.time.OffsetDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.requests.RenewAll;
import org.folio.edge.sip2.domain.messages.requests.RenewAll.RenewAllBuilder;

/**
 * Parser for the Renew All message.
 *
 * @author mreno-EBSCO
 *
 */
public class RenewAllMessageParser extends MessageParser {
  private static final Logger log = LogManager.getLogger();

  public RenewAllMessageParser(Character delimiter, String timezone) {
    super(delimiter, timezone);
  }

  /**
   * Parses the Renew All message from a SIP string.
   *
   * @param message the SIP string.
   * @return the decoded Renew All message.
   */
  public RenewAll parse(String message) {
    final RenewAllBuilder raBuilder = builder();
    final char [] messageChars = message.toCharArray();

    // transaction date: 18-char, fixed-length required field
    final OffsetDateTime transactionDate = parseDateTime(messageChars);
    raBuilder.transactionDate(transactionDate);

    // Variable length fields
    do {
      final Field field = parseFieldIdentifier(messageChars);
      final String valueString = parseVariableLengthField(messageChars, field);

      switch (field) {
        case AO:
          // institution id: variable-length required field
          raBuilder.institutionId(valueString);
          break;
        case AA:
          // patron identifier: variable-length required field
          raBuilder.patronIdentifier(valueString);
          break;
        case AD:
          // patron password: variable-length optional field
          raBuilder.patronPassword(valueString);
          break;
        case AC:
          // terminal password: variable-length optional field
          raBuilder.terminalPassword(valueString);
          break;
        case BO:
          // fee acknowledged: 1-char, optional field field
          raBuilder.feeAcknowledged(convertFieldToBoolean(valueString));
          break;
        default:
          log.warn("Unknown Renew All field with value {}", valueString);
      }

      position++;
    } while (position != messageChars.length);

    return raBuilder.build();
  }
}
