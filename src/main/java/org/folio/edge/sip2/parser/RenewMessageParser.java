package org.folio.edge.sip2.parser;

import static org.folio.edge.sip2.domain.messages.requests.Renew.builder;

import java.time.OffsetDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.requests.Renew;
import org.folio.edge.sip2.domain.messages.requests.Renew.RenewBuilder;

/**
 * Parser for the Renew message.
 *
 * @author mreno-EBSCO
 *
 */
public class RenewMessageParser extends MessageParser {
  private static final Logger log = LogManager.getLogger();

  public RenewMessageParser(Character delimiter) {
    super(delimiter);
  }

  /**
   * Parses the Renew message from a SIP string.
   *
   * @param message the SIP string.
   * @return the decoded Renew message.
   */
  public Renew parse(String message) {
    final RenewBuilder rBuilder = builder();
    final char [] messageChars = message.toCharArray();

    // third party allowed: 1-char, fixed-length required field
    rBuilder.thirdPartyAllowed(parseBoolean(messageChars));

    // no block: 1-char, fixed-length required field
    rBuilder.noBlock(parseBoolean(messageChars));

    // transaction date: 18-char, fixed-length required field
    final OffsetDateTime transactionDate = parseDateTime(messageChars);
    rBuilder.transactionDate(transactionDate);

    // nb due date: 18-char, fixed-length required field
    final OffsetDateTime nbDueDate = parseDateTime(messageChars);
    rBuilder.nbDueDate(nbDueDate);

    // Variable length fields
    do {
      final Field field = parseFieldIdentifier(messageChars);
      final String valueString = parseVariableLengthField(messageChars, field);

      switch (field) {
        case AO:
          // institution id: variable-length required field
          rBuilder.institutionId(valueString);
          break;
        case AA:
          // patron identifier: variable-length required field
          rBuilder.patronIdentifier(valueString);
          break;
        case AD:
          // patron password: variable-length optional field
          rBuilder.patronPassword(valueString);
          break;
        case AB:
          // item identifier: variable-length optional field
          rBuilder.itemIdentifier(valueString);
          break;
        case AJ:
          // title identifier: variable-length optional field
          rBuilder.titleIdentifier(valueString);
          break;
        case AC:
          // terminal password: variable-length optional field
          rBuilder.terminalPassword(valueString);
          break;
        case CH:
          // item properties: variable-length optional field
          rBuilder.itemProperties(valueString);
          break;
        case BO:
          // fee acknowledged: 1-char, optional field field
          rBuilder.feeAcknowledged(convertFieldToBoolean(valueString));
          break;
        default:
          log.warn("Unknown Renew field with value {}", valueString);
      }

      position++;
    } while (position != messageChars.length);

    return rBuilder.build();
  }
}
