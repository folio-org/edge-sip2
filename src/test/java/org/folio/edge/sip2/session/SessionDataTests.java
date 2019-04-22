package org.folio.edge.sip2.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SessionDataTests {
  final String tenant = "diku";
  final char fieldDelimiter = '|';
  final boolean errorDetectionEnabled = true;
  final String charset = "IBM850";
  final String authenticationToken = "abc123";
  final int maxWidth = 25;
  final String password = "xyzzy";
  final String scLocation = "pluto";
  final String username = "jsmith";
  SessionData sessionData;

  @BeforeEach
  void setup() {
    sessionData = SessionData.createSession(tenant, fieldDelimiter, errorDetectionEnabled, charset);
    sessionData.setAuthenticationToken(authenticationToken);
    sessionData.setMaxPrintWidth(maxWidth);
    sessionData.setPassword(password);
    sessionData.setScLocation(scLocation);
    sessionData.setUsername(username);
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
  }
}
