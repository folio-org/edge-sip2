package org.folio.edge.sip2.domain.messages.responses;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.folio.edge.sip2.domain.messages.responses.LoginResponse.builder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LoginResponseTests {
  final Boolean ok = TRUE;

  @Test
  void testGetOk() {
    final LoginResponse lr = builder().ok(ok).build();
    assertEquals(ok, lr.getOk());
  }

  @Test
  void testEquals() {
    final LoginResponse lr1 = builder()
        .ok(ok)
        .build();
    final LoginResponse lr2 = builder()
        .ok(ok)
        .build();
    assertTrue(lr1.equals(lr2));
    assertTrue(lr2.equals(lr1));
  }

  @Test
  void testNotEquals() {
    final LoginResponse lr1 = builder()
        .ok(ok)
        .build();
    final LoginResponse lr2 = builder()
        .ok(FALSE)
        .build();
    assertFalse(lr1.equals(lr2));
    assertFalse(lr2.equals(lr1));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("LoginResponse [ok=").append(ok)
        .append(']').toString();
    final LoginResponse lr = builder()
        .ok(ok)
        .build();
    assertEquals(expectedString, lr.toString());
  }
}
