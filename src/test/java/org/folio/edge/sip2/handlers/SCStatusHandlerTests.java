package org.folio.edge.sip2.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.enumerations.Messages;
import org.folio.edge.sip2.domain.messages.enumerations.StatusCode;
import org.folio.edge.sip2.domain.messages.requests.SCStatus;
import org.folio.edge.sip2.repositories.ConfigurationRepository;
import org.folio.edge.sip2.repositories.DefaultResourceProvider;
import org.folio.edge.sip2.repositories.IResourceProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class SCStatusHandlerTests {

  @Test
  public void canExecuteASampleScStatusRequestUsingHandlersFactory(
      Vertx vertx,
      VertxTestContext testContext) {

    IResourceProvider defaultConfigurationProvider = new DefaultResourceProvider();
    Clock clock = TestUtils.getUtcFixedClock();


    SCStatus.SCStatusBuilder statusBuilder = SCStatus.builder();
    statusBuilder.maxPrintWidth(20);
    statusBuilder.protocolVersion("1.00");
    statusBuilder.statusCode(StatusCode.SC_OK);
    SCStatus status =  statusBuilder.build();

    SCStatusHandler handler = ((SCStatusHandler) HandlersFactory
        .getScStatusHandlerInstance(null, defaultConfigurationProvider, null,
          clock, "abcdefg.com", vertx));

    handler.execute(status, TestUtils.getMockedSessionData()).setHandler(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          // Because the sipMessage has a dateTime component that's supposed
          // to be current, we can't assert on the entirety of the string,
          // have to break it up into pieces.

          String expectedDateTimeString =
              TestUtils.getFormattedLocalDateTime(ZonedDateTime.now(clock));

          String expectedSipResponse = "98YYNYNN005003"
              + expectedDateTimeString
              + "1.23AOdikutest|AMdiku|BXYNNNYNYNNNNNNNYN|ANTL01|";

          assertEquals(expectedSipResponse, sipMessage);
          testContext.completeNow();
        })));
  }

  @Test
  public void cannotGetAValidResponseDueToMissingTemplate(
      Vertx vertx,
      VertxTestContext testContext) {
    IResourceProvider defaultConfigurationProvider = new DefaultResourceProvider();
    ConfigurationRepository configurationRepository =
        new ConfigurationRepository(defaultConfigurationProvider,
            Clock.fixed(Instant.now(), ZoneOffset.UTC));

    SCStatus.SCStatusBuilder statusBuilder = SCStatus.builder();
    statusBuilder.maxPrintWidth(20);
    statusBuilder.protocolVersion("1.00");
    statusBuilder.statusCode(StatusCode.SC_OK);
    SCStatus status =  statusBuilder.build();

    SCStatusHandler handler = new SCStatusHandler(configurationRepository, null);

    handler.execute(status, TestUtils.getMockedSessionData()).setHandler(
        testContext.failing(throwable -> testContext.verify(() -> {
          assertEquals("", throwable.getMessage());
          testContext.completeNow();
        })));
  }

  @Test
  public void canGetValidPackagedSupportedMessages() {
    Set<Messages> supportedMessages = new HashSet<>();
    supportedMessages.add(Messages.CHECKIN);
    supportedMessages.add(Messages.CHECKOUT);
    supportedMessages.add(Messages.HOLD);
    supportedMessages.add(Messages.RENEW);


    SCStatusHandler.PackagedSupportedMessages psm =
        new SCStatusHandler.PackagedSupportedMessages(supportedMessages);
    assertTrue(psm.getCheckIn());
    assertTrue(psm.getCheckOut());
    assertTrue(psm.getHold());
    assertTrue(psm.getRenew());
    assertFalse(psm.getBlockPatron());
    assertFalse(psm.getEndPatronSession());
    assertFalse(psm.getFeePaid());
    assertFalse(psm.getItemInformation());
    assertFalse(psm.getItemStatusUpdate());
    assertFalse(psm.getLogin());
    assertFalse(psm.getPatronEnable());
    assertFalse(psm.getPatronStatusRequest());
    assertFalse(psm.getRenewAll());
    assertFalse(psm.getRequestScAcsResend());
    assertFalse(psm.getScAcsStatus());
    assertFalse(psm.getPatronInformation());
  }
}
