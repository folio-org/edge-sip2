package org.folio.edge.sip2.repositories;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static java.util.Arrays.asList;
import static org.folio.edge.sip2.api.support.TestUtils.getJsonFromFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.impl.NoStackTraceThrowable;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.enumerations.MediaType;
import org.folio.edge.sip2.domain.messages.requests.Checkin;
import org.folio.edge.sip2.domain.messages.requests.Checkout;
import org.folio.edge.sip2.domain.messages.requests.Renew;
import org.folio.edge.sip2.domain.messages.requests.RenewAll;
import org.folio.edge.sip2.domain.messages.responses.CheckoutResponse;
import org.folio.edge.sip2.repositories.domain.ExtendedUser;
import org.folio.edge.sip2.repositories.domain.PatronPasswordVerificationRecords;
import org.folio.edge.sip2.repositories.domain.User;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.Utils;
import org.folio.okapi.common.refreshtoken.client.ClientException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
class CirculationRepositoryTests {

  @Test
   void canCreateCirculationRepository(
      @Mock IResourceProvider<IRequestData> mockFolioResource,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository mockItemRepository,
      @Mock Clock clock) {
    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioResource, mockPasswordVerifier, mockItemRepository, clock);

    assertNotNull(circulationRepository);
  }

  @Test
  void cannotCreateCirculationRepositoryWhenResourceProviderIsNull() {
    final NullPointerException thrown = assertThrows(
        NullPointerException.class,
        () -> new CirculationRepository(null, null, null, null));

    assertEquals("Resource provider cannot be null", thrown.getMessage());
  }

  @Test
  void canCheckin(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository mockItemRepository) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final OffsetDateTime returnDate = OffsetDateTime.now();
    final String currentLocation = UUID.randomUUID().toString();
    final String itemIdentifier = "1234567890";
    final String titleIdentifier = "Some Cool Book";
    final String callNumber = "9983235487258";
    final Checkin checkin = Checkin.builder()
        .noBlock(FALSE)
        .transactionDate(OffsetDateTime.now())
        .returnDate(returnDate)
        .currentLocation(currentLocation)
        .institutionId("diku")
        .itemIdentifier(itemIdentifier)
        .terminalPassword("1234")
        .itemProperties("Some property of this item")
        .cancel(FALSE)
        .build();

    final JsonObject checkinResponseJson = new JsonObject()
        .put("item", new JsonObject()
            .put("callNumber", callNumber)
            .put("title", titleIdentifier)
            .put("location", new JsonObject()
                .put("name", "Main Library"))
            .put("materialType", new JsonObject()
                .put("name", "book"))
            .put("inTransitDestinationServicePoint", new JsonObject()
                .put("name", "Annex"))
        );


    final JsonObject getRequestsResponseJson = new JsonObject()
        .put("requests", new JsonArray()
            .add(new JsonObject()
                .put("requestType", "Hold")
                .put("requestLevel", "Item")))
        .put("totalRecords", 1);

    when(mockFolioProvider.createResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(checkinResponseJson,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(getRequestsResponseJson,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    when(mockItemRepository.getItemById(anyString(), any()))
        .thenReturn(Future.succeededFuture(
            new JsonObject().put("status", new JsonObject().put("name", "Available"))));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, mockItemRepository, clock);
    circulationRepository.performCheckinCommand(checkin, sessionData).onComplete(
        testContext.succeeding(checkinResponse -> testContext.verify(() -> {
          assertNotNull(checkinResponse);
          assertTrue(checkinResponse.getOk());
          assertTrue(checkinResponse.getResensitize());
          assertNull(checkinResponse.getMagneticMedia());
          assertTrue(checkinResponse.getAlert());
          assertEquals(OffsetDateTime.now(clock), checkinResponse.getTransactionDate());
          assertEquals("diku", checkinResponse.getInstitutionId());
          assertEquals(itemIdentifier, checkinResponse.getItemIdentifier());
          assertEquals("Main Library", checkinResponse.getPermanentLocation());
          assertEquals(titleIdentifier, checkinResponse.getTitleIdentifier());
          assertNull(checkinResponse.getSortBin());
          assertNull(checkinResponse.getPatronIdentifier());
          assertEquals(MediaType.BOOK, checkinResponse.getMediaType());
          assertNull(checkinResponse.getItemProperties());
          assertNull(checkinResponse.getScreenMessage());
          assertNull(checkinResponse.getPrintLine());
          assertEquals(callNumber, checkinResponse.getCallNumber());
          assertEquals("Annex", checkinResponse.getPickupServicePoint());

          testContext.completeNow();
        })));
  }

  @Test
  void canCheckinWithoutTitleIdentifier(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository mockItemRepository) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final OffsetDateTime returnDate = OffsetDateTime.now();
    final String currentLocation = UUID.randomUUID().toString();
    final String itemIdentifier = "1234567890";
    final Checkin checkin = Checkin.builder()
        .noBlock(FALSE)
        .transactionDate(OffsetDateTime.now())
        .returnDate(returnDate)
        .currentLocation(currentLocation)
        .institutionId("diku")
        .itemIdentifier(itemIdentifier)
        .terminalPassword("1234")
        .itemProperties("Some property of this item")
        .cancel(FALSE)
        .build();

    final JsonObject checkinResponseJson = new JsonObject()
        .put("item", new JsonObject()
          .put("materialType", new JsonObject()
            .put("name", "dvd"))
            .put("location", new JsonObject()
                .put("name", "Main Library")));

    final JsonObject getRequestsResponseJson = new JsonObject()
        .put("requests", new JsonArray())
        .put("totalRecords", 0);

    when(mockFolioProvider.createResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(checkinResponseJson,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(getRequestsResponseJson,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    when(mockItemRepository.getItemById(anyString(), any()))
        .thenReturn(Future.succeededFuture(
        new JsonObject().put("status", new JsonObject().put("name", "Available"))));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, mockItemRepository, clock);
    circulationRepository.performCheckinCommand(checkin, sessionData).onComplete(
        testContext.succeeding(checkinResponse -> testContext.verify(() -> {
          assertNotNull(checkinResponse);
          assertTrue(checkinResponse.getOk());
          assertTrue(checkinResponse.getResensitize());
          assertNull(checkinResponse.getMagneticMedia());
          assertFalse(checkinResponse.getAlert());
          assertEquals(OffsetDateTime.now(clock), checkinResponse.getTransactionDate());
          assertEquals("diku", checkinResponse.getInstitutionId());
          assertEquals(itemIdentifier, checkinResponse.getItemIdentifier());
          assertEquals("Main Library", checkinResponse.getPermanentLocation());
          assertEquals(itemIdentifier, checkinResponse.getTitleIdentifier());
          assertNull(checkinResponse.getSortBin());
          assertNull(checkinResponse.getPatronIdentifier());
          assertEquals(MediaType.VIDEO_TAPE, checkinResponse.getMediaType());
          assertNull(checkinResponse.getItemProperties());
          assertNull(checkinResponse.getScreenMessage());
          assertNull(checkinResponse.getPrintLine());

          testContext.completeNow();
        })));
  }

  @Test
  void cannotCheckin(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository mockItemRepository) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final OffsetDateTime returnDate = OffsetDateTime.now();
    final String currentLocation = UUID.randomUUID().toString();
    final String itemIdentifier = "1234567890";
    final Checkin checkin = Checkin.builder()
        .noBlock(FALSE)
        .transactionDate(OffsetDateTime.now())
        .returnDate(returnDate)
        .currentLocation(currentLocation)
        .institutionId("diku")
        .itemIdentifier(itemIdentifier)
        .terminalPassword("1234")
        .itemProperties("Some property of this item")
        .cancel(FALSE)
        .build();

    when(mockFolioProvider.createResource(any()))
        .thenReturn(Future.failedFuture(new NoStackTraceThrowable("Test failure")));

    when(mockItemRepository.getItemById(anyString(), any()))
        .thenReturn(Future.succeededFuture(
        new JsonObject().put("status", new JsonObject().put("name", "Available"))));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, mockItemRepository, clock);
    circulationRepository.performCheckinCommand(checkin, sessionData).onComplete(
        testContext.succeeding(checkinResponse -> testContext.verify(() -> {
          assertNotNull(checkinResponse);
          assertFalse(checkinResponse.getOk());
          assertFalse(checkinResponse.getResensitize());
          assertNull(checkinResponse.getMagneticMedia());
          assertFalse(checkinResponse.getAlert());
          assertEquals(OffsetDateTime.now(clock), checkinResponse.getTransactionDate());
          assertEquals("diku", checkinResponse.getInstitutionId());
          assertEquals(itemIdentifier, checkinResponse.getItemIdentifier());
          assertEquals("", checkinResponse.getPermanentLocation());
          assertEquals(itemIdentifier, checkinResponse.getTitleIdentifier());
          assertNull(checkinResponse.getSortBin());
          assertNull(checkinResponse.getPatronIdentifier());
          assertNull(checkinResponse.getMediaType());
          assertNull(checkinResponse.getItemProperties());
          assertNull(checkinResponse.getScreenMessage());
          assertNull(checkinResponse.getPrintLine());

          testContext.completeNow();
        })));
  }

  @Test
  void cannotCheckinDueToItemStatus(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository mockItemRepository) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final OffsetDateTime returnDate = OffsetDateTime.now();
    final String currentLocation = UUID.randomUUID().toString();
    final String itemIdentifier = "1234567890";
    final String titleIdentifier = "Some Cool Book";
    final String callNumber = "9983235487258";
    final Checkin checkin = Checkin.builder()
        .noBlock(FALSE)
        .transactionDate(OffsetDateTime.now())
        .returnDate(returnDate)
        .currentLocation(currentLocation)
        .institutionId("diku")
        .itemIdentifier(itemIdentifier)
        .terminalPassword("1234")
        .itemProperties("Some property of this item")
        .cancel(FALSE)
        .build();

    final JsonObject checkinResponseJson = new JsonObject()
        .put("item", new JsonObject()
            .put("callNumber", callNumber)
            .put("title", titleIdentifier)
            .put("location", new JsonObject()
                .put("name", "Main Library"))
            .put("materialType", new JsonObject()
                .put("name", "book"))
            .put("inTransitDestinationServicePoint", new JsonObject()
                .put("name", "Annex"))
        );




    when(mockItemRepository.getItemById(anyString(), any()))
        .thenReturn(Future.succeededFuture(
        new JsonObject().put("status", new JsonObject().put("name", "Withdrawn"))));

    final SessionData sessionData = TestUtils.getMockedSessionData();
    List<String> rejectList = new ArrayList<>();
    rejectList.add("Withdrawn");
    sessionData.setInvalidCheckinStatusList(rejectList);

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, mockItemRepository, clock);
    circulationRepository.performCheckinCommand(checkin, sessionData).onComplete(
        testContext.succeeding(checkinResponse -> testContext.verify(() -> {
          assertNotNull(checkinResponse);
          assertFalse(checkinResponse.getOk());
          assertEquals("Item status 'Withdrawn' is not valid for checkin",
              checkinResponse.getScreenMessage().get(0));

          testContext.completeNow();
        })));
  }

  @Test
  void canCheckout(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository itemRepository) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final OffsetDateTime nbDueDate =  OffsetDateTime.now().plusDays(30);
    final String patronIdentifier = "1029384756";
    final String itemIdentifier = "1234567890";
    final String title = "Some book";
    final Checkout checkout = Checkout.builder()
        .scRenewalPolicy(FALSE)
        .noBlock(FALSE)
        .transactionDate(OffsetDateTime.now())
        .nbDueDate(nbDueDate)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .terminalPassword("1234")
        .itemProperties("Some property of this item")
        .patronPassword("7890")
        .feeAcknowledged(FALSE)
        .cancel(FALSE)
        .build();

    final JsonObject response = new JsonObject()
        .put("item", new JsonObject()
            .put("title", title))
        .put("dueDate", nbDueDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    when(mockFolioProvider.createResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(response,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));
    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("7890"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder().build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, itemRepository, clock);
    circulationRepository.performCheckoutCommand(checkout, sessionData).onComplete(
        testContext.succeeding(checkoutResponse -> testContext.verify(() -> {
          assertNotNull(checkoutResponse);
          assertTrue(checkoutResponse.getOk());
          assertFalse(checkoutResponse.getRenewalOk());
          assertNull(checkoutResponse.getMagneticMedia());
          assertTrue(checkoutResponse.getDesensitize());
          assertEquals(OffsetDateTime.now(clock), checkoutResponse.getTransactionDate());
          assertEquals("diku", checkoutResponse.getInstitutionId());
          assertEquals(patronIdentifier, checkoutResponse.getPatronIdentifier());
          assertEquals(itemIdentifier, checkoutResponse.getItemIdentifier());
          assertEquals(title, checkoutResponse.getTitleIdentifier());
          assertEquals(nbDueDate, checkoutResponse.getDueDate());
          assertNull(checkoutResponse.getFeeType());
          assertNull(checkoutResponse.getSecurityInhibit());
          assertNull(checkoutResponse.getCurrencyType());
          assertNull(checkoutResponse.getFeeAmount());
          assertNull(checkoutResponse.getMediaType());
          assertNull(checkoutResponse.getItemProperties());
          assertNull(checkoutResponse.getTransactionId());
          assertNull(checkoutResponse.getScreenMessage());
          assertNull(checkoutResponse.getPrintLine());

          testContext.completeNow();
        })));
  }

  @Test
  void cantCheckout(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository itemRepository) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final OffsetDateTime nbDueDate =  OffsetDateTime.now().plusDays(30);
    final String patronIdentifier = "1029384756";
    final String itemIdentifier = "1234567890";
    final String title = "Some book";
    final Checkout checkout = Checkout.builder()
        .scRenewalPolicy(FALSE)
        .noBlock(FALSE)
        .transactionDate(OffsetDateTime.now())
        .nbDueDate(nbDueDate)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .terminalPassword("1234")
        .itemProperties("Some property of this item")
        .patronPassword("7890")
        .feeAcknowledged(FALSE)
        .cancel(FALSE)
        .build();

    final JsonObject response = new JsonObject()
        .put("item", new JsonObject()
        .put("title", title))
        .put("dueDate", nbDueDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("7890"), any()))
        .thenReturn(Future.failedFuture(new ClientException("Incorrect Username")));

    final SessionData sessionData = TestUtils.getMockedSessionData();
    sessionData.setPatronPasswordVerificationRequired(TRUE);
    sessionData.setErrorResponseMessage(CheckoutResponse.builder()
        .ok(FALSE)
        .renewalOk(FALSE)
        .magneticMedia(null)
        .desensitize(FALSE)
        .transactionDate(OffsetDateTime.now(clock))
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier("test")
        .dueDate(OffsetDateTime.now(clock))
        .screenMessage(Collections.singletonList("Incorrect Username"))
        .build());

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, itemRepository, clock);
    circulationRepository.performCheckoutCommand(checkout, sessionData).onComplete(
        testContext.failing(checkoutResponse -> testContext.verify(() -> {
          assertNotNull(checkoutResponse);
          assertEquals("Incorrect Username", checkoutResponse.getMessage());

          testContext.completeNow();
        })));
  }

  @Test
  void canRenew(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository itemRepository) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final OffsetDateTime nbDueDate =  OffsetDateTime.now().plusDays(30);
    final String patronIdentifier = "1029384756";
    final String itemIdentifier = "1234567890";
    final String title = "Some book";
    final Renew renew = Renew.builder()
        .noBlock(FALSE)
        .transactionDate(OffsetDateTime.now())
        .nbDueDate(nbDueDate)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .terminalPassword("1234")
        .itemProperties("Some property of this item")
        .patronPassword("7890")
        .feeAcknowledged(FALSE)
        .build();

    final JsonObject response = new JsonObject()
        .put("item", new JsonObject()
        .put("title", title))
        .put("dueDate", nbDueDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    when(mockFolioProvider.createResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(response,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));
    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("7890"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder().build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, itemRepository, clock);
    circulationRepository.performRenewCommand(renew, sessionData).onComplete(
        testContext.succeeding(renewResponse -> testContext.verify(() -> {
          assertNotNull(renewResponse);
          assertTrue(renewResponse.getOk());
          assertTrue(renewResponse.getRenewalOk());
          assertNull(renewResponse.getMagneticMedia());
          assertEquals(OffsetDateTime.now(clock), renewResponse.getTransactionDate());
          assertEquals("diku", renewResponse.getInstitutionId());
          assertEquals(patronIdentifier, renewResponse.getPatronIdentifier());
          assertEquals(itemIdentifier, renewResponse.getItemIdentifier());
          assertEquals("", renewResponse.getTitleIdentifier());
          assertEquals(nbDueDate, renewResponse.getDueDate());
          assertNull(renewResponse.getFeeType());
          assertNull(renewResponse.getSecurityInhibit());
          assertNull(renewResponse.getCurrencyType());
          assertNull(renewResponse.getFeeAmount());
          assertNull(renewResponse.getMediaType());
          assertNull(renewResponse.getItemProperties());
          assertNull(renewResponse.getTransactionId());
          assertNull(renewResponse.getScreenMessage());
          assertNull(renewResponse.getPrintLine());

          testContext.completeNow();
        })));
  }

  @Test
  void canRenewWithTitleIdentifier(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository itemRepository) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final OffsetDateTime nbDueDate =  OffsetDateTime.now().plusDays(30);
    final String patronIdentifier = "1029384756";
    final String itemIdentifier = "1234567890";
    final String title = "Some book";
    final Renew renew = Renew.builder()
        .noBlock(FALSE)
        .titleIdentifier(title)
        .transactionDate(OffsetDateTime.now())
        .nbDueDate(nbDueDate)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .terminalPassword("1234")
        .itemProperties("Some property of this item")
        .patronPassword("7890")
        .feeAcknowledged(FALSE)
        .build();

    final JsonObject response = new JsonObject()
        .put("item", new JsonObject()
        .put("title", title))
        .put("dueDate", nbDueDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    when(mockFolioProvider.createResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(response,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));
    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("7890"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder().build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, itemRepository, clock);
    circulationRepository.performRenewCommand(renew, sessionData).onComplete(
        testContext.succeeding(renewResponse -> testContext.verify(() -> {
          assertNotNull(renewResponse);
          assertTrue(renewResponse.getOk());
          assertTrue(renewResponse.getRenewalOk());
          assertNull(renewResponse.getMagneticMedia());
          assertEquals(OffsetDateTime.now(clock), renewResponse.getTransactionDate());
          assertEquals("diku", renewResponse.getInstitutionId());
          assertEquals(patronIdentifier, renewResponse.getPatronIdentifier());
          assertEquals("", renewResponse.getTitleIdentifier());
          assertEquals(nbDueDate, renewResponse.getDueDate());
          assertNull(renewResponse.getFeeType());
          assertNull(renewResponse.getSecurityInhibit());
          assertNull(renewResponse.getCurrencyType());
          assertNull(renewResponse.getFeeAmount());
          assertNull(renewResponse.getMediaType());
          assertNull(renewResponse.getItemProperties());
          assertNull(renewResponse.getTransactionId());
          assertNull(renewResponse.getScreenMessage());
          assertNull(renewResponse.getPrintLine());

          testContext.completeNow();
        })));
  }


  @Test
  void canNotRenewWithoutTitleAndItemIdentifier(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository itemRepository) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final OffsetDateTime nbDueDate =  OffsetDateTime.now().plusDays(30);
    final String patronIdentifier = "1029384756";
    final String title = "Some book";
    final Renew renew = Renew.builder()
        .noBlock(FALSE)
        .transactionDate(OffsetDateTime.now())
        .nbDueDate(nbDueDate)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .terminalPassword("1234")
        .itemProperties("Some property of this item")
        .patronPassword("7890")
        .feeAcknowledged(FALSE)
        .build();

    final JsonObject response = new JsonObject()
        .put("item", new JsonObject()
        .put("title", title))
        .put("dueDate", nbDueDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, itemRepository, clock);
    circulationRepository.performRenewCommand(renew, sessionData).onComplete(
        testContext.succeeding(renewResponse -> testContext.verify(() -> {
          assertNotNull(renewResponse);
          assertFalse(renewResponse.getOk());
          assertFalse(renewResponse.getRenewalOk());
          assertNull(renewResponse.getMagneticMedia());
          assertEquals(OffsetDateTime.now(clock), renewResponse.getTransactionDate());
          assertEquals("diku", renewResponse.getInstitutionId());
          assertNull(renewResponse.getFeeType());
          assertNull(renewResponse.getSecurityInhibit());
          assertNull(renewResponse.getCurrencyType());
          assertNull(renewResponse.getFeeAmount());
          assertNull(renewResponse.getMediaType());
          assertNull(renewResponse.getItemProperties());
          assertNull(renewResponse.getTransactionId());
          assertEquals(Collections.singletonList("Either or both of the 'item identifier' or "
              + "'title identifier' fields must be present for the message to"
              + " be useful."),
              renewResponse.getScreenMessage());
          assertNull(renewResponse.getPrintLine());

          testContext.completeNow();
        })));
  }

  @Test
  void canRenewAll(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository itemRepository) {
    final String patronIdentifier = "1029384756";
    final Clock clock = TestUtils.getUtcFixedClock();
    final String title = "Some book";
    final OffsetDateTime nbDueDate =  OffsetDateTime.now().plusDays(30);
    final String userId = UUID.randomUUID().toString();
    final String itemId = UUID.randomUUID().toString();
    final RenewAll renewAll = RenewAll.builder()
        .transactionDate(OffsetDateTime.now())
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .patronPassword("7890")
        .terminalPassword("1234")
        .feeAcknowledged(FALSE)
        .build();


    final JsonObject loansResponse = new JsonObject()
        .put("loans", new JsonArray()
            .add(new JsonObject()
                .put("userId", userId)
                .put("itemId", itemId)
                .put("loanDate", OffsetDateTime.now(clock).format(ISO_OFFSET_DATE_TIME))
                .put("action", "checkedout")))
        .put("totalRecords", 1);

    final JsonObject response = new JsonObject()
        .put("item", new JsonObject()
        .put("title", title))
        .put("dueDate", nbDueDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

    ExtendedUser extendedUser = new ExtendedUser();
    extendedUser.setUser(new User.Builder().id(userId).build());

    when(mockFolioProvider.createResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(response,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));
    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(loansResponse,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));
    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("7890"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder().extendedUser(
            extendedUser).build()));


    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, itemRepository, clock);

    circulationRepository.performRenewAllCommand(renewAll, sessionData).onComplete(
        testContext.succeeding(renewAllResponse -> testContext.verify(() -> {
          assertNotNull(renewAllResponse);
          assertEquals("diku", renewAllResponse.getInstitutionId());
          assertTrue(renewAllResponse.getOk());
          assertTrue(renewAllResponse.getRenewedItems().isEmpty());
          assertTrue(renewAllResponse.getUnrenewedItems().isEmpty());
          assertNull(renewAllResponse.getPrintLine());
          testContext.completeNow();
        }))
    );


  }

  @Test
  void cannotRenewAllWithBadPassword(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository itemRepository) {
    final String patronIdentifier = "1029384756";
    final Clock clock = TestUtils.getUtcFixedClock();
    final String title = "Some book";
    final OffsetDateTime nbDueDate =  OffsetDateTime.now().plusDays(30);
    final String userId = UUID.randomUUID().toString();
    final String itemId = UUID.randomUUID().toString();
    final RenewAll renewAll = RenewAll.builder()
        .transactionDate(OffsetDateTime.now())
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .patronPassword("7890")
        .terminalPassword("1234")
        .feeAcknowledged(FALSE)
        .build();

    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("7890"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder()
          .passwordVerified(false).build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, itemRepository, clock);

    circulationRepository.performRenewAllCommand(renewAll, sessionData).onComplete(
        testContext.succeeding(renewAllResponse -> testContext.verify(() -> {
          assertNotNull(renewAllResponse);
          assertEquals("diku", renewAllResponse.getInstitutionId());
          assertTrue(renewAllResponse.getRenewedItems().isEmpty());
          assertFalse(renewAllResponse.getOk());
          testContext.completeNow();
        }))
    );

  }

  @Test
  void canCheckoutRequiredPassword(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository itemRepository) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final OffsetDateTime nbDueDate =  OffsetDateTime.now().plusDays(30);
    final String patronIdentifier = "1029384756";
    final String itemIdentifier = "1234567890";
    final String title = "Some book";
    final Checkout checkout = Checkout.builder()
        .scRenewalPolicy(FALSE)
        .noBlock(FALSE)
        .transactionDate(OffsetDateTime.now())
        .nbDueDate(nbDueDate)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .terminalPassword("1234")
        .itemProperties("Some property of this item")
        .patronPassword("7890")
        .feeAcknowledged(FALSE)
        .cancel(FALSE)
        .build();

    final JsonObject response = new JsonObject()
        .put("item", new JsonObject()
            .put("title", title))
        .put("dueDate", nbDueDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    final String userResponseJson = getJsonFromFile("json/user_response.json");
    final User userResponse = Json.decodeValue(userResponseJson, User.class);
    ExtendedUser extendedUser = new ExtendedUser();
    extendedUser.setUser(userResponse);
    when(mockFolioProvider.createResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(response,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));
    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("7890"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder()
            .extendedUser(extendedUser)
            .passwordVerified(TRUE)
            .build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();
    sessionData.setPatronPasswordVerificationRequired(true);

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, itemRepository, clock);
    circulationRepository.performCheckoutCommand(checkout, sessionData).onComplete(
        testContext.succeeding(checkoutResponse -> testContext.verify(() -> {
          assertNotNull(checkoutResponse);
          assertTrue(checkoutResponse.getOk());
          assertFalse(checkoutResponse.getRenewalOk());
          assertNull(checkoutResponse.getMagneticMedia());
          assertTrue(checkoutResponse.getDesensitize());
          assertEquals(OffsetDateTime.now(clock), checkoutResponse.getTransactionDate());
          assertEquals("diku", checkoutResponse.getInstitutionId());
          assertEquals(patronIdentifier, checkoutResponse.getPatronIdentifier());
          assertEquals(itemIdentifier, checkoutResponse.getItemIdentifier());
          assertEquals(title, checkoutResponse.getTitleIdentifier());
          assertEquals(nbDueDate, checkoutResponse.getDueDate());
          assertNull(checkoutResponse.getFeeType());
          assertNull(checkoutResponse.getSecurityInhibit());
          assertNull(checkoutResponse.getCurrencyType());
          assertNull(checkoutResponse.getFeeAmount());
          assertNull(checkoutResponse.getMediaType());
          assertNull(checkoutResponse.getItemProperties());
          assertNull(checkoutResponse.getTransactionId());
          assertNull(checkoutResponse.getScreenMessage());
          assertNull(checkoutResponse.getPrintLine());

          testContext.completeNow();
        })));
  }

  @Test
  void cannotCheckoutRequiredPasswordWithBadPassword(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository itemRepository) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final OffsetDateTime nbDueDate =  OffsetDateTime.now(clock).plusDays(30);
    final String patronIdentifier = "1029384756";
    final String itemIdentifier = "1234567890";
    final String title = "";
    final Checkout checkout = Checkout.builder()
        .scRenewalPolicy(FALSE)
        .noBlock(FALSE)
        .transactionDate(OffsetDateTime.now())
        .nbDueDate(nbDueDate)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .terminalPassword("1234")
        .itemProperties("Some property of this item")
        .patronPassword("7890")
        .feeAcknowledged(FALSE)
        .cancel(FALSE)
        .build();

    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("7890"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder()
            .passwordVerified(FALSE)
            .errorMessages(Collections.singletonList("Password does not match"))
            .build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();
    sessionData.setPatronPasswordVerificationRequired(true);

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, itemRepository, clock);
    circulationRepository.performCheckoutCommand(checkout, sessionData).onComplete(
        testContext.succeeding(checkoutResponse -> testContext.verify(() -> {
          assertNotNull(checkoutResponse);
          assertFalse(checkoutResponse.getOk());
          assertFalse(checkoutResponse.getRenewalOk());
          assertNull(checkoutResponse.getMagneticMedia());
          assertFalse(checkoutResponse.getDesensitize());
          assertEquals(OffsetDateTime.now(clock), checkoutResponse.getTransactionDate());
          assertEquals("diku", checkoutResponse.getInstitutionId());
          assertEquals(patronIdentifier, checkoutResponse.getPatronIdentifier());
          assertEquals(itemIdentifier, checkoutResponse.getItemIdentifier());
          assertEquals(title, checkoutResponse.getTitleIdentifier());
          assertEquals(OffsetDateTime.now(clock), checkoutResponse.getDueDate());
          assertNull(checkoutResponse.getFeeType());
          assertNull(checkoutResponse.getSecurityInhibit());
          assertNull(checkoutResponse.getCurrencyType());
          assertNull(checkoutResponse.getFeeAmount());
          assertNull(checkoutResponse.getMediaType());
          assertNull(checkoutResponse.getItemProperties());
          assertNull(checkoutResponse.getTransactionId());
          assertNotNull(checkoutResponse.getScreenMessage());
          assertEquals(Collections.singletonList("Password does not match"),
              checkoutResponse.getScreenMessage());
          assertNull(checkoutResponse.getPrintLine());

          testContext.completeNow();
        })));
  }

  @Test
  void cannotRenewRequiredPasswordWithBadPassword(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository itemRepository) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final OffsetDateTime nbDueDate =  OffsetDateTime.now(clock).plusDays(30);
    final String patronIdentifier = "1029384756";
    final String itemIdentifier = "1234567890";
    final String title = "";
    final Renew renew = Renew.builder()
        .noBlock(FALSE)
        .transactionDate(OffsetDateTime.now())
        .nbDueDate(nbDueDate)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .terminalPassword("1234")
        .itemProperties("Some property of this item")
        .patronPassword("7890")
        .feeAcknowledged(FALSE)
        .build();

    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("7890"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder()
        .passwordVerified(FALSE)
        .errorMessages(Collections.singletonList("Password does not match"))
        .build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();
    sessionData.setPatronPasswordVerificationRequired(true);

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, itemRepository, clock);
    circulationRepository.performRenewCommand(renew, sessionData).onComplete(
        testContext.succeeding(renewResponse -> testContext.verify(() -> {
          assertNotNull(renewResponse);
          assertFalse(renewResponse.getOk());
          assertFalse(renewResponse.getRenewalOk());
          assertNull(renewResponse.getMagneticMedia());
          assertEquals(OffsetDateTime.now(clock), renewResponse.getTransactionDate());
          assertEquals("diku", renewResponse.getInstitutionId());
          assertNull(renewResponse.getFeeType());
          assertNull(renewResponse.getSecurityInhibit());
          assertNull(renewResponse.getCurrencyType());
          assertNull(renewResponse.getFeeAmount());
          assertNull(renewResponse.getMediaType());
          assertNull(renewResponse.getItemProperties());
          assertNull(renewResponse.getTransactionId());
          assertNotNull(renewResponse.getScreenMessage());
          assertEquals(Collections.singletonList("Password does not match"),
              renewResponse.getScreenMessage());
          assertNull(renewResponse.getPrintLine());

          testContext.completeNow();
        })));
  }



  private static Stream<Arguments> provideCirculationErrors() {
    return Stream.of(
        Arguments.of("{\n"
            + "  \"errors\" : [ {\n"
            + "    \"message\" : \"Item is already checked out\",\n"
            + "    \"parameters\" : [ {\n"
            + "      \"key\" : \"itemBarcode\",\n"
            + "      \"value\" : \"12345\"\n"
            + "    } ]\n"
            + "  }, {\n"
            + "    \"message\" : \"Item is lost\",\n"
            + "    \"parameters\" : [ {\n"
            + "      \"key\" : \"itemBarcode\",\n"
            + "      \"value\" : \"12345\"\n"
            + "    } ]\n"
            + "  } ]\n"
            + "}", asList("Item is already checked out", "Item is lost")),
        Arguments.of("Not logged in", asList("Not logged in")));
  }


  private static Stream<Arguments> provideCirculationAndSearchErrors() {
    return Stream.of(
      Arguments.of("{\n"
        + "  \"errors\" : [ {\n"
        + "    \"message\" : \"Item is already checked out\",\n"
        + "    \"parameters\" : [ {\n"
        + "      \"key\" : \"itemBarcode\",\n"
        + "      \"value\" : \"12345\"\n"
        + "    } ]\n"
        + "  }, {\n"
        + "    \"message\" : \"Item is lost\",\n"
        + "    \"parameters\" : [ {\n"
        + "      \"key\" : \"itemBarcode\",\n"
        + "      \"value\" : \"12345\"\n"
        + "    } ]\n"
        + "  } ]\n"
        + "}", asList("Item is already checked out", "Item is lost",
                      "Failed while calling mod-search")),
      Arguments.of("Not logged in", asList("Not logged in","Failed while calling mod-search")));
  }

  private static Stream<Arguments> provideCirculationAndTitleNotFound() {
    return Stream.of(
      Arguments.of("{\n"
        + "  \"errors\" : [ {\n"
        + "    \"message\" : \"Item is already checked out\",\n"
        + "    \"parameters\" : [ {\n"
        + "      \"key\" : \"itemBarcode\",\n"
        + "      \"value\" : \"12345\"\n"
        + "    } ]\n"
        + "  }, {\n"
        + "    \"message\" : \"Item is lost\",\n"
        + "    \"parameters\" : [ {\n"
        + "      \"key\" : \"itemBarcode\",\n"
        + "      \"value\" : \"12345\"\n"
        + "    } ]\n"
        + "  } ]\n"
        + "}", asList("Item is already checked out", "Item is lost","Title Not Found")),
      Arguments.of("Not logged in", asList("Not logged in","Title Not Found")));
  }

  @ParameterizedTest
  @MethodSource("provideCirculationErrors")
  void cannotCheckout(String errorMessage, List<String> expectedErrors, Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository itemRepository) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final OffsetDateTime nbDueDate = OffsetDateTime.now().plusDays(30);
    final String patronIdentifier = "1029384756";
    final String itemIdentifier = "453987605438/93";
    final Checkout checkout = Checkout.builder()
        .scRenewalPolicy(FALSE)
        .noBlock(FALSE)
        .transactionDate(OffsetDateTime.now())
        .nbDueDate(nbDueDate)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .terminalPassword("1234")
        .itemProperties("Some property of this item")
        .patronPassword("7890")
        .feeAcknowledged(FALSE)
        .cancel(FALSE)
        .build();

    final JsonObject response = new JsonObject()
        .put("instances", new JsonArray()
        .add(new JsonObject()
          .put("id", "7fbd5d84-62d1-44c6-9c45-6cb173998bbd")
          .put("title","Bridget Jones's Baby: the diaries")
          .put("contributors", new JsonArray().add(new JsonObject().put("name","Fielding,Helen")))))
        .put("totalRecords", 1);

    final String expectedPath = "/search/instances?limit=1&query="
        + "items.barcode%3D%3D%22453987605438%2F93%22";

    when(mockFolioProvider.retrieveResource(
        argThat((IRequestData data) -> data.getPath().equals(expectedPath)
          && data.getHeaders().get("accept").equals("application/json"))))
        .thenReturn(Future.succeededFuture(new FolioResource(response,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    when(mockFolioProvider.createResource(any()))
        .thenReturn(Future.failedFuture(new FolioRequestThrowable(errorMessage)));
    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("7890"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder().build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, itemRepository, clock);
    circulationRepository.performCheckoutCommand(checkout, sessionData).onComplete(
        testContext.succeeding(checkoutResponse -> testContext.verify(() -> {
          assertNotNull(checkoutResponse);
          assertFalse(checkoutResponse.getOk());
          assertFalse(checkoutResponse.getRenewalOk());
          assertNull(checkoutResponse.getMagneticMedia());
          assertFalse(checkoutResponse.getDesensitize());
          assertEquals(OffsetDateTime.now(clock), checkoutResponse.getTransactionDate());
          assertEquals("diku", checkoutResponse.getInstitutionId());
          assertEquals(patronIdentifier, checkoutResponse.getPatronIdentifier());
          assertEquals(itemIdentifier, checkoutResponse.getItemIdentifier());
          assertEquals("Bridget Jones's Baby: the diaries", checkoutResponse.getTitleIdentifier());
          assertEquals(OffsetDateTime.now(clock), checkoutResponse.getDueDate());
          assertNull(checkoutResponse.getFeeType());
          assertNull(checkoutResponse.getSecurityInhibit());
          assertNull(checkoutResponse.getCurrencyType());
          assertNull(checkoutResponse.getFeeAmount());
          assertNull(checkoutResponse.getMediaType());
          assertNull(checkoutResponse.getItemProperties());
          assertNull(checkoutResponse.getTransactionId());
          assertEquals(expectedErrors, checkoutResponse.getScreenMessage());
          assertNull(checkoutResponse.getPrintLine());
          testContext.completeNow();
        })));
  }

  @ParameterizedTest
  @MethodSource("provideCirculationAndSearchErrors")
  void cannotCheckoutGetTitleFailed(String errorMessage, List<String> expectedErrors, Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository itemRepository) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final OffsetDateTime nbDueDate = OffsetDateTime.now().plusDays(30);
    final String patronIdentifier = "1029384756";
    final String itemIdentifier = "453987605438";
    final Checkout checkout = Checkout.builder()
        .scRenewalPolicy(FALSE)
        .noBlock(FALSE)
        .transactionDate(OffsetDateTime.now())
        .nbDueDate(nbDueDate)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .terminalPassword("1234")
        .itemProperties("Some property of this item")
        .patronPassword("7890")
        .feeAcknowledged(FALSE)
        .cancel(FALSE)
        .build();

    final JsonObject response = new JsonObject()
        .put("instances", new JsonArray()
        .add(new JsonObject()
          .put("id", "7fbd5d84-62d1-44c6-9c45-6cb173998bbd")
          .put("title","Bridget Jones's Baby: the diaries")
          .put("contributors", new JsonArray().add(new JsonObject().put("name","Fielding,Helen")))))
        .put("totalRecords", 1);

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.failedFuture(new Exception("Failed while calling mod-search")));

    when(mockFolioProvider.createResource(any()))
        .thenReturn(Future.failedFuture(new FolioRequestThrowable(errorMessage)));
    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("7890"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder().build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, itemRepository, clock);
    circulationRepository.performCheckoutCommand(checkout, sessionData).onComplete(
        testContext.succeeding(checkoutResponse -> testContext.verify(() -> {
          assertNotNull(checkoutResponse);
          assertFalse(checkoutResponse.getOk());
          assertFalse(checkoutResponse.getRenewalOk());
          assertNull(checkoutResponse.getMagneticMedia());
          assertFalse(checkoutResponse.getDesensitize());
          assertEquals(OffsetDateTime.now(clock), checkoutResponse.getTransactionDate());
          assertEquals("diku", checkoutResponse.getInstitutionId());
          assertEquals(patronIdentifier, checkoutResponse.getPatronIdentifier());
          assertEquals(itemIdentifier, checkoutResponse.getItemIdentifier());
          assertEquals("TITLE NOT FOUND", checkoutResponse.getTitleIdentifier());
          assertEquals(OffsetDateTime.now(clock), checkoutResponse.getDueDate());
          assertNull(checkoutResponse.getFeeType());
          assertNull(checkoutResponse.getSecurityInhibit());
          assertNull(checkoutResponse.getCurrencyType());
          assertNull(checkoutResponse.getFeeAmount());
          assertNull(checkoutResponse.getMediaType());
          assertNull(checkoutResponse.getItemProperties());
          assertNull(checkoutResponse.getTransactionId());
          assertEquals(expectedErrors, checkoutResponse.getScreenMessage());
          assertNull(checkoutResponse.getPrintLine());
          testContext.completeNow();
        })));
  }


  @ParameterizedTest
  @MethodSource("provideCirculationAndTitleNotFound")
  void cannotCheckoutAndTitleNotFoundInSearch(String errorMessage, List<String> expectedErrors,
      Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository itemRepository) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final OffsetDateTime nbDueDate = OffsetDateTime.now().plusDays(30);
    final String patronIdentifier = "1029384756";
    final String itemIdentifier = "453987605438";
    final Checkout checkout = Checkout.builder()
        .scRenewalPolicy(FALSE)
        .noBlock(FALSE)
        .transactionDate(OffsetDateTime.now())
        .nbDueDate(nbDueDate)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .terminalPassword("1234")
        .itemProperties("Some property of this item")
        .patronPassword("7890")
        .feeAcknowledged(FALSE)
        .cancel(FALSE)
        .build();

    final JsonObject response = new JsonObject()
        .put("instances", new JsonArray())
        .put("totalRecords", 0);

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(response,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    when(mockFolioProvider.createResource(any()))
        .thenReturn(Future.failedFuture(new FolioRequestThrowable(errorMessage)));
    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("7890"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder().build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, itemRepository, clock);
    circulationRepository.performCheckoutCommand(checkout, sessionData).onComplete(
        testContext.succeeding(checkoutResponse -> testContext.verify(() -> {
          assertNotNull(checkoutResponse);
          assertFalse(checkoutResponse.getOk());
          assertFalse(checkoutResponse.getRenewalOk());
          assertNull(checkoutResponse.getMagneticMedia());
          assertFalse(checkoutResponse.getDesensitize());
          assertEquals(OffsetDateTime.now(clock), checkoutResponse.getTransactionDate());
          assertEquals("diku", checkoutResponse.getInstitutionId());
          assertEquals(patronIdentifier, checkoutResponse.getPatronIdentifier());
          assertEquals(itemIdentifier, checkoutResponse.getItemIdentifier());
          assertEquals("TITLE NOT FOUND", checkoutResponse.getTitleIdentifier());
          assertEquals(OffsetDateTime.now(clock), checkoutResponse.getDueDate());
          assertNull(checkoutResponse.getFeeType());
          assertNull(checkoutResponse.getSecurityInhibit());
          assertNull(checkoutResponse.getCurrencyType());
          assertNull(checkoutResponse.getFeeAmount());
          assertNull(checkoutResponse.getMediaType());
          assertNull(checkoutResponse.getItemProperties());
          assertNull(checkoutResponse.getTransactionId());
          assertEquals(expectedErrors, checkoutResponse.getScreenMessage());
          assertNull(checkoutResponse.getPrintLine());
          testContext.completeNow();
        })));
  }

  @ParameterizedTest
  @MethodSource("provideCirculationAndTitleNotFound")
  void cannotCheckoutAndTitleNotFoundInSearchandReturnNull(String errorMessage,
      List<String> expectedErrors,
      Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository itemRepository) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final OffsetDateTime nbDueDate = OffsetDateTime.now().plusDays(30);
    final String patronIdentifier = "1029384756";
    final String itemIdentifier = "453987605438";
    final Checkout checkout = Checkout.builder()
        .scRenewalPolicy(FALSE)
        .noBlock(FALSE)
        .transactionDate(OffsetDateTime.now())
        .nbDueDate(nbDueDate)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .itemIdentifier(itemIdentifier)
        .terminalPassword("1234")
        .itemProperties("Some property of this item")
        .patronPassword("7890")
        .feeAcknowledged(FALSE)
        .cancel(FALSE)
        .build();

    final JsonObject response = null;

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(response,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    when(mockFolioProvider.createResource(any()))
        .thenReturn(Future.failedFuture(new FolioRequestThrowable(errorMessage)));
    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("7890"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder().build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, itemRepository, clock);
    circulationRepository.performCheckoutCommand(checkout, sessionData).onComplete(
        testContext.succeeding(checkoutResponse -> testContext.verify(() -> {
          assertNotNull(checkoutResponse);
          assertFalse(checkoutResponse.getOk());
          assertFalse(checkoutResponse.getRenewalOk());
          assertNull(checkoutResponse.getMagneticMedia());
          assertFalse(checkoutResponse.getDesensitize());
          assertEquals(OffsetDateTime.now(clock), checkoutResponse.getTransactionDate());
          assertEquals("diku", checkoutResponse.getInstitutionId());
          assertEquals(patronIdentifier, checkoutResponse.getPatronIdentifier());
          assertEquals(itemIdentifier, checkoutResponse.getItemIdentifier());
          assertEquals("TITLE NOT FOUND", checkoutResponse.getTitleIdentifier());
          assertEquals(OffsetDateTime.now(clock), checkoutResponse.getDueDate());
          assertNull(checkoutResponse.getFeeType());
          assertNull(checkoutResponse.getSecurityInhibit());
          assertNull(checkoutResponse.getCurrencyType());
          assertNull(checkoutResponse.getFeeAmount());
          assertNull(checkoutResponse.getMediaType());
          assertNull(checkoutResponse.getItemProperties());
          assertNull(checkoutResponse.getTransactionId());
          assertEquals(expectedErrors, checkoutResponse.getScreenMessage());
          assertNull(checkoutResponse.getPrintLine());

          testContext.completeNow();
        })));
  }

  @Test
  void canGetLoansByUserId(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository itemRepository) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final String userId = UUID.randomUUID().toString();
    final String itemId = UUID.randomUUID().toString();

    final JsonObject response = new JsonObject()
        .put("loans", new JsonArray()
            .add(new JsonObject()
                .put("userId", userId)
                .put("itemId", itemId)
                .put("loanDate", OffsetDateTime.now(clock).format(ISO_OFFSET_DATE_TIME))
                .put("action", "checkedout")))
        .put("totalRecords", 1);

    final String expectedPath = "/circulation/loans?query="
        + Utils.encode("(userId==" + userId + " and status.name=Open)");

    when(mockFolioProvider.retrieveResource(
        argThat((IRequestData data) -> data.getPath().equals(expectedPath))))
        .thenReturn(Future.succeededFuture(new FolioResource(response,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, itemRepository, clock);
    circulationRepository.getLoansByUserId(userId, null, null, sessionData).onComplete(
        testContext.succeeding(loansResponse -> testContext.verify(() -> {
          assertNotNull(loansResponse);
          assertEquals(1, loansResponse.getInteger("totalRecords"));
          final JsonArray loans = loansResponse.getJsonArray("loans");
          assertNotNull(loans);
          final JsonObject loan = loans.getJsonObject(0);
          assertNotNull(loan);
          assertEquals(userId, loan.getString("userId"));
          assertEquals(itemId, loan.getString("itemId"));

          testContext.completeNow();
        })));
  }

  @Test
  void cannotGetLoansByUserId(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository itemRepository) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final String userId = UUID.randomUUID().toString();

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.failedFuture(new NoStackTraceThrowable("cannotGetLoansByUserId")));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, itemRepository, clock);
    circulationRepository.getLoansByUserId(userId, null, null, sessionData).onComplete(
        testContext.succeeding(loansResponse -> testContext.verify(() -> {
          assertNull(loansResponse);
          testContext.completeNow();
        })));
  }

  @Test
  void canGetOverdueLoansByUserId(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository itemRepository) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final String userId = UUID.randomUUID().toString();
    final String itemId = UUID.randomUUID().toString();
    final String dueDate = OffsetDateTime.now(clock).format(ISO_OFFSET_DATE_TIME);

    final JsonObject response = new JsonObject()
        .put("loans", new JsonArray()
            .add(new JsonObject()
                .put("userId", userId)
                .put("itemId", itemId)
                .put("loanDate", dueDate)
                .put("action", "checkedout")))
        .put("totalRecords", 1);

    final String expectedPath = "/circulation/loans?query="
        + Utils.encode("(userId==" + userId + " and status.name=Open and dueDate<"
        + dueDate + ")");

    when(mockFolioProvider.retrieveResource(
        argThat((IRequestData data) -> data.getPath().equals(expectedPath))))
        .thenReturn(Future.succeededFuture(new FolioResource(response,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, itemRepository, clock);
    circulationRepository.getOverdueLoansByUserId(userId, OffsetDateTime.now(clock), null, null,
        sessionData).onComplete(testContext.succeeding(loansResponse -> testContext.verify(() -> {
          assertNotNull(loansResponse);
          assertEquals(1, loansResponse.getInteger("totalRecords"));
          final JsonArray loans = loansResponse.getJsonArray("loans");
          assertNotNull(loans);
          final JsonObject loan = loans.getJsonObject(0);
          assertNotNull(loan);
          assertEquals(userId, loan.getString("userId"));
          assertEquals(itemId, loan.getString("itemId"));
          testContext.completeNow();
        })));
  }

  @Test
  void cannotGetOverdueLoansByUserId(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository itemRepository) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final String userId = UUID.randomUUID().toString();

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.failedFuture(
            new NoStackTraceThrowable("cannotGetOverdueLoansByUserId")));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, itemRepository, clock);
    circulationRepository.getOverdueLoansByUserId(userId, OffsetDateTime.now(clock), null, null,
        sessionData).onComplete(testContext.succeeding(loansResponse -> testContext.verify(() -> {
          assertNull(loansResponse);
          testContext.completeNow();
        })));
  }

  @Test
  void canGetRequestsByItemId(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository itemRepository) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final String userId = UUID.randomUUID().toString();
    final String itemId = UUID.randomUUID().toString();

    final JsonObject response = new JsonObject()
        .put("requests", new JsonArray()
            .add(new JsonObject()
                .put("requesterId", userId)
                .put("itemId", itemId)
                .put("requestType", "Recall")
                .put("requestDate", OffsetDateTime.now(clock).format(ISO_OFFSET_DATE_TIME))
                .put("fulfillmentPreference", "Hold Shelf")))
        .put("totalRecords", 1);

    final String expectedPath = "/circulation/requests?query="
        + Utils.encode("(itemId==" + itemId
        + " and status=Open and requestType==Recall)");

    when(mockFolioProvider.retrieveResource(
        argThat((IRequestData data) -> data.getPath().equals(expectedPath))))
        .thenReturn(Future.succeededFuture(new FolioResource(response,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, itemRepository, clock);
    circulationRepository.getRequestsByItemId(itemId, "Recall", null, null, sessionData)
        .onComplete(testContext.succeeding(requestsResponse -> testContext.verify(() -> {
          assertNotNull(requestsResponse);
          assertEquals(1, requestsResponse.getInteger("totalRecords"));
          final JsonArray requests = requestsResponse.getJsonArray("requests");
          assertNotNull(requests);
          final JsonObject request = requests.getJsonObject(0);
          assertNotNull(request);
          assertEquals(userId, request.getString("requesterId"));
          assertEquals(itemId, request.getString("itemId"));
          testContext.completeNow();
        })));
  }

  @Test
  void cannotGetRequestsByItemId(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository itemRepository) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final String itemId = UUID.randomUUID().toString();

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.failedFuture(new NoStackTraceThrowable("cannotGetRequestsByItemId")));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, itemRepository, clock);
    circulationRepository.getRequestsByItemId(itemId, "Recall", null, null,
        sessionData).onComplete(testContext.succeeding(
            requestsResponse -> testContext.verify(() -> {
              assertNull(requestsResponse);

              testContext.completeNow();
            })));
  }

  @Test
  void canGetRequestsByUserId(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository itemRepository) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final String userId = UUID.randomUUID().toString();
    final String itemId = UUID.randomUUID().toString();

    final JsonObject response = new JsonObject()
        .put("requests", new JsonArray()
            .add(new JsonObject()
                .put("requesterId", userId)
                .put("itemId", itemId)
                .put("requestType", "Hold")
                .put("requestDate", OffsetDateTime.now(clock).format(ISO_OFFSET_DATE_TIME))
                .put("fulfillmentPreference", "Hold Shelf")))
        .put("totalRecords", 1);
    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(response,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, itemRepository, clock);
    circulationRepository.getRequestsByUserId(userId, "Hold", null, null, sessionData)
        .onComplete(testContext.succeeding(requestsResponse -> testContext.verify(() -> {
          assertNotNull(requestsResponse);
          assertEquals(1, requestsResponse.getInteger("totalRecords"));
          final JsonArray requests = requestsResponse.getJsonArray("requests");
          assertNotNull(requests);
          final JsonObject request = requests.getJsonObject(0);
          assertNotNull(request);
          assertEquals(userId, request.getString("requesterId"));
          assertEquals(itemId, request.getString("itemId"));

          testContext.completeNow();
        })));
  }

  @Test
  void cannotGetRequestsByUserId(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock ItemRepository itemRepository) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final String userId = UUID.randomUUID().toString();

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.failedFuture(new NoStackTraceThrowable("cannotGetRequestsByUserId")));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository = new CirculationRepository(
        mockFolioProvider, mockPasswordVerifier, itemRepository, clock);
    circulationRepository.getRequestsByUserId(userId, "Hold", null, null,
        sessionData).onComplete(testContext.succeeding(
            requestsResponse -> testContext.verify(() -> {
              assertNull(requestsResponse);

              testContext.completeNow();
            })));
  }

  @Test
  void testGetAlertType() {
    assertEquals("01", CirculationRepository.getAlertType(
        false, true, false));
    assertEquals("01", CirculationRepository.getAlertType(
        false, false, true));
    assertEquals("02", CirculationRepository.getAlertType(
        true, false, true));
    assertEquals("02", CirculationRepository.getAlertType(
        true, true, false));
    assertEquals("04", CirculationRepository.getAlertType(
        true, false, false));
    assertNull(CirculationRepository.getAlertType(
        false, false, false));
  }
}
