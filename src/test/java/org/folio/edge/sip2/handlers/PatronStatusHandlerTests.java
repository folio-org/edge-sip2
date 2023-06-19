package org.folio.edge.sip2.handlers;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.folio.edge.sip2.parser.Command.PATRON_STATUS_RESPONSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.enumerations.Language;
import org.folio.edge.sip2.domain.messages.enumerations.PatronStatus;
import org.folio.edge.sip2.domain.messages.requests.PatronStatusRequest;
import org.folio.edge.sip2.domain.messages.responses.PatronStatusResponse;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.repositories.PatronRepository;
import org.folio.edge.sip2.repositories.domain.Personal;
import org.folio.edge.sip2.repositories.domain.User;
import org.folio.edge.sip2.session.SessionData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith({VertxExtension.class, MockitoExtension.class})
class PatronStatusHandlerTests {
  @Test
  void canExecutePatronStatus(Vertx vertx,
      VertxTestContext testContext,
      @Mock PatronRepository mockPatronRepository) {

    final Clock clock = TestUtils.getUtcFixedClock();
    final OffsetDateTime nbDueDate = OffsetDateTime.now();
    final String patronIdentifier = "1029384756";
    final String patronPassword = "1234";
    final String institutionId = "diku";
    final String userId = "99a81cee-d439-42c8-9860-2bd1de881c4a";
    final String userBarcode = "2349871212";
    final Float feeAmount = 34.50f;
    final Personal personal = new Personal.Builder()
        .firstName("Joe")
        .middleName("Zee")
        .lastName("Blow")
        .build();

    final User user = new User.Builder()
        .id(userId)
        .barcode(userBarcode)
        .personal(personal)
        .build();

    final PatronStatusRequest patronStatus = PatronStatusRequest.builder()
        .patronIdentifier(patronIdentifier)
        .patronPassword(patronPassword)
        .institutionId(institutionId)
        .transactionDate(OffsetDateTime.now())
        .language(Language.UNKNOWN)
        .build();

    final PatronStatusResponse patronStatusResponse = PatronStatusResponse.builder()
        .personalName("Joe Zee Blow")
        .feeAmount(feeAmount.toString())
        .patronStatus(EnumSet.allOf(PatronStatus.class))
        .transactionDate(OffsetDateTime.now(clock))
        .institutionId(institutionId)
        .language(Language.UNKNOWN)
        .patronIdentifier(patronIdentifier)
        .validPatron(TRUE)
        .validPatronPassword(TRUE)
        .build();

    final SessionData sessionData = TestUtils.getMockedSessionData();
    final String expectedString = "24" + "YYYYYYYYYYYYYY" + "000"
        + TestUtils.getFormattedLocalDateTime(OffsetDateTime.now(clock))
        + "AO" + institutionId + "|" + "AA" + patronIdentifier + "|"
        + "AE" + "Joe Zee Blow" + "|" + "BL" + "Y" + "|" + "CQ" + "Y" + "|"
        + "BV" + feeAmount.toString() + "|";

    when(mockPatronRepository.performPatronStatusCommand(any(), any()))
        .thenReturn(Future.succeededFuture(patronStatusResponse));

    PatronStatusHandler handler = new PatronStatusHandler(mockPatronRepository,
        FreemarkerRepository.getInstance().getFreemarkerTemplate(PATRON_STATUS_RESPONSE));

    handler.execute(patronStatus, sessionData).onComplete(testContext.succeeding(
        sipMessage -> testContext.verify(() -> {
          assertEquals(expectedString, sipMessage);
          testContext.completeNow();
        })));
  }




}
