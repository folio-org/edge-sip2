package org.folio.edge.sip2.handlers;

import static org.folio.edge.sip2.parser.Command.ACS_STATUS;

import freemarker.template.Template;
import io.vertx.core.Future;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.enumerations.Messages;
import org.folio.edge.sip2.domain.messages.enumerations.StatusCode;
import org.folio.edge.sip2.domain.messages.requests.SCStatus;
import org.folio.edge.sip2.domain.messages.responses.ACSStatus;
import org.folio.edge.sip2.handlers.freemarker.FormatDateTimeMethodModel;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerUtils;
import org.folio.edge.sip2.repositories.ConfigurationRepository;
import org.folio.edge.sip2.session.SessionData;

public class SCStatusHandler implements ISip2RequestHandler {

  private final ConfigurationRepository configurationRepository;
  private final Logger log;
  private final Template template;

  /**
   * Constructor of SCStatusHandler.
   *
   * @param configurationRepository the repository necessary to retrieve config data from.
   * @param template the template to apply to the data.
   *
   */
  public SCStatusHandler(ConfigurationRepository configurationRepository, Template template) {
    this.configurationRepository = configurationRepository;
    log = LogManager.getLogger(MethodHandles.lookup().lookupClass());
    this.template = template;
  }

  @Override
  public Future<String> execute(Object message, SessionData sessionData)  {

    SCStatus scStatus = (SCStatus)message;
    sessionData.setMaxPrintWidth(scStatus.getMaxPrintWidth());

    StatusCode scStatusCode = scStatus.getStatusCode();
    if (scStatusCode == StatusCode.SC_OK) {

      Future<ACSStatus> future = configurationRepository.getACSStatus(sessionData);

      return future.compose(acsStatus -> {
        Map<String, Object> root = new HashMap<>();
        root.put("PackagedSupportedMessages",
            new PackagedSupportedMessages(acsStatus.getSupportedMessages()));
        root.put("ACSStatus",acsStatus);
        root.put("formatDateTime", new FormatDateTimeMethodModel());
        root.put("delimiter", sessionData.getFieldDelimiter());
        root.put("maxLength", 100); // should come from the sc status command

        if (template == null) {
          log.error("Unable to locate Freemarker template for the command: " + ACS_STATUS.name());
          return Future.failedFuture("");
        }

        String acsSipStatusMessage = FreemarkerUtils.executeFreemarkerTemplate(root, template);
        log.debug("Sip2 ACSStatus message: " + acsSipStatusMessage);

        return Future.succeededFuture(acsSipStatusMessage);
      });
    } else {
      log.error("SC at location {} status is {}", sessionData.getScLocation(), scStatusCode);
      return Future.failedFuture("Cannot service this request because SC is " + scStatusCode);
    }
  }

  /**
   * Inner utility class to help laid out the Messages data for freemarker template to consume.
   *
   */
  public static class PackagedSupportedMessages {
    private Boolean patronStatusRequest;
    private Boolean checkOut;
    private Boolean checkIn;
    private Boolean blockPatron;
    private Boolean scAcsStatus;
    private Boolean requestScAcsResend;
    private Boolean login;
    private Boolean patronInformation;
    private Boolean endPatronSession;
    private Boolean feePaid;
    private Boolean itemInformation;
    private Boolean itemStatusUpdate;
    private Boolean patronEnable;
    private Boolean hold;
    private Boolean renew;
    private Boolean renewAll;

    /**
     * Constructor of PackagedSupportedMessages.
     * @param supportedMessage the list of Messages that this ACS supports.
     *
     */
    public PackagedSupportedMessages(Set<Messages> supportedMessage) {
      patronStatusRequest = supportedMessage.contains(Messages.PATRON_STATUS_REQUEST);
      checkOut = supportedMessage.contains(Messages.CHECKOUT);
      checkIn = supportedMessage.contains(Messages.CHECKIN);
      blockPatron = supportedMessage.contains(Messages.BLOCK_PATRON);
      scAcsStatus = supportedMessage.contains(Messages.SC_ACS_STATUS);
      login = supportedMessage.contains(Messages.LOGIN);
      requestScAcsResend = supportedMessage.contains(Messages.REQUEST_SC_ACS_RESEND);
      patronInformation = supportedMessage.contains(Messages.PATRON_INFORMATION);
      endPatronSession = supportedMessage.contains(Messages.END_PATRON_SESSION);
      feePaid = supportedMessage.contains(Messages.FEE_PAID);
      itemInformation = supportedMessage.contains(Messages.ITEM_INFORMATION);
      itemStatusUpdate = supportedMessage.contains(Messages.ITEM_STATUS_UPDATE);
      patronEnable = supportedMessage.contains(Messages.PATRON_ENABLE);
      hold = supportedMessage.contains(Messages.HOLD);
      renew = supportedMessage.contains(Messages.RENEW);
      renewAll = supportedMessage.contains(Messages.RENEW_ALL);
    }

    public Boolean getPatronStatusRequest() {
      return patronStatusRequest;
    }

    public Boolean getCheckOut() {
      return checkOut;
    }

    public Boolean getCheckIn() {
      return checkIn;
    }

    public Boolean getBlockPatron() {
      return blockPatron;
    }

    public Boolean getScAcsStatus() {
      return scAcsStatus;
    }

    public Boolean getRequestScAcsResend() {
      return requestScAcsResend;
    }

    public Boolean getLogin() {
      return login;
    }

    public Boolean getPatronInformation() {
      return patronInformation;
    }

    public Boolean getEndPatronSession() {
      return endPatronSession;
    }

    public Boolean getFeePaid() {
      return feePaid;
    }

    public Boolean getItemInformation() {
      return itemInformation;
    }

    public Boolean getItemStatusUpdate() {
      return itemStatusUpdate;
    }

    public Boolean getPatronEnable() {
      return patronEnable;
    }

    public Boolean getHold() {
      return hold;
    }

    public Boolean getRenew() {
      return renew;
    }

    public Boolean getRenewAll() {
      return renewAll;
    }
  }
}
