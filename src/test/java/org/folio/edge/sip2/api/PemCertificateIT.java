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
import org.folio.edge.sip2.support.tags.EnableTls;
import org.folio.edge.sip2.support.tags.IntegrationTest;
import org.folio.edge.sip2.support.wiremock.WiremockStubs;
import org.junit.jupiter.api.Test;

@EnableTls
@IntegrationTest
@Sip2TestConfig("sip2-pem-certificate.conf")
class PemCertificateIT extends BaseIntegrationTest {

  @Override
  protected Sip2SessionConfiguration getSip2SessionConfig() {
    return getSip2SessionConfiguration(true);
  }

  @Test
  @WiremockStubs({
    "/wiremock/stubs/mod-settings/200-get-locale.json",
    "/wiremock/stubs/mod-settings/200-get-settings.json",
    "/wiremock/stubs/mod-login/201-post-acs-login.json",
    "/wiremock/stubs/mod-users/200-get-user-by-patron-identifier.json",
    "/wiremock/stubs/mod-users-bl/200-get-user-by-id.json",
  })
  void pemCertificateCommands_positive() throws Throwable {
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

  private static Sip2SessionConfiguration getSip2SessionConfiguration(boolean useSsl) {
    return Sip2SessionConfiguration.builder()
        .port(mainVerticlePort)
        .hostname("localhost")
        .useSsl(useSsl)
        .socketTimeout(Duration.ofSeconds(5))
        .charset(StandardCharsets.ISO_8859_1)
        .errorProtectionEnabled(true)
        .build();
  }
}
