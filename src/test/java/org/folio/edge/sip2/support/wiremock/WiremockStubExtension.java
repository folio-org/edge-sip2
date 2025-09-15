package org.folio.edge.sip2.support.wiremock;

import static com.github.tomakehurst.wiremock.admin.model.ServeEventQuery.ALL_UNMATCHED;
import static org.folio.edge.sip2.support.wiremock.WiremockContainerExtension.getWireMockAdminClient;

import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import io.vertx.core.json.JsonObject;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

public class WiremockStubExtension implements
    BeforeEachCallback, AfterEachCallback, BeforeAllCallback, AfterAllCallback {

  private static final Namespace NAMESPACE = Namespace.create(WiremockStubExtension.class);
  public static final String STUB_IDS = "stubIds";

  @Override
  public void afterAll(ExtensionContext context) {
    getWireMockAdminClient().resetAll();
  }

  @Override
  public void beforeAll(ExtensionContext context) {
    getWireMockAdminClient().resetAll();
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    var testMethod = context.getRequiredTestMethod();
    var testClass = context.getRequiredTestClass();

    var stubFilePaths = new LinkedHashSet<String>();
    stubFilePaths.addAll(getStubPathsSafe(testClass.getAnnotation(WiremockStubs.class)));
    stubFilePaths.addAll(getStubPathsSafe(testMethod.getAnnotation(WiremockStubs.class)));

    var stubIds = new ArrayList<UUID>();
    var wireMockAdminClient = getWireMockAdminClient();
    context.getStore(NAMESPACE).put(getStubIdsKey(context), stubIds);
    for (var stubPath : stubFilePaths) {
      var stubId = UUID.randomUUID();
      var jsonString = readJsonString(stubPath);
      var stubMapping = StubMapping.buildFrom(jsonString);
      stubMapping.setId(stubId);
      wireMockAdminClient.addStubMapping(stubMapping);
      stubIds.add(stubId);
    }
  }

  @Override
  public void afterEach(ExtensionContext context) {
    var store = context.getStore(NAMESPACE);
    var wireMockAdminClient = getWireMockAdminClient();

    var unmatchedServeEvents = wireMockAdminClient.getServeEvents(ALL_UNMATCHED);
    var unmatchedRequestValues = unmatchedServeEvents.getServeEvents().stream()
        .map(event -> getUnmatchedRequestString(event.getRequest()))
        .toList();

    wireMockAdminClient.resetAll();
    store.remove(getStubIdsKey(context));
    Assertions.assertTrue(unmatchedRequestValues.isEmpty(),
        "Should be 0 unmatched requests, found unmatched requests: " + unmatchedRequestValues);
  }

  private static String getStubIdsKey(ExtensionContext context) {
    return context.getRequiredTestMethod().getName() + "-" + STUB_IDS;
  }

  @SneakyThrows
  private String readJsonString(String path) {
    var url = this.getClass().getClassLoader().getResource(path);
    if (url == null) {
      throw new IllegalArgumentException("File not found: " + path);
    }

    return Files.readString(Paths.get(url.toURI()));
  }

  private String getUnmatchedRequestString(LoggedRequest request) {
    var jsonObject = new JsonObject();
    jsonObject.put("method", request.getMethod());
    jsonObject.put("path", URI.create(request.getUrl()).getPath());
    jsonObject.put("queryParams", request.getQueryParams().toString());
    jsonObject.put("requestBody", request.getBodyAsString());
    return jsonObject.encode();
  }

  private static List<String> getStubPathsSafe(WiremockStubs stubs) {
    return Optional.ofNullable(stubs)
        .map(WiremockStubs::value)
        .stream()
        .flatMap(Arrays::stream)
        .distinct()
        .toList();
  }
}
