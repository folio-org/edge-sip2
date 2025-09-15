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
import org.folio.edge.sip2.domain.messages.requests.Renew;
import org.folio.edge.sip2.domain.messages.responses.RenewResponse;
import org.folio.edge.sip2.handlers.freemarker.FormatDateTimeMethodModel;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerUtils;
import org.folio.edge.sip2.repositories.CirculationRepository;
import org.folio.edge.sip2.session.SessionData;
import org.folio.okapi.common.refreshtoken.client.ClientException;

public class RenewHandler implements ISip2RequestHandler {
  private static final Logger log = LogManager.getLogger();

  private final CirculationRepository circulationRepository;
  private final Template commandTemplate;

  @Inject
  RenewHandler(
      CirculationRepository circulationRepository,
        @Named("renewResponse") Template commandTemplate) {
    this.circulationRepository = Objects.requireNonNull(circulationRepository,
        "CirculationRepository cannot be null");
    this.commandTemplate = Objects.requireNonNull(commandTemplate, "Template cannot be null");
  }

  @Override
  public Future<String> execute(Object message, SessionData sessionData) {
    final Renew renew = (Renew) message;

    log.debug("Renew: {}", () -> renew);

    final Future<RenewResponse> renewFuture =
        circulationRepository.performRenewCommand(renew, sessionData);

    renewFuture.onFailure(throwable -> {
      if (throwable instanceof ClientException) {
        sessionData.setErrorResponseMessage(
            constructRenewResponse(
            sessionData,
            (RenewResponse) sessionData.getErrorResponseMessage()));
      }
    });

    return renewFuture.compose(renewResponse ->
      Future.succeededFuture(
        constructRenewResponse(
          sessionData, renewResponse)));
  }

  /**
   * Construct Renew Response Message.
   * @param sessionData sessionData
   * @param renewResponse renewResponse
   * @return response String
   */
  private String constructRenewResponse(SessionData sessionData, RenewResponse renewResponse) {
    log.info("RenewResponse: {}", () -> renewResponse);

    final Map<String, Object> root = new HashMap<>();
    root.put("formatDateTime", new FormatDateTimeMethodModel());
    root.put("delimiter", sessionData.getFieldDelimiter());
    root.put("renewResponse", renewResponse);
    root.put("timezone", sessionData.getTimeZone());

    final String response = FreemarkerUtils
        .executeFreemarkerTemplate(root, commandTemplate);

    log.debug("SIP renew response: {}", response);
    return response;
  }
}
