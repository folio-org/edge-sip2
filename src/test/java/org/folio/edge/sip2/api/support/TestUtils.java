package org.folio.edge.sip2.api.support;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import org.folio.edge.sip2.session.SessionData;

public class TestUtils {
  private TestUtils() {
    super();
  }

  /**
   * Utility method to get the local datetime formatted as SIP expects.
   * @param dateTime Local datetime instance
   * @return formatted into "yyyyMMdd    HHmmss"
   */
  public static String getFormattedLocalDateTime(OffsetDateTime dateTime) {
    final OffsetDateTime d =  dateTime.truncatedTo(SECONDS);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    return formatter.format(d);
  }

  /**
   * Method to get a fixed UTC clock for unit testing.
   * @return UTC fixed clock
   */
  public static Clock getUtcFixedClock() {
    return Clock.fixed(Instant.now(), ZoneOffset.UTC);
  }

  /**
   * Util method that returns the current (now) OffsetDateTime
   * instance in UTC for testing.
   * @return current OffsetDateTime in UTC.
   */
  public static OffsetDateTime getOffsetDateTimeUtc() {
    return OffsetDateTime.now(TestUtils.getUtcFixedClock());
  }

  /**
   * Returns a mocked session data with UTC timezones.
   * @return SessionData object
   */
  public static SessionData getMockedSessionData() {
    SessionData sessionData = SessionData.createSession("dikutest", '|', false, "IBM850");
    sessionData.setTimeZone(UTCTimeZone);
    sessionData.setMaxPrintWidth(100);
    sessionData.setScLocation("testLocation");
    return sessionData;
  }

  /**
   * Returns a mocked session data without Location Code.
   * @return SessionData object
   */

  public static SessionData getMockedSessionData1() {
    SessionData sessionData = SessionData.createSession("dikutest", '|', false, "IBM850");
    sessionData.setTimeZone(UTCTimeZone);
    sessionData.setMaxPrintWidth(100);
    return sessionData;
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

  /**
   * Public constant for referring to UTC Timezone.
   */
  public static final String UTCTimeZone = "Etc/UTC";
}
