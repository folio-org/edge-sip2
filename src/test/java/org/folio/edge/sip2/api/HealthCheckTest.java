package org.folio.edge.sip2.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class HealthCheckTest {
  protected static Logger log = LogManager.getLogger();
  private NetServer server;
  private NetClient client;

  /**
   * Setting up the server and client before each test.
   * @param vertx the vertx instance.
   * @param testContext vertx test context.
   */
  @BeforeEach
  public void setUp(Vertx vertx, VertxTestContext testContext) {
    server = vertx.createNetServer(new NetServerOptions().setPort(8080));

    server.connectHandler(socket -> {
      socket.handler(buffer -> {
        String request = buffer.toString();
        if (request.contains("GET /admin/health HTTP/1.1")) {
          socket.write(Buffer.buffer("HTTP/1.1 200 OK\r\nContent-Length: 2\r\n\r\nOK"));
        } else {
          log.info("socket closed");
          socket.close();
        }
      });
    });

    server.listen(testContext.succeeding(ar -> testContext.completeNow()));
    client = vertx.createNetClient(new NetClientOptions());
  }

  @AfterEach
  public void tearDown(VertxTestContext testContext) {
    server.close(testContext.succeeding(ar -> testContext.completeNow()));
    client.close();
  }

  @Test
  @DisplayName("Test /admin/health endpoint")
  public void testHealthCheckPoint(VertxTestContext testContext) {
    client.connect(8080, "localhost", testContext.succeeding(socket -> {
      socket.handler(buffer -> {
        String actualResponse = buffer.toString();
        String expectResponse = "HTTP/1.1 200 OK\r\nContent-Length: 2\r\n\r\nOK";
        assertEquals(expectResponse, actualResponse);
        testContext.completeNow();
      });
      socket.write(Buffer.buffer("GET /admin/health HTTP/1.1"));
    }));
  }
}
