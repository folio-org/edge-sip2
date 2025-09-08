package org.folio.edge.sip2.api;

import static java.lang.System.getProperty;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static org.folio.edge.sip2.api.support.TestUtils.executeInSession;
import static org.folio.edge.sip2.api.support.TestUtils.getRandomPort;
import static org.folio.edge.sip2.support.Sip2TestCommand.sip2Exchange;
import static org.folio.edge.sip2.support.wiremock.WiremockContainerExtension.WM_URL_PROPERTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import org.folio.edge.sip2.support.Sip2Commands;
import org.folio.edge.sip2.support.Sip2SessionConfiguration;
import org.folio.edge.sip2.support.Sip2TestCommand;
import org.folio.edge.sip2.support.tags.IntegrationTest;
import org.folio.edge.sip2.support.vertx.VertxModule;
import org.folio.edge.sip2.support.wiremock.EnableWiremock;
import org.folio.edge.sip2.support.wiremock.WiremockStubs;
import org.junit.function.ThrowingRunnable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@EnableWiremock
@IntegrationTest
@ExtendWith({ VertxExtension.class, MockitoExtension.class })
class PortConfigIT {

  private static final String TEST_TENANT_ID = "testtenant";
  private static final String OTHER_TENANT_ID = "other_tenant";
  private static final Credentials TEST_TENANT_CREDENTIALS =
      new Credentials("test_username", "test_password", "1a62fa88-e887-4454-a345-24b4a01095fa");
  private static final Credentials OTHER_TENANT_CREDENTIALS =
      new Credentials("other_username", "other_password", "986e5b2e-98c8-4da0-9c0d-b1608939e945");

  @Test
  @WiremockStubs({
      "wiremock/stubs/mod-login/201-post-acs-login.json",
      "wiremock/stubs/mod-login/401-post-invalid-login.json",
      "wiremock/stubs/mod-configuration/200-get-configuration.json",
  })
  @DisplayName("Should handle multiple port configurations correctly")
  void singlePortConfigTest_positive(Vertx vertx, VertxTestContext testContext) {
    var port = getRandomPort();
    var sip2Configuration = sip2Configuration(port, tenantStore(TEST_TENANT_ID, port));

    withDeployedModule(vertx, testContext, sip2Configuration, () -> {
      executeInSession(getSessionConfig(port),
          successLogin(TEST_TENANT_CREDENTIALS),
          successStatus("BXYYYNYNYYYYYNNNYY"));

      executeInSession(getSessionConfig(port),
          failedLogin(OTHER_TENANT_CREDENTIALS));
    });
  }

  @Test
  @WiremockStubs({
      "wiremock/stubs/mod-login/201-post-acs-login.json",
      "wiremock/stubs/mod-login/401-post-invalid-login.json",
      "wiremock/stubs/mod-login/201-post-acs-login-otherTenant.json",
      "wiremock/stubs/mod-configuration/200-get-configuration.json",
      "wiremock/stubs/mod-configuration/200-get-configuration-otherTenant.json",
  })
  @DisplayName("Should handle multiple port configurations correctly")
  void multiplePortConfigTest_positive(Vertx vertx, VertxTestContext testContext) {
    var testTenantPort = getRandomPort();
    var otherTenantPort = getRandomPort();

    var sip2Configuration = sip2Configuration(
        List.of(testTenantPort, otherTenantPort),
        tenantStore(TEST_TENANT_ID, testTenantPort),
        tenantStore(OTHER_TENANT_ID, otherTenantPort));

    withDeployedModule(vertx, testContext, sip2Configuration, () -> {
      executeInSession(getSessionConfig(testTenantPort),
          successLogin(TEST_TENANT_CREDENTIALS),
          successStatus("BXYYYNYNYYYYYNNNYY"));

      executeInSession(getSessionConfig(testTenantPort),
          failedLogin(OTHER_TENANT_CREDENTIALS));

      executeInSession(getSessionConfig(otherTenantPort),
          successLogin(OTHER_TENANT_CREDENTIALS),
          successStatus("BXNNNNYNYNNNNNNNNN"));

      executeInSession(getSessionConfig(otherTenantPort),
          failedLogin(TEST_TENANT_CREDENTIALS));
    });
  }

  @Test
  @DisplayName("Should fail with invalid port configurations")
  void deployVerticle_negative_portIsInvalid(Vertx vertx, VertxTestContext testContext) {
    var port = "invalidPort";
    var sip2Configuration = sip2Configuration(port, tenantStore(TEST_TENANT_ID, null));
    new VertxModule(vertx, sip2Configuration)
        .deployModule()
        .onComplete(testContext.failing(error -> {
          assertThat(error, instanceOf(IllegalArgumentException.class));
          assertThat(error.getMessage(),
              is("Port configuration must be an integer or a list of integers"));
          testContext.completeNow();
        }));
  }

