package org.folio.edge.sip2.handlers;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.folio.edge.sip2.parser.Command.CHECKIN_RESPONSE;
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
import java.util.UUID;
import org.folio.edge.sip2.domain.messages.requests.Checkin;
import org.folio.edge.sip2.domain.messages.responses.CheckinResponse;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.repositories.CirculationRepository;
import org.folio.edge.sip2.session.SessionData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
public class CheckinHandlerTests {
  @Test
  public void canExecuteASampleCheckinUsingHandler(
      @Mock CirculationRepository mockCirculationRepository,
      Vertx vertx,
      VertxTestContext testContext) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final ZonedDateTime returnDate = ZonedDateTime.now();
    final String institutionId = "diku";
    final String itemIdentifier = "1234567890";
    final String currentLocation = UUID.randomUUID().toString();
    final Checkin checkin = Checkin.builder()
        .noBlock(FALSE)
        .transactionDate(ZonedDateTime.now())
        .returnDate(returnDate)
        .currentLocation(currentLocation)
        .institutionId(institutionId)
        .itemIdentifier(itemIdentifier)
        .terminalPassword("1234")
        .itemProperties("Some property of this item")
        .cancel(FALSE)
        .build();

    when(mockCirculationRepository.checkin(any(), any()))
        .thenReturn(Future.succeededFuture(CheckinResponse.builder()
            .ok(TRUE)
            .resensitize(TRUE)
            .magneticMedia(null)
            .alert(FALSE)
            .transactionDate(ZonedDateTime.now(clock))
            .institutionId(institutionId)
            .itemIdentifier(itemIdentifier)
            .permanentLocation("Main Library")
            .build()));

    final CheckinHandler handler = new CheckinHandler(mockCirculationRepository,
        FreemarkerRepository.getInstance().getFreemarkerTemplate(CHECKIN_RESPONSE));

    final SessionData sessionData = SessionData.createSession(institutionId, '|', false, "IBM850");

    handler.execute(checkin, sessionData).setHandler(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          final String expectedString = "101YUN"
              + ZonedDateTime.now(clock).format(DateTimeFormatter.ofPattern("yyyyMMdd    HHmmss"))
              + "AO" + institutionId + "|AB" + itemIdentifier + "|AQMain Library|";

          assertEquals(expectedString, sipMessage);

          testContext.completeNow();
        })));
  }

  @Test
  public void canExecuteASampleFailedCheckinUsingHandler(
      @Mock CirculationRepository mockCirculationRepository,
      Vertx vertx,
      VertxTestContext testContext) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final ZonedDateTime returnDate = ZonedDateTime.now();
    final String institutionId = "diku";
    final String itemIdentifier = "1234567890";
    final String currentLocation = UUID.randomUUID().toString();
    final Checkin checkin = Checkin.builder()
        .noBlock(FALSE)
        .transactionDate(ZonedDateTime.now())
        .returnDate(returnDate)
        .currentLocation(currentLocation)
        .institutionId(institutionId)
        .itemIdentifier(itemIdentifier)
        .terminalPassword("1234")
        .itemProperties("Some property of this item")
        .cancel(FALSE)
        .build();

    when(mockCirculationRepository.checkin(any(), any()))
        .thenReturn(Future.succeededFuture(CheckinResponse.builder()
            .ok(FALSE)
            .resensitize(TRUE)
            .magneticMedia(null)
            .alert(FALSE)
            .transactionDate(ZonedDateTime.now(clock))
            .institutionId(institutionId)
            .itemIdentifier(itemIdentifier)
            .permanentLocation("")
            .build()));

    final CheckinHandler handler = new CheckinHandler(mockCirculationRepository,
        FreemarkerRepository.getInstance().getFreemarkerTemplate(CHECKIN_RESPONSE));

    final SessionData sessionData = SessionData.createSession(institutionId, '|', false, "IBM850");

    handler.execute(checkin, sessionData).setHandler(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          final String expectedString = "100YUN"
              + ZonedDateTime.now(clock).format(DateTimeFormatter.ofPattern("yyyyMMdd    HHmmss"))
              + "AO" + institutionId + "|AB" + itemIdentifier + "|AQ|";

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
