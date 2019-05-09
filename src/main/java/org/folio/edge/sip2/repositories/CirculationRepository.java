package org.folio.edge.sip2.repositories;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.folio.edge.sip2.utils.JsonUtils.getChildString;
import static org.folio.edge.sip2.utils.JsonUtils.getSubChildString;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
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
public class CirculationRepository extends AbstractRepository {
  // Should consider letting the template take care of required fields with missing values
  private static final String UNKNOWN = "";
  private final IResourceProvider<IRequestData> resourceProvider;

  @Inject
  CirculationRepository(IResourceProvider<IRequestData> resourceProvider, Clock clock) {
    super(clock);
    this.resourceProvider = Objects.requireNonNull(resourceProvider,
        "Resource provider cannot be null");
  }

  /**
   * Perform a checkin.
   *
   * @param checkin the checkin domain object
   * @return the checkin response domain object
   */
  public Future<CheckinResponse> performCheckinCommand(Checkin checkin, SessionData sessionData) {
    // We'll need to convert this date properly. It is likely that it will not include timezone
    // information, so we'll need to use the tenant/SC timezone as the basis and convert to UTC.
    final OffsetDateTime returnDate = checkin.getReturnDate();
    final String scLocation = sessionData.getScLocation();
    final String institutionId = checkin.getInstitutionId();
    final String itemIdentifier = checkin.getItemIdentifier();

    final JsonObject body = new JsonObject()
        .put("itemBarcode", itemIdentifier)
        .put("servicePointId", scLocation)
        .put("checkInDate", DateTimeFormatter.ISO_DATE_TIME
            .withZone(ZoneId.of(sessionData.getTimeZone()))
            .format(returnDate));

    final Map<String, String> headers = getBaseHeaders();

    final CheckinRequestData checkinRequestData =
        new CheckinRequestData(body, headers, sessionData);
    final Future<IResource> result = resourceProvider
        .createResource(checkinRequestData);

    return result
        .otherwise(() -> null)
        .compose(resource -> Future.succeededFuture(
            CheckinResponse.builder()
              .ok(resource.getResource() == null ? FALSE : TRUE)
              .resensitize(resource.getResource() == null ? FALSE : TRUE)
              .magneticMedia(null)
              .alert(FALSE)
              .transactionDate(getTransactionTimestamp(sessionData.getTimeZone()))
              .institutionId(institutionId)
              .itemIdentifier(itemIdentifier)
              // this is probably not the permanent location
              // this might require a call to inventory
              .permanentLocation(
                  resource.getResource() == null ? UNKNOWN
                      : getSubChildString(resource.getResource(),
                          Arrays.asList("item", "location"), "name", UNKNOWN))
              .build()));
  }

