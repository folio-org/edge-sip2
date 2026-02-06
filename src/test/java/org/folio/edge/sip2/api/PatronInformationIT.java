package org.folio.edge.sip2.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.edge.sip2.support.Sip2TestCommand.sip2Exchange;
import static org.folio.edge.sip2.support.model.PatronInformationCommand.PatronInfoSummaryType.HOLD_ITEMS;

import java.util.EnumSet;
import java.util.List;
import org.folio.edge.sip2.api.support.AbstractErrorDetectionEnabledTest;
import org.folio.edge.sip2.domain.messages.enumerations.Language;
import org.folio.edge.sip2.domain.messages.enumerations.PatronStatus;
import org.folio.edge.sip2.domain.messages.responses.PatronInformationResponse;
import org.folio.edge.sip2.parser.LanguageMapper;
import org.folio.edge.sip2.support.Sip2Commands;
import org.folio.edge.sip2.support.model.PatronInformationCommand;
import org.folio.edge.sip2.support.response.PatronInformationResponseParser;
import org.folio.edge.sip2.support.tags.IntegrationTest;
import org.folio.edge.sip2.support.wiremock.WiremockStubs;
import org.junit.jupiter.api.Test;

@IntegrationTest
class PatronInformationIT extends AbstractErrorDetectionEnabledTest {

