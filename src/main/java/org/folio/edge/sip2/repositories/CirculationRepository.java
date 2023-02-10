package org.folio.edge.sip2.repositories;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.folio.edge.sip2.utils.JsonUtils.getChildString;
import static org.folio.edge.sip2.utils.JsonUtils.getSubChildString;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.requests.Checkin;
import org.folio.edge.sip2.domain.messages.requests.Checkout;
import org.folio.edge.sip2.domain.messages.responses.CheckinResponse;
import org.folio.edge.sip2.domain.messages.responses.CheckoutResponse;
import org.folio.edge.sip2.repositories.domain.User;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.Utils;

/**
 * Provides interaction with the circulation service.
 *
 * @author mreno-EBSCO
 *
 */
public class CirculationRepository {
  // Should consider letting the template take care of required fields with missing values
  private static final Logger log = LogManager.getLogger();

  private static final String UNKNOWN = "";
  public static final String TITLE_NOT_FOUND = "TITLE NOT FOUND";
  public static final String TITLE = "title";
  private final IResourceProvider<IRequestData> resourceProvider;
  private final PasswordVerifier passwordVerifier;
  private final Clock clock;


  @Inject
  CirculationRepository(IResourceProvider<IRequestData> resourceProvider,
      PasswordVerifier passwordVerifier, Clock clock) {
    this.resourceProvider = Objects.requireNonNull(resourceProvider,
        "Resource provider cannot be null");
    this.passwordVerifier = Objects.requireNonNull(passwordVerifier,
        "Password verifier cannot be null");
    this.clock = Objects.requireNonNull(clock, "Clock cannot be null");
  }

