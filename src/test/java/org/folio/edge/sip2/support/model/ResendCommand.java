package org.folio.edge.sip2.support.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResendCommand implements Sip2Command {

  @Override
  public String getMessage(char fieldDelimiter) {
    return new Sip2MessageBuilder(97, fieldDelimiter).build();
  }
}
