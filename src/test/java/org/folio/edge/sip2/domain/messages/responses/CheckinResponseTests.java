package org.folio.edge.sip2.domain.messages.responses;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static org.folio.edge.sip2.domain.messages.enumerations.MediaType.OTHER;
import static org.folio.edge.sip2.domain.messages.enumerations.MediaType.VIDEO_TAPE;
import static org.folio.edge.sip2.domain.messages.responses.CheckinResponse.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import java.util.List;
import org.folio.edge.sip2.domain.messages.enumerations.MediaType;
import org.junit.jupiter.api.Test;

class CheckinResponseTests {
  final Boolean ok = TRUE;
  final Boolean resensitize = TRUE;
  final Boolean magneticMedia = FALSE;
  final Boolean alert = TRUE;
  final OffsetDateTime transactionDate = OffsetDateTime.now();
  final String institutionId = "diku";
  final String itemIdentifier = "8675309";
  final String permanentLocation = "circ_desk";
  final String titleIdentifier = "5551212";
  final String sortBin = "2";
  final String patronIdentifier = "1234567890";
  final MediaType mediaType = VIDEO_TAPE;
  final String itemProperties = "Directors Cut";
  final List<String> screenMessage = asList("Please rewind");
  final List<String> printLine = asList("Enjoy!");
  final String callNumber = "34995.2345";
  final String servicePoint = "Annex";
  final String alertType = "01";

  @Test
  void testGetOk() {
    final CheckinResponse cir = builder().ok(ok).build();
    assertEquals(ok, cir.getOk());
    assertNull(cir.getResensitize());
    assertNull(cir.getMagneticMedia());
    assertNull(cir.getAlert());
    assertNull(cir.getTransactionDate());
    assertNull(cir.getInstitutionId());
    assertNull(cir.getItemIdentifier());
    assertNull(cir.getPermanentLocation());
    assertNull(cir.getTitleIdentifier());
    assertNull(cir.getSortBin());
    assertNull(cir.getPatronIdentifier());
    assertNull(cir.getMediaType());
    assertNull(cir.getItemProperties());
    assertNull(cir.getScreenMessage());
    assertNull(cir.getPrintLine());
  }

  @Test
  void testGetResensitize() {
    final CheckinResponse cir = builder().resensitize(resensitize).build();
    assertNull(cir.getOk());
    assertEquals(resensitize, cir.getResensitize());
    assertNull(cir.getMagneticMedia());
    assertNull(cir.getAlert());
    assertNull(cir.getTransactionDate());
    assertNull(cir.getInstitutionId());
    assertNull(cir.getItemIdentifier());
    assertNull(cir.getPermanentLocation());
    assertNull(cir.getTitleIdentifier());
    assertNull(cir.getSortBin());
    assertNull(cir.getPatronIdentifier());
    assertNull(cir.getMediaType());
    assertNull(cir.getItemProperties());
    assertNull(cir.getScreenMessage());
    assertNull(cir.getPrintLine());
  }

  @Test
  void testGetMagneticMedia() {
    final CheckinResponse cir = builder().magneticMedia(magneticMedia).build();
    assertNull(cir.getOk());
    assertNull(cir.getResensitize());
    assertEquals(magneticMedia, cir.getMagneticMedia());
    assertNull(cir.getAlert());
    assertNull(cir.getTransactionDate());
    assertNull(cir.getInstitutionId());
    assertNull(cir.getItemIdentifier());
    assertNull(cir.getPermanentLocation());
    assertNull(cir.getTitleIdentifier());
    assertNull(cir.getSortBin());
    assertNull(cir.getPatronIdentifier());
    assertNull(cir.getMediaType());
    assertNull(cir.getItemProperties());
    assertNull(cir.getScreenMessage());
    assertNull(cir.getPrintLine());
  }

  @Test
  void testGetAlert() {
    final CheckinResponse cir = builder().alert(alert).build();
    assertNull(cir.getOk());
    assertNull(cir.getResensitize());
    assertNull(cir.getMagneticMedia());
    assertEquals(alert, cir.getAlert());
    assertNull(cir.getTransactionDate());
    assertNull(cir.getInstitutionId());
    assertNull(cir.getItemIdentifier());
    assertNull(cir.getPermanentLocation());
    assertNull(cir.getTitleIdentifier());
    assertNull(cir.getSortBin());
    assertNull(cir.getPatronIdentifier());
    assertNull(cir.getMediaType());
    assertNull(cir.getItemProperties());
    assertNull(cir.getScreenMessage());
    assertNull(cir.getPrintLine());
  }

