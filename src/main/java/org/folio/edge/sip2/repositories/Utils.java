package org.folio.edge.sip2.repositories;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

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
}
