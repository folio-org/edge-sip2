package org.folio.edge.sip2.support.model;

import static java.time.OffsetDateTime.now;
import static org.folio.edge.sip2.api.support.TestUtils.getFormattedLocalDateTime;
import static org.folio.edge.sip2.api.support.TestUtils.getUtcFixedClock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CheckInCommand implements Sip2Command {

  @Builder.Default
  private final Boolean noBlock = false;

  private final String locationCode;
  private final String institutionId;
  private final String itemIdentifier;
  private final String patronIdentifier;
  private final String terminalPassword;
  private final String itemProperties;
  private final Boolean cancel;

  @Override
  public String getMessage(char fieldDelimiter) {
    return new Sip2MessageBuilder(9, fieldDelimiter)
        .withValue(noBlock) // No block
        .withValue(getFormattedLocalDateTime(now(getUtcFixedClock()))) // transaction date
        .withValue(getFormattedLocalDateTime(now(getUtcFixedClock()).plusDays(1))) // return date
        .withFieldValue("AP", locationCode) // current location (not supported)
        .withFieldValue("AO", institutionId, true) // institution id
        .withFieldValue("AB", itemIdentifier, true) // item identifier (e.g. barcode)
        .withOptFieldValue("AC", terminalPassword, true) // terminal password
        .withOptFieldValue("CH", itemProperties, true) // Number of the first item to be sent to SC
        .withOptFieldValue("BI", cancel, true)  // transaction is used to cancel failed checkout
        .build();
  }
}
