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
import org.folio.edge.sip2.repositories.CirculationRepository;
import org.folio.edge.sip2.session.SessionData;

public class PatronInformationHandler implements ISip2RequestHandler {
  private static final Logger log = LogManager.getLogger();

  private final CirculationRepository circulationRepository;
  private final Template commandTemplate;

  @Inject
  PatronInformationHandler(
      CirculationRepository circulationRepository,
      @Named("checkoutResponse") Template commandTemplate) {
    this.circulationRepository = Objects.requireNonNull(circulationRepository,
        "CirculationRepository cannot be null");
    this.commandTemplate = Objects.requireNonNull(commandTemplate, "Template cannot be null");
  }

  @Override
  public Future<String> execute(Object message, SessionData sessionData) {
    final PatronInformation patronInformation = (PatronInformation) message;

    log.debug("Patron Information: {}", () -> patronInformation);

    // We need to collect data from the following places:
    // - mod-users
    // - mod-circulation
    // - mod-feefines
//    final Future<PatronInformationResponse> circulationFuture =
//        circulationRepository.checkout(patronInformation, sessionData);
//
//    return circulationFuture.compose(checkoutResponse -> {
//      log.debug("CheckoutResponse: {}", () -> checkoutResponse);
//
//      final Map<String, Object> root = new HashMap<>();
//      root.put("formatDateTime", new FormatDateTimeMethodModel());
//      root.put("delimiter", sessionData.getFieldDelimiter());
//      root.put("checkoutResponse", checkoutResponse);
//
//      final String response = FreemarkerUtils.executeFreemarkerTemplate(root, commandTemplate);
//
//      log.debug("SIP checkout response: {}", response);
//
//      return Future.succeededFuture(response);
//    });
  }
}
