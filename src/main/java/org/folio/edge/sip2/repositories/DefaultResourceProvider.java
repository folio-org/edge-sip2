package org.folio.edge.sip2.repositories;

import io.vertx.core.json.JsonObject;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultResourceProvider implements IResourceProvider {

  private String configInstancePath;
  private final Logger log;

  public DefaultResourceProvider(String path) {
    this.configInstancePath = path;
    log = LogManager.getLogger(MethodHandles.lookup().lookupClass());
  }

  @Override
  public JsonObject createResource(Object fromData) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonObject retrieveResource(Object key) {

    //Temporary debug code
    System.out.println("Path: " + configInstancePath);
    File fileName = new File(configInstancePath);
    File[] fileList = fileName.listFiles();

    for (File file: fileList) {

      System.out.println(file);
    }

    JsonObject jsonFile = null;
    String filePath = configInstancePath + "DefaultACSConfiguration.json";

    try {
      String fileContent =  new String(Files.readAllBytes(Paths.get(filePath)));
      jsonFile = new JsonObject(fileContent);

    } catch (IOException e) {
      log.error("Unable to read configuration in {} file", e, filePath);
    } catch (Exception ex) {
      log.error("General exception encountered: " + ex.getMessage());
    }

    return jsonFile;
  }

  @Override
  public JsonObject editResource(Object fromData) {
    throw new UnsupportedOperationException();
  }

  @Override
  public JsonObject deleteResource(Object resource) {
    throw new UnsupportedOperationException();
  }
}
