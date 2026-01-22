package org.folio.edge.sip2.api;

import static org.folio.edge.sip2.support.Sip2TestCommand.sip2Exchange;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.Matchers.startsWith;

import org.folio.edge.sip2.api.support.AbstractErrorDetectionEnabledTest;
import org.folio.edge.sip2.support.Sip2Commands;
import org.folio.edge.sip2.support.tags.IntegrationTest;
import org.folio.edge.sip2.support.wiremock.WiremockStubs;
import org.junit.jupiter.api.Test;

@IntegrationTest
@WiremockStubs({
    "/wiremock/stubs/mod-settings/200-get-locale.json",
    "/wiremock/stubs/mod-settings/200-get-settings.json",
    "/wiremock/stubs/mod-login/201-post-acs-login.json",
})
class CheckInIT extends AbstractErrorDetectionEnabledTest {

  private static final String ITEM_BARCODE = "test123456789";

  @Test
  @WiremockStubs({
      "/wiremock/stubs/mod-inventory/200-items-by-barcode.json",
      "/wiremock/stubs/mod-circulation/201-check-in-by-barcode.json",
      "/wiremock/stubs/mod-circulation/200-get-open-circulation-requests-by-item-id.json",
  })
  void performCheckin_positive_holdSummaryType() throws Throwable {
    executeInSession(
        successLoginExchange(),
        sip2Exchange(
            Sip2Commands.checkIn(ITEM_BARCODE),
            sip2Result -> {
              assertSuccessfulExchange(sip2Result);

              var respMsg = sip2Result.getResponseMessage();
              assertThat(respMsg, startsWith("10"));
              validateCheckinCommandResponse(respMsg);
            }
        ));
  }

  private static void validateCheckinCommandResponse(String message) {
    assertThat(message.charAt(2), is('1')); // completed successfully
    assertThat(message.charAt(3), is('Y')); // item is magnetic
    assertThat(message.charAt(4), is('U')); // is library only use
    assertThat(message.charAt(5), is('Y')); // alert supported
    assertThat(message.substring(6, 24), matchesRegex("\\d{8}\\s{4}\\d{6}")); // Transaction date

    // empty The institution ID
    assertThat(message.substring(24, 26), is("AO"));
    assertThat(message.charAt(26), is('|'));

    // item identifier (AB)
    assertThat(message.substring(27, 29), is("AB"));
    assertThat(message.substring(29, 42), is(ITEM_BARCODE));

    // The name of the item’s permanent location (AQ)
    assertThat(message.substring(43, 45), is("AQ"));
    assertThat(message.substring(45, 57), is("Main Library"));

    // The item’s title (AJ)
    assertThat(message.substring(58, 60), is("AJ"));
    assertThat(message.substring(60, 81), is("A semantic web primer"));

    // Media Type - physical material type (CK)
    assertThat(message.substring(82, 84), is("CK"));
    assertThat(message.substring(84, 87), is("001"));

    // Item Call Number (CS)
    assertThat(message.substring(88, 90), is("CS"));
    assertThat(message.substring(90, 122), is("TK5105.88815 . A58 2004 FT MEADE"));

    // Alert Type (CV)
    assertThat(message.substring(123, 125), is("CV"));
    assertThat(message.substring(125, 127), is("04"));

    // Pickup Service Point Name (CT)
    assertThat(message.substring(128, 130), is("CT"));
    assertThat(message.substring(130, 141), is("Circ Desk 1"));
  }
}
