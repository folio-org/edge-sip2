package org.folio.edge.sip2.api;

import static org.folio.edge.sip2.support.Sip2TestCommand.sip2Exchange;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.folio.edge.sip2.api.support.AbstractErrorDetectionEnabledTest;
import org.folio.edge.sip2.support.Sip2TestConfig;
import org.folio.edge.sip2.support.model.RawCommand;
import org.folio.edge.sip2.support.tags.IntegrationTest;
import org.folio.edge.sip2.support.wiremock.WiremockStubs;
import org.junit.jupiter.api.Test;

@IntegrationTest
@Sip2TestConfig("sip2-checksum-verification-enabled.conf")
@WiremockStubs({
    "wiremock/stubs/mod-login/201-post-acs-login.json",
    "wiremock/stubs/mod-configuration/200-get-configuration.json"
})
class ScOtherCommandsIT extends AbstractErrorDetectionEnabledTest {

  @Test
  void getAcsStatus_negative_invalidStatusRequestAndGetErrorMessage() throws Throwable {
    executeInSession(
        successLoginExchange(),
        sip2Exchange(RawCommand.of("unknown command", true),
            sip2Result -> {
              assertTrue(sip2Result.isSuccessfulExchange());
              assertTrue(sip2Result.isChecksumValid());
              assertThat(sip2Result.getResponseMessage(), containsString("96AZFEF6"));
            })
    );
  }
}
