package org.folio.edge.sip2.domain.messages.requests;

import static org.folio.edge.sip2.domain.messages.requests.Login.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.folio.edge.sip2.domain.messages.enumerations.PWDAlgorithm;
import org.folio.edge.sip2.domain.messages.enumerations.UIDAlgorithm;
import org.junit.jupiter.api.Test;

class LoginTests {
  final UIDAlgorithm uidAlgorithm = UIDAlgorithm.NO_ENCRYPTION;
  final PWDAlgorithm pwdAlgorithm = PWDAlgorithm.NO_ENCRYPTION;
  final String loginUserId = "sc";
  final String loginPassword = "pass";
  final String locationCode = "circ_desk";

  @Test
  void testGetUIDAlgorithm() {
    final Login l = builder().uidAlgorithm(uidAlgorithm).build();
    assertEquals(uidAlgorithm, l.getUIDAlgorithm());
    assertNull(l.getPWDAlgorithm());
    assertNull(l.getLoginUserId());
    assertNull(l.getLoginPassword());
    assertNull(l.getLocationCode());
  }

  @Test
  void testGetPWDAlgorithm() {
    final Login l = builder().pwdAlgorithm(pwdAlgorithm).build();
    assertNull(l.getUIDAlgorithm());
    assertEquals(pwdAlgorithm, l.getPWDAlgorithm());
    assertNull(l.getLoginUserId());
    assertNull(l.getLoginPassword());
    assertNull(l.getLocationCode());
  }

  @Test
  void testGetLoginUserId() {
    final Login l = builder().loginUserId(loginUserId).build();
    assertNull(l.getUIDAlgorithm());
    assertNull(l.getPWDAlgorithm());
    assertEquals(loginUserId, l.getLoginUserId());
    assertNull(l.getLoginPassword());
    assertNull(l.getLocationCode());
  }

  @Test
  void testGetLoginPassword() {
    final Login l = builder().loginPassword(loginPassword).build();
    assertNull(l.getUIDAlgorithm());
    assertNull(l.getPWDAlgorithm());
    assertNull(l.getLoginUserId());
    assertEquals(loginPassword, l.getLoginPassword());
    assertNull(l.getLocationCode());
  }

  @Test
  void testGetLocationCode() {
    final Login l = builder().locationCode(locationCode).build();
    assertNull(l.getUIDAlgorithm());
    assertNull(l.getPWDAlgorithm());
    assertNull(l.getLoginUserId());
    assertNull(l.getLoginPassword());
    assertEquals(locationCode, l.getLocationCode());
  }

  @Test
  void testCompleteLogin() {
    final Login l = builder()
        .uidAlgorithm(uidAlgorithm)
        .pwdAlgorithm(pwdAlgorithm)
        .loginUserId(loginUserId)
        .loginPassword(loginPassword)
        .locationCode(locationCode)
        .build();
    assertAll("Login",
        () -> assertEquals(uidAlgorithm, l.getUIDAlgorithm()),
        () -> assertEquals(pwdAlgorithm, l.getPWDAlgorithm()),
        () -> assertEquals(loginUserId, l.getLoginUserId()),
        () -> assertEquals(loginPassword, l.getLoginPassword()),
        () -> assertEquals(locationCode, l.getLocationCode())
    );
  }

  @Test
  void testEqualsObject() {
    final Login l1 = builder()
        .uidAlgorithm(uidAlgorithm)
        .pwdAlgorithm(pwdAlgorithm)
        .loginUserId(loginUserId)
        .loginPassword(loginPassword)
        .locationCode(locationCode)
        .build();
    final Login l2 = builder()
        .uidAlgorithm(uidAlgorithm)
        .pwdAlgorithm(pwdAlgorithm)
        .loginUserId(loginUserId)
        .loginPassword(loginPassword)
        .locationCode(locationCode)
        .build();
    assertTrue(l1.equals(l2));
    assertTrue(l1.equals(l2));
  }

  @Test
  void testNotEqualsObject() {
    final Login l1 = builder()
        .uidAlgorithm(uidAlgorithm)
        .pwdAlgorithm(pwdAlgorithm)
        .loginUserId(loginUserId)
        .loginPassword(loginPassword)
        .locationCode(locationCode)
        .build();
    final Login l2 = builder()
        .uidAlgorithm(null)
        .pwdAlgorithm(null)
        .loginUserId("test")
        .loginPassword("xyzzy")
        .locationCode("moon")
        .build();
    assertFalse(l1.equals(l2));
    assertFalse(l1.equals(l2));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("Login [uidAlgorithm=").append(uidAlgorithm)
        .append(", pwdAlgorithm=").append(pwdAlgorithm)
        .append(", loginUserId=").append(loginUserId)
        .append(", loginPassword=").append(loginPassword)
        .append(", locationCode=").append(locationCode)
        .append(']')
        .toString();
    final Login l = builder()
        .uidAlgorithm(uidAlgorithm)
        .pwdAlgorithm(pwdAlgorithm)
        .loginUserId(loginUserId)
        .loginPassword(loginPassword)
        .locationCode(locationCode)
        .build();
    assertEquals(expectedString, l.toString());
  }
}
