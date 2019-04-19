package org.folio.edge.sip2.repositories;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.impl.NoStackTraceThrowable;
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
  private static final Logger log = LogManager.getLogger();

  private final String okapiUrl;
  private final WebClient client;

  /**
   * Construct a FOLIO resource provider with the specified parameters.
   * @param okapiUrl the URL for okapi
   * @param vertx the vertx instance
   */
  @Inject
  public FolioResourceProvider(
      @Named("okapiUrl") String okapiUrl,
      @Named("vertx") Vertx vertx) {
    this.okapiUrl = okapiUrl;
    this.client = WebClient.create(vertx);
  }

  @Override
  public Future<IResource> retrieveResource(IRequestData requestData) {
    final HttpRequest<Buffer> request =
        client.getAbs(okapiUrl + requestData.getPath());

    setHeaders(requestData.getHeaders(), request, requestData.getSessionData());

    final Future<IResource> future = Future.future();
    request
        .expect(ResponsePredicate.create(ResponsePredicate.SC_OK, getErrorConverter()))
        // Some APIs return application/json, some return with the charset
        // parameter (e.g. circulation). So we can't use the built-in JSON
        // predicate here.
        .expect(ResponsePredicate.contentType(Arrays.asList(
            "application/json",
            "application/json; charset=utf-8")))
        .as(BodyCodec.jsonObject())
        .send(ar -> handleResponse(future, ar));

    return future;
  }

  @Override
  public Future<IResource> createResource(IRequestData requestData) {
    final HttpRequest<Buffer> request =
        client.postAbs(okapiUrl + requestData.getPath());

    setHeaders(requestData.getHeaders(), request, requestData.getSessionData());

    final Future<IResource> future = Future.future();
    request
        .expect(ResponsePredicate.create(ResponsePredicate.SC_SUCCESS, getErrorConverter()))
        // Some APIs return application/json, some return with the charset
        // parameter (e.g. circulation). So we can't use the built-in JSON
        // predicate here.
        .expect(ResponsePredicate.contentType(Arrays.asList(
            "application/json",
            "application/json; charset=utf-8")))
        .as(BodyCodec.jsonObject())
        .sendJsonObject(requestData.getBody(),
            ar -> handleResponse(future, ar));

    return future;
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

    if (sessionData != null) {
      final String authenticationToken = sessionData.getAuthenticationToken();
      if (authenticationToken != null) {
        request.putHeader(HEADER_X_OKAPI_TOKEN, authenticationToken);
      }
      request.putHeader("x-okapi-tenant", sessionData.getTenant());
    }
  }

  private void handleResponse(
      Future<IResource> future,
      AsyncResult<HttpResponse<JsonObject>> ar) {
    if (ar.succeeded()) {
      log.debug("FOLIO response body: {}",
          () -> ar.result().body().encodePrettily());

      future.complete(new FolioResource(ar.result().body(), ar.result().headers()));
    } else {
      log.error("Creation failed", ar.cause());
      future.fail(ar.cause());
    }
  }

  private ErrorConverter getErrorConverter() {
    return ErrorConverter.createFullBody(result -> {
      log.error("Error communicating with FOLIO: {}", result.response().bodyAsString());
      return new NoStackTraceThrowable(result.message());
    });
  }
}
