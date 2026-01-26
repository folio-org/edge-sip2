package org.folio.edge.sip2.api;

import static org.folio.edge.sip2.support.Sip2TestCommand.sip2Exchange;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.folio.edge.sip2.api.support.BaseIntegrationTest;
import org.folio.edge.sip2.support.Sip2Commands;
import org.folio.edge.sip2.support.Sip2SessionConfiguration;
import org.folio.edge.sip2.support.Sip2TestConfig;
import org.folio.edge.sip2.support.tags.IntegrationTest;
import org.folio.edge.sip2.support.wiremock.EnableWiremock;
import org.folio.edge.sip2.support.wiremock.WiremockStubs;
import org.junit.jupiter.api.Test;

@EnableWiremock(https = true)
@IntegrationTest
@Sip2TestConfig("sip2-https-wiremock-local.conf")
class TlsTruststoreIT extends BaseIntegrationTest {

  @Override
  protected Sip2SessionConfiguration getSip2SessionConfig() {
    return Sip2SessionConfiguration.builder()
        .port(mainVerticlePort)
        .hostname("localhost")
        .useSsl(true)
        .socketTimeout(Duration.ofSeconds(5))
        .charset(StandardCharsets.ISO_8859_1)
        .errorProtectionEnabled(true)
        .build();
  }

  @Test
  @WiremockStubs({
      "wiremock/stubs/mod-login/201-post-acs-login.json",
      "wiremock/stubs/mod-configuration/200-get-configuration.json",
      "wiremock/stubs/mod-users/200-get-user-by-patron-identifier.json",
      "wiremock/stubs/mod-users-bl/200-get-user-by-id.json",
  })
  void backendHttps_withWebClientTlsOptions_success() throws Throwable {
    executeInSession(
        successLoginExchange(),
        sip2Exchange(
            Sip2Commands.status(),
            sip2Result -> {
              assertSuccessfulExchange(sip2Result);
              var respMsg = sip2Result.getResponseMessage();
              assertThat(respMsg, startsWith("98YYYYNN"));
              assertThat(respMsg, containsString("BXYYYNYNYYYYYNNNYY"));
            }),
        sip2Exchange(
            Sip2Commands.endSession(PATRON_BARCODE),
            sip2Result -> {
              assertSuccessfulExchange(sip2Result);
              var message = sip2Result.getResponseMessage();
              assertThat(message, startsWith("36Y"));
            }
        ));
  }
}
