package org.folio.edge.sip2.api;

import static org.folio.edge.sip2.support.Sip2TestCommand.sip2Exchange;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

import org.folio.edge.sip2.api.support.AbstractErrorDetectionEnabledTest;
import org.folio.edge.sip2.support.Sip2Commands;
import org.folio.edge.sip2.support.tags.IntegrationTest;
import org.folio.edge.sip2.support.wiremock.WiremockStubs;
import org.junit.jupiter.api.Test;

@IntegrationTest
class ResendIT extends AbstractErrorDetectionEnabledTest {

  @Test
  @WiremockStubs({
      "/wiremock/stubs/mod-settings/200-get-locale.json",
      "/wiremock/stubs/mod-settings/200-get-settings.json",
      "/wiremock/stubs/mod-login/201-post-acs-login.json",
  })
  void resend_positive() throws Throwable {
    executeInSession(
        successLoginExchange(),
        sip2Exchange(
            Sip2Commands.resend(),
            sip2Result -> {
              assertSuccessfulExchange(sip2Result);
              assertThat(sip2Result.getResponseMessage(), startsWith("941"));
            }));
  }

  @Test
  void resend_negative_emptyPreviousMessage() throws Throwable {
    executeInSession(
        sip2Exchange(
            Sip2Commands.resend(),
            sip2Result -> {
              assertThat(sip2Result.isChecksumValid(), is(false));
              var respMessage = sip2Result.getResponseMessage();
              assertThat(respMessage, is("PreviousMessage is NULL"));
            }
        ));
  }
}
