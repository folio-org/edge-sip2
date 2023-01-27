package org.folio.edge.sip2.repositories;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.enumerations.CirculationStatus;
import org.folio.edge.sip2.domain.messages.enumerations.ItemStatus;
import org.folio.edge.sip2.domain.messages.enumerations.SecurityMarker;
import org.folio.edge.sip2.domain.messages.requests.ItemInformation;
import org.folio.edge.sip2.domain.messages.responses.ItemInformationResponse;
import org.folio.edge.sip2.domain.messages.responses.ItemInformationResponse.ItemInformationResponseBuilder;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.Utils;

//import org.folio.edge.sip2.utils.Utils.buildQueryString;


/**
 * Provides interaction with the Items service.
 *
 * @author mreno-EBSCO
 *
 */
public class ItemRepository {
  private static final Logger log = LogManager.getLogger();
  private final IResourceProvider<IRequestData> resourceProvider;
  private Clock clock;

  @Inject
  ItemRepository(IResourceProvider<IRequestData> resourceProvider,
                 Clock clock) {
    this.resourceProvider = Objects.requireNonNull(resourceProvider,
        "Resource provider cannot be null");
    this.clock = Objects.requireNonNull(clock, "Clock cannot be null");
  }

  private Map<String, String> getBaseHeaders() {
    final Map<String, String> headers = new HashMap<>();
    headers.put("accept", "application/json");
    return headers;
  }

  private class ItemInformationRequestData implements IRequestData {

    private final String itemIdentifier;
    private final Map<String, String> headers;
    private final SessionData sessionData;

    private ItemInformationRequestData(
        String itemIdentifier,
        Map<String, String> headers,
        SessionData sessionData) {
      this.itemIdentifier = itemIdentifier;
      this.headers = headers;
      this.sessionData = sessionData;
    }

    public String getPath() {
      String uri = "/inventory/items?limit=1&query=barcode==" + itemIdentifier;
      log.info("URI: {}", () -> uri);
      return uri;
    }

    public Map<String, String> getHeaders() {
      return headers;
    }

    @Override
    public SessionData getSessionData() {
      return sessionData;
    }
  }

  private class HoldingsRequestData implements IRequestData {

    private final String holdingsId;
    private final Map<String, String> headers;
    private final SessionData sessionData;

    private HoldingsRequestData(
        String holdingsId,
        Map<String, String> headers,
        SessionData sessionData) {
      this.holdingsId = holdingsId;
      this.headers = headers;
      this.sessionData = sessionData;
    }

    public String getPath() {
      String uri = "/holdings-storage/holdings/" + holdingsId;
      log.info("URI: {}", () -> uri);
      return uri;
    }

    public Map<String, String> getHeaders() {
      return headers;
    }

    @Override
    public SessionData getSessionData() {
      return sessionData;
    }
  }

  private class InstanceRequestData implements IRequestData {

    private final String instanceId;
    private final Map<String, String> headers;
    private final SessionData sessionData;

    private InstanceRequestData(
        String instanceId,
        Map<String, String> headers,
        SessionData sessionData) {
      this.instanceId = instanceId;
      this.headers = headers;
      this.sessionData = sessionData;
    }

    public String getPath() {
      String uri = "/inventory/instances/" + instanceId;
      log.info("URI: {}", () -> uri);
      return uri;
    }

    public Map<String, String> getHeaders() {
      return headers;
    }

    @Override
    public SessionData getSessionData() {
      return sessionData;
    }
  }

  private class NextHoldRequestData implements IRequestData {

    private final String itemUuid;
    private final Map<String, String> headers;
    private final SessionData sessionData;

    private NextHoldRequestData(
        String itemUuid,
        Map<String, String> headers,
        SessionData sessionData) {
      this.itemUuid = itemUuid;
      this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
      this.sessionData = sessionData;
    }

    public String getPath() {
      String query = Utils.encode("status==(Open - Awaiting pickup or Open - In Transit) and "
          + "(itemId==" + itemUuid);
      return "/circulation/requests?limit=1&query=" + query;
    }

    public Map<String, String> getHeaders() {
      return headers;
    }

    @Override
    public SessionData getSessionData() {
      return sessionData;
    }
  }

  private class LoanRequestData implements IRequestData {

