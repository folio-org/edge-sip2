package org.folio.edge.sip2.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.time.LocalDate;

import org.folio.edge.sip2.domain.messages.enumerations.Messages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class ConfigurationRepositoryTests {

  @Test
  public void canCreateConfigurationRepo() {
    @SuppressWarnings("unchecked")
    IResourceProvider<Object> mockConfig = mock(IResourceProvider.class);
    ConfigurationRepository configRepo = new ConfigurationRepository(mockConfig);
    assertNotNull(configRepo);
  }

  @Test
  public void cannotCreateConfigurationRepoWhenConfigProviderIsNull() {
    try {
      new ConfigurationRepository(null);
      fail("expected an exception to be thrown");
    } catch (IllegalArgumentException ex) {
      assertEquals("configGateway is null", ex.getMessage());
    }
  }

  @Test
  public void canGetValidAcsStatus(Vertx vertx, VertxTestContext testContext) {

    JsonArray supportedMsgs = new JsonArray();
    supportedMsgs.add(new JsonObject().put("messageName", "PATRON_INFORMATION")
                            .put("isSupported","Y"));
    supportedMsgs.add(new JsonObject().put("messageName", "RENEW")
                            .put("isSupported","N"));
    supportedMsgs.add(new JsonObject().put("messageName", "BLOCK_PATRON")
                            .put("isSupported","Y"));

    JsonObject acsConfig = new JsonObject();
    acsConfig.put("onlineStatus", false);
    acsConfig.put("checkinOk", false);
    acsConfig.put("checkoutOk", true);
    acsConfig.put("acsRenewalPolicy", false);
    acsConfig.put("statusUpdateOk", false);
    acsConfig.put("offlineOk", true);
    acsConfig.put("timeoutPeriod", 3);
    acsConfig.put("retriesAllowed", 2);
    acsConfig.put("dateTimeSync", "2019-04-05 13:26:13");
    acsConfig.put("protocolVersion", "1.23");
    acsConfig.put("institutionId", "fs00000010");
    acsConfig.put("printLine", "testing");
    acsConfig.put("libraryName", "Chalmers");
    acsConfig.put("screenMessage", "Hello, welcome");
    acsConfig.put("terminalLocation", "SE10");
    acsConfig.put("supportedMessages", supportedMsgs);

    JsonObject defaultConfigurations = new JsonObject();
    defaultConfigurations.put("acsConfiguration", acsConfig);

    @SuppressWarnings("unchecked")
    IResourceProvider<Object> mockConfigProvider = mock(IResourceProvider.class);
    when(mockConfigProvider.retrieveResource(null))
      .thenReturn(Future.succeededFuture(defaultConfigurations));

    ConfigurationRepository configurationRepository =
        new ConfigurationRepository(mockConfigProvider);
    configurationRepository.getACSStatus().setHandler(
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
          assertEquals("testing", status.getPrintLine());
          assertEquals("Chalmers", status.getLibraryName());
          assertEquals("Hello, welcome", status.getScreenMessage());
          assertEquals("SE10", status.getTerminalLocation());

          LocalDate currentDate = LocalDate.now();
          assertEquals(currentDate.getYear(), status.getDateTimeSync().getYear());
          assertEquals(currentDate.getMonth(), status.getDateTimeSync().getMonth());
          assertEquals(currentDate.getDayOfMonth(), status.getDateTimeSync().getDayOfMonth());

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
      VertxTestContext testContext) {
    DefaultResourceProvider resourceProvider = new DefaultResourceProvider();
    ConfigurationRepository configRepo = new ConfigurationRepository(resourceProvider);

    configRepo.retrieveTenantConfiguration("fs00000010test").setHandler(
        testContext.succeeding(testTenantConfig -> testContext.verify(() -> {
          assertNotNull(testTenantConfig);
          assertEquals("fs00000010test",
              testTenantConfig.getString("tenantId"));
          assertEquals("ASCII", testTenantConfig.getString("encoding"));

          testContext.completeNow();
        })));
  }
}
