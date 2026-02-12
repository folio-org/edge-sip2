package org.folio.edge.sip2.support.model;

import static java.time.OffsetDateTime.now;
import static org.folio.edge.sip2.api.support.TestUtils.getFormattedLocalDateTime;
import static org.folio.edge.sip2.api.support.TestUtils.getUtcFixedClock;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RenewCommand implements Sip2Command {

  @Builder.Default
  private final Boolean thirdPartyAllowed = false;

  @Builder.Default
  private final Boolean noBlock = false;

  private final String institutionId;
  private final String patronIdentifier;
  private final String patronPassword;
  private final String itemIdentifier;
  private final String titleIdentifier;
  private final String terminalPassword;
  private final String itemProperties;
  private final Boolean feeAcknowledged;

  @Override
  public String getMessage(char fieldDelimiter) {
    return new Sip2MessageBuilder(29, fieldDelimiter)
        .withValue(thirdPartyAllowed)
        .withValue(noBlock)
        .withValue(getFormattedLocalDateTime(now(getUtcFixedClock())))
        .withValue(getFormattedLocalDateTime(now(getUtcFixedClock()).plusDays(30)))
        .withFieldValue("AO", institutionId)
        .withFieldValue("AA", patronIdentifier, true)
        .withOptFieldValue("AD", patronPassword, true)
        .withOptFieldValue("AB", itemIdentifier, true)
        .withOptFieldValue("AJ", titleIdentifier, true)
        .withOptFieldValue("AC", terminalPassword, true)
        .withOptFieldValue("CH", itemProperties, true)
        .withOptFieldValue("BO", feeAcknowledged, true)
        .build();
  }
}
