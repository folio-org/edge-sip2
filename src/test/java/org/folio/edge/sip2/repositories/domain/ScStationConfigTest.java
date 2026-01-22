package org.folio.edge.sip2.repositories.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.folio.edge.sip2.support.tags.UnitTest;
import org.junit.jupiter.api.Test;

@UnitTest
class ScStationConfigTest {

  @Test
  void chainMethodsTest() {
    assertThat(scStationConfig()).isEqualTo(scStationConfig());
  }

  private static ScStationConfig scStationConfig() {
    return new ScStationConfig()
        .retriesAllowed(3)
        .timeoutPeriod(10)
        .checkinOk(true)
        .acsRenewalPolicy(true)
        .checkoutOk(true)
        .libraryName("test")
        .terminalLocation("testLocation");
  }
}
