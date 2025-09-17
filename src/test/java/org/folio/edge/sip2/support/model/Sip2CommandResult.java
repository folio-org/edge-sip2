package org.folio.edge.sip2.support.model;

import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Sip2CommandResult {

  @Builder.Default
  private boolean isMessageSent = false;

  @Builder.Default
  private boolean isMessageReceived = false;

  @Builder.Default
  private boolean isChecksumValid = true;

  private OffsetDateTime messageSendStartTime;
  private OffsetDateTime messageSentEndTime;

  private OffsetDateTime messageReadEndTime;
  private OffsetDateTime messageReadStartTime;

  private String errorMessage;
  private String requestMessage;
  private String responseMessage;

  private Exception exception;

  public boolean isSuccessfulExchange() {
    return isMessageSent && isMessageReceived && exception == null;
  }
}
