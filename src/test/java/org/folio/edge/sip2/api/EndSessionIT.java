package org.folio.edge.sip2.api;

import static org.folio.edge.sip2.support.Sip2TestCommand.sip2Exchange;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.Matchers.startsWith;

import org.folio.edge.sip2.api.support.AbstractErrorDetectionEnabledTest;
import org.folio.edge.sip2.support.Sip2Commands;
import org.folio.edge.sip2.support.tags.IntegrationTest;
import org.folio.edge.sip2.support.wiremock.WiremockStubs;
import org.junit.jupiter.api.Test;

@IntegrationTest
class EndSessionIT extends AbstractErrorDetectionEnabledTest {

  @Test
  @WiremockStubs({
      "/wiremock/stubs/mod-settings/200-get-locale.json",
      "/wiremock/stubs/mod-settings/200-get-settings.json",
      "/wiremock/stubs/mod-login/201-post-acs-login.json",
      "/wiremock/stubs/mod-users/200-get-user-by-patron-identifier.json",
      "/wiremock/stubs/mod-users-bl/200-get-user-by-id.json",
  })
  void endSession_positive_allFields() throws Throwable {
    executeInSession(
        successLoginExchange(),
        sip2Exchange(
            Sip2Commands.endSession("Test Institution", PATRON_BARCODE, "test", "test"),
            sip2Result -> {
              assertSuccessfulExchange(sip2Result);
              var message = sip2Result.getResponseMessage();
              assertThat(message, startsWith("36Y"));
              assertThat(message.substring(3, 21), matchesRegex("\\d{8}\\s{4}\\d{6}"));
              assertThat(message, containsString("AOTest Institution|AAtestBarcode|"));
            }
        ));
  }

  @Test
  @WiremockStubs({
      "/wiremock/stubs/mod-settings/200-get-locale.json",
      "/wiremock/stubs/mod-settings/200-get-settings(pin-validation).json",
      "/wiremock/stubs/mod-login/201-post-acs-login.json",
      "/wiremock/stubs/mod-users/200-get-user-by-patron-identifier.json",
      "/wiremock/stubs/mod-users/200-post-patron-pin.json",
      "/wiremock/stubs/mod-users-bl/200-get-user-by-id.json",
  })
  void endSession_positive_pinVerificationEnabledForPatron() throws Throwable {
    executeInSession(
        successLoginExchange(),
        sip2Exchange(
            Sip2Commands.endSession(null, PATRON_BARCODE, null, "132456"),
            sip2Result -> {
              assertSuccessfulExchange(sip2Result);
              var message = sip2Result.getResponseMessage();
              assertThat(message, startsWith("36Y"));
              assertThat(message.substring(3, 21), matchesRegex("\\d{8}\\s{4}\\d{6}"));
              assertThat(message, containsString("AO|AAtestBarcode|"));
            }
        ));
  }

  @Test
  @WiremockStubs({
      "/wiremock/stubs/mod-settings/200-get-locale.json",
      "/wiremock/stubs/mod-settings/200-get-settings(pin-validation).json",
      "/wiremock/stubs/mod-login/201-post-acs-login.json",
      "/wiremock/stubs/mod-users/200-get-user-by-patron-identifier.json",
      "/wiremock/stubs/mod-users/422-post-patron-pin.json",
      "/wiremock/stubs/mod-users-bl/200-get-user-by-id.json",
  })
  void endSession_negative_pinVerificationEnabledForPatronWithInvalidPin() throws Throwable {
    executeInSession(
        successLoginExchange(),
        sip2Exchange(
            Sip2Commands.endSession(null, PATRON_BARCODE, null, "132456"),
            sip2Result -> {
              assertSuccessfulExchange(sip2Result);
              var message = sip2Result.getResponseMessage();
              assertThat(message, startsWith("36N"));
              assertThat(message.substring(3, 21), matchesRegex("\\d{8}\\s{4}\\d{6}"));
              assertThat(message, containsString("AO|AAtestBarcode|"));
            }
        ));
  }

  @Test
  @WiremockStubs({
      "/wiremock/stubs/mod-settings/200-get-locale.json",
      "/wiremock/stubs/mod-settings/200-get-settings.json",
      "/wiremock/stubs/mod-login/201-post-acs-login.json",
      "/wiremock/stubs/mod-users/200-get-user-by-patron-identifier(not found).json",
  })
  void endSession_negative_userNotFound() throws Throwable {
    executeInSession(
        successLoginExchange(),
        sip2Exchange(
            Sip2Commands.endSession(null, PATRON_BARCODE, null, "132456"),
            sip2Result -> {
              assertSuccessfulExchange(sip2Result);
              var message = sip2Result.getResponseMessage();
              assertThat(message, startsWith("36Y"));
              assertThat(message.substring(3, 21), matchesRegex("\\d{8}\\s{4}\\d{6}"));
              assertThat(message, containsString("AO|AAtestBarcode|"));
            }
        ));
  }

  @Test
  @WiremockStubs({
      "/wiremock/stubs/mod-settings/200-get-locale.json",
      "/wiremock/stubs/mod-settings/200-get-settings.json",
      "/wiremock/stubs/mod-login/201-post-acs-login.json",
      "/wiremock/stubs/mod-users/200-get-user-by-patron-identifier.json",
      "/wiremock/stubs/mod-users-bl/200-get-user-by-id.json",
  })
  void endSession_positive_onlyPatronIdentifier() throws Throwable {
    executeInSession(
        successLoginExchange(),
        sip2Exchange(
            Sip2Commands.endSession(PATRON_BARCODE),
            sip2Result -> {
              assertSuccessfulExchange(sip2Result);
              var message = sip2Result.getResponseMessage();
              assertThat(message, startsWith("36Y"));
              assertThat(message.substring(3, 21), matchesRegex("\\d{8}\\s{4}\\d{6}"));
              assertThat(message, containsString("AO|AAtestBarcode|"));
            }
        ));
  }
}
