package org.folio.edge.sip2.handlers;

import static org.folio.edge.sip2.parser.Command.END_SESSION_RESPONSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import freemarker.template.Template;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.requests.EndPatronSession;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.session.SessionData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({VertxExtension.class})
public class EndPatronSessionHandlerTests {
  @Test
  public void canSuccessfullyGetEndPatronSessionResponse(Vertx vertx,
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

    Template template = FreemarkerRepository
        .getInstance()
        .getFreemarkerTemplate(END_SESSION_RESPONSE);

    final EndPatronSessionHandler handler = new EndPatronSessionHandler(template);

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
          assertNull(sessionData.getAuthenticationToken());
          assertNull(sessionData.getUsername());
          assertNull(sessionData.getPassword());

          testContext.completeNow();
        })));
  }
}
