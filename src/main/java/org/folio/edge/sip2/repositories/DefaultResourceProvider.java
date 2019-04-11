package org.folio.edge.sip2.repositories;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultResourceProvider implements IResourceProvider<Object> {

  private final Logger log;

  public DefaultResourceProvider() {
    log = LogManager.getLogger(MethodHandles.lookup().lookupClass());
  }

  @Override
  public Future<JsonObject> createResource(Object fromData) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Future<JsonObject> retrieveResource(Object key) {

    JsonObject jsonFile = null;

    URL configurationResource = ClassLoader.getSystemResource("DefaultACSConfiguration.json");

    try (InputStream inputStream = configurationResource.openStream();
         InputStreamReader isr = new InputStreamReader(inputStream);
         BufferedReader br = new BufferedReader(isr)) {

      log.debug("Config file location:" + configurationResource.toString());
      String fileContent = br.lines().collect(Collectors.joining("\n"));
      br.lines().close();

      log.debug(fileContent);
      jsonFile = new JsonObject(fileContent);

    } catch (Exception ex) {
      log.error("General exception encountered reading configuration file: " + ex.getMessage());
    }
    return Future.succeededFuture(jsonFile);
  }

  @Override
  public Future<JsonObject> editResource(Object fromData) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Future<JsonObject> deleteResource(Object resource) {
    throw new UnsupportedOperationException();
  }
}
