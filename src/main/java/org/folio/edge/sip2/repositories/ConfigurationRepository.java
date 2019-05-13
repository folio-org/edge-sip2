package org.folio.edge.sip2.repositories;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
import org.folio.edge.sip2.utils.Utils;

public class ConfigurationRepository {

  private IResourceProvider<IRequestData> resourceProvider;
  private final Logger log;
  private Clock clock;

  static final String TENANT_CONFIG_NAME = "acsTenantConfig";
  static final String SC_STATION_CONFIG_NAME = "selfCheckoutConfig";
  static final String CONFIG_MODULE = "edge-sip2";
  private static final String CONFIGURATION_TEMPLATE = "%s.%s.%s";
  private static final String EMPTY_CODE = "null";
  private static final String KEY_CONFIG_NAME = "configName";
  private static final String KEY_CONFIG_MODULE = "module";
  private static final String KEY_CONFIG_CODE = "code";

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

    LinkedHashMap<String, String> tenantLevelQueryParams = new LinkedHashMap<>();
    tenantLevelQueryParams.put(KEY_CONFIG_MODULE, CONFIG_MODULE);
    tenantLevelQueryParams.put(KEY_CONFIG_NAME, TENANT_CONFIG_NAME);
    final String configKeyTenant = String.format(CONFIGURATION_TEMPLATE,
                                          CONFIG_MODULE, TENANT_CONFIG_NAME, EMPTY_CODE);

    LinkedHashMap<String, String> scLevelQueryParams = new LinkedHashMap<>();
    scLevelQueryParams.put(KEY_CONFIG_MODULE, CONFIG_MODULE);
    scLevelQueryParams.put(KEY_CONFIG_NAME,
            SC_STATION_CONFIG_NAME + "." + sessionData.getScLocation());
    final String configKeySC = String.format(CONFIGURATION_TEMPLATE,
            CONFIG_MODULE, SC_STATION_CONFIG_NAME + "." + sessionData.getScLocation(), EMPTY_CODE);

    LinkedHashMap<String, String> tenantTimeZoneQueryParams = new LinkedHashMap<>();
    tenantTimeZoneQueryParams.put(KEY_CONFIG_MODULE, "ORG");
    tenantTimeZoneQueryParams.put(KEY_CONFIG_NAME, "localeSettings");
    final String configKeyLocale = String.format(CONFIGURATION_TEMPLATE,
                                          "ORG", "localeSettings", EMPTY_CODE);

    List<LinkedHashMap<String, String>> kvpQueryParamsList = new ArrayList<>();
    kvpQueryParamsList.add(tenantLevelQueryParams);
    kvpQueryParamsList.add(scLevelQueryParams);
    kvpQueryParamsList.add(tenantTimeZoneQueryParams);

    ACSStatus.ACSStatusBuilder builder = ACSStatus.builder();

    final Future<ACSStatusBuilder> acsStatusBuilderFuture =
        retrieveConfigurations(sessionData, kvpQueryParamsList)
        .map(configs -> setACSConfig(configs,
                                     configKeyTenant, configKeySC,
                                     configKeyLocale, builder,
                                     sessionData));

