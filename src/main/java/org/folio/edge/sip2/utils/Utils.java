package org.folio.edge.sip2.utils;

import io.micrometer.common.util.StringUtils;
import io.vertx.core.json.JsonObject;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
   * Retrieves a value from system properties or environment variables, or returns a default.
   *
   * @param propertyName   - the name of the property variable
   * @param envName        - the name of the environment variable
   * @param defaultValue   - the value to return if neither is set or conversion fails
   * @param valueConverter - a function to convert the string value to the desired type
   * @param <T>            - the type of the value
   * @return the converted value, or defaultValue if not found or conversion fails
   */
  public static <T> T getEnvOrDefault(String propertyName, String envName, T defaultValue,
      Function<String, T> valueConverter) {
    var propertyValue = System.getProperty(propertyName, System.getenv(envName));
    if (StringUtils.isBlank(propertyValue)) {
      return defaultValue;
    }

    try {
      return valueConverter.apply(propertyValue);
    } catch (Exception e) {
      return defaultValue;
    }
  }
}
