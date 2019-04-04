package org.folio.edge.sip2.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.folio.edge.sip2.api.support.BaseTest;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;

import org.folio.edge.sip2.Sip2HandlerCommandTypes;
import org.junit.jupiter.api.Test;

public class MainVerticleTests extends BaseTest {

  @Test
  public void canStartMainVerticle() {
    assertNotNull(myVerticle.deploymentID());
  }

  @Test
  public void canMakeARequest(Vertx vertex, VertxTestContext testContext) throws Throwable {
    callService(Sip2HandlerCommandTypes.LOGIN.getValue() + "Martin",
        testContext, vertex, result -> {
          assertEquals("Logged Martin in", result);
        });
  }

  @Test
  public void canStartMainVericleInjectingSip2RequestHandlers(Vertx vertex,
      VertxTestContext testContext) throws Throwable {

    String title = "Angry Planet";
    String sipMessage = Sip2HandlerCommandTypes.CHECKOUT.getValue() + title;

    callService(sipMessage, testContext, vertex, result -> {
      assertEquals("Successfully checked out " + title, result);
    });
  }

  @Test
  public void cannotCheckoutWithInvalidCommandCode(Vertx vertex,
      VertxTestContext testContext) throws Throwable {
    callService("blablabalb", testContext, vertex, result -> {
      assertTrue(result.contains("Problems handling the request"));
    });
  }

  @Test
  public void canGetACSStatus(Vertx vertex,
                              VertxTestContext testContext) throws Throwable {
    callService("990231.23", testContext, vertex, result -> {
      assertTrue(result.contains("Problems handling the request"));
    });
  }
}
