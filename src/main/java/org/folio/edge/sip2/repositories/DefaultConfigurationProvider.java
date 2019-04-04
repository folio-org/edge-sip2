package org.folio.edge.sip2.repositories;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class DefaultConfigurationProvider implements IConfigurationProvider{

  private String configInstancePath;
  private final Logger log;

  public DefaultConfigurationProvider(String path){
    this.configInstancePath = path;
    log = LogManager.getLogger(MethodHandles.lookup().lookupClass());
  }

  @Override
  public JsonObject retrieveConfiguration(String configKey) {

    JsonObject configJson = null;
    String filePath = configInstancePath + "DefaultACSConfiguration.json";

    try {

      File fileName = new File(configInstancePath);
      File[] fileList = fileName.listFiles();

      for (File file: fileList) {

        System.out.println(file);
      }

      String fileContent =  new String(Files.readAllBytes(Paths.get(filePath)));
      JsonObject jsonFile = new JsonObject(fileContent);
      JsonArray tenantConfigurations = jsonFile.getJsonArray("defaultConfigurations");
      Optional tenantConfigObject = tenantConfigurations
        .stream()
        .filter( config -> ((JsonObject)config).getString("tenantId").equalsIgnoreCase(configKey))
        .findFirst();

      if ( tenantConfigObject.isPresent()) {
        JsonObject resultObject = (JsonObject) tenantConfigObject.get();
        configJson = resultObject.getJsonObject("configuration");
      }
    } catch (IOException e) {
      log.error("Unable to read configuration in {} file", e, filePath);
    } catch (Exception ex) {
      log.error( "General exception encountered: " + ex.getMessage());
    }
    return configJson;
  }
}
