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
import org.folio.edge.sip2.domain.messages.requests.RenewAll;
import org.folio.edge.sip2.domain.messages.responses.RenewAllResponse;
import org.folio.edge.sip2.handlers.freemarker.FormatDateTimeMethodModel;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerUtils;
import org.folio.edge.sip2.repositories.CirculationRepository;
import org.folio.edge.sip2.session.SessionData;
import org.folio.okapi.common.refreshtoken.client.ClientException;

public class RenewAllHandler implements ISip2RequestHandler {
  private static final Logger log = LogManager.getLogger();

  private final CirculationRepository circulationRepository;
  private final Template commandTemplate;

  @Inject
  RenewAllHandler(
      CirculationRepository circulationRepository,
        @Named("renewAllResponse") Template commandTemplate) {
    this.circulationRepository = Objects.requireNonNull(circulationRepository,
        "CirculationRepository cannot be null");
    this.commandTemplate = Objects.requireNonNull(commandTemplate, "Template cannot be null");
  }

  @Override
  public Future<String> execute(Object message, SessionData sessionData) {
    final RenewAll renewAll = (RenewAll) message;

    log.debug("RenewAll: {}", () -> renewAll);

    final Future<RenewAllResponse> renewAllFuture =
        circulationRepository.performRenewAllCommand(renewAll, sessionData);

    renewAllFuture.onFailure(throwable -> {
      if (throwable instanceof ClientException) {
        sessionData.setErrorResponseMessage(
            constructRenewAllResponse(
            sessionData,
            (RenewAllResponse) sessionData.getErrorResponseMessage()));
      }
    });

    return renewAllFuture.compose(renewAllResponse ->
      Future.succeededFuture(
        constructRenewAllResponse(
          sessionData, renewAllResponse)));
  }

  /**
   * Construct Renew All Response Message.
   * @param sessionData sessionData
   * @param renewAllResponse renewAllResponse
   * @return response String
   */
  private String constructRenewAllResponse(
      SessionData sessionData,
      RenewAllResponse renewAllResponse) {
    log.info("RenewAllResponse: {}", () -> renewAllResponse);

    final Map<String, Object> root = new HashMap<>();
    root.put("formatDateTime", new FormatDateTimeMethodModel());
    root.put("delimiter", sessionData.getFieldDelimiter());
    root.put("renewAllResponse", renewAllResponse);
    root.put("timezone", sessionData.getTimeZone());

    final String response = FreemarkerUtils
        .executeFreemarkerTemplate(root, commandTemplate);

    log.debug("SIP renewAll response: {}", response);
    return response;
  }
}
