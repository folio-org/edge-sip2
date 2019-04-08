package org.folio.edge.sip2.repositories;

import io.vertx.core.json.JsonObject;

/**
 * Interface that specifies CRUD implementations to provide a resource.
 */
public interface IResourceProvider {

  JsonObject retrieveResource(Object key);

  JsonObject createResource(Object fromData);

  JsonObject editResource(Object fromData);

  JsonObject deleteResource(Object resource);
}
