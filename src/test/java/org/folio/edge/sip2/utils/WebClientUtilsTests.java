package org.folio.edge.sip2.utils;

import static org.folio.edge.sip2.utils.WebClientUtils.SYS_CERT_PATHS;
import static org.folio.edge.sip2.utils.WebClientUtils.SYS_NET_SERVER_OPTIONS;
import static org.folio.edge.sip2.utils.WebClientUtils.SYS_PEM_KEY_CERT_OPTIONS;
import static org.folio.edge.sip2.utils.WebClientUtils.SYS_PFX_KEY_CERT_OPTIONS;
import static org.folio.edge.sip2.utils.WebClientUtils.SYS_PORT;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.SelfSignedCertificate;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class WebClientUtilsTests {

  private static final String RESPONSE_MESSAGE = "<OK>";
  private static final Logger log = LogManager.getLogger();
  private Integer serverPort;
  private SelfSignedCertificate selfSignedCertificate;

  @BeforeEach
  void setup() {
    this.serverPort = getAvailablePort();
    this.selfSignedCertificate = SelfSignedCertificate.create();
  }

  @AfterEach
  void tearDown() {
    this.serverPort = null;
    this.selfSignedCertificate = null;
  }

  @Test
  void testCreateWebClientTlsOff(Vertx vertx) {
    JsonObject config = new JsonObject();
    Assertions.assertDoesNotThrow(() -> WebClientUtils.create(vertx, config));
  }

  @Test
  void testCreateWebClientTlsOn(Vertx vertx) {
    JsonObject config = new JsonObject().put(SYS_NET_SERVER_OPTIONS, new JsonObject()
        .put(SYS_PEM_KEY_CERT_OPTIONS, selfSignedCertificate.keyCertOptions().toJson()));
    Assertions.assertDoesNotThrow(() -> WebClientUtils.create(vertx, config));
  }

  @Test
  void testCreateWebClientTlsOnMultipleCerts(Vertx vertx) {
    JsonArray certPathsArray = new JsonArray()
        .add(SelfSignedCertificate.create().certificatePath())
        .add(SelfSignedCertificate.create().certificatePath())
        .add(SelfSignedCertificate.create().certificatePath());
    JsonObject certPaths = new JsonObject().put(SYS_CERT_PATHS, certPathsArray);

    JsonObject config = new JsonObject().put(SYS_NET_SERVER_OPTIONS, new JsonObject()
        .put(SYS_PEM_KEY_CERT_OPTIONS, certPaths));
    Assertions.assertDoesNotThrow(() -> WebClientUtils.create(vertx, config));
  }

  @Test
  void testCreateWebClientWithMissingCertPaths(Vertx vertx) {
    JsonObject config = new JsonObject().put(SYS_NET_SERVER_OPTIONS, new JsonObject()
        .put(SYS_PEM_KEY_CERT_OPTIONS, new JsonObject()));
    Assertions.assertThrows(WebClientConfigException.class,
        () -> WebClientUtils.create(vertx, config));
  }

  @Test
  void testCreateWebClientWithEmptyCertPaths(Vertx vertx) {
    JsonObject config = new JsonObject().put(SYS_NET_SERVER_OPTIONS, new JsonObject()
        .put(SYS_PEM_KEY_CERT_OPTIONS, new JsonObject().put(SYS_CERT_PATHS, new JsonArray())));
    Assertions.assertThrows(WebClientConfigException.class,
        () -> WebClientUtils.create(vertx, config));
  }

  @Test
  void testWebClientServerCommunication(Vertx vertx, VertxTestContext testContext) {
    JsonObject sipConfig = getCommonSipConfig(vertx);

    sipConfig.put(SYS_PORT, serverPort);
    sipConfig.put(SYS_NET_SERVER_OPTIONS, new JsonObject()
        .put(SYS_PEM_KEY_CERT_OPTIONS, selfSignedCertificate.keyCertOptions().toJson()));

    createServerTlsOn(vertx, testContext);

    final WebClient webClient = WebClientUtils.create(vertx, sipConfig);
    webClient.get(serverPort, "localhost", "/")
        .send()
        .onComplete(testContext.succeeding(response -> {
          String message = response.body().toString();
          log.info("WebClient sent message to port {}, message: {}", serverPort, message);
          Assertions.assertEquals(HttpResponseStatus.OK.code(), response.statusCode());
          Assertions.assertEquals(RESPONSE_MESSAGE, message);
          testContext.completeNow();
        }));
  }

  @Test
  void testFailingWebClientServerCommunication(Vertx vertx, VertxTestContext testContext) {
    JsonObject sipConfig = getCommonSipConfig(vertx);

    sipConfig.put(SYS_PORT, serverPort);
    sipConfig.put(SYS_NET_SERVER_OPTIONS, new JsonObject());

    createServerTlsOn(vertx, testContext);

    final WebClient webClient = WebClientUtils.create(vertx, sipConfig);
    webClient.get(serverPort, "localhost", "/")
        .send()
        .onComplete(testContext.failing(err -> {
          log.info("Connection error: ", err);
          testContext.completeNow();
        }));
  }

  @Test
  void testCreateWebClientTlsOnWithPfx(Vertx vertx) throws Exception {
    String password = "changeit";
    Path trustStore = createTrustStoreFrom(selfSignedCertificate, password);

    JsonObject config = new JsonObject()
        .put(SYS_NET_SERVER_OPTIONS, new JsonObject()
        .put(SYS_PFX_KEY_CERT_OPTIONS, new JsonObject()
          .put("path", trustStore.toString())
          .put("password", password)));

    Assertions.assertDoesNotThrow(() -> WebClientUtils.create(vertx, config));
  }

  @Test
  void testCreateWebClientTlsOnWithPfxMissingPath(Vertx vertx) {
    JsonObject config = new JsonObject()
        .put(SYS_NET_SERVER_OPTIONS, new JsonObject()
        .put(SYS_PFX_KEY_CERT_OPTIONS, new JsonObject()
          .put("password", "secret")));

    Assertions.assertDoesNotThrow(() -> WebClientUtils.create(vertx, config));
  }

  private static Path createTrustStoreFrom(SelfSignedCertificate certificate, String password)
      throws Exception {
    KeyStore keyStore = KeyStore.getInstance("PKCS12");
    keyStore.load(null, password.toCharArray());

    CertificateFactory factory = CertificateFactory.getInstance("X.509");
    try (InputStream certStream = Files.newInputStream(Path.of(certificate.certificatePath()))) {
      Certificate cert = factory.generateCertificate(certStream);
      keyStore.setCertificateEntry("alias", cert);
    }

    Path temp = Files.createTempFile("sip2-truststore-", ".p12");
    temp.toFile().deleteOnExit();
    try (OutputStream out = Files.newOutputStream(temp)) {
      keyStore.store(out, password.toCharArray());
    }
    return temp;
  }

  private void createServerTlsOn(Vertx vertx, VertxTestContext testContext) {
    final HttpServerOptions httpServerOptions = new HttpServerOptions()
        .setPort(serverPort)
        .setSsl(true)
        .setKeyCertOptions(selfSignedCertificate.keyCertOptions());

    final HttpServer httpServer = vertx.createHttpServer(httpServerOptions);
    httpServer
        .requestHandler(req -> req.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, "text/plain")
            .end(RESPONSE_MESSAGE))
        .listen(serverPort, http -> {
          if (http.succeeded()) {
            log.info("Server started on port: {}", serverPort);
          } else {
            testContext.failNow(http.cause());
          }
        });
  }

  private static JsonObject getCommonSipConfig(Vertx vertx) {
    final FileSystem fileSystem = vertx.fileSystem();
    return fileSystem.readFileBlocking("test-sip2.conf").toJsonObject();
  }

  private static int getAvailablePort() {
    do {
      try (ServerSocket socket = new ServerSocket(0)) {
        return socket.getLocalPort();
      } catch (IOException e) {
        // ignore
      }
    } while (true);
  }
}
