package org.folio.edge.sip2.repositories;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.enumerations.Messages;
import org.folio.edge.sip2.domain.messages.responses.ACSStatus;

public class ConfigurationRepository {

  private IConfigurationProvider configurationProvider;
  private final Logger log;

  /**
   * @param configProvider
   */
  public ConfigurationRepository(IConfigurationProvider configProvider) {
    if (configProvider == null) {
      throw new IllegalArgumentException("configGateway is null");
    }
    this.configurationProvider = configProvider;
    log = LogManager.getLogger(MethodHandles.lookup().lookupClass());
  }

  /**
   * @param tenantId
   * @return
   */
  public ACSStatus getACSStatus(String tenantId) {
    JsonObject jsonConfig = configurationProvider.retrieveConfiguration(tenantId);

    ACSStatus acsStatus = null;
    if (jsonConfig != null) {
      ACSStatus.ACSStatusBuilder builder = ACSStatus.builder();
      builder.checkinOk(jsonConfig.getBoolean("onlineStatus"));
      builder.acsRenewalPolicy(jsonConfig.getBoolean("acsRenewalPolicy"));
      builder.checkoutOk(jsonConfig.getBoolean("checkoutOk"));
      builder.dateTimeSync(LocalDateTime.parse(jsonConfig.getString("dateTimeSync"),
                                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                                .atZone(ZoneId.of("Europe/Paris")));
      builder.institutionId(jsonConfig.getString("institutionId"));
      builder.libraryName(jsonConfig.getString("libraryName"));
      builder.offLineOk(jsonConfig.getBoolean("checkoutOk"));
      builder.onLineStatus(jsonConfig.getBoolean("onlineStatus"));
      builder.printLine(jsonConfig.getString("printLine"));
      builder.protocolVersion(jsonConfig.getString("protocolVersion"));
      builder.retriesAllowed(jsonConfig.getInteger("retriesAllowed"));
      builder.screenMessage(jsonConfig.getString("screenMessage"));
      builder.statusUpdateOk(jsonConfig.getBoolean("statusUpdateOk"));
      builder.terminalLocation(jsonConfig.getString("terminalLocation"));
      builder.timeoutPeriod(jsonConfig.getInteger("timeoutPeriod"));
      builder.supportedMessages(getSupportedMessagesFromJson(
                                  jsonConfig.getJsonArray("supportedMessages")));

      acsStatus = builder.build();

    } else {
      log.error("The JsonConfig object is null");
    }

    return acsStatus;
  }

  private Set<Messages> getSupportedMessagesFromJson(JsonArray supportedMessages) {

    /*
    LinkedHashSet<Messages> messages = new LinkedHashSet<>();
    supportedMessages
     .forEach( (message) -> {
       JsonObject aMessage = (JsonObject)message;
       if (aMessage.getString("isSupported").equals("Y")){
         messages.add(Messages.valueOf(aMessage.getString("messageName")));
       }
    });
    */

    return supportedMessages
        .stream()
        .filter(el -> ((JsonObject) el).getString("isSupported").equalsIgnoreCase("Y"))
        .map(el -> Messages.valueOf(((JsonObject) el).getString("messageName")))
        .collect(Collectors.toSet());
  }
}
