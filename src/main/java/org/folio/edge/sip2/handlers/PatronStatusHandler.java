package org.folio.edge.sip2.handlers;

import static org.folio.edge.sip2.handlers.freemarker.FreemarkerUtils.executeFreemarkerTemplate;

import freemarker.template.Template;
import io.vertx.core.Future;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Named;
import org.folio.edge.sip2.domain.messages.requests.PatronStatusRequest;
import org.folio.edge.sip2.domain.messages.responses.PatronStatusResponse;
import org.folio.edge.sip2.handlers.freemarker.FormatDateTimeMethodModel;
import org.folio.edge.sip2.repositories.PatronRepository;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.Sip2LogAdapter;
import org.folio.okapi.common.refreshtoken.client.ClientException;


public class PatronStatusHandler implements ISip2RequestHandler {
  private static final Sip2LogAdapter log = Sip2LogAdapter.getLogger(PatronStatusHandler.class);


  private final PatronRepository patronRepository;
  private final Template commandTemplate;

  @Inject
  PatronStatusHandler(
      PatronRepository patronRepository,
      @Named("patronStatusResponse") Template commandTemplate) {
    this.patronRepository = Objects.requireNonNull(patronRepository,
          "PatronRepositorysRepository cannot be null");

    this.commandTemplate = Objects.requireNonNull(commandTemplate, "Template cannot be null");
  }

  @Override
  public Future<String> execute(Object message, SessionData sessionData) {
    final PatronStatusRequest patronStatus = (PatronStatusRequest) message;

    log.debug(sessionData, "PatronStatusRequest: {}", () -> patronStatus);

    final Future<PatronStatusResponse> patronStatusFuture =
        patronRepository.performPatronStatusCommand(patronStatus, sessionData);

    patronStatusFuture.onFailure(throwable -> {
      if (throwable instanceof ClientException) {
        sessionData.setErrorResponseMessage(createPatronStatusResponse(sessionData,
            (PatronStatusResponse) sessionData.getErrorResponseMessage()));
      }
    });

    return patronStatusFuture.compose(patronStatusResponse -> Future.succeededFuture(
      createPatronStatusResponse(sessionData, patronStatusResponse)
    ));
  }

  /**
   * Create Patron Status Response Message.
   * @param sessionData Session Data
   * @param patronStatusResponse Patron Status Response
   * @return response String
   */
  public String createPatronStatusResponse(
      SessionData sessionData,
      PatronStatusResponse patronStatusResponse) {
    log.info(sessionData, "PatronStatusResponse: {}", () -> patronStatusResponse);

    final Map<String, Object> root = new HashMap<>();
    root.put("formatDateTime", new FormatDateTimeMethodModel());
    root.put("delimiter", sessionData.getFieldDelimiter());
    root.put("patronStatusResponse", patronStatusResponse);
    root.put("timezone", sessionData.getTimeZone());

    final String response = executeFreemarkerTemplate(sessionData, root, commandTemplate);

    log.debug(sessionData, "SIP patronStatus response: {}", response);
    return response;
  }
}

