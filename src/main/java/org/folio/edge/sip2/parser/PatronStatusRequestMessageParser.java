package org.folio.edge.sip2.parser;

import static org.folio.edge.sip2.domain.messages.requests.PatronStatusRequest.builder;

import java.time.OffsetDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.requests.PatronStatusRequest;
import org.folio.edge.sip2.domain.messages.requests.PatronStatusRequest.PatronStatusRequestBuilder;

/**
 * Parser for the Patron Status Request message.
 *
 * @author mreno-EBSCO
 *
 */
public class PatronStatusRequestMessageParser extends MessageParser {
  private static final Logger log = LogManager.getLogger();

  public PatronStatusRequestMessageParser(Character delimiter, String timezone) {
    super(delimiter, timezone);
  }

  /**
   * Parses the Patron Status Request message from a SIP string.
   *
   * @param message the SIP string.
   * @return the decoded Patron Status Request message.
   */
  public PatronStatusRequest parse(String message) {
    final PatronStatusRequestBuilder psrBuilder = builder();
    final char [] messageChars = message.toCharArray();

    // language: 3-char, fixed-length required field
    final String languageString = new String(messageChars, position, 3);
    position += 3;
    psrBuilder.language(LanguageMapper.find(languageString).getLanguage());

    // transaction date: 18-char, fixed-length required field
    final OffsetDateTime transactionDate = parseDateTime(messageChars);
    psrBuilder.transactionDate(transactionDate);

    // Variable length fields
    do {
      final Field field = parseFieldIdentifier(messageChars);
      final String valueString = parseVariableLengthField(messageChars, field);

      switch (field) {
        case AO:
          // institution id: variable-length required field
          psrBuilder.institutionId(valueString);
          break;
        case AA:
          // patron identifier: variable-length required field
          psrBuilder.patronIdentifier(valueString);
          break;
        case AC:
          // terminal password: variable-length required field
          psrBuilder.terminalPassword(valueString);
          break;
        case AD:
          // patron password: variable-length required field
          psrBuilder.patronPassword(valueString);
          break;
        default:
          log.warn("Unknown Patron Status Request field with value {}",
              valueString);
      }

      position++;
    } while (position != messageChars.length);

    return psrBuilder.build();
  }
}
