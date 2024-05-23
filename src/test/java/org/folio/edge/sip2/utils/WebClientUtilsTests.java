package org.folio.edge.sip2.utils;

import static org.folio.edge.sip2.utils.WebClientUtils.SYS_NET_SERVER_OPTIONS;
import static org.folio.edge.sip2.utils.WebClientUtils.SYS_PEM_KEY_CERT_OPTIONS;
import static org.folio.edge.sip2.utils.WebClientUtils.SYS_PORT;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.SelfSignedCertificate;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.io.IOException;
import java.net.ServerSocket;
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
    this.serverPort = getRandomPort();
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
  void testCreateWebClientWithMissingCertPaths(Vertx vertx) {
    JsonObject config = new JsonObject().put(SYS_NET_SERVER_OPTIONS, new JsonObject()
        .put(SYS_PEM_KEY_CERT_OPTIONS, new JsonObject()));
    Assertions.assertThrows(WebClientConfigException.class, () -> WebClientUtils.create(vertx, config));
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

  private static int getRandomPort() {
    do {
      try (ServerSocket socket = new ServerSocket(0)) {
        return socket.getLocalPort();
      } catch (IOException e) {
        // ignore
      }
    } while (true);
  }
}
