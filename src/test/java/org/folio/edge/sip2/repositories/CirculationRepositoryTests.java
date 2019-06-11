package org.folio.edge.sip2.repositories;

import static java.lang.Boolean.FALSE;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.impl.NoStackTraceThrowable;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.requests.Checkin;
import org.folio.edge.sip2.domain.messages.requests.Checkout;
import org.folio.edge.sip2.session.SessionData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
public class CirculationRepositoryTests {

  @Test
  public void canCreateCirculationRepository(
      @Mock IResourceProvider<IRequestData> mockFolioResource, @Mock Clock clock) {
    final CirculationRepository circulationRepository =
        new CirculationRepository(mockFolioResource, clock);

    assertNotNull(circulationRepository);
  }

  @Test
  public void cannotCreateCirculationRepositoryWhenResourceProviderIsNull() {
    final NullPointerException thrown = assertThrows(
        NullPointerException.class,
        () -> new CirculationRepository(null, null));

    assertEquals("Resource provider cannot be null", thrown.getMessage());
  }

  @Test
  public void canCheckin(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
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

    final JsonObject response = new JsonObject()
        .put("item", new JsonObject()
            .put("location", new JsonObject()
                .put("name", "Main Library")));
    when(mockFolioProvider.createResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(response,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository =
        new CirculationRepository(mockFolioProvider, clock);
    circulationRepository.performCheckinCommand(checkin, sessionData).setHandler(
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
          assertNull(checkinResponse.getTitleIdentifier());
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
  public void canCheckinWithTitleIdentifier(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final OffsetDateTime returnDate = OffsetDateTime.now();
    final String currentLocation = UUID.randomUUID().toString();
    final String itemIdentifier = "1234567890";
    final String titleIdentifier = "Some Cool Book";
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

    final JsonObject response = new JsonObject()
        .put("item", new JsonObject()
            .put("title", titleIdentifier)
            .put("location", new JsonObject()
                .put("name", "Main Library")));
    when(mockFolioProvider.createResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(response,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository =
        new CirculationRepository(mockFolioProvider, clock);
    circulationRepository.performCheckinCommand(checkin, sessionData).setHandler(
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
          assertEquals(titleIdentifier, checkinResponse.getTitleIdentifier());
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
  public void cannotCheckin(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
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

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository =
        new CirculationRepository(mockFolioProvider, clock);
    circulationRepository.performCheckinCommand(checkin, sessionData).setHandler(
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
          assertNull(checkinResponse.getTitleIdentifier());
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
  public void canCheckout(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
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

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository =
        new CirculationRepository(mockFolioProvider, clock);
    circulationRepository.performCheckoutCommand(checkout, sessionData).setHandler(
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

  @ParameterizedTest
  @MethodSource("provideCirculationErrors")
  void cannotCheckout(String errorMessage, List<String> expectedErrors, Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final OffsetDateTime nbDueDate = OffsetDateTime.now().plusDays(30);
    final String patronIdentifier = "1029384756";
    final String itemIdentifier = "1234567890";
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

    when(mockFolioProvider.createResource(any()))
        .thenReturn(Future.failedFuture(new FolioRequestThrowable(errorMessage)));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository =
        new CirculationRepository(mockFolioProvider, clock);
    circulationRepository.performCheckoutCommand(checkout, sessionData).setHandler(
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
          assertEquals("", checkoutResponse.getTitleIdentifier());
          assertNull(checkoutResponse.getDueDate());
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
  public void canGetLoansByUserId(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
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
    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(response,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository =
        new CirculationRepository(mockFolioProvider, clock);
    circulationRepository.getLoansByUserId(userId, null, null, sessionData).setHandler(
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
  public void cannotGetLoansByUserId(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final String userId = UUID.randomUUID().toString();

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.failedFuture(new NoStackTraceThrowable("cannotGetLoansByUserId")));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository =
        new CirculationRepository(mockFolioProvider, clock);
    circulationRepository.getLoansByUserId(userId, null, null, sessionData).setHandler(
        testContext.succeeding(loansResponse -> testContext.verify(() -> {
          assertNull(loansResponse);

          testContext.completeNow();
        })));
  }

  @Test
  public void canGetOverdueLoansByUserId(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
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
    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(response,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository =
        new CirculationRepository(mockFolioProvider, clock);
    circulationRepository.getOverdueLoansByUserId(userId, OffsetDateTime.now(clock), null, null,
        sessionData).setHandler(testContext.succeeding(loansResponse -> testContext.verify(() -> {
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
  public void cannotGetOverdueLoansByUserId(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final String userId = UUID.randomUUID().toString();

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.failedFuture(
            new NoStackTraceThrowable("cannotGetOverdueLoansByUserId")));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository =
        new CirculationRepository(mockFolioProvider, clock);
    circulationRepository.getOverdueLoansByUserId(userId, OffsetDateTime.now(clock), null, null,
        sessionData).setHandler(testContext.succeeding(loansResponse -> testContext.verify(() -> {
          assertNull(loansResponse);

          testContext.completeNow();
        })));
  }

  @Test
  public void canGetRequestsByItemId(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
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
                .put("fulfilmentPreference", "Hold Shelf")))
        .put("totalRecords", 1);
    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(response,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository =
        new CirculationRepository(mockFolioProvider, clock);
    circulationRepository.getRequestsByItemId(itemId, "Recall", null, null, sessionData)
        .setHandler(testContext.succeeding(requestsResponse -> testContext.verify(() -> {
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
  public void cannotGetRequestsByItemId(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final String itemId = UUID.randomUUID().toString();

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.failedFuture(new NoStackTraceThrowable("cannotGetRequestsByItemId")));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository =
        new CirculationRepository(mockFolioProvider, clock);
    circulationRepository.getRequestsByItemId(itemId, "Recall", null, null,
        sessionData).setHandler(testContext.succeeding(
            requestsResponse -> testContext.verify(() -> {
              assertNull(requestsResponse);

              testContext.completeNow();
            })));
  }

  @Test
  public void canGetRequestsByUserId(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
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
                .put("fulfilmentPreference", "Hold Shelf")))
        .put("totalRecords", 1);
    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(response,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository =
        new CirculationRepository(mockFolioProvider, clock);
    circulationRepository.getRequestsByItemId(itemId, "Hold", null, null, sessionData)
        .setHandler(testContext.succeeding(requestsResponse -> testContext.verify(() -> {
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
  public void cannotGetRequestsByUserId(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final String userId = UUID.randomUUID().toString();

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.failedFuture(new NoStackTraceThrowable("cannotGetRequestsByUserId")));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final CirculationRepository circulationRepository =
        new CirculationRepository(mockFolioProvider, clock);
    circulationRepository.getRequestsByItemId(userId, "Hold", null, null,
        sessionData).setHandler(testContext.succeeding(
            requestsResponse -> testContext.verify(() -> {
              assertNull(requestsResponse);

              testContext.completeNow();
            })));
  }
}
