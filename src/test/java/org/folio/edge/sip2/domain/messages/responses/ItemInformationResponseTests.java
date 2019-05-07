package org.folio.edge.sip2.domain.messages.responses;

import static java.util.Arrays.asList;
import static org.folio.edge.sip2.domain.messages.enumerations.CirculationStatus.AVAILABLE;
import static org.folio.edge.sip2.domain.messages.enumerations.CirculationStatus.MISSING;
import static org.folio.edge.sip2.domain.messages.enumerations.CurrencyType.GBP;
import static org.folio.edge.sip2.domain.messages.enumerations.CurrencyType.USD;
import static org.folio.edge.sip2.domain.messages.enumerations.FeeType.DAMAGE;
import static org.folio.edge.sip2.domain.messages.enumerations.FeeType.RENTAL;
import static org.folio.edge.sip2.domain.messages.enumerations.MediaType.OTHER;
import static org.folio.edge.sip2.domain.messages.enumerations.MediaType.VIDEO_TAPE;
import static org.folio.edge.sip2.domain.messages.enumerations.SecurityMarker.NONE;
import static org.folio.edge.sip2.domain.messages.enumerations.SecurityMarker.WHISPER_TAPE;
import static org.folio.edge.sip2.domain.messages.responses.ItemInformationResponse.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;
import java.util.List;
import org.folio.edge.sip2.domain.messages.enumerations.CirculationStatus;
import org.folio.edge.sip2.domain.messages.enumerations.CurrencyType;
import org.folio.edge.sip2.domain.messages.enumerations.FeeType;
import org.folio.edge.sip2.domain.messages.enumerations.MediaType;
import org.folio.edge.sip2.domain.messages.enumerations.SecurityMarker;
import org.junit.jupiter.api.Test;

class ItemInformationResponseTests {
  final CirculationStatus circulationStatus = AVAILABLE;
  final SecurityMarker securityMarker = NONE;
  final FeeType feeType = RENTAL;
  final ZonedDateTime transactionDate = ZonedDateTime.now();
  final Integer holdQueueLength = Integer.valueOf(3);
  final ZonedDateTime dueDate = transactionDate.plusDays(30);
  final ZonedDateTime recallDate = transactionDate.plusDays(60);
  final ZonedDateTime holdPickupDate = transactionDate.plusDays(90);
  final String itemIdentifier = "8675309";
  final String titleIdentifier = "5551212";
  final String owner = "University Library";
  final CurrencyType currencyType = USD;
  final String feeAmount = "2.50";
  final MediaType mediaType = VIDEO_TAPE;
  final String permanentLocation = "circ_desk";
  final String currentLocation = "another_circ_desk";
  final String itemProperties = "Directors Cut";
  final List<String> screenMessage = asList("Please rewind");
  final List<String> printLine = asList("Enjoy!");

  @Test
  void testGetCirculationStatus() {
    final ItemInformationResponse iir = builder()
        .circulationStatus(circulationStatus)
        .build();
    assertEquals(circulationStatus, iir.getCirculationStatus());
    assertNull(iir.getSecurityMarker());
    assertNull(iir.getFeeType());
    assertNull(iir.getTransactionDate());
    assertNull(iir.getHoldQueueLength());
    assertNull(iir.getDueDate());
    assertNull(iir.getRecallDate());
    assertNull(iir.getHoldPickupDate());
    assertNull(iir.getItemIdentifier());
    assertNull(iir.getTitleIdentifier());
    assertNull(iir.getOwner());
    assertNull(iir.getCurrencyType());
    assertNull(iir.getFeeAmount());
    assertNull(iir.getMediaType());
    assertNull(iir.getPermanentLocation());
    assertNull(iir.getCurrentLocation());
    assertNull(iir.getItemProperties());
    assertNull(iir.getScreenMessage());
    assertNull(iir.getPrintLine());
  }

