package org.folio.edge.sip2.repositories;

import static io.vertx.ext.web.client.predicate.ResponsePredicate.SC_SUCCESS;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ErrorConverter;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.ext.web.codec.BodyCodec;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.Sip2LogAdapter;

/**
 * Resource provider for communicating with FOLIO.
 *
 * @author mreno-EBSCO
 *
 */
public class FolioResourceProvider implements IResourceProvider<IRequestData> {
  private static final String HEADER_X_OKAPI_TOKEN = "x-okapi-token";
  private static final String HEADER_X_OKAPI_TENANT = "x-okapi-tenant";
  private static final String HEADER_X_OKAPI_REQUEST_ID = "x-okapi-request-id";
  private static final Sip2LogAdapter log = Sip2LogAdapter.getLogger(FolioResourceProvider.class);

  private final String okapiUrl;
  private final WebClient client;
  private final LoginRepository loginRepository;

  /**
   * Construct a FOLIO resource provider with the specified parameters.
   * @param okapiUrl the URL for okapi
   * @param webClient the WebClient instance
   */
  @Inject
  public FolioResourceProvider(
      LoginRepository loginRepository,
      @Named("okapiUrl") String okapiUrl,
      @Named("webClient") WebClient webClient) {
    this.loginRepository = loginRepository;
    this.okapiUrl = okapiUrl;
    this.client = webClient;
  }

  @Override
  public Future<IResource> retrieveResource(IRequestData requestData) {
    var sessionData = requestData.getSessionData();
    log.debug(sessionData, "retrieve resource {}", requestData::getPath);

    final HttpRequest<Buffer> request =
        client.getAbs(okapiUrl + requestData.getPath());

    return Future.<Void>future(promise -> setHeaders(requestData.getHeaders(), request,
      Objects.requireNonNull(requestData.getSessionData(),
        "SessionData cannot be null"), promise)).compose(v -> request
        .expect(ResponsePredicate.create(ResponsePredicate.SC_OK, getErrorConverter(sessionData)))
        // Some APIs return application/json, some return with the charset
        // parameter (e.g. circulation). So we can't use the built-in JSON
        // predicate here.
        .expect(ResponsePredicate.contentType(Arrays.asList(
          "application/json",
          "application/json; charset=utf-8")))
        .as(BodyCodec.jsonObject())
        .send()
        .map(response -> toIResource(sessionData, response))
      ).recover(
        e -> {
          if (e instanceof IllegalStateException) {
            // This is a common error when the headers are not set correctly
            sessionData.setErrorResponseMessage("Headers not set correctly: " + e.getMessage());
          } else {
            sessionData.setErrorResponseMessage("Failed to retrieve resource: " + e.getMessage());
          }
          return Future.failedFuture(e);
        });
  }

  /**
   * Verify a pin.
   *
   * @param requestData The request data with the path and payload
   * @return Boolean True if successful
   */
  public Future<Boolean> doPinCheck(IRequestData requestData) {
    var sessionData = requestData.getSessionData();
    log.debug(sessionData, "Doing pin verification at {}", requestData::getPath);

    final HttpRequest<Buffer> request =
        client.postAbs(okapiUrl + requestData.getPath());

    return Future.<Void>future(promise -> setHeaders(
        requestData.getHeaders(), request, requestData.getSessionData(),
        promise))
      .compose(v -> request
        .expect(ResponsePredicate.create(SC_SUCCESS, getErrorConverter(sessionData)))
        .as(BodyCodec.jsonObject())
        .sendJsonObject(requestData.getBody())
        .map(Boolean.TRUE)
        .onFailure(e -> log.error(sessionData, "Pin check failed", e)));
  }

  @Override
  public Future<IResource> createResource(IRequestData requestData) {
    var sessionData = requestData.getSessionData();
    log.debug(sessionData, "Create resource {}, body: {}",
        requestData::getPath,
        () -> requestData.getBody().encode());

    final HttpRequest<Buffer> request =
        client.postAbs(okapiUrl + requestData.getPath());

    return Future.<Void>future(promise -> setHeaders(requestData.getHeaders(),
        request, requestData.getSessionData(), promise))
      .compose(v -> request
        .expect(ResponsePredicate.create(SC_SUCCESS, getErrorConverter(sessionData)))
        // Some APIs return application/json, some return with the charset
        // parameter (e.g. circulation). So we can't use the built-in JSON
        // predicate here.
        .expect(ResponsePredicate.contentType(Arrays.asList(
            "application/json",
            "application/json; charset=utf-8")))
        .as(BodyCodec.jsonObject())
        .sendJsonObject(requestData.getBody())
        .map(response -> toIResource(sessionData, response))
        .onFailure(e -> log.error(sessionData, "Request failed", e)));
  }

  @Override
  public Future<IResource> editResource(IRequestData fromData) {
    return Future.failedFuture(new UnsupportedOperationException("Not implemented"));
  }

  @Override
  public Future<IResource> deleteResource(IRequestData resource) {
    return Future.failedFuture(new UnsupportedOperationException("Not implemented"));
  }

  private void setHeaders(
      Map<String, String> headers,
      HttpRequest<Buffer> request,
      SessionData sessionData,
      Promise<Void> promise) {
    for (Map.Entry<String,String> entry : Optional.ofNullable(headers)
        .orElse(Collections.emptyMap()).entrySet()) {
      request.putHeader(entry.getKey(), entry.getValue());
    }

    loginRepository.getSessionAccessToken(sessionData)
        .onFailure(throwable -> {
          sessionData.setErrorResponseMessage("Access token missing.");
          promise.fail(throwable);
        })
        .onSuccess(accessToken -> {
          request.putHeader(HEADER_X_OKAPI_TOKEN, accessToken);
          request.putHeader(HEADER_X_OKAPI_TENANT, sessionData.getTenant());
          request.putHeader(HEADER_X_OKAPI_REQUEST_ID, sessionData.getRequestId());
          promise.complete();
        });
  }

  private static IResource toIResource(SessionData sessionData, HttpResponse<JsonObject> response) {
    log.info(sessionData, "FOLIO response body: {}", () -> response.body().encode());
    return new FolioResource(response.body(), response.headers());
  }

  private ErrorConverter getErrorConverter(SessionData sessionData) {
    return ErrorConverter.createFullBody(result -> {
      log.error(sessionData, "Error communicating with FOLIO: {}",
          result.response().bodyAsString());
      return new FolioRequestThrowable(result.response().bodyAsString());
    });
  }
}
