package org.folio.edge.sip2.support;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.folio.edge.sip2.support.model.Sip2Command;
import org.folio.edge.sip2.support.model.Sip2CommandResult;
import org.junit.jupiter.api.function.ThrowingConsumer;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Sip2TestCommand {

  private final Sip2Command sip2Command;
  private final ThrowingConsumer<Sip2CommandResult> resultVerifier;

  public static Sip2TestCommand sip2Exchange(
      Sip2Command sip2Command, ThrowingConsumer<Sip2CommandResult> resultVerifier) {
    return new Sip2TestCommand(sip2Command, resultVerifier);
  }
}
