package org.folio.edge.sip2.utils;

import io.vertx.core.json.JsonObject;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.folio.edge.sip2.repositories.IResource;
import org.folio.edge.sip2.repositories.RequestThrowable;
import org.folio.util.StringUtil;

/**
 * Utils for the repository implementations.
 *
 * @author mreno-EBSCO
 *
 */
public final class Utils {

  public static final Integer DEFAULT_USER_LOANS_LIMIT = 500;
  public static final String TITLE_NOT_FOUND = "TITLE NOT FOUND";

  private Utils() {
    super();
  }

  /**
   * Returns a FOLIO datetime formatter that conforms to SIP protocol.
   * @return SIP-conformant datetime formatter.
   */
  public static DateTimeFormatter getFolioDateTimeFormatter() {
    return new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        .appendPattern("[XXX][XX][X]")
        .toFormatter();
  }

  /**
   * Utility method to handle errors.
   * @param t - a throwable object
   * @return - List of error messages
   */
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
          return Collections.singletonList(t.getMessage());
        }
      }
    };
  }

  /**
   * Checks null or empty string.
   * @param someString some string to check
   * @return true if String is null or empty
   */
  public static boolean isStringNullOrEmpty(String someString) {
    return someString == null || someString.isEmpty();
  }

  /**
   * Converts an OffsetDatetime instance from its default TZ to a specified TZ.
   * @param instance a time instance with some default time zone
   * @param timeZone e.g. "Etc/UTC" or "Europe/Stockholm"
   * @return a converted OffsetDatetime instance in the desired TZ
   */
  public static OffsetDateTime convertDateTime(OffsetDateTime instance, String timeZone) {
    return OffsetDateTime.ofInstant(instance.toInstant(), ZoneId.of(timeZone));
  }

  /**
   * Constructs a query string given a list of KV pairs parameters
   * a delimeter, and an "equal" delimeter.
   * @param queryStringParameters an ordered list of query string parameters to parse
   * @param delimiter e.g. "&"|" AND "; spaces included if that's how it's supposed to be formatted
   * @param operator e.g. "=" | "==" | "<" | ">""
   * @return parsed query string: e.g, {@code module=="edge-sip2" AND configName=="acsTenantConfig"}
   */
  public static String buildQueryString(Map<String,String> queryStringParameters,
                                        String delimiter,
                                        String operator) {

    StringBuilder stringBuilder = new StringBuilder();
    boolean firstParamEmpty = true;

    for (Map.Entry<String, String> entry: queryStringParameters.entrySet()) {
      if (!Utils.isStringNullOrEmpty(entry.getValue())) {
        stringBuilder.append(firstParamEmpty ? "" : delimiter)
                     .append(entry.getKey())
                     .append(operator)
                     .append(StringUtil.cqlEncode(entry.getValue()));
        firstParamEmpty = false;
      }
    }
    return stringBuilder.toString();
  }

  /**
   * Utility method to handle mod-search errors.
   * @param cause - a throwable object
   * @param errorMessages - a List of error Message from previous chain
   * @return - IResource
   */
  public static IResource handleSearchErrors(Throwable cause, List<String> errorMessages) {
    return new IResource() {
      @Override
      public JsonObject getResource() {
        return null;
      }

      @Override
      public String getTitle() {
        return TITLE_NOT_FOUND;
      }

      @Override
      public List<String> getErrorMessages() {
        List<String> temp = new ArrayList<>(errorMessages);
        if (cause != null) {
          temp.add(cause.getMessage());
        }
          return temp;
        }
    };
  }

  /**
   * Adds offset and limit to the query string.
   *
   * @param sb - StringBuilder to append to
   * @param start - the starting item number
   * @param end - the ending item number
   * @return StringBuilder with offset and limit appended
   */
  public static StringBuilder appendQueryLimits(StringBuilder sb, Integer start, Integer end) {
    final int offset;
    if (start != null) {
      offset = start - 1; // expects a 1-based count, FOLIO is 0
      sb.append("&offset=").append(offset);
    } else {
      offset = 0;
    }

    if (end != null) {
      final int limit = end - offset;
      sb.append("&limit=").append(limit);
    }

    return sb;
  }
}