  /**
   * Perform a checkin.
   *
   * @param checkin the checkin domain object
   * @return the checkin response domain object
   */
  public Future<CheckinResponse> performCheckinCommand(Checkin checkin, SessionData sessionData) {
    log.debug("performCheckinCommand checkin:{}",checkin);
    // We'll need to convert this date properly. It is likely that it will not include timezone
    // information, so we'll need to use the tenant/SC timezone as the basis and convert to UTC.
    final OffsetDateTime returnDate = checkin.getReturnDate();
    final String scLocation = sessionData.getScLocation();
    final String institutionId = checkin.getInstitutionId();
    final String itemIdentifier = checkin.getItemIdentifier();

    final JsonObject body = new JsonObject()
        .put("itemBarcode", itemIdentifier)
        .put("servicePointId", scLocation)
        .put("checkInDate", returnDate
            .withOffsetSameInstant(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

    final Map<String, String> headers = getBaseHeaders();

    final CheckinRequestData checkinRequestData =
        new CheckinRequestData(body, headers, sessionData);
    final Future<IResource> result = resourceProvider
        .createResource(checkinRequestData);

    return result
        .otherwise(() -> null)
        .compose(resource -> {
          log.info("performCheckinCommand resource:{}",resource);
          return Future.succeededFuture(
            CheckinResponse.builder()
              .ok(resource.getResource() == null ? FALSE : TRUE)
              .resensitize(resource.getResource() == null ? FALSE : TRUE)
              .magneticMedia(null)
              .alert(FALSE)
              .transactionDate(OffsetDateTime.now(clock))
              .institutionId(institutionId)
              .itemIdentifier(itemIdentifier)
              // if the title is not available, use the item identifier passed in to the checkin.
              // this allows the kiosk to show something related to the item that could be used
              // by the patron to identify which item this checkin response applies to.
              .titleIdentifier(resource.getResource() == null ? itemIdentifier
                  : getChildString(resource.getResource(), "item", TITLE, itemIdentifier))
              // this is probably not the permanent location
              // this might require a call to inventory
              .permanentLocation(
                  resource.getResource() == null ? UNKNOWN
                      : getSubChildString(resource.getResource(),
                          Arrays.asList("item", "location"), "name", UNKNOWN))
              .build());
              }
          );
  }

  /**
   * Perform a checkout.
   *
   * @param checkout the checkout domain object
   * @return the checkout response domain object
   */
  public Future<CheckoutResponse> performCheckoutCommand(Checkout checkout,
                                                         SessionData sessionData) {
    log.debug("performCheckoutCommand checkout:{} sessionData:{}",checkout,sessionData);
    final String institutionId = checkout.getInstitutionId();
    final String patronIdentifier = checkout.getPatronIdentifier();
    final String itemIdentifier = checkout.getItemIdentifier();
    final String patronPassword = checkout.getPatronPassword();

    return passwordVerifier.verifyPatronPassword(patronIdentifier, patronPassword, sessionData)
      .compose(verification -> {
        log.info("performCheckoutCommand verification:{}",verification);
        if (FALSE.equals(verification.getPasswordVerified())) {
          return Future.succeededFuture(CheckoutResponse.builder()
            .ok(FALSE)
            .renewalOk(FALSE)
            .magneticMedia(null)
            .desensitize(FALSE)
            .transactionDate(OffsetDateTime.now(clock))
            .institutionId(institutionId)
            .patronIdentifier(patronIdentifier)
            .itemIdentifier(itemIdentifier)
            .titleIdentifier(UNKNOWN)
            .dueDate(OffsetDateTime.now(clock))
            .screenMessage(verification.getErrorMessages())
            .build());
        }

        final User user = verification.getUser();
        final JsonObject body = new JsonObject()
            .put("itemBarcode", itemIdentifier)
            .put("userBarcode", user != null ? user.getBarcode() : patronIdentifier)
            .put("servicePointId", sessionData.getScLocation());

        final Map<String, String> headers = getBaseHeaders();

        final CheckoutRequestData checkoutRequestData =
            new CheckoutRequestData(body, headers, sessionData);

        final Future<IResource> result = resourceProvider.createResource(checkoutRequestData);

        return result
          .otherwise(Utils::handleErrors)
          .compose(res -> addTitleIfNotFound(sessionData, itemIdentifier, res))
          .map(resource -> {
            log.debug("performCheckoutCommand resource:{}",resource.getResource());
            final Optional<JsonObject> response = Optional.ofNullable(resource.getResource());

            final OffsetDateTime dueDate = response
                .map(v -> v.getString("dueDate"))
                .map(v -> OffsetDateTime.from(Utils.getFolioDateTimeFormatter().parse(v)))
                .orElse(OffsetDateTime.now(clock));

            return CheckoutResponse.builder()
              .ok(Boolean.valueOf(response.isPresent()))
              .renewalOk(FALSE)
              .magneticMedia(null)
              .desensitize(Boolean.valueOf(response.isPresent()))
              .transactionDate(OffsetDateTime.now(clock))
              .institutionId(institutionId)
              .patronIdentifier(patronIdentifier)
              .itemIdentifier(itemIdentifier)
              .titleIdentifier(response.map(
                  v -> getChildString(v, "item", TITLE, UNKNOWN))
                .orElse(resource.getTitle()))
              .dueDate(dueDate)
              .screenMessage(Optional.of(resource.getErrorMessages())
                .filter(v -> !v.isEmpty())
                .orElse(null))
              .build();
          });
      });
  }

  private Future<IResource> addTitleIfNotFound(SessionData sessionData,
                                               String itemIdentifier, IResource circRes) {
    if (circRes.getErrorMessages().isEmpty()) {
      return Future.succeededFuture(circRes);
    }
    return getTitle(itemIdentifier, sessionData, circRes.getErrorMessages());
  }

  private IResource getiResourceFromTitle(String title, List<String> circErrorMessages) {
    return new IResource() {
      @Override
      public JsonObject getResource() {
        return null;
      }

      @Override
      public String getTitle() {
        return title;
      }

      @Override
      public List<String> getErrorMessages() {
        List<String> tempError = new ArrayList<>(circErrorMessages);
        if (TITLE_NOT_FOUND.equals(title)) {
          tempError.add("Title Not Found");
          return tempError;
        }
        return tempError;
      }
    };
  }

  private Future<IResource> getTitle(String itemIdentifier, SessionData sessionData,
                                     List<String> circErrorMessages) {

    final Map<String, String> headers = getBaseHeaders();
    final ItemRequestData itemRequestData =
        new ItemRequestData(null, headers, sessionData, itemIdentifier);

    final Future<IResource> result = resourceProvider.retrieveResource(itemRequestData);

    return result
      .otherwise(Utils.handleSearchErrors(result.cause(), circErrorMessages))
      .map(searchResult -> getTitleFromJson(searchResult, circErrorMessages));
  }

  private IResource getTitleFromJson(IResource resource, List<String> circErrorMessages) {
    if (!resource.getErrorMessages().isEmpty()) {
      return resource;
    }

    String title = TITLE_NOT_FOUND;
    final Optional<JsonObject> response = Optional.ofNullable(resource.getResource());
    if (response.isEmpty()) {
      return getiResourceFromTitle(title, circErrorMessages);
    }

    final String instances = "instances";
    JsonArray instanceArray = response.get().getJsonArray(instances);
    if (instanceArray.size() > 0) {
      title = instanceArray.getJsonObject(0).getString(TITLE);
      return getiResourceFromTitle(title, circErrorMessages);
    }
    return getiResourceFromTitle(title, circErrorMessages);
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
                                                Integer startItem, Integer endItem,
                                                SessionData sessionData) {
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
        sb.append("&offset=").append(offset);
      } else {
        offset = 0;
      }

      if (endItem != null) {
        final int limit = endItem.intValue() - offset;
        sb.append("&limit=").append(limit);
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
      final StringBuilder qSb = new StringBuilder()
          .append("(")
          .append(idField)
          .append("==")
          .append(idValue)
          .append(" and status=Open");
      if (requestType != null) {
        qSb.append(" and requestType==").append(requestType);
      }
      qSb.append(')');
      final StringBuilder urlSb = new StringBuilder()
          .append("/circulation/requests?query=")
          .append(Utils.encode(qSb.toString()));

      return appendLimits(urlSb).toString();
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
      String query = Utils.encode("(userId==" + userId + " and status.name=Open)");
      return "/circulation/loans?query=" + query;
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
      final StringBuilder qSb = new StringBuilder()
          .append("(userId==")
          .append(userId)
          .append(" and status.name=Open and dueDate<")
          .append(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(dueDate))
          .append(')');
      final StringBuilder path = new StringBuilder()
          .append("/circulation/loans?query=")
          .append(Utils.encode(qSb.toString()));

      return appendLimits(path).toString();
    }
  }

  private class ItemRequestData extends SearchRequestData {
    private String itemBarcode;

    private ItemRequestData(JsonObject body, Map<String, String> headers,
                            SessionData sessionData,String itemBarcode) {
      super(body, null, null, headers, sessionData);
      this.itemBarcode = itemBarcode;
    }

    @Override
    public String getPath() {

      final StringBuilder qSb = new StringBuilder()
          .append("/search/instances?limit=1&query=")
          .append("(items.barcode")
          .append("==")
          .append(itemBarcode).append(")");
      return qSb.toString();
    }
  }

  private abstract class SearchRequestData implements IRequestData {
    private final JsonObject body;
    final Integer startItem;
    final Integer endItem;
    private final Map<String, String> headers;
    private final SessionData sessionData;

    private SearchRequestData(
        JsonObject body,
        Integer startItem,
        Integer endItem,
        Map<String, String> headers,
        SessionData sessionData) {
      this.startItem = startItem;
      this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
      this.sessionData = sessionData;
      this.endItem = endItem;
      this.body = body;
    }

    @Override
    public JsonObject getBody() {
      return body;
    }

    @Override
    public SessionData getSessionData() {
      return sessionData;
    }

    @Override
    public Map<String, String> getHeaders() {
      return headers;
    }

  }
}
