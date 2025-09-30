package org.folio.edge.sip2.service.config;

import io.vertx.core.json.JsonObject;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;

@Log4j2
public class TenantConfigurationService {

  private static final JsonObject DEFAULT_CONFIG_VALUE = new JsonObject();
  private final AtomicReference<JsonObject> multitenantConfigReference = new AtomicReference<>();

  /**
   * Retrieves the current multitenant configuration.
   * If no configuration is set, returns the default configuration.
   *
   * @return the current configuration as a {@link JsonObject}
   */
  public JsonObject getConfiguration() {
    var config = multitenantConfigReference.get();
    return ObjectUtils.defaultIfNull(config, DEFAULT_CONFIG_VALUE);
  }

  /**
   * Updates the multitenant configuration with the provided value.
   *
   * @param newConfig the new configuration to set as a {@link JsonObject}
   */
  public void updateConfiguration(JsonObject newConfig) {
    multitenantConfigReference.set(newConfig);
    log.info("Multitenant configuration is updated");
  }
}
