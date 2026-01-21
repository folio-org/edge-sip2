package org.folio.edge.sip2.support.wiremock;

import static com.github.tomakehurst.wiremock.admin.model.ServeEventQuery.ALL_UNMATCHED;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.folio.edge.sip2.support.wiremock.WiremockContainerExtension.getWireMockAdminClient;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

public class WiremockStubExtension implements
    BeforeEachCallback, AfterEachCallback, BeforeAllCallback, AfterAllCallback {

  private static final String STUB_IDS = "stubIds";
  private static final Namespace NAMESPACE = Namespace.create(WiremockStubExtension.class);
  private static final ObjectMapper MAPPER = new ObjectMapper()
      .setSerializationInclusion(JsonInclude.Include.NON_NULL)
      .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  private static final ClassLoader CLASS_LOADER = WiremockStubExtension.class.getClassLoader();

  @Override
  public void afterAll(ExtensionContext context) {
    resetWiremockStubs();
  }

  @Override
  public void beforeAll(ExtensionContext context) {
    resetWiremockStubs();
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    var testMethod = context.getRequiredTestMethod();
    var testClass = context.getRequiredTestClass();

    var stubFilePaths = new LinkedHashSet<String>();
    stubFilePaths.addAll(getStubPathsSafe(testClass.getAnnotation(WiremockStubs.class)));
    stubFilePaths.addAll(getStubPathsSafe(testMethod.getAnnotation(WiremockStubs.class)));

    var stubIds = new ArrayList<StubMappingRepr>();
    context.getStore(NAMESPACE).put(getStubIdsKey(context), stubIds);
    for (@Language("file-reference") var stubPath : stubFilePaths) {
      stubIds.addAll(addStubMappings(stubPath));
    }
  }

  @Override
  public void afterEach(ExtensionContext context) {
    var store = context.getStore(NAMESPACE);
    //noinspection unchecked
    var definedStubMappings = (List<StubMappingRepr>) store.get(getStubIdsKey(context));
    var unmatchedRequestValues = findUnmatchedRequests();

    if (definedStubMappings == null || definedStubMappings.isEmpty()) {
      store.remove(getStubIdsKey(context));
      resetWiremockStubs();

      validateUnmatchedRequests(unmatchedRequestValues);
      return;
    }

    final var unusedStubs = findUnusedStubs(definedStubMappings);
    store.remove(getStubIdsKey(context));
    resetWiremockStubs();

    validateUnmatchedRequests(unmatchedRequestValues);
    validateUnusedStubMappings(unusedStubs);
  }

  /**
   * Resets all WireMock stubs.
   */
  public static void resetWiremockStubs() {
    getWireMockAdminClient().resetAll();
  }

  /**
   * Adds WireMock stub mappings from the specified file paths.
   *
   * @param paths the file paths to the stub mapping JSON files
   */
  public static void addStubMappings(@Language("file-reference") String... paths) {
    for (@Language("file-reference") var path : paths) {
      addStubMappings(path);
    }
  }

  /**
   * Adds WireMock stub mappings from the specified file path.
   *
   * @param path the file path to the stub mapping JSON file
   * @return the list of added stub mapping representations
   */
  public static List<StubMappingRepr> addStubMappings(@Language("file-reference") String path) {
    if (StringUtils.isBlank(path)) {
      throw new IllegalArgumentException("Path must not be empty");
    }

    // Support both "classpath:" prefixed and plain paths
    var classpathPrefix = "classpath:";
    var resourcePath = StringUtils.startsWith(path, classpathPrefix)
        ? path.substring(classpathPrefix.length())
        : path;

    var wireMockAdminClient = getWireMockAdminClient();
    var jsonString = readJsonString(resourcePath);
    var stubMappings = parseStubMappings(jsonString);
    var stubIds = new ArrayList<StubMappingRepr>();
    for (var stubMapping : stubMappings) {
      var stubId = UUID.randomUUID();
      stubMapping.setId(stubId);
      stubIds.add(new StubMappingRepr(stubId, getStubMappingStringRepresentation(stubMapping)));
      wireMockAdminClient.addStubMapping(stubMapping);
    }

    return stubIds;
  }

  private static String getStubIdsKey(ExtensionContext context) {
    return context.getRequiredTestMethod().getName() + "-" + STUB_IDS;
  }

  @SneakyThrows
  private static String readJsonString(String path) {
    var updatedPath = path.startsWith("/") ? path.substring(1) : path;
    try (var stream = CLASS_LOADER.getResourceAsStream(updatedPath)) {
      assertNotNull(stream, "Resource not found: " + updatedPath);
      return new String(stream.readAllBytes());
    }
  }

  @SneakyThrows
  private static List<StubMapping> parseStubMappings(String jsonString) {
    var jsonNode = MAPPER.readTree(jsonString);

    var mappingsKey = "mappings";
    var mappingNode = jsonNode.path(mappingsKey);
    if (mappingNode instanceof ArrayNode arrMappingNode) {
      return StreamSupport.stream(arrMappingNode.spliterator(), false)
          .map(arrayValue -> StubMapping.buildFrom(arrayValue.toString()))
          .toList();
    }

    return List.of(StubMapping.buildFrom(jsonString));
  }

  @SneakyThrows
  private static String getServeEventRequestString(LoggedRequest request) {
    var map = new LinkedHashMap<>();
    map.put("method", request.getMethod().getName());
    map.put("path", URI.create(request.getUrl()).getPath());
    map.put("queryParams", request.getQueryParams().toString());
    map.put("requestBody", trimToNull(request.getBodyAsString()));
    return MAPPER.writeValueAsString(map);
  }

  @SneakyThrows
  private static String getStubMappingStringRepresentation(StubMapping stubMapping) {
    var request = stubMapping.getRequest();
    var map = new HashMap<String, Object>();
    map.put("method", request.getMethod().getName());
    map.put("url", request.getUrl());
    map.put("urlPath", request.getUrlPath());
    map.put("urlPattern", request.getUrlPattern());
    map.put("urlPathTemplate", request.getUrlPathTemplate());
    return MAPPER.writeValueAsString(map);
  }

  private static List<String> getStubPathsSafe(WiremockStubs stubs) {
    return Optional.ofNullable(stubs)
        .map(WiremockStubs::value)
        .stream()
        .flatMap(Arrays::stream)
        .distinct()
        .toList();
  }

  private static List<String> findUnusedStubs(List<StubMappingRepr> definedStubMappings) {
    if (definedStubMappings == null || definedStubMappings.isEmpty()) {
      return emptyList();
    }

    var allServeEvents = getWireMockAdminClient().getServeEvents();
    var matchedStubEventsIds = allServeEvents.getServeEvents().stream()
        .map(ServeEvent::getStubMapping)
        .map(StubMapping::getId)
        .filter(Objects::nonNull)
        .toList();

    return definedStubMappings.stream()
        .filter(mapping -> !matchedStubEventsIds.contains(mapping.id()))
        .map(StubMappingRepr::representation)
        .toList();
  }

  private static List<String> findUnmatchedRequests() {
    var unmatchedServeEvents = getWireMockAdminClient().getServeEvents(ALL_UNMATCHED);
    return unmatchedServeEvents.getServeEvents().stream()
        .map(event -> getServeEventRequestString(event.getRequest()))
        .toList();
  }

  private static void validateUnusedStubMappings(List<String> unusedStubs) {
    assertTrue(unusedStubs.isEmpty(), "The following stubs were never called:\n"
        + formatRepresentation(unusedStubs));
  }

  private static void validateUnmatchedRequests(List<String> unmatchedRequestValues) {
    assertTrue(unmatchedRequestValues.isEmpty(), "Should be 0 unmatched requests:\n"
        + formatRepresentation(unmatchedRequestValues));
  }

  private static String formatRepresentation(List<String> unmatchedStubRepresentations) {
    return unmatchedStubRepresentations.stream()
        .map(" => %s"::formatted)
        .collect(Collectors.joining("\n", "[\n", "\n]"));
  }

  public record StubMappingRepr(UUID id, String representation) {
  }
}
