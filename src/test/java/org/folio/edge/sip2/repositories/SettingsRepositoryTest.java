package org.folio.edge.sip2.repositories;

import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.edge.sip2.api.support.TestUtils.getMockedSessionData;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.enumerations.Messages;
import org.folio.edge.sip2.domain.messages.responses.ACSStatus;
import org.folio.edge.sip2.session.SessionData;
import org.folio.okapi.common.UrlDecoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({ VertxExtension.class, MockitoExtension.class })
class SettingsRepositoryTest {

  @InjectMocks private SettingsRepository settingsRepository;
  @Spy private Clock clock = TestUtils.getUtcFixedClock();
  @Mock private ConfigurationRepository configurationRepository;
  @Mock private IResourceProvider<IRequestData> resourceProvider;
  @Captor private ArgumentCaptor<IRequestData> requestCaptor;

  @Test
  void getACSStatus_positive_allValuesFound(VertxTestContext testContext) {
    var settingsResponse = new JsonObject()
        .put("items", new JsonArray(List.of(acsTenantConfigValue(), scConfigValue())))
        .put("resultInfo", new JsonObject().put("totalRecords", 2));

    when(resourceProvider.retrieveResource(requestCaptor.capture()))
        .thenReturn(succeededFuture(SettingsRepositoryTest::tenantLocale))
        .thenReturn(succeededFuture(() -> settingsResponse));

    var sessionData = getMockedSessionData();
    settingsRepository.getACSStatus(sessionData).onComplete(
        testContext.succeeding(status -> testContext.verify(() -> {
          validateStatusAndSessionData(status, sessionData);

          var capturedValues = requestCaptor.getAllValues();
          assertThat(capturedValues).hasSize(2);
          var capturedLocaleRequest = capturedValues.getFirst();
          var capturedSettingsRequest = capturedValues.getLast();

          assertThat(capturedLocaleRequest.getPath()).isEqualTo("/locale");
          assertThat(capturedLocaleRequest.getSessionData()).isNotNull();
          assertThat(capturedLocaleRequest.getBody()).isNull();
          assertThat(capturedLocaleRequest.getHeaders()).isEmpty();

          assertThat(capturedSettingsRequest.getBody()).isNull();
          assertThat(capturedSettingsRequest.getHeaders()).isEmpty();
          assertThat(capturedSettingsRequest.getSessionData()).isNotNull();
          assertThat(UrlDecoder.decode(capturedSettingsRequest.getPath()))
              .isEqualTo("/settings/entries?query="
                  + "(scope==\"edge-sip2\" and key==\"acsTenantConfig\") "
                  + "or (scope==\"edge-sip2\" and key==\"selfCheckoutConfig.testLocation\")"
                  + "&limit=100&offset=0");

          testContext.completeNow();
        })));
  }

  @Test
  void getACSStatus_negative_failedToRetrieveSettings(VertxTestContext testContext) {
    when(resourceProvider.retrieveResource(any()))
        .thenReturn(succeededFuture(SettingsRepositoryTest::tenantLocale))
        .thenReturn(failedFuture(new FolioRequestThrowable("Internal server error")));

    var sessionData = getMockedSessionData();
    settingsRepository.getACSStatus(sessionData).onComplete(
        testContext.failing(err -> testContext.verify(() -> {
          assertThat(err)
              .isInstanceOf(FolioRequestThrowable.class)
              .hasMessage("Internal server error");
          testContext.completeNow();
        })));
  }

  @Test
  void getACSStatus_positive_localeNotFound(VertxTestContext testContext) {
    var settingsResponse = new JsonObject()
        .put("items", new JsonArray(List.of(acsTenantConfigValue(), scConfigValue())))
        .put("resultInfo", new JsonObject().put("totalRecords", 2));

    when(resourceProvider.retrieveResource(any()))
        .thenReturn(failedFuture(new FolioRequestThrowable("not found")))
        .thenReturn(succeededFuture(() -> settingsResponse));

    when(configurationRepository.retrieveConfigurations(any()))
        .thenReturn(succeededFuture(fullConfigurationResponse()));

    var sessionData = getMockedSessionData();
    settingsRepository.getACSStatus(sessionData)
        .onComplete(testContext.succeeding(status -> testContext.verify(() -> {
          validateStatusAndSessionData(status, sessionData);
          testContext.completeNow();
        })));
  }

