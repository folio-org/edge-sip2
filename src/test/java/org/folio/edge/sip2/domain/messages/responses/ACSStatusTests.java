package org.folio.edge.sip2.domain.messages.responses;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static org.folio.edge.sip2.domain.messages.enumerations.Messages.BLOCK_PATRON;
import static org.folio.edge.sip2.domain.messages.enumerations.Messages.CHECKIN;
import static org.folio.edge.sip2.domain.messages.enumerations.Messages.CHECKOUT;
import static org.folio.edge.sip2.domain.messages.enumerations.Messages.HOLD;
import static org.folio.edge.sip2.domain.messages.enumerations.Messages.LOGIN;
import static org.folio.edge.sip2.domain.messages.responses.ACSStatus.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.folio.edge.sip2.domain.messages.enumerations.Messages;
import org.junit.jupiter.api.Test;

class ACSStatusTests {
  final Boolean onLineStatus = TRUE;
  final Boolean checkinOk = TRUE;
  final Boolean checkoutOk = TRUE;
  final Boolean acsRenewalPolicy = TRUE;
  final Boolean statusUpdateOk = TRUE;
  final Boolean offLineOk = TRUE;
  final Integer timeoutPeriod = Integer.valueOf(600);
  final Integer retriesAllowed = Integer.valueOf(3);
  final OffsetDateTime dateTimeSync = OffsetDateTime.now();
  final String protocolVersion = "2.00";
  final String institutionId = "diku";
  final String libraryName = "Datalogisk Institut Københavns Universitet";
  private Set<Messages> supportedMessages = EnumSet.of(CHECKOUT, CHECKIN);
  private String terminalLocation = "circ_desk";
  final List<String> screenMessage = asList("Hello, world!");
  final List<String> printLine = asList("Dot matrix");

  @Test
  void testGetOnLineStatus() {
    final ACSStatus acss = builder().onLineStatus(onLineStatus).build();
    assertEquals(onLineStatus, acss.getOnLineStatus());
    assertNull(acss.getCheckinOk());
    assertNull(acss.getCheckoutOk());
    assertNull(acss.getAcsRenewalPolicy());
    assertNull(acss.getStatusUpdateOk());
    assertNull(acss.getOffLineOk());
    assertNull(acss.getTimeoutPeriod());
    assertNull(acss.getRetriesAllowed());
    assertNull(acss.getDateTimeSync());
    assertNull(acss.getProtocolVersion());
    assertNull(acss.getInstitutionId());
    assertNull(acss.getLibraryName());
    assertTrue(acss.getSupportedMessages().isEmpty());
    assertNull(acss.getTerminalLocation());
    assertNull(acss.getScreenMessage());
    assertNull(acss.getPrintLine());
  }

  @Test
  void testGetCheckinOk() {
    final ACSStatus acss = builder().checkinOk(checkinOk).build();
    assertNull(acss.getOnLineStatus());
    assertEquals(checkinOk, acss.getCheckinOk());
    assertNull(acss.getCheckoutOk());
    assertNull(acss.getAcsRenewalPolicy());
    assertNull(acss.getStatusUpdateOk());
    assertNull(acss.getOffLineOk());
    assertNull(acss.getTimeoutPeriod());
    assertNull(acss.getRetriesAllowed());
    assertNull(acss.getDateTimeSync());
    assertNull(acss.getProtocolVersion());
    assertNull(acss.getInstitutionId());
    assertNull(acss.getLibraryName());
    assertTrue(acss.getSupportedMessages().isEmpty());
    assertNull(acss.getTerminalLocation());
    assertNull(acss.getScreenMessage());
    assertNull(acss.getPrintLine());
  }

