package api;

import org.folio.edge.sip2.Sip2HandlerCommandTypes;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import api.support.BaseTest;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;

public class MainVerticleTests extends BaseTest {

  @Test
  public void CanStartMainVerticle(){
    Assert.assertNotNull(myVerticle.deploymentID());
  }

  @Test
  public void CanMakeARequest(Vertx vertex, VertxTestContext testContext){
    callService(Sip2HandlerCommandTypes.LOGIN.getValue() + "Martin", testContext, vertex, result ->{
      Assert.assertEquals("Logged Martin in", result);
    });
  }

  @Test
  public void CanStartMainVericleInjectingSip2RequestHandlers(Vertx vertex, VertxTestContext testContext){

      String title = "Angry Planet";
      String sipMessage = Sip2HandlerCommandTypes.CHECKOUT.getValue() + title;

      callService(sipMessage, testContext, vertex, result ->{
      Assert.assertEquals("Successfully checked out " + title, result);
    });
  }
}
