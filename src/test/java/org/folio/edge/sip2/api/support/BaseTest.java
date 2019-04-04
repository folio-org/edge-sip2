package org.folio.edge.sip2.api.support;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.EnumMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.folio.edge.sip2.MainVerticle;
import org.folio.edge.sip2.Sip2HandlerCommandTypes;
import org.folio.edge.sip2.handlers.LoginHandler;
import org.folio.edge.sip2.handlers.ISip2RequestHandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public abstract class BaseTest {

  protected MainVerticle myVerticle;
  private final int port = getRandomPort();

  /**
   * Deploy the verticle before each test.
   * @param vertx the vertx instance.
   * @param testContext vertx test context.
   * @param testInfo info about the test.
   */
  @BeforeEach
  @DisplayName("Deploy the verticle")
  public void deployVerticle(Vertx vertx, VertxTestContext testContext, TestInfo testInfo) {

    DeploymentOptions opt = new DeploymentOptions();

    JsonObject sipConfig = new JsonObject();
    sipConfig.put("port", port);
    opt.setConfig(sipConfig);

    setMainVerticleInstance(testInfo.getDisplayName());
    vertx.deployVerticle(myVerticle, opt, testContext.completing());

    System.out.println("done deploying in base class");
  }

  /**
   * Calls the service.
   * @param ncipMessage the sip message to send.
   * @param testContext the vertx test context.
   * @param vertx the vertx instance.
   * @param testHandler the handler for this test.
   * @throws Throwable an unexpected error.
   */
  public void callService(String ncipMessage, VertxTestContext testContext,
      Vertx vertx, Handler<String> testHandler) throws Throwable {

    NetClientOptions options = new NetClientOptions();
    options.setConnectTimeout(2);
    options.setIdleTimeout(2);
    options.setIdleTimeoutUnit(TimeUnit.SECONDS);

    NetClient tcpClient = vertx.createNetClient(options);

    tcpClient.connect(port, "localhost", res -> {
      System.out.println("Shaking hands...");
      NetSocket socket = res.result();
      socket.write(ncipMessage);
      System.out.println("done writing");

      socket.handler(buffer -> {
        String message = buffer.getString(0, buffer.length());
        testContext.verify(() -> testHandler.handle(message));
        testContext.completeNow();
      });
    });
  }

  private void setMainVerticleInstance(String methodName) {
    if (methodName.equalsIgnoreCase("CanStartMainVericleInjectingSip2RequestHandlers")) {
      LoginHandler loginHandler = new LoginHandler();

      EnumMap<Sip2HandlerCommandTypes, ISip2RequestHandler> requestHandlerMap =
          new EnumMap<>(Sip2HandlerCommandTypes.class);
      requestHandlerMap.put(Sip2HandlerCommandTypes.LOGIN, loginHandler);

      myVerticle = new MainVerticle(requestHandlerMap);

    } else {
      myVerticle = new MainVerticle();
    }
  }

  private static int getRandomPort() {
    int port = -1;
    do {
      // Use a random ephemeral port
      port = new Random().nextInt(16_384) + 49_152;
      try {
        final ServerSocket socket = new ServerSocket(port);
        socket.close();
      } catch (IOException e) {
        continue;
      }
      break;
    } while (true);

    return port;
  }
}