  @Test
  void testGetCheckoutOk() {
    final ACSStatus acss = builder().checkoutOk(checkoutOk).build();
    assertNull(acss.getOnLineStatus());
    assertNull(acss.getCheckinOk());
    assertEquals(checkoutOk, acss.getCheckoutOk());
    assertNull(acss.getAcsRenewalPolicy());
    assertNull(acss.getStatusUpdateOk());
    assertNull(acss.getOffLineOk());
    assertNull(acss.getTimeoutPeriod());
    assertNull(acss.getRetriesAllowed());
    assertNull(acss.getDateTimeSync());
    assertNull(acss.getProtocolVersion());
    assertNull(acss.getInstitutionId());
    assertNull(acss.getLibraryName());
    assertTrue(acss.getSupportedMessages().isEmpty());
    assertNull(acss.getTerminalLocation());
    assertNull(acss.getScreenMessage());
    assertNull(acss.getPrintLine());
  }

  @Test
  void testGetACSRenewalPolicy() {
    final ACSStatus acss = builder().acsRenewalPolicy(acsRenewalPolicy).build();
    assertNull(acss.getOnLineStatus());
    assertNull(acss.getCheckinOk());
    assertNull(acss.getCheckoutOk());
    assertEquals(acsRenewalPolicy, acss.getAcsRenewalPolicy());
    assertNull(acss.getStatusUpdateOk());
    assertNull(acss.getOffLineOk());
    assertNull(acss.getTimeoutPeriod());
    assertNull(acss.getRetriesAllowed());
    assertNull(acss.getDateTimeSync());
    assertNull(acss.getProtocolVersion());
    assertNull(acss.getInstitutionId());
    assertNull(acss.getLibraryName());
    assertTrue(acss.getSupportedMessages().isEmpty());
    assertNull(acss.getTerminalLocation());
    assertNull(acss.getScreenMessage());
    assertNull(acss.getPrintLine());
  }

  @Test
  void testGetStatusUpdateOk() {
    final ACSStatus acss = builder().statusUpdateOk(statusUpdateOk).build();
    assertNull(acss.getOnLineStatus());
    assertNull(acss.getCheckinOk());
    assertNull(acss.getCheckoutOk());
    assertNull(acss.getAcsRenewalPolicy());
    assertEquals(statusUpdateOk, acss.getStatusUpdateOk());
    assertNull(acss.getOffLineOk());
    assertNull(acss.getTimeoutPeriod());
    assertNull(acss.getRetriesAllowed());
    assertNull(acss.getDateTimeSync());
    assertNull(acss.getProtocolVersion());
    assertNull(acss.getInstitutionId());
    assertNull(acss.getLibraryName());
    assertTrue(acss.getSupportedMessages().isEmpty());
    assertNull(acss.getTerminalLocation());
    assertNull(acss.getScreenMessage());
    assertNull(acss.getPrintLine());
  }

  @Test
  void testGetOffLineOk() {
    final ACSStatus acss = builder().offLineOk(offLineOk).build();
    assertNull(acss.getOnLineStatus());
    assertNull(acss.getCheckinOk());
    assertNull(acss.getCheckoutOk());
    assertNull(acss.getAcsRenewalPolicy());
    assertNull(acss.getStatusUpdateOk());
    assertEquals(offLineOk, acss.getOffLineOk());
    assertNull(acss.getTimeoutPeriod());
    assertNull(acss.getRetriesAllowed());
    assertNull(acss.getDateTimeSync());
    assertNull(acss.getProtocolVersion());
    assertNull(acss.getInstitutionId());
    assertNull(acss.getLibraryName());
    assertTrue(acss.getSupportedMessages().isEmpty());
    assertNull(acss.getTerminalLocation());
    assertNull(acss.getScreenMessage());
    assertNull(acss.getPrintLine());
  }

