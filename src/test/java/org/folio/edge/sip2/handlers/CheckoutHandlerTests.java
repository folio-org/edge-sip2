package org.folio.edge.sip2.handlers;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.folio.edge.sip2.parser.Command.CHECKOUT_RESPONSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.folio.edge.sip2.domain.messages.requests.Checkout;
import org.folio.edge.sip2.domain.messages.responses.CheckoutResponse;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.repositories.CirculationRepository;
import org.folio.edge.sip2.session.SessionData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
public class CheckoutHandlerTests {
  @Test
  public void canExecuteASampleCheckoutUsingHandler(
      @Mock CirculationRepository mockCirculationRepository,
      Vertx vertx,
      VertxTestContext testContext) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final ZonedDateTime nbDueDate = ZonedDateTime.now();
    final String institutionId = "diku";
    final String patronIdentifier = "0192837465";
    final String itemIdentifier = "1234567890";
    final Checkout checkout = Checkout.builder()
        .scRenewalPolicy(FALSE)
        .noBlock(FALSE)
        .transactionDate(ZonedDateTime.now())
        .nbDueDate(nbDueDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .terminalPassword("1234")
        .itemProperties("Some property of this item")
        .patronPassword("4321")
        .feeAcknowledged(FALSE)
        .cancel(FALSE)
        .build();

    when(mockCirculationRepository.checkout(any(), any()))
        .thenReturn(Future.succeededFuture(CheckoutResponse.builder()
            .ok(TRUE)
            .renewalOk(FALSE)
            .magneticMedia(null)
            .desensitize(TRUE)
            .transactionDate(ZonedDateTime.now(clock))
            .institutionId(institutionId)
            .patronIdentifier(patronIdentifier)
            .itemIdentifier(itemIdentifier)
            .titleIdentifier("Some Book")
            .dueDate(ZonedDateTime.now(clock).plusDays(30))
            .build()));

    final CheckoutHandler handler = new CheckoutHandler(mockCirculationRepository,
        FreemarkerRepository.getInstance().getFreemarkerTemplate(CHECKOUT_RESPONSE));

    final SessionData sessionData = SessionData.createSession(institutionId, '|', false, "IBM850");

    handler.execute(checkout, sessionData).setHandler(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          final String expectedString = "121NUY"
              + ZonedDateTime.now(clock).format(DateTimeFormatter.ofPattern("yyyyMMdd    HHmmss"))
              + "AO" + institutionId + "|AA" + patronIdentifier + "|AB" + itemIdentifier
              + "|AJSome Book|AH"
              + ZonedDateTime.now(clock).plusDays(30).format(
                  DateTimeFormatter.ofPattern("yyyyMMdd    HHmmss"))
              + '|';

          assertEquals(expectedString, sipMessage);

          testContext.completeNow();
        })));
  }

  @Test
  public void canExecuteASampleFailedCheckoutUsingHandler(
      @Mock CirculationRepository mockCirculationRepository,
      Vertx vertx,
      VertxTestContext testContext) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final ZonedDateTime nbDueDate = ZonedDateTime.now();
    final String institutionId = "diku";
    final String patronIdentifier = "0192837465";
    final String itemIdentifier = "1234567890";
    final Checkout checkout = Checkout.builder()
        .scRenewalPolicy(FALSE)
        .noBlock(FALSE)
        .transactionDate(ZonedDateTime.now())
        .nbDueDate(nbDueDate)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .terminalPassword("1234")
        .itemProperties("Some property of this item")
        .patronPassword("4321")
        .feeAcknowledged(FALSE)
        .cancel(FALSE)
        .build();

    when(mockCirculationRepository.checkout(any(), any()))
        .thenReturn(Future.succeededFuture(CheckoutResponse.builder()
            .ok(FALSE)
            .renewalOk(FALSE)
            .magneticMedia(null)
            .desensitize(FALSE)
            .transactionDate(ZonedDateTime.now(clock))
            .institutionId(institutionId)
            .patronIdentifier(patronIdentifier)
            .itemIdentifier(itemIdentifier)
            .titleIdentifier("Some Book")
            .dueDate(ZonedDateTime.now(clock).plusDays(30))
            .build()));

    final CheckoutHandler handler = new CheckoutHandler(mockCirculationRepository,
        FreemarkerRepository.getInstance().getFreemarkerTemplate(CHECKOUT_RESPONSE));

    final SessionData sessionData = SessionData.createSession(institutionId, '|', false, "IBM850");

    handler.execute(checkout, sessionData).setHandler(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          final String expectedString = "120NUN"
              + ZonedDateTime.now(clock).format(DateTimeFormatter.ofPattern("yyyyMMdd    HHmmss"))
              + "AO" + institutionId + "|AA" + patronIdentifier + "|AB" + itemIdentifier
              + "|AJSome Book|AH"
              + ZonedDateTime.now(clock).plusDays(30).format(
                  DateTimeFormatter.ofPattern("yyyyMMdd    HHmmss"))
              + '|';

          assertEquals(expectedString, sipMessage);

          testContext.completeNow();
        })));
  }

  @Test
  public void cannotCreateHandlerDueToMissingCirculationRepository() {
    final NullPointerException thrown = assertThrows(
        NullPointerException.class,
        () -> new CheckinHandler(null, null));

    assertEquals("CirculationRepository cannot be null", thrown.getMessage());
  }

  @Test
  public void cannotCreateHandlerDueToMissingTemplate(@Mock CirculationRepository mock) {
    final NullPointerException thrown = assertThrows(NullPointerException.class,
        () -> new CheckinHandler(mock, null));

    assertEquals("Template cannot be null", thrown.getMessage());
  }
}
