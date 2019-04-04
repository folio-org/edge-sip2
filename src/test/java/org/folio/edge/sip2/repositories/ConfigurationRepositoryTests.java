package org.folio.edge.sip2.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import org.folio.edge.sip2.domain.messages.enumerations.Messages;
import org.folio.edge.sip2.domain.messages.responses.ACSStatus;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

public class ConfigurationRepositoryTests {

  @Test
  public void canCreateConfigurationRepo(){
    IConfigurationProvider mockConfig = mock(IConfigurationProvider.class);
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
  public void canGetValidAcsStatus(){

    JsonArray arr = new JsonArray();
    arr.add(new JsonObject().put("messageName", "PATRON_INFORMATION")
                            .put("isSupported","Y"));
    arr.add(new JsonObject().put("messageName", "RENEW")
                            .put("isSupported","N"));
    arr.add(new JsonObject().put("messageName", "BLOCK_PATRON")
                            .put("isSupported","Y"));

    JsonObject mockConfig = new JsonObject();
    mockConfig.put("onlineStatus", false);
    mockConfig.put("checkinOk", false);
    mockConfig.put("checkoutOk", true);
    mockConfig.put("acsRenewalPolicy", false);
    mockConfig.put("statusUpdateOk", false);
    mockConfig.put("offlineOk", true);
    mockConfig.put("timeoutPeriod", 3);
    mockConfig.put("retriesAllowed", 2);
    mockConfig.put("dateTimeSync", "2019-04-05 13:26:13");
    mockConfig.put("protocolVersion", "1.23");
    mockConfig.put("institutionId", "fs00000010");
    mockConfig.put("printLine", "testing");
    mockConfig.put("libraryName", "Chalmers");
    mockConfig.put("screenMessage", "Hello, welcome");
    mockConfig.put("terminalLocation", "SE10");
    mockConfig.put("supportedMessages", arr);

    IConfigurationProvider mockConfigProvider = mock(IConfigurationProvider.class);
    when(mockConfigProvider.retrieveConfiguration("fs00000010")).thenReturn(mockConfig);

    ConfigurationRepository configurationRepository = new ConfigurationRepository(mockConfigProvider);
    final ACSStatus status  = configurationRepository.getACSStatus("fs00000010");

    assertNotNull(status);
    assertEquals(false, status.getOnLineStatus());
    assertEquals( false, status.getCheckinOk());
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
    assertEquals("2019-04-05T13:26:13+02:00[Europe/Paris]", status.getDateTimeSync().toString());

    assertEquals(2, status.getSupportedMessages().size());
    Messages[] supportedMsgs = status.getSupportedMessages().toArray(new Messages[2]);

    //note that the messages will be reordered because it's stored in a list.
    //it's up to the appropriate handler to re-present the messages in the correct order
    assertEquals(Messages.BLOCK_PATRON, supportedMsgs[0]);
    assertEquals(Messages.PATRON_INFORMATION, supportedMsgs[1]);
  }

}