  @Test
  void testGetSecurityMarker() {
    final ItemInformationResponse iir = builder()
        .securityMarker(securityMarker)
        .build();
    assertNull(iir.getCirculationStatus());
    assertEquals(securityMarker, iir.getSecurityMarker());
    assertNull(iir.getFeeType());
    assertNull(iir.getTransactionDate());
    assertNull(iir.getHoldQueueLength());
    assertNull(iir.getDueDate());
    assertNull(iir.getRecallDate());
    assertNull(iir.getHoldPickupDate());
    assertNull(iir.getItemIdentifier());
    assertNull(iir.getTitleIdentifier());
    assertNull(iir.getOwner());
    assertNull(iir.getCurrencyType());
    assertNull(iir.getFeeAmount());
    assertNull(iir.getMediaType());
    assertNull(iir.getPermanentLocation());
    assertNull(iir.getCurrentLocation());
    assertNull(iir.getItemProperties());
    assertNull(iir.getScreenMessage());
    assertNull(iir.getPrintLine());
  }

  @Test
  void testGetFeeType() {
    final ItemInformationResponse iir = builder().feeType(feeType).build();
    assertNull(iir.getCirculationStatus());
    assertNull(iir.getSecurityMarker());
    assertEquals(feeType, iir.getFeeType());
    assertNull(iir.getTransactionDate());
    assertNull(iir.getHoldQueueLength());
    assertNull(iir.getDueDate());
    assertNull(iir.getRecallDate());
    assertNull(iir.getHoldPickupDate());
    assertNull(iir.getItemIdentifier());
    assertNull(iir.getTitleIdentifier());
    assertNull(iir.getOwner());
    assertNull(iir.getCurrencyType());
    assertNull(iir.getFeeAmount());
    assertNull(iir.getMediaType());
    assertNull(iir.getPermanentLocation());
    assertNull(iir.getCurrentLocation());
    assertNull(iir.getItemProperties());
    assertNull(iir.getScreenMessage());
    assertNull(iir.getPrintLine());
  }

  @Test
  void testGetTransactionDate() {
    final ItemInformationResponse iir = builder()
        .transactionDate(transactionDate)
        .build();
    assertNull(iir.getCirculationStatus());
    assertNull(iir.getSecurityMarker());
    assertNull(iir.getFeeType());
    assertEquals(transactionDate, iir.getTransactionDate());
    assertNull(iir.getHoldQueueLength());
    assertNull(iir.getDueDate());
    assertNull(iir.getRecallDate());
    assertNull(iir.getHoldPickupDate());
    assertNull(iir.getItemIdentifier());
    assertNull(iir.getTitleIdentifier());
    assertNull(iir.getOwner());
    assertNull(iir.getCurrencyType());
    assertNull(iir.getFeeAmount());
    assertNull(iir.getMediaType());
    assertNull(iir.getPermanentLocation());
    assertNull(iir.getCurrentLocation());
    assertNull(iir.getItemProperties());
    assertNull(iir.getScreenMessage());
    assertNull(iir.getPrintLine());
  }

  @Test
  void testGetHoldQueueLength() {
    final ItemInformationResponse iir = builder()
        .holdQueueLength(holdQueueLength)
        .build();
    assertNull(iir.getCirculationStatus());
    assertNull(iir.getSecurityMarker());
    assertNull(iir.getFeeType());
    assertNull(iir.getTransactionDate());
    assertEquals(holdQueueLength, iir.getHoldQueueLength());
    assertNull(iir.getDueDate());
    assertNull(iir.getRecallDate());
    assertNull(iir.getHoldPickupDate());
    assertNull(iir.getItemIdentifier());
    assertNull(iir.getTitleIdentifier());
    assertNull(iir.getOwner());
    assertNull(iir.getCurrencyType());
    assertNull(iir.getFeeAmount());
    assertNull(iir.getMediaType());
    assertNull(iir.getPermanentLocation());
    assertNull(iir.getCurrentLocation());
    assertNull(iir.getItemProperties());
    assertNull(iir.getScreenMessage());
    assertNull(iir.getPrintLine());
  }

