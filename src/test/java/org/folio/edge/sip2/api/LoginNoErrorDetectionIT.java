package org.folio.edge.sip2.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import org.folio.edge.sip2.api.support.AbstractErrorDetectionDisabledTest;
import org.folio.edge.sip2.support.Sip2Commands;
import org.folio.edge.sip2.support.Sip2Session;
import org.folio.edge.sip2.support.Sip2SessionConfiguration;
import org.folio.edge.sip2.support.tags.IntegrationTest;
import org.folio.edge.sip2.support.wiremock.WiremockStubs;
import org.junit.jupiter.api.Test;

@IntegrationTest
class LoginNoErrorDetectionIT extends AbstractErrorDetectionDisabledTest {

  @Test
  @WiremockStubs({
      "/wiremock/stubs/mod-settings/200-get-locale.json",
      "/wiremock/stubs/mod-settings/200-get-settings.json",
      "/wiremock/stubs/mod-login/201-post-acs-login.json",
  })
  void login_positive() {
    try (var session = new Sip2Session(getSip2SessionConfig())) {
      var command = Sip2Commands.login("test_username", "test_password", SERVICE_POINT_ID);
      var result = session.executeCommand(command);
      assertTrue(result.isSuccessfulExchange());
      assertThat(result.getResponseMessage(), startsWith("941"));
    }
  }

  @Test
  @WiremockStubs("/wiremock/stubs/mod-login/401-post-invalid-login.json")
  void login_negative_invalidCredentials() {
    try (var session = new Sip2Session(getSip2SessionConfig())) {
      var command = Sip2Commands.login("test_username", "test_password", SERVICE_POINT_ID);
      var result = session.executeCommand(command);
      assertTrue(result.isSuccessfulExchange());
      assertThat(result.getResponseMessage(), startsWith("940"));
    }
  }

  @Override
  protected Sip2SessionConfiguration getSip2SessionConfig() {
    return Sip2SessionConfiguration.builder()
        .port(mainVerticlePort)
        .hostname("localhost")
        .useSsl(false)
        .charset(StandardCharsets.ISO_8859_1)
        .errorProtectionEnabled(false)
        .build();
  }
}