  @Test
  void getACSStatus_positive_settingsNotFound(VertxTestContext testContext) {
    var settingsResponse = new JsonObject()
        .put("items", new JsonArray())
        .put("resultInfo", new JsonObject().put("totalRecords", 0));

    when(resourceProvider.retrieveResource(any()))
        .thenReturn(succeededFuture(SettingsRepositoryTest::tenantLocale))
        .thenReturn(succeededFuture(() -> settingsResponse));

    when(configurationRepository.retrieveConfigurations(any()))
        .thenReturn(succeededFuture(fullConfigurationResponse()));

    var sessionData = getMockedSessionData();
    settingsRepository.getACSStatus(sessionData)
        .onComplete(testContext.succeeding(status -> testContext.verify(() -> {
          validateStatusAndSessionData(status, sessionData);
          testContext.completeNow();
        })));
  }

  @Test
  void getACSStatus_positive_emptyConfigurationAndSettings(VertxTestContext testContext) {
    var settingsResponse = new JsonObject()
        .put("items", new JsonArray())
        .put("resultInfo", new JsonObject().put("totalRecords", 0));

    when(resourceProvider.retrieveResource(any()))
        .thenReturn(failedFuture(new FolioRequestThrowable("not found")))
        .thenReturn(succeededFuture(() -> settingsResponse));

    when(configurationRepository.retrieveConfigurations(any()))
        .thenReturn(succeededFuture(Collections.emptyMap()));

    var sessionData = getMockedSessionData();
    settingsRepository.getACSStatus(sessionData)
        .onComplete(testContext.succeeding(status -> testContext.verify(() -> {
          assertThat(status).isEqualTo(ACSStatus.builder().institutionId("dikutest").build());
          testContext.completeNow();
        })));
  }

  @Test
  void getACSStatus_negative_invalidJsonValue(VertxTestContext testContext) {
    var acsTenantConfigValue = acsTenantConfigValue().put("value", false);
    var settingsResponse = new JsonObject()
        .put("items", new JsonArray(List.of(scConfigValue(), acsTenantConfigValue)))
        .put("resultInfo", new JsonObject().put("totalRecords", 2));

    when(resourceProvider.retrieveResource(any()))
        .thenReturn(succeededFuture(SettingsRepositoryTest::tenantLocale))
        .thenReturn(succeededFuture(() -> settingsResponse));

    var sessionData = getMockedSessionData();
    settingsRepository.getACSStatus(sessionData)
        .onComplete(testContext.succeeding(status -> testContext.verify(() -> {
          assertThat(status)
              .usingRecursiveComparison()
              .ignoringFields("dateTimeSync")
              .isEqualTo(acsStatusWithoutAcsTenantConfig());
          testContext.completeNow();
        })));
  }

  @Test
  void getACSStatus_positive_invalidSettingItem(VertxTestContext testContext) {
    var settingsResponse = new JsonObject()
        .put("items", new JsonArray(List.of(scConfigValue(), "test")))
        .put("resultInfo", new JsonObject().put("totalRecords", 2));

    when(resourceProvider.retrieveResource(any()))
        .thenReturn(succeededFuture(SettingsRepositoryTest::tenantLocale))
        .thenReturn(succeededFuture(() -> settingsResponse));

    when(configurationRepository.retrieveConfigurations(any()))
        .thenReturn(succeededFuture(fullConfigurationResponse()));

    var sessionData = getMockedSessionData();
    settingsRepository.getACSStatus(sessionData)
        .onComplete(testContext.succeeding(status -> testContext.verify(() -> {
          validateStatusAndSessionData(status, sessionData);
          testContext.completeNow();
        })));
  }

  @Test
  void getACSStatus_positive_settingItemWithoutKey(VertxTestContext testContext) {
    var acsTenantConfigValue = acsTenantConfigValue();
    acsTenantConfigValue.remove("key");
    var settingsResponse = new JsonObject()
        .put("items", new JsonArray(List.of(scConfigValue(), acsTenantConfigValue)))
        .put("resultInfo", new JsonObject().put("totalRecords", 2));

    when(resourceProvider.retrieveResource(any()))
        .thenReturn(succeededFuture(SettingsRepositoryTest::tenantLocale))
        .thenReturn(succeededFuture(() -> settingsResponse));

    when(configurationRepository.retrieveConfigurations(any()))
        .thenReturn(succeededFuture(fullConfigurationResponse()));

    var sessionData = getMockedSessionData();
    settingsRepository.getACSStatus(sessionData)
        .onComplete(testContext.succeeding(status -> testContext.verify(() -> {
          validateStatusAndSessionData(status, sessionData);
          testContext.completeNow();
        })));
  }

