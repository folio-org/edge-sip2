package org.folio.edge.sip2.repositories;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Throwable that occurs when a Request fails in some way.
 *
 * @author mreno-EBSCO
 *
 */
public class FolioRequestThrowable extends RequestThrowable {
  private static final long serialVersionUID = -3631812923140695707L;

  public FolioRequestThrowable(String message) {
    super(message);
  }

  @Override
  public List<String> getErrorMessages() {
    try {
      final JsonObject message = new JsonObject(getMessage());
      final JsonArray errors = message.getJsonArray("errors");
      if (errors == null) {
        return Arrays.asList(getMessage());
      } else {
        return errors.stream()
            .map(o -> (JsonObject) o)
            .filter(jo -> jo.containsKey("message"))
            .map(jo -> jo.getString("message"))
            .collect(Collectors.toList());
      }
    } catch (Exception e) {
      return Arrays.asList(getMessage());
    }
  }
}
