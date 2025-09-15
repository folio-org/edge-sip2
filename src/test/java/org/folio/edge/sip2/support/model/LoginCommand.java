package org.folio.edge.sip2.support.model;

import static java.util.Objects.requireNonNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginCommand implements Sip2Command {

  private final String loginUserId;
  private final String loginPassword;
  private final String locationCode;

  @Override
  public String getMessage(char fieldDelimiter) {
    return new Sip2MessageBuilder(93, fieldDelimiter)
        .withValue("00") // no UID algorithm / PWD algorithm encryption
        .withFieldValue("CN", requireNonNull(loginUserId))
        .withFieldValue("CO", requireNonNull(loginPassword), true)
        .withFieldValue("CP", locationCode, true)
        .build();
  }
}