  @Test
  void testGetDueDate() {
    final ItemInformationResponse iir = builder().dueDate(dueDate).build();
    assertNull(iir.getCirculationStatus());
    assertNull(iir.getSecurityMarker());
    assertNull(iir.getFeeType());
    assertNull(iir.getTransactionDate());
    assertNull(iir.getHoldQueueLength());
    assertEquals(dueDate, iir.getDueDate());
    assertNull(iir.getRecallDate());
    assertNull(iir.getHoldPickupDate());
    assertNull(iir.getItemIdentifier());
    assertNull(iir.getTitleIdentifier());
    assertNull(iir.getOwner());
    assertNull(iir.getCurrencyType());
    assertNull(iir.getFeeAmount());
    assertNull(iir.getMediaType());
    assertNull(iir.getPermanentLocation());
    assertNull(iir.getCurrentLocation());
    assertNull(iir.getItemProperties());
    assertNull(iir.getScreenMessage());
    assertNull(iir.getPrintLine());
  }

  @Test
  void testGetRecallDate() {
    final ItemInformationResponse iir = builder()
        .recallDate(recallDate)
        .build();
    assertNull(iir.getCirculationStatus());
    assertNull(iir.getSecurityMarker());
    assertNull(iir.getFeeType());
    assertNull(iir.getTransactionDate());
    assertNull(iir.getHoldQueueLength());
    assertNull(iir.getDueDate());
    assertEquals(recallDate, iir.getRecallDate());
    assertNull(iir.getHoldPickupDate());
    assertNull(iir.getItemIdentifier());
    assertNull(iir.getTitleIdentifier());
    assertNull(iir.getOwner());
    assertNull(iir.getCurrencyType());
    assertNull(iir.getFeeAmount());
    assertNull(iir.getMediaType());
    assertNull(iir.getPermanentLocation());
    assertNull(iir.getCurrentLocation());
    assertNull(iir.getItemProperties());
    assertNull(iir.getScreenMessage());
    assertNull(iir.getPrintLine());
  }

  @Test
  void testGetHoldPickupDate() {
    final ItemInformationResponse iir = builder()
        .holdPickupDate(holdPickupDate)
        .build();
    assertNull(iir.getCirculationStatus());
    assertNull(iir.getSecurityMarker());
    assertNull(iir.getFeeType());
    assertNull(iir.getTransactionDate());
    assertNull(iir.getHoldQueueLength());
    assertNull(iir.getDueDate());
    assertNull(iir.getRecallDate());
    assertEquals(holdPickupDate, iir.getHoldPickupDate());
    assertNull(iir.getItemIdentifier());
    assertNull(iir.getTitleIdentifier());
    assertNull(iir.getOwner());
    assertNull(iir.getCurrencyType());
    assertNull(iir.getFeeAmount());
    assertNull(iir.getMediaType());
    assertNull(iir.getPermanentLocation());
    assertNull(iir.getCurrentLocation());
    assertNull(iir.getItemProperties());
    assertNull(iir.getScreenMessage());
    assertNull(iir.getPrintLine());
  }

  @Test
  void testGetItemIdentifier() {
    final ItemInformationResponse iir = builder()
        .itemIdentifier(itemIdentifier)
        .build();
    assertNull(iir.getCirculationStatus());
    assertNull(iir.getSecurityMarker());
    assertNull(iir.getFeeType());
    assertNull(iir.getTransactionDate());
    assertNull(iir.getHoldQueueLength());
    assertNull(iir.getDueDate());
    assertNull(iir.getRecallDate());
    assertNull(iir.getHoldPickupDate());
    assertEquals(itemIdentifier, iir.getItemIdentifier());
    assertNull(iir.getTitleIdentifier());
    assertNull(iir.getOwner());
    assertNull(iir.getCurrencyType());
    assertNull(iir.getFeeAmount());
    assertNull(iir.getMediaType());
    assertNull(iir.getPermanentLocation());
    assertNull(iir.getCurrentLocation());
    assertNull(iir.getItemProperties());
    assertNull(iir.getScreenMessage());
    assertNull(iir.getPrintLine());
  }

