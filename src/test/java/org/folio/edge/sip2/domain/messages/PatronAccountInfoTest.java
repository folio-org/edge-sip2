package org.folio.edge.sip2.domain.messages;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

public class PatronAccountInfoTest {
  @Test
  void testBasicValues() {
    String id = "df5c1d2b-5ca0-4d1b-bc99-0aa1b8501d31";
    String feeFineType = "7214a939-a355-411a-951a-6b8e84f6a9a8";
    Double feeFineAmount = 50.0;
    Double feeFineRemaining = 25.0;
    Double feeFinePaid = 25.0;
    String itemBarcode = "abc123";
    OffsetDateTime feeCreationDate = OffsetDateTime.now();
    String feeFineId = "fe9f31e9-149f-4e90-b5fa-b6e55e7641c0";
    String feeDescription = "A fee";
    String itemTitle = "Code Harder, Better, Faster, Stronger";

    PatronAccountInfo patronAccountInfo = new PatronAccountInfo();
    patronAccountInfo.setId(id);
    patronAccountInfo.setItemTitle(itemTitle);
    patronAccountInfo.setFeeFineAmount(feeFineAmount);
    patronAccountInfo.setItemBarcode(itemBarcode);
    patronAccountInfo.setFeeDescription(feeDescription);
    patronAccountInfo.setFeeFineType(feeFineType);
    patronAccountInfo.setFeeFineRemaining(feeFineRemaining);
    patronAccountInfo.setFeeFinePaid(feeFinePaid);
    patronAccountInfo.setFeeCreationDate(feeCreationDate);
    patronAccountInfo.setFeeFineId(feeFineId);

    assertEquals(itemTitle, patronAccountInfo.getItemTitle());
    assertEquals(feeFineAmount, patronAccountInfo.getFeeFineAmount());
    assertEquals(itemBarcode, patronAccountInfo.getItemBarcode());
    assertEquals(feeDescription, patronAccountInfo.getFeeDescription());

  }
}
