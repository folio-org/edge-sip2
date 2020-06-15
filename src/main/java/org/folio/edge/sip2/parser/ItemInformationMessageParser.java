package org.folio.edge.sip2.parser;

import static org.folio.edge.sip2.domain.messages.requests.ItemInformation.builder;

import java.time.OffsetDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.requests.ItemInformation;
import org.folio.edge.sip2.domain.messages.requests.ItemInformation.ItemInformationBuilder;

/**
 * Parser for the Item Information message.
 *
 * @author mreno-EBSCO
 *
 */
public class ItemInformationMessageParser extends MessageParser {
  private static final Logger log = LogManager.getLogger();

  public ItemInformationMessageParser(Character delimiter, String timezone) {
    super(delimiter, timezone);
  }

  /**
   * Parses the Item Information message from a SIP string.
   *
   * @param message the SIP string.
   * @return the decoded Item Information message.
   */
  public ItemInformation parse(String message) {
    final ItemInformationBuilder iiBuilder = builder();
    final char [] messageChars = message.toCharArray();

    // transaction date: 18-char, fixed-length required field
    final OffsetDateTime transactionDate = parseDateTime(messageChars);
    iiBuilder.transactionDate(transactionDate);

    // Variable length fields
    do {
      final Field field = parseFieldIdentifier(messageChars);
      final String valueString = parseVariableLengthField(messageChars, field);

      switch (field) {
        case AO:
          // institution id: variable-length required field
          iiBuilder.institutionId(valueString);
          break;
        case AB:
          // item identifier: variable-length required field
          iiBuilder.itemIdentifier(valueString);
          break;
        case AC:
          // terminal password: variable-length optional field
          iiBuilder.terminalPassword(valueString);
          break;
        default:
          log.warn("Unknown Item Information field with value {}",
              valueString);
      }

      position++;
    } while (position != messageChars.length);

    return iiBuilder.build();
  }
}
