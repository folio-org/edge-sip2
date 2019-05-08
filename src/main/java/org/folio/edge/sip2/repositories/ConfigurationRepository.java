package org.folio.edge.sip2.repositories;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.enumerations.Messages;
import org.folio.edge.sip2.domain.messages.responses.ACSStatus;
import org.folio.edge.sip2.domain.messages.responses.ACSStatus.ACSStatusBuilder;
import org.folio.edge.sip2.session.SessionData;

public class ConfigurationRepository {

  private IResourceProvider<IRequestData> resourceProvider;
  private final Logger log;
  private Clock clock;

  static final String TENANT_CONFIG_NAME = "acsTenantConfig";
  static final String SC_STATION_CONFIG_NAME = "selfCheckoutConfig";
  static final String CONFIG_MODULE = "edge-sip2";

  /**
   * Constructor that takes an IResourceProvider.
   *
   * @param resourceProvider This can be DefaultResourceProvider or any provider in the future.
   */

  public ConfigurationRepository(IResourceProvider<IRequestData> resourceProvider, Clock clock) {
    this.resourceProvider = Objects.requireNonNull(resourceProvider,
        "ConfigGateway cannot be null");
    this.clock = Objects.requireNonNull(clock, "Clock cannot be null");
    log = LogManager.getLogger();
  }

  /**
   * Method that returns ACSStatus built from the ACS configuration JSON snippet.
   *
   * @return ACSStatus object
   */
  public Future<ACSStatus> getACSStatus(SessionData sessionData) {

    ACSStatus.ACSStatusBuilder builder = ACSStatus.builder();

    final Future<ACSStatusBuilder> tenantConfigfuture =
        retrieveConfiguration(sessionData, CONFIG_MODULE, TENANT_CONFIG_NAME, "")
        .map(config -> addTenantConfig(config, builder));

    final Future<ACSStatusBuilder> selfCheckoutConfigFuture =
        retrieveConfiguration(sessionData, CONFIG_MODULE,
            SC_STATION_CONFIG_NAME + "." + sessionData.getScLocation(), "")
        .map(config -> addSCStationConfig(config, builder));

    return CompositeFuture.all(tenantConfigfuture, selfCheckoutConfigFuture)
        .map(result -> builder.build());
  }

  /**
   * Method that retrieves the configuration from a resource provider.
   *
   * @param sessionData sessionData containing tenant ID to retrieve desired tenant configuration.
   * @param configCode config code to identify the configuration
   * @param configName name of the configuration
   * @param module module that the configuration was created for
   * @return JSON object containing tenant configuration
   */
  public Future<JsonObject> retrieveConfiguration(SessionData sessionData,
                                                  String module,
                                                  String configName,
                                                  String configCode) {
    final Map<String, String> headers = new HashMap<>();
    headers.put("accept", "application/json");

    ConfigurationRequestData requestData = new ConfigurationRequestData(null,
        headers, sessionData, module, configName, configCode);

    Future<IResource> future = resourceProvider.retrieveResource(requestData);
    return future.compose(resource -> {
      final JsonObject scConfiguration = resource.getResource();

      if (scConfiguration != null) {
        JsonArray configs = scConfiguration.getJsonArray("configs");
        if (configs.size() > 0) {
          JsonObject firstConfig = configs.getJsonObject(0);

          String acsConfigurationString = firstConfig.getString("value");
          if (acsConfigurationString != null && !acsConfigurationString.isEmpty()) {
            JsonObject acsConfiguration = new JsonObject(acsConfigurationString);
            return Future.succeededFuture(acsConfiguration);
          } else {
            logWithConfigDetails("Getting no value from config store for", module,
                configName, configCode);
            return Future.failedFuture("Getting no value from config store");
          }
        } else {
          logWithConfigDetails("Unable to find the configuration by the combination", module,
              configName, configCode);
          return Future.failedFuture("Unable to find the configuration");
        }
      } else {
        logWithConfigDetails("Unable to find the configuration by the combination", module,
            configName, configCode);
        return Future.failedFuture("Unable to find the configuration");
      }
    });
  }

  private void logWithConfigDetails(String message, String module, String configName,
      String configCode) {
    log.error("{} Module: {}; ConfigName: {}; ConfigCode {}", message, module, configName,
        configCode);
  }

  private ACSStatusBuilder addTenantConfig(JsonObject config, ACSStatusBuilder builder) {
    if (config != null) {
      builder.onLineStatus(config.getBoolean("onlineStatus"));
      builder.statusUpdateOk(config.getBoolean("statusUpdateOk"));
      builder.offLineOk(config.getBoolean("offlineOk"));
      builder.timeoutPeriod(config.getInteger("timeoutPeriod"));
      builder.retriesAllowed(config.getInteger("retriesAllowed"));
      builder.protocolVersion(config.getString("protocolVersion"));
      builder.institutionId(config.getString("institutionId"));
      builder.supportedMessages(getSupportedMessagesFromJson(
          config.getJsonArray("supportedMessages")));
    }
    return builder;
  }

  private ACSStatusBuilder addSCStationConfig(JsonObject config, ACSStatusBuilder builder) {
    if (config != null) {
      builder.checkinOk(config.getBoolean("checkinOk"));
      builder.acsRenewalPolicy(config.getBoolean("acsRenewalPolicy"));
      builder.checkoutOk(config.getBoolean("checkoutOk"));
      builder.dateTimeSync(OffsetDateTime.now(clock));
      builder.libraryName(config.getString("libraryName"));
      builder.terminalLocation(config.getString("terminalLocation"));
    }
    return builder;
  }

  private Set<Messages> getSupportedMessagesFromJson(JsonArray supportedMessages) {
    return supportedMessages
        .stream()
        .filter(el -> ((JsonObject) el).getString("isSupported").equalsIgnoreCase("Y"))
        .map(el -> Messages.valueOf(((JsonObject) el).getString("messageName")))
        .collect(Collectors.toSet());
  }

  private class ConfigurationRequestData implements IRequestData {

    private final String module;
    private final String configCode;
    private final String configName;

    private final JsonObject body;
    private final Map<String, String> headers;
    private final SessionData sessionData;

    private ConfigurationRequestData(JsonObject body, Map<String, String> headers,
                                     SessionData sessionData, String module,
                                     String configName,  String configCode) {
      this.body = body;
      this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
      this.sessionData = sessionData;
      this.configCode = configCode;
      this.configName = configName;
      this.module = module;
    }

    @Override
    public Map<String, String> getHeaders() {
      return headers;
    }

    @Override
    public JsonObject getBody() {
      return body;
    }

    @Override
    public SessionData getSessionData() {
      return sessionData;
    }

    @Override
    public String getPath() {
      return String.format("/configurations/entries?query=module==%s "
          + "AND configName==%s AND code==%s",
          module, configName, configCode);
    }
  }
}
