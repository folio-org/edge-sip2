package org.folio.edge.sip2.repositories;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.lang.invoke.MethodHandles;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.enumerations.Messages;
import org.folio.edge.sip2.domain.messages.responses.ACSStatus;

public class ConfigurationRepository {

  private IResourceProvider<Object> resourceProvider;
  private final Logger log;

  /**
   * Constructor that takes an IResourceProvider.
   *
   * @param resourceProvider This can be DefaultResourceProvider or any provider in the future.
   */
  public ConfigurationRepository(IResourceProvider<Object> resourceProvider) {
    if (resourceProvider == null) {
      throw new IllegalArgumentException("configGateway is null");
    }
    this.resourceProvider = resourceProvider;
    log = LogManager.getLogger(MethodHandles.lookup().lookupClass());
  }

  /**
   * Method that returns ACSStatus built from the ACS configuration JSON snippet.
   *
   * @return ACSStatus object
   */
  public Future<ACSStatus> getACSStatus() {
    Future<JsonObject> future = retrieveAcsConfiguration();

    return future.compose(acsConfiguration -> {
      ACSStatus acsStatus = null;
      if (acsConfiguration != null) {
        ACSStatus.ACSStatusBuilder builder = ACSStatus.builder();

        builder.checkinOk(acsConfiguration.getBoolean("onlineStatus"));
        builder.acsRenewalPolicy(acsConfiguration.getBoolean("acsRenewalPolicy"));
        builder.checkoutOk(acsConfiguration.getBoolean("checkoutOk"));
        builder.dateTimeSync(ZonedDateTime.now(Clock.systemUTC()));
        builder.institutionId(acsConfiguration.getString("institutionId"));
        builder.libraryName(acsConfiguration.getString("libraryName"));
        builder.offLineOk(acsConfiguration.getBoolean("checkoutOk"));
        builder.onLineStatus(acsConfiguration.getBoolean("onlineStatus"));
        builder.printLine(acsConfiguration.getString("printLine"));
        builder.protocolVersion(acsConfiguration.getString("protocolVersion"));
        builder.retriesAllowed(acsConfiguration.getInteger("retriesAllowed"));
        builder.screenMessage(acsConfiguration.getString("screenMessage"));
        builder.statusUpdateOk(acsConfiguration.getBoolean("statusUpdateOk"));
        builder.terminalLocation(acsConfiguration.getString("terminalLocation"));
        builder.timeoutPeriod(acsConfiguration.getInteger("timeoutPeriod"));
        builder.supportedMessages(getSupportedMessagesFromJson(
          acsConfiguration.getJsonArray("supportedMessages")));

        acsStatus = builder.build();

      } else {
        log.error("The JsonConfig object is null");
      }

      return Future.succeededFuture(acsStatus);
    });
  }


  /**
   * Method that retrieves the tenant configuration with the tenantID as configKey.
   *
   * @param configKey key to retrieving the desired tenant configuration. It is tenantId.
   * @return JSON object containing tenant configuration
   */
  public Future<JsonObject> retrieveTenantConfiguration(String configKey) {

    Future<IResource> future = resourceProvider.retrieveResource(null);

    return future.compose(resource -> {
      final JsonObject jsonFile = resource.getResource();
      JsonObject configJson = null;

      JsonArray tenantConfigurations = jsonFile.getJsonArray("tenantConfigurations");
      Optional<Object> tenantConfigObject = tenantConfigurations
        .stream()
        .filter(config -> ((JsonObject)config).getString("tenantId").equalsIgnoreCase(configKey))
        .findFirst();

      if (tenantConfigObject.isPresent()) {
        configJson = (JsonObject) tenantConfigObject.get();
      }

      return Future.succeededFuture(configJson);
    });
  }

  private Future<JsonObject> retrieveAcsConfiguration() {

    Future<IResource> future = resourceProvider.retrieveResource(null);
    return future.compose(resource -> {
      final JsonObject fullConfiguration = resource.getResource();
      JsonObject acsConfiguration = null;
      if (fullConfiguration != null) {
        acsConfiguration = fullConfiguration.getJsonObject("acsConfiguration");
      }

      return Future.succeededFuture(acsConfiguration);
    });
  }

  private Set<Messages> getSupportedMessagesFromJson(JsonArray supportedMessages) {
    return supportedMessages
      .stream()
      .filter(el -> ((JsonObject) el).getString("isSupported").equalsIgnoreCase("Y"))
      .map(el -> Messages.valueOf(((JsonObject) el).getString("messageName")))
      .collect(Collectors.toSet());
  }
}
