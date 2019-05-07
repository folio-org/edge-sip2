package org.folio.edge.sip2.utils;

import io.vertx.core.json.JsonObject;
import java.util.List;
import java.util.Objects;

/**
 * Utilities for making JSON access less complicated in the code. At some point we may use POJOs
 * for this and that would likely eliminate the need for this utility class.
 *
 * @author mreno-EBSCO
 *
 */
public final class JsonUtils {
  private JsonUtils() {
    super();
  }

  /**
   * Get the value of a field in a child of a {@code JsonObject}.
   *
   * @param parent the parent JsonObject
   * @param childField the name of the field that contains the child JsonObject
   * @param field the name of the field to retrieve
   * @return the value of the named field or {@code null} if the field of child are not set
   */
  public static String getChildString(JsonObject parent, String childField, String field) {
    return getChildString(parent, childField, field, null);
  }

  /**
   * Get the value of a field in a child of a {@code JsonObject}.
   *
   * @param parent the parent JsonObject
   * @param childField the name of the field that contains the child JsonObject
   * @param field the name of the field to retrieve
   * @param defaultString the string to return if not found, can be {@code null}
   * @return the value of the named field or {@code defaultString} if the field or child are not set
   */
  public static String getChildString(JsonObject parent, String childField, String field,
      String defaultString) {
    Objects.requireNonNull(parent, "parent cannot be null");
    final JsonObject child = parent.getJsonObject(childField);
    if (child == null) {
      return defaultString;
    }
    return child.getString(field, defaultString);
  }

  /**
   * Get the value of a field in a child n levels deep in the JsonObject.
   *
   * @param parent the parent JsonObject
   * @param descendants a list of descendant objects field names
   * @param field the name of the field to retrieve
   * @param defaultString the string to return if not found, can be {@code null}
   * @return the value of the named field or {@code defaultString} if the field or child are not set
   */
  public static String getSubChildString(JsonObject parent, List<String> descendants, String field,
      String defaultString) {
    Objects.requireNonNull(parent, "parent cannot be null");
    JsonObject current = parent;
    for (String descendant : descendants) {
      current = current.getJsonObject(descendant);
      if (current == null) {
        return defaultString;
      }
    }

    return current.getString(field, defaultString);
  }
}
