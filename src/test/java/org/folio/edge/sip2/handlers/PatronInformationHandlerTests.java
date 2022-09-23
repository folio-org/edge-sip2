package org.folio.edge.sip2.handlers;

import static java.lang.Boolean.TRUE;
import static org.folio.edge.sip2.domain.messages.enumerations.Language.ENGLISH;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.HOLD_ITEMS;
import static org.folio.edge.sip2.parser.Command.PATRON_INFORMATION_RESPONSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.requests.PatronInformation;
import org.folio.edge.sip2.domain.messages.responses.PatronInformationResponse;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.repositories.PatronRepository;
import org.folio.edge.sip2.session.SessionData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
public class PatronInformationHandlerTests {
  @Test
  public void canExecuteASamplePatronInformationUsingHandler(
      @Mock PatronRepository mockPatronRepository,
      Vertx vertx,
      VertxTestContext testContext) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String institutionId = "diku";
    final String patronIdentifier = "123456";
    final Integer holdItemsCount = Integer.valueOf(5);
    final Integer overdueItemsCount = Integer.valueOf(1);
    final Integer recallItemsCount = Integer.valueOf(1);
    final Integer chargedItemsCount = 3;
    final Integer fineItemsCount = 1;
    final String personalName = "Some Guy";
    final List<String> holdItems = Arrays.asList("Book2", "Book3", "Book4");
    final String homeAddress = "1234 Fake St., Anytown US";
    final String emailAddress = "jdoe@example.com";
    final String homePhoneNumber = "555-1234";
    final String screenMessage = "This is a screen message";
    final String printLine = "This is a print line";
    final PatronInformation patronInformation = PatronInformation.builder()
        .language(ENGLISH)
        .transactionDate(OffsetDateTime.now(clock))
        .summary(HOLD_ITEMS)
        .institutionId(institutionId)
        .patronIdentifier(patronIdentifier)
        .terminalPassword("1234")
        .patronPassword("xyzzy")
        .startItem(Integer.valueOf(2))
        .endItem(Integer.valueOf(4))
        .build();
    when(mockPatronRepository.performPatronInformationCommand(any(), any()))
        .thenReturn(Future.succeededFuture(PatronInformationResponse.builder()
            .patronStatus(null)
            .language(ENGLISH)
            .transactionDate(OffsetDateTime.now(clock).plusSeconds(5))
            .holdItemsCount(holdItemsCount)
            .overdueItemsCount(overdueItemsCount)
            .chargedItemsCount(3)
            .fineItemsCount(1)
            .recallItemsCount(recallItemsCount)
            .unavailableHoldsCount(null)
            .institutionId(institutionId)
            .patronIdentifier(patronIdentifier)
            .personalName(personalName)
            .holdItemsLimit(null)
            .overdueItemsLimit(null)
            .chargedItemsLimit(null)
            .validPatron(TRUE)
            .validPatronPassword(null)
            .currencyType(null)
            .feeAmount(null)
            .feeLimit(null)
            .holdItems(holdItems)
            .overdueItems(Collections.emptyList())
            .chargedItems(Collections.emptyList())
            .fineItems(Collections.emptyList())
            .recallItems(Collections.emptyList())
            .unavailableHoldItems(Collections.emptyList())
            .homeAddress(homeAddress)
            .emailAddress(emailAddress)
            .homePhoneNumber(homePhoneNumber)
            .screenMessage(Arrays.asList(screenMessage))
            .printLine(Arrays.asList(printLine))
            .build()));

    final PatronInformationHandler handler = new PatronInformationHandler(mockPatronRepository,
        FreemarkerRepository.getInstance().getFreemarkerTemplate(PATRON_INFORMATION_RESPONSE));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    handler.execute(patronInformation, sessionData).onComplete(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          final String expectedString = "64              001"
              + TestUtils.getFormattedLocalDateTime(OffsetDateTime.now(clock).plusSeconds(5))
              + String.format("%04d%04d%04d%04d%04d    ",
                  holdItemsCount, overdueItemsCount, chargedItemsCount,
                  fineItemsCount, recallItemsCount)
              + String.format("AO%s|AA%s|AE%s|BLY|", institutionId, patronIdentifier, personalName)
              + String.format("AS%s|AS%s|AS%s|", holdItems.toArray(new Object[holdItems.size()]))
              + String.format("BD%s|BE%s|BF%s|", homeAddress, emailAddress, homePhoneNumber)
              + String.format("AF%s|AG%s|", screenMessage, printLine);

          assertEquals(expectedString, sipMessage);
          testContext.completeNow();
        })));
  }

  @Test
  public void cannotCreateHandlerDueToMissingPatronRepository() {
    final NullPointerException thrown = assertThrows(
        NullPointerException.class,
        () -> new PatronInformationHandler(null, null));

    assertEquals("PatronRepository cannot be null", thrown.getMessage());
  }

  @Test
  public void cannotCreateHandlerDueToMissingTemplate(@Mock PatronRepository mockPatronRepository) {
    final NullPointerException thrown = assertThrows(NullPointerException.class,
        () -> new PatronInformationHandler(mockPatronRepository, null));

    assertEquals("Template cannot be null", thrown.getMessage());
  }
}
