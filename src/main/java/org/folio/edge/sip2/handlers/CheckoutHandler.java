package org.folio.edge.sip2.handlers;

import static org.folio.edge.sip2.handlers.freemarker.FreemarkerUtils.executeFreemarkerTemplate;

import freemarker.template.Template;
import io.vertx.core.Future;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Named;
import org.folio.edge.sip2.domain.messages.requests.Checkout;
import org.folio.edge.sip2.domain.messages.responses.CheckoutResponse;
import org.folio.edge.sip2.handlers.freemarker.FormatDateTimeMethodModel;
import org.folio.edge.sip2.repositories.CirculationRepository;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.Sip2LogAdapter;
import org.folio.okapi.common.refreshtoken.client.ClientException;

public class CheckoutHandler implements ISip2RequestHandler {
  private static final Sip2LogAdapter log = Sip2LogAdapter.getLogger(CheckoutHandler.class);

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
    log.debug(sessionData, "CheckoutHandler :: execute message:{} sessionData:{}",message,sessionData);
    final Checkout checkout = (Checkout) message;

    log.info(sessionData, "CheckoutHandler :: execute Checkout: {}", checkout::getCheckOutLogInfo);

    final Future<CheckoutResponse> circulationFuture =
        circulationRepository.performCheckoutCommand(checkout, sessionData);

    circulationFuture.onFailure(throwable -> {
      if (throwable instanceof ClientException) {
        sessionData.setErrorResponseMessage(
            constructCheckoutResponse(
              sessionData,
              (CheckoutResponse) sessionData.getErrorResponseMessage()));
      }
    });

    return circulationFuture.compose(checkoutResponse ->
      Future.succeededFuture(
        constructCheckoutResponse(
          sessionData, checkoutResponse)));
  }

  /**
   * Construct CheckOut Response Message.
   * @param sessionData sessionData
   * @param checkoutResponse checkoutResponse
   * @return response string
   */
  private String constructCheckoutResponse(
      SessionData sessionData,
      CheckoutResponse checkoutResponse) {
    log.info(sessionData, "CheckoutHandler :: execute CheckoutResponse: {}", () -> checkoutResponse);

    final Map<String, Object> root = new HashMap<>();
    root.put("formatDateTime", new FormatDateTimeMethodModel());
    root.put("delimiter", sessionData.getFieldDelimiter());
    root.put("checkoutResponse", checkoutResponse);
    root.put("timezone", sessionData.getTimeZone());

    final String response = executeFreemarkerTemplate(sessionData, root, commandTemplate);

    log.info(sessionData, "CheckoutHandler :: execute SIP checkout response: {}", response);
    return response;
  }
}
