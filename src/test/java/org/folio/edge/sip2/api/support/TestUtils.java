package org.folio.edge.sip2.api.support;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TestUtils {
  private TestUtils() {
    super();
  }

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

  /**
   * Load a file from the file system returning it as a string (intended for JSON). If the file
   * cannot be loaded we assert failure (fail the test).
   * @param fileName the file name
   * @return the contents of the file as a {@code String}
   */
  public static String getJsonFromFile(String fileName) {
    try {
      return String.join("\n", Files.readAllLines(
          Paths.get(TestUtils.class.getClassLoader().getResource(fileName).toURI())));
    } catch (Exception e) {
      fail(e);
      return null;
    }
  }
}
