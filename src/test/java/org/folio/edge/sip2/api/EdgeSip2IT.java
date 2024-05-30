package org.folio.edge.sip2.api;

import static com.github.tomakehurst.wiremock.client.WireMock.created;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.status;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Path;
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
    var uri = "http://" + EDGE_SIP2.getHost() + ":" + EDGE_SIP2.getMappedPort(8081) + "/admin/health";
    when()
        .get(uri)
    .then()
        .statusCode(200);
  }

  @Test
  void loginSuccess() {
    stubFor(post("/authn/login-with-expiry")
        .willReturn(created().withHeader("Set-Cookie", "folioAccessToken=abc.def.ghi")));
    stubFor(get(urlPathEqualTo("/configurations/entries")).willReturn(ok()));
    assertThat(send("9300CNMartin|COpassword|\r"), is("941\r"));
  }

  @Test
  void loginFailure() {
    stubFor(post("/authn/login-with-expiry").willReturn(status(422)));
    assertThat(send("9300CNMartin|COpassword|\r"), is("940\r"));
  }

  String send(String request) {
    var response = new StringBuilder();
    try (var socket = new Socket(EDGE_SIP2.getHost(), EDGE_SIP2.getMappedPort(6443));
        var out = new PrintWriter(socket.getOutputStream());
        var in = socket.getInputStream()) {
      out.append(request).flush();
      socket.setSoTimeout(2000);
      int c;
      do {
        c = in.read();
        if (c == -1) {
          break;
        }
        response.append((char) c);
      } while (c != '\r');
      return response.toString();
    } catch (SocketTimeoutException e) {
      throw new UncheckedIOException("Got '" + response.toString() + "' but no \\r", e);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
