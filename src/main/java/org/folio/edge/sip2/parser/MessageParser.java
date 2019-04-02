package org.folio.edge.sip2.parser;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.folio.edge.sip2.parser.Field.UNKNOWN;

import java.time.ZonedDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.parser.exceptions.MissingDelimiterException;

/**
 * Base class for message parsing. Contains some common parsing methods.
 *
 * @author mreno-EBSCO
 *
 */
public abstract class MessageParser {
  private static final Logger log = LogManager.getLogger();

  protected int position;
  protected Character delimiter;

  protected MessageParser(Character delimiter) {
    this.delimiter = delimiter;
  }

  protected Field parseFieldIdentifier(char [] messageChars) {
    final String fieldIdentifier = new String(new char [] {
        messageChars[position++], messageChars[position++]
    });

    final Field field = Field.find(fieldIdentifier);

    if (field == UNKNOWN) {
      log.warn("Unknown field {}", fieldIdentifier);
    }

    return field;
  }

  protected String parseVariableLengthField(char [] messageChars, Field field) {
    final int startPosition = position;
    while (position < messageChars.length
        && messageChars[position] != delimiter.charValue()) {
      position++;
    }

    if (position == messageChars.length
        && messageChars[position - 1] != delimiter.charValue()) {
      throw new MissingDelimiterException(
          "Field does not contain a valid delimiter: " + field);
    }

    return new String(messageChars, startPosition, position - startPosition);
  }

  protected ZonedDateTime parseDateTime(char [] messageChars) {
    final String transactionDateString = new String(messageChars, position, 18);
    position += 18;

    // TIMEZONE: We'll need to get the correct TZ from somewhere
    ZonedDateTime now = ZonedDateTime.now();
    DateTimeMapper dtMapper = new DateTimeMapper(now.getOffset());
    return dtMapper.mapDateTime(transactionDateString);
  }

  protected Boolean parseBoolean(char [] messageChars) {
    final Boolean result;

    if (messageChars[position] == 'Y' || messageChars[position] == 'y') {
      result = TRUE;
    } else {
      result = FALSE;
    }

    position++; // increment position

    return result;
  }

  protected Boolean convertFieldToBoolean(String value) {
    return Boolean.valueOf("Y".equalsIgnoreCase(value));
  }

  protected Integer convertFieldToInteger(Field field, String value) {
    try {
      return Integer.valueOf(value);
    } catch (NumberFormatException e) {
      log.error("Field {} not an number: {}, ignoring", field, value);
      return null;
    }
  }
}
