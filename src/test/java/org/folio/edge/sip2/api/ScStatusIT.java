package org.folio.edge.sip2.api;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.edge.sip2.support.Sip2TestCommand.sip2Exchange;
import static org.folio.edge.sip2.support.model.StatusCommand.SIP2_PROTOCOL_VERSION;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Set;
import org.folio.edge.sip2.api.support.AbstractErrorDetectionEnabledTest;
import org.folio.edge.sip2.domain.messages.enumerations.Messages;
import org.folio.edge.sip2.domain.messages.responses.ACSStatus;
import org.folio.edge.sip2.support.Sip2Commands;
import org.folio.edge.sip2.support.model.RawCommand;
import org.folio.edge.sip2.support.response.ScStatusResponseParser;
import org.folio.edge.sip2.support.tags.IntegrationTest;
import org.folio.edge.sip2.support.wiremock.WiremockStubs;
import org.junit.jupiter.api.Test;

@IntegrationTest
class ScStatusIT extends AbstractErrorDetectionEnabledTest {

  @Test
  @WiremockStubs({
      "/wiremock/stubs/mod-settings/200-get-locale.json",
      "/wiremock/stubs/mod-settings/200-get-settings.json",
      "/wiremock/stubs/mod-login/201-post-acs-login.json",
  })
  void getAcsStatus_positive() throws Throwable {
    var timezone = "Europe/Paris";
    var currentTs = OffsetDateTime.now(ZoneId.of(timezone)).truncatedTo(SECONDS);
    executeInSession(
        successLoginExchange(),
        sip2Exchange(Sip2Commands.status(), sip2Result -> {
          assertSuccessfulExchange(sip2Result);
          var respMsg = sip2Result.getResponseMessage();
          var acsStatus = new ScStatusResponseParser(delimiter, timezone).parse(respMsg);
          assertThat(acsStatus)
              .satisfies(status -> assertThat(status.getDateTimeSync())
                  .isAfterOrEqualTo(currentTs)
                  .isBeforeOrEqualTo(currentTs.plusSeconds(5)))
              .usingRecursiveComparison()
              .ignoringFields("dateTimeSync")
              .isEqualTo(expectedAcsStatus());
        }));
  }

  @Test
  @WiremockStubs({
      "/wiremock/stubs/mod-settings/404-get-locale.json",
      "/wiremock/stubs/mod-settings/200-get-settings(empty).json",
      "/wiremock/stubs/mod-login/201-post-acs-login.json",
      "/wiremock/stubs/mod-configuration/200-get-configuration.json"
  })
  void getAcsStatus_positive_allSettingsNotFound() throws Throwable {
    var timezone = "America/New_York";
    var currentTs = OffsetDateTime.now(ZoneId.of(timezone)).truncatedTo(SECONDS);
    executeInSession(
        successLoginExchange(),
        sip2Exchange(Sip2Commands.status(), sip2Result -> {
          assertSuccessfulExchange(sip2Result);
          var respMsg = sip2Result.getResponseMessage();
          var acsStatus = new ScStatusResponseParser(delimiter, timezone).parse(respMsg);
          assertThat(acsStatus)
              .satisfies(status -> assertThat(status.getDateTimeSync())
                  .isAfterOrEqualTo(currentTs)
                  .isBeforeOrEqualTo(currentTs.plusSeconds(5)))
              .usingRecursiveComparison()
              .ignoringFields("dateTimeSync")
              .isEqualTo(expectedAcsStatus());
        }));
  }

  @Test
  @WiremockStubs({
      "/wiremock/stubs/mod-settings/200-get-locale.json",
      "/wiremock/stubs/mod-settings/200-get-settings(empty).json",
      "/wiremock/stubs/mod-login/201-post-acs-login.json",
      "/wiremock/stubs/mod-configuration/200-get-configuration.json"
  })
  void getAcsStatus_positive_settingsNotFound() throws Throwable {
    var timezone = "Europe/Paris";
    var currentTs = OffsetDateTime.now(ZoneId.of(timezone)).truncatedTo(SECONDS);
    executeInSession(
        successLoginExchange(),
        sip2Exchange(Sip2Commands.status(), sip2Result -> {
          assertSuccessfulExchange(sip2Result);
          var respMsg = sip2Result.getResponseMessage();
          var acsStatus = new ScStatusResponseParser(delimiter, timezone).parse(respMsg);
          assertThat(acsStatus)
              .satisfies(status -> assertThat(status.getDateTimeSync())
                  .isAfterOrEqualTo(currentTs)
                  .isBeforeOrEqualTo(currentTs.plusSeconds(5)))
              .usingRecursiveComparison()
              .ignoringFields("dateTimeSync")
              .isEqualTo(expectedAcsStatus());
        }));
  }

  @Test
  @WiremockStubs({
      "/wiremock/stubs/mod-settings/200-get-locale.json",
      "/wiremock/stubs/mod-settings/200-get-settings.json",
      "/wiremock/stubs/mod-login/201-post-acs-login.json",
  })
  void getAcsStatus_negative_resendMessageIsExpectedForInvalidCommand() throws Throwable {
    var scStatusMessage = "9900401.00AY1AZAAAA";
    executeInSession(
        successLoginExchange(),
        sip2Exchange(
            Sip2Commands.raw(scStatusMessage, true),
            sip2Result -> {
              assertSuccessfulExchange(sip2Result);
              assertThat(sip2Result.getResponseMessage()).isEqualTo("96AZFEF6");
            }));
  }

  @Test
  @WiremockStubs({
      "/wiremock/stubs/mod-settings/200-get-locale.json",
      "/wiremock/stubs/mod-settings/200-get-settings.json",
      "/wiremock/stubs/mod-login/201-post-acs-login.json",
  })
  void getAcsStatus_negative_invalidStatusRequestAndGetErrorMessage() throws Throwable {
    executeInSession(
        successLoginExchange(),
        sip2Exchange(RawCommand.of("990231.23", false),
            sip2Result -> {
              assertFalse(sip2Result.isSuccessfulExchange());
              assertFalse(sip2Result.isChecksumValid());
              assertThat(sip2Result.getResponseMessage());
            }));
  }

  private static ACSStatus expectedAcsStatus() {
    return ACSStatus.builder()
        .onLineStatus(true)
        .checkinOk(true)
        .checkoutOk(true)
        .acsRenewalPolicy(true)
        .statusUpdateOk(false)
        .offLineOk(false)
        .timeoutPeriod(5)
        .retriesAllowed(3)
        .protocolVersion(SIP2_PROTOCOL_VERSION)
        .institutionId(TENANT_ID)
        .libraryName("TestLibrary")
        .terminalLocation(SERVICE_POINT_ID)
        .printLine(Collections.emptyList())
        .screenMessage(Collections.emptyList())
        .supportedMessages(Set.of(
            Messages.PATRON_STATUS_REQUEST,
            Messages.CHECKOUT,
            Messages.CHECKIN,
            Messages.SC_ACS_STATUS,
            Messages.LOGIN,
            Messages.PATRON_INFORMATION,
            Messages.END_PATRON_SESSION,
            Messages.FEE_PAID,
            Messages.ITEM_INFORMATION,
            Messages.RENEW,
            Messages.RENEW_ALL
        ))
        .build();
  }
}
