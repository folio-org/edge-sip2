package org.folio.edge.sip2.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.edge.sip2.support.Sip2TestCommand.sip2Exchange;

import java.util.UUID;
import org.folio.edge.sip2.api.support.AbstractErrorDetectionEnabledTest;
import org.folio.edge.sip2.domain.messages.enumerations.CurrencyType;
import org.folio.edge.sip2.domain.messages.enumerations.FeeType;
import org.folio.edge.sip2.domain.messages.enumerations.PaymentType;
import org.folio.edge.sip2.support.model.FeePaidCommand;
import org.folio.edge.sip2.support.model.Sip2Command;
import org.folio.edge.sip2.support.response.FeePaidResponseParser;
import org.folio.edge.sip2.support.tags.IntegrationTest;
import org.folio.edge.sip2.support.wiremock.WiremockStubs;
import org.junit.jupiter.api.Test;

@IntegrationTest
class FeePaidIT extends AbstractErrorDetectionEnabledTest {

  private static final String TIMEZONE = "Europe/Paris";

  @Test
  @WiremockStubs({
      "/wiremock/stubs/mod-settings/200-get-locale.json",
      "/wiremock/stubs/mod-settings/200-get-settings.json",
      "/wiremock/stubs/mod-login/201-post-acs-login.json",
      "/wiremock/stubs/mod-users/200-get-user-by-patron-identifier.json",
      "/wiremock/stubs/mod-users-bl/200-get-user-by-id.json",
      "/wiremock/stubs/mod-fee-fines/200-get-accounts-open-status.json",
      "/wiremock/stubs/mod-fee-fines/201-post(accounts-bulk-pay).json",
  })
  void payFee_positive() throws Throwable {
    executeInSession(
        successLoginExchange(),
        sip2Exchange(
            feePaidCommand(),
            sip2Result -> {
              assertThat(sip2Result).isNotNull();
              assertSuccessfulExchange(sip2Result);

              var respMsg = sip2Result.getResponseMessage();
              var response = new FeePaidResponseParser(delimiter, TIMEZONE).parse(respMsg);
              assertThat(response.getPaymentAccepted()).isTrue();
              assertThat(respMsg).contains("FG7.50");
              assertThat(respMsg).contains("FA17.50");
            }
        ));
  }

  private static Sip2Command feePaidCommand() {
    return FeePaidCommand.builder()
        .paymentType(PaymentType.VISA)
        .feeType(FeeType.HOLD_FEE)
        .currencyType(CurrencyType.USD)
        .feeAmount(7.5f)
        .institutionId(TENANT_ID)
        .patronIdentifier(PATRON_BARCODE)
        .transactionId(UUID.randomUUID().toString())
        .build();
  }
}