  @Test
  void testGetTimeoutPeriod() {
    final ACSStatus acss = builder().timeoutPeriod(timeoutPeriod).build();
    assertNull(acss.getOnLineStatus());
    assertNull(acss.getCheckinOk());
    assertNull(acss.getCheckoutOk());
    assertNull(acss.getAcsRenewalPolicy());
    assertNull(acss.getStatusUpdateOk());
    assertNull(acss.getOffLineOk());
    assertEquals(timeoutPeriod, acss.getTimeoutPeriod());
    assertNull(acss.getRetriesAllowed());
    assertNull(acss.getDateTimeSync());
    assertNull(acss.getProtocolVersion());
    assertNull(acss.getInstitutionId());
    assertNull(acss.getLibraryName());
    assertTrue(acss.getSupportedMessages().isEmpty());
    assertNull(acss.getTerminalLocation());
    assertNull(acss.getScreenMessage());
    assertNull(acss.getPrintLine());
  }

  @Test
  void testGetRetriesAllowed() {
    final ACSStatus acss = builder().retriesAllowed(retriesAllowed).build();
    assertNull(acss.getOnLineStatus());
    assertNull(acss.getCheckinOk());
    assertNull(acss.getCheckoutOk());
    assertNull(acss.getAcsRenewalPolicy());
    assertNull(acss.getStatusUpdateOk());
    assertNull(acss.getOffLineOk());
    assertNull(acss.getTimeoutPeriod());
    assertEquals(retriesAllowed, acss.getRetriesAllowed());
    assertNull(acss.getDateTimeSync());
    assertNull(acss.getProtocolVersion());
    assertNull(acss.getInstitutionId());
    assertNull(acss.getLibraryName());
    assertTrue(acss.getSupportedMessages().isEmpty());
    assertNull(acss.getTerminalLocation());
    assertNull(acss.getScreenMessage());
    assertNull(acss.getPrintLine());
  }

  @Test
  void testGetDateTimeSync() {
    final ACSStatus acss = builder().dateTimeSync(dateTimeSync).build();
    assertNull(acss.getOnLineStatus());
    assertNull(acss.getCheckinOk());
    assertNull(acss.getCheckoutOk());
    assertNull(acss.getAcsRenewalPolicy());
    assertNull(acss.getStatusUpdateOk());
    assertNull(acss.getOffLineOk());
    assertNull(acss.getTimeoutPeriod());
    assertNull(acss.getRetriesAllowed());
    assertEquals(dateTimeSync, acss.getDateTimeSync());
    assertNull(acss.getProtocolVersion());
    assertNull(acss.getInstitutionId());
    assertNull(acss.getLibraryName());
    assertTrue(acss.getSupportedMessages().isEmpty());
    assertNull(acss.getTerminalLocation());
    assertNull(acss.getScreenMessage());
    assertNull(acss.getPrintLine());
  }

  @Test
  void testGetProtocolVersion() {
    final ACSStatus acss = builder().protocolVersion(protocolVersion).build();
    assertNull(acss.getOnLineStatus());
    assertNull(acss.getCheckinOk());
    assertNull(acss.getCheckoutOk());
    assertNull(acss.getAcsRenewalPolicy());
    assertNull(acss.getStatusUpdateOk());
    assertNull(acss.getOffLineOk());
    assertNull(acss.getTimeoutPeriod());
    assertNull(acss.getRetriesAllowed());
    assertNull(acss.getDateTimeSync());
    assertEquals(protocolVersion, acss.getProtocolVersion());
    assertNull(acss.getInstitutionId());
    assertNull(acss.getLibraryName());
    assertTrue(acss.getSupportedMessages().isEmpty());
    assertNull(acss.getTerminalLocation());
    assertNull(acss.getScreenMessage());
    assertNull(acss.getPrintLine());
  }

  @Test
  void testGetInstitutionId() {
    final ACSStatus acss = builder().institutionId(institutionId).build();
    assertNull(acss.getOnLineStatus());
    assertNull(acss.getCheckinOk());
    assertNull(acss.getCheckoutOk());
    assertNull(acss.getAcsRenewalPolicy());
    assertNull(acss.getStatusUpdateOk());
    assertNull(acss.getOffLineOk());
    assertNull(acss.getTimeoutPeriod());
    assertNull(acss.getRetriesAllowed());
    assertNull(acss.getDateTimeSync());
    assertNull(acss.getProtocolVersion());
    assertEquals(institutionId, acss.getInstitutionId());
    assertNull(acss.getLibraryName());
    assertTrue(acss.getSupportedMessages().isEmpty());
    assertNull(acss.getTerminalLocation());
    assertNull(acss.getScreenMessage());
    assertNull(acss.getPrintLine());
  }

