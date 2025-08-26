package org.folio.edge.sip2.handlers;

import freemarker.template.Template;
import io.vertx.core.Future;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.requests.FeePaid;
import org.folio.edge.sip2.domain.messages.responses.FeePaidResponse;
import org.folio.edge.sip2.handlers.freemarker.FormatDateTimeMethodModel;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerUtils;
import org.folio.edge.sip2.repositories.FeeFinesRepository;
import org.folio.edge.sip2.session.SessionData;

public class FeePaidHandler implements ISip2RequestHandler {
  private static final Logger log = LogManager.getLogger();

  private final FeeFinesRepository feeFinesRepository;
  private final Template commandTemplate;

  @Inject
  FeePaidHandler(
      FeeFinesRepository feeFinesRepository,
      @Named("feePaidResponse") Template commandTemplate) {
    this.feeFinesRepository = Objects.requireNonNull(feeFinesRepository,
        "FeeFinesRepository cannot be null");
    this.commandTemplate = Objects.requireNonNull(commandTemplate, "Template cannot be null");
  }

  @Override
  public Future<String> execute(Object message, SessionData sessionData) {
    final FeePaid feePaid = (FeePaid) message;

    log.debug("FeePaid: {}", () -> feePaid);

    final Future<FeePaidResponse> feePaidFuture =
        feeFinesRepository.performFeePaidCommand(feePaid, sessionData);

    return feePaidFuture.compose(feePaidResponse -> {
      log.info("FeePaidResponse: {}", () -> feePaidResponse);

      final Map<String, Object> root = new HashMap<>();
      root.put("formatDateTime", new FormatDateTimeMethodModel());
      root.put("delimiter", sessionData.getFieldDelimiter());
      root.put("feePaidResponse", feePaidResponse);
      root.put("timezone", sessionData.getTimeZone());

      final String response = FreemarkerUtils
          .executeFreemarkerTemplate(root, commandTemplate);

      log.debug("SIP feePaid response: {}", response);

      return Future.succeededFuture(response);
    });
  }
}

