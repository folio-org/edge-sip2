package org.folio.edge.sip2.repositories;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;

/**
 * Represents resource data retrieved by an {@code IResourceProvider}.
 *
 * @author mreno-EBSCO
 *
 */
public interface IResource {
  /**
   * Returns the resource data as a JsonObject.
   * @return the resource data
   */
  JsonObject getResource();

  /**
   * Returns resource metadata in whatever format the provider specifies.
   * @return the resource metadata
   */
  default MultiMap getMetadata() {
    return null;
  }

  /**
   * Returns the authentication token used to access the resource. The resource provider may not
   * require a token, in this case this method may return {@code ""}. A return of {@code null}
   * means something went wrong.
   * @return the authentication token
   */
  default String getAuthenticationToken() {
    return "";
  }
}
