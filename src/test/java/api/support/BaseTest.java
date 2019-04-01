package api.support;

import java.util.EnumMap;

import org.folio.edge.sip2.MainVerticle;
import org.folio.edge.sip2.Sip2HandlerCommandTypes;
import org.folio.edge.sip2.handlers.LoginHandler;
import org.folio.edge.sip2.handlers.Sip2RequestHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
public abstract class BaseTest {

  protected MainVerticle myVerticle;
  private final int PORT = 1234;

  @BeforeEach
  @DisplayName("Deploy the verticle")
  public void deployVerticle(Vertx vertx, VertxTestContext testContext, TestInfo testInfo) {

    DeploymentOptions opt = new DeploymentOptions();

    JsonObject sipConfig = new JsonObject();
    sipConfig.put("port", PORT);
    opt.setConfig(sipConfig);

    setMainVerticleInstance(testInfo.getDisplayName());
    vertx.deployVerticle(myVerticle, opt, testContext.completing() );

    System.out.println("done deploying in base class");
  }

  public void callService(String ncipMessage, VertxTestContext testContext, Vertx vertx, Handler<String> testHandler) {

    NetClientOptions options = new NetClientOptions().setConnectTimeout(10000);
    NetClient tcpClient = vertx.createNetClient(options);

    tcpClient.connect(PORT, "localhost", res -> {
      System.out.println("Shaking hands...");
      NetSocket socket = res.result();
      socket.write(ncipMessage);
      socket.handler(buffer -> {
        String message = buffer.getString(0, buffer.length());
        testContext.verify( () -> testHandler.handle(message));
        testContext.completeNow();
      });
    });
  }

  private void setMainVerticleInstance(String methodName){
    if (methodName == "CanStartMainVericleInjectingSip2RequestHandlers"){
      LoginHandler loginHandler = new LoginHandler();
      EnumMap<Sip2HandlerCommandTypes, Sip2RequestHandler> requestHandlerMap = new EnumMap<>(Sip2HandlerCommandTypes.class);
      requestHandlerMap.put(Sip2HandlerCommandTypes.LOGIN, loginHandler);

      myVerticle = new MainVerticle(requestHandlerMap);
    } else {
      myVerticle = new MainVerticle();
    }
  }
}
