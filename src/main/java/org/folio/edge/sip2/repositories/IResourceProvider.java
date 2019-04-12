package org.folio.edge.sip2.repositories;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * Interface that specifies CRUD implementations to provide a resource.
 */
public interface IResourceProvider<T> {

  Future<JsonObject> retrieveResource(T key);

  Future<JsonObject> createResource(T fromData);

  Future<JsonObject> editResource(T fromData);

  Future<JsonObject> deleteResource(T resource);
}
