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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.requests.RenewAll;
import org.folio.edge.sip2.domain.messages.responses.RenewAllResponse;
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
public class RenewAllHandlerTests {

  @Test
  public void canRenewAllWithHandler(Vertx vertx,
      VertxTestContext testContext,
      @Mock CirculationRepository mockCirculationRepository) {

    final String patronIdentifier = "1029384756";
    final Clock clock = TestUtils.getUtcFixedClock();
    final String title = "Some book";
    final OffsetDateTime nbDueDate =  OffsetDateTime.now().plusDays(30);
    final String userId = UUID.randomUUID().toString();
    final String itemId = UUID.randomUUID().toString();
    List<String> emptyItems = new ArrayList<String>();

    final RenewAll renewAll = RenewAll.builder()
        .transactionDate(OffsetDateTime.now())
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .patronPassword("7890")
        .terminalPassword("1234")
        .feeAcknowledged(FALSE)
        .build();

    when(mockCirculationRepository.performRenewAllCommand(any(), any()))
        .thenReturn(Future.succeededFuture(RenewAllResponse.builder()
        .ok(TRUE)
        .transactionDate(OffsetDateTime.now())
        .institutionId("diku")
        .renewedCount(0)
        .unrenewedCount(0)
        .renewedItems(emptyItems)
        .unrenewedItems(emptyItems)
        .build()
      ));

    final RenewAllHandler handler = new RenewAllHandler(mockCirculationRepository,
        FreemarkerRepository.getInstance().getFreemarkerTemplate(Command.RENEW_ALL_RESPONSE));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final String expectedString = "66" + "1" + "0000" + "0000"
        + TestUtils.getFormattedLocalDateTime(OffsetDateTime.now(clock))
        + "AO" + "diku" + "|";

    handler.execute(renewAll, sessionData).onComplete(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          assertNotNull(sipMessage);
          assertEquals(expectedString, sipMessage);
          testContext.completeNow();
        }
    )));


  }

  @Test
  void cantRenewAllWithHandler(Vertx vertx,
                                     VertxTestContext testContext,
                                     @Mock CirculationRepository mockCirculationRepository) {

    final String patronIdentifier = "1029384756";
    final Clock clock = TestUtils.getUtcFixedClock();
    final String title = "Some book";
    final OffsetDateTime nbDueDate =  OffsetDateTime.now().plusDays(30);
    final String userId = UUID.randomUUID().toString();
    final String itemId = UUID.randomUUID().toString();
    List<String> emptyItems = new ArrayList<String>();

    final RenewAll renewAll = RenewAll.builder()
        .transactionDate(OffsetDateTime.now())
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .patronPassword("7890")
        .terminalPassword("1234")
        .feeAcknowledged(FALSE)
        .build();

    when(mockCirculationRepository.performRenewAllCommand(any(), any()))
        .thenReturn(Future.failedFuture(new ClientException("Incorrect Username")));

    final RenewAllHandler handler = new RenewAllHandler(mockCirculationRepository,
        FreemarkerRepository.getInstance().getFreemarkerTemplate(Command.RENEW_ALL_RESPONSE));

    final SessionData sessionData = TestUtils.getMockedSessionData();
    sessionData.setPatronPasswordVerificationRequired(TRUE);
    sessionData.setErrorResponseMessage(RenewAllResponse.builder()
        .ok(FALSE)
        .transactionDate(OffsetDateTime.now())
        .institutionId("diku")
        .renewedCount(0)
        .unrenewedCount(0)
        .renewedItems(emptyItems)
        .unrenewedItems(emptyItems)
        .build()
    );

    handler.execute(renewAll, sessionData).onComplete(
        testContext.failing(sipMessage -> testContext.verify(() -> {
              assertNotNull(sipMessage);
              assertEquals("Incorrect Username", sipMessage.getMessage());
              testContext.completeNow();
            }
        )));

  }

}
