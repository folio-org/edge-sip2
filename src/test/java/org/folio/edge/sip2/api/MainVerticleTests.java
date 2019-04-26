package org.folio.edge.sip2.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.folio.edge.sip2.api.support.BaseTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for SIP service.
 * 1. Not all cases need to be tested.
 * 2. Only test cases that cannot be unit tested
 * 3. The order of the tests need to be kept as is.
 *    New tests should be added on the bottom.
 */
public class MainVerticleTests extends BaseTest {

  @Test
  public void canStartMainVerticle() {
    assertNotNull(myVerticle.deploymentID());
  }

  /**
   * This test checks the negative case when there is no previous request stored.
   */
  @Test
  public void cannotSuccessfullyResendPreviousRequest(Vertx vertx, VertxTestContext context) {
    String sipMessage = "97\r";
    callService(sipMessage,
        context, vertx, result -> {
          final String expectedString = "PreviousMessage is NULL\r";
          assertEquals(expectedString, result);
        });
  }

  @Test
  public void canMakeARequest(Vertx vertex, VertxTestContext testContext) {
    callService("9300CNMartin|COpassword|\r",
        testContext, vertex, result -> {
          final String expectedString = "941\r";
          assertEquals(expectedString, result);
        });
  }

  @Test
  public void cannotCheckoutWithInvalidCommandCode(Vertx vertex, VertxTestContext testContext) {
    callService("blablabalb\r", testContext, vertex, result -> {
      assertTrue(result.contains("Problems handling the request"));
    });
  }

  @Test
  public void canMakeValidSCStatusRequest(Vertx vertex, VertxTestContext testContext) {
    callService("9900401.00AY1AZFCA5\r",
        testContext, vertex, result -> {
          validateExpectedACSStatus(result);
      });
  }

  @Test
  public void canMakeInvalidStatusRequestAndGetExpectedErrorMessage(
      Vertx vertex, VertxTestContext testContext) {
    callService("990231.23\r", testContext, vertex, result -> {
      assertTrue(result.contains("Problems handling the request"));
    });
  }

  @Test
  @Tag("ErrorDetectionEnabled")
  public void canGetCsResendMessageWhenSendingInvalidMessage(
      Vertx vertx, VertxTestContext testContext) {
    String scStatusMessage = "9900401.00AY1AZAAAA\r";
    callService(scStatusMessage, testContext, vertx, result -> {
      assertEquals("96AZFEF6\r", result);
    });
  }

  @Test
  public void canGetACSStatusMessageWhenSendingValidMessage(
      Vertx vertx, VertxTestContext testContext) {
    String scStatusMessage = "9900401.00AY1AZFCA5\r";
    callService(scStatusMessage, testContext, vertx, result -> {
      validateExpectedACSStatus(result);
    });
  }

  @Test
  public void canTriggerAcsToResendMessage(
      Vertx vertx, VertxTestContext testContext) {
    // Note that this test is highly dependent on the previous test
    // to set the previous message to be "9900401.00AY1AZFCA5\r";

    String[] sipMessaces = new String[2];
    sipMessaces[0] = "9900401.00AY1AZFCA5\r";
    sipMessaces[1] = "97\r";

    callServiceMultiple(sipMessaces,
        testContext, vertx, result -> {
          validateExpectedACSStatus(result);
        });

  }

  @Test
  public void canTriggerAcsToResendMessageBySendingSameRequestMessage(
      Vertx vertx, VertxTestContext testContext) {

    String[] sipMessaces = new String[2];
    sipMessaces[0] = "9900401.00AY1AZFCA5\r";
    sipMessaces[1] = "9900401.00AY1AZFCA5\r";

    callServiceMultiple(sipMessaces,
        testContext, vertx, result -> {
          validateExpectedACSStatus(result);
        });
  }

  @Test
  public void cannotTriggerAcsToResendMessageBySendingSameMessageWithoutED(
      Vertx vertx, VertxTestContext testContext) {

    String[] sipMessaces = new String[2];
    sipMessaces[0] = "9900401.00AY1AZFCA5\r";
    sipMessaces[1] = "9900401.00\r";

    callServiceMultiple(sipMessaces,
        testContext, vertx, result -> {
          // there is no way to verify the intended behavior
          // because it also results in a fresh lookup by the ACS.
          // Can only verify the lookup's result.
          validateExpectedACSStatus(result);
        });
  }

  private String getFormattedDateString() {
    String pattern = "YYYYMMdd";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    return simpleDateFormat.format(new Date());
  }

  private void validateExpectedACSStatus(String acsResponse) {

    log.info("ACS response: " + acsResponse);

    String expectedPreLocalTime = "98YYNYNN53" + getFormattedDateString();
    String expectedPostLocalTime =
        "1.23|AOfs00000010test|AMChalmers|BXYNNNYNYNNNNNNNYN|ANTL01|AFscreenMessages|AGline|\r";
    String expectedBlankSpaces = "    ";

    assertEquals(expectedPreLocalTime, acsResponse.substring(0, 18),
        "preLocalTime substring is not as expected");
    assertEquals(expectedBlankSpaces, acsResponse.substring(18, 22),
        "blank spaces substring is not as expected");
    assertEquals(expectedPostLocalTime, acsResponse.substring(28),
        "postLocalTime substring is not as expected");
  }
}
