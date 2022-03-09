package org.folio.edge.sip2.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class DefaultResourceProviderTests {

  private static final String DEFAULT_CONFIGURATION_FILE = "json/DefaultACSConfiguration.json";

  @Test
  public void canConstructDefaultConfigurationProvider() {
    DefaultResourceProvider defaultConfigurationProvider =
        new DefaultResourceProvider(DEFAULT_CONFIGURATION_FILE);
    assertNotNull(defaultConfigurationProvider);
  }

  @Test
  public void canRetrieveSCConfiguration(
      Vertx vertx,
      VertxTestContext testContext) {

    IRequestData mockRequestData = mock(IRequestData.class);
    when(mockRequestData.getPath()).thenReturn(ConfigurationRepository.SC_STATION_CONFIG_NAME);

    DefaultResourceProvider defaultConfigurationProvider =
        new DefaultResourceProvider(DEFAULT_CONFIGURATION_FILE);
    defaultConfigurationProvider.retrieveResource(mockRequestData).onComplete(
        testContext.succeeding(resource -> testContext.verify(() -> {
          final JsonObject jsonConfig = resource.getResource();
          assertNotNull(jsonConfig);

          JsonArray configs = jsonConfig.getJsonArray("configs");
          JsonObject firstConfig = configs.getJsonObject(0);

          String configString = firstConfig.getString("value");
          JsonObject scConfig = new JsonObject(configString);

          assertFalse(scConfig.getBoolean("checkoutOk"));
          assertEquals("CEST", scConfig.getString("SCtimeZone"));

          testContext.completeNow();
        })));
  }

  @Test
  public void canRetrieveTenantConfiguration(
      Vertx vertx,
      VertxTestContext testContext) {

    IRequestData mockRequestData = mock(IRequestData.class);
    when(mockRequestData.getPath()).thenReturn(ConfigurationRepository.TENANT_CONFIG_NAME);

    DefaultResourceProvider defaultConfigurationProvider =
        new DefaultResourceProvider(DEFAULT_CONFIGURATION_FILE);
    defaultConfigurationProvider.retrieveResource(mockRequestData).onComplete(
        testContext.succeeding(resource -> testContext.verify(() -> {
          final JsonObject jsonConfig = resource.getResource();
          assertNotNull(jsonConfig);

          JsonArray configs = jsonConfig.getJsonArray("configs");
          JsonObject secondConfig = configs.getJsonObject(1);

          String configString = secondConfig.getString("value");
          JsonObject tenantConfig = new JsonObject(configString);

          assertEquals("dikutest", tenantConfig.getString("tenantId"));
          assertEquals(5, tenantConfig.getInteger("overdueItemsLimit"));

          testContext.completeNow();
        })));
  }
}