  @Test
  void testGetTitleIdentifier() {
    final ItemInformationResponse iir = builder()
        .titleIdentifier(titleIdentifier)
        .build();
    assertNull(iir.getCirculationStatus());
    assertNull(iir.getSecurityMarker());
    assertNull(iir.getFeeType());
    assertNull(iir.getTransactionDate());
    assertNull(iir.getHoldQueueLength());
    assertNull(iir.getDueDate());
    assertNull(iir.getRecallDate());
    assertNull(iir.getHoldPickupDate());
    assertNull(iir.getItemIdentifier());
    assertEquals(titleIdentifier, iir.getTitleIdentifier());
    assertNull(iir.getOwner());
    assertNull(iir.getCurrencyType());
    assertNull(iir.getFeeAmount());
    assertNull(iir.getMediaType());
    assertNull(iir.getPermanentLocation());
    assertNull(iir.getCurrentLocation());
    assertNull(iir.getItemProperties());
    assertNull(iir.getScreenMessage());
    assertNull(iir.getPrintLine());
  }

  @Test
  void testGetOwner() {
    final ItemInformationResponse iir = builder().owner(owner).build();
    assertNull(iir.getCirculationStatus());
    assertNull(iir.getSecurityMarker());
    assertNull(iir.getFeeType());
    assertNull(iir.getTransactionDate());
    assertNull(iir.getHoldQueueLength());
    assertNull(iir.getDueDate());
    assertNull(iir.getRecallDate());
    assertNull(iir.getHoldPickupDate());
    assertNull(iir.getItemIdentifier());
    assertNull(iir.getTitleIdentifier());
    assertEquals(owner, iir.getOwner());
    assertNull(iir.getCurrencyType());
    assertNull(iir.getFeeAmount());
    assertNull(iir.getMediaType());
    assertNull(iir.getPermanentLocation());
    assertNull(iir.getCurrentLocation());
    assertNull(iir.getItemProperties());
    assertNull(iir.getScreenMessage());
    assertNull(iir.getPrintLine());
  }

  @Test
  void testGetCurrencyType() {
    final ItemInformationResponse iir = builder()
        .currencyType(currencyType)
        .build();
    assertNull(iir.getCirculationStatus());
    assertNull(iir.getSecurityMarker());
    assertNull(iir.getFeeType());
    assertNull(iir.getTransactionDate());
    assertNull(iir.getHoldQueueLength());
    assertNull(iir.getDueDate());
    assertNull(iir.getRecallDate());
    assertNull(iir.getHoldPickupDate());
    assertNull(iir.getItemIdentifier());
    assertNull(iir.getTitleIdentifier());
    assertNull(iir.getOwner());
    assertEquals(currencyType, iir.getCurrencyType());
    assertNull(iir.getFeeAmount());
    assertNull(iir.getMediaType());
    assertNull(iir.getPermanentLocation());
    assertNull(iir.getCurrentLocation());
    assertNull(iir.getItemProperties());
    assertNull(iir.getScreenMessage());
    assertNull(iir.getPrintLine());
  }

  @Test
  void testGetFeeAmount() {
    final ItemInformationResponse iir = builder().feeAmount(feeAmount).build();
    assertNull(iir.getCirculationStatus());
    assertNull(iir.getSecurityMarker());
    assertNull(iir.getFeeType());
    assertNull(iir.getTransactionDate());
    assertNull(iir.getHoldQueueLength());
    assertNull(iir.getDueDate());
    assertNull(iir.getRecallDate());
    assertNull(iir.getHoldPickupDate());
    assertNull(iir.getItemIdentifier());
    assertNull(iir.getTitleIdentifier());
    assertNull(iir.getOwner());
    assertNull(iir.getCurrencyType());
    assertEquals(feeAmount, iir.getFeeAmount());
    assertNull(iir.getMediaType());
    assertNull(iir.getPermanentLocation());
    assertNull(iir.getCurrentLocation());
    assertNull(iir.getItemProperties());
    assertNull(iir.getScreenMessage());
    assertNull(iir.getPrintLine());
  }