  @Test
  void testGetLibraryName() {
    final ACSStatus acss = builder().libraryName(libraryName).build();
    assertNull(acss.getOnLineStatus());
    assertNull(acss.getCheckinOk());
    assertNull(acss.getCheckoutOk());
    assertNull(acss.getAcsRenewalPolicy());
    assertNull(acss.getStatusUpdateOk());
    assertNull(acss.getOffLineOk());
    assertNull(acss.getTimeoutPeriod());
    assertNull(acss.getRetriesAllowed());
    assertNull(acss.getDateTimeSync());
    assertNull(acss.getProtocolVersion());
    assertNull(acss.getInstitutionId());
    assertEquals(libraryName, acss.getLibraryName());
    assertTrue(acss.getSupportedMessages().isEmpty());
    assertNull(acss.getTerminalLocation());
    assertNull(acss.getScreenMessage());
    assertNull(acss.getPrintLine());
  }

  @Test
  void testGetSupportedMessages() {
    final ACSStatus acss = builder()
        .supportedMessages(supportedMessages)
        .build();
    assertNull(acss.getOnLineStatus());
    assertNull(acss.getCheckinOk());
    assertNull(acss.getCheckoutOk());
    assertNull(acss.getAcsRenewalPolicy());
    assertNull(acss.getStatusUpdateOk());
    assertNull(acss.getOffLineOk());
    assertNull(acss.getTimeoutPeriod());
    assertNull(acss.getRetriesAllowed());
    assertNull(acss.getDateTimeSync());
    assertNull(acss.getProtocolVersion());
    assertNull(acss.getInstitutionId());
    assertNull(acss.getLibraryName());
    assertEquals(supportedMessages, acss.getSupportedMessages());
    assertNull(acss.getTerminalLocation());
    assertNull(acss.getScreenMessage());
    assertNull(acss.getPrintLine());
  }

  @Test
  void testGetTerminalLocation() {
    final ACSStatus acss = builder().terminalLocation(terminalLocation).build();
    assertNull(acss.getOnLineStatus());
    assertNull(acss.getCheckinOk());
    assertNull(acss.getCheckoutOk());
    assertNull(acss.getAcsRenewalPolicy());
    assertNull(acss.getStatusUpdateOk());
    assertNull(acss.getOffLineOk());
    assertNull(acss.getTimeoutPeriod());
    assertNull(acss.getRetriesAllowed());
    assertNull(acss.getDateTimeSync());
    assertNull(acss.getProtocolVersion());
    assertNull(acss.getInstitutionId());
    assertNull(acss.getLibraryName());
    assertTrue(acss.getSupportedMessages().isEmpty());
    assertEquals(terminalLocation, acss.getTerminalLocation());
    assertNull(acss.getScreenMessage());
    assertNull(acss.getPrintLine());
  }

  @Test
  void testGetScreenMessage() {
    final ACSStatus acss = builder().screenMessage(screenMessage).build();
    assertNull(acss.getOnLineStatus());
    assertNull(acss.getCheckinOk());
    assertNull(acss.getCheckoutOk());
    assertNull(acss.getAcsRenewalPolicy());
    assertNull(acss.getStatusUpdateOk());
    assertNull(acss.getOffLineOk());
    assertNull(acss.getTimeoutPeriod());
    assertNull(acss.getRetriesAllowed());
    assertNull(acss.getDateTimeSync());
    assertNull(acss.getProtocolVersion());
    assertNull(acss.getInstitutionId());
    assertNull(acss.getLibraryName());
    assertTrue(acss.getSupportedMessages().isEmpty());
    assertNull(acss.getTerminalLocation());
    assertEquals(screenMessage, acss.getScreenMessage());
    assertNull(acss.getPrintLine());
  }

