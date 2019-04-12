package org.folio.edge.sip2.repositories;

import io.vertx.core.json.JsonObject;
import java.util.Collections;
import java.util.Map;

/**
 * Data used for making the resource request.
 *
 * @author mreno-EBSCO
 *
 */
public interface IRequestData {
  String getPath();

  default JsonObject getBody() {
    return new JsonObject();
  }

  default Map<String, String> getHeaders() {
    return Collections.emptyMap();
  }
}
