package org.folio.edge.sip2.repositories.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.folio.edge.sip2.domain.messages.enumerations.Messages;
import org.folio.edge.sip2.repositories.domain.AcsTenantConfig.SupportedMessage;
import org.folio.edge.sip2.support.tags.UnitTest;
import org.junit.jupiter.api.Test;

@UnitTest
class AcsTenantConfigTest {

  @Test
  void getSupportedMessagesSet_positive() {
    var acsTenantConfig = acsTenantConfig("status1,status2", List.of(
        new SupportedMessage().messageName("CHECKOUT").isSupported("Y"),
        new SupportedMessage().messageName("PATRON_INFORMATION").isSupported("N")
    ));

    var supportedMessagesSet = acsTenantConfig.getSupportedMessagesSet();
    assertThat(supportedMessagesSet).containsExactly(Messages.CHECKOUT);

    var invalidCheckinStatuses = acsTenantConfig.getInvalidCheckinStatusesList();
    assertThat(invalidCheckinStatuses).containsExactly("status1", "status2");
  }

  @Test
  void getSupportedMessagesSet_positive_nullValues() {
    var acsTenantConfig = acsTenantConfig(null, null);
    assertThat(acsTenantConfig.getSupportedMessagesSet()).isEmpty();
    assertThat(acsTenantConfig.getInvalidCheckinStatusesList()).isEmpty();
  }

  private static AcsTenantConfig acsTenantConfig(String invalidCheckinStatuses,
      List<SupportedMessage> supportedMessages) {

    return new AcsTenantConfig()
        .statusUpdateOk(true)
        .offlineOk(true)
        .alwaysCheckPatronPassword(true)
        .usePinForPatronVerification(true)
        .patronPasswordVerificationRequired(true)
        .invalidCheckinStatuses(invalidCheckinStatuses)
        .supportedMessages(supportedMessages);
  }
}
