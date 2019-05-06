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

  @Test
  public void canConstructDefaultConfigurationProvider() {
    DefaultResourceProvider defaultConfigurationProvider = new DefaultResourceProvider();
    assertNotNull(defaultConfigurationProvider);
  }

  @Test
  public void canRetrieveSCConfiguration(
      Vertx vertx,
      VertxTestContext testContext) {

    IRequestData mockRequestData = mock(IRequestData.class);
    when(mockRequestData.getPath()).thenReturn(ConfigurationRepository.SC_STATION_CONFIG_NAME);

    DefaultResourceProvider defaultConfigurationProvider = new DefaultResourceProvider();
    defaultConfigurationProvider.retrieveResource(mockRequestData).setHandler(
        testContext.succeeding(resource -> testContext.verify(() -> {
          final JsonObject jsonConfig = resource.getResource();
          assertNotNull(jsonConfig);

          String configString = jsonConfig.getString("value");
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

    DefaultResourceProvider defaultConfigurationProvider = new DefaultResourceProvider();
    defaultConfigurationProvider.retrieveResource(mockRequestData).setHandler(
        testContext.succeeding(resource -> testContext.verify(() -> {
          final JsonObject jsonConfig = resource.getResource();
          assertNotNull(jsonConfig);

          String configString = jsonConfig.getString("value");
          JsonObject tenantConfig = new JsonObject(configString);

          assertEquals("fs00000010test", tenantConfig.getString("tenantId"));
          assertEquals(5, tenantConfig.getInteger("overdueItemsLimit"));

          testContext.completeNow();
        })));
  }
}