  @Test
  void testGetTransactionDate() {
    final CheckinResponse cir = builder()
        .transactionDate(transactionDate)
        .build();
    assertNull(cir.getOk());
    assertNull(cir.getResensitize());
    assertNull(cir.getMagneticMedia());
    assertNull(cir.getAlert());
    assertEquals(transactionDate, cir.getTransactionDate());
    assertNull(cir.getInstitutionId());
    assertNull(cir.getItemIdentifier());
    assertNull(cir.getPermanentLocation());
    assertNull(cir.getTitleIdentifier());
    assertNull(cir.getSortBin());
    assertNull(cir.getPatronIdentifier());
    assertNull(cir.getMediaType());
    assertNull(cir.getItemProperties());
    assertNull(cir.getScreenMessage());
    assertNull(cir.getPrintLine());
  }

  @Test
  void testGetInstitutionId() {
    final CheckinResponse cir = builder().institutionId(institutionId).build();
    assertNull(cir.getOk());
    assertNull(cir.getResensitize());
    assertNull(cir.getMagneticMedia());
    assertNull(cir.getAlert());
    assertNull(cir.getTransactionDate());
    assertEquals(institutionId, cir.getInstitutionId());
    assertNull(cir.getItemIdentifier());
    assertNull(cir.getPermanentLocation());
    assertNull(cir.getTitleIdentifier());
    assertNull(cir.getSortBin());
    assertNull(cir.getPatronIdentifier());
    assertNull(cir.getMediaType());
    assertNull(cir.getItemProperties());
    assertNull(cir.getScreenMessage());
    assertNull(cir.getPrintLine());
  }

  @Test
  void testGetItemIdentifier() {
    final CheckinResponse cir = builder()
        .itemIdentifier(itemIdentifier)
        .build();
    assertNull(cir.getOk());
    assertNull(cir.getResensitize());
    assertNull(cir.getMagneticMedia());
    assertNull(cir.getAlert());
    assertNull(cir.getTransactionDate());
    assertNull(cir.getInstitutionId());
    assertEquals(itemIdentifier, cir.getItemIdentifier());
    assertNull(cir.getPermanentLocation());
    assertNull(cir.getTitleIdentifier());
    assertNull(cir.getSortBin());
    assertNull(cir.getPatronIdentifier());
    assertNull(cir.getMediaType());
    assertNull(cir.getItemProperties());
    assertNull(cir.getScreenMessage());
    assertNull(cir.getPrintLine());
  }

  @Test
  void testGetPermanentLocation() {
    final CheckinResponse cir = builder()
        .permanentLocation(permanentLocation)
        .build();
    assertNull(cir.getOk());
    assertNull(cir.getResensitize());
    assertNull(cir.getMagneticMedia());
    assertNull(cir.getAlert());
    assertNull(cir.getTransactionDate());
    assertNull(cir.getInstitutionId());
    assertNull(cir.getItemIdentifier());
    assertEquals(permanentLocation, cir.getPermanentLocation());
    assertNull(cir.getTitleIdentifier());
    assertNull(cir.getSortBin());
    assertNull(cir.getPatronIdentifier());
    assertNull(cir.getMediaType());
    assertNull(cir.getItemProperties());
    assertNull(cir.getScreenMessage());
    assertNull(cir.getPrintLine());
  }

  @Test
  void testGetTitleIdentifier() {
    final CheckinResponse cir = builder()
        .titleIdentifier(titleIdentifier)
        .build();
    assertNull(cir.getOk());
    assertNull(cir.getResensitize());
    assertNull(cir.getMagneticMedia());
    assertNull(cir.getAlert());
    assertNull(cir.getTransactionDate());
    assertNull(cir.getInstitutionId());
    assertNull(cir.getItemIdentifier());
    assertNull(cir.getPermanentLocation());
    assertEquals(titleIdentifier, cir.getTitleIdentifier());
    assertNull(cir.getSortBin());
    assertNull(cir.getPatronIdentifier());
    assertNull(cir.getMediaType());
    assertNull(cir.getItemProperties());
    assertNull(cir.getScreenMessage());
    assertNull(cir.getPrintLine());
  }

  @Test
  void testGetSortBin() {
    final CheckinResponse cir = builder().sortBin(sortBin).build();
    assertNull(cir.getOk());
    assertNull(cir.getResensitize());
    assertNull(cir.getMagneticMedia());
    assertNull(cir.getAlert());
    assertNull(cir.getTransactionDate());
    assertNull(cir.getInstitutionId());
    assertNull(cir.getItemIdentifier());
    assertNull(cir.getPermanentLocation());
    assertNull(cir.getTitleIdentifier());
    assertEquals(sortBin, cir.getSortBin());
    assertNull(cir.getPatronIdentifier());
    assertNull(cir.getMediaType());
    assertNull(cir.getItemProperties());
    assertNull(cir.getScreenMessage());
    assertNull(cir.getPrintLine());
  }

