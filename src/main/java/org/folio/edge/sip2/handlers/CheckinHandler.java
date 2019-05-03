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
import org.folio.edge.sip2.domain.messages.requests.Checkin;
import org.folio.edge.sip2.domain.messages.responses.CheckinResponse;
import org.folio.edge.sip2.handlers.freemarker.FormatDateTimeMethodModel;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerUtils;
import org.folio.edge.sip2.repositories.CirculationRepository;
import org.folio.edge.sip2.session.SessionData;

public class CheckinHandler implements ISip2RequestHandler {
  private static final Logger log = LogManager.getLogger();

  private final CirculationRepository circulationRepository;
  private final Template commandTemplate;

  @Inject
  CheckinHandler(
      CirculationRepository circulationRepository,
      @Named("checkinResponse") Template commandTemplate) {
    this.circulationRepository = Objects.requireNonNull(circulationRepository,
        "CirculationRepository cannot be null");
    this.commandTemplate = Objects.requireNonNull(commandTemplate, "Template cannot be null");
  }

  @Override
  public Future<String> execute(Object message, SessionData sessionData) {
    final Checkin checkin = (Checkin) message;

    log.debug("Checkin: {}", () -> checkin);

    final Future<CheckinResponse> circulationFuture =
        circulationRepository.performCheckinCommand(checkin, sessionData);

    return circulationFuture.compose(checkinResponse -> {
      log.debug("CheckinResponse: {}", () -> checkinResponse);

      final Map<String, Object> root = new HashMap<>();
      root.put("formatDateTime", new FormatDateTimeMethodModel());
      root.put("delimiter", sessionData.getFieldDelimiter());
      root.put("checkinResponse", checkinResponse);

      final String response = FreemarkerUtils
          .executeFreemarkerTemplate(root, commandTemplate);

      log.debug("SIP checkin response: {}", response);

      return Future.succeededFuture(response);
    });
  }
}
