package org.folio.edge.sip2.handlers;

import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.edge.sip2.handlers.freemarker.FreemarkerUtils.executeFreemarkerTemplate;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.PatronAccountInfo;
import org.folio.edge.sip2.domain.messages.requests.FeePaid;
import org.folio.edge.sip2.domain.messages.responses.FeePaidResponse;
import org.folio.edge.sip2.handlers.freemarker.FormatDateTimeMethodModel;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.parser.Command;
import org.folio.edge.sip2.repositories.FeeFinesRepository;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.support.tags.UnitTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith({VertxExtension.class, MockitoExtension.class})
class FeePaidHandlerTests {

  private final FreemarkerRepository freemarkerRepository = new FreemarkerRepository();

  @AfterEach
  void tearDown() {
    System.clearProperty("sip2TemplateLocale");
  }

  @Test
  void canPayFeeWithHandler(Vertx vertx,
      VertxTestContext testContext,
      @Mock FeeFinesRepository mockFeeFinesRepository) {

    final String patronIdentifier = "1029384756";
    final String accountIdentifier = "c78489bd-4d1b-4e4f-87d3-caa915946aa4";
    final String feeFineIdentifier = "9ffed8e5-d1b2-4857-a07b-30199204783d";
    final String transactionId = "7e15ba2d-cc85-4226-963d-d6c7d5c03f26";
    final double feeAmount = 66.67;
    final String itemBarcode = "a32451";
    final List<PatronAccountInfo> patronAccountInfoList = new ArrayList<>();
    final PatronAccountInfo patronAccountInfo = new PatronAccountInfo();
    final String feeFineCreationDate = "2023-11-13T10:15:02+01:00";
    final double feeRemaining = 3.33;
    final String feeRemainingString = String.format(Locale.ROOT, "%.2f", feeRemaining);
    final String feeAmountString = String.format(Locale.ROOT, "%.2f", feeAmount);

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
        .feeAmount(feeAmountString)
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
        freemarkerRepository.getFreemarkerTemplate(Command.FEE_PAID_RESPONSE));

    final String expectedString = "38" + "Y"
        + TestUtils.getFormattedLocalDateTime(OffsetDateTime.now(clock))
        + "AO" + "diku" + "|" + "AA" + patronIdentifier + "|"
        + "BK" + transactionId + "|"
        + "CG" + accountIdentifier + "|"
        + "FA" + feeRemainingString + "|"
        + "FC" + "13.11.2023" + "|"
        + "FE" + feeFineIdentifier + "|"
        + "FG" + feeAmountString + "|";

    handler.execute(feePaid, sessionData).onComplete(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          assertNotNull(sipMessage);
          assertEquals(expectedString, sipMessage);
          testContext.completeNow();
        }
    )));
  }

  @Test
  void renderFeePaidResponse_positive_defaultLocale() {
    var result = renderFeePaidResponse(new FreemarkerRepository());
    assertThat(result).contains("FA0.01", "FG0.05");
  }

  @Test
  void renderFeePaidResponse_positive_sip2LocaleSystemPropertyIsRespected() {
    System.setProperty("sip2TemplateLocale", "de-DE");
    var result = renderFeePaidResponse(new FreemarkerRepository());
    assertThat(result).contains("FA0,01", "FG0,05");
  }

  @Test
  void renderFeePaidResponse_positive_sip2LocaleIsInvalid() {
    System.setProperty("sip2TemplateLocale", "not_a_locale");
    var result = renderFeePaidResponse(new FreemarkerRepository());
    assertThat(result).contains("FA0.01", "FG0.05");
  }

  private String renderFeePaidResponse(FreemarkerRepository repo) {
    var account = new PatronAccountInfo();
    account.setFeeFineRemaining(0.01);
    account.setFeeFinePaid(0.05);

    var response = FeePaidResponse.builder()
        .paymentAccepted(Boolean.TRUE)
        .transactionDate(OffsetDateTime.parse("2025-01-09T00:00:00Z"))
        .institutionId("diku")
        .patronIdentifier("123456")
        .patronAccountInfoList(List.of(account))
        .build();

    Map<String, Object> root = new HashMap<>();
    root.put("feePaidResponse", response);
    root.put("delimiter", '|');
    root.put("timezone", "UTC");
    root.put("formatDateTime", new FormatDateTimeMethodModel());

    var template = repo.getFreemarkerTemplate(Command.FEE_PAID_RESPONSE);
    var sessionData = SessionData.createSession("diku", '|', false, "UTF-8");
    return executeFreemarkerTemplate(sessionData, root, template);
  }
}
