package org.folio.edge.sip2.repositories;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.Sip2LogAdapter;
import org.folio.edge.sip2.utils.Utils;
import org.folio.util.PercentCodec;

public class ConfigurationRepository {

  private IResourceProvider<IRequestData> resourceProvider;
  private final Sip2LogAdapter log;

  static final String TENANT_CONFIG_NAME = "acsTenantConfig";
  static final String SC_STATION_CONFIG_NAME = "selfCheckoutConfig";
  static final String CONFIG_MODULE = "edge-sip2";
  private static final String KEY_CONFIG_NAME = "configName";
  private static final String KEY_CONFIG_MODULE = "module";

  /**
   * Constructor that takes an IResourceProvider.
   *
   * @param resourceProvider This can be DefaultResourceProvider or any provider in the future.
   */

  @Inject
  public ConfigurationRepository(IResourceProvider<IRequestData> resourceProvider) {
    this.resourceProvider = Objects.requireNonNull(resourceProvider,
        "ConfigGateway cannot be null");
    log = Sip2LogAdapter.getLogger(ConfigurationRepository.class);
  }

  /**
   * Method that retrieves the configuration from a resource provider.
   *
   * @param sessionData sessionData containing tenant ID to retrieve desired tenant configuration.
   * @return A Map of keys and JSON config objects
   */
  public Future<Map<String, Object>> retrieveConfigurations(SessionData sessionData) {
    var configParameters = getConfigParameters(sessionData);
    final Map<String, String> headers = new HashMap<>();
    headers.put("accept", "application/json");

    ConfigurationRequestData requestData = new ConfigurationRequestData(null,
        headers, sessionData, configParameters);

    Future<IResource> future = resourceProvider.retrieveResource(requestData);
    return future.compose(resource -> {
      final JsonObject scConfiguration = resource.getResource();
      JsonArray configs = scConfiguration.getJsonArray("configs");
      int totalConfigs = configs.size();
      /*
        Previously, this was a hard fail if there were fewer configurations found than were
        specified in the queries. Changing it to be more forgiving but to issue a warning
       */
      if (totalConfigs < configParameters.size()) {
        log.warn(sessionData, "Found fewer configurations than expected. Expected {} but found {}",
            configParameters.size(), totalConfigs);
      }

      if (Utils.isStringNullOrEmpty(sessionData.getScLocation())) {
        log.warn(sessionData, "Configuration: No value found for Location Code.");
      }

      Map<String, Object> resultJsonConfigs = new LinkedHashMap<>();

      for (int i = 0; i < totalConfigs; i++) {
        JsonObject config = configs.getJsonObject(i);
        log.debug(sessionData, "Configuration is {}", config.encode());
        String configName = config.getString(KEY_CONFIG_NAME);

        log.debug(sessionData, "Getting configuration with key {}", configName);
        String configurationString = config.getString("value");
        if (!Utils.isStringNullOrEmpty(configurationString)) {
          resultJsonConfigs.put(configName, configurationString);
        } else {
          log.warn(sessionData, "Getting no value from config store for configuration string: {}",
              configurationString);
        }
      }
      return Future.succeededFuture(resultJsonConfigs);
    });
  }

  private static List<LinkedHashMap<String, String>> getConfigParameters(SessionData sessionData) {
    LinkedHashMap<String, String> tenantLevelQueryParams = new LinkedHashMap<>();
    tenantLevelQueryParams.put(KEY_CONFIG_MODULE, CONFIG_MODULE);
    tenantLevelQueryParams.put(KEY_CONFIG_NAME, TENANT_CONFIG_NAME);

    LinkedHashMap<String, String> scLevelQueryParams = new LinkedHashMap<>();
    scLevelQueryParams.put(KEY_CONFIG_MODULE, CONFIG_MODULE);
    scLevelQueryParams.put(KEY_CONFIG_NAME,
        SC_STATION_CONFIG_NAME + "." + sessionData.getScLocation());

    LinkedHashMap<String, String> tenantTimeZoneQueryParams = new LinkedHashMap<>();
    tenantTimeZoneQueryParams.put(KEY_CONFIG_MODULE, "ORG");
    tenantTimeZoneQueryParams.put(KEY_CONFIG_NAME, "localeSettings");

    List<LinkedHashMap<String, String>> kvpQueryParamsList = new ArrayList<>();
    kvpQueryParamsList.add(tenantLevelQueryParams);
    kvpQueryParamsList.add(scLevelQueryParams);
    kvpQueryParamsList.add(tenantTimeZoneQueryParams);
    return kvpQueryParamsList;
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
      var pathStringBuilder = new StringBuilder();
      for (int i = 0; i < configQueryParams.size(); i++) {
        if (i > 0) {
          pathStringBuilder.append(" OR ");
        }
        pathStringBuilder.append("(");
        pathStringBuilder.append(Utils.buildQueryString(configQueryParams.get(i), " AND ", "=="));
        pathStringBuilder.append(")");
      }
      String partialPath = pathStringBuilder.toString();
      log.debug(sessionData, "Configuration path before encoding: {}", partialPath);
      String path =  "/configurations/entries?query=" + PercentCodec.encode(partialPath);

      log.debug(sessionData, "Parsed mod-config path: {}", path);

      return path;
    }
  }
}
