package org.folio.edge.sip2.domain.messages.requests;

import static org.folio.edge.sip2.domain.messages.requests.RequestACSResend.builder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class RequestACSResendTests {
  @Test
  void testRequestACSResend() {
    final RequestACSResend racsr = builder().build();
    assertNotNull(racsr);
  }

  @Test
  void testToString() {
    final String expectedString = "RequestACSResend []";
    final RequestACSResend racsr = builder().build();
    assertEquals(expectedString, racsr.toString());
  }
}
