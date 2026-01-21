package org.folio.edge.sip2.api.support;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.folio.edge.sip2.support.Sip2SessionConfiguration;
import org.folio.edge.sip2.support.Sip2TestConfig;

@Sip2TestConfig("sip2-checksum-verification-enabled.conf")
public abstract class AbstractErrorDetectionEnabledTest extends BaseIntegrationTest {

  protected static char delimiter = '|';

  @Override
  protected Sip2SessionConfiguration getSip2SessionConfig() {
    return Sip2SessionConfiguration.builder()
      .port(mainVerticlePort)
      .fieldDelimiter(delimiter)
      .hostname("localhost")
      .useSsl(false)
      .socketTimeout(Duration.ofSeconds(5))
      .charset(StandardCharsets.ISO_8859_1)
      .errorProtectionEnabled(true)
      .build();
  }
}
