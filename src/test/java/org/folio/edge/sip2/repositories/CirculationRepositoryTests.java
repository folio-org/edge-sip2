package org.folio.edge.sip2.repositories;

import static java.lang.Boolean.FALSE;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
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
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.folio.edge.sip2.domain.messages.requests.Checkin;
import org.folio.edge.sip2.domain.messages.requests.Checkout;
import org.folio.edge.sip2.session.SessionData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final ZonedDateTime returnDate = ZonedDateTime.now();
    final String currentLocation = UUID.randomUUID().toString();
    final String itemIdentifier = "1234567890";
    final Checkin checkin = Checkin.builder()
        .noBlock(FALSE)
        .transactionDate(ZonedDateTime.now())
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

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    final CirculationRepository circulationRepository =
        new CirculationRepository(mockFolioProvider, clock);
    circulationRepository.checkin(checkin, sessionData).setHandler(
        testContext.succeeding(checkinResponse -> testContext.verify(() -> {
          assertNotNull(checkinResponse);
          assertTrue(checkinResponse.getOk());
          assertTrue(checkinResponse.getResensitize());
          assertNull(checkinResponse.getMagneticMedia());
          assertFalse(checkinResponse.getAlert());
          assertEquals(ZonedDateTime.now(clock), checkinResponse.getTransactionDate());
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
  public void cannotCheckin(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final ZonedDateTime returnDate = ZonedDateTime.now();
    final String currentLocation = UUID.randomUUID().toString();
    final String itemIdentifier = "1234567890";
    final Checkin checkin = Checkin.builder()
        .noBlock(FALSE)
        .transactionDate(ZonedDateTime.now())
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

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    final CirculationRepository circulationRepository =
        new CirculationRepository(mockFolioProvider, clock);
    circulationRepository.checkin(checkin, sessionData).setHandler(
        testContext.succeeding(checkinResponse -> testContext.verify(() -> {
          assertNotNull(checkinResponse);
          assertFalse(checkinResponse.getOk());
          assertFalse(checkinResponse.getResensitize());
          assertNull(checkinResponse.getMagneticMedia());
          assertFalse(checkinResponse.getAlert());
          assertEquals(ZonedDateTime.now(clock), checkinResponse.getTransactionDate());
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
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final ZonedDateTime nbDueDate = ZonedDateTime.now().plusDays(30);
    final String patronIdentifier = "1029384756";
    final String itemIdentifier = "1234567890";
    final String title = "Some book";
    final Checkout checkout = Checkout.builder()
        .scRenewalPolicy(FALSE)
        .noBlock(FALSE)
        .transactionDate(ZonedDateTime.now())
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

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    final CirculationRepository circulationRepository =
        new CirculationRepository(mockFolioProvider, clock);
    circulationRepository.checkout(checkout, sessionData).setHandler(
        testContext.succeeding(checkoutResponse -> testContext.verify(() -> {
          assertNotNull(checkoutResponse);
          assertTrue(checkoutResponse.getOk());
          assertFalse(checkoutResponse.getRenewalOk());
          assertNull(checkoutResponse.getMagneticMedia());
          assertTrue(checkoutResponse.getDesensitize());
          assertEquals(ZonedDateTime.now(clock), checkoutResponse.getTransactionDate());
          assertEquals("diku", checkoutResponse.getInstitutionId());
          assertEquals(patronIdentifier, checkoutResponse.getPatronIdentifier());
          assertEquals(itemIdentifier, checkoutResponse.getItemIdentifier());
          assertEquals(title, checkoutResponse.getTitleIdentifier());
          assertEquals(nbDueDate.toOffsetDateTime(),
              checkoutResponse.getDueDate().toOffsetDateTime());
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
  public void cannotCheckout(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final ZonedDateTime nbDueDate = ZonedDateTime.now().plusDays(30);
    final String patronIdentifier = "1029384756";
    final String itemIdentifier = "1234567890";
    final Checkout checkout = Checkout.builder()
        .scRenewalPolicy(FALSE)
        .noBlock(FALSE)
        .transactionDate(ZonedDateTime.now())
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
        .thenReturn(Future.failedFuture(new NoStackTraceThrowable("Test failure")));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    final CirculationRepository circulationRepository =
        new CirculationRepository(mockFolioProvider, clock);
    circulationRepository.checkout(checkout, sessionData).setHandler(
        testContext.succeeding(checkoutResponse -> testContext.verify(() -> {
          assertNotNull(checkoutResponse);
          assertFalse(checkoutResponse.getOk());
          assertFalse(checkoutResponse.getRenewalOk());
          assertNull(checkoutResponse.getMagneticMedia());
          assertFalse(checkoutResponse.getDesensitize());
          assertEquals(ZonedDateTime.now(clock), checkoutResponse.getTransactionDate());
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
          assertNull(checkoutResponse.getScreenMessage());
          assertNull(checkoutResponse.getPrintLine());

          testContext.completeNow();
        })));
  }

  @Test
  public void canGetLoansByUserId(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String userId = UUID.randomUUID().toString();
    final String itemId = UUID.randomUUID().toString();

    final JsonObject response = new JsonObject()
        .put("loans", new JsonArray()
            .add(new JsonObject()
                .put("userId", userId)
                .put("itemId", itemId)
                .put("loanDate", ZonedDateTime.now(clock).format(ISO_OFFSET_DATE_TIME))
                .put("action", "checkedout")))
        .put("totalRecords", 1);
    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(response,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

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
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String userId = UUID.randomUUID().toString();

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.failedFuture(new NoStackTraceThrowable("cannotGetLoansByUserId")));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

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
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String userId = UUID.randomUUID().toString();
    final String itemId = UUID.randomUUID().toString();

    final JsonObject response = new JsonObject()
        .put("loans", new JsonArray()
            .add(new JsonObject()
                .put("userId", userId)
                .put("itemId", itemId)
                .put("loanDate", ZonedDateTime.now(clock).format(ISO_OFFSET_DATE_TIME))
                .put("action", "checkedout")))
        .put("totalRecords", 1);
    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(response,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    final CirculationRepository circulationRepository =
        new CirculationRepository(mockFolioProvider, clock);
    circulationRepository.getOverdueLoansByUserId(userId, ZonedDateTime.now(clock), null, null,
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
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String userId = UUID.randomUUID().toString();

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.failedFuture(
            new NoStackTraceThrowable("cannotGetOverdueLoansByUserId")));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    final CirculationRepository circulationRepository =
        new CirculationRepository(mockFolioProvider, clock);
    circulationRepository.getOverdueLoansByUserId(userId, ZonedDateTime.now(clock), null, null,
        sessionData).setHandler(testContext.succeeding(loansResponse -> testContext.verify(() -> {
          assertNull(loansResponse);

          testContext.completeNow();
        })));
  }

  @Test
  public void canGetRequestsByItemId(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String userId = UUID.randomUUID().toString();
    final String itemId = UUID.randomUUID().toString();

    final JsonObject response = new JsonObject()
        .put("requests", new JsonArray()
            .add(new JsonObject()
                .put("requesterId", userId)
                .put("itemId", itemId)
                .put("requestType", "Recall")
                .put("requestDate", ZonedDateTime.now(clock).format(ISO_OFFSET_DATE_TIME))
                .put("fulfilmentPreference", "Hold Shelf")))
        .put("totalRecords", 1);
    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(response,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

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
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String itemId = UUID.randomUUID().toString();

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.failedFuture(new NoStackTraceThrowable("cannotGetRequestsByItemId")));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

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
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String userId = UUID.randomUUID().toString();
    final String itemId = UUID.randomUUID().toString();

    final JsonObject response = new JsonObject()
        .put("requests", new JsonArray()
            .add(new JsonObject()
                .put("requesterId", userId)
                .put("itemId", itemId)
                .put("requestType", "Hold")
                .put("requestDate", ZonedDateTime.now(clock).format(ISO_OFFSET_DATE_TIME))
                .put("fulfilmentPreference", "Hold Shelf")))
        .put("totalRecords", 1);
    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(response,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

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
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String userId = UUID.randomUUID().toString();

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.failedFuture(new NoStackTraceThrowable("cannotGetRequestsByUserId")));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

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
