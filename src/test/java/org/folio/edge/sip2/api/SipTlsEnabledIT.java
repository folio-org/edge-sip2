package org.folio.edge.sip2.api;

import static org.folio.edge.sip2.support.Sip2TestCommand.sip2Exchange;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.Matchers.startsWith;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.folio.edge.sip2.api.support.BaseIntegrationTest;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.support.Sip2Commands;
import org.folio.edge.sip2.support.Sip2SessionConfiguration;
import org.folio.edge.sip2.support.Sip2TestConfig;
import org.folio.edge.sip2.support.tags.EnableTls;
import org.folio.edge.sip2.support.tags.IntegrationTest;
import org.folio.edge.sip2.support.wiremock.WiremockStubs;
import org.junit.jupiter.api.Test;

@EnableTls
@IntegrationTest
@Sip2TestConfig("sip2-checksum-verification-enabled.conf")
class SipTlsEnabledIT extends BaseIntegrationTest {

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
  void tlsCommands_positive() throws Throwable {
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
              assertThat(message.substring(3, 21), matchesRegex("\\d{8}\\s{4}\\d{6}"));
              assertThat(message, containsString("AO|AAtestBarcode|"));
            }
        ));
  }

  @Test
  void tlsCommands_negative_connectionRefusedForNonTlsClient() throws Throwable {
    TestUtils.executeInSession(getSip2SessionConfiguration(false),
        sip2Exchange(
            Sip2Commands.login("test_username", "test_password", SERVICE_POINT_ID),
            sip2Result -> {
              assertThat(sip2Result.getErrorMessage(), startsWith("Connection is not available"));
              assertThat(sip2Result.getException(), instanceOf(IllegalStateException.class));
              assertThat(sip2Result.getException().getMessage(),
                  is("Server requires SSL/TLS but client is configured for plain connection"));
            }));
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
