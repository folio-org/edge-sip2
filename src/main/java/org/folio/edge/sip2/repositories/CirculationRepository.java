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
import org.folio.edge.sip2.domain.messages.responses.CheckinResponse;
import org.folio.edge.sip2.session.SessionData;

/**
 * Provides interaction with the login service.
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
   * @param checkin the login domain object
   * @return the checkin response domain object
   */
  public Future<CheckinResponse> checkin(Checkin checkin, SessionData sessionData) {
    final ZonedDateTime returnDate = checkin.getReturnDate();
    final String currentLocation = checkin.getCurrentLocation();
    final String institutionId = checkin.getInstitutionId();
    final String itemIdentifier = checkin.getItemIdentifier();

    final JsonObject body = new JsonObject()
        .put("itemBarcode", itemIdentifier)
        .put("servicePointId", currentLocation)
        .put("checkInDate", DateTimeFormatter.ISO_DATE_TIME
            .withZone(ZoneOffset.UTC)
            .format(returnDate));
    Map<String, String> headers = new HashMap<>();
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
}
