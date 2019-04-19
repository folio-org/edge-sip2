package org.folio.edge.sip2.api.support;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.folio.edge.sip2.MainVerticle;
import org.folio.edge.sip2.handlers.ISip2RequestHandler;
import org.folio.edge.sip2.handlers.LoginHandler;
import org.folio.edge.sip2.parser.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
public abstract class BaseTest {
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

    JsonObject sipConfig = new JsonObject();
    sipConfig.put("port", port);
    if (testInfo.getTags().contains("ErrorDetectionEnabled")) {
      sipConfig.put("errorDetectionEnabled", true);
    }
    sipConfig.put("okapiUrl", "http://example.com");
    sipConfig.put("tenant", "diku");

    DeploymentOptions opt = new DeploymentOptions();
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
   */
  public void callService(String ncipMessage, VertxTestContext testContext,
      Vertx vertx, Handler<String> testHandler) {

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

  protected String getFormattedLocalDateTime(ZonedDateTime dateTime) {
    final ZonedDateTime d =  dateTime.truncatedTo(SECONDS);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    return formatter.format(d);
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
