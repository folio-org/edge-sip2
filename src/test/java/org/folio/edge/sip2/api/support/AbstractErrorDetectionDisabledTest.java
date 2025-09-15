package org.folio.edge.sip2.api.support;

import java.nio.charset.StandardCharsets;
import org.folio.edge.sip2.support.Sip2SessionConfiguration;
import org.folio.edge.sip2.support.Sip2TestConfig;

@Sip2TestConfig("sip2-checksum-verification-disabled.conf")
public abstract class AbstractErrorDetectionDisabledTest extends BaseIntegrationTest {

  @Override
  protected Sip2SessionConfiguration getSip2SessionConfig() {
    return Sip2SessionConfiguration.builder()
        .port(mainVerticlePort)
        .hostname("localhost")
        .useSsl(false)
        .charset(StandardCharsets.ISO_8859_1)
        .errorProtectionEnabled(true)
        .build();
  }
}
