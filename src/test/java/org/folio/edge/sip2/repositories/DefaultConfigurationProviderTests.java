package org.folio.edge.sip2.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonObject;

public class DefaultConfigurationProviderTests {

  @Test
  public void canConstructDefaultConfigurationProvider(){
    DefaultConfigurationProvider defaultConfigurationProvider = new DefaultConfigurationProvider("somePath");
    assertNotNull(defaultConfigurationProvider);
  }

  @Test
  public void canRetrieveConfiguration(){
    DefaultConfigurationProvider defaultConfigurationProvider = new DefaultConfigurationProvider("./src/test/resources/");
    JsonObject jsonConfig = defaultConfigurationProvider.retrieveConfiguration("fs00000010test");

    assertNotNull(jsonConfig);
    assertEquals("fs00000010test", jsonConfig.getString("institutionId"));
    assertEquals("1.23", jsonConfig.getString("protocolVersion"));
  }
}
