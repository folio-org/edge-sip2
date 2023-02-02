package org.folio.edge.sip2.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import org.folio.edge.sip2.domain.PreviousMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionDataTests {
  final char fieldDelimiter = '|';
  final String tenant = "diku";
  final boolean errorDetectionEnabled = true;
  final String charset = "IBM850";

  final String scLocation = "pluto";
  final String authenticationToken = "abc123";
  final int maxWidth = 25;
  final String username = "jsmith";
  final String password = "xyzzy";
  final String previousMessageResponse = "test response";
  final String previousMessageChecksum = "abc123";
  final int previousMessageSequenceNo = 5;
  final String timeZone = "America/New_York";
  final boolean patronPasswordVerificationRequired = true;
  SessionData sessionData;

  @BeforeEach
  void setup(@Mock PreviousMessage previousMessage) {
    lenient().when(previousMessage.getPreviousMessageResponse())
      .thenReturn(previousMessageResponse);
    lenient().when(previousMessage.getPreviousRequestChecksum())
      .thenReturn(previousMessageChecksum);
    lenient().when(previousMessage.getPreviousRequestSequenceNo())
      .thenReturn(previousMessageSequenceNo);

    sessionData = SessionData.createSession(tenant, fieldDelimiter, errorDetectionEnabled, charset);
    sessionData.setScLocation(scLocation);
    sessionData.setAuthenticationToken(authenticationToken);
    sessionData.setMaxPrintWidth(maxWidth);
    sessionData.setUsername(username);
    sessionData.setPassword(password);
    sessionData.setPreviousMessage(previousMessage);
    sessionData.setTimeZone(timeZone);
    sessionData.setPatronPasswordVerificationRequired(patronPasswordVerificationRequired);
  }

  @Test
  void testGetScLocation() {
    assertEquals(scLocation, sessionData.getScLocation());
  }

  @Test
  void testSetScLocation() {
    sessionData.setScLocation("jupiter");
    assertEquals("jupiter", sessionData.getScLocation());
  }

  @Test
  void testGetAuthenticationToken() {
    assertEquals(authenticationToken, sessionData.getAuthenticationToken());
  }

  @Test
  void testSetAuthenticationToken() {
    sessionData.setAuthenticationToken("890xyz");
    assertEquals("890xyz", sessionData.getAuthenticationToken());
  }

  @Test
  void testGetMaxPrintWidth() {
    assertEquals(maxWidth, sessionData.getMaxPrintWidth());
  }

  @Test
  void testSetMaxPrintWidth() {
    sessionData.setMaxPrintWidth(100);
    assertEquals(100, sessionData.getMaxPrintWidth());
  }

  @Test
  void testGetUsername() {
    assertEquals(username, sessionData.getUsername());
  }

  @Test
  void testSetUsername() {
    sessionData.setUsername("jdoe");
    assertEquals("jdoe", sessionData.getUsername());
  }

  @Test
  void testGetPassword() {
    assertEquals(password, sessionData.getPassword());
  }

  @Test
  void testSetPassword() {
    sessionData.setPassword("plugh");
    assertEquals("plugh", sessionData.getPassword());
  }

  @Test
  void testGetPreviousMessage() {
    final PreviousMessage m = sessionData.getPreviousMessage();
    assertEquals(previousMessageResponse, m.getPreviousMessageResponse());
    assertEquals(previousMessageChecksum, m.getPreviousRequestChecksum());
    assertEquals(previousMessageSequenceNo, m.getPreviousRequestSequenceNo());
  }

  @Test
  void testSetPreviousMessage(@Mock PreviousMessage previousMessage) {
    when(previousMessage.getPreviousMessageResponse()).thenReturn("another test");
    when(previousMessage.getPreviousRequestChecksum()).thenReturn("xyz890");
    when(previousMessage.getPreviousRequestSequenceNo()).thenReturn(7);

    sessionData.setPreviousMessage(previousMessage);
    final PreviousMessage m = sessionData.getPreviousMessage();
    assertEquals("another test", m.getPreviousMessageResponse());
    assertEquals("xyz890", m.getPreviousRequestChecksum());
    assertEquals(7, m.getPreviousRequestSequenceNo());
  }

  @Test
  void testGetTimeZone() {
    assertEquals(timeZone, sessionData.getTimeZone());
  }

  @Test
  void testSetTimeZone() {
    sessionData.setTimeZone("America/Chicago");
    assertEquals("America/Chicago", sessionData.getTimeZone());
  }


  @Test
  void testGetPatronPasswordVerificationRequired() {
    assertEquals(patronPasswordVerificationRequired,
        sessionData.isPatronPasswordVerificationRequired());
  }

  @Test
  void testSetPatronPasswordVerificationRequired() {
    sessionData.setPatronPasswordVerificationRequired(!patronPasswordVerificationRequired);
    assertEquals(!patronPasswordVerificationRequired,
        sessionData.isPatronPasswordVerificationRequired());
  }

  @Test
  void testGetFieldDelimiter() {
    assertEquals(fieldDelimiter, sessionData.getFieldDelimiter());
  }

  @Test
  void testGetTenant() {
    assertEquals(tenant, sessionData.getTenant());
  }

  @Test
  void testIsErrorDetectionEnabled() {
    assertEquals(errorDetectionEnabled, sessionData.isErrorDetectionEnabled());
  }

  @Test
  void testGetCharset() {
    assertEquals(charset, sessionData.getCharset());
  }

  @Test
  void testCreateSession() {
    final SessionData newSessionData = SessionData.createSession("xxx", '^', false, "UTF-8");
    assertEquals("xxx", newSessionData.getTenant());
    assertEquals('^', newSessionData.getFieldDelimiter());
    assertFalse(newSessionData.isErrorDetectionEnabled());
    assertEquals("UTF-8", newSessionData.getCharset());
    assertNull(newSessionData.getAuthenticationToken());
    assertEquals(-1, newSessionData.getMaxPrintWidth());
    assertNull(newSessionData.getPassword());
    assertNull(newSessionData.getScLocation());
    assertNull(newSessionData.getUsername());
    assertNull(newSessionData.getPreviousMessage());
    assertNull(newSessionData.getTimeZone());
    assertFalse(newSessionData.isPatronPasswordVerificationRequired());
  }
}
