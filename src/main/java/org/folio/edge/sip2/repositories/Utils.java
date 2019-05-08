package org.folio.edge.sip2.repositories;

import io.vertx.core.json.JsonObject;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
  /*
  public static String getCurrentTime() {

    DateTimeFormatter format = getFolioDateTimeFormatter();

    //Convert String to LocalDateTime
    String date = "2016-08-22 14:30";
    LocalDateTime ldt = LocalDateTime.parse(ZonedDateTime.now().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    System.out.println("LocalDateTime : " + format.format(ldt));

    //Paris, 2016 Apr-Oct = DST, UTC+2, other months UTC+1
    //UTC+2
    ZonedDateTime parisDateTime;
    parisDateTime = ldt.atZone(ZoneId.of("Europe/Paris"));
    System.out.println("Depart : " + format.format(parisDateTime));


  }*/
}
