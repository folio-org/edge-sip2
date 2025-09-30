package org.folio.edge.sip2.api.support;

import static java.lang.System.getProperty;
import static java.util.Objects.requireNonNull;
import static org.folio.edge.sip2.api.support.TestUtils.getRandomPort;
import static org.folio.edge.sip2.support.Sip2TestCommand.sip2Exchange;
import static org.folio.edge.sip2.support.wiremock.WiremockContainerExtension.WM_URL_PROPERTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.SelfSignedCertificate;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.concurrent.atomic.AtomicReference;
import org.folio.edge.sip2.support.Sip2Commands;
import org.folio.edge.sip2.support.Sip2SessionConfiguration;
import org.folio.edge.sip2.support.Sip2TestCommand;
import org.folio.edge.sip2.support.Sip2TestConfig;
import org.folio.edge.sip2.support.model.Sip2CommandResult;
import org.folio.edge.sip2.support.tags.EnableTls;
import org.folio.edge.sip2.support.vertx.VertxModule;
import org.folio.edge.sip2.support.wiremock.EnableWiremock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@EnableWiremock
@ExtendWith({ VertxExtension.class, MockitoExtension.class })
public abstract class BaseIntegrationTest {

  protected static final String TENANT_ID = "testtenant";
  protected static final String SERVICE_POINT_ID = "1a62fa88-e887-4454-a345-24b4a01095fa";
  protected static final String PATRON_BARCODE = "testBarcode";

  protected static final String TEST_USERNAME = "test_username";
  protected static final String TEST_PASSWORD = "test_password";

  protected static VertxModule module;
  protected static SelfSignedCertificate certificate;
  protected static AtomicReference<String> moduleIdReference = new AtomicReference<>();

  protected static int mainVerticlePort = getRandomPort();
  protected static int healthCheckPort = getRandomPort();

  @BeforeAll
  static void beforeAll(Vertx vertx, VertxTestContext testContext, TestInfo testInfo) {
    System.setProperty(
        "vertx.logger-delegate-factory-class-name",
        "io.vertx.core.logging.Log4j2LogDelegateFactory");
    var testClazz = testInfo.getTestClass();
    var sip2TestConfigPath = testClazz
        .map(clazz -> clazz.getAnnotation(Sip2TestConfig.class))
        .map(Sip2TestConfig::value)
        .orElse(null);

    assertNotNull(sip2TestConfigPath);

    var fileSystem = vertx.fileSystem();
    var sipConfig = fileSystem.readFileBlocking(sip2TestConfigPath).toJsonObject();

    sipConfig.put("port", mainVerticlePort);
    sipConfig.put("okapiUrl", requireNonNull(getProperty(WM_URL_PROPERTY)));

    if (isTlsEnabledForTest(testInfo)) {
      certificate = SelfSignedCertificate.create();
      sipConfig.put("netServerOptions", new JsonObject()
          .put("ssl", true)
          .put("pemKeyCertOptions", certificate.keyCertOptions().toJson()));
    }

    System.setProperty("healthCheckPort", String.valueOf(healthCheckPort));

    module = new VertxModule(vertx, sipConfig);
    module.deployModule()
        .onSuccess(id -> moduleIdReference.set(id))
        .onComplete(testContext.succeedingThenComplete());
  }

  private static boolean isTlsEnabledForTest(TestInfo testInfo) {
    return testInfo.getTestClass()
        .map(testClass -> testClass.getAnnotation(EnableTls.class))
        .isPresent();
  }

  @AfterAll
  static void afterAll(Vertx vertx, VertxTestContext testContext) {
    assertNotNull(moduleIdReference.get());
    vertx.undeploy(moduleIdReference.get())
        .onComplete(testContext.succeedingThenComplete());

  }

  protected void executeInSession(Sip2TestCommand... sip2Commands) throws Throwable {
    TestUtils.executeInSession(getSip2SessionConfig(), sip2Commands);
  }

  protected abstract Sip2SessionConfiguration getSip2SessionConfig();

  protected static Sip2TestCommand successLoginExchange() {
    return sip2Exchange(
        Sip2Commands.login("test_username", "test_password", SERVICE_POINT_ID),
        sip2Result -> {
          assertSuccessfulExchange(sip2Result);
          assertThat(sip2Result.getResponseMessage(), startsWith("941"));
        });
  }

  protected static void assertSuccessfulExchange(Sip2CommandResult result) {
    assertTrue(result.isSuccessfulExchange());
    assertTrue(result.isChecksumValid());
  }
}
