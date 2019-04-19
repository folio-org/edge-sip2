package org.folio.edge.sip2.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class DefaultResourceProviderTests {

  @Test
  public void canConstructDefaultConfigurationProvider() {
    DefaultResourceProvider defaultConfigurationProvider = new DefaultResourceProvider();
    assertNotNull(defaultConfigurationProvider);
  }

  @Test
  public void canRetrieveAcsConfiguration(
      Vertx vertx,
      VertxTestContext testContext) {
    DefaultResourceProvider defaultConfigurationProvider = new DefaultResourceProvider();
    defaultConfigurationProvider.retrieveResource(null).setHandler(
        testContext.succeeding(jsonConfig -> testContext.verify(() -> {

          assertNotNull(jsonConfig);

          JsonObject acsConfig = jsonConfig.getJsonObject("acsConfiguration");

          assertEquals("fs00000010test", acsConfig.getString("institutionId"));
          assertEquals("1.23", acsConfig.getString("protocolVersion"));

          testContext.completeNow();
        })));
  }

  @Test
  public void canRetrieveTenantConfiguration(
      Vertx vertx,
      VertxTestContext testContext) {

    DefaultResourceProvider defaultConfigurationProvider = new DefaultResourceProvider();
    defaultConfigurationProvider.retrieveResource(null).setHandler(
        testContext.succeeding(jsonConfig -> testContext.verify(() -> {

          assertNotNull(jsonConfig);

          JsonArray tenantConfigs = jsonConfig.getJsonArray("tenantConfigurations");
          JsonObject firstTenantConfig = tenantConfigs.getJsonObject(0);

          assertEquals("fs00000010test", firstTenantConfig.getString("tenantId"));
          assertEquals("ASCII", firstTenantConfig.getString("encoding"));

          testContext.completeNow();
        })));
  }
}
