package org.folio.edge.sip2.repositories;

import static io.vertx.core.http.HttpMethod.GET;
import static io.vertx.core.http.HttpMethod.POST;
import static io.vertx.core.http.HttpResponseExpectation.SC_CREATED;
import static io.vertx.core.http.HttpResponseExpectation.SC_OK;
import static io.vertx.core.http.HttpResponseExpectation.contentType;
import static io.vertx.ext.web.codec.BodyCodec.jsonObject;

import io.vertx.core.Expectation;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpResponseExpectation;
import io.vertx.core.http.HttpResponseHead;
import io.vertx.core.http.impl.headers.HeadersMultiMap;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;
import java.util.Objects;
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
  public static final List<String> EXPECTED_CONTENT_TYPES =
      List.of("application/json", "application/json; charset=utf-8");

  private final String okapiUrl;
  private final WebClient client;
  private final LoginRepository loginRepository;

  /**
   * Construct a FOLIO resource provider with the specified parameters.
   *
   * @param okapiUrl  the URL for okapi
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
    Objects.requireNonNull(sessionData, "SessionData cannot be null");
    log.debug(sessionData, "Retrieving resource {}", requestData::getPath);

    return initHttpRequest(GET, requestData)
        .flatMap(HttpRequest::send)
        .expecting(getHttpRequestExpectations(sessionData, SC_OK))
        .map(response -> toIResource(sessionData, response))
        .recover(error -> handleErrorResponse(sessionData, error));
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
    return initHttpRequest(POST, requestData)
        .flatMap(request -> request.sendJsonObject(requestData.getBody()))
        .expecting(getHttpRequestExpectations(sessionData, SC_OK))
        .map(Boolean.TRUE)
        .onFailure(e -> log.error(sessionData, "Pin check failed", e));
  }

  @Override
  public Future<IResource> createResource(IRequestData requestData) {
    var sessionData = requestData.getSessionData();
    log.debug(sessionData, "Create resource {}, body: {}",
        requestData::getPath,
        () -> requestData.getBody().encode());

    return initHttpRequest(POST, requestData)
        .flatMap(request -> request.sendJsonObject(requestData.getBody()))
        .expecting(getHttpRequestExpectations(sessionData, SC_CREATED))
        .map(response -> toIResource(sessionData, response))
        .onFailure(error -> log.error(sessionData, "Request failed", error));
  }

  @Override
  public Future<IResource> editResource(IRequestData fromData) {
    return Future.failedFuture(new UnsupportedOperationException("Not implemented"));
  }

  @Override
  public Future<IResource> deleteResource(IRequestData resource) {
    return Future.failedFuture(new UnsupportedOperationException("Not implemented"));
  }

  private Future<HttpRequest<JsonObject>> initHttpRequest(HttpMethod method, IRequestData data) {
    var sessionData = data.getSessionData();
    if (sessionData == null) {
      return Future.failedFuture(new IllegalArgumentException("SessionData cannot be null"));
    }

    return loginRepository.getSessionAccessToken(sessionData)
        .map(accessToken -> client.requestAbs(method, okapiUrl + data.getPath())
            .as(jsonObject())
            .putHeaders(getDataHeaders(data))
            .putHeader(HEADER_X_OKAPI_TOKEN, accessToken)
            .putHeader(HEADER_X_OKAPI_TENANT, sessionData.getTenant())
            .putHeader(HEADER_X_OKAPI_REQUEST_ID, sessionData.getRequestId()));
  }

  private static IResource toIResource(SessionData sessionData, HttpResponse<JsonObject> response) {
    log.info(sessionData, "FOLIO response body: {}", () -> response.body().encode());
    return new FolioResource(response.body(), response.headers());
  }

  private static Expectation<HttpResponseHead> getHttpRequestExpectations(
      SessionData sd, HttpResponseExpectation expectedStatus) {

    return expectedStatus
        .and(contentType(EXPECTED_CONTENT_TYPES))
        .wrappingFailure((head, err) -> getHttpRequestError(sd, head, err));
  }

  private static FolioRequestThrowable getHttpRequestError(SessionData sessionData,
      HttpResponseHead responseHead, Throwable e) {
    var status = responseHead.statusCode() + " " + responseHead.statusMessage();
    log.error(sessionData, "login:: Invalid response from FOLIO '{}': {}", status, e.getMessage());
    return new FolioRequestThrowable("Failed to perform request: " + status);
  }

  private static Future<IResource> handleErrorResponse(SessionData sessionData, Throwable error) {
    if (error instanceof IllegalStateException) {
      // This is a common error when the headers are not set correctly
      sessionData.setErrorResponseMessage("Headers not set correctly: " + error.getMessage());
    } else {
      sessionData.setErrorResponseMessage("Failed to retrieve resource: " + error.getMessage());
    }
    return Future.failedFuture(error);
  }

  private static HeadersMultiMap getDataHeaders(IRequestData data) {
    var headers = HeadersMultiMap.httpHeaders();
    data.getHeaders().forEach(headers::add);
    return headers;
  }
}
