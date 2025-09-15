package org.folio.edge.sip2.support.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatusCommand implements Sip2Command {

  public static final String SIP2_PROTOCOL_VERSION = "2.00";

  @Override
  public String getMessage(char fieldDelimiter) {
    return new Sip2MessageBuilder(99, fieldDelimiter)
        // 0 – SC unit is OK, 1 – SC printer is out of paper, 2 – SC is about to shut down
        .withValue("0")
        // 3-digit This is the maximum number of characters that the SC
        .withValue("040")
        .withValue(SIP2_PROTOCOL_VERSION) // protocol version
        .build();
  }
}
