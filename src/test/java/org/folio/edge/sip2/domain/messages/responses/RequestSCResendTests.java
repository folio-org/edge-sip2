package org.folio.edge.sip2.domain.messages.responses;

import static org.folio.edge.sip2.domain.messages.responses.RequestSCResend.builder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class RequestSCResendTests {
  @Test
  void testRequestSCResend() {
    final RequestSCResend rscr = builder().build();
    assertNotNull(rscr);
  }

  @Test
  void testToString() {
    final String expectedString = "RequestSCResend []";
    final RequestSCResend rscr = builder().build();
    assertEquals(expectedString, rscr.toString());
  }
}
