package org.folio.edge.sip2.api;

import static org.folio.edge.sip2.support.Sip2TestCommand.sip2Exchange;
import static org.folio.edge.sip2.support.model.PatronInformationCommand.PatronInfoSummaryType.HOLD_ITEMS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

import org.folio.edge.sip2.api.support.AbstractErrorDetectionEnabledTest;
import org.folio.edge.sip2.support.Sip2Commands;
import org.folio.edge.sip2.support.tags.IntegrationTest;
import org.folio.edge.sip2.support.wiremock.WiremockStubs;
import org.junit.jupiter.api.Test;

@IntegrationTest
@WiremockStubs({
    "wiremock/stubs/mod-login/201-post-acs-login.json",
    "wiremock/stubs/mod-configuration/200-get-configuration.json"
})
class PatronInformationIT extends AbstractErrorDetectionEnabledTest {

  @Test
  @WiremockStubs({
      "wiremock/stubs/mod-login/401-post-invalid-login.json",
      "wiremock/stubs/mod-users/200-get-user-by-patron-identifier.json",
      "wiremock/stubs/mod-users-bl/200-get-user-by-id.json",
      "wiremock/stubs/mod-circulation/200-get-circulation-open-loans.json",
      "wiremock/stubs/mod-circulation/200-get-circulation-open-loans-by-due-date.json",
      "wiremock/stubs/mod-circulation/200-get-circulation-requests-hold.json",
      "wiremock/stubs/mod-circulation/200-get-circulation-requests-recall.json",
      "wiremock/stubs/mod-fee-fines/200-get-accounts.json",
      "wiremock/stubs/mod-fee-fines/200-get-manualblocks.json",
      "wiremock/stubs/mod-fee-fines/200-get-feefines-empty.json",
  })
  void getPatronInformation_positive_holdSummaryType() throws Throwable {
    executeInSession(
        successLoginExchange(),
        sip2Exchange(
            Sip2Commands.patronInformation(PATRON_BARCODE, HOLD_ITEMS),
            sip2Result -> {
              assertSuccessfulExchange(sip2Result);

              var respMsg = sip2Result.getResponseMessage();
              assertThat(respMsg, startsWith("64"));
              validatePatronInformationResponse(respMsg);
            }
        ));
  }

  private static void validatePatronInformationResponse(String message) {
    assertThat(message.charAt(2), is(' ')); // CHARGE_PRIVILEGES_DENIED
    assertThat(message.charAt(3), is(' ')); // RENEWAL_PRIVILEGES_DENIED
    assertThat(message.charAt(4), is(' ')); // RECALL_PRIVILEGES_DENIED
    assertThat(message.charAt(5), is(' ')); // HOLD_PRIVILEGES_DENIED
    assertThat(message.charAt(6), is(' ')); // CARD_REPORTED_LOST
    assertThat(message.charAt(7), is(' ')); // TOO_MANY_ITEMS_CHARGED
    assertThat(message.charAt(8), is(' ')); // TOO_MANY_ITEMS_OVERDUE
    assertThat(message.charAt(9), is(' ')); // TOO_MANY_RENEWALS
    assertThat(message.charAt(10), is(' ')); // TOO_MANY_CLAIMS_OF_ITEMS_RETURNED
    assertThat(message.charAt(11), is(' ')); // TOO_MANY_ITEMS_LOST
    assertThat(message.charAt(12), is(' ')); // EXCESSIVE_OUTSTANDING_FINES
    assertThat(message.charAt(13), is(' ')); // EXCESSIVE_OUTSTANDING_FEES
    assertThat(message.charAt(14), is(' ')); // RECALL_OVERDUE
    assertThat(message.charAt(15), is(' ')); // TOO_MANY_ITEMS_BILLED
    assertThat(message.substring(16, 19), is("001")); // Language code

    // system date
    assertThat(message.substring(19, 37), matchesRegex("\\d{8}\\s{4}\\d{6}"));
    assertThat(message.substring(37, 41), is("0001")); // number of hold items
    assertThat(message.substring(41, 45), is("0001")); // number of overdue items
    assertThat(message.substring(45, 49), is("0001")); // number of charged items
    assertThat(message.substring(49, 53), is("0001")); // number of fine items
    assertThat(message.substring(53, 57), is("0000")); // number of recall items
    assertThat(message.substring(57, 61), is("    ")); // number of unavailable holds

    // institution id (AO)
    assertThat(message.substring(61, 63), is("AO"));

    // patron identifier (AA)
    assertThat(message.substring(64, 66), is("AA"));
    assertThat(message.substring(66, 77), is(PATRON_BARCODE));

    // personal name (AE)
    assertThat(message.substring(78, 80), is("AE"));
    assertThat(message.substring(80, 91), is("panic morty"));

    // Item Barcode (AS)
    assertThat(message.substring(109, 111), is("AS"));
    assertThat(message.substring(111, 127), is("testItemBarcode1"));

    // Patron’s email address (BE)
    assertThat(message.substring(128, 130), is("BE"));
    assertThat(message.substring(130, 147), is("morty@example.com"));

    // Borrower Type (FU)
    assertThat(message.substring(148, 150), is("FU"));
    assertThat(message.substring(150, 157), is("Student"));

    // Borrower Type Description (FV)
    assertThat(message.substring(158, 160), is("FV"));
    assertThat(message.substring(160, 178), is("University Student"));
  }
}
