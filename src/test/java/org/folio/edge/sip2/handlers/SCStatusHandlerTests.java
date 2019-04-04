package org.folio.edge.sip2.handlers;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.folio.edge.sip2.repositories.ConfigurationRepository;
import org.folio.edge.sip2.repositories.DefaultConfigurationProvider;
import org.junit.jupiter.api.Test;

public class SCStatusHandlerTests {

  @Test
  public void canExecuteASampleScRequest(){

    DefaultConfigurationProvider defaultConfigurationProvider = new DefaultConfigurationProvider("./src/test/resources/");
    ConfigurationRepository configRepo = new ConfigurationRepository(defaultConfigurationProvider);

    SCStatusHandler handler = new SCStatusHandler(configRepo, "./src/main/resources/templates");

    try {
      handler.execute("990231.23");
    } catch (IOException e) {
      fail("encountered an exception: " + e.getMessage());
    }
  }

}
