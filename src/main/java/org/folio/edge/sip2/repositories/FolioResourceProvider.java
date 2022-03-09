package org.folio.edge.sip2.repositories;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ErrorConverter;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.ext.web.codec.BodyCodec;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.session.SessionData;

/**
 * Resource provider for communicating with FOLIO.
 *
 * @author mreno-EBSCO
 *
 */
public class FolioResourceProvider implements IResourceProvider<IRequestData> {
  private static final String HEADER_X_OKAPI_TOKEN = "x-okapi-token";
  private static final String HEADER_X_OKAPI_TENANT = "x-okapi-tenant";
  private static final Logger log = LogManager.getLogger();

  private final String okapiUrl;
  private final WebClient client;

  /**
   * Construct a FOLIO resource provider with the specified parameters.
   * @param okapiUrl the URL for okapi
   * @param webClient the WebClient instance
   */
  @Inject
  public FolioResourceProvider(
      @Named("okapiUrl") String okapiUrl,
      @Named("webClient") WebClient webClient) {
    this.okapiUrl = okapiUrl;
    this.client = webClient;
  }

  @Override
  public Future<IResource> retrieveResource(IRequestData requestData) {
    log.debug("retrieve resource {}", requestData::getPath);

    final HttpRequest<Buffer> request =
        client.getAbs(okapiUrl + requestData.getPath());

    setHeaders(requestData.getHeaders(), request,
        Objects.requireNonNull(requestData.getSessionData(), "SessionData cannot be null"));

    return request
        .expect(ResponsePredicate.create(ResponsePredicate.SC_OK, getErrorConverter()))
        // Some APIs return application/json, some return with the charset
        // parameter (e.g. circulation). So we can't use the built-in JSON
        // predicate here.
        .expect(ResponsePredicate.contentType(Arrays.asList(
            "application/json",
            "application/json; charset=utf-8")))
        .as(BodyCodec.jsonObject())
        .send()
        .map(FolioResourceProvider::toIResource)
        .onFailure(e -> log.error("Request failed", e));
  }

  @Override
  public Future<IResource> createResource(IRequestData requestData) {
    log.debug("Create resource {}, body: {}",
        requestData::getPath,
        () -> requestData.getBody().encodePrettily());

    final HttpRequest<Buffer> request =
        client.postAbs(okapiUrl + requestData.getPath());

    setHeaders(requestData.getHeaders(), request, requestData.getSessionData());

    return request
        .expect(ResponsePredicate.create(ResponsePredicate.SC_SUCCESS, getErrorConverter()))
        // Some APIs return application/json, some return with the charset
        // parameter (e.g. circulation). So we can't use the built-in JSON
        // predicate here.
        .expect(ResponsePredicate.contentType(Arrays.asList(
            "application/json",
            "application/json; charset=utf-8")))
        .as(BodyCodec.jsonObject())
        .sendJsonObject(requestData.getBody())
        .map(FolioResourceProvider::toIResource)
        .onFailure(e -> log.error("Request failed", e));
  }

  @Override
  public Future<IResource> editResource(IRequestData fromData) {
    return null;
  }

  @Override
  public Future<IResource> deleteResource(IRequestData resource) {
    return null;
  }

  private void setHeaders(
      Map<String, String> headers,
      HttpRequest<Buffer> request,
      SessionData sessionData) {
    for (Map.Entry<String,String> entry : Optional.ofNullable(headers)
        .orElse(Collections.emptyMap()).entrySet()) {
      request.putHeader(entry.getKey(), entry.getValue());
    }

    final String authenticationToken = sessionData.getAuthenticationToken();
    if (authenticationToken != null) {
      log.debug(HEADER_X_OKAPI_TOKEN + ": {}", authenticationToken);
      request.putHeader(HEADER_X_OKAPI_TOKEN, authenticationToken);
    }

    log.debug(HEADER_X_OKAPI_TENANT + ": {}", sessionData.getTenant());
    request.putHeader(HEADER_X_OKAPI_TENANT, sessionData.getTenant());
  }

  private static IResource toIResource(HttpResponse<JsonObject> httpResponse) {
    log.debug("FOLIO response body: {}", () -> httpResponse.body().encodePrettily());
    return new FolioResource(httpResponse.body(), httpResponse.headers());
  }

  private ErrorConverter getErrorConverter() {
    return ErrorConverter.createFullBody(result -> {
      log.error("Error communicating with FOLIO: {}", result.response().bodyAsString());
      return new FolioRequestThrowable(result.response().bodyAsString());
    });
  }
}
