package org.folio.edge.sip2.support.model;

import static java.time.OffsetDateTime.now;
import static org.folio.edge.sip2.api.support.TestUtils.getFormattedLocalDateTime;
import static org.folio.edge.sip2.api.support.TestUtils.getUtcFixedClock;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor(staticName = "of")
public class EndSessionCommand implements Sip2Command {

  private final String institutionId;
  private final String patronIdentifier;
  private final String terminalPassword;
  private final String patronPassword;

  @Override
  public String getMessage(char fieldDelimiter) {
    var clock = getUtcFixedClock();
    return new Sip2MessageBuilder(35, fieldDelimiter)
        .withValue(getFormattedLocalDateTime(now(clock)))
        .withFieldValue("AO", institutionId, false)
        .withFieldValue("AA", patronIdentifier, true)
        .withOptFieldValue("AC", terminalPassword, true)
        .withOptFieldValue("AD", patronPassword, true)
        .build();
  }
}
