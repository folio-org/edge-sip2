package org.folio.edge.sip2.handlers;

import static org.folio.edge.sip2.handlers.freemarker.FreemarkerUtils.executeFreemarkerTemplate;

import freemarker.template.Template;
import io.vertx.core.Future;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Named;
import org.folio.edge.sip2.domain.messages.requests.PatronInformation;
import org.folio.edge.sip2.domain.messages.responses.PatronInformationResponse;
import org.folio.edge.sip2.handlers.freemarker.FormatDateTimeMethodModel;
import org.folio.edge.sip2.repositories.PatronRepository;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.Sip2LogAdapter;
import org.folio.okapi.common.refreshtoken.client.ClientException;

public class PatronInformationHandler implements ISip2RequestHandler {
  private static final Sip2LogAdapter log =
      Sip2LogAdapter.getLogger(PatronInformationHandler.class);

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

    log.info(sessionData, "Patron Information: {}", patronInformation::getPatronLogInfo);

    // We need to collect data from the following places:
    // - mod-users
    // - mod-circulation
    // - mod-feefines
    final Future<PatronInformationResponse> patronFuture =
        patronRepository.performPatronInformationCommand(patronInformation, sessionData);

    patronFuture.onFailure(throwable -> {
      if (throwable instanceof ClientException) {
        sessionData.setErrorResponseMessage(
            createPatronInformationResponse(sessionData,
              (PatronInformationResponse) sessionData.getErrorResponseMessage()));
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
   * @return response string
   */
  private String createPatronInformationResponse(
      SessionData sessionData,
      PatronInformationResponse patronInformationResponse) {
    log.debug(sessionData, "PatronInformationResponse: {}", () -> patronInformationResponse);

    final Map<String, Object> root = new HashMap<>();
    root.put("formatDateTime", new FormatDateTimeMethodModel());
    root.put("delimiter", sessionData.getFieldDelimiter());
    root.put("patronInformationResponse", patronInformationResponse);
    root.put("maxLength", sessionData.getMaxPrintWidth());
    root.put("timezone", sessionData.getTimeZone());

    final String response = executeFreemarkerTemplate(sessionData, root, commandTemplate);
    log.debug(sessionData, "SIP patron information response: {}", response);
    return response;
  }
}
