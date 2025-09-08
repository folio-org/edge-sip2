package org.folio.edge.sip2.api.support;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.support.Sip2Session;
import org.folio.edge.sip2.support.Sip2SessionConfiguration;
import org.folio.edge.sip2.support.Sip2TestCommand;

public class TestUtils {
  private TestUtils() {
    super();
  }

  /**
   * Utility method to get the local datetime formatted as SIP expects.
   *
   * @param dateTime Local datetime instance
   * @return formatted into "yyyyMMdd    HHmmss"
   */
  public static String getFormattedLocalDateTime(OffsetDateTime dateTime) {
    final OffsetDateTime d = dateTime.truncatedTo(SECONDS);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    return formatter.format(d);
  }

  /**
   * Method to get a fixed UTC clock for unit testing.
   *
   * @return UTC fixed clock
   */
  public static Clock getUtcFixedClock() {
    return Clock.fixed(Instant.now(), ZoneOffset.UTC);
  }

  /**
   * Util method that returns the current (now) OffsetDateTime
   * instance in UTC for testing.
   *
   * @return current OffsetDateTime in UTC.
   */
  public static OffsetDateTime getOffsetDateTimeUtc() {
    return OffsetDateTime.now(TestUtils.getUtcFixedClock());
  }

  /**
   * Returns a mocked session data with UTC timezones.
   *
   * @return SessionData object
   */
  public static SessionData getMockedSessionData() {
    SessionData sessionData = SessionData.createSession("dikutest", '|', false, "IBM850");
    sessionData.setTimeZone(UTCTimeZone);
    sessionData.setMaxPrintWidth(100);
    sessionData.setCurrency("USD");
    sessionData.setScLocation("testLocation");
    return sessionData;
  }

  /**
   * Returns a mocked session data without Location Code.
   *
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
   *
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

  /**
   * Executes a list of SIP2 test commands using the provided session configuration.
   * Each command is executed in the context of a new {@link Sip2Session}, and its result
   * is verified using the command's result verifier.
   *
   * @param configuration - the SIP2 session configuration
   * @param sip2Command   - the list of SIP2 test commands to execute
   * @throws Throwable if any command execution or verification fails
   */
  protected static void executeInSession(
      Sip2SessionConfiguration configuration,
      List<Sip2TestCommand> sip2Command) throws Throwable {

    try (var session = new Sip2Session(configuration)) {
      for (var command : sip2Command) {
        var result = session.executeCommand(command.getSip2Command());
        command.getResultVerifier().accept(result);
      }
    }
  }

  /**
   * Executes one or more SIP2 test commands using the provided session configuration.
   * This is a convenience overload that accepts varargs and delegates to the list-based method.
   *
   * @param sip2SessionConfiguration - the SIP2 session configuration
   * @param sip2Commands             - the SIP2 test commands to execute
   * @throws Throwable if any command execution or verification fails
   */
  public static void executeInSession(
      Sip2SessionConfiguration sip2SessionConfiguration,
      Sip2TestCommand... sip2Commands) throws Throwable {

    var sip2CommandsList = Stream.of(sip2Commands)
        .filter(Objects::nonNull)
        .toList();

    executeInSession(sip2SessionConfiguration, sip2CommandsList);
  }

  /**
   * Get a random available port by binding to port 0.
   *
   * @return - the available port
   */
  public static int getRandomPort() {
    do {
      try (var socket = new ServerSocket(0)) {
        return socket.getLocalPort();
      } catch (IOException e) {
        // ignore
      }
    } while (true);
  }
}
