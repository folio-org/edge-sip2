package org.folio.edge.sip2.api;

import static com.github.tomakehurst.wiremock.client.WireMock.created;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.status;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.common.ContentTypes.APPLICATION_JSON;
import static io.restassured.RestAssured.when;
import static java.time.Duration.ofSeconds;
import static java.time.OffsetDateTime.now;
import static org.apache.hc.core5.http.HttpHeaders.CONTENT_TYPE;
import static org.folio.edge.sip2.api.support.TestUtils.executeInSession;
import static org.folio.edge.sip2.support.Sip2TestCommand.sip2Exchange;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.vertx.core.json.JsonObject;
import java.nio.file.Path;
import org.folio.edge.sip2.support.Sip2Commands;
import org.folio.edge.sip2.support.Sip2SessionConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

/**
 * Integration test executed in "mvn verify" phase.
 *
 * <p>It checks the shaded fat uber jar (this is not tested in "mvn test" unit tests).
 *
 * <p>It checks the Dockerfile (this is not tested in "mvn test" unit tests).
 */
@Testcontainers
@WireMockTest(httpPort = 9130)
class EdgeSip2IT {
  private static final Logger LOGGER = LoggerFactory.getLogger(EdgeSip2IT.class);

  @Container
  private static final GenericContainer<?> EDGE_SIP2 =
      new GenericContainer<>(
          new ImageFromDockerfile("edge-sip2").withDockerfile(Path.of("./Dockerfile")))
          .withCopyFileToContainer(MountableFile.forHostPath(
              "src/test/resources/sip2.conf"), "/usr/verticles/sip2.conf")
          .withCopyFileToContainer(MountableFile.forHostPath(
              "src/test/resources/sip2-tenants.conf"), "/usr/verticles/sip2-tenants.conf")
          .withCommand("./run-java.sh -conf sip2.conf")
          .withExposedPorts(6443, 8081)
          .withAccessToHost(true);

  @BeforeAll
  static void beforeAll() {
    EDGE_SIP2.followOutput(new Slf4jLogConsumer(LOGGER).withSeparateOutputStreams());
    org.testcontainers.Testcontainers.exposeHostPorts(9130);
  }

  @Test
  void health() {
    var baseUrl = "http://" + EDGE_SIP2.getHost() + ":" + EDGE_SIP2.getMappedPort(8081);
    when().get(baseUrl + "/admin/health").then().statusCode(200);
  }

  @Test
  void loginSuccess() throws Throwable {
    var loginResponseBody = new JsonObject()
        .put("accessTokenExpiration", now().plusMinutes(5).toString())
        .put("refreshTokenExpiration", now().plusMinutes(15).toString());

    stubFor(post("/authn/login-with-expiry")
        .willReturn(created()
            .withBody(loginResponseBody.encode())
            .withHeader(CONTENT_TYPE, APPLICATION_JSON)
            .withHeader("Set-Cookie", "folioAccessToken=abc.def.ghi"))
    );

    stubFor(get(urlPathEqualTo("/configurations/entries")).willReturn(ok()));
    executeInSession(getSip2SessionConfiguration(),
        sip2Exchange(
            Sip2Commands.login("Matrin", "password"),
            sip2Result -> assertThat(sip2Result.getResponseMessage(), startsWith("941")))
    );
  }

  @Test
  void loginFailure() throws Throwable {
    stubFor(post("/authn/login-with-expiry")
        .willReturn(status(422).withBody("Unprocessable Content")));
    executeInSession(getSip2SessionConfiguration(),
        sip2Exchange(
            Sip2Commands.login("Matrin", "password"),
            sip2Result -> assertThat(sip2Result.getResponseMessage(), startsWith("940")))
    );
  }

  private Sip2SessionConfiguration getSip2SessionConfiguration() {
    return Sip2SessionConfiguration.builder()
        .hostname(EDGE_SIP2.getHost())
        .port(EDGE_SIP2.getMappedPort(6443))
        .useSsl(false)
        .socketTimeout(ofSeconds(5))
        .build();
  }
}
