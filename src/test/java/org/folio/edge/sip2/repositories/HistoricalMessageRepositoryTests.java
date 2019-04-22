package org.folio.edge.sip2.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.folio.edge.sip2.domain.PreviousMessage;
import org.folio.edge.sip2.parser.Message;
import org.junit.jupiter.api.Test;

public class HistoricalMessageRepositoryTests {

  @Test
  public void canSetAndGetPreviousMessage() {
    final int expectedSeqNo = 9;
    final String expectedChecksum = "AABBCC";
    final String expectedResponse = "this is a SIP response";

    Message.MessageBuilder<Object> builder = Message.builder();
    builder.sequenceNumber(expectedSeqNo);
    builder.checksumString(expectedChecksum);
    builder.valid(true);
    builder.build();

    Message<Object> sampleMessage = new Message<>(builder);

    HistoricalMessageRepository.setPreviousMessage(sampleMessage, expectedResponse);
    PreviousMessage prevMsg = HistoricalMessageRepository.getPreviousMessage();

    assertNotNull(prevMsg);
    assertEquals(expectedSeqNo, prevMsg.getPreviousRequestSequenceNo());
    assertEquals(expectedChecksum, prevMsg.getPreviousRequestChecksum());
    assertEquals(expectedResponse, prevMsg.getPreviousMessageResponse());
  }
}
