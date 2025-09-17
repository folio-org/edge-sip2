package org.folio.edge.sip2.support.wiremock;

import com.github.tomakehurst.wiremock.client.HttpAdminClient;
import com.github.tomakehurst.wiremock.core.Admin;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public class WiremockContainerExtension implements BeforeAllCallback, AfterAllCallback {

  public static final int WM_DOCKER_PORT = 8080;
  public static final String WM_NETWORK_ALIAS = UUID.randomUUID().toString();
  public static final String WM_URL_PROPERTY = "wm.url";

  private static final DockerImageName WM_IMAGE = DockerImageName.parse("wiremock/wiremock:2.35.0");
  private static final String WM_URL_VARS_FILE = "wiremock-url.vars";

  private static Admin adminClient;

  @SuppressWarnings("resource")
  private static final GenericContainer<?> WM_CONTAINER = new GenericContainer<>(WM_IMAGE)
      .withNetwork(Network.SHARED)
      .withExposedPorts(WM_DOCKER_PORT)
      .withAccessToHost(true)
      .withNetworkAliases(WM_NETWORK_ALIAS)
      .withCommand(
          "--local-response-templating",
          "--disable-banner",
          "--verbose"
      )
      .withLogConsumer(new Slf4jLogConsumer(log).withSeparateOutputStreams());

  /**
   * Gets the WireMock admin client.
   *
   * @return - the admin client
   */
  public static Admin getWireMockAdminClient() {
    if (adminClient == null) {
      throw new IllegalStateException("WireMock admin client isn't initialized");
    }

    return adminClient;
  }

  @Override
  public void beforeAll(ExtensionContext context) {
    runContainer();
    var wmUrl = getUrlForExposedPort();
    System.setProperty(WM_URL_PROPERTY, wmUrl);
    setSystemVarsToWireMockUrl(context, wmUrl);
  }

  @Override
  public void afterAll(ExtensionContext context) {
    System.clearProperty(WM_URL_PROPERTY);

    clearSystemVarsWithWireMockUrl(context);
  }

  @SneakyThrows
  private static void runContainer() {
    if (!WM_CONTAINER.isRunning()) {
      WM_CONTAINER.start();

      var wmUrl = getUrlForExposedPort();
      log.info("Wire mock server started [url: {}]", wmUrl);

      int hostPort = WM_CONTAINER.getMappedPort(WM_DOCKER_PORT);
      Testcontainers.exposeHostPorts(hostPort);
      log.info("Host port exposed to containers: {}", hostPort);

      adminClient = new HttpAdminClient(
          WM_CONTAINER.getHost(), WM_CONTAINER.getMappedPort(WM_DOCKER_PORT));
    }
  }

  private void setSystemVarsToWireMockUrl(ExtensionContext context, String wmUrl) {
    List<String> vars = readWireMockUrlVars(context);

    log.debug("Assigning WireMock url to system variables: {}", vars);
    vars.forEach(env -> System.setProperty(env, wmUrl));
  }

  private void clearSystemVarsWithWireMockUrl(ExtensionContext context) {
    List<String> vars = readWireMockUrlVars(context);

    log.debug("Clearing system variables with WireMock url: {}", vars);
    vars.forEach(System::clearProperty);
  }

  @SneakyThrows
  private static List<String> readWireMockUrlVars(ExtensionContext context) {
    var cl = context.getRequiredTestClass().getClassLoader();

    var url = cl.getResource(WM_URL_VARS_FILE);
    if (url == null) {
      return Collections.emptyList();
    }

    return FileUtils.readLines(new File(url.toURI()), StandardCharsets.UTF_8);
  }

  private static String getUrlForExposedPort() {
    return String.format("http://%s:%s",
        WM_CONTAINER.getHost(),
        WM_CONTAINER.getMappedPort(WM_DOCKER_PORT));
  }
}
