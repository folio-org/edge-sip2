package org.folio.edge.sip2.domain.messages.requests;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.folio.edge.sip2.domain.messages.requests.BlockPatron.builder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

class BlockPatronTests {
  final Boolean cardRetained = TRUE;
  final ZonedDateTime transactionDate = ZonedDateTime.now();
  final String institutionId = "diku";
  final String blockedCardMsg = "You break it, you bought it!";
  final String patronIdentifier = "1234567890";
  final String terminalPassword = "12345";

  @Test
  void testGetCardRetained() {
    final BlockPatron bp = builder().cardRetained(cardRetained).build();
    assertEquals(cardRetained, bp.getCardRetained());
    assertNull(bp.getTransactionDate());
    assertNull(bp.getInstitutionId());
    assertNull(bp.getBlockedCardMsg());
    assertNull(bp.getPatronIdentifier());
    assertNull(bp.getTerminalPassword());
  }

  @Test
  void testGetTransactionDate() {
    final BlockPatron bp = builder().transactionDate(transactionDate).build();
    assertNull(bp.getCardRetained());
    assertEquals(transactionDate, bp.getTransactionDate());
    assertNull(bp.getInstitutionId());
    assertNull(bp.getBlockedCardMsg());
    assertNull(bp.getPatronIdentifier());
    assertNull(bp.getTerminalPassword());
  }

  @Test
  void testGetInstitutionId() {
    final BlockPatron bp = builder().institutionId(institutionId).build();
    assertNull(bp.getCardRetained());
    assertNull(bp.getTransactionDate());
    assertEquals(institutionId, bp.getInstitutionId());
    assertNull(bp.getBlockedCardMsg());
    assertNull(bp.getPatronIdentifier());
    assertNull(bp.getTerminalPassword());
  }

  @Test
  void testGetBlockedCardMsg() {
    final BlockPatron bp = builder().blockedCardMsg(blockedCardMsg).build();
    assertNull(bp.getCardRetained());
    assertNull(bp.getTransactionDate());
    assertNull(bp.getInstitutionId());
    assertEquals(blockedCardMsg, bp.getBlockedCardMsg());
    assertNull(bp.getPatronIdentifier());
    assertNull(bp.getTerminalPassword());
  }

  @Test
  void testGetPatronIdentifier() {
    final BlockPatron bp = builder().patronIdentifier(patronIdentifier).build();
    assertNull(bp.getCardRetained());
    assertNull(bp.getTransactionDate());
    assertNull(bp.getInstitutionId());
    assertNull(bp.getBlockedCardMsg());
    assertEquals(patronIdentifier, bp.getPatronIdentifier());
    assertNull(bp.getTerminalPassword());
  }

  @Test
  void testGetTerminalPassword() {
    final BlockPatron bp = builder().terminalPassword(terminalPassword).build();
    assertNull(bp.getCardRetained());
    assertNull(bp.getTransactionDate());
    assertNull(bp.getInstitutionId());
    assertNull(bp.getBlockedCardMsg());
    assertNull(bp.getPatronIdentifier());
    assertEquals(terminalPassword, bp.getTerminalPassword());
  }


  @Test
  void testCompleteBlockPatron() {
    final BlockPatron bp = builder()
        .cardRetained(cardRetained)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .blockedCardMsg(blockedCardMsg)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .build();
    assertAll("BlockPatron",
        () -> assertEquals(cardRetained, bp.getCardRetained()),
        () -> assertEquals(transactionDate, bp.getTransactionDate()),
        () -> assertEquals(institutionId, bp.getInstitutionId()),
        () -> assertEquals(blockedCardMsg, bp.getBlockedCardMsg()),
        () -> assertEquals(patronIdentifier, bp.getPatronIdentifier()),
        () -> assertEquals(terminalPassword, bp.getTerminalPassword())
    );
  }

  @Test
  void testEqualsObject() {
    final BlockPatron bp1 = builder()
        .cardRetained(cardRetained)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .blockedCardMsg(blockedCardMsg)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .build();
    final BlockPatron bp2 = builder()
        .cardRetained(cardRetained)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .blockedCardMsg(blockedCardMsg)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .build();
    assertTrue(bp1.equals(bp2));
    assertTrue(bp1.equals(bp2));
  }

  @Test
  void testNotEqualsObject() {
    final BlockPatron bp1 = builder()
        .cardRetained(cardRetained)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .blockedCardMsg(blockedCardMsg)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .build();
    final BlockPatron bp2 = builder()
        .cardRetained(FALSE)
        .transactionDate(transactionDate.minusDays(100))
        .institutionId("xyzzy")
        .blockedCardMsg("Tilt")
        .patronIdentifier("111111111")
        .terminalPassword("88888888")
        .build();
    assertFalse(bp1.equals(bp2));
    assertFalse(bp1.equals(bp2));
  }

  @Test
  void testToString() {
    final String expectedString = new StringBuilder()
        .append("BlockPatron [cardRetained=").append(cardRetained)
        .append(", transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", blockedCardMsg=").append(blockedCardMsg)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", terminalPassword=").append(terminalPassword)
        .append(']')
        .toString();
    final BlockPatron bp = builder()
        .cardRetained(cardRetained)
        .transactionDate(transactionDate)
        .institutionId(institutionId)
        .blockedCardMsg(blockedCardMsg)
        .patronIdentifier(patronIdentifier)
        .terminalPassword(terminalPassword)
        .build();
    assertEquals(expectedString, bp.toString());
  }
}
