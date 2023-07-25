package org.folio.edge.sip2.handlers;

import freemarker.template.Template;
import io.vertx.core.Future;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.requests.PatronInformation;
import org.folio.edge.sip2.domain.messages.responses.PatronInformationResponse;
import org.folio.edge.sip2.handlers.freemarker.FormatDateTimeMethodModel;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerUtils;
import org.folio.edge.sip2.repositories.PatronRepository;
import org.folio.edge.sip2.session.SessionData;
import org.folio.okapi.common.refreshtoken.client.ClientException;

public class PatronInformationHandler implements ISip2RequestHandler {
  private static final Logger log = LogManager.getLogger();

  private final PatronRepository patronRepository;
  private final Template commandTemplate;

  @Inject
  PatronInformationHandler(
      PatronRepository patronRepository,
      @Named("patronInformationResponse") Template commandTemplate) {
    this.patronRepository = Objects.requireNonNull(patronRepository,
        "PatronRepository cannot be null");
    this.commandTemplate = Objects.requireNonNull(commandTemplate, "Template cannot be null");
  }

  @Override
  public Future<String> execute(Object message, SessionData sessionData) {
    final PatronInformation patronInformation = (PatronInformation) message;

    log.info("Patron Information: {}", patronInformation::getPatronLogInfo);

    // We need to collect data from the following places:
    // - mod-users
    // - mod-circulation
    // - mod-feefines
    final Future<PatronInformationResponse> patronFuture =
        patronRepository.performPatronInformationCommand(patronInformation, sessionData);

    patronFuture.onFailure(throwable -> {
      if (throwable instanceof ClientException) {
        sessionData.setResponseMessage(
            createPatronInformationResponse(sessionData,
              (PatronInformationResponse) sessionData.getResponseMessage()));
      }
    });

    return patronFuture.compose(patronInformationResponse -> Future.succeededFuture(
      createPatronInformationResponse(sessionData,
        patronInformationResponse)));
  }

  /**
   * Create Patron Information Response message.
   * @param sessionData sessionData
   * @param patronInformationResponse patronInformationResponse
   * @return
   */
  private String createPatronInformationResponse(
      SessionData sessionData,
      PatronInformationResponse patronInformationResponse) {
    log.debug("PatronInformationResponse: {}", () -> patronInformationResponse);

    final Map<String, Object> root = new HashMap<>();
    root.put("formatDateTime", new FormatDateTimeMethodModel());
    root.put("delimiter", sessionData.getFieldDelimiter());
    root.put("patronInformationResponse", patronInformationResponse);
    root.put("maxLength", sessionData.getMaxPrintWidth());
    root.put("timezone", sessionData.getTimeZone());

    final String response = FreemarkerUtils.executeFreemarkerTemplate(root, commandTemplate);
    log.debug("SIP patron information response: {}", response);
    return response;
  }
}
