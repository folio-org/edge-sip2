package org.folio.edge.sip2.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.edge.sip2.support.Sip2TestCommand.sip2Exchange;

import org.folio.edge.sip2.api.support.AbstractErrorDetectionEnabledTest;
import org.folio.edge.sip2.support.model.RenewCommand;
import org.folio.edge.sip2.support.tags.IntegrationTest;
import org.folio.edge.sip2.support.wiremock.WiremockStubs;
import org.junit.jupiter.api.Test;

@IntegrationTest
@WiremockStubs({
    "/wiremock/stubs/mod-settings/200-get-locale.json",
    "/wiremock/stubs/mod-settings/200-get-settings.json",
    "/wiremock/stubs/mod-login/201-post-acs-login.json",
})
class RenewIT extends AbstractErrorDetectionEnabledTest {

  private static final String RENEWABLE_ITEM_BARCODE = "renewableItem123";
  private static final String NON_RENEWABLE_ITEM_BARCODE = "nonRenewableItem456";

  @Test
  @WiremockStubs({
      "/wiremock/stubs/mod-users/200-get-user-by-patron-identifier.json",
      "/wiremock/stubs/mod-users-bl/200-get-user-by-id.json",
      "/wiremock/stubs/mod-circulation/201-renew-by-barcode-success.json"
  })
  void renewItem_positive_renewalAllowed() throws Throwable {
    executeInSession(
        successLoginExchange(),
        sip2Exchange(
            RenewCommand.builder()
                .institutionId(TENANT_ID)
                .patronIdentifier(PATRON_BARCODE)
                .itemIdentifier(RENEWABLE_ITEM_BARCODE)
                .build(),
            sip2Result -> {
              assertSuccessfulExchange(sip2Result);

              var respMsg = sip2Result.getResponseMessage();

              assertThat(respMsg).startsWith("301Y");
              assertThat(respMsg)
                  .contains("AJ" + "Test Book Title")
                  .contains("AB" + RENEWABLE_ITEM_BARCODE);
            }
        ));
  }

  @Test
  @WiremockStubs({
      "/wiremock/stubs/mod-users/200-get-user-by-patron-identifier.json",
      "/wiremock/stubs/mod-users-bl/200-get-user-by-id.json",
      "/wiremock/stubs/mod-circulation/201-renew-by-barcode-not-renewable.json"
  })
  void renewItem_negative_renewalNotAllowed() throws Throwable {
    executeInSession(
        successLoginExchange(),
        sip2Exchange(
            RenewCommand.builder()
                .institutionId(TENANT_ID)
                .patronIdentifier(PATRON_BARCODE)
                .itemIdentifier(NON_RENEWABLE_ITEM_BARCODE)
                .build(),
            sip2Result -> {
              assertSuccessfulExchange(sip2Result);

              var respMsg = sip2Result.getResponseMessage();

              assertThat(respMsg).startsWith("300N");
              assertThat(respMsg).contains("AB" + NON_RENEWABLE_ITEM_BARCODE);
            }
        ));
  }
}
