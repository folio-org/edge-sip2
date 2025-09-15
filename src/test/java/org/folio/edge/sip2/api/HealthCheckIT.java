package org.folio.edge.sip2.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.junit5.VertxTestContext;
import org.folio.edge.sip2.api.support.AbstractErrorDetectionDisabledTest;
import org.folio.edge.sip2.support.tags.IntegrationTest;
import org.junit.jupiter.api.Test;

@IntegrationTest
class HealthCheckIT extends AbstractErrorDetectionDisabledTest {

  @Test
  void healthCheck_positive(
      Vertx vertx, VertxTestContext testContext) {

    HttpClient client = vertx.createHttpClient();
    client.request(HttpMethod.GET, healthCheckPort, "localhost", "/admin/health")
        .compose(HttpClientRequest::send)
        .onComplete(testContext.succeeding(response -> {
          assertEquals(200, response.statusCode());
          response.bodyHandler(body -> {
            assertEquals("OK", body.toString());
            testContext.completeNow();
          });
        }));
  }

  @Test
  void healthCheck_negative_invalidPath(
      Vertx vertx, VertxTestContext testContext) {

    var client = vertx.createHttpClient();
    client.request(HttpMethod.GET, healthCheckPort, "localhost", "/")
        .compose(HttpClientRequest::send)
        .onComplete(testContext.succeeding(response -> {
          assertEquals(404, response.statusCode());
          testContext.completeNow();
        }));
  }
}