  @Test
  @WiremockStubs({
      "/wiremock/stubs/mod-settings/200-get-locale.json",
      "/wiremock/stubs/mod-settings/200-get-settings.json",
      "/wiremock/stubs/mod-login/201-post-acs-login.json",
      "/wiremock/stubs/mod-users/200-get-user-by-patron-identifier.json",
      "/wiremock/stubs/mod-users-bl/200-get-user-by-id.json",
      "/wiremock/stubs/mod-circulation/200-get-circulation-open-loans.json",
      "/wiremock/stubs/mod-circulation/200-get-circulation-open-loans-by-due-date.json",
      "/wiremock/stubs/mod-circulation/200-get-circulation-requests-hold.json",
      "/wiremock/stubs/mod-circulation/200-get-circulation-requests-recall.json",
      "/wiremock/stubs/mod-fee-fines/200-get-accounts.json",
      "/wiremock/stubs/mod-fee-fines/200-get-manualblocks.json",
      "/wiremock/stubs/mod-fee-fines/200-get-feefines-empty.json",
  })
  void getPatronInformation_positive_holdSummaryType() throws Throwable {
    executeInSession(
        successLoginExchange(),
        sip2Exchange(
            Sip2Commands.patronInformation(PATRON_BARCODE, HOLD_ITEMS),
            sip2Result -> {
              assertSuccessfulExchange(sip2Result);

              var respMsg = sip2Result.getResponseMessage();
              assertThat(respMsg).startsWith("64");

              var patronInfo = new PatronInformationResponseParser(delimiter, "America/New_York")
                  .parse(respMsg);

              assertThat(patronInfo)
                  .usingRecursiveComparison()
                  .ignoringFields("transactionDate", "institutionId", "currencyType", "feeAmount")
                  .isEqualTo(expectedPatronInfoWithHolds());
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
      "/wiremock/stubs/mod-circulation/200-get-circulation-open-loans.json",
      "/wiremock/stubs/mod-circulation/200-get-circulation-open-loans-by-due-date.json",
      "/wiremock/stubs/mod-circulation/200-get-circulation-requests-hold(no item barcode).json",
      "/wiremock/stubs/mod-circulation/200-get-circulation-requests-recall.json",
      "/wiremock/stubs/mod-fee-fines/200-get-accounts-empty.json",
      "/wiremock/stubs/mod-fee-fines/200-get-manualblocks.json",
      "/wiremock/stubs/mod-fee-fines/500-get-feefines-invalid-query.json",
  })
  void getPatronInformation_positive_holdSummaryTypeAndEmptyRequests() throws Throwable {
    executeInSession(
        sip2Exchange(
            Sip2Commands.login("test_username", "test_password", SERVICE_POINT_ID),
            sip2Result -> {
              assertSuccessfulExchange(sip2Result);
              assertThat(sip2Result.getResponseMessage()).startsWith("941");
            }),

        sip2Exchange(
            Sip2Commands.patronInformation(PATRON_BARCODE, HOLD_ITEMS),
            sip2Result -> {
              assertSuccessfulExchange(sip2Result);
              var respMsg = sip2Result.getResponseMessage();
              assertThat(respMsg).startsWith("64");

              var patronInfo = new PatronInformationResponseParser(delimiter, "America/New_York")
                  .parse(respMsg);

              assertThat(patronInfo.getHoldItems()).isEmpty();
            }
        ));
  }

  @Test
  @WiremockStubs({
      "/wiremock/stubs/mod-settings/200-get-locale.json",
      "/wiremock/stubs/mod-settings/200-get-settings(password-verification-required).json",
      "/wiremock/stubs/mod-login/201-post-acs-login.json",
      "/wiremock/stubs/mod-login/401-post-invalid-login.json",
      "/wiremock/stubs/mod-users/200-get-user-by-patron-identifier.json",
      "/wiremock/stubs/mod-users-bl/200-get-user-by-id.json",
      "/wiremock/stubs/mod-circulation/200-get-circulation-open-loans.json",
      "/wiremock/stubs/mod-circulation/200-get-circulation-open-loans-by-due-date.json",
      "/wiremock/stubs/mod-circulation/200-get-circulation-requests-hold.json",
      "/wiremock/stubs/mod-circulation/200-get-circulation-requests-recall.json",
      "/wiremock/stubs/mod-fee-fines/200-get-accounts.json",
      "/wiremock/stubs/mod-fee-fines/200-get-manualblocks.json",
      "/wiremock/stubs/mod-fee-fines/200-get-feefines-empty.json",
  })
  void getPatronInformationWithPasswordVerificationRequired_invalidPassword() throws Throwable {
    executeInSession(
        successLoginExchange(),
        sip2Exchange(
            PatronInformationCommand.builder()
                .patronIdentifier(PATRON_BARCODE)
                .languageCode(LanguageMapper.ENGLISH)
                .summary(HOLD_ITEMS)
                .patronPassword("test_password")
                .build(),
            sip2Result -> {
              assertSuccessfulExchange(sip2Result);

              var respMsg = sip2Result.getResponseMessage();
              assertThat(respMsg).startsWith("64");

              var patronInfo = new PatronInformationResponseParser(delimiter, "America/New_York")
                  .parse(respMsg);

              assertThat(patronInfo.getValidPatron()).isTrue();
              assertThat(patronInfo.getValidPatronPassword()).isFalse();
            }
        ));
  }

  @Test
  @WiremockStubs({
    "/wiremock/stubs/mod-settings/200-get-locale.json",
    "/wiremock/stubs/mod-settings/200-get-settings(password-verification-required).json",
    "/wiremock/stubs/mod-login/201-post-acs-login.json",
    "/wiremock/stubs/mod-login/201-post-patron-valid-login.json",
    "/wiremock/stubs/mod-users/200-get-user-by-patron-identifier.json",
    "/wiremock/stubs/mod-users-bl/200-get-user-by-id.json",
    "/wiremock/stubs/mod-circulation/200-get-circulation-open-loans.json",
    "/wiremock/stubs/mod-circulation/200-get-circulation-open-loans-by-due-date.json",
    "/wiremock/stubs/mod-circulation/200-get-circulation-requests-hold.json",
    "/wiremock/stubs/mod-circulation/200-get-circulation-requests-recall.json",
    "/wiremock/stubs/mod-fee-fines/200-get-accounts.json",
    "/wiremock/stubs/mod-fee-fines/200-get-manualblocks.json",
    "/wiremock/stubs/mod-fee-fines/200-get-feefines-empty.json",
  })
  void getPatronInformationWithPasswordVerificationRequired_validPassword() throws Throwable {
    executeInSession(
        successLoginExchange(),
        sip2Exchange(
            PatronInformationCommand.builder()
                .patronIdentifier(PATRON_BARCODE)
                .languageCode(LanguageMapper.ENGLISH)
                .summary(HOLD_ITEMS)
                .patronPassword("correct_password")
                .build(),
            sip2Result -> {
              assertSuccessfulExchange(sip2Result);

              var respMsg = sip2Result.getResponseMessage();
              assertThat(respMsg).startsWith("64");

              var patronInfo = new PatronInformationResponseParser(delimiter, "America/New_York")
                  .parse(respMsg);

              assertThat(patronInfo.getValidPatron()).isTrue();
              assertThat(patronInfo.getValidPatronPassword()).isTrue();
            }
        ));
  }

  private static PatronInformationResponse expectedPatronInfoWithHolds() {
    return PatronInformationResponse.builder()
      .patronStatus(EnumSet.noneOf(PatronStatus.class))
      .language(Language.ENGLISH)
      .holdItemsCount(1)
      .overdueItemsCount(1)
      .chargedItemsCount(1)
      .fineItemsCount(1)
      .recallItemsCount(0)
      .patronIdentifier(PATRON_BARCODE)
      .personalName("panic morty")
      .validPatron(true)
      .validPatronPassword(null)
      .holdItems(List.of("testItemBarcode1"))
      .overdueItems(List.of())
      .chargedItems(List.of())
      .fineItems(List.of())
      .recallItems(List.of())
      .unavailableHoldItems(List.of())
      .emailAddress("morty@example.com")
      .borrowerType("Student")
      .borrowerTypeDescription("University Student")
      .screenMessage(List.of())
      .printLine(List.of())
      .build();
  }
}
