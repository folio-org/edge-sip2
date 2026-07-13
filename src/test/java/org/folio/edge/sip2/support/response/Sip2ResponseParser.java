package org.folio.edge.sip2.support.response;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.folio.edge.sip2.domain.messages.enumerations.Language;
import org.folio.edge.sip2.domain.messages.enumerations.PatronStatus;
import org.folio.edge.sip2.parser.LanguageMapper;

@RequiredArgsConstructor
public abstract class Sip2ResponseParser<T> {

  protected static final DateTimeFormatter DATE_TIME_FORMATTER = ofPattern("yyyyMMdd    HHmmss");
  protected static final Map<Character, Boolean> BOOLEAN_VALUES = Map.ofEntries(
      Map.entry('Y', true),
      Map.entry('1', true),
      Map.entry('N', false),
      Map.entry('0', false)
  );

  protected int position;
  protected final char delimiter;
  protected final String timezone;

  /**
   * Parses a SIP2 response message string into a JSON-like object structure.
   *
   * @param responseMessage - the response message from `edge-sip2`
   * @return the parsed response as a Map-based structure
   */
  public T parse(String responseMessage) {
    if (responseMessage == null || responseMessage.length() < 2) {
      throw new IllegalArgumentException("Invalid response message");
    }

    position = 0;
    var messageChars = responseMessage.toCharArray();
    var statusCode = parseString(messageChars, 2);
    var expectedStatusCode = Integer.toString(getCommandCode());
    if (!Objects.equals(expectedStatusCode, statusCode)) {
      throw new IllegalArgumentException(
          "Invalid message type: expected %s, got: %s".formatted(expectedStatusCode, statusCode));
    }
    return parseBody(messageChars);
  }

  /**
   * Parses a SIP2 response message string into a JSON-like object structure.
   *
   * @param messageChars the raw SIP2 response message characters to parse
   * @return the parsed response as a Map-based structure
   */
  protected abstract T parseBody(char[] messageChars);

  /**
   * Returns a command code expected to be in response.
   *
   * @return the command code as int
   */
  public abstract int getCommandCode();

  protected void parseVariableLengthFields(char[] chars, BiConsumer<String, String> handler) {
    while (position < chars.length && chars[position] != delimiter) {
      var fieldCode = parseFieldCode(chars);
      var fieldValue = parseVariableLengthField(chars);
      handler.accept(fieldCode, fieldValue);
      if (position < chars.length && chars[position] == delimiter) {
        position++;
      }
    }
  }

  protected Boolean parseBoolean(char[] messageChars) {
    if (position >= messageChars.length) {
      return null;
    }
    char value = messageChars[position++];
    return BOOLEAN_VALUES.getOrDefault(value, null);
  }

  protected Boolean parseBoolean(String value) {
    if (StringUtils.isBlank(value)) {
      return null;
    }
    return BOOLEAN_VALUES.getOrDefault(value.charAt(0), null);
  }

  protected Integer parseInteger(char[] messageChars, int length) {
    if (position + length > messageChars.length) {
      return null;
    }
    var value = new String(messageChars, position, length);
    position += length;
    try {
      return Integer.valueOf(value);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  protected String parseString(char[] messageChars, int length) {
    if (position + length > messageChars.length) {
      return null;
    }
    var value = new String(messageChars, position, length);
    position += length;
    return value;
  }

  protected String parseFieldCode(char[] messageChars) {
    if (position + 2 > messageChars.length) {
      return "";
    }
    var fieldCode = new String(messageChars, position, 2);
    position += 2;
    return fieldCode;
  }

  protected String parseVariableLengthField(char[] messageChars) {
    int startPosition = position;
    while (position < messageChars.length && messageChars[position] != delimiter) {
      position++;
    }
    return new String(messageChars, startPosition, position - startPosition);
  }

  protected OffsetDateTime parseDateTime(char[] messageChars) {
    if (position + 18 > messageChars.length) {
      throw new IllegalArgumentException("Invalid date time format, insufficient length");
    }

    var dateTimeStr = new String(messageChars, position, 18);
    position += 18;

    return parseDateTime(dateTimeStr);
  }

  protected OffsetDateTime parseDateTime(String dateTimeStr) {
    if (dateTimeStr == null || dateTimeStr.length() != 18) {
      return null;
    }

    try {
      return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER)
          .atZone(ZoneId.of(timezone))
          .toOffsetDateTime();
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date time format", e);
    }
  }

  protected EnumSet<PatronStatus> parsePatronStatuses(char[] messageChars) {
    var length = 14;
    if (position + length > messageChars.length) {
      return EnumSet.noneOf(PatronStatus.class);
    }

    var statuses = EnumSet.noneOf(PatronStatus.class);
    var statusValues = PatronStatus.values();

    for (int i = 0; i < length && i < statusValues.length; i++) {
      if (messageChars[position + i] == 'Y') {
        statuses.add(statusValues[i]);
      }
    }

    position += length;
    return statuses;
  }

  protected Language parseLanguage(char[] messageChars) {
    var langCode = parseString(messageChars, 3);
    return LanguageMapper.find(langCode).getLanguage();
  }

  protected static void doNothing() {
    // Used to ignore unrecognized field codes
  }
}
