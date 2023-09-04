package org.folio.edge.sip2.handlers;

import static org.folio.edge.sip2.parser.Command.ITEM_INFORMATION_RESPONSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.time.Clock;
import java.time.OffsetDateTime;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.enumerations.CirculationStatus;
import org.folio.edge.sip2.domain.messages.requests.ItemInformation;
import org.folio.edge.sip2.domain.messages.responses.ItemInformationResponse;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.repositories.ItemRepository;
import org.folio.edge.sip2.session.SessionData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;



@ExtendWith({VertxExtension.class, MockitoExtension.class})
class ItemInformationHandlerTests {
  @Test
  void canExecuteASampleItemInformationUsingHandler(
         @Mock ItemRepository mockItemRepository,
      VertxTestContext testContext) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final String institutionId = "diku";
    final String itemIdentifier = "1234567890";
    final ItemInformation itemInformation = ItemInformation.builder()
        .transactionDate(OffsetDateTime.now())
        .institutionId(institutionId)
        .itemIdentifier(itemIdentifier)
        .terminalPassword("1234")
        .build();

    when(mockItemRepository.performItemInformationCommand(any(), any()))
        .thenReturn(Future.succeededFuture(ItemInformationResponse.builder()
        .transactionDate(OffsetDateTime.now(clock))
        .itemIdentifier(itemIdentifier)
        .permanentLocation("Main Library")
        .circulationStatus(CirculationStatus.AVAILABLE)
        .build()));

    final ItemInformationHandler handler = new ItemInformationHandler(mockItemRepository,
        FreemarkerRepository.getInstance().getFreemarkerTemplate(ITEM_INFORMATION_RESPONSE));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    handler.execute(itemInformation, sessionData).onComplete(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          final String expectedString = "18030001"
              + TestUtils.getFormattedLocalDateTime(OffsetDateTime.now(clock))
              + "AB" + itemIdentifier + "|AJ|AQMain Library|AP|";

          assertEquals(expectedString, sipMessage);

          testContext.completeNow();
        })));
  }

  @Test
  void canExecuteASampleFailedItemInformationUsingHandler(
      @Mock ItemRepository itemRepository,
      VertxTestContext testContext) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final String institutionId = "diku";
    final String itemIdentifier = "1234567890";
    final ItemInformation itemInformation = ItemInformation.builder()
        .transactionDate(OffsetDateTime.now())
        .institutionId(institutionId)
        .itemIdentifier(itemIdentifier)
        .terminalPassword("1234")
        .build();

    when(itemRepository.performItemInformationCommand(any(), any()))
        .thenReturn(Future.failedFuture("Item does not exists."));

    final ItemInformationHandler handler = new ItemInformationHandler(itemRepository,
        FreemarkerRepository.getInstance().getFreemarkerTemplate(ITEM_INFORMATION_RESPONSE));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    handler.execute(itemInformation, sessionData).onComplete(
        testContext.failing(throwable -> testContext.verify(() -> {
          assertEquals("Item does not exists.", throwable.getMessage());
          testContext.completeNow();
        })));
  }

  @Test
  void cannotCreateHandlerDueToMissingItemRepository() {
    final NullPointerException thrown = assertThrows(
        NullPointerException.class,
        () -> new ItemInformationHandler(null, null));

    assertEquals("ItemRepository cannot be null", thrown.getMessage());
  }

  @Test
  void cannotCreateHandlerDueToMissingTemplate(@Mock ItemRepository mock) {
    final NullPointerException thrown = assertThrows(NullPointerException.class,
        () -> new ItemInformationHandler(mock, null));

    assertEquals("Template cannot be null", thrown.getMessage());
  }
}