  @Test
  void testGetPrintLine() {
    final ACSStatus acss = builder().printLine(printLine).build();
    assertNull(acss.getOnLineStatus());
    assertNull(acss.getCheckinOk());
    assertNull(acss.getCheckoutOk());
    assertNull(acss.getAcsRenewalPolicy());
    assertNull(acss.getStatusUpdateOk());
    assertNull(acss.getOffLineOk());
    assertNull(acss.getTimeoutPeriod());
    assertNull(acss.getRetriesAllowed());
    assertNull(acss.getDateTimeSync());
    assertNull(acss.getProtocolVersion());
    assertNull(acss.getInstitutionId());
    assertNull(acss.getLibraryName());
    assertTrue(acss.getSupportedMessages().isEmpty());
    assertNull(acss.getTerminalLocation());
    assertNull(acss.getScreenMessage());
    assertEquals(printLine, acss.getPrintLine());
  }

  @Test
  void testCompleteACSStatus() {
    final ACSStatus acss = builder()
        .onLineStatus(onLineStatus)
        .checkinOk(checkinOk)
        .checkoutOk(checkoutOk)
        .acsRenewalPolicy(acsRenewalPolicy)
        .statusUpdateOk(statusUpdateOk)
        .offLineOk(offLineOk)
        .timeoutPeriod(timeoutPeriod)
        .retriesAllowed(retriesAllowed)
        .dateTimeSync(dateTimeSync)
        .protocolVersion(protocolVersion)
        .institutionId(institutionId)
        .libraryName(libraryName)
        .supportedMessages(supportedMessages)
        .terminalLocation(terminalLocation)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertAll("ACSStatus",
        () -> assertEquals(onLineStatus, acss.getOnLineStatus()),
        () -> assertEquals(checkinOk, acss.getCheckinOk()),
        () -> assertEquals(checkoutOk, acss.getCheckoutOk()),
        () -> assertEquals(acsRenewalPolicy, acss.getAcsRenewalPolicy()),
        () -> assertEquals(statusUpdateOk, acss.getStatusUpdateOk()),
        () -> assertEquals(offLineOk, acss.getOffLineOk()),
        () -> assertEquals(timeoutPeriod, acss.getTimeoutPeriod()),
        () -> assertEquals(retriesAllowed, acss.getRetriesAllowed()),
        () -> assertEquals(dateTimeSync, acss.getDateTimeSync()),
        () -> assertEquals(protocolVersion, acss.getProtocolVersion()),
        () -> assertEquals(institutionId, acss.getInstitutionId()),
        () -> assertEquals(libraryName, acss.getLibraryName()),
        () -> assertEquals(supportedMessages, acss.getSupportedMessages()),
        () -> assertEquals(terminalLocation, acss.getTerminalLocation()),
        () -> assertEquals(screenMessage, acss.getScreenMessage()),
        () -> assertEquals(printLine, acss.getPrintLine())
    );
  }

  @Test
  void testEquals() {
    final ACSStatus acss1 = builder()
        .onLineStatus(onLineStatus)
        .checkinOk(checkinOk)
        .checkoutOk(checkoutOk)
        .acsRenewalPolicy(acsRenewalPolicy)
        .statusUpdateOk(statusUpdateOk)
        .offLineOk(offLineOk)
        .timeoutPeriod(timeoutPeriod)
        .retriesAllowed(retriesAllowed)
        .dateTimeSync(dateTimeSync)
        .protocolVersion(protocolVersion)
        .institutionId(institutionId)
        .libraryName(libraryName)
        .supportedMessages(supportedMessages)
        .terminalLocation(terminalLocation)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    final ACSStatus acss2 = builder()
        .onLineStatus(onLineStatus)
        .checkinOk(checkinOk)
        .checkoutOk(checkoutOk)
        .acsRenewalPolicy(acsRenewalPolicy)
        .statusUpdateOk(statusUpdateOk)
        .offLineOk(offLineOk)
        .timeoutPeriod(timeoutPeriod)
        .retriesAllowed(retriesAllowed)
        .dateTimeSync(dateTimeSync)
        .protocolVersion(protocolVersion)
        .institutionId(institutionId)
        .libraryName(libraryName)
        .supportedMessages(supportedMessages)
        .terminalLocation(terminalLocation)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertTrue(acss1.equals(acss2));
    assertTrue(acss2.equals(acss1));
  }