  @Test
  @DisplayName("Should handle null port configuration gracefully")
  void deployVerticle_negative_portIsNull(Vertx vertx, VertxTestContext testContext) {
    var sip2Configuration = sip2Configuration(null, tenantStore(TEST_TENANT_ID, null));
    new VertxModule(vertx, sip2Configuration)
        .deployModule()
        .onComplete(testContext.failing(error -> {
          assertThat(error, instanceOf(IllegalArgumentException.class));
          assertThat(error.getMessage(), is("Port configuration cannot be null"));
          testContext.completeNow();
        }));
  }

  @Test
  @DisplayName("Should handle null port in json object configuration gracefully")
  void deployVerticle_negative_portListWithNullValue(Vertx vertx, VertxTestContext testContext) {
    var portValue = singletonList((Integer) null);
    var sip2Configuration = sip2Configuration(portValue, tenantStore(TEST_TENANT_ID, null));

    new VertxModule(vertx, sip2Configuration)
        .deployModule()
        .onComplete(testContext.failing(error -> {
          assertThat(error, instanceOf(IllegalArgumentException.class));
          assertThat(error.getMessage(), is("Port value null is not an integer"));
          testContext.completeNow();
        }));
  }

  private static Sip2TestCommand successStatus(String expectedEnabledFuncString) {
    return sip2Exchange(
        Sip2Commands.status(),
        sip2Result -> {
          assertThat(sip2Result.getResponseMessage(), startsWith("98"));
          assertThat(sip2Result.getResponseMessage(), containsString(expectedEnabledFuncString));
        }
    );
  }

  private static Sip2TestCommand successLogin(Credentials creds) {
    return sip2Exchange(
        Sip2Commands.login(creds.username(), creds.password(), creds.locationCode()),
        sip2Result -> {
          assertTrue(sip2Result.isSuccessfulExchange());
          assertTrue(sip2Result.isChecksumValid());
          assertThat(sip2Result.getResponseMessage(), startsWith("941"));
        });
  }

  private static Sip2TestCommand failedLogin(Credentials creds) {
    return sip2Exchange(
        Sip2Commands.login(creds.username(), creds.password(), creds.locationCode()),
        sip2Result -> {
          assertTrue(sip2Result.isSuccessfulExchange());
          assertTrue(sip2Result.isChecksumValid());
          assertThat(sip2Result.getResponseMessage(), startsWith("940"));
        });
  }

  private static void withDeployedModule(Vertx vertx, VertxTestContext testContext,
      JsonObject sip2Configuration, ThrowingRunnable tenantHandler) {
    new VertxModule(vertx, sip2Configuration)
        .deployModule()
        .onComplete(testContext.succeeding(deploymentId -> {
          try {
            tenantHandler.run();
          } catch (Throwable e) {
            testContext.failNow(e);
          }
        }))
        .onSuccess(vertx::undeploy)
        .onComplete(testContext.succeedingThenComplete());
  }

  private static JsonObject sip2Configuration(Object portConfig, JsonObject... scTenants) {
    var scTenantsConfigObject = new JsonObject()
        .put("scTenants", new JsonArray(List.of(scTenants)));

    var storesConfiguration = new JsonObject()
        .put("type", "json")
        .put("format", "json")
        .put("config", scTenantsConfigObject);

    var tenantConfigRetrieverOptions = new JsonObject()
        .put("scanPeriod", 1000)
        .put("stores", new JsonArray(List.of(storesConfiguration)));

    return new JsonObject()
        .put("okapiUrl", requireNonNull(getProperty(WM_URL_PROPERTY)))
        .put("port", portConfig)
        .put("tenantConfigRetrieverOptions", tenantConfigRetrieverOptions);
  }

  private static JsonObject tenantStore(String tenantId, Integer port) {
    return new JsonObject()
        .put("port", port)
        .put("scSubnet", "0.0.0.0/0")
        .put("tenant", tenantId)
        .put("errorDetectionEnabled", true)
        .put("messageDelimiter", "\r")
        .put("fieldDelimiter", "|")
        .put("charset", "ISO-8859-1");
  }

  protected static Sip2SessionConfiguration getSessionConfig(int port) {
    return Sip2SessionConfiguration.builder()
        .port(port)
        .hostname("localhost")
        .useSsl(false)
        .socketTimeout(Duration.ofSeconds(5))
        .charset(StandardCharsets.ISO_8859_1)
        .errorProtectionEnabled(true)
        .build();
  }

  record Credentials(String username, String password, String locationCode) {}
}