  /**
   * Perform a checkout.
   *
   * @param checkout the checkout domain object
   * @return the checkout response domain object
   */
  public Future<CheckoutResponse> performCheckoutCommand(Checkout checkout,
      SessionData sessionData) {
    final String institutionId = checkout.getInstitutionId();
    final String patronIdentifier = checkout.getPatronIdentifier();
    final String itemIdentifier = checkout.getItemIdentifier();

    final JsonObject body = new JsonObject()
        .put("itemBarcode", itemIdentifier)
        .put("userBarcode", patronIdentifier)
        .put("servicePointId", sessionData.getScLocation());

    final Map<String, String> headers = getBaseHeaders();

    final CheckoutRequestData checkoutRequestData =
        new CheckoutRequestData(body, headers, sessionData);
    final Future<IResource> result = resourceProvider.createResource(checkoutRequestData);

    return result
        .otherwise(Utils::handleErrors)
        .compose(resource -> {
          final OffsetDateTime dueDate;
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
              dueDate = Utils.convertDateTime(OffsetDateTime.from(Utils.getFolioDateTimeFormatter().parse(dueDateString)),
                                              sessionData.getTimeZone());
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
              .transactionDate(getTransactionTimestamp(sessionData.getTimeZone()))
              .institutionId(institutionId)
              .patronIdentifier(patronIdentifier)
              .itemIdentifier(itemIdentifier)
              .titleIdentifier(resource.getResource() == null ? UNKNOWN
                  : getChildString(resource.getResource(), "item", "title", UNKNOWN))
              .dueDate(dueDate)
              .screenMessage(resource.getResource() == null ? resource.getErrorMessages() : null)
              .build());
        });
  }

  /**
   * Get requests for the specified patron.
   *
   * @param userId the FOLIO ID of the patron
   * @param requestType The request type to filter on
   * @param startItem the first item to return
   * @param endItem the last item to return
   * @param sessionData session data
   * @return the requests the patron has placed
   */
  public Future<JsonObject> getRequestsByUserId(String userId, String requestType,
      Integer startItem, Integer endItem, SessionData sessionData) {
    final Map<String, String> headers = getBaseHeaders();

    final RequestsRequestData requestsRequestData = new RequestsRequestData("requesterId", userId,
        requestType, startItem, endItem, headers, sessionData);
    final Future<IResource> result = resourceProvider.retrieveResource(requestsRequestData);

    return result.otherwise(() -> null).map(IResource::getResource);
  }

  /**
   * Gets open requests for a specific item.
   *
   * @param itemId the UUID of the item
   * @param requestType the request type (can be null)
   * @param startItem the start item (can be null)
   * @param endItem the end item (can be null)
   * @param sessionData the session data
   * @return a list of open requests for the specified item
   */
  public Future<JsonObject> getRequestsByItemId(String itemId, String requestType,
      Integer startItem, Integer endItem, SessionData sessionData) {
    final Map<String, String> headers = getBaseHeaders();

    final RequestsRequestData requestsRequestData = new RequestsRequestData("itemId", itemId,
        requestType, startItem, endItem, headers, sessionData);
    final Future<IResource> result = resourceProvider.retrieveResource(requestsRequestData);

    return result.otherwise(() -> null).map(IResource::getResource);
  }

  /**
   * Get loans for the specified patron.
   *
   * @param userId the FOLIO ID of the patron
   * @param startItem the first item to return
   * @param endItem the last item to return
   * @param sessionData session data
   * @return the loans the patron has open
   */
  public Future<JsonObject> getLoansByUserId(String userId, Integer startItem, Integer endItem,
      SessionData sessionData) {
    final Map<String, String> headers = getBaseHeaders();

    final LoansRequestData loansRequestData =
        new LoansRequestData(userId, startItem, endItem, headers, sessionData);
    final Future<IResource> result = resourceProvider.retrieveResource(loansRequestData);

    return result
        .otherwise(() -> null)
        .map(IResource::getResource);
  }

  /**
   * Returns a list of over due items that the patron has on loan.
   *
   * @param userId the patron's user ID
   * @param dueDate the date and time (UTC) that items are considered over due
   * @param startItem the first item to return
   * @param endItem the last item to return
   * @param sessionData session info
   * @return the list of over due items for this patron
   */
  public Future<JsonObject> getOverdueLoansByUserId(String userId, OffsetDateTime dueDate,
      Integer startItem, Integer endItem, SessionData sessionData) {
    final Map<String, String> headers = getBaseHeaders();

    final OverdueLoansRequestData loansRequestData =
        new OverdueLoansRequestData(userId, dueDate, startItem, endItem, headers, sessionData);
    final Future<IResource> result = resourceProvider.retrieveResource(loansRequestData);

    return result
        .otherwise(() -> null)
        .map(IResource::getResource);
  }

  private Map<String, String> getBaseHeaders() {
    final Map<String, String> headers = new HashMap<>();
    headers.put("accept", "application/json");
    return headers;
  }

  private abstract class CirculationRequestData implements IRequestData {
    private final JsonObject body;
    private final Integer startItem;
    private final Integer endItem;
    private final Map<String, String> headers;
    private final SessionData sessionData;

    private CirculationRequestData(
        JsonObject body,
        Integer startItem,
        Integer endItem,
        Map<String, String> headers,
        SessionData sessionData) {
      this.body = body;
      this.startItem = startItem;
      this.endItem = endItem;
      this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
      this.sessionData = sessionData;
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

    protected StringBuilder appendLimits(StringBuilder sb) {
      final int offset;
      if (startItem != null) {
        offset = startItem.intValue() - 1; // expects a 1-based count, FOLIO is 0
        sb.append("&offset=")
          .append(offset);
      } else {
        offset = 0;
      }

      if (endItem != null) {
        final int limit = endItem.intValue() - offset;
        sb.append("&limit=")
          .append(limit);
      }

      return sb;
    }
  }

  private class CheckinRequestData extends CirculationRequestData {
    private CheckinRequestData(JsonObject body, Map<String, String> headers,
        SessionData sessionData) {
      super(body, null, null, headers, sessionData);
    }

    @Override
    public String getPath() {
      return "/circulation/check-in-by-barcode";
    }
  }

  private class CheckoutRequestData extends CirculationRequestData {
    private CheckoutRequestData(JsonObject body, Map<String, String> headers,
        SessionData sessionData) {
      super(body, null, null, headers, sessionData);
    }

    @Override
    public String getPath() {
      return "/circulation/check-out-by-barcode";
    }
  }

  private class RequestsRequestData extends CirculationRequestData {
    private final String idField;
    private final String idValue;
    private final String requestType;

    private RequestsRequestData(
        String idField,
        String idValue,
        String requestType,
        Integer startItem,
        Integer endItem,
        Map<String, String> headers,
        SessionData sessionData) {
      super(null, startItem, endItem, headers, sessionData);
      this.idField = idField;
      this.idValue = idValue;
      this.requestType = requestType;
    }

    @Override
    public String getPath() {
      final StringBuilder sb = new StringBuilder()
          .append("/circulation/requests?query=(")
          .append(idField)
          .append("==")
          .append(idValue)
          .append(" and status=Open");
      if (requestType != null) {
        sb.append(" and requestType==")
          .append(requestType);
      }
      sb.append(')');

      return appendLimits(sb).toString();
    }
  }

  private class LoansRequestData extends CirculationRequestData {
    private final String userId;

    private LoansRequestData(
        String userId,
        Integer startItem,
        Integer endItem,
        Map<String, String> headers,
        SessionData sessionData) {
      super(null, startItem, endItem, headers, sessionData);
      this.userId = userId;
    }

    @Override
    public String getPath() {
      return "/circulation/loans?query=(userId==" + userId
          + " and status.name=Open)";
    }
  }

  private class OverdueLoansRequestData extends CirculationRequestData {
    private final String userId;
    private final OffsetDateTime dueDate;

    private OverdueLoansRequestData(
        String userId,
        OffsetDateTime dueDate,
        Integer startItem,
        Integer endItem,
        Map<String, String> headers,
        SessionData sessionData) {
      super(null, startItem, endItem, headers, sessionData);
      this.userId = userId;
      this.dueDate = dueDate;
    }

    @Override
    public String getPath() {
      final StringBuilder sb = new StringBuilder()
          .append("/circulation/loans?query=(userId==")
          .append(userId)
          .append(" and status.name=Open and dueDate<")
          .append(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(dueDate))
          .append(')');
      return appendLimits(sb).toString();
    }
  }
}