  @Test
  void testGetPatronIdentifier() {
    final CheckinResponse cir = builder()
        .patronIdentifier(patronIdentifier)
        .build();
    assertNull(cir.getOk());
    assertNull(cir.getResensitize());
    assertNull(cir.getMagneticMedia());
    assertNull(cir.getAlert());
    assertNull(cir.getTransactionDate());
    assertNull(cir.getInstitutionId());
    assertNull(cir.getItemIdentifier());
    assertNull(cir.getPermanentLocation());
    assertNull(cir.getTitleIdentifier());
    assertNull(cir.getSortBin());
    assertEquals(patronIdentifier, cir.getPatronIdentifier());
    assertNull(cir.getMediaType());
    assertNull(cir.getItemProperties());
    assertNull(cir.getScreenMessage());
    assertNull(cir.getPrintLine());
  }

  @Test
  void testGetMediaType() {
    final CheckinResponse cir = builder().mediaType(mediaType).build();
    assertNull(cir.getOk());
    assertNull(cir.getResensitize());
    assertNull(cir.getMagneticMedia());
    assertNull(cir.getAlert());
    assertNull(cir.getTransactionDate());
    assertNull(cir.getInstitutionId());
    assertNull(cir.getItemIdentifier());
    assertNull(cir.getPermanentLocation());
    assertNull(cir.getTitleIdentifier());
    assertNull(cir.getSortBin());
    assertNull(cir.getPatronIdentifier());
    assertEquals(mediaType, cir.getMediaType());
    assertNull(cir.getItemProperties());
    assertNull(cir.getScreenMessage());
    assertNull(cir.getPrintLine());
  }

  @Test
  void testGetItemProperties() {
    final CheckinResponse cir = builder()
        .itemProperties(itemProperties)
        .build();
    assertNull(cir.getOk());
    assertNull(cir.getResensitize());
    assertNull(cir.getMagneticMedia());
    assertNull(cir.getAlert());
    assertNull(cir.getTransactionDate());
    assertNull(cir.getInstitutionId());
    assertNull(cir.getItemIdentifier());
    assertNull(cir.getPermanentLocation());
    assertNull(cir.getTitleIdentifier());
    assertNull(cir.getSortBin());
    assertNull(cir.getPatronIdentifier());
    assertNull(cir.getMediaType());
    assertEquals(itemProperties, cir.getItemProperties());
    assertNull(cir.getScreenMessage());
    assertNull(cir.getPrintLine());
  }

  @Test
  void testGetScreenMessage() {
    final CheckinResponse cir = builder().screenMessage(screenMessage).build();
    assertNull(cir.getOk());
    assertNull(cir.getResensitize());
    assertNull(cir.getMagneticMedia());
    assertNull(cir.getAlert());
    assertNull(cir.getTransactionDate());
    assertNull(cir.getInstitutionId());
    assertNull(cir.getItemIdentifier());
    assertNull(cir.getPermanentLocation());
    assertNull(cir.getTitleIdentifier());
    assertNull(cir.getSortBin());
    assertNull(cir.getPatronIdentifier());
    assertNull(cir.getMediaType());
    assertNull(cir.getItemProperties());
    assertEquals(screenMessage, cir.getScreenMessage());
    assertNull(cir.getPrintLine());
  }

  @Test
  void testGetPrintLine() {
    final CheckinResponse cir = builder().printLine(printLine).build();
    assertNull(cir.getOk());
    assertNull(cir.getResensitize());
    assertNull(cir.getMagneticMedia());
    assertNull(cir.getAlert());
    assertNull(cir.getTransactionDate());
    assertNull(cir.getInstitutionId());
    assertNull(cir.getItemIdentifier());
    assertNull(cir.getPermanentLocation());
    assertNull(cir.getTitleIdentifier());
    assertNull(cir.getSortBin());
    assertNull(cir.getPatronIdentifier());
    assertNull(cir.getMediaType());
    assertNull(cir.getItemProperties());
    assertNull(cir.getScreenMessage());
    assertEquals(printLine, cir.getPrintLine());
  }

