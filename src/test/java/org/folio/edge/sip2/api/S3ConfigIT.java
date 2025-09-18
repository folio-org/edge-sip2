package org.folio.edge.sip2.api;

import static java.lang.System.getProperty;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static org.folio.edge.sip2.api.support.TestUtils.executeInSession;
import static org.folio.edge.sip2.api.support.TestUtils.getRandomPort;
import static org.folio.edge.sip2.api.support.TestUtils.withDeployedModule;
import static org.folio.edge.sip2.support.Sip2TestCommand.sip2Exchange;
import static org.folio.edge.sip2.support.minio.MinioContainerExtension.TEST_AWS_REGION;
import static org.folio.edge.sip2.support.minio.MinioContainerExtension.TEST_BUCKET_NAME;
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
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import org.folio.edge.sip2.support.Sip2Commands;
import org.folio.edge.sip2.support.Sip2SessionConfiguration;
import org.folio.edge.sip2.support.Sip2TestCommand;
import org.folio.edge.sip2.support.minio.EnableMinio;
import org.folio.edge.sip2.support.minio.MinioContainerExtension;
import org.folio.edge.sip2.support.tags.IntegrationTest;
import org.folio.edge.sip2.support.vertx.VertxModule;
import org.folio.edge.sip2.support.wiremock.EnableWiremock;
import org.folio.edge.sip2.support.wiremock.WiremockStubs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@EnableMinio
@EnableWiremock
@IntegrationTest
@ExtendWith({ VertxExtension.class, MockitoExtension.class })
class S3ConfigIT {

  private static final String TENANT_ID = "testtenant";

  @AfterEach
  void tearDown() {
    var s3Client = MinioContainerExtension.getS3Client();
    var existingObjects = s3Client.list("sip2/");
    existingObjects.forEach(s3Client::remove);
  }

  @Test
  @WiremockStubs({
      "wiremock/stubs/mod-login/201-post-acs-login.json",
      "wiremock/stubs/mod-login/401-post-invalid-login.json",
      "wiremock/stubs/mod-configuration/200-get-configuration.json",
  })
  void deployVerticle_positive_configurationFound(Vertx vertx, VertxTestContext testContext) {
    var port = getRandomPort();
    var sip2Configuration = s3Configuration(port, s3TenantStoreConfig());
    var s3Client = MinioContainerExtension.getS3Client();
    var tenantConfig = new ByteArrayInputStream(getScTenantConfContent(port).getBytes(UTF_8));
    s3Client.write("sip2/sip2-tenants.conf", tenantConfig);

    withDeployedModule(vertx, testContext, sip2Configuration, () ->
        executeInSession(getSessionConfig(port), successLogin(), successStatus()));
  }

  @Test
  void deployVerticle_negative_configurationNotFound(Vertx vertx, VertxTestContext testContext) {
    var port = getRandomPort();
    var sip2Configuration = s3Configuration(port, s3TenantStoreConfig());
    new VertxModule(vertx, sip2Configuration)
        .deployModule()
        .onComplete(testContext.failing(err -> {
          assertThat(err, instanceOf(IllegalStateException.class));
          assertThat(err.getMessage(), is("Failed to load tenant configuration from S3"));
          testContext.completeNow();
        }));
  }

  @Test
  void deployVerticle_negative_configurationNotFull(Vertx vertx, VertxTestContext testContext) {
    var port = getRandomPort();
    var s3StoreConfig = new JsonObject().put("endpoint_url", getProperty("AWS_URL"));
    var sip2Configuration = s3Configuration(port, s3StoreConfig);

    new VertxModule(vertx, sip2Configuration)
        .deployModule()
        .onComplete(testContext.failing(err -> {
          assertThat(err, instanceOf(Error.class));
          assertThat(err.getCause(), instanceOf(IllegalArgumentException.class));
          var expectedErrorMsg = "java.lang.IllegalArgumentException: "
              + "'access_key' is required for s3 configuration";
          assertThat(err.getMessage(), is(expectedErrorMsg));
          testContext.completeNow();
        }));
  }

  private static Sip2TestCommand successStatus() {
    return sip2Exchange(
        Sip2Commands.status(),
        sip2Result -> {
          assertThat(sip2Result.getResponseMessage(), startsWith("98"));
          assertThat(sip2Result.getResponseMessage(), containsString("BXYYYNYNYYYYYNNNYY"));
        }
    );
  }

  private static Sip2TestCommand successLogin() {
    var locationCode = "1a62fa88-e887-4454-a345-24b4a01095fa";
    return sip2Exchange(
        Sip2Commands.login("test_username", "test_password", locationCode),
        sip2Result -> {
          assertTrue(sip2Result.isSuccessfulExchange());
          assertTrue(sip2Result.isChecksumValid());
          assertThat(sip2Result.getResponseMessage(), startsWith("941"));
        });
  }

  private static JsonObject s3Configuration(Object portConfig, JsonObject s3StoreConfig) {
    var storesConfiguration = new JsonObject()
        .put("type", "s3")
        .put("format", "json")
        .put("config", s3StoreConfig);

    var tenantConfigRetrieverOptions = new JsonObject()
        .put("scanPeriod", 10000)
        .put("stores", new JsonArray(List.of(storesConfiguration)))
        .put("optional", true);

    return new JsonObject()
        .put("okapiUrl", requireNonNull(getProperty(WM_URL_PROPERTY)))
        .put("port", portConfig)
        .put("tenantConfigRetrieverOptions", tenantConfigRetrieverOptions);
  }

  private static JsonObject s3TenantStoreConfig() {
    var endpointUrl = System.getProperty("AWS_URL");
    var accessKey = System.getProperty("AWS_ACCESS_KEY_ID");
    var secretAccessKey = System.getProperty("AWS_SECRET_ACCESS_KEY");

    return new JsonObject()
        .put("region", TEST_AWS_REGION)
        .put("bucket", TEST_BUCKET_NAME)
        .put("access_key", accessKey)
        .put("secret_access_key", secretAccessKey)
        .put("endpoint_url", endpointUrl)
        .put("key", "sip2/sip2-tenants.conf");
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

  private static String getScTenantConfContent(int port) {
    var scTenantObject = new JsonObject()
        .put("port", port)
        .put("scSubnet", "0.0.0.0/0")
        .put("tenant", TENANT_ID)
        .put("errorDetectionEnabled", true)
        .put("messageDelimiter", "\r")
        .put("fieldDelimiter", "|")
        .put("charset", "ISO-8859-1");

    return new JsonObject()
        .put("scTenants", new JsonArray(List.of(scTenantObject)))
        .encodePrettily();
  }
}
