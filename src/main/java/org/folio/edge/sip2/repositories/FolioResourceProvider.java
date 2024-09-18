package org.folio.edge.sip2.repositories;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.cache.TokenCacheFactory;
import org.folio.edge.sip2.session.SessionData;
import org.folio.okapi.common.refreshtoken.client.Client;
import org.folio.okapi.common.refreshtoken.client.ClientOptions;

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

  Client tokenClient;
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

    return Future.<Void>future(promise -> setHeaders(requestData.getHeaders(), request,
      Objects.requireNonNull(requestData.getSessionData(),
        "SessionData cannot be null"), promise)).compose(v -> request
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
        .onFailure(e -> log.error("Request failed", e))
    );
  }

  /**
   * Login and set the access token in session data object.
   * @param username UserName
   * @param getPasswordSupplier PasswordSupplier
   * @param sessionData session data
   * @return response
   */

  public Future<String> loginWithSupplier(
      String username,
      Supplier<Future<String>> getPasswordSupplier,
      SessionData sessionData, boolean useCache) {
    log.info("loginWithSupplier username={} cache={} useCache={}",
        username, TokenCacheFactory.get(), useCache);
    ClientOptions clientOptions = new ClientOptions()
        .okapiUrl(okapiUrl)
        .webClient(client);

    if (useCache) {
      tokenClient = Client.createLoginClient(clientOptions, TokenCacheFactory.get(),
        sessionData.getTenant(), username, getPasswordSupplier);
    } else {
      tokenClient = Client.createLoginClient(clientOptions, null,
        sessionData.getTenant(), username, getPasswordSupplier);
    }
    return tokenClient.getToken().compose(token -> {
      log.debug("The login token is {}", token);
      return Future.succeededFuture(token);
    }).onFailure(e -> {
      log.error("Unable to get the access token ", e);
      sessionData.setAuthenticationToken(null);
      sessionData.setLoginErrorMessage(e.getMessage());
    });
  }

  /**
   * Verify a pin.
   * @param requestData The request data with the path and payload
   * @return Boolean True if successful
   */
  public Future<Boolean> doPinCheck(IRequestData requestData) {
    log.info("Doing pin verification at {}", requestData::getPath);

    final HttpRequest<Buffer> request =
        client.postAbs(okapiUrl + requestData.getPath());

    return Future.<Void>future(promise -> setHeaders(
        requestData.getHeaders(), request, requestData.getSessionData(),
        promise))
      .compose(v -> request
        .expect(ResponsePredicate.create(ResponsePredicate.SC_SUCCESS, getErrorConverter()))
        .as(BodyCodec.jsonObject())
        .sendJsonObject(requestData.getBody())
        .map(Boolean.TRUE)
        .onFailure(e -> log.error("Pin check failed", e)));
  }


  @Override
  public Future<IResource> createResource(IRequestData requestData) {
    log.debug("Create resource {}, body: {}",
        requestData::getPath,
        () -> requestData.getBody().encodePrettily());

    final HttpRequest<Buffer> request =
        client.postAbs(okapiUrl + requestData.getPath());

    return Future.<Void>future(promise -> setHeaders(requestData.getHeaders(),
        request, requestData.getSessionData(), promise))
      .compose(v -> request
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
        .onFailure(e -> log.error("Request failed", e)));
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
      SessionData sessionData,
      Promise<Void> promise) {
    for (Map.Entry<String,String> entry : Optional.ofNullable(headers)
        .orElse(Collections.emptyMap()).entrySet()) {
      request.putHeader(entry.getKey(), entry.getValue());
    }

    Future<String> token = loginWithSupplier(sessionData.getUsername(),
        () -> Future.succeededFuture(sessionData.getPassword()), sessionData, true);
    token.onFailure(throwable ->
        sessionData.setErrorResponseMessage("Access token missing.")
    )
        .onSuccess(accessToken -> {
          sessionData.setErrorResponseMessage(null);
          sessionData.setAuthenticationToken(accessToken);
          request.putHeader(HEADER_X_OKAPI_TOKEN, accessToken);
          request.putHeader(HEADER_X_OKAPI_TENANT, sessionData.getTenant());
          promise.complete();
        });
  }

  private static IResource toIResource(HttpResponse<JsonObject> httpResponse) {
    log.info("FOLIO response body: {}", () -> httpResponse.body().encodePrettily());
    return new FolioResource(httpResponse.body(), httpResponse.headers());
  }

  private ErrorConverter getErrorConverter() {
    return ErrorConverter.createFullBody(result -> {
      log.error("Error communicating with FOLIO: {}", result.response().bodyAsString());
      return new FolioRequestThrowable(result.response().bodyAsString());
    });
  }
}