  @Test
  void testNotEquals() {
    final ACSStatus acss1 = builder()
        .onLineStatus(onLineStatus)
        .checkinOk(checkinOk)
        .checkoutOk(checkoutOk)
        .acsRenewalPolicy(acsRenewalPolicy)
        .statusUpdateOk(statusUpdateOk)
        .offLineOk(offLineOk)
        .timeoutPeriod(timeoutPeriod)
        .retriesAllowed(retriesAllowed)
        .dateTimeSync(dateTimeSync)
        .protocolVersion(protocolVersion)
        .institutionId(institutionId)
        .libraryName(libraryName)
        .supportedMessages(supportedMessages)
        .terminalLocation(terminalLocation)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    final ACSStatus acss2 = builder()
        .onLineStatus(FALSE)
        .checkinOk(FALSE)
        .checkoutOk(FALSE)
        .acsRenewalPolicy(FALSE)
        .statusUpdateOk(FALSE)
        .offLineOk(FALSE)
        .timeoutPeriod(Integer.valueOf(999))
        .retriesAllowed(Integer.valueOf(999))
        .dateTimeSync(OffsetDateTime.now())
        .protocolVersion("1.99")
        .institutionId("test")
        .libraryName("Test Library")
        .supportedMessages(EnumSet.of(BLOCK_PATRON, HOLD, LOGIN))
        .terminalLocation("circ_desk_basement")
        .screenMessage(asList("Welcome to the jungle."))
        .printLine(asList("Print print print"))
        .build();
    assertFalse(acss1.equals(acss2));
    assertFalse(acss2.equals(acss1));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("ACSStatus [onLineStatus=").append(onLineStatus)
        .append(", checkinOk=").append(checkinOk)
        .append(", checkoutOk=").append(checkoutOk)
        .append(", acsRenewalPolicy=").append(acsRenewalPolicy)
        .append(", statusUpdateOk=").append(statusUpdateOk)
        .append(", offLineOk=").append(offLineOk)
        .append(", timeoutPeriod=").append(timeoutPeriod)
        .append(", retriesAllowed=").append(retriesAllowed)
        .append(", dateTimeSync=").append(dateTimeSync)
        .append(", protocolVersion=").append(protocolVersion)
        .append(", institutionId=").append(institutionId)
        .append(", libraryName=").append(libraryName)
        .append(", supportedMessages=").append(supportedMessages)
        .append(", terminalLocation=").append(terminalLocation)
        .append(", screenMessage=").append(screenMessage)
        .append(", printLine=").append(printLine)
        .append(']').toString();
    final ACSStatus acss = builder()
        .onLineStatus(onLineStatus)
        .checkinOk(checkinOk)
        .checkoutOk(checkoutOk)
        .acsRenewalPolicy(acsRenewalPolicy)
        .statusUpdateOk(statusUpdateOk)
        .offLineOk(offLineOk)
        .timeoutPeriod(timeoutPeriod)
        .retriesAllowed(retriesAllowed)
        .dateTimeSync(dateTimeSync)
        .protocolVersion(protocolVersion)
        .institutionId(institutionId)
        .libraryName(libraryName)
        .supportedMessages(supportedMessages)
        .terminalLocation(terminalLocation)
        .screenMessage(screenMessage)
        .printLine(printLine)
        .build();
    assertEquals(expectedString, acss.toString());
  }
}
