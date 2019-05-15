package org.folio.edge.sip2.parser;

import static java.lang.Character.valueOf;
import static org.folio.edge.sip2.domain.messages.enumerations.StatusCode.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.requests.SCStatus;
import org.junit.jupiter.api.Test;

class SCStatusMessageParserTests {
  @Test
  void testParse() {
    SCStatusMessageParser parser = new SCStatusMessageParser(valueOf('|'),
        TestUtils.UTCTimeZone);
    final SCStatus scStatus = parser.parse("01202.00");

    assertEquals(SC_OK, scStatus.getStatusCode());
    assertEquals(Integer.valueOf(120), scStatus.getMaxPrintWidth());
    assertEquals("2.00", scStatus.getProtocolVersion());
  }
}
