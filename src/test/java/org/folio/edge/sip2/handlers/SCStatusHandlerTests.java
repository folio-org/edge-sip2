package org.folio.edge.sip2.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import org.folio.edge.sip2.domain.messages.enumerations.Messages;
import org.folio.edge.sip2.domain.messages.enumerations.StatusCode;
import org.folio.edge.sip2.domain.messages.requests.SCStatus;
import org.folio.edge.sip2.repositories.ConfigurationRepository;
import org.folio.edge.sip2.repositories.DefaultResourceProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class SCStatusHandlerTests {

  @Test
  public void canExecuteASampleScStatusRequestUsingHandlersFactory(
      Vertx vertx,
      VertxTestContext testContext) {

    DefaultResourceProvider defaultConfigurationProvider = new DefaultResourceProvider();

    SCStatus.SCStatusBuilder statusBuilder = SCStatus.builder();
    statusBuilder.maxPrintWidth(20);
    statusBuilder.protocolVersion("1.00");
    statusBuilder.statusCode(StatusCode.SC_OK);
    SCStatus status =  statusBuilder.build();

    SCStatusHandler handler = ((SCStatusHandler) HandlersFactory
        .getScStatusHandlerInstance(null, defaultConfigurationProvider, null));

    handler.execute(status, null).setHandler(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          // Because the sipMessage has a dateTime component that's supposed
          // to be current, we can't assert on the entirety of the string,
          // have to break it up into pieces.
          String expectedPreLocalTime = "98YYNYNN53" + getFormattedDateString();
          String expectedPostLocalTime =
              "1.23|AOfs00000010test|AMChalmers|BXYNNNYNYNNNNNNNYN|ANTL01|"
              + "AFscreenMessages|AGline|";
          String expectedBlankSpaces = "    ";
          assertEquals(expectedPreLocalTime, sipMessage.substring(0, 18));
          assertEquals(expectedBlankSpaces, sipMessage.substring(18, 22));
          assertEquals(expectedPostLocalTime, sipMessage.substring(28));
          testContext.completeNow();
        })));
  }

  @Test
  public void cannotGetAValidResponseDueToMissingTemplate(
      Vertx vertx,
      VertxTestContext testContext) {
    DefaultResourceProvider defaultConfigurationProvider = new DefaultResourceProvider();
    ConfigurationRepository configurationRepository =
        new ConfigurationRepository(defaultConfigurationProvider);

    SCStatus.SCStatusBuilder statusBuilder = SCStatus.builder();
    statusBuilder.maxPrintWidth(20);
    statusBuilder.protocolVersion("1.00");
    statusBuilder.statusCode(StatusCode.SC_OK);
    SCStatus status =  statusBuilder.build();

    SCStatusHandler handler = new SCStatusHandler(configurationRepository, null);

    handler.execute(status, null).setHandler(
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

  private String getFormattedDateString() {
    String pattern = "YYYYMMdd";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    return simpleDateFormat.format(new Date());
  }
}
