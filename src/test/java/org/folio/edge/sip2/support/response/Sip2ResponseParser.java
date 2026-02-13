package org.folio.edge.sip2.support.response;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public abstract class Sip2ResponseParser<T> {

  protected static final DateTimeFormatter DATE_TIME_FORMATTER = ofPattern("yyyyMMdd    HHmmss");
  private static final Map<Character, Boolean> BOOLEAN_VALUES;

  static {
    Map<Character, Boolean> map = new HashMap<>();
    map.put('Y', true);
    map.put('1', true);
    map.put('N', false);
    map.put('0', false);
    map.put('U', null);
    BOOLEAN_VALUES = Collections.unmodifiableMap(map);
  }

  protected int position;
  protected final char delimiter;
  protected final String timezone;

  /**
   * Parses a SIP2 response message string into a JSON-like object structure.
   *
   * @param responseMessage the raw SIP2 response message string to parse
   * @return the parsed response as a Map-based structure
   */
  public abstract T parse(String responseMessage);

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
    String value = new String(messageChars, position, length);
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
    String value = new String(messageChars, position, length);
    position += length;
    return value;
  }

  protected String parseFieldCode(char[] messageChars) {
    if (position + 2 > messageChars.length) {
      return "";
    }
    String fieldCode = new String(messageChars, position, 2);
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

    String dateTimeStr = new String(messageChars, position, 18);
    position += 18;

    try {
      return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER)
        .atZone(ZoneId.of(timezone))
        .toOffsetDateTime();
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Invalid date time format", e);
    }
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
      return null;
    }
  }
}