  @Test
  void testGetMediaType() {
    final ItemInformationResponse iir = builder().mediaType(mediaType).build();
    assertNull(iir.getCirculationStatus());
    assertNull(iir.getSecurityMarker());
    assertNull(iir.getFeeType());
    assertNull(iir.getTransactionDate());
    assertNull(iir.getHoldQueueLength());
    assertNull(iir.getDueDate());
    assertNull(iir.getRecallDate());
    assertNull(iir.getHoldPickupDate());
    assertNull(iir.getItemIdentifier());
    assertNull(iir.getTitleIdentifier());
    assertNull(iir.getOwner());
    assertNull(iir.getCurrencyType());
    assertNull(iir.getFeeAmount());
    assertEquals(mediaType, iir.getMediaType());
    assertNull(iir.getPermanentLocation());
    assertNull(iir.getCurrentLocation());
    assertNull(iir.getItemProperties());
    assertNull(iir.getScreenMessage());
    assertNull(iir.getPrintLine());
  }

  @Test
  void testGetPermanentLocation() {
    final ItemInformationResponse iir = builder()
        .permanentLocation(permanentLocation)
        .build();
    assertNull(iir.getCirculationStatus());
    assertNull(iir.getSecurityMarker());
    assertNull(iir.getFeeType());
    assertNull(iir.getTransactionDate());
    assertNull(iir.getHoldQueueLength());
    assertNull(iir.getDueDate());
    assertNull(iir.getRecallDate());
    assertNull(iir.getHoldPickupDate());
    assertNull(iir.getItemIdentifier());
    assertNull(iir.getTitleIdentifier());
    assertNull(iir.getOwner());
    assertNull(iir.getCurrencyType());
    assertNull(iir.getFeeAmount());
    assertNull(iir.getMediaType());
    assertEquals(permanentLocation, iir.getPermanentLocation());
    assertNull(iir.getCurrentLocation());
    assertNull(iir.getItemProperties());
    assertNull(iir.getScreenMessage());
    assertNull(iir.getPrintLine());
  }

  @Test
  void testGetCurrentLocation() {
    final ItemInformationResponse iir = builder()
        .currentLocation(currentLocation)
        .build();
    assertNull(iir.getCirculationStatus());
    assertNull(iir.getSecurityMarker());
    assertNull(iir.getFeeType());
    assertNull(iir.getTransactionDate());
    assertNull(iir.getHoldQueueLength());
    assertNull(iir.getDueDate());
    assertNull(iir.getRecallDate());
    assertNull(iir.getHoldPickupDate());
    assertNull(iir.getItemIdentifier());
    assertNull(iir.getTitleIdentifier());
    assertNull(iir.getOwner());
    assertNull(iir.getCurrencyType());
    assertNull(iir.getFeeAmount());
    assertNull(iir.getMediaType());
    assertNull(iir.getPermanentLocation());
    assertEquals(currentLocation, iir.getCurrentLocation());
    assertNull(iir.getItemProperties());
    assertNull(iir.getScreenMessage());
    assertNull(iir.getPrintLine());
  }

  @Test
  void testGetItemProperties() {
    final ItemInformationResponse iir = builder()
        .itemProperties(itemProperties)
        .build();
    assertNull(iir.getCirculationStatus());
    assertNull(iir.getSecurityMarker());
    assertNull(iir.getFeeType());
    assertNull(iir.getTransactionDate());
    assertNull(iir.getHoldQueueLength());
    assertNull(iir.getDueDate());
    assertNull(iir.getRecallDate());
    assertNull(iir.getHoldPickupDate());
    assertNull(iir.getItemIdentifier());
    assertNull(iir.getTitleIdentifier());
    assertNull(iir.getOwner());
    assertNull(iir.getCurrencyType());
    assertNull(iir.getFeeAmount());
    assertNull(iir.getMediaType());
    assertNull(iir.getPermanentLocation());
    assertNull(iir.getCurrentLocation());
    assertEquals(itemProperties, iir.getItemProperties());
    assertNull(iir.getScreenMessage());
    assertNull(iir.getPrintLine());
  }

