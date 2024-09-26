package org.folio.edge.sip2.repositories;

import static org.folio.edge.sip2.api.support.TestUtils.getJsonFromFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.time.Clock;
import java.time.OffsetDateTime;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.enumerations.CirculationStatus;
import org.folio.edge.sip2.domain.messages.requests.ItemInformation;
import org.folio.edge.sip2.session.SessionData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
class ItemRepositoryTests {

  @Test
  void canCreateItemRepository(
      @Mock IResourceProvider<IRequestData> mockFolioResource,
      @Mock Clock clock) {
    final ItemRepository ItemRepository = new ItemRepository(
        mockFolioResource, clock);

    assertNotNull(ItemRepository);
  }

  @Test
   void cannotCreateItemRepositoryWhenResourceProviderIsNull() {
    final NullPointerException thrown = assertThrows(
        NullPointerException.class,
        () -> new ItemRepository(null, null));

    assertEquals("Resource provider cannot be null", thrown.getMessage());
  }

  @Test
  void getItemInformation(
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      VertxTestContext testContext) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final String itemIdentifier = "230317";
    final String itemInformationResponseJson = getJsonFromFile(
        "json/item_information_response.json");
    final JsonObject itemInformationRes = new JsonObject(itemInformationResponseJson);
    final String holdingResponseJson = getJsonFromFile(
        "json/holding_response.json");
    final JsonObject holdingRes = new JsonObject(holdingResponseJson);
    final ItemInformation itemInformation = ItemInformation.builder()
        .transactionDate(OffsetDateTime.now())
        .institutionId("diku")
        .itemIdentifier(itemIdentifier)
        .terminalPassword("1234")
        .build();

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(itemInformationRes,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    String expectedPath = "/holdings-storage/holdings/" + "5f0140c5-96c7-44e1-845b-853e2d7cdf13";

    when(mockFolioProvider.retrieveResource(argThat((IRequestData data) ->
        data.getPath().equals(expectedPath))))
        .thenReturn(Future.succeededFuture(new FolioResource(holdingRes,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));


    final SessionData sessionData = TestUtils.getMockedSessionData();

    final ItemRepository ItemRepository = new ItemRepository(
        mockFolioProvider, clock);
    ItemRepository.performItemInformationCommand(itemInformation, sessionData).onComplete(
        testContext.succeeding(itemInformationResponse -> testContext.verify(() -> {
          assertNotNull(itemInformationResponse);
          assertEquals(OffsetDateTime.now(clock), itemInformationResponse.getTransactionDate());
          assertEquals(itemIdentifier, itemInformationResponse.getItemIdentifier());
          assertEquals("Fool moon", itemInformationResponse.getTitleIdentifier());
          assertNull(itemInformationResponse.getFeeType());
          assertNull(itemInformationResponse.getCurrencyType());
          assertNull(itemInformationResponse.getFeeAmount());
          assertNull(itemInformationResponse.getMediaType());
          assertNull(itemInformationResponse.getItemProperties());
          assertNull(itemInformationResponse.getPrintLine());
          testContext.completeNow();
        })));
  }

  @Test
  void getItemInformationFailed(
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      VertxTestContext testContext) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final String itemIdentifier = "230317";
    final ItemInformation itemInformation = ItemInformation.builder()
        .transactionDate(OffsetDateTime.now())
        .institutionId("diku")
        .itemIdentifier(itemIdentifier)
        .terminalPassword("1234")
        .build();



    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.failedFuture(new Exception("Item does not exists.")));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final ItemRepository ItemRepository = new ItemRepository(
        mockFolioProvider, clock);
    ItemRepository.performItemInformationCommand(itemInformation, sessionData).onComplete(
        testContext.succeeding(itemInformationResponse -> testContext.verify(() -> {
          assertNotNull(itemInformationResponse);
          assertEquals("Unknown", itemInformationResponse.getTitleIdentifier());
          assertTrue(itemInformationResponse.getScreenMessage().contains("Item does not exist"));
          assertEquals(CirculationStatus.OTHER, itemInformationResponse.getCirculationStatus());
          testContext.completeNow();
        })));
  }
}
