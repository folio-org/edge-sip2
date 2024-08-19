package org.folio.edge.sip2.api.support;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SelfSignedCertificate;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.EnumMap;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.MainVerticle;
import org.folio.edge.sip2.handlers.ACSResendHandler;
import org.folio.edge.sip2.handlers.ISip2RequestHandler;
import org.folio.edge.sip2.handlers.LoginHandler;
import org.folio.edge.sip2.parser.Command;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
public abstract class BaseTest {
  protected static final String TLS_ENABLED = "TLSEnabled";
  protected static final String ERROR_DETECTION_ENABLED = "ErrorDetectionEnabled";

  protected static Logger log = LogManager.getLogger();

  @Mock
  private LoginHandler mockLoginHandler;
  protected MainVerticle myVerticle;
  private final int port = getRandomPort();

  static {
    System.setProperty("vertx.logger-delegate-factory-class-name",
        "io.vertx.core.logging.Log4j2LogDelegateFactory");
  }

  /**
   * Deploy the verticle before each test.
   * @param vertx the vertx instance.
   * @param testContext vertx test context.
   * @param testInfo info about the test.
   */
  @BeforeEach
  @DisplayName("Deploy the verticle")
  public void deployVerticle(Vertx vertx, VertxTestContext testContext, TestInfo testInfo) {
    log.info("Starting: {}", testInfo.getDisplayName());


    FileSystem fs = vertx.fileSystem();
    JsonObject sipConfig = fs.readFileBlocking("test-sip2.conf").toJsonObject();
    sipConfig.put("port", port);
    if (testInfo.getTags().contains(TLS_ENABLED)) {
      final SelfSignedCertificate certificate = SelfSignedCertificate.create();
      sipConfig.put("netServerOptions", new JsonObject()
          .put("ssl", true)
          .put("pemKeyCertOptions", certificate.keyCertOptions().toJson()));
    }
    DeploymentOptions opt = new DeploymentOptions();
    opt.setConfig(sipConfig);

    setMainVerticleInstance(testInfo.getDisplayName());
    vertx.deployVerticle(myVerticle, opt, testContext.succeedingThenComplete());

    if (testInfo.getTags().contains(ERROR_DETECTION_ENABLED)) {
      try {
        Thread.sleep(500);  // allow for MainVerticle to start before changing config
        Buffer buff = fs.readFileBlocking("sip2-tenants-error-detect-true.conf");
        fs.writeFileBlocking("sip2-tenants.conf", buff);
        Thread.sleep(1500);  // allow for MainVerticle to detect config change
      } catch (InterruptedException e) {
        log.info("Interrupted sleep, gonna be tired :( ");
      }
    }

    log.info("done deploying in base class");
  }

  @AfterEach
  @DisplayName("Shutdown")
  void shutdown(Vertx vertx, TestInfo testInfo) {
    log.info("Finished: {}", testInfo.getDisplayName());
  }

  /**
   * Calls the service multiple times for all the sipMessages passed in.
   * @param sipMessages the sip messages to send.
   * @param testContext the vertx test context.
   * @param testHandler the handler for this test.
   */
  public void callServiceMultiple(String[] sipMessages, VertxTestContext testContext,
                          Vertx vertx, Handler<String> testHandler) {

    NetClientOptions options = new NetClientOptions();
    options.setConnectTimeout(2);
    options.setIdleTimeout(2);
    options.setIdleTimeoutUnit(TimeUnit.SECONDS);
    NetClient tcpClient = vertx.createNetClient(options);

    tcpClient.connect(port, "localhost", res -> {
      if (res.succeeded()) {

        log.debug("Shaking hands...");
        NetSocket socket = res.result();

        for (int i = 0; i < sipMessages.length; i++) {
          socket.handler(buffer -> {
            String message = buffer.getString(0, buffer.length());
            testContext.verify(() -> testHandler.handle(message));
          }).exceptionHandler(t -> {
            log.error("Socket handler test expection", t);
            testContext.failNow(t);
          }).write(sipMessages[i]);
          log.debug("done writing");
        }
      } else {
        log.error("Failed to connect", res.cause());
      }
      testContext.completeNow();
    });
  }

  public void callService(String sipMessage, VertxTestContext testContext,
      Vertx vertx, Handler<String> testHandler) {
    callService(sipMessage, testContext, vertx, null, testHandler);
  }

  /**
   * Calls the service.
   * @param sipMessage the sip message to send.
   * @param testContext the vertx test context.
   * @param vertx the vertx instance.
   * @param testInfo info about the test.
   * @param testHandler the handler for this test.
   */
  public void callService(String sipMessage, VertxTestContext testContext,
                          Vertx vertx, TestInfo testInfo, Handler<String> testHandler) {

    NetClientOptions options = new NetClientOptions();
    options.setConnectTimeout(2);
    options.setIdleTimeout(2);
    options.setIdleTimeoutUnit(TimeUnit.SECONDS);
    options.setHostnameVerificationAlgorithm("HTTPS");

    if (testInfo != null && testInfo.getTags().contains(TLS_ENABLED)) {
      options.setSsl(true);
      options.setTrustAll(true);
    }

    NetClient tcpClient = vertx.createNetClient(options);

    tcpClient.connect(port, "localhost", res -> {
      if (res.succeeded()) {
        log.debug("Shaking hands...");
        NetSocket socket = res.result();
        if (testInfo != null && testInfo.getTags().contains(TLS_ENABLED)) {
          assertTrue(socket.isSsl());
        } else {
          assertFalse(socket.isSsl());
        }
        socket.handler(buffer -> {
          String message = buffer.getString(0, buffer.length());
          testContext.verify(() -> testHandler.handle(message));
          testContext.completeNow();
        }).exceptionHandler(t -> {
          log.error("Socket handler test expection", t);
          testContext.failNow(t);
        }).write(sipMessage);
        log.debug("done writing");
      } else {
        log.error("Failed to connect", res.cause());
      }
    });
  }

  private void setMainVerticleInstance(String methodName) {
    if (methodName.equalsIgnoreCase("CanStartMainVericleInjectingSip2RequestHandlers")) {
      EnumMap<Command, ISip2RequestHandler> requestHandlerMap =
          new EnumMap<>(Command.class);
      requestHandlerMap.put(Command.LOGIN, mockLoginHandler);

      myVerticle = new MainVerticle(requestHandlerMap);

    } else if (methodName.startsWith("canMakeARequest(")) {
      when(mockLoginHandler.execute(any(), any())).thenReturn(Future.succeededFuture("941"));

      EnumMap<Command, ISip2RequestHandler> requestHandlerMap =
          new EnumMap<>(Command.class);
      requestHandlerMap.put(Command.LOGIN, mockLoginHandler);
      requestHandlerMap.put(Command.REQUEST_ACS_RESEND, new ACSResendHandler());

      myVerticle = new MainVerticle(requestHandlerMap);

    } else {
      myVerticle = new MainVerticle();
    }
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
