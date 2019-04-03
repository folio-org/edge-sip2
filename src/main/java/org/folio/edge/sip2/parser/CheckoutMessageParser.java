package org.folio.edge.sip2.parser;

import static org.folio.edge.sip2.domain.messages.requests.Checkout.builder;

import java.time.ZonedDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.requests.Checkout;
import org.folio.edge.sip2.domain.messages.requests.Checkout.CheckoutBuilder;

/**
 * Parser for the Checkout message.
 *
 * @author mreno-EBSCO
 *
 */
public class CheckoutMessageParser extends MessageParser {
  private static final Logger log = LogManager.getLogger();

  public CheckoutMessageParser(Character delimiter) {
    super(delimiter);
  }

  /**
   * Parses the Checkout message from a SIP string.
   *
   * @param message the SIP string.
   * @return the decoded Checkout message.
   */
  public Checkout parse(String message) {
    final CheckoutBuilder coBuilder = builder();
    final char [] messageChars = message.toCharArray();

    // SC renewal policy: 1-char, fixed-length required field
    coBuilder.scRenewalPolicy(parseBoolean(messageChars));

    // no block: 1-char, fixed-length required field
    coBuilder.noBlock(parseBoolean(messageChars));

    // transaction date: 18-char, fixed-length required field
    final ZonedDateTime transactionDate = parseDateTime(messageChars);
    coBuilder.transactionDate(transactionDate);

    // nb due date: 18-char, fixed-length required field
    final ZonedDateTime nbDueDate = parseDateTime(messageChars);
    coBuilder.nbDueDate(nbDueDate);

    // Variable length fields
    do {
      final Field field = parseFieldIdentifier(messageChars);
      final String valueString = parseVariableLengthField(messageChars, field);

      switch (field) {
        case AO:
          // institution id: variable-length required field
          coBuilder.institutionId(valueString);
          break;
        case AA:
          // patron identifier: variable-length required field
          coBuilder.patronIdentifier(valueString);
          break;
        case AB:
          // item identifier: variable-length required field
          coBuilder.itemIdentifier(valueString);
          break;
        case AC:
          // terminal password: variable-length required field
          coBuilder.terminalPassword(valueString);
          break;
        case CH:
          // item properties: variable-length optional field
          coBuilder.itemProperties(valueString);
          break;
        case AD:
          // patron password: variable-length required field
          coBuilder.patronPassword(valueString);
          break;
        case BO:
          // fee acknowledged: 1-char, optional field field
          coBuilder.feeAcknowledged(convertFieldToBoolean(valueString));
          break;
        case BI:
          // cancel: 1-char, optional field field
          coBuilder.cancel(convertFieldToBoolean(valueString));
          break;
        default:
          log.warn("Unknown Checkout field with value {}",
              valueString);
      }

      position++;
    } while (position != messageChars.length);

    return coBuilder.build();
  }
}