  @Test
  void testGetScreenMessage() {
    final ItemInformationResponse iir = builder().screenMessage(screenMessage).build();
    assertNull(iir.getCirculationStatus());
    assertNull(iir.getSecurityMarker());
    assertNull(iir.getFeeType());
    assertNull(iir.getTransactionDate());
    assertNull(iir.getHoldQueueLength());
    assertNull(iir.getDueDate());
    assertNull(iir.getRecallDate());
    assertNull(iir.getHoldPickupDate());
    assertNull(iir.getItemIdentifier());
    assertNull(iir.getTitleIdentifier());
    assertNull(iir.getOwner());
    assertNull(iir.getCurrencyType());
    assertNull(iir.getFeeAmount());
    assertNull(iir.getMediaType());
    assertNull(iir.getPermanentLocation());
    assertNull(iir.getCurrentLocation());
    assertNull(iir.getItemProperties());
    assertEquals(screenMessage, iir.getScreenMessage());
    assertNull(iir.getPrintLine());
  }

  @Test
  void testGetPrintLine() {
    final ItemInformationResponse iir = builder().printLine(printLine).build();
    assertNull(iir.getCirculationStatus());
    assertNull(iir.getSecurityMarker());
    assertNull(iir.getFeeType());
    assertNull(iir.getTransactionDate());
    assertNull(iir.getHoldQueueLength());
    assertNull(iir.getDueDate());
    assertNull(iir.getRecallDate());
    assertNull(iir.getHoldPickupDate());
    assertNull(iir.getItemIdentifier());
    assertNull(iir.getTitleIdentifier());
    assertNull(iir.getOwner());
    assertNull(iir.getCurrencyType());
    assertNull(iir.getFeeAmount());
    assertNull(iir.getMediaType());
    assertNull(iir.getPermanentLocation());
    assertNull(iir.getCurrentLocation());
    assertNull(iir.getItemProperties());
    assertNull(iir.getScreenMessage());
    assertEquals(printLine, iir.getPrintLine());
  }

  @Test
  void testCompleteItemInformationResponse() {
    final ItemInformationResponse iir = builder()
        .circulationStatus(circulationStatus)
        .securityMarker(securityMarker)
        .feeType(feeType)
        .transactionDate(transactionDate)
        .holdQueueLength(holdQueueLength)
        .dueDate(dueDate)
        .recallDate(recallDate)
        .holdPickupDate(holdPickupDate)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .owner(owner)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .mediaType(mediaType)
        .permanentLocation(permanentLocation)
        .currentLocation(currentLocation)
        .itemProperties(itemProperties)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertAll("ItemInformationResponse",
        () -> assertEquals(circulationStatus, iir.getCirculationStatus()),
        () -> assertEquals(securityMarker, iir.getSecurityMarker()),
        () -> assertEquals(feeType, iir.getFeeType()),
        () -> assertEquals(transactionDate, iir.getTransactionDate()),
        () -> assertEquals(holdQueueLength, iir.getHoldQueueLength()),
        () -> assertEquals(dueDate, iir.getDueDate()),
        () -> assertEquals(recallDate, iir.getRecallDate()),
        () -> assertEquals(holdPickupDate, iir.getHoldPickupDate()),
        () -> assertEquals(itemIdentifier, iir.getItemIdentifier()),
        () -> assertEquals(titleIdentifier, iir.getTitleIdentifier()),
        () -> assertEquals(owner, iir.getOwner()),
        () -> assertEquals(currencyType, iir.getCurrencyType()),
        () -> assertEquals(feeAmount, iir.getFeeAmount()),
        () -> assertEquals(mediaType, iir.getMediaType()),
        () -> assertEquals(permanentLocation, iir.getPermanentLocation()),
        () -> assertEquals(currentLocation, iir.getCurrentLocation()),
        () -> assertEquals(itemProperties, iir.getItemProperties()),
        () -> assertEquals(screenMessage, iir.getScreenMessage()),
        () -> assertEquals(printLine, iir.getPrintLine())
    );
  }

