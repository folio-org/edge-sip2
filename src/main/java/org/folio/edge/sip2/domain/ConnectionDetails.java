package org.folio.edge.sip2.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(staticName = "of")
public class ConnectionDetails {

  private final int clientPort;
  private final String clientAddress;
}
