package org.folio.edge.sip2.repositories;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.session.SessionData;

public class DefaultResourceProvider implements IResourceProvider<IRequestData> {

  private final Logger log;
  private String fileName;

  public DefaultResourceProvider(String fileName) {
    this.fileName = fileName;
    log = LogManager.getLogger(MethodHandles.lookup().lookupClass());
  }

  @Override
  public Future<IResource> createResource(IRequestData fromData) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Future<IResource> retrieveResource(IRequestData key) {
    log.debug("retrieveResource key:{}",key);

    JsonObject jsonFile = null;

    URL configurationResource = ClassLoader.getSystemResource(fileName);

    try (InputStream inputStream = configurationResource.openStream();
         InputStreamReader isr = new InputStreamReader(inputStream);
         BufferedReader br = new BufferedReader(isr)) {

      log.debug("Config file location: {}", configurationResource);
      String fileContent = br.lines().collect(Collectors.joining("\n"));
      br.lines().close();

      log.info("retrieveResource fileContent:{}",fileContent);
      jsonFile = new JsonObject(fileContent);

    } catch (Exception ex) {
      log.error("General exception encountered reading configuration file: " + ex.getMessage());
    }
    final JsonObject result = jsonFile;
    return Future.succeededFuture(() -> result);
  }

  @Override
  public Future<IResource> editResource(IRequestData fromData) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Future<IResource> deleteResource(IRequestData resource) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Future<String> loginWithSupplier(String username,
                                          Supplier<Future<String>> getPasswordSupplier,
                                          SessionData sessionData,
                                          boolean cache) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Future<Boolean> doPinCheck(IRequestData data) {
    throw new UnsupportedOperationException();
  }
}