    private final String itemId;
    private final Map<String, String> headers;
    private final SessionData sessionData;

    private LoanRequestData(
        String itemId,
        Map<String, String> headers,
        SessionData sessionData) {
      this.itemId = itemId;
      this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
      this.sessionData = sessionData;
    }

    public String getPath() {
      String query = Utils.encode("(itemId==" + itemId + " and status.name=Open)");
      return "/circulation/loans?query=" + query;
    }

    public Map<String, String> getHeaders() {
      return headers;
    }

    @Override
    public SessionData getSessionData() {
      return sessionData;
    }
  }

  /**
   * Perform itemInformation command.
   *
   * @param itemInformation the itemInformation domain object
   * @return the itemInformation response domain object
   */
  public Future<ItemInformationResponse> performItemInformationCommand(
      ItemInformation itemInformation, SessionData sessionData) {
    final String itemIdentifier = itemInformation.getItemIdentifier();

    ItemInformationRequestData itemInformationRequestData =
        new ItemInformationRequestData(itemIdentifier, getBaseHeaders(), sessionData);

    return getItemView(itemInformationRequestData)
      .otherwiseEmpty()
      .compose(itemView -> {
        log.info("itemView1: {}", () -> itemView);
        JsonObject item = itemView.getJsonObject("item");
        JsonObject holding = itemView.getJsonObject("holding");
        JsonObject instance = itemView.getJsonObject("instance");
        JsonObject loan = itemView.getJsonObject("loan");
        log.debug("itemView1: {}", () -> itemView);
        NextHoldRequestData nextHoldRequestData =
            new NextHoldRequestData(item.getString("id"), getBaseHeaders(), sessionData);

        Future<IResource> nextHoldResult;
        nextHoldResult = resourceProvider.retrieveResource(nextHoldRequestData);

        return nextHoldResult
            .otherwise(Utils::handleErrors)
            .compose(holdResource -> {
              final ItemInformationResponseBuilder builder = ItemInformationResponse.builder();
              OffsetDateTime dueDate = OffsetDateTime.now(clock);
              if (!loan.isEmpty()) {
                dueDate = OffsetDateTime.from(
                    Utils.getFolioDateTimeFormatter().parse(loan.getString("dueDate"))
                );
              }
              builder
                  .circulationStatus(
                      lookupCirculationStatus(item.getJsonObject("status").getString("name")))
                  .securityMarker(SecurityMarker.NONE)
                  .transactionDate(OffsetDateTime.now(clock))
                  .dueDate(dueDate)
                  .itemIdentifier(itemIdentifier)
                  .titleIdentifier(item.getString("title"))
                  .permanentLocation(item.getJsonObject("effectiveLocation").getString("name"))
                  .destinationInstitutionId(
                      item.getJsonObject("effectiveLocation").getString("name"))
                  .isbn(getIsbn(instance.getJsonArray("identifiers")))
                  .author(getAuthor(instance.getJsonArray("contributors")))
                  .summary(getSummary(instance.getJsonArray("notes")))
                  .screenMessage(Collections.singletonList(
                      item.getJsonObject("status").getString("name")));
              JsonObject holdResponse = holdResource.getResource();

              if (holdResponse.getJsonArray("requests").size() > 0) {
                JsonObject nextHold = holdResponse.getJsonArray("requests").getJsonObject(0);
                JsonObject holdPatron = nextHold.getJsonObject("requester");
                JsonObject holdLocation = nextHold.getJsonObject("pickupServicePoint");
                builder
                    .destinationInstitutionId(holdLocation.getString("name"))
                    .holdPatronId(holdPatron.getString("barcode"))
                    .holdPatronName(holdPatron.getString("lastName") + ", "
                      + holdPatron.getString("firstName"));
              }
              if (item.getJsonObject("status")
                  .getString("name").equals(ItemStatus.CHECKED_OUT.getValue())) {
                    LoanRequestData loanRequestData =
                        new LoanRequestData(item.getString("id"), getBaseHeaders(), sessionData);
                    Future<IResource> loansResult = resourceProvider
                        .retrieveResource(loanRequestData);
                    //   loansResult
                    //       .otherwiseEmpty()
                    //       .map(IResource::getResource)
                    //       .compose(loansResource -> {
                    //         JsonArray loans = loansResource.getJsonArray("loans");
                    //       }); //end compose
              }

              return Future.succeededFuture(builder.build());
            }
          );
      }); // end compose
  }

