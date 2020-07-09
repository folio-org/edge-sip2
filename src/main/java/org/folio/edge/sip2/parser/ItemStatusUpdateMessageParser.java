package org.folio.edge.sip2.parser;

import static org.folio.edge.sip2.domain.messages.requests.ItemStatusUpdate.builder;

import java.time.OffsetDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.requests.ItemStatusUpdate;
import org.folio.edge.sip2.domain.messages.requests.ItemStatusUpdate.ItemStatusUpdateBuilder;

/**
 * Parser for the Item Status Update message.
 *
 * @author mreno-EBSCO
 *
 */
public class ItemStatusUpdateMessageParser extends MessageParser {
  private static final Logger log = LogManager.getLogger();

  public ItemStatusUpdateMessageParser(Character delimiter, String timezone) {
    super(delimiter, timezone);
  }

  /**
   * Parses the Item Status Update message from a SIP string.
   *
   * @param message the SIP string.
   * @return the decoded Item Status Update message.
   */
  public ItemStatusUpdate parse(String message) {
    final ItemStatusUpdateBuilder isuBuilder = builder();
    final char [] messageChars = message.toCharArray();

    // transaction date: 18-char, fixed-length required field
    final OffsetDateTime transactionDate = parseDateTime(messageChars);
    isuBuilder.transactionDate(transactionDate);

    // Variable length fields
    do {
      final Field field = parseFieldIdentifier(messageChars);
      final String valueString = parseVariableLengthField(messageChars, field);

      switch (field) {
        case AO:
          // institution id: variable-length required field
          isuBuilder.institutionId(valueString);
          break;
        case AB:
          // item identifier: variable-length required field
          isuBuilder.itemIdentifier(valueString);
          break;
        case AC:
          // terminal password: variable-length optional field
          isuBuilder.terminalPassword(valueString);
          break;
        case CH:
          // item properties: variable-length required field
          isuBuilder.itemProperties(valueString);
          break;
        default:
          log.warn("Unknown Item Status Update field with value {}",
              valueString);
      }

      position++;
    } while (position != messageChars.length);

    return isuBuilder.build();
  }
}
