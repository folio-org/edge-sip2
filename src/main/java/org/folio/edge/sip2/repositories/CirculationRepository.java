package org.folio.edge.sip2.repositories;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import java.time.Clock;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import org.folio.edge.sip2.domain.messages.requests.Checkin;
import org.folio.edge.sip2.domain.messages.requests.Checkout;
import org.folio.edge.sip2.domain.messages.responses.CheckinResponse;
import org.folio.edge.sip2.domain.messages.responses.CheckoutResponse;
import org.folio.edge.sip2.session.SessionData;

/**
 * Provides interaction with the circulation service.
 *
 * @author mreno-EBSCO
 *
 */
public class CirculationRepository {
  private final IResourceProvider<IRequestData> resourceProvider;
  private final Clock clock;

  @Inject
  CirculationRepository(IResourceProvider<IRequestData> resourceProvider, Clock clock) {
    this.resourceProvider = Objects.requireNonNull(resourceProvider,
        "Resource provider cannot be null");
    this.clock = Objects.requireNonNull(clock, "Clock cannot be null");
  }

  /**
   * Perform a checkin.
   *
   * @param checkin the checkin domain object
   * @return the checkin response domain object
   */
  public Future<CheckinResponse> checkin(Checkin checkin, SessionData sessionData) {
    // We'll need to convert this date properly. It is likely that it will not include timezone
    // information, so we'll need to use the tenant/SC timezone as the basis and convert to UTC.
    final ZonedDateTime returnDate = checkin.getReturnDate();
    final String scLocation = sessionData.getScLocation();
    final String institutionId = checkin.getInstitutionId();
    final String itemIdentifier = checkin.getItemIdentifier();

    final JsonObject body = new JsonObject()
        .put("itemBarcode", itemIdentifier)
        .put("servicePointId", scLocation)
        .put("checkInDate", DateTimeFormatter.ISO_DATE_TIME
            .withZone(ZoneOffset.UTC)
            .format(returnDate));

    final Map<String, String> headers = new HashMap<>();
    headers.put("accept", "application/json");

    final CheckinRequestData checkinRequestData =
        new CheckinRequestData(body, headers, sessionData);
    final Future<IResource> result = resourceProvider
        .createResource(checkinRequestData);

    return result
        .otherwiseEmpty()
        .compose(resource -> Future.succeededFuture(
            CheckinResponse.builder()
              .ok(resource.getResource() == null ? FALSE : TRUE)
              .resensitize(resource.getResource() == null ? FALSE : TRUE)
              .magneticMedia(null)
              .alert(FALSE)
              // Need to get the "local" time zone from somewhere
              // Using UTC for now
              .transactionDate(ZonedDateTime.now(clock))
              .institutionId(institutionId)
              .itemIdentifier(itemIdentifier)
              // this is probably not the permanent location
              // this might require a call to inventory
              .permanentLocation(
                  resource.getResource() == null ? "Unknown"
                      : resource.getResource().getJsonObject("item",
                          new JsonObject()).getJsonObject("location",
                              new JsonObject()).getString("name", "Unknown"))
              .build()));
  }

  /**
   * Perform a checkout.
   *
   * @param checkout the checkout domain object
   * @return the checkout response domain object
   */
  public Future<CheckoutResponse> checkout(Checkout checkout, SessionData sessionData) {
    final String institutionId = checkout.getInstitutionId();
    final String patronIdentifier = checkout.getPatronIdentifier();
    final String itemIdentifier = checkout.getItemIdentifier();

    final JsonObject body = new JsonObject()
        .put("itemBarcode", itemIdentifier)
        .put("userBarcode", patronIdentifier)
        .put("servicePointId", sessionData.getScLocation());

    final Map<String, String> headers = new HashMap<>();
    headers.put("accept", "application/json");

    final CheckoutRequestData checkoutRequestData =
        new CheckoutRequestData(body, headers, sessionData);
    final Future<IResource> result = resourceProvider.createResource(checkoutRequestData);

    return result
        .otherwiseEmpty()
        .compose(resource -> {
          final ZonedDateTime dueDate;
          // This is a mess. Need to clean this up. The problem here is that the checkout has
          // already succeeded, but something could be wrong with the returned data. The odds of
          // this are low, so we should be able to simplify this logic. The "dueDate" field is not
          // required, so a loan could be missing one. I am not sure what that means for SIP2
          // where the due date field is required. Setting it to null is probably wrong and will
          // likely break the template when building the SIP2 response.
          if (resource.getResource() != null) {
            final String dueDateString = resource.getResource().getString("dueDate", null);
            if (dueDateString == null) {
              dueDate = null;
            } else {
              // Need to convert to the tenant local timezone
              dueDate = ZonedDateTime.from(Utils.getFolioDateTimeFormatter().parse(dueDateString));
            }
          } else {
            dueDate = null;
          }
          return Future.succeededFuture(
            CheckoutResponse.builder()
              .ok(resource.getResource() == null ? FALSE : TRUE)
              .renewalOk(FALSE)
              .magneticMedia(null)
              .desensitize(resource.getResource() == null ? FALSE : TRUE)
              // Need to get the "local" time zone from somewhere
              // Using UTC for now
              .transactionDate(ZonedDateTime.now(clock))
              .institutionId(institutionId)
              .patronIdentifier(patronIdentifier)
              .itemIdentifier(itemIdentifier)
              .titleIdentifier(resource.getResource() == null ? "Unknown" :
                resource.getResource().getJsonObject("item",
                    new JsonObject()).getString("title", "Unknown"))
              .dueDate(dueDate)
              .build());
        });
  }

  private class CheckinRequestData implements IRequestData {
    private final JsonObject body;
    private final Map<String, String> headers;
    private final SessionData sessionData;

    private CheckinRequestData(JsonObject body, Map<String, String> headers,
        SessionData sessionData) {
      this.body = body;
      this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
      this.sessionData = sessionData;
    }

    @Override
    public String getPath() {
      return "/circulation/check-in-by-barcode";
    }

    @Override
    public Map<String, String> getHeaders() {
      return headers;
    }

    @Override
    public JsonObject getBody() {
      return body;
    }

    @Override
    public SessionData getSessionData() {
      return sessionData;
    }
  }

  private class CheckoutRequestData implements IRequestData {
    private final JsonObject body;
    private final Map<String, String> headers;
    private final SessionData sessionData;

    private CheckoutRequestData(JsonObject body, Map<String, String> headers,
        SessionData sessionData) {
      this.body = body;
      this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
      this.sessionData = sessionData;
    }

    @Override
    public String getPath() {
      return "/circulation/check-out-by-barcode";
    }

    @Override
    public Map<String, String> getHeaders() {
      return headers;
    }

    @Override
    public JsonObject getBody() {
      return body;
    }

    @Override
    public SessionData getSessionData() {
      return sessionData;
    }
  }
}
