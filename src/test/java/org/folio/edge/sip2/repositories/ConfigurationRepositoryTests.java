package org.folio.edge.sip2.repositories;

import static io.vertx.core.Future.succeededFuture;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

import java.time.Clock;
import java.time.ZonedDateTime;

import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.enumerations.Messages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
public class ConfigurationRepositoryTests {

  @Test
  public void canCreateConfigurationRepo(@Mock IResourceProvider<Object> mockConfig,
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
  public void canGetValidAcsStatus(Vertx vertx, VertxTestContext testContext) {

    JsonObject configObject = new JsonObject();
    configObject.put("value", "{\"tenantId\":\"fs00000010\",\"supportedMessages\":"
          + "[{\"messageName\": \"PATRON_INFORMATION\",\"isSupported\": \"Y\"},"
          + "{\"messageName\": \"RENEW\",\"isSupported\": \"N\"},"
          + "{\"messageName\": \"BLOCK_PATRON\",\"isSupported\": \"Y\"}],"
          +  "\"onlineStatus\": false,\"statusUpdateOk\": false,\"offlineOk\":true,"
          +  "\"timeoutPeriod\":3,\"retriesAllowed\":2,"
          +  "\"protocolVersion\":\"1.23\",\"institutionId\":\"fs00000010\","
          +   "\"screenMessage\":\"Hello, welcome\","
          +  "\"printLine\":\"testing\",\"checkinOk\":false,"
          +   "\"checkoutOk\":true,\"acsRenewalPolicy\":false,"
          +  "\"libraryName\":\"Chalmers\",\"terminalLocation\":\"SE10\"}");

    JsonArray configsArray = new JsonArray();
    configsArray.add(configObject);

    JsonObject resultsWrapper = new JsonObject();
    resultsWrapper.put("configs", configsArray);

    IResourceProvider<Object> mockFolioProvider = mock(IResourceProvider.class);

    when(mockFolioProvider.retrieveResource(any()))
      .thenReturn(succeededFuture(() -> resultsWrapper));

    Clock clock = TestUtils.getUtcFixedClock();

    ConfigurationRepository configurationRepository =
        new ConfigurationRepository(mockFolioProvider, clock);

    configurationRepository.getACSStatus(TestUtils.getMockedSessionData()).setHandler(
        testContext.succeeding(status -> testContext.verify(() -> {

          assertNotNull(status);
          assertEquals(false, status.getOnLineStatus());
          assertEquals(false, status.getCheckinOk());
          assertEquals(true, status.getCheckoutOk());
          assertEquals(false, status.getAcsRenewalPolicy());
          assertEquals(true, status.getOffLineOk());
          assertEquals(3, status.getTimeoutPeriod());
          assertEquals(2, status.getRetriesAllowed());
          assertEquals("1.23", status.getProtocolVersion());
          assertEquals("fs00000010", status.getInstitutionId());
          assertEquals("Chalmers", status.getLibraryName());
          assertEquals("SE10", status.getTerminalLocation());
          assertEquals(ZonedDateTime.now(clock), status.getDateTimeSync());

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
    IResourceProvider resourceProvider = new DefaultResourceProvider();
    ConfigurationRepository configRepo = new ConfigurationRepository(resourceProvider, clock);

    configRepo.retrieveConfiguration(TestUtils.getMockedSessionData(),
        ConfigurationRepository.CONFIG_MODULE,
        ConfigurationRepository.TENANT_CONFIG_NAME, "").setHandler(
          testContext.succeeding(testTenantConfig -> testContext.verify(() -> {
            assertNotNull(testTenantConfig);
            assertEquals("fs00000010test",
                testTenantConfig.getString("tenantId"));
            assertEquals("Krona", testTenantConfig.getString("currencyType"));

            testContext.completeNow();
          })));
  }
}
