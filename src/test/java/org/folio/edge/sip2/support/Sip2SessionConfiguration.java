
package org.folio.edge.sip2.support;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Sip2SessionConfiguration {

  @Builder.Default
  private final int port = 6443;

  @Builder.Default
  private final String hostname = "localhost";

  @Builder.Default
  private final Duration socketTimeout = Duration.ofSeconds(60);

  @Builder.Default
  private boolean useSsl = false;

  @Builder.Default
  private final Charset charset = StandardCharsets.UTF_8;

  @Builder.Default
  private final boolean errorProtectionEnabled = false;

  @Builder.Default
  private final int readBufferSize = 2048;

  @Builder.Default
  private final char messageDelimiter = '\r';

  @Builder.Default
  private final char fieldDelimiter = '|';
}
