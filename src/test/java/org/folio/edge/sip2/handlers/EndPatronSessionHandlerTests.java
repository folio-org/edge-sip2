package org.folio.edge.sip2.handlers;

import static java.lang.Boolean.TRUE;
import static org.folio.edge.sip2.parser.Command.END_SESSION_RESPONSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import freemarker.template.Template;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.requests.EndPatronSession;
import org.folio.edge.sip2.domain.messages.responses.EndSessionResponse;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.repositories.PatronRepository;
import org.folio.edge.sip2.session.SessionData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
public class EndPatronSessionHandlerTests {
  @Test
  public void canSuccessfullyGetEndPatronSessionResponse(
      @Mock PatronRepository mockPatronRepository,
      Vertx vertx,
      VertxTestContext testContext) {

    final String institutionId = "fs00000001";
    final String patronIdentifier = "patronId1234";
    final String patronPassword = "patronPassword";
    final Clock clock = TestUtils.getUtcFixedClock();

    final EndPatronSession endPatronSessionRequest = EndPatronSession.builder()
        .patronIdentifier(patronIdentifier)
        .patronPassword(patronPassword)
        .institutionId(institutionId)
        .terminalPassword("12345")
        .transactionDate(OffsetDateTime.now(clock))
        .build();

    when(mockPatronRepository.performEndPatronSessionCommand(any(), any()))
        .thenReturn(Future.succeededFuture(EndSessionResponse.builder()
          .endSession(TRUE)
          .transactionDate(OffsetDateTime.now(clock))
          .institutionId(institutionId)
          .patronIdentifier(patronIdentifier)
          .build()));
    Template template = FreemarkerRepository
        .getInstance()
        .getFreemarkerTemplate(END_SESSION_RESPONSE);

    final EndPatronSessionHandler handler =
        new EndPatronSessionHandler(mockPatronRepository, template);

    final SessionData sessionData = TestUtils.getMockedSessionData();
    sessionData.setPassword("some random password");
    sessionData.setUsername("JoeSmith");
    sessionData.setAuthenticationToken("abcdefghijklmnop");

    handler.execute(endPatronSessionRequest, sessionData).setHandler(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          final String expectedString = "36Y"
              + OffsetDateTime.now(clock).format(DateTimeFormatter.ofPattern("yyyyMMdd    HHmmss"))
              + "AO" + institutionId + "|AA" + patronIdentifier + '|';

          assertEquals(expectedString, sipMessage);

          //through the magic of Pass-by-Reference, sessionData was updated
          //and we can see its updated values without having it explicitly being returned
          assertEquals("abcdefghijklmnop", sessionData.getAuthenticationToken());
          assertEquals("JoeSmith", sessionData.getUsername());
          assertEquals("some random password", sessionData.getPassword());

          testContext.completeNow();
        })));
  }
}
