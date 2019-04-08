package org.folio.edge.sip2.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class DefaultResourceProviderTests {

  private final String DEFAULT_RESOURCE_PATH = "./src/test/resources/";

  @Test
  public void canConstructDefaultConfigurationProvider(){
    DefaultResourceProvider defaultConfigurationProvider = new DefaultResourceProvider("somePath");
    assertNotNull(defaultConfigurationProvider);
  }

  @Test
  public void canRetrieveAcsConfiguration(){
    DefaultResourceProvider defaultConfigurationProvider = new DefaultResourceProvider(DEFAULT_RESOURCE_PATH);
    JsonObject jsonConfig = defaultConfigurationProvider.retrieveResource(null);

    assertNotNull(jsonConfig);

    JsonObject acsConfig = jsonConfig.getJsonObject("acsConfiguration");

    assertEquals("fs00000010test", acsConfig.getString("institutionId"));
    assertEquals("1.23", acsConfig.getString("protocolVersion"));
  }

  @Test
  public void canRetrieveTenantConfiguration(){
    DefaultResourceProvider defaultConfigurationProvider = new DefaultResourceProvider(DEFAULT_RESOURCE_PATH);
    JsonObject jsonConfig = defaultConfigurationProvider.retrieveResource(null);

    assertNotNull(jsonConfig);

    JsonArray tenantConfigs = jsonConfig.getJsonArray("tenantConfigurations");
    JsonObject firstTenantConfig = tenantConfigs.getJsonObject(0);

    assertEquals("fs00000010test", firstTenantConfig.getString("tenantId"));
    assertEquals("ASCII", firstTenantConfig.getString("encoding"));
  }
}
