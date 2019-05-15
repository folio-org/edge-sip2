package org.folio.edge.sip2.parser;

import static java.lang.Character.valueOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.requests.RequestACSResend;
import org.junit.jupiter.api.Test;

class RequestACSResendMessageParserTests {
  @Test
  void testParse() {
    RequestACSResendMessageParser parser =
        new RequestACSResendMessageParser(valueOf('|'), TestUtils.UTCTimeZone);
    final RequestACSResend requestACSResend = parser.parse("");

    // This is about the best we can do here.
    assertNotNull(requestACSResend);
  }
}
