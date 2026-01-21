package org.folio.edge.sip2.repositories;

import static io.vertx.core.Future.succeededFuture;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.folio.edge.sip2.utils.CqlQuery.exactMatchByKey;
import static org.folio.edge.sip2.utils.CqlQuery.exactMatchByScope;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.folio.edge.sip2.domain.messages.enumerations.CurrencyType;
import org.folio.edge.sip2.domain.messages.responses.ACSStatus;
import org.folio.edge.sip2.repositories.domain.AcsConfig;
import org.folio.edge.sip2.repositories.domain.AcsTenantConfig;
import org.folio.edge.sip2.repositories.domain.ScStationConfig;
import org.folio.edge.sip2.repositories.domain.TenantLocaleConfig;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.CqlQuery;
import org.folio.edge.sip2.utils.Sip2LogAdapter;

public class SettingsRepository {

  private static final int QUERY_LIMIT = 100;
  private static final String SIP2_MODULE_SCOPE = "edge-sip2";
  private static final String ACS_TENANT_CONFIG_KEY = "acsTenantConfig";
  private static final String SC_STATION_CONFIG_KEY = "selfCheckoutConfig";
  private static final String LOCALE_SETTINGS_KEY = "localeSettings";
  private static final String FAILED_TO_FIND_SETTING_MSG_TEMPLATE =
      "Failed to find configuration: {}";

  private final Clock clock;
  private final Sip2LogAdapter log;
  private final ConfigurationRepository configurationRepository;
  private final IResourceProvider<IRequestData> resourceProvider;

  /**
   * Constructs a new SettingsRepository.
   *
   * @param resourceProvider the resource provider for request data, must not be null
   * @param clock            the clock instance, must not be null
   */
  @Inject
  public SettingsRepository(IResourceProvider<IRequestData> resourceProvider, Clock clock,
      ConfigurationRepository configurationRepository) {
    this.resourceProvider = requireNonNull(resourceProvider, "resourceProvider cannot be null");
    this.configurationRepository = requireNonNull(
        configurationRepository, "configurationRepository cannot be null");
    this.clock = requireNonNull(clock, "Clock cannot be null");
    log = Sip2LogAdapter.getLogger(SettingsRepository.class);
  }

  /**
   * Retrieves the ACSStatus for the given session by fetching and parsing settings.
   *
   * @param sessionData the session data containing context for the request
   * @return a Future containing the ACSStatus built from the retrieved settings
   */
  public Future<ACSStatus> getACSStatus(SessionData sessionData) {
    log.debug(sessionData, "getACSStatus:: retrieving from mod-settings: {}", sessionData);
    var params = getSettingsConfigKeys(sessionData);
    var settingsRequestData = new SettingsRequestData(QUERY_LIMIT, sessionData, params);
    return resourceProvider.retrieveResource(new LocaleRequestData(sessionData))
        .map(this::parseLocaleConfiguration)
        .otherwise(error -> getNullTenantLocaleConfig(sessionData, error))
        .flatMap(localeConfig -> resourceProvider.retrieveResource(settingsRequestData)
            .map(resource -> getSettingsAsMap(sessionData, resource, localeConfig))
            .flatMap(settings -> getMissingSettingsFromConfiguration(sessionData, settings, params))
            .flatMap(configsByKey -> parseFoundConfiguration(sessionData, configsByKey))
            .flatMap(acsConfig -> processAcsConfig(sessionData, acsConfig))
            .onFailure(e -> log.error(sessionData, "Error loading data from 'mod-settings'", e)));
  }

  private TenantLocaleConfig parseLocaleConfiguration(IResource resource) {
    var localeConfig = resource.getResource();
    return new TenantLocaleConfig()
        .locale(localeConfig.getString("locale"))
        .currency(localeConfig.getString("currency"))
        .timezone(localeConfig.getString("timezone"));
  }

  private TenantLocaleConfig getNullTenantLocaleConfig(SessionData sessionData, Throwable error) {
    log.warn(sessionData, "Failed to retrieve locale data from 'mod-settings', using null", error);
    return null;
  }

