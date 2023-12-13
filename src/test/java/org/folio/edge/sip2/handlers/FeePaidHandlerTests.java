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
import org.folio.edge.sip2.domain.messages.PatronAccountInfo;
import org.folio.edge.sip2.domain.messages.requests.FeePaid;
import org.folio.edge.sip2.domain.messages.responses.FeePaidResponse;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.parser.Command;
import org.folio.edge.sip2.repositories.FeeFinesRepository;
import org.folio.edge.sip2.session.SessionData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
class FeePaidHandlerTests {

  @Test
  void canPayFeeWithHandler(Vertx vertx,
      VertxTestContext testContext,
      @Mock FeeFinesRepository mockFeeFinesRepository) {

    final String patronIdentifier = "1029384756";
    final String accountIdentifier = "c78489bd-4d1b-4e4f-87d3-caa915946aa4";
    final String feeFineIdentifier = "9ffed8e5-d1b2-4857-a07b-30199204783d";
    final String transactionId = "7e15ba2d-cc85-4226-963d-d6c7d5c03f26";
    final String feeAmount = "66.67";
    final String itemBarcode = "a32451";
    final List<PatronAccountInfo> patronAccountInfoList = new ArrayList<>();
    final PatronAccountInfo patronAccountInfo = new PatronAccountInfo();
    final String feeFineCreationDate = "2023-11-13T10:15:02+01:00";

    patronAccountInfo.setId(accountIdentifier);
    patronAccountInfo.setFeeFinePaid(66.67);
    patronAccountInfo.setFeeFineAmount(70.0);
    patronAccountInfo.setFeeFineRemaining(3.33);
    patronAccountInfo.setItemBarcode(itemBarcode);
    patronAccountInfo.setFeeFineId(feeFineIdentifier);
    patronAccountInfo.setFeeCreationDate(OffsetDateTime.parse(feeFineCreationDate));

    patronAccountInfoList.add(patronAccountInfo);

    final Clock clock = TestUtils.getUtcFixedClock();

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final FeePaid feePaid = FeePaid.builder()
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .transactionId(transactionId)
        .feeAmount(feeAmount)
        .feeIdentifier(accountIdentifier)
        .build();

    when(mockFeeFinesRepository.performFeePaidCommand(any(), any()))
        .thenReturn(Future.succeededFuture(FeePaidResponse.builder()
        .paymentAccepted(TRUE)
        .transactionDate(OffsetDateTime.now(clock))
        .transactionId(transactionId)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .patronAccountInfoList(patronAccountInfoList)
        .build()
      ));

    final FeePaidHandler handler = new FeePaidHandler(mockFeeFinesRepository,
        FreemarkerRepository.getInstance().getFreemarkerTemplate(Command.FEE_PAID_RESPONSE));

    final String expectedString = "38" + "Y"
        + TestUtils.getFormattedLocalDateTime(OffsetDateTime.now(clock))
        + "AO" + "diku" + "|" + "AA" + patronIdentifier + "|"
        + "BK" + transactionId + "|"
        + "CG" + accountIdentifier + "|"
        + "FA" + "3.33" + "|"
        + "FC" + "13.11.2023" + "|"
        + "FE" + feeFineIdentifier + "|"
        + "FG" + feeAmount + "|";

    handler.execute(feePaid, sessionData).onComplete(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          assertNotNull(sipMessage);
          assertEquals(expectedString, sipMessage);
          testContext.completeNow();
        }
    )));
  }
}
