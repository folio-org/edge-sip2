package org.folio.edge.sip2.api;

import static org.folio.edge.sip2.support.Sip2TestCommand.sip2Exchange;
import static org.folio.edge.sip2.support.model.StatusCommand.SIP2_PROTOCOL_VERSION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.folio.edge.sip2.api.support.AbstractErrorDetectionEnabledTest;
import org.folio.edge.sip2.support.Sip2Commands;
import org.folio.edge.sip2.support.model.RawCommand;
import org.folio.edge.sip2.support.tags.IntegrationTest;
import org.folio.edge.sip2.support.wiremock.WiremockStubs;
import org.junit.jupiter.api.Test;

@IntegrationTest
@WiremockStubs({
    "wiremock/stubs/mod-login/201-post-acs-login.json",
    "wiremock/stubs/mod-configuration/200-get-configuration.json"
})
class ScStatusIT extends AbstractErrorDetectionEnabledTest {

  @Test
  void getAcsStatus_positive() throws Throwable {
    executeInSession(
        successLoginExchange(),
        sip2Exchange(
            Sip2Commands.status(),
            sup2Result -> {
              assertSuccessfulExchange(sup2Result);
              var respMsg = sup2Result.getResponseMessage();
              assertThat(respMsg, startsWith("98"));
              validateStatusResponseMessage(respMsg);
            }));
  }

  @Test
  void getAcsStatus_negative_resendMessageIsExpectedForInvalidCommand() throws Throwable {
    var scStatusMessage = "9900401.00AY1AZAAAA";
    executeInSession(
        successLoginExchange(),
        sip2Exchange(
            Sip2Commands.raw(scStatusMessage, true),
            sip2Result -> {
              assertSuccessfulExchange(sip2Result);
              assertThat(sip2Result.getResponseMessage(), is("96AZFEF6"));
            }));
  }

  @Test
  void getAcsStatus_negative_invalidStatusRequestAndGetErrorMessage() throws Throwable {
    executeInSession(
        successLoginExchange(),
        sip2Exchange(RawCommand.of("990231.23", false),
            sip2Result -> {
              assertFalse(sip2Result.isSuccessfulExchange());
              assertFalse(sip2Result.isChecksumValid());
              assertThat(sip2Result.getResponseMessage(),
                  containsString("Problems handling the request"));
            }));
  }

  private static void validateStatusResponseMessage(String message) {
    assertThat(message.length(), is(130));
    assertThat(message.charAt(2), is('Y')); // onlineStatus
    assertThat(message.charAt(3), is('Y')); // checkInOk
    assertThat(message.charAt(4), is('Y')); // checkOutOk
    assertThat(message.charAt(5), is('Y')); // renewalPolicy
    assertThat(message.charAt(6), is('N')); // statusUpdateOk
    assertThat(message.charAt(7), is('N')); // offlineOk

    assertThat(message.substring(8, 11), is("005")); // timeoutPeriod
    assertThat(message.substring(11, 14), is("003")); // retriesAllowed

    // system date/time
    assertThat(message.substring(14, 32), matchesRegex("\\d{8}\\s{4}\\d{6}"));
    assertThat(message.substring(32, 36), is(SIP2_PROTOCOL_VERSION)); // protocol version

    // Institution Id (AO)
    assertThat(message.substring(36, 38), is("AO")); // institutionId
    assertThat(message.substring(38, 48), is(TENANT_ID)); // institutionId

    // Library Name (AM)
    assertThat(message.substring(49, 51), is("AM"));
    assertThat(message.substring(51, 62), is("TestLibrary"));

    // Supported messages (BX)
    assertThat(message.substring(63, 65), is("BX"));
    assertThat(message.charAt(65), is('Y')); // Patron Status Request
    assertThat(message.charAt(66), is('Y')); // Check-Out
    assertThat(message.charAt(67), is('Y')); // Check-In
    assertThat(message.charAt(68), is('N')); // Block Patron
    assertThat(message.charAt(69), is('Y')); // SC Status
    assertThat(message.charAt(70), is('N')); // Request Status SC ACS Resend
    assertThat(message.charAt(71), is('Y')); // Login
    assertThat(message.charAt(72), is('Y')); // Patron Information
    assertThat(message.charAt(73), is('Y')); // End Patron Session
    assertThat(message.charAt(74), is('Y')); // Fee Paid
    assertThat(message.charAt(75), is('Y')); // Item Information
    assertThat(message.charAt(76), is('N')); // Item Status Update
    assertThat(message.charAt(77), is('N')); // Patron Enable Request
    assertThat(message.charAt(78), is('N')); // Hold Request
    assertThat(message.charAt(79), is('Y')); // Renew
    assertThat(message.charAt(80), is('Y')); // Renew All

    // The circulation desk’s name
    assertThat(message.substring(82, 84), is("AN"));
    assertThat(message.substring(84, 120), is(SERVICE_POINT_ID));
  }
}
