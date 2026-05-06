package org.folio.edge.sip2.support.model;

import static java.time.OffsetDateTime.now;
import static org.folio.edge.sip2.api.support.TestUtils.getFormattedLocalDateTime;
import static org.folio.edge.sip2.api.support.TestUtils.getUtcFixedClock;

import lombok.Builder;
import lombok.Data;
import org.folio.edge.sip2.parser.LanguageMapper;

@Data
@Builder
public class PatronStatusCommand implements Sip2Command {

  private final LanguageMapper languageCode;
  private final String institutionId;
  private final String patronIdentifier;
  private final String terminalPassword;
  private final String patronPassword;

  @Override
  public String getMessage(char fieldDelimiter) {
    return new Sip2MessageBuilder(23, fieldDelimiter)
        .withValue(languageCode.code())
        .withValue(getFormattedLocalDateTime(now(getUtcFixedClock())))
        .withFieldValue("AO", institutionId)
        .withFieldValue("AA", patronIdentifier, true)
        .withOptFieldValue("AC", terminalPassword, true)
        .withOptFieldValue("AD", patronPassword, true)
        .build();
  }
}
