package org.folio.edge.sip2.repositories;

import static java.lang.Boolean.FALSE;
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
}
