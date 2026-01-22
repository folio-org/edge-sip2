package org.folio.edge.sip2.repositories;

import static io.vertx.core.Future.succeededFuture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.Map;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.okapi.common.UrlDecoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({ VertxExtension.class, MockitoExtension.class })
class ConfigurationRepositoryTests {

  @InjectMocks private ConfigurationRepository configurationRepository;
  @Mock private IResourceProvider<IRequestData> resourceProvider;
  @Captor private ArgumentCaptor<IRequestData> requestDataCaptor;

  @Test
  void cannotCreateConfigurationRepoWhenConfigProviderIsNull() {
    assertThatThrownBy(() -> new ConfigurationRepository(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("ConfigGateway cannot be null");
  }

  @Test
  void retrieveConfigurations_positive(VertxTestContext context) {
    var configurationResponse = new JsonObject()
        .put("totalRecords", 3)
        .put("configs", new JsonArray()
            .add(configValue("localeSettings", "ORG", localeSettingsConfig()))
            .add(configValue("acsTenantConfig", "edge-sip2", acsTenantConfig()))
            .add(configValue("selfCheckoutConfig.SE10", "edge-sip2", scConfig())));

    when(resourceProvider.retrieveResource(requestDataCaptor.capture()))
        .thenReturn(succeededFuture(() -> configurationResponse));

    var sessionData = TestUtils.getMockedSessionData();
    sessionData.setScLocation("SE10");

    configurationRepository.retrieveConfigurations(sessionData).onComplete(
        context.succeeding(configMap -> context.verify(() -> {
          assertThat(configMap).isEqualTo(Map.of(
              "selfCheckoutConfig.SE10", scConfig().encode(),
              "acsTenantConfig", acsTenantConfig().encode(),
              "localeSettings", localeSettingsConfig().encode()));
          context.completeNow();

          var capturedValue = requestDataCaptor.getValue();
          assertThat(UrlDecoder.decode(capturedValue.getPath())).isEqualTo(
              "/configurations/entries"
                  + "?query=(module==\"edge-sip2\" AND configName==\"acsTenantConfig\") "
                  + "OR (module==\"edge-sip2\" AND configName==\"selfCheckoutConfig.SE10\") "
                  + "OR (module==\"ORG\" AND configName==\"localeSettings\")");
          assertThat(capturedValue.getHeaders()).contains(entry("accept", "application/json"));
          assertThat(capturedValue.getBody()).isNull();
          assertThat(capturedValue.getSessionData()).isNotNull();
        })));
  }

  @Test
  void retrieveConfigurations_positive_scLocationIsNull(VertxTestContext context) {
    var configurationResponse = new JsonObject()
        .put("totalRecords", 3)
        .put("configs", new JsonArray()
            .add(configValue("localeSettings", "ORG", localeSettingsConfig()))
            .add(configValue("acsTenantConfig", "edge-sip2", acsTenantConfig()))
            .add(configValue("selfCheckoutConfig.null", "edge-sip2", scConfig())));

    when(resourceProvider.retrieveResource(any()))
        .thenReturn(succeededFuture(() -> configurationResponse));

    var sessionData = TestUtils.getMockedSessionData();
    sessionData.setScLocation(null);

    configurationRepository.retrieveConfigurations(sessionData).onComplete(
        context.succeeding(configMap -> context.verify(() -> {
          assertThat(configMap).isEqualTo(Map.of(
              "selfCheckoutConfig.null", scConfig().encode(),
              "acsTenantConfig", acsTenantConfig().encode(),
              "localeSettings", localeSettingsConfig().encode()));
          context.completeNow();
        })));
  }

  @Test
  void retrieveConfigurations_positive_nullStringInValue(VertxTestContext context) {
    var configurationResponse = new JsonObject()
        .put("totalRecords", 3)
        .put("configs", new JsonArray()
            .add(configValue("localeSettings", "ORG", null))
            .add(configValue("acsTenantConfig", "edge-sip2", acsTenantConfig()))
            .add(configValue("selfCheckoutConfig.SE10", "edge-sip2", scConfig())));

    when(resourceProvider.retrieveResource(any()))
        .thenReturn(succeededFuture(() -> configurationResponse));

    var sessionData = TestUtils.getMockedSessionData();
    sessionData.setScLocation("SE10");

    configurationRepository.retrieveConfigurations(sessionData).onComplete(
        context.succeeding(configMap -> context.verify(() -> {
          assertThat(configMap).isEqualTo(Map.of(
              "selfCheckoutConfig.SE10", scConfig().encode(),
              "acsTenantConfig", acsTenantConfig().encode()));
          context.completeNow();
        })));
  }

  @Test
  void retrieveConfigurations_positive_fewerConfigurationFound(VertxTestContext context) {
    var configurationResponse = new JsonObject()
        .put("totalRecords", 2)
        .put("configs", new JsonArray()
            .add(configValue("localeSettings", "ORG", localeSettingsConfig()))
            .add(configValue("selfCheckoutConfig.SE10", "edge-sip2", scConfig())));

    when(resourceProvider.retrieveResource(any()))
        .thenReturn(succeededFuture(() -> configurationResponse));

    var sessionData = TestUtils.getMockedSessionData();
    sessionData.setScLocation("SE10");

    configurationRepository.retrieveConfigurations(sessionData).onComplete(
        context.succeeding(configMap -> context.verify(() -> {
          assertThat(configMap).isEqualTo(Map.of(
              "selfCheckoutConfig.SE10", scConfig().encode(),
              "localeSettings", localeSettingsConfig().encode()));
          context.completeNow();
        })));
  }

  private static JsonObject configValue(String name, String module, JsonObject value) {
    return new JsonObject()
        .put("configName", name)
        .put("module", module)
        .put("value", value != null ? value.encode() : null);
  }

  private static JsonObject localeSettingsConfig() {
    return new JsonObject()
        .put("locale", "en-US")
        .put("timezone", "America/New_York")
        .put("currency", "USD");
  }

  private static JsonObject scConfig() {
    return new JsonObject()
        .put("checkinOk", false)
        .put("checkoutOk", true)
        .put("acsRenewalPolicy", false)
        .put("maxPrintWidth", 200)
        .put("timeoutPeriod", 3)
        .put("retriesAllowed", 2)
        .put("libraryName", "diku")
        .put("terminalLocation", "testLocation");
  }

  private static JsonObject acsTenantConfig() {
    return new JsonObject()
        .put("tenantId", "diku")
        .put("supportedMessages", new JsonArray()
            .add(new JsonObject().put("messageName", "PATRON_INFORMATION").put("isSupported", "Y"))
            .add(new JsonObject().put("messageName", "RENEW").put("isSupported", "N"))
            .add(new JsonObject().put("messageName", "BLOCK_PATRON").put("isSupported", "Y")))
        .put("onlineStatus", false)
        .put("statusUpdateOk", false)
        .put("offlineOk", true)
        .put("protocolVersion", "1.23")
        .put("institutionId", "diku")
        .put("screenMessage", "Hello, welcome")
        .put("printLine", "testing")
        .put("invalidCheckinStatuses", "Withdrawn,Restricted");
  }
}

