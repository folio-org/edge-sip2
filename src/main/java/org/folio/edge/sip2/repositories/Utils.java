package org.folio.edge.sip2.repositories;

import io.vertx.core.json.JsonObject;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.List;

/**
 * Utils for the repository implementations.
 *
 * @author mreno-EBSCO
 *
 */
final class Utils {
  private Utils() {
    super();
  }

  public static DateTimeFormatter getFolioDateTimeFormatter() {
    return new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        .appendPattern("[XXX][XX][X]")
        .toFormatter();
  }

  public static IResource handleErrors(Throwable t) {
    return new IResource() {
      @Override
      public JsonObject getResource() {
        return null;
      }

      @Override
      public List<String> getErrorMessages() {
        if (t instanceof RequestThrowable) {
          return ((RequestThrowable) t).getErrorMessages();
        } else {
          return Arrays.asList(t.getMessage());
        }
      }
    };
  }

  public static boolean isStringNullOrEmpty(String aString) {
    return aString == null ? true : aString == "";
  }

  public static OffsetDateTime getOffsetDateTimeNow(String timeZone) {
    return OffsetDateTime.now(ZoneId.of(timeZone));
  }

  public static OffsetDateTime convertDateTime(OffsetDateTime instance, String timeZone) {
    return OffsetDateTime.ofInstant(instance.toInstant(), ZoneId.of(timeZone));
  }
}
