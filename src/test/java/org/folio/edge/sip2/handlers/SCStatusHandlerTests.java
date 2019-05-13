package org.folio.edge.sip2.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.enumerations.Messages;
import org.folio.edge.sip2.domain.messages.enumerations.StatusCode;
import org.folio.edge.sip2.domain.messages.requests.SCStatus;
import org.folio.edge.sip2.repositories.ConfigurationRepository;
import org.folio.edge.sip2.repositories.DefaultResourceProvider;
import org.folio.edge.sip2.repositories.IRequestData;
import org.folio.edge.sip2.repositories.IResourceProvider;
import org.folio.edge.sip2.session.SessionData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class SCStatusHandlerTests {
/*
  @Test
  public void canExecuteASampleScStatusRequestUsingHandlersFactoryIntegration(
    Vertx vertx,
    VertxTestContext testContext) {

    String okapiUrl = "http://okapi-fse-eu-central-1.folio.ebsco.com:9130";

    IResourceProvider defaultConfigurationProvider = new FolioResourceProvider(okapiUrl, vertx);
    Clock clock = TestUtils.getUtcFixedClock();

    Login login = Login.builder()
      .locationCode("bf042bfd-a0ad-420c-8188-8e2ae01d9c0c")
      .loginUserId("sip2admin")
      .loginPassword("sip2admin")
      .build();

    LoginRepository loginRepository = new LoginRepository(defaultConfigurationProvider);
    LoginHandler loginHandler = new LoginHandler(loginRepository, FreemarkerRepository.getInstance().getFreemarkerTemplate(Command.LOGIN_RESPONSE));

    SessionData sessionData = SessionData.createSession("fs00001000", '|', true, "IBM850");
    sessionData.setScLocation("bf042bfd-a0ad-420c-8188-8e2ae01d9c0c");
    //sessionData.setTimeZone("Etc/UTC");

    loginHandler.execute(login, sessionData).setHandler(
      testContext.succeeding( loginMessage -> {
        SCStatus.SCStatusBuilder statusBuilder = SCStatus.builder();
        statusBuilder.maxPrintWidth(20);
        statusBuilder.protocolVersion("1.00");
        statusBuilder.statusCode(StatusCode.SC_OK);
        SCStatus status =  statusBuilder.build();

        SCStatusHandler handler = ((SCStatusHandler) HandlersFactory
          .getScStatusHandlerInstance(null, defaultConfigurationProvider, null,
            clock, okapiUrl, vertx));

        handler.execute(status, sessionData).setHandler(
          testContext.succeeding(sipMessage -> testContext.verify(() -> {
            // Because the sipMessage has a dateTime component that's supposed
            // to be current, we can't assert on the entirety of the string,
            // have to break it up into pieces.

            String expectedDateTimeString =
              TestUtils.getFormattedLocalDateTime(OffsetDateTime.now(clock));

            String expectedSipResponse = "98YYNYNN005003"
              + expectedDateTimeString
              + "1.23AOfs00000010test|AMChalmers|BXYNNNYNYNNNNNNNYN|ANTL01|";

            assertEquals(expectedSipResponse, sipMessage);
            testContext.completeNow();
          })));
      })
    );
  }
*/
  @Test
  public void canExecuteASampleScStatusRequestUsingHandlersFactory(
      Vertx vertx,
      VertxTestContext testContext) {

    IResourceProvider<IRequestData> defaultConfigurationProvider = new DefaultResourceProvider("DefaultACSConfiguration.json");
    Clock clock = TestUtils.getUtcFixedClock();

    SCStatusHandler handler = ((SCStatusHandler) HandlersFactory
        .getScStatusHandlerInstance(null, defaultConfigurationProvider, null,
          clock, "abcdefg.com", vertx));

    SessionData sessionData = TestUtils.getMockedSessionData();
    sessionData.setScLocation("TL01");

    handler.execute(getMockedSCStatusMessage(), sessionData).setHandler(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          // Because the sipMessage has a dateTime component that's supposed
          // to be current, we can't assert on the entirety of the string,
          // have to break it up into pieces.

          String expectedDateTimeString =
              TestUtils.getFormattedLocalDateTime(OffsetDateTime.now(clock));

          String expectedSipResponse = "98YYNYNN005003"
              + expectedDateTimeString
              + "2.00AOdikutest|AMdiku|BXYNNNYNYNNNNNNNYN|ANTL01|";

          assertEquals(expectedSipResponse, sipMessage);
          testContext.completeNow();
        })));
  }

  @Test
  public void canGetValidScStatusRequestWithNonDefaultTimezone(
      Vertx vertx,
      VertxTestContext testContext) {

    IResourceProvider<IRequestData> defaultConfigurationProvider =
      new DefaultResourceProvider(
        "json/DefaultACSConfigurationNonDefaultedTimezone.json");
    Clock clock = TestUtils.getUtcFixedClock();

    SCStatusHandler handler = ((SCStatusHandler) HandlersFactory
        .getScStatusHandlerInstance(null, defaultConfigurationProvider, null,
            clock, "abcdefg.com", vertx));

    SessionData sessionData = TestUtils.getMockedSessionData();
    sessionData.setScLocation("TL01");

    handler.execute(getMockedSCStatusMessage(), sessionData).setHandler(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          // Because the sipMessage has a dateTime component that's supposed
          // to be current, we can't assert on the entirety of the string,
          // have to break it up into pieces.

          String expectedDateTimeString =
              TestUtils.getFormattedLocalDateTime(
                OffsetDateTime.now(clock).plusHours(2)); //Europe/Stockholm is 2 hours ahead of UTC

          String expectedSipResponse = "98YYNYNN005003"
              + expectedDateTimeString
              + "2.00AOdikutest|AMdiku|BXYNNNYNYNNNNNNNYN|ANTL01|";

          assertEquals(expectedSipResponse, sipMessage);
          testContext.completeNow();
        })));
  }

  @Test
  public void cannotGetAValidResponseDueToMissingTemplate(
      Vertx vertx,
      VertxTestContext testContext) {
    IResourceProvider<IRequestData> defaultConfigurationProvider = new DefaultResourceProvider();
    ConfigurationRepository configurationRepository =
        new ConfigurationRepository(defaultConfigurationProvider, TestUtils.getUtcFixedClock());

    SCStatusHandler handler = new SCStatusHandler(configurationRepository, null);

    handler.execute(getMockedSCStatusMessage(),
                    TestUtils.getMockedSessionData()).setHandler(
        testContext.failing(throwable -> testContext.verify(() -> {
          assertEquals("", throwable.getMessage());
          testContext.completeNow();
        })));
  }


  @Test
  public void cannotGetAValidResponseDueToMissingConfig(
    Vertx vertx,
    VertxTestContext testContext) {

    IResourceProvider<IRequestData> defaultConfigurationProvider =
        new DefaultResourceProvider("json/ACSConfigurationWithMissingConfigs.json");
    ConfigurationRepository configurationRepository =
      new ConfigurationRepository(defaultConfigurationProvider, TestUtils.getUtcFixedClock());

    SCStatusHandler handler = new SCStatusHandler(configurationRepository, null);

    handler.execute(getMockedSCStatusMessage(),
                    TestUtils.getMockedSessionData()).setHandler(
      testContext.failing(throwable -> testContext.verify(() -> {
        assertEquals("Unable to find all necessary configuration(s). Found 2 of 3", throwable.getMessage());
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

  private SCStatus getMockedSCStatusMessage() {
    SCStatus.SCStatusBuilder statusBuilder = SCStatus.builder();
    statusBuilder.maxPrintWidth(20);
    statusBuilder.protocolVersion("1.00");
    statusBuilder.statusCode(StatusCode.SC_OK);
    SCStatus status =  statusBuilder.build();

    return status;
  }
}