  private Map<String, Object> getSettingsAsMap(SessionData sessionData,
      IResource resource, TenantLocaleConfig localeConfig) {
    var foundSettings = resource.getResource();
    var settingsItems = foundSettings.getJsonArray("items", new JsonArray());
    var stringObjectHashMap = groupSettingsByKey(sessionData, settingsItems);
    if (localeConfig != null) {
      stringObjectHashMap.put(LOCALE_SETTINGS_KEY, localeConfig);
    }
    return stringObjectHashMap;
  }

  private Future<Map<String, Object>> getMissingSettingsFromConfiguration(
      SessionData sessionData, Map<String, Object> settingsByKey, List<ConfigKey> configKeys) {

    if (settingsByKey.size() == configKeys.size() + 1) {
      return succeededFuture(settingsByKey);
    }

    return configurationRepository.retrieveConfigurations(sessionData)
        .map(configsByKey -> mergeConfiguration(sessionData, settingsByKey, configsByKey));
  }

  private Future<AcsConfig> parseFoundConfiguration(
      SessionData sessionData, Map<String, Object> configsByKey) {

    var localeConfigObject = configsByKey.get(LOCALE_SETTINGS_KEY);
    var tenantLocaleConfig = parseSafe(sessionData, localeConfigObject, TenantLocaleConfig.class);
    if (tenantLocaleConfig.isEmpty()) {
      log.info(sessionData, FAILED_TO_FIND_SETTING_MSG_TEMPLATE, LOCALE_SETTINGS_KEY);
    }

    var acsTenantConfigObject = configsByKey.get(ACS_TENANT_CONFIG_KEY);
    var acsTenantConfig = parseSafe(sessionData, acsTenantConfigObject, AcsTenantConfig.class);
    if (acsTenantConfig.isEmpty()) {
      log.info(sessionData, FAILED_TO_FIND_SETTING_MSG_TEMPLATE, ACS_TENANT_CONFIG_KEY);
    }

    var scStationConfigName = getScStationConfigName(sessionData);
    var scStationConfigObject = configsByKey.get(scStationConfigName);
    var scStationConfig = parseSafe(sessionData, scStationConfigObject, ScStationConfig.class);
    if (scStationConfig.isEmpty()) {
      log.info(sessionData, FAILED_TO_FIND_SETTING_MSG_TEMPLATE, scStationConfigName);
    }

    return succeededFuture(AcsConfig.builder()
        .scStationConfig(scStationConfig.orElse(null))
        .tenantLocaleConfig(tenantLocaleConfig.orElse(null))
        .acsTenantConfig(acsTenantConfig.orElse(null))
        .build());
  }

  private Future<ACSStatus> processAcsConfig(SessionData sessionData, AcsConfig acsConfig) {
    var builder = ACSStatus.builder();

    addLocaleConfig(sessionData, acsConfig);
    addTenantConfig(sessionData, acsConfig, builder);
    addScStationConfig(acsConfig, builder);
    builder.institutionId(sessionData.getTenant());

    return succeededFuture(builder.build());
  }

  private void addLocaleConfig(SessionData sessionData, AcsConfig acsConfig) {
    var localeConfigObject = acsConfig.getTenantLocaleConfig();
    if (localeConfigObject != null) {
      sessionData.setTimeZone(localeConfigObject.getTimezone());
      sessionData.setCurrency(CurrencyType.fromStringSafe(localeConfigObject.getCurrency()));
    }
  }

  private void addTenantConfig(SessionData sd,
      AcsConfig acsConfig, ACSStatus.ACSStatusBuilder builder) {
    var tc = acsConfig.getAcsTenantConfig();
    if (tc != null) {
      builder.onLineStatus(true);
      builder.statusUpdateOk(tc.getStatusUpdateOk());
      builder.offLineOk(tc.getOfflineOk());
      builder.protocolVersion("2.00");
      builder.supportedMessages(tc.getSupportedMessagesSet());
      sd.setInvalidCheckinStatusList(tc.getInvalidCheckinStatusesList());
      sd.setAlwaysCheckPatronPassword(isTrue(tc.getAlwaysCheckPatronPassword()));
      sd.setUsePinForPatronVerification(isTrue(tc.getUsePinForPatronVerification()));
      sd.setPatronPasswordVerificationRequired(isTrue(tc.getPatronPasswordVerificationRequired()));
    }
  }

