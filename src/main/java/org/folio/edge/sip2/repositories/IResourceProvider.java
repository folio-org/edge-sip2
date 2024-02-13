package org.folio.edge.sip2.repositories;

import io.vertx.core.Future;
import java.util.function.Supplier;
import org.folio.edge.sip2.session.SessionData;

/**
 * Interface that specifies CRUD implementations to provide a resource.
 */
public interface IResourceProvider<T> {

  Future<IResource> retrieveResource(T key);

  Future<IResource> createResource(T fromData);

  Future<IResource> editResource(T fromData);

  Future<IResource> deleteResource(T resource);

  Future<String> loginWithSupplier(String username,
                                   Supplier<Future<String>> getPasswordSupplier,
                                   SessionData sessionData,
                                   boolean cache);
}