  private Future<JsonObject> getItemView(ItemInformationRequestData itemInformationRequestData) {

    JsonObject itemJson = new JsonObject();
    JsonObject holdingJson = new JsonObject();
    JsonObject instanceJson = new JsonObject();
    JsonObject loanJson = new JsonObject();
    //TODO this should be refactored for concurrency not sequential, holds and loans
    return getItem(itemInformationRequestData)
        .compose(itemResult -> {
          itemJson.mergeIn(itemResult);
          log.info("After merge item json " + itemJson);
          String itemId = itemResult.getString("id");
          String holdingsId = itemResult.getString("holdingsRecordId");
          HoldingsRequestData holdingsRequestData =
              new HoldingsRequestData(holdingsId, getBaseHeaders(),
                itemInformationRequestData.sessionData);
          LoanRequestData loanRequestData =
              new LoanRequestData(itemId, getBaseHeaders(),
                itemInformationRequestData.sessionData);

          return getHoldings(holdingsRequestData)
              .compose(holdingsResult -> {
                holdingJson.mergeIn(holdingsResult);
                String instanceId = holdingsResult.getString("instanceId");
                InstanceRequestData instanceRequestData =
                    new InstanceRequestData(instanceId, getBaseHeaders(),
                      holdingsRequestData.sessionData);

                return getInstance(instanceRequestData)
                    .compose(instanceResult -> {
                      instanceJson.mergeIn(instanceResult);
                      //JsonArray identifiers = instanceResult.getJsonArray("identifiers");
                      return getLoan(loanRequestData)
                        .compose(loanResult -> {
                          log.debug("LoanResult: {}", () -> loanResult);
                          loanJson.mergeIn(loanResult);
                          return Future.succeededFuture(loanResult);
                        });
                    });

              });
        })
        .compose(ar -> {
          log.info("Item Json" + itemJson);
          log.info("holding Json" + holdingJson);
          log.info("instance Json" + instanceJson);
          log.info("loan Json" + loanJson);
          JsonObject viewJson = new JsonObject();
          viewJson
              .put("item", itemJson)
              .put("holding", holdingJson)
              .put("instance", instanceJson)
              .put("loan", loanJson);
          return Future.succeededFuture(viewJson);
        });
  }

  private Future<JsonObject> getItem(ItemInformationRequestData itemInformationRequestData) {
    return resourceProvider
      .retrieveResource(itemInformationRequestData)
      .compose(itemResource -> {
        JsonObject item = itemResource.getResource();
        log.info("The requested item is " + item);
        log.info("The specific json " + item
            .getJsonArray("items").getJsonObject(0));
        return Future.succeededFuture(item
          .getJsonArray("items").getJsonObject(0));
      });

  }

  private Future<JsonObject> getHoldings(HoldingsRequestData holdingsRequestData) {
    return resourceProvider
      .retrieveResource(holdingsRequestData)
      .compose(holdingsResource -> {
        JsonObject holdings = holdingsResource.getResource();
        return Future.succeededFuture(holdings);
      });
  }

  private Future<JsonObject> getInstance(InstanceRequestData instanceRequestData) {
    return resourceProvider
      .retrieveResource(instanceRequestData)
      .compose(instanceResource -> {
        JsonObject instance = instanceResource.getResource();
        return Future.succeededFuture(instance);
      });
  }

  private Future<JsonObject> getLoan(LoanRequestData loanRequestData) {
    return resourceProvider
      .retrieveResource(loanRequestData)
      .compose(loanResource -> {
        JsonObject loan = loanResource.getResource();
        if (loan.getJsonArray("loans").isEmpty()) {

          JsonObject noloan = new JsonObject();
          return Future.succeededFuture(noloan);
        }
        return Future.succeededFuture(loan.getJsonArray("loans").getJsonObject(0));
      });
  }

  // private Future<JsonObject> getLoan(LoanRequestData loanRequestData) {
  //   final Future<IResource> result = resourceProvider.createResource(loanRequestData);
  //   return result
  //     .map(resource -> {
  //       final Optional<JsonObject> response = Optional.ofNullable(resource.getResource());