  private void addScStationConfig(AcsConfig acsConfig, ACSStatus.ACSStatusBuilder builder) {
    var scStationConfig = acsConfig.getScStationConfig();
    if (scStationConfig != null) {
      builder.retriesAllowed(scStationConfig.getRetriesAllowed());
      builder.timeoutPeriod(scStationConfig.getTimeoutPeriod());
      builder.checkinOk(scStationConfig.getCheckinOk());
      builder.acsRenewalPolicy(scStationConfig.getAcsRenewalPolicy());
      builder.checkoutOk(scStationConfig.getCheckoutOk());
      builder.dateTimeSync(OffsetDateTime.now(clock));
      builder.libraryName(scStationConfig.getLibraryName());
      builder.terminalLocation(scStationConfig.getTerminalLocation());
    }
  }

  private Map<String, Object> groupSettingsByKey(SessionData sessionData, JsonArray settingsItems) {
    var settingsByKey = new LinkedHashMap<String, Object>();
    for (var settingsItem : settingsItems) {
      if (!(settingsItem instanceof JsonObject settingsItemObject)) {
        log.warn(sessionData, "Invalid settings item format: {}", settingsItem);
        continue;
      }

      var key = settingsItemObject.getString("key");
      if (key == null) {
        log.warn(sessionData, "Invalid settings item format: {}", settingsItem);
        continue;
      }
      settingsByKey.put(key, settingsItemObject.getValue("value"));
    }
    return settingsByKey;
  }

  private <T> Optional<T> parseSafe(SessionData sd, Object object, Class<T> targetClass) {
    if (object == null) {
      return Optional.empty();
    }

    if (targetClass.isInstance(object)) {
      return Optional.of(targetClass.cast(object));
    }

    try {
      if (object instanceof String stringValue) {
        return Optional.of(new JsonObject(stringValue).mapTo(targetClass));
      }

      if (object instanceof JsonObject jsonObject) {
        return Optional.of(jsonObject.mapTo(targetClass));
      }
    } catch (Exception e) {
      log.warn(sd, "parseSafe:: failed to parse object (default value used): {}", object, e);
      return Optional.empty();
    }

    return Optional.empty();
  }

  private static List<ConfigKey> getSettingsConfigKeys(SessionData sessionData) {
    var parameters = new ArrayList<ConfigKey>();
    parameters.add(new ConfigKey(SIP2_MODULE_SCOPE, ACS_TENANT_CONFIG_KEY));
    parameters.add(new ConfigKey(SIP2_MODULE_SCOPE, getScStationConfigName(sessionData)));
    return parameters;
  }

  private static String getScStationConfigName(SessionData sessionData) {
    return "%s.%s".formatted(SC_STATION_CONFIG_KEY, sessionData.getScLocation());
  }

  private Map<String, Object> mergeConfiguration(SessionData sessionData,
      Map<String, Object> settingsMap, Map<String, Object> configMap) {
    var mergedMap = new LinkedHashMap<>(settingsMap);
    for (var entry : configMap.entrySet()) {
      var key = entry.getKey();
      mergedMap.computeIfAbsent(key, k -> {
        log.warn(sessionData, "Using value from 'mod-configuration' by key: {}", k);
        return entry.getValue();
      });
    }
    return mergedMap;
  }

  private record ConfigKey(String scope, String key) {
  }

  @RequiredArgsConstructor
  static class LocaleRequestData implements IRequestData {

    private final SessionData sessionData;

    @Override
    public String getPath() {
      return "/locale";
    }

    @Override
    public JsonObject getBody() {
      return null;
    }

    @Override
    public SessionData getSessionData() {
      return this.sessionData;
    }
  }

  @RequiredArgsConstructor
  static class SettingsRequestData implements IRequestData {

    private final int limit;
    private final SessionData sessionData;
    private final List<ConfigKey> parameters;

    @Override
    public String getPath() {
      CqlQuery cqlQuery = null;
      for (var entry : parameters) {
        var subQuery = exactMatchByScope(entry.scope()).and(exactMatchByKey(entry.key()), true);
        cqlQuery = cqlQuery == null ? subQuery : cqlQuery.or(subQuery);
      }

      return "/settings/entries"
          + "?query=" + requireNonNull(cqlQuery).toText()
          + "&limit=" + limit
          + "&offset=0";
    }

    @Override
    public JsonObject getBody() {
      return null;
    }

    @Override
    public SessionData getSessionData() {
      return this.sessionData;
    }

  }
}
