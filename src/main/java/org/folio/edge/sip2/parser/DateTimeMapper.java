package org.folio.edge.sip2.parser;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps a SIP date and time to a domain date and time.
 * 
 * @author mreno-EBSCO
 *
 */
public final class DateTimeMapper {
  private static final String DATE_FORMAT_STRING = "yyyyMMdd";
  private static final String TIME_FORMAT_STRING = "HHmmss";

  private static final Map<Long, String> TZ_MAP;

  static {
    final Map<Long, String> zoneLookup = new HashMap<>();
    zoneLookup.put(1L * 60L * 60L, "   A");
    zoneLookup.put(2L * 60L * 60L, "   B");
    zoneLookup.put(3L * 60L * 60L, "   C");
    zoneLookup.put(4L * 60L * 60L, "   D");
    zoneLookup.put(5L * 60L * 60L, "   E");
    zoneLookup.put(6L * 60L * 60L, "   F");
    zoneLookup.put(7L * 60L * 60L, "   G");
    zoneLookup.put(8L * 60L * 60L, "   H");
    zoneLookup.put(9L * 60L * 60L, "   I");
    zoneLookup.put(10L * 60L * 60L, "   K");
    zoneLookup.put(11L * 60L * 60L, "   L");
    zoneLookup.put(12L * 60L * 60L, "   M");
    zoneLookup.put(-1L * 60L * 60L, "   N");
    zoneLookup.put(-2L * 60L * 60L, "   O");
    zoneLookup.put(-3L * 60L * 60L, "   P");
    zoneLookup.put(-4L * 60L * 60L, "   Q");
    zoneLookup.put(-5L * 60L * 60L, "   R");
    zoneLookup.put(-6L * 60L * 60L, "   S");
    zoneLookup.put(-7L * 60L * 60L, "   T");
    zoneLookup.put(-8L * 60L * 60L, "   U");
    zoneLookup.put(-9L * 60L * 60L, "   V");
    zoneLookup.put(-10L * 60L * 60L, "   W");
    zoneLookup.put(-11L * 60L * 60L, "   X");
    zoneLookup.put(-12L * 60L * 60L, "   Y");
    zoneLookup.put(0L * 60L * 60L, "   Z");

    TZ_MAP = Collections.unmodifiableMap(zoneLookup);
  }

  private final Map<Long, String> localTzMap;

  /**
   * Construct a {@code DateTimeMapper} with the specified {@code ZoneOffset}.
   * @param scTimeZone the time zone specified by the SC.
   */
  public DateTimeMapper(ZoneOffset scTimeZone) {
    final Map<Long, String> localMap = new HashMap<>();
    localMap.put((long) scTimeZone.getTotalSeconds(), "    ");
    localTzMap = Collections.unmodifiableMap(localMap);
  }

  /**
   * Return a {@code ZoneDateTime} from a SIP formatted date and time.
   * @param scDateTime the SIP formatted date and time.
   * @return the domain date and time.
   */
  public ZonedDateTime mapDateTime(String scDateTime) {
    final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        .appendPattern(DATE_FORMAT_STRING)
        // These optional sections are because there will be an overlap
        // between local TZ and a specified time zone that matches the local
        // time zone, so we need a map for "    " and a map for all the
        // specified time zones. There might be a better way to do this.
        .optionalStart()
          .appendText(ChronoField.OFFSET_SECONDS, localTzMap)
        .optionalEnd()
        .optionalStart()
          .appendText(ChronoField.OFFSET_SECONDS, TZ_MAP)
        .optionalEnd()
        .appendPattern(TIME_FORMAT_STRING)
        .toFormatter();

    return ZonedDateTime.parse(scDateTime, formatter);
  }
}
