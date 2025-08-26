package org.folio.edge.sip2.domain.messages.responses;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class OkapiLoginResponseTests {
  final Boolean ok = TRUE;

  @Test
  void testGetOk() {
    final LoginResponse lr = LoginResponse.of(ok);
    assertEquals(ok, lr.getOk());
  }

  @Test
  void testEquals() {
    final LoginResponse lr1 = LoginResponse.of(ok);
    final LoginResponse lr2 = LoginResponse.of(ok);
    assertEquals(lr1, lr2);
    assertEquals(lr2, lr1);
  }

  @Test
  void testNotEquals() {
    final LoginResponse lr1 = LoginResponse.of(ok);
    final LoginResponse lr2 = LoginResponse.of(FALSE);
    assertNotEquals(lr1, lr2);
    assertNotEquals(lr2, lr1);
  }

  @Test
  void testToString() {
    final String expectedString = "LoginResponse(ok=" + ok + ')';
    final LoginResponse lr = LoginResponse.of(ok);
    assertEquals(expectedString, lr.toString());
  }
}
