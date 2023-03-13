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
import org.folio.edge.sip2.domain.messages.requests.Checkout;
import org.folio.edge.sip2.domain.messages.responses.CheckoutResponse;
import org.folio.edge.sip2.handlers.freemarker.FormatDateTimeMethodModel;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerUtils;
import org.folio.edge.sip2.repositories.CirculationRepository;
import org.folio.edge.sip2.session.SessionData;

public class CheckoutHandler implements ISip2RequestHandler {
  {
    log.info("Inside CheckoutHandler");
  }
  private static final Logger log = LogManager.getLogger();

  private final CirculationRepository circulationRepository;
  private final Template commandTemplate;

  @Inject
  CheckoutHandler(
      CirculationRepository circulationRepository,
      @Named("checkoutResponse") Template commandTemplate) {
    this.circulationRepository = Objects.requireNonNull(circulationRepository,
        "CirculationRepository cannot be null");
    this.commandTemplate = Objects.requireNonNull(commandTemplate, "Template cannot be null");
  }

  @Override
  public Future<String> execute(Object message, SessionData sessionData) {
    log.info("CheckoutHandler :: execute message:{} sessionData:{}",message,sessionData);
    log.info(this);
    final Checkout checkout = (Checkout) message;

    log.info("CheckoutHandler :: execute Checkout: {}", checkout::getCheckOutLogInfo);
    String syncValue = checkout.getPatronIdentifier()+sessionData.getScLocation();
    synchronized(syncValue) {
      try {
        log.info("Inside Sync block with value {} , Thread name", syncValue,Thread.currentThread().getName());
        Thread.sleep(10000);
        final Future<CheckoutResponse> circulationFuture =
          circulationRepository.performCheckoutCommand(checkout, sessionData);


        return circulationFuture.compose(checkoutResponse -> {
          log.info("CheckoutHandler :: execute CheckoutResponse: {}", () -> checkoutResponse);

          final Map<String, Object> root = new HashMap<>();
          root.put("formatDateTime", new FormatDateTimeMethodModel());
          root.put("delimiter", sessionData.getFieldDelimiter());
          root.put("checkoutResponse", checkoutResponse);
          root.put("timezone", sessionData.getTimeZone());

          final String response = FreemarkerUtils.executeFreemarkerTemplate(root, commandTemplate);

          log.info("CheckoutHandler :: execute SIP checkout response: {}", response);

          return Future.succeededFuture(response);
        });
      } catch (InterruptedException e) {
        log.info("Inside interrupted exception");
        throw new RuntimeException(e);
      }
    }
  }
}
