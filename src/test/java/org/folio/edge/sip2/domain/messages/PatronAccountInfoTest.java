package org.folio.edge.sip2.domain.messages;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import org.folio.edge.sip2.support.tags.UnitTest;
import org.junit.jupiter.api.Test;

@UnitTest
public class PatronAccountInfoTest {

  @Test
  void testBasicValues() {
    var id = "df5c1d2b-5ca0-4d1b-bc99-0aa1b8501d31";
    var feeFineType = "7214a939-a355-411a-951a-6b8e84f6a9a8";
    var feeFineAmount = "50.00";
    var feeFineRemaining = "25.00";
    var feeFinePaid = "25.00";
    var itemBarcode = "abc123";
    var feeCreationDate = OffsetDateTime.now();
    var feeFineId = "fe9f31e9-149f-4e90-b5fa-b6e55e7641c0";
    var feeDescription = "A fee";
    var itemTitle = "Code Harder, Better, Faster, Stronger";

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
