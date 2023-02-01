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
          + "(itemId==" + itemUuid + ")");
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
    Objects.requireNonNull(itemInformation, "itemInformation cannot be null");
    Objects.requireNonNull(sessionData, "sessionData cannot be null");
    log.debug("performItemInformationCommand itemIdentifier:{}",
        itemInformation.getItemIdentifier());

    final String itemIdentifier = itemInformation.getItemIdentifier();

    ItemInformationRequestData itemInformationRequestData =
        new ItemInformationRequestData(itemIdentifier, getBaseHeaders(), sessionData);

    return getItemView(itemInformationRequestData)
      .compose(itemView -> {
        if (itemView != null) {
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
                OffsetDateTime dueDate = null;
                if (!loan.isEmpty()) {
                  dueDate = OffsetDateTime.from(
                      Utils.getFolioDateTimeFormatter().parse(loan.getString("dueDate"))
                  );
                }
                builder
                    .circulationStatus(lookupCirculationStatus(item.getJsonObject("status")
                          .getString("name")))
                    .securityMarker(SecurityMarker.NONE)
                    .transactionDate(OffsetDateTime.now(clock))
                    .dueDate(dueDate)
                    .itemIdentifier(itemIdentifier)
                    .titleIdentifier(item.getString("title"))
                    .permanentLocation(item.getJsonObject("effectiveLocation").getString("name"))
                    .screenMessage(Collections.singletonList(
                        item.getJsonObject("status").getString("name")));
                return Future.succeededFuture(builder.build());
              }
            );
        }
        return Future.failedFuture("Item does not exists.");
            }

      ); // end compose
  }

  private Future<JsonObject> getItemView(ItemInformationRequestData itemInformationRequestData) {

    JsonObject itemJson = new JsonObject();
    JsonObject holdingJson = new JsonObject();
    JsonObject instanceJson = new JsonObject();
    JsonObject loanJson = new JsonObject();

    return getItem(itemInformationRequestData)
      .otherwiseEmpty()
        .compose(itemResult -> {
          if (itemResult != null) {
            itemJson.mergeIn(itemResult);
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
                        return getLoan(loanRequestData)
                          .compose(loanResult -> {
                            log.debug("LoanResult: {}", () -> loanResult);
                            loanJson.mergeIn(loanResult);
                            return Future.succeededFuture(loanResult);
                          });
                      });

                });
          }
          return Future.failedFuture("Item does not exists.");
        })
          .compose(ar -> {
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
