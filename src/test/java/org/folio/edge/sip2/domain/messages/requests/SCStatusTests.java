package org.folio.edge.sip2.domain.messages.requests;

import static org.folio.edge.sip2.domain.messages.enumerations.StatusCode.SC_ABOUT_TO_SHUT_DOWN;
import static org.folio.edge.sip2.domain.messages.enumerations.StatusCode.SC_OK;
import static org.folio.edge.sip2.domain.messages.requests.SCStatus.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.folio.edge.sip2.domain.messages.enumerations.StatusCode;
import org.junit.jupiter.api.Test;

class SCStatusTests {
  final StatusCode statusCode = SC_OK;
  final Integer maxPrintWidth = Integer.valueOf(99);
  final String protocolVersion = "2.0.0";

  @Test
  void testGetStatusCode() {
    final SCStatus scs = builder().statusCode(statusCode).build();
    assertEquals(statusCode, scs.getStatusCode());
    assertNull(scs.getMaxPrintWidth());
    assertNull(scs.getProtocolVersion());
  }

  @Test
  void testGetMaxPrintWidth() {
    final SCStatus scs = builder().maxPrintWidth(maxPrintWidth).build();
    assertNull(scs.getStatusCode());
    assertEquals(maxPrintWidth, scs.getMaxPrintWidth());
    assertNull(scs.getProtocolVersion());
  }

  @Test
  void testGetProtocolVersion() {
    final SCStatus scs = builder().protocolVersion(protocolVersion).build();
    assertNull(scs.getStatusCode());
    assertNull(scs.getMaxPrintWidth());
    assertEquals(protocolVersion, scs.getProtocolVersion());
  }

  @Test
  void testCompleteSCStatus() {
    final SCStatus scs = builder()
        .statusCode(statusCode)
        .maxPrintWidth(maxPrintWidth)
        .protocolVersion(protocolVersion)
        .build();
    assertAll("SCStatus",
        () -> assertEquals(statusCode, scs.getStatusCode()),
        () -> assertEquals(maxPrintWidth, scs.getMaxPrintWidth()),
        () -> assertEquals(protocolVersion, scs.getProtocolVersion())
    );
  }

  @Test
  void testEqualsObject() {
    final SCStatus scs1 = builder()
        .statusCode(statusCode)
        .maxPrintWidth(maxPrintWidth)
        .protocolVersion(protocolVersion)
        .build();
    final SCStatus scs2 = builder()
        .statusCode(statusCode)
        .maxPrintWidth(maxPrintWidth)
        .protocolVersion(protocolVersion)
        .build();
    assertTrue(scs1.equals(scs2));
    assertTrue(scs1.equals(scs2));
  }

  @Test
  void testNotEqualsObject() {
    final SCStatus scs1 = builder()
        .statusCode(statusCode)
        .maxPrintWidth(maxPrintWidth)
        .protocolVersion(protocolVersion)
        .build();
    final SCStatus scs2 = builder()
        .statusCode(SC_ABOUT_TO_SHUT_DOWN)
        .maxPrintWidth(Integer.valueOf(50))
        .protocolVersion("1.0.0")
        .build();
    assertFalse(scs1.equals(scs2));
    assertFalse(scs1.equals(scs2));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("SCStatus [statusCode=").append(statusCode)
        .append(", maxPrintWidth=").append(maxPrintWidth)
        .append(", protocolVersion=").append(protocolVersion)
        .append(']')
        .toString();
    final SCStatus scs = builder()
        .statusCode(statusCode)
        .maxPrintWidth(maxPrintWidth)
        .protocolVersion(protocolVersion)
        .build();
    assertEquals(expectedString, scs.toString());
  }
}
