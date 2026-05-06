package org.folio.edge.sip2.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.edge.sip2.support.Sip2TestCommand.sip2Exchange;

import java.util.EnumSet;
import java.util.List;
import org.folio.edge.sip2.api.support.AbstractErrorDetectionEnabledTest;
import org.folio.edge.sip2.domain.messages.enumerations.PatronStatus;
import org.folio.edge.sip2.domain.messages.responses.PatronStatusResponse;
import org.folio.edge.sip2.support.Sip2Commands;
import org.folio.edge.sip2.support.response.PatronStatusResponseParser;
import org.folio.edge.sip2.support.tags.IntegrationTest;
import org.folio.edge.sip2.support.wiremock.WiremockStubs;
import org.junit.jupiter.api.Test;

@IntegrationTest
class PatronStatusIT extends AbstractErrorDetectionEnabledTest {

  private static final String TIMEZONE = "Europe/Paris";

  @Test
  @WiremockStubs({
      "/wiremock/stubs/mod-settings/200-get-locale.json",
      "/wiremock/stubs/mod-settings/200-get-settings.json",
      "/wiremock/stubs/mod-login/201-post-acs-login.json",
      "/wiremock/stubs/mod-users/200-get-user-by-patron-identifier.json",
      "/wiremock/stubs/mod-users-bl/200-get-user-by-id.json",
      "/wiremock/stubs/mod-fee-fines/200-get-accounts-open-status.json",
      "/wiremock/stubs/mod-fee-fines/200-get-manualblocks.json",
  })
  void getPatronStatus_noManualBlocks_patronStatusFlagsAreEmpty() throws Throwable {
    executeInSession(
        successLoginExchange(),
        sip2Exchange(
            Sip2Commands.patronStatus(PATRON_BARCODE),
            sip2Result -> {
              assertSuccessfulExchange(sip2Result);

              var respMsg = sip2Result.getResponseMessage();
              assertThat(respMsg).startsWith("24");

              var response = parseResponse(respMsg);
              assertThat(response.getValidPatron()).isTrue();
              assertThat(response.getPatronStatus()).isEmpty();
              assertThat(response.getScreenMessage()).isNull();
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
      "/wiremock/stubs/mod-fee-fines/200-get-accounts-open-status.json",
      "/wiremock/stubs/mod-fee-fines/200-get-manualblocks-with-borrowing-block.json",
  })
  void getPatronStatus_withManualBorrowingBlock_allStatusFlagsSetAndMessageReturned()
      throws Throwable {
    executeInSession(
        successLoginExchange(),
        sip2Exchange(
            Sip2Commands.patronStatus(PATRON_BARCODE),
            sip2Result -> {
              assertSuccessfulExchange(sip2Result);

              var respMsg = sip2Result.getResponseMessage();
              assertThat(respMsg).startsWith("24");

              var response = parseResponse(respMsg);
              assertThat(response.getValidPatron()).isTrue();
              assertThat(response.getPatronStatus())
                  .isEqualTo(EnumSet.allOf(PatronStatus.class));
              assertThat(response.getScreenMessage())
                  .isEqualTo(List.of(
                      "Your account is blocked. Please contact the library."));
            }
        ));
  }

  private PatronStatusResponse parseResponse(String respMsg) {
    return new PatronStatusResponseParser(delimiter, TIMEZONE).parse(respMsg);
  }
}
