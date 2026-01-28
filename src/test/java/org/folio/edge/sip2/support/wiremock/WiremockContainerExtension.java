package org.folio.edge.sip2.support.wiremock;

import com.github.tomakehurst.wiremock.client.HttpAdminClient;
import com.github.tomakehurst.wiremock.core.Admin;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
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
import org.testcontainers.utility.MountableFile;

@Slf4j
public class WiremockContainerExtension implements BeforeAllCallback, AfterAllCallback {

  public static final int WM_DOCKER_PORT = 8080;
  public static final int WM_DOCKER_HTTPS_PORT = 8443;
  public static final String WM_NETWORK_ALIAS = UUID.randomUUID().toString();
  public static final String WM_URL_PROPERTY = "wm.url";

  private static final DockerImageName WM_IMAGE = DockerImageName.parse("wiremock/wiremock:3.13.1");
  private static final String WM_URL_VARS_FILE = "wiremock-url.vars";
  private static final String WM_KEYSTORE_PATH =
      "src/test/resources/certificates/test.keystore.jks";
  private static final String WM_KEYSTORE_TYPE = "JKS";
  private static final String WM_KEYSTORE_PASS = "SecretPassword";

  private static final Map<Boolean, GenericContainer<?>> containerMap = new ConcurrentHashMap<>();
  private static final Map<Boolean, Admin> adminClientMap = new ConcurrentHashMap<>();
  private static final ThreadLocal<Boolean> currentHttpsMode = new ThreadLocal<>();

  /**
   * Gets the WireMock admin client for the current test context.
   *
   * @return - the admin client
   */
  public static Admin getWireMockAdminClient() {
    Boolean https = currentHttpsMode.get();
    if (https == null) {
      throw new IllegalStateException(
          "WireMock admin client context not initialized. Call from @EnableWiremock test.");
    }

    Admin client = adminClientMap.get(https);
    if (client == null) {
      throw new IllegalStateException("WireMock admin client isn't initialized for https=" + https);
    }

    return client;
  }

  @Override
  public void beforeAll(ExtensionContext context) {
    boolean https = isHttpsEnabled(context);
    currentHttpsMode.set(https);

    ensureContainerStarted(https);
    String wmUrl = buildWireMockUrl(https);
    System.setProperty(WM_URL_PROPERTY, wmUrl);
    setSystemVarsToWireMockUrl(context, wmUrl);
  }

  @Override
  public void afterAll(ExtensionContext context) {
    Boolean https = currentHttpsMode.get();

    System.clearProperty(WM_URL_PROPERTY);
    clearSystemVarsWithWireMockUrl(context);

    if (https != null) {
      resetWireMock(https);
      currentHttpsMode.remove();
    }
  }

  private boolean isHttpsEnabled(ExtensionContext context) {
    EnableWiremock annotation = context.getRequiredTestClass().getAnnotation(EnableWiremock.class);

    return annotation != null && annotation.https();
  }

  private void ensureContainerStarted(boolean https) {
    GenericContainer<?> container = containerMap.computeIfAbsent(https, this::createContainer);
    if (!container.isRunning()) {
      container.start();
      exposeMappedPort(https, container);
      createAdminClient(https, container);
      log.info("WireMock server started [url: {}, https: {}]", buildWireMockUrl(https), https);
    } else {
      log.debug("Reusing existing WireMock container [https: {}]", https);
    }
  }

  private GenericContainer<?> createContainer(boolean https) {
    GenericContainer<?> container = new GenericContainer<>(WM_IMAGE)
        .withNetwork(Network.SHARED)
        .withAccessToHost(true)
        .withNetworkAliases(WM_NETWORK_ALIAS)
        .withCommand(buildCommand(https))
        .withExposedPorts(https ? WM_DOCKER_HTTPS_PORT : WM_DOCKER_PORT)
        .withLogConsumer(new Slf4jLogConsumer(log).withSeparateOutputStreams());

    if (https) {
      container.withCopyFileToContainer(
          MountableFile.forHostPath(WM_KEYSTORE_PATH), "/home/wiremock/keystore.jks");
    }

    return container;
  }

  private String[] buildCommand(boolean https) {
    List<String> base = List.of(
        "--local-response-templating",
        "--disable-banner",
        "--verbose"
    );

    if (!https) {
      return base.toArray(String[]::new);
    }

    return Stream.concat(base.stream(), Stream.of(
        "--https-port", String.valueOf(WM_DOCKER_HTTPS_PORT),
        "--https-keystore", "/home/wiremock/keystore.jks",
        "--keystore-password", WM_KEYSTORE_PASS,
        "--key-manager-password", WM_KEYSTORE_PASS,
        "--keystore-type", WM_KEYSTORE_TYPE,
        "--disable-http"
    )).toArray(String[]::new);
  }

  private void exposeMappedPort(boolean https, GenericContainer<?> container) {
    int mappedPort = container.getMappedPort(https ? WM_DOCKER_HTTPS_PORT : WM_DOCKER_PORT);
    Testcontainers.exposeHostPorts(mappedPort);
    log.info("Host port exposed to containers: {}", mappedPort);
  }

  private void createAdminClient(boolean https, GenericContainer<?> container) {
    int mappedPort = container.getMappedPort(https ? WM_DOCKER_HTTPS_PORT : WM_DOCKER_PORT);
    Admin admin = https
        ? new HttpAdminClient("https", container.getHost(), mappedPort)
        : new HttpAdminClient(container.getHost(), mappedPort);
    adminClientMap.put(https, admin);
  }

  private void resetWireMock(boolean https) {
    Admin admin = adminClientMap.get(https);
    if (admin != null) {
      admin.resetAll();
    }
  }

  private String buildWireMockUrl(boolean https) {
    GenericContainer<?> container = containerMap.get(https);
    if (container == null) {
      throw new IllegalStateException("WireMock container not initialized for https=" + https);
    }

    int port = container.getMappedPort(https ? WM_DOCKER_HTTPS_PORT : WM_DOCKER_PORT);
    String scheme = https ? "https" : "http";

    return String.format("%s://%s:%d", scheme, container.getHost(), port);
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
}
