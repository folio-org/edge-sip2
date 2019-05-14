package org.folio.edge.sip2.parser;

import static org.folio.edge.sip2.domain.messages.enumerations.StatusCode.SC_ABOUT_TO_SHUT_DOWN;
import static org.folio.edge.sip2.domain.messages.enumerations.StatusCode.SC_OK;
import static org.folio.edge.sip2.domain.messages.enumerations.StatusCode.SC_OUT_OF_PAPER;
import static org.folio.edge.sip2.domain.messages.requests.SCStatus.builder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.enumerations.StatusCode;
import org.folio.edge.sip2.domain.messages.requests.SCStatus;
import org.folio.edge.sip2.domain.messages.requests.SCStatus.SCStatusBuilder;

/**
 * Parser for the SC Status message.
 *
 * @author mreno-EBSCO
 *
 */
public class SCStatusMessageParser extends MessageParser {
  private static final Logger log = LogManager.getLogger();

  public static final Integer DEFAULT_MAX_PRINT_WIDTH = Integer.valueOf(80);

  public SCStatusMessageParser(Character delimiter, String timzeone) {
    super(delimiter, timzeone);
  }

  /**
   * Parses the SC Status message from a SIP string.
   *
   * @param message the SIP string.
   * @return the decoded SC Status message.
   */
  public SCStatus parse(String message) {
    final SCStatusBuilder builder = builder();
    final char [] messageChars = message.toCharArray();

    // status code: 1-char, fixed-length required field
    builder.statusCode(parseStatusCode(messageChars));

    // max print width: 3-char, fixed-length required field
    final String maxPrintWidthString = new String(messageChars, position, 3);
    position += 3;
    Integer maxPrintWidth;
    try {
      maxPrintWidth = Integer.valueOf(maxPrintWidthString);
    } catch (NumberFormatException e) {
      log.error("Max print width {} outside 000-999 range, defaulting to {}",
          maxPrintWidthString, DEFAULT_MAX_PRINT_WIDTH);
      maxPrintWidth = DEFAULT_MAX_PRINT_WIDTH;
    }
    builder.maxPrintWidth(maxPrintWidth);

    // protocol version: 4-char, fixed-length required field
    final String protocolVersion = new String(messageChars, position, 4);
    builder.protocolVersion(protocolVersion);

    return builder.build();
  }


  private StatusCode parseStatusCode(char [] messageChars) {
    final StatusCode result;

    switch (messageChars[position]) {
      case '0':
        result = SC_OK;
        break;
      case '1':
        result = SC_OUT_OF_PAPER;
        break;
      case '2':
        result = SC_ABOUT_TO_SHUT_DOWN;
        break;
      default:
        log.error("Unknown status code: {}", messageChars[position]);
        result = null;
    }

    position++; // increment position

    return result;
  }
}