  //       final OffsetDateTime dueDate = response
  //           .map(v -> v.getString("dueDate"))
  //           .map(v -> OffsetDateTime.from(Utils.getFolioDateTimeFormatter().parse(v)))
  //           .orElse(OffsetDateTime.now(clock));
  //     });
  // }



  private String getAuthor(JsonArray contributers) {
    for (int i = 0;i < contributers.size();i++) {
      if (contributers.getJsonObject(i).getBoolean("primary").equals(true)) {
        return contributers.getJsonObject(i).getString("name");
      }
    }
    return "Not Found";
  }

  private List<String> getIsbn(JsonArray identifiers) {
    List<String> list = new ArrayList<String>();
    for (int i = 0;i < identifiers.size();i++) {
      if (identifiers
          .getJsonObject(i)
          .getString("identifierTypeId").equals("8261054f-be78-422d-bd51-4ed9f33c3422")) {
        list.add(identifiers.getJsonObject(i).getString("value"));
      }
    }
    return list;
  }

  private String getSummary(JsonArray notes) {
    for (int i = 0;i < notes.size();i++) {
      if (notes
          .getJsonObject(i)
          .getString("instanceNoteTypeId").equals("10e2e11b-450f-45c8-b09b-0f819999966e")) {
        return notes.getJsonObject(i).getString("note");
      }
    }
    return "";
  }

  /**
   * Lookup SIP CirculationStatus by FOLIO Item Status.
   *
   * @param folioString the itemStatus string from the FOLIO JSON respons
   * @return the CirculationStatus enum item
   */
  public CirculationStatus lookupCirculationStatus(String folioString) {

    if (ItemStatus.AVAILABLE.getValue().equals(folioString)) {
      return CirculationStatus.AVAILABLE;
    }
    if (ItemStatus.AWAITING_PICKUP.getValue().equals(folioString)) {
      return CirculationStatus.WAITING_ON_HOLD_SHELF;
    }
    if (ItemStatus.AWAITING_DELIVERY.getValue().equals(folioString)) {
      return CirculationStatus.IN_TRANSIT_BETWEEN_LIBRARY_LOCATIONS;
    }
    if (ItemStatus.CHECKED_OUT.getValue().equals(folioString)) {
      return CirculationStatus.CHARGED;
    }
    if (ItemStatus.IN_TRANSIT.getValue().equals(folioString)) {
      return CirculationStatus.IN_TRANSIT_BETWEEN_LIBRARY_LOCATIONS;
    }
    if (ItemStatus.PAGED.getValue().equals(folioString)) {
      return CirculationStatus.RECALLED;
    }
    if (ItemStatus.ON_ORDER.getValue().equals(folioString)) {
      return CirculationStatus.ON_ORDER;
    }
    if (ItemStatus.IN_PROCESS.getValue().equals(folioString)) {
      return CirculationStatus.IN_PROCESS;
    }
    if (ItemStatus.DECLARED_LOST.getValue().equals(folioString)) {
      return CirculationStatus.LOST;
    }
    if (ItemStatus.CLAIMED_RETURNED.getValue().equals(folioString)) {
      return CirculationStatus.CLAIMED_RETURNED;
    }
    if (ItemStatus.LOST_AND_PAID.getValue().equals(folioString)) {
      return CirculationStatus.LOST;
    }
    if (ItemStatus.INTELLECTUAL_ITEM.getValue().equals(folioString)) {
      return CirculationStatus.OTHER;
    }
    if (ItemStatus.IN_PROCESS_NON_REQUESTABLE.getValue().equals(folioString)) {
      return CirculationStatus.IN_PROCESS;
    }
    if (ItemStatus.LONG_MISSING.getValue().equals(folioString)) {
      return CirculationStatus.MISSING;
    }
    if (ItemStatus.UNAVAILABLE.getValue().equals(folioString)) {
      return CirculationStatus.OTHER;
    }
    if (ItemStatus.RESTRICTED.getValue().equals(folioString)) {
      return CirculationStatus.OTHER;
    }
    if (ItemStatus.AGED_TO_LOST.getValue().equals(folioString)) {
      return CirculationStatus.LOST;
    }
    return CirculationStatus.OTHER;
  }
}
