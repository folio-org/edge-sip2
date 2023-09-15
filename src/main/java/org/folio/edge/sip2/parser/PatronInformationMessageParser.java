package org.folio.edge.sip2.parser;

import static org.folio.edge.sip2.domain.messages.enumerations.Summary.CHARGED_ITEMS;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.FINE_ITEMS;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.HOLD_ITEMS;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.OVERDUE_ITEMS;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.RECALL_ITEMS;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.UNAVAILABLE_HOLDS;
import static org.folio.edge.sip2.domain.messages.requests.PatronInformation.builder;
import static org.folio.edge.sip2.parser.Field.BP;
import static org.folio.edge.sip2.parser.Field.BQ;

import java.time.OffsetDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.enumerations.Summary;
import org.folio.edge.sip2.domain.messages.requests.PatronInformation;
import org.folio.edge.sip2.domain.messages.requests.PatronInformation.PatronInformationBuilder;

/**
 * Parser for the Patron Information message.
 *
 * @author mreno-EBSCO
 *
 */
public class PatronInformationMessageParser extends MessageParser {
  private static final Logger log = LogManager.getLogger();

  public PatronInformationMessageParser(Character delimiter, String timezone) {
    super(delimiter, timezone);
  }

  /**
   * Parses the Patron Information message from a SIP string.
   *
   * @param message the SIP string.
   * @return the decoded Patron Information message.
   */
  public PatronInformation parse(String message) {
    final PatronInformationBuilder piBuilder = builder();
    final char [] messageChars = message.toCharArray();

    // language: 3-char, fixed-length required field
    final String languageString = new String(messageChars, position, 3);
    position += 3;
    piBuilder.language(LanguageMapper.find(languageString).getLanguage());

    // transaction date: 18-char, fixed-length required field
    final OffsetDateTime transactionDate = parseDateTime(messageChars);
    piBuilder.transactionDate(transactionDate);

    // summary: 10-char, fixed-length required field
    final Summary summary = parseSummary(messageChars);
    piBuilder.summary(summary);

    // Variable length fields
    do {
      final Field field = parseFieldIdentifier(messageChars);
      final String valueString = parseVariableLengthField(messageChars, field);

      switch (field) {
        case AO:
          // institution id: variable-length required field
          piBuilder.institutionId(valueString);
          break;
        case AA:
          // patron identifier: variable-length required field
          piBuilder.patronIdentifier(valueString);
          break;
        case AC:
          // terminal password: variable-length optional field
          piBuilder.terminalPassword(valueString);
          break;
        case AD:
          // patron password: variable-length optional field
          piBuilder.patronPassword(valueString);
          break;
        case BP:
          // start item: variable-length optional field
          piBuilder.startItem(convertFieldToInteger(BP, valueString));
          break;
        case BQ:
          // end item: variable-length optional field
          piBuilder.endItem(convertFieldToInteger(BQ, valueString));
          break;
        default:
          log.warn("Unknown Patron Information field with value {}",
              valueString);
      }

      position++;
    } while (position != messageChars.length);

    return piBuilder.build();
  }

  private Summary parseSummary(char [] messageChars) {
    if (Boolean.TRUE.equals(parseBoolean(messageChars))) {
      position += 9;
      return HOLD_ITEMS;
    }

    if (Boolean.TRUE.equals(parseBoolean(messageChars))) {
      position += 8;
      return OVERDUE_ITEMS;
    }

    if (Boolean.TRUE.equals(parseBoolean(messageChars))) {
      position += 7;
      return CHARGED_ITEMS;
    }

    if (Boolean.TRUE.equals(parseBoolean(messageChars))) {
      position += 6;
      return FINE_ITEMS;
    }

    if (Boolean.TRUE.equals(parseBoolean(messageChars))) {
      position += 5;
      return RECALL_ITEMS;
    }

    if (Boolean.TRUE.equals(parseBoolean(messageChars))) {
      position += 4;
      return UNAVAILABLE_HOLDS;
    }

    position += 4;
    return null; // consider adding Summary.NONE
  }
}
