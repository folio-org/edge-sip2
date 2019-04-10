package org.folio.edge.sip2.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.folio.edge.sip2.domain.messages.enumerations.Messages;
import org.folio.edge.sip2.domain.messages.enumerations.StatusCode;
import org.folio.edge.sip2.domain.messages.requests.SCStatus;
import org.folio.edge.sip2.repositories.ConfigurationRepository;
import org.folio.edge.sip2.repositories.DefaultResourceProvider;
import org.junit.jupiter.api.Test;

public class SCStatusHandlerTests {

  @Test
  public void canExecuteASampleScStatusRequestUsingHandlersFactory() {

    DefaultResourceProvider defaultConfigurationProvider = new DefaultResourceProvider();

    SCStatusHandler handler = ((SCStatusHandler) HandlersFactory.getScStatusHandlerInstance(
        null, defaultConfigurationProvider, null));

    SCStatus.SCStatusBuilder statusBuilder = SCStatus.builder();
    statusBuilder.maxPrintWidth(20);
    statusBuilder.protocolVersion("1.00");
    statusBuilder.statusCode(StatusCode.SC_OK);
    SCStatus status =  statusBuilder.build();

    String sipMessage = handler.execute(status);
    //Because the sipMessage has a dateTime component that's supposed to be current,
    //we can't assert on the entirety of the string, have to break it up into pieces.
    String expectedPreLocalTime = "98YYNYNN53" + getFormattedDateString();
    String expectedPostLocalTime =
        "1.23|AOfs00000010test|AMChalmers|BXYNNNYNYNNNNNNNYN|ANTL01|AFscreenMessages|AGline|\r";
    String expectedBlankSpaces = "    ";

    assertEquals(sipMessage.substring(0, 18), expectedPreLocalTime);
    assertEquals(sipMessage.substring(18, 22), expectedBlankSpaces);
    assertEquals(sipMessage.substring(28), expectedPostLocalTime);
  }

  @Test
  public void cannotGetAValidResponseDueToMissingTemplate() {
    DefaultResourceProvider defaultConfigurationProvider = new DefaultResourceProvider();
    ConfigurationRepository configurationRepository =
        new ConfigurationRepository(defaultConfigurationProvider);

    SCStatusHandler handler = new SCStatusHandler(configurationRepository, null);

    SCStatus.SCStatusBuilder statusBuilder = SCStatus.builder();
    statusBuilder.maxPrintWidth(20);
    statusBuilder.protocolVersion("1.00");
    statusBuilder.statusCode(StatusCode.SC_OK);
    SCStatus status =  statusBuilder.build();

    String sipMessage = handler.execute(status);
    assertEquals("", sipMessage);
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
    return simpleDateFormat.format(new Date());
  }
}
