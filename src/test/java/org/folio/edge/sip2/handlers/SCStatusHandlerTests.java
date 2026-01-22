package org.folio.edge.sip2.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import freemarker.template.Template;
import io.vertx.core.Future;
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
import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.parser.Command;
import org.folio.edge.sip2.repositories.ConfigurationRepository;
import org.folio.edge.sip2.repositories.DefaultResourceProvider;
import org.folio.edge.sip2.repositories.IRequestData;
import org.folio.edge.sip2.repositories.IResource;
import org.folio.edge.sip2.repositories.IResourceProvider;
import org.folio.edge.sip2.repositories.SettingsRepository;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.Utils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class SCStatusHandlerTests {

  private static final String DEFAULT_ACS_CONFIG_TEST_FILE = "json/DefaultACSConfiguration.json";

  @Test
  void canExecuteASampleScStatusRequestUsingHandlersFactory(VertxTestContext testContext) {
    var clock = TestUtils.getUtcFixedClock();
    var handler = getScStatusHandler(
        "json/AcsLocale.json",
        "json/DefaultACSSettings.json");

    SessionData sessionData = TestUtils.getMockedSessionData();
    sessionData.setScLocation("TL01");

    handler.execute(getMockedSCStatusMessage(), sessionData).onComplete(
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
  void canGetValidScStatusRequestWithNonDefaultTimezone(VertxTestContext testContext) {
    Clock clock = TestUtils.getUtcFixedClock();
    var handler = getScStatusHandler(
        "json/AcsLocaleNonDefaultTimezone.json",
        "json/DefaultACSSettings.json");

    SessionData sessionData = TestUtils.getMockedSessionData();
    sessionData.setScLocation("TL01");

    handler.execute(getMockedSCStatusMessage(), sessionData).onComplete(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          // Because the sipMessage has a dateTime component that's supposed
          // to be current, we can't assert on the entirety of the string,
          // have to break it up into pieces.

          String expectedDateTimeString =
              TestUtils.getFormattedLocalDateTime(
                  Utils.convertDateTime(OffsetDateTime.now(clock), "Europe/Stockholm"));

          String expectedSipResponse = "98YYNYNN005003"
              + expectedDateTimeString
              + "2.00AOdikutest|AMdiku|BXYNNNYNYNNNNNNNYN|ANTL01|";

          assertEquals(expectedSipResponse, sipMessage);
          testContext.completeNow();
        })));
  }

  @Test
  void cannotGetAValidResponseDueToMissingTemplate(VertxTestContext testContext) {
    var handler = getScStatusHandler("json/AcsLocale.json",
        DEFAULT_ACS_CONFIG_TEST_FILE, "json/DefaultACSSettings.json", null);

    handler.execute(getMockedSCStatusMessage(),
        TestUtils.getMockedSessionData()).onComplete(
        testContext.failing(throwable -> testContext.verify(() -> {
          assertEquals("", throwable.getMessage());
          testContext.completeNow();
        })));
  }

  @Disabled("Disabled until configuration policy is more concrete")
  @Test
  void cannotGetAValidResponseDueToMissingConfig(VertxTestContext testContext) {
    var handler = getScStatusHandler(
        "json/ACSConfigurationWithMissingConfigs.json",
        "json/ACSSettingsWithMissingConfigs.json");

    handler.execute(getMockedSCStatusMessage(),
        TestUtils.getMockedSessionData()).onComplete(
        testContext.failing(throwable -> testContext.verify(() -> {
          assertEquals("Unable to find all necessary configuration(s). Found 2 of 3",
              throwable.getMessage());
          testContext.completeNow();
        })));
  }

  @Disabled("Disabled until configuration policy is more concrete")
  @Test
  void cannotGetAValidResponseDueToMissingLocationCode(VertxTestContext testContext) {
    var handler = getScStatusHandler(
        "json/ACSConfigurationWithMissingConfigs.json",
        "json/ACSSettingsWithMissingConfigs.json");

    handler.execute(getMockedSCStatusMessage(),
        TestUtils.getMockedSessionData1()).onComplete(
        testContext.failing(throwable -> testContext.verify(() -> {
          assertEquals("Configuration error: please add a value to Location Code.",
              throwable.getMessage());
          testContext.completeNow();
        })));
  }

  @Test
  void canGetValidPackagedSupportedMessages() {
    Set<Messages> supportedMessages = new HashSet<>();
    supportedMessages.add(Messages.CHECKIN);
    supportedMessages.add(Messages.CHECKOUT);
    supportedMessages.add(Messages.HOLD);
    supportedMessages.add(Messages.RENEW);

    var psm = new SCStatusHandler.PackagedSupportedMessages(supportedMessages);
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

  private static SCStatusHandler getScStatusHandler(String localeFile, String settingsFile) {
    var template = FreemarkerRepository.getInstance().getFreemarkerTemplate(Command.ACS_STATUS);
    return getScStatusHandler(localeFile, DEFAULT_ACS_CONFIG_TEST_FILE, settingsFile, template);
  }

  private static SCStatusHandler getScStatusHandler(String localeFile, String configFile,
      String settingsFile, Template template) {
    var configurationProvider = new DefaultResourceProvider(configFile);
    var configRepository = new ConfigurationRepository(configurationProvider);

    var settingsProvider = new SettingsProvider(localeFile, settingsFile);
    var settingsRepository = new SettingsRepository(
        settingsProvider, TestUtils.getUtcFixedClock(), configRepository);

    return new SCStatusHandler(settingsRepository, template);
  }

  private SCStatus getMockedSCStatusMessage() {
    SCStatus.SCStatusBuilder statusBuilder = SCStatus.builder();
    statusBuilder.maxPrintWidth(20);
    statusBuilder.protocolVersion("1.00");
    statusBuilder.statusCode(StatusCode.SC_OK);
    return statusBuilder.build();
  }

  private static class SettingsProvider implements IResourceProvider<IRequestData> {

    private final IResourceProvider<IRequestData> localeResourceProvider;
    private final IResourceProvider<IRequestData> settingsResourceProvider;

    private SettingsProvider(String localeFileName, String settingsResourceProvider) {
      this.localeResourceProvider = new DefaultResourceProvider(localeFileName);
      this.settingsResourceProvider = new DefaultResourceProvider(settingsResourceProvider);
    }

    @Override
    public Future<IResource> retrieveResource(IRequestData requestData) {
      if (requestData.getPath().startsWith("/locale")) {
        return localeResourceProvider.retrieveResource(requestData);
      }
      return settingsResourceProvider.retrieveResource(requestData);
    }

    @Override
    public Future<IResource> createResource(IRequestData fromData) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Future<IResource> editResource(IRequestData fromData) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Future<IResource> deleteResource(IRequestData resource) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Future<Boolean> doPinCheck(IRequestData fromData) {
      throw new UnsupportedOperationException();
    }
  }
}