  @Test
  void getACSStatus_negative_invalidJsonValueString(VertxTestContext testContext) {
    var acsTenantConfigValue = acsTenantConfigValue()
        .put("value", "{\"test");
    var settingsResponse = new JsonObject()
        .put("items", new JsonArray(List.of(scConfigValue(), acsTenantConfigValue)))
        .put("resultInfo", new JsonObject().put("totalRecords", 2));

    when(resourceProvider.retrieveResource(any()))
        .thenReturn(succeededFuture(SettingsRepositoryTest::tenantLocale))
        .thenReturn(succeededFuture(() -> settingsResponse));

    var sessionData = getMockedSessionData();
    settingsRepository.getACSStatus(sessionData)
        .onComplete(testContext.succeeding(status -> testContext.verify(() -> {
          assertThat(status)
              .usingRecursiveComparison()
              .ignoringFields("dateTimeSync")
              .isEqualTo(acsStatusWithoutAcsTenantConfig());
          testContext.completeNow();
        })));
  }

  private void validateStatusAndSessionData(ACSStatus status, SessionData sessionData) {
    var currentTs = OffsetDateTime.now(clock).truncatedTo(SECONDS);
    assertThat(status)
        .satisfies(statusVal -> assertThat(statusVal.getDateTimeSync())
            .isAfterOrEqualTo(currentTs)
            .isBeforeOrEqualTo(currentTs.plusSeconds(1)))
        .usingRecursiveComparison()
        .ignoringFields("dateTimeSync")
        .isEqualTo(acsStatus());
    assertFalse(sessionData.isValidCheckinStatus("WITHDRAWN"));
    assertFalse(sessionData.isValidCheckinStatus("restricted"));
    assertTrue(sessionData.isValidCheckinStatus("Lost and paid"));
    assertTrue(sessionData.isValidCheckinStatus("AVAILABLE"));
  }

  private static JsonObject tenantLocale() {
    return new JsonObject()
        .put("locale", "en-US")
        .put("timezone", "America/New_York")
        .put("currency", "USD")
        .put("numberingSystem", "latn");
  }

  private static ACSStatus acsStatus() {
    return ACSStatus.builder()
        .onLineStatus(true)
        .checkinOk(false)
        .checkoutOk(true)
        .acsRenewalPolicy(false)
        .offLineOk(true)
        .statusUpdateOk(false)
        .timeoutPeriod(3)
        .retriesAllowed(2)
        .protocolVersion("2.00")
        .institutionId("dikutest")
        .libraryName("diku")
        .terminalLocation("testLocation")
        .supportedMessages(Set.of(Messages.BLOCK_PATRON, Messages.PATRON_INFORMATION))
        .build();
  }

  private static ACSStatus acsStatusWithoutAcsTenantConfig() {
    return ACSStatus.builder()
        .checkinOk(false)
        .checkoutOk(true)
        .acsRenewalPolicy(false)
        .timeoutPeriod(3)
        .retriesAllowed(2)
        .institutionId("dikutest")
        .libraryName("diku")
        .terminalLocation("testLocation")
        .build();
  }

  private static JsonObject scConfigValue() {
    return new JsonObject()
        .put("key", "selfCheckoutConfig.testLocation")
        .put("scope", "edge-sip2")
        .put("value", scConfig());
  }

  private static JsonObject acsTenantConfigValue() {
    return new JsonObject()
        .put("key", "acsTenantConfig")
        .put("scope", "edge-sip2")
        .put("value", acsTenantConfig());
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

  private static Map<String, Object> fullConfigurationResponse() {
    return Map.of(
        "selfCheckoutConfig.testLocation", scConfig().encode(),
        "acsTenantConfig", acsTenantConfig().encode(),
        "localeSettings", tenantLocale().encode()
    );
  }
}