    return acsStatusBuilderFuture.map(result -> builder.build());
  }

  /**
   * Method that retrieves the configuration from a resource provider.
   *
   * @param sessionData sessionData containing tenant ID to retrieve desired tenant configuration.
   * @param configParameters different set of parameters to get values from config store
   * @return A Map of keys and JSON config objects
   */
  public Future<LinkedHashMap<String, JsonObject>> retrieveConfigurations(SessionData sessionData,
                                           List<LinkedHashMap<String, String>> configParameters) {
    final Map<String, String> headers = new HashMap<>();
    headers.put("accept", "application/json");

    ConfigurationRequestData requestData = new ConfigurationRequestData(null,
        headers, sessionData, configParameters);

    Future<IResource> future = resourceProvider.retrieveResource(requestData);
    return future.compose(resource -> {
      final JsonObject scConfiguration = resource.getResource();
      JsonArray configs = scConfiguration.getJsonArray("configs");
      int totalConfigs = configs.size();
      if (totalConfigs >= configParameters.size()) {

        LinkedHashMap<String, JsonObject> resultJsonConfigs = new LinkedHashMap<>();

        for (int i = 0; i < totalConfigs; i++) {
          JsonObject config = configs.getJsonObject(i);
          String module = config.getString(KEY_CONFIG_MODULE);
          String configName = config.getString(KEY_CONFIG_NAME);
          String code = config.getString(KEY_CONFIG_CODE);

          String configKey = String.format(CONFIGURATION_TEMPLATE, module, configName, code);

          String configurationString = config.getString("value");
          if (!Utils.isStringNullOrEmpty(configurationString)) {
            JsonObject jsonConfiguration = new JsonObject(configurationString);
            resultJsonConfigs.put(configKey,jsonConfiguration);
          } else {
            log.error("Getting no value from config store for one of the result config records");
          }
        }

        return Future.succeededFuture(resultJsonConfigs);

      } else {
        log.error("Unable to find all necessary configuration(s). Found {} of {}",
                      totalConfigs, configParameters.size());
        return Future.failedFuture("Unable to find all necessary configuration(s). Found "
                      + totalConfigs + " of " + configParameters.size());
      }
    });
  }

  private ACSStatusBuilder setACSConfig(LinkedHashMap<String, JsonObject> sets,
                                        String configKeyTenant, String configKeySC,
                                        String configKeyLocale, ACSStatusBuilder builder,
                                        SessionData sessionData) {

    addTenantConfig(sets.get(configKeyTenant), builder);
    addSCStationConfig(sets.get(configKeySC), builder);
    addLocaleConfig(sets.get(configKeyLocale), sessionData);
    builder.institutionId(sessionData.getTenant());

    return builder;
  }

  private void addLocaleConfig(JsonObject config, SessionData sessionData) {
    if (config != null) {
      sessionData.setTimeZone(config.getString("timezone"));
    }
  }

  private void addTenantConfig(JsonObject config, ACSStatusBuilder builder) {
    if (config != null) {
      builder.onLineStatus(true);
      builder.statusUpdateOk(config.getBoolean("statusUpdateOk"));
      builder.offLineOk(config.getBoolean("offlineOk"));
      builder.protocolVersion("2.00");
      builder.supportedMessages(getSupportedMessagesFromJson(
          config.getJsonArray("supportedMessages")));
    }
  }

  private void addSCStationConfig(JsonObject config, ACSStatusBuilder builder) {
    if (config != null) {
      builder.retriesAllowed(config.getInteger("retriesAllowed"));
      builder.timeoutPeriod(config.getInteger("timeoutPeriod"));
      builder.checkinOk(config.getBoolean("checkinOk"));
      builder.acsRenewalPolicy(config.getBoolean("acsRenewalPolicy"));
      builder.checkoutOk(config.getBoolean("checkoutOk"));
      builder.dateTimeSync(OffsetDateTime.now(clock));
      builder.libraryName(config.getString("libraryName"));
      builder.terminalLocation(config.getString("terminalLocation"));
    }
  }

  private Set<Messages> getSupportedMessagesFromJson(JsonArray supportedMessages) {
    return supportedMessages
        .stream()
        .filter(el -> ((JsonObject) el).getString("isSupported").equalsIgnoreCase("Y"))
        .map(el -> Messages.valueOf(((JsonObject) el).getString("messageName")))
        .collect(Collectors.toSet());
  }

  class ConfigurationRequestData implements IRequestData {

    List<LinkedHashMap<String, String>> configQueryParams;

    private final JsonObject body;
    private final Map<String, String> headers;
    private final SessionData sessionData;

    private ConfigurationRequestData(JsonObject body, Map<String, String> headers,
                                     SessionData sessionData,
                                     List<LinkedHashMap<String, String>> configQueryParams) {
      this.body = body;
      this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
      this.sessionData = sessionData;
      this.configQueryParams = configQueryParams;
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

      StringBuilder pathStringBuilder = new StringBuilder();
      pathStringBuilder.append("/configurations/entries?query=");

      for (int i = 0; i < configQueryParams.size(); i++) {
        if (i > 0) {
          pathStringBuilder.append(" OR ");
        }
        pathStringBuilder.append("(");
        pathStringBuilder.append(Utils.parseQueryString(configQueryParams.get(i), " AND ", "=="));
        pathStringBuilder.append(")");
      }

      String path =  pathStringBuilder.toString();

      log.debug("Parsed mod-config path: {}", path);

      return path;
    }
  }
}
