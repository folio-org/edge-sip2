package org.folio.edge.sip2.repositories;

import io.vertx.core.json.JsonObject;

public interface IConfigurationProvider {
   JsonObject retrieveConfiguration(String configKey);
}
