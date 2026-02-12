package org.folio.edge.sip2.handlers;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Collections;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.requests.Renew;
import org.folio.edge.sip2.domain.messages.responses.RenewResponse;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.parser.Command;
import org.folio.edge.sip2.repositories.CirculationRepository;
import org.folio.edge.sip2.session.SessionData;
import org.folio.okapi.common.refreshtoken.client.ClientException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
class RenewHandlerTests {

  @Test
   void canRenewWithHandler(Vertx vertx,
      VertxTestContext testContext,
      @Mock CirculationRepository mockCirculationRepository) {

    final String patronIdentifier = "1029384756";
    final Clock clock = TestUtils.getUtcFixedClock();

    final Renew renew = Renew.builder()
        .transactionDate(OffsetDateTime.now())
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .patronPassword("7890")
        .terminalPassword("1234")
        .feeAcknowledged(FALSE)
        .build();

    when(mockCirculationRepository.performRenewCommand(any(), any()))
        .thenReturn(Future.succeededFuture(RenewResponse.builder()
        .ok(TRUE)
        .renewalOk(TRUE)
        .transactionDate(OffsetDateTime.now())
        .institutionId("diku")
        .build()
      ));

    final RenewHandler handler = new RenewHandler(mockCirculationRepository,
        FreemarkerRepository.getInstance().getFreemarkerTemplate(Command.RENEW_RESPONSE));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final String expectedString = "30" + "1" + "YUU"
        + TestUtils.getFormattedLocalDateTime(OffsetDateTime.now(clock))
        + "AO" + "diku" + "|AA|AB|AJ|AH|";

    handler.execute(renew, sessionData).onComplete(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          assertNotNull(sipMessage);
          assertEquals(expectedString, sipMessage);
          testContext.completeNow();
        }
    )));

  }

  @Test
  void canRenewWithHandler_negative_renewalNotAllowed(Vertx vertx,
      VertxTestContext testContext,
      @Mock CirculationRepository mockCirculationRepository) {

    final String patronIdentifier = "1029384756";
    final Clock clock = TestUtils.getUtcFixedClock();
    final Renew renew = Renew.builder()
        .transactionDate(OffsetDateTime.now())
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .patronPassword("7890")
        .itemIdentifier("item123")
        .terminalPassword("1234")
        .feeAcknowledged(FALSE)
        .build();

    when(mockCirculationRepository.performRenewCommand(any(), any()))
        .thenReturn(Future.succeededFuture(RenewResponse.builder()
        .ok(FALSE)
        .renewalOk(FALSE)
        .transactionDate(OffsetDateTime.now(clock))
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .itemIdentifier("item123")
        .screenMessage(Collections.singletonList("loan is not renewable"))
        .build()
      ));

    final RenewHandler handler = new RenewHandler(mockCirculationRepository,
        FreemarkerRepository.getInstance().getFreemarkerTemplate(Command.RENEW_RESPONSE));
    final SessionData sessionData = TestUtils.getMockedSessionData();
    final String expectedPrefix = "30" + "0" + "N";

    handler.execute(renew, sessionData).onComplete(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          assertNotNull(sipMessage);
          assertEquals(expectedPrefix.charAt(0), sipMessage.charAt(0));
          assertEquals(expectedPrefix.charAt(1), sipMessage.charAt(1));
          assertEquals(expectedPrefix.charAt(2), sipMessage.charAt(2));
          assertEquals(expectedPrefix.charAt(3), sipMessage.charAt(3));
          testContext.completeNow();
        }
      )));
  }

  @Test
   void canRenewWithHandlerFail(Vertx vertx,
                                  VertxTestContext testContext,
                                  @Mock CirculationRepository mockCirculationRepository) {

    final String patronIdentifier = "1029384756";
    final Clock clock = TestUtils.getUtcFixedClock();

    final Renew renew = Renew.builder()
        .transactionDate(OffsetDateTime.now())
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .patronPassword("7890")
        .terminalPassword("1234")
        .feeAcknowledged(FALSE)
        .build();

    when(mockCirculationRepository.performRenewCommand(any(), any()))
        .thenReturn(Future.failedFuture(new ClientException("Invalid username")));

    final SessionData sessionData = TestUtils.getMockedSessionData();
    sessionData.setErrorResponseMessage(
        RenewResponse.builder()
        .ok(FALSE)
        .renewalOk(FALSE)
        .transactionDate(OffsetDateTime.now(clock))
        .institutionId("abc")
        .screenMessage(Collections.singletonList("Invalid username"))
        .build()
    );

    final RenewHandler handler = new RenewHandler(mockCirculationRepository,
        FreemarkerRepository.getInstance().getFreemarkerTemplate(Command.RENEW_RESPONSE));

    final String expectedString = "Invalid username";

    handler.execute(renew, sessionData).onComplete(
        testContext.failing(sipMessage -> testContext.verify(() -> {
              assertNotNull(sipMessage);
              assertEquals(expectedString, sipMessage.getMessage());
              testContext.completeNow();
            }
      )));


  }
}
