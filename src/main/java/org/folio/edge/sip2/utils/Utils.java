package org.folio.edge.sip2.utils;

import io.vertx.core.json.JsonObject;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.List;

import org.folio.edge.sip2.repositories.IResource;
import org.folio.edge.sip2.repositories.RequestThrowable;

/**
 * Utils for the repository implementations.
 *
 * @author mreno-EBSCO
 *
 */
public final class Utils {
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

  public static boolean isStringNullOrEmpty(String someString) {
    return someString == null ? true : someString.equals("");
  }

  public static OffsetDateTime convertDateTime(OffsetDateTime instance, String timeZone) {
    return OffsetDateTime.ofInstant(instance.toInstant(), ZoneId.of(timeZone));
  }

  public static OffsetDateTime getTransactionTimestamp(String timeZone, Clock clock) {
    return convertDateTime(OffsetDateTime.now(clock), timeZone);
  }
}
