package org.folio.edge.sip2.repositories;

import static io.vertx.core.Future.succeededFuture;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.enumerations.Messages;
import org.folio.edge.sip2.session.SessionData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
public class ConfigurationRepositoryTests {

  @Test
  public void canCreateConfigurationRepo(@Mock IResourceProvider<IRequestData> mockConfig,
      @Mock Clock clock) {
    ConfigurationRepository configRepo = new ConfigurationRepository(mockConfig, clock);
    assertNotNull(configRepo);
  }

  @Test
  public void cannotCreateConfigurationRepoWhenConfigProviderIsNull(@Mock Clock clock) {

    final NullPointerException thrown = assertThrows(
        NullPointerException.class, () -> new ConfigurationRepository(null, clock));

    assertEquals("ConfigGateway cannot be null", thrown.getMessage());
  }

  @Test
  public void canGetValidAcsStatus(Vertx vertx, VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {

    JsonObject tenantConfigObject = new JsonObject();
    tenantConfigObject.put("value", "{\"tenantId\":\"diku\",\"supportedMessages\":"
          + "[{\"messageName\": \"PATRON_INFORMATION\",\"isSupported\": \"Y\"},"
          + "{\"messageName\": \"RENEW\",\"isSupported\": \"N\"},"
          + "{\"messageName\": \"BLOCK_PATRON\",\"isSupported\": \"Y\"}],"
          +  "\"onlineStatus\": false,\"statusUpdateOk\": false,\"offlineOk\":true,"
          +  "\"protocolVersion\":\"1.23\",\"institutionId\":\"diku\","
          +   "\"screenMessage\":\"Hello, welcome\","
          +  "\"printLine\":\"testing\","
          + "\"invalidCheckinStatuses\":\"Withdrawn,Restricted\"}");

    tenantConfigObject.put("module", "edge-sip2");
    tenantConfigObject.put("configName", "acsTenantConfig");


    JsonObject scConfigObject = new JsonObject();
    scConfigObject.put("value", "{\"checkinOk\": false,\"checkoutOk\": true,"
          + "\"acsRenewalPolicy\": false,\"maxPrintWidth\" : 200,"
          +  "\"timeoutPeriod\":3,\"retriesAllowed\":2,"
          + "\"libraryName\": \"diku\",\"terminalLocation\": \"SE10\"}");
    scConfigObject.put("module", "edge-sip2");
    scConfigObject.put("configName", "selfCheckoutConfig.SE10");

    JsonArray configsArray = new JsonArray();
    configsArray.add(tenantConfigObject);
    configsArray.add(scConfigObject);

    //Need to add another JSON object to make it 3 (required for ACS Status,
    //but can get away with 2 now. The third one is to get timezone, not needed here.
    configsArray.add(new JsonObject());

    JsonObject resultsWrapper = new JsonObject();
    resultsWrapper.put("configs", configsArray);

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(succeededFuture(() -> resultsWrapper));

    Clock clock = TestUtils.getUtcFixedClock();

    ConfigurationRepository configurationRepository =
        new ConfigurationRepository(mockFolioProvider, clock);

    SessionData sessionData = TestUtils.getMockedSessionData();
    sessionData.setScLocation("SE10");

    configurationRepository.getACSStatus(sessionData).onComplete(
        testContext.succeeding(status -> testContext.verify(() -> {

          assertNotNull(status);
          assertEquals(true, status.getOnLineStatus());
          assertEquals(false, status.getCheckinOk());
          assertEquals(true, status.getCheckoutOk());
          assertEquals(false, status.getAcsRenewalPolicy());
          assertEquals(true, status.getOffLineOk());
          assertEquals(3, status.getTimeoutPeriod());
          assertEquals(2, status.getRetriesAllowed());
          assertEquals("2.00", status.getProtocolVersion());
          assertEquals("dikutest", status.getInstitutionId());
          assertEquals("diku", status.getLibraryName());
          assertEquals("SE10", status.getTerminalLocation());
          assertEquals(OffsetDateTime.now(clock), status.getDateTimeSync());
          assertFalse(sessionData.isValidCheckinStatus("Withdrawn"));
          assertFalse(sessionData.isValidCheckinStatus("Restricted"));
          assertTrue(sessionData.isValidCheckinStatus("Lost and paid"));

          assertEquals(2, status.getSupportedMessages().size());
          Messages[] supportedMsgsArr = status.getSupportedMessages().toArray(new Messages[2]);

          //note that the messages will be reordered because it's stored in a list.
          //it's up to the appropriate handler to re-present the messages in the correct order
          assertEquals(Messages.BLOCK_PATRON, supportedMsgsArr[0]);
          assertEquals(Messages.PATRON_INFORMATION, supportedMsgsArr[1]);

          testContext.completeNow();
        })));
  }

  @Test
  public void canRetrieveTenantConfiguration(
      Vertx vertx,
      VertxTestContext testContext, @Mock Clock clock) {

    List<LinkedHashMap<String, String>> configParamsList = new ArrayList<>();
    LinkedHashMap<String, String> configParamsSet = new LinkedHashMap<>();
    configParamsSet.put("module", "edge-sip2");
    configParamsSet.put("configName", "acsTenantConfig");
    String configKey = String.format("%s.%s.%s", "edge-sip2", "acsTenantConfig", "null");

    configParamsList.add(configParamsSet);

    IResourceProvider<IRequestData> resourceProvider =
        new DefaultResourceProvider("json/ACSConfigurationWithMissingConfigs.json");
    ConfigurationRepository configRepo = new ConfigurationRepository(resourceProvider, clock);

    configRepo.retrieveConfigurations(TestUtils.getMockedSessionData(),
        configParamsList).onComplete(
          testContext.succeeding(testTenantConfig -> testContext.verify(() -> {
            assertNotNull(testTenantConfig);

            JsonObject config = testTenantConfig.get(configKey);

            assertEquals("dikutest",
                config.getString("tenantId"));
            assertEquals("Krona", config.getString("currencyType"));

            testContext.completeNow();
          })));
  }

  @Test
  public void canRetrieveLocaleConfigurationWithAlternateCurrency(
      Vertx vertx,
      VertxTestContext testContext, @Mock Clock clock) {

    List<LinkedHashMap<String, String>> configParamsList = new ArrayList<>();
    LinkedHashMap<String, String> configParamsSet = new LinkedHashMap<>();
    configParamsSet.put("module", "edge-sip2");
    configParamsSet.put("configName", "acsTenantConfig");
    String configKey = String.format("%s.%s.%s", "ORG", "localeSettings", "null");

    configParamsList.add(configParamsSet);

    IResourceProvider<IRequestData> resourceProvider =
        new DefaultResourceProvider("json/DefaultACSConfigurationNonDefaultedCurrency.json");
    ConfigurationRepository configRepo = new ConfigurationRepository(resourceProvider, clock);

    configRepo.retrieveConfigurations(TestUtils.getMockedSessionData(),
        configParamsList).onComplete(
        testContext.succeeding(testTenantConfig -> testContext.verify(() -> {
          assertNotNull(testTenantConfig);

          JsonObject config = testTenantConfig.get(configKey);

          assertEquals("EUR", config.getString("currency"));

          testContext.completeNow();
        })));
  }
}

