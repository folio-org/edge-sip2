package org.folio.edge.sip2.support.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(staticName = "of")
public class RawCommand implements Sip2Command {

  private final String message;
  private final boolean ignoreErrorDetection;

  @Override
  public String getMessage(char fieldDelimiter) {
    return message;
  }

  @Override
  public boolean ignoreErrorDetection() {
    return this.ignoreErrorDetection;
  }
}
