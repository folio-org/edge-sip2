package org.folio.edge.sip2.api.support;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TestUtils {

  /**
   * Utility method to get the local datetime formatted as SIP expects.
   * @param dateTime Local datetime instance
   * @return formatted into "yyyyMMdd    HHmmss"
   */
  public static String getFormattedLocalDateTime(ZonedDateTime dateTime) {
    final ZonedDateTime d =  dateTime.truncatedTo(SECONDS);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    return formatter.format(d);
  }

  /**
   * Method to get a fixed UTC clock for unit testing.
   * @return
   */
  public static Clock getUtcFixedClock() {
    return Clock.fixed(Instant.now(), ZoneOffset.UTC);
  }
}