  @Test
  void testCompleteCheckinResponse() {
    final CheckinResponse cir = builder()
        .ok(ok)
        .resensitize(resensitize)
        .magneticMedia(magneticMedia)
        .alert(alert)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .itemIdentifier(itemIdentifier)
        .permanentLocation(permanentLocation)
        .titleIdentifier(titleIdentifier)
        .sortBin(sortBin)
        .patronIdentifier(patronIdentifier)
        .mediaType(mediaType)
        .itemProperties(itemProperties)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertAll("CheckinResponse",
        () -> assertEquals(ok, cir.getOk()),
        () -> assertEquals(resensitize, cir.getResensitize()),
        () -> assertEquals(magneticMedia, cir.getMagneticMedia()),
        () -> assertEquals(alert, cir.getAlert()),
        () -> assertEquals(transactionDate, cir.getTransactionDate()),
        () -> assertEquals(institutionId, cir.getInstitutionId()),
        () -> assertEquals(itemIdentifier, cir.getItemIdentifier()),
        () -> assertEquals(permanentLocation, cir.getPermanentLocation()),
        () -> assertEquals(titleIdentifier, cir.getTitleIdentifier()),
        () -> assertEquals(sortBin, cir.getSortBin()),
        () -> assertEquals(patronIdentifier, cir.getPatronIdentifier()),
        () -> assertEquals(mediaType, cir.getMediaType()),
        () -> assertEquals(itemProperties, cir.getItemProperties()),
        () -> assertEquals(screenMessage, cir.getScreenMessage()),
        () -> assertEquals(printLine, cir.getPrintLine())
    );
  }

  @Test
  void testEqualsObject() {
    final CheckinResponse cir1 = builder()
        .ok(ok)
        .resensitize(resensitize)
        .magneticMedia(magneticMedia)
        .alert(alert)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .itemIdentifier(itemIdentifier)
        .permanentLocation(permanentLocation)
        .titleIdentifier(titleIdentifier)
        .sortBin(sortBin)
        .patronIdentifier(patronIdentifier)
        .mediaType(mediaType)
        .itemProperties(itemProperties)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    final CheckinResponse cir2 = builder()
        .ok(ok)
        .resensitize(resensitize)
        .magneticMedia(magneticMedia)
        .alert(alert)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .itemIdentifier(itemIdentifier)
        .permanentLocation(permanentLocation)
        .titleIdentifier(titleIdentifier)
        .sortBin(sortBin)
        .patronIdentifier(patronIdentifier)
        .mediaType(mediaType)
        .itemProperties(itemProperties)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertTrue(cir1.equals(cir2));
    assertTrue(cir1.equals(cir2));
  }

  @Test
  void testNotEqualsObject() {
    final CheckinResponse cir1 = builder()
        .ok(ok)
        .resensitize(resensitize)
        .magneticMedia(magneticMedia)
        .alert(alert)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .itemIdentifier(itemIdentifier)
        .permanentLocation(permanentLocation)
        .titleIdentifier(titleIdentifier)
        .sortBin(sortBin)
        .patronIdentifier(patronIdentifier)
        .mediaType(mediaType)
        .itemProperties(itemProperties)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    final CheckinResponse cir2 = builder()
        .ok(FALSE)
        .resensitize(FALSE)
        .magneticMedia(TRUE)
        .alert(FALSE)
        .transactionDate(OffsetDateTime.now())
        .institutionId("xyzzy")
        .itemIdentifier("222222222")
        .permanentLocation("circ_desk_basement")
        .titleIdentifier("ou812")
        .sortBin("3")
        .patronIdentifier("111111111")
        .mediaType(OTHER)
        .itemProperties("Testing")
        .screenMessage(asList("This is a test"))
        .printLine(asList("This is a print test"))
        .build();
    assertFalse(cir1.equals(cir2));
    assertFalse(cir1.equals(cir2));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("CheckinResponse [ok=").append(ok)
        .append(", resensitize=").append(resensitize)
        .append(", magneticMedia=").append(magneticMedia)
        .append(", alert=").append(alert)
        .append(", transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", itemIdentifier=").append(itemIdentifier)
        .append(", permanentLocation=").append(permanentLocation)
        .append(", titleIdentifier=").append(titleIdentifier)
        .append(", sortBin=").append(sortBin)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", mediaType=").append(mediaType)
        .append(", itemProperties=").append(itemProperties)
        .append(", screenMessage=").append(screenMessage)
        .append(", printLine=").append(printLine)
        .append(", callNumber=").append(callNumber)
        .append(", alertType=").append(alertType)
        .append(", pickupServicePoint=").append(servicePoint)
        .append(']').toString();
    final CheckinResponse cir = builder()
        .ok(ok)
        .resensitize(resensitize)
        .magneticMedia(magneticMedia)
        .alert(alert)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .itemIdentifier(itemIdentifier)
        .permanentLocation(permanentLocation)
        .titleIdentifier(titleIdentifier)
        .sortBin(sortBin)
        .patronIdentifier(patronIdentifier)
        .mediaType(mediaType)
        .itemProperties(itemProperties)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .callNumber(callNumber)
        .alertType(alertType)
        .pickupServicePoint(servicePoint)
        .build();
    assertEquals(expectedString, cir.toString());
  }
}
