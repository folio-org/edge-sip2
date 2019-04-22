package org.folio.edge.sip2.repositories;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;

/**
 * Represents the response from the {@code FolioResourceProvider}. Contains the response JSON,
 * if any, as well as the headers stored as metadata. The data is not immutable.
 * @author mreno-EBSCO
 *
 */
public class FolioResource implements IResource {
  private final JsonObject resource;
  private final MultiMap metadata;

  public FolioResource(JsonObject resource, MultiMap metadata) {
    this.resource = resource;
    this.metadata = metadata;
  }

  @Override
  public JsonObject getResource() {
    return resource;
  }

  @Override
  public MultiMap getMetadata() {
    return metadata;
  }

  @Override
  public String getAuthenticationToken() {
    return metadata.get("x-okapi-token");
  }
}