  @Test
  void testEqualsObject() {
    final ItemInformationResponse iir1 = builder()
        .circulationStatus(circulationStatus)
        .securityMarker(securityMarker)
        .feeType(feeType)
        .transactionDate(transactionDate)
        .holdQueueLength(holdQueueLength)
        .dueDate(dueDate)
        .recallDate(recallDate)
        .holdPickupDate(holdPickupDate)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .owner(owner)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .mediaType(mediaType)
        .permanentLocation(permanentLocation)
        .currentLocation(currentLocation)
        .itemProperties(itemProperties)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    final ItemInformationResponse iir2 = builder()
        .circulationStatus(circulationStatus)
        .securityMarker(securityMarker)
        .feeType(feeType)
        .transactionDate(transactionDate)
        .holdQueueLength(holdQueueLength)
        .dueDate(dueDate)
        .recallDate(recallDate)
        .holdPickupDate(holdPickupDate)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .owner(owner)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .mediaType(mediaType)
        .permanentLocation(permanentLocation)
        .currentLocation(currentLocation)
        .itemProperties(itemProperties)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertTrue(iir1.equals(iir2));
    assertTrue(iir1.equals(iir2));
  }

  @Test
  void testNotEqualsObject() {
    final ItemInformationResponse iir1 = builder()
        .circulationStatus(circulationStatus)
        .securityMarker(securityMarker)
        .feeType(feeType)
        .transactionDate(transactionDate)
        .holdQueueLength(holdQueueLength)
        .dueDate(dueDate)
        .recallDate(recallDate)
        .holdPickupDate(holdPickupDate)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .owner(owner)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .mediaType(mediaType)
        .permanentLocation(permanentLocation)
        .currentLocation(currentLocation)
        .itemProperties(itemProperties)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    final ItemInformationResponse iir2 = builder()
        .circulationStatus(MISSING)
        .securityMarker(WHISPER_TAPE)
        .feeType(DAMAGE)
        .transactionDate(ZonedDateTime.now())
        .holdQueueLength(Integer.valueOf(10))
        .dueDate(ZonedDateTime.now().plusDays(5))
        .recallDate(ZonedDateTime.now().plusDays(6))
        .holdPickupDate(ZonedDateTime.now().plusDays(7))
        .itemIdentifier("222222222")
        .titleIdentifier("ou812")
        .owner("Some Other Library")
        .currencyType(GBP)
        .feeAmount("125.00")
        .mediaType(OTHER)
        .permanentLocation("basement")
        .currentLocation("stolen")
        .itemProperties("Testing")
        .screenMessage(asList("This is a test"))
        .printLine(asList("This is a print test"))
        .build();
    assertFalse(iir1.equals(iir2));
    assertFalse(iir1.equals(iir2));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("ItemInformationResponse [circulationStatus=").append(circulationStatus)
        .append(", securityMarker=").append(securityMarker)
        .append(", feeType=").append(feeType)
        .append(", transactionDate=").append(transactionDate)
        .append(", holdQueueLength=").append(holdQueueLength)
        .append(", dueDate=").append(dueDate)
        .append(", recallDate=").append(recallDate)
        .append(", holdPickupDate=").append(holdPickupDate)
        .append(", itemIdentifier=").append(itemIdentifier)
        .append(", titleIdentifier=").append(titleIdentifier)
        .append(", owner=").append(owner)
        .append(", currencyType=").append(currencyType)
        .append(", feeAmount=").append(feeAmount)
        .append(", mediaType=").append(mediaType)
        .append(", permanentLocation=").append(permanentLocation)
        .append(", currentLocation=").append(currentLocation)
        .append(", itemProperties=").append(itemProperties)
        .append(", screenMessage=").append(screenMessage)
        .append(", printLine=").append(printLine)
        .append(']').toString();
    final ItemInformationResponse iir = builder()
        .circulationStatus(circulationStatus)
        .securityMarker(securityMarker)
        .feeType(feeType)
        .transactionDate(transactionDate)
        .holdQueueLength(holdQueueLength)
        .dueDate(dueDate)
        .recallDate(recallDate)
        .holdPickupDate(holdPickupDate)
        .itemIdentifier(itemIdentifier)
        .titleIdentifier(titleIdentifier)
        .owner(owner)
        .currencyType(currencyType)
        .feeAmount(feeAmount)
        .mediaType(mediaType)
        .permanentLocation(permanentLocation)
        .currentLocation(currentLocation)
        .itemProperties(itemProperties)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertEquals(expectedString, iir.toString());
  }
}
