package org.folio.edge.sip2.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.junit5.VertxTestContext;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;
import org.folio.edge.sip2.api.support.BaseTest;
import org.folio.edge.sip2.api.support.TestUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Integration tests for SIP service.
 * 1. Not all cases need to be tested.
 * 2. Only test cases that cannot be unit tested
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
  public void canMakeARequest(Vertx vertx, VertxTestContext testContext) {
    callService("9300CNMartin|COpassword|\r",
        testContext, vertx, result -> {
          final String expectedString = "941\r";
          assertEquals(expectedString, result);
        });
  }

  @Test
  @Disabled("temporary disabled: unstable")
  void canMakeAFailingRequest(Vertx vertx, VertxTestContext testContext) {
    callService("9300CNMartin|COpassword|\r",
        testContext, vertx, result -> {
          final String expectedString = "940\r";
          assertEquals(expectedString, result);
        });
  }

  @Test
  public void cannotCheckoutWithInvalidCommandCode(Vertx vertx, VertxTestContext testContext) {
    callService("blablabalb\r", testContext, vertx, result -> {
      assertTrue(result.contains("Problems handling the request"));
    });
  }

  @Disabled("Need mock Okapi")
  @Test
  public void canMakeValidSCStatusRequest(Vertx vertx, VertxTestContext testContext) {
    callService("9900401.00AY1AZFCA5\r",
        testContext, vertx, result -> {
          validateExpectedACSStatus(result);
      });
  }

  @Test
  public void canMakeInvalidStatusRequestAndGetExpectedErrorMessage(
      Vertx vertx, VertxTestContext testContext) {
    callService("990231.23\r", testContext, vertx, result -> {
      assertTrue(result.contains("Problems handling the request"));
    });
  }

  @Test
  @Tag(ERROR_DETECTION_ENABLED)
  public void canGetCsResendMessageWhenSendingInvalidMessage(
      Vertx vertx, VertxTestContext testContext) {
    String scStatusMessage = "9900401.00AY1AZAAAA\r";
    callService(scStatusMessage, testContext, vertx, result -> {
      assertEquals("96AZFEF6\r", result);
    });
  }

  @Disabled("Need mock Okapi")
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

  @Disabled("Need mock Okapi")
  @Test
  public void canExecuteEndSessionCommand(
      Vertx vertx, VertxTestContext testContext) {

    final String institutionId = "fs00000001";
    final String patronIdentifier = "patronId1234";
    final String patronPassword = "patronPassword";
    final String terminalPassword = "terminalPassword";
    final Clock clock = TestUtils.getUtcFixedClock();
    final String delimeter = "|";

    StringBuffer sipMessageBf = new StringBuffer();
    sipMessageBf.append("35");
    sipMessageBf.append(TestUtils.getFormattedLocalDateTime(OffsetDateTime.now(clock)));
    sipMessageBf.append("AO" + institutionId + delimeter);
    sipMessageBf.append("AA" + patronIdentifier + delimeter);
    sipMessageBf.append("AC" + terminalPassword + delimeter);
    sipMessageBf.append("AD" + patronPassword + delimeter);
    sipMessageBf.append("\r");

    final String expectedString = "36Y"
        + OffsetDateTime.now(clock).format(DateTimeFormatter.ofPattern("yyyyMMdd    HHmmss"))
        + "AO" + institutionId + "|AA" + patronIdentifier + '|' + '\r';

    callService(sipMessageBf.toString(),
        testContext, vertx, result -> {
          assertEquals(expectedString, result);
        });
  }

  @Test
  @Tag(TLS_ENABLED)
  void canConnectWithTLS(Vertx vertx, VertxTestContext testContext, TestInfo testInfo) {
    callService("990231.23\r", testContext, vertx, testInfo, result -> {
      assertTrue(result.contains("Problems handling the request"));
    });
  }

  private String getFormattedDateString() {
    String pattern = "YYYYMMdd";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    return simpleDateFormat.format(new Date());
  }

  private void validateExpectedACSStatus(String acsResponse) {

    log.info("ACS response: " + acsResponse);

    String expectedPreLocalTime = "98YYNYNN005003" + getFormattedDateString();
    String expectedPostLocalTime =
        "1.23|AOdikutest|AMdiku|BXYNNNYNYNNNNNNNYN|ANTL01|AFscreenMessages|AGline|\r";
    String expectedBlankSpaces = "    ";

    assertEquals(expectedPreLocalTime, acsResponse.substring(0, 22),
        "preLocalTime substring is not as expected");
    assertEquals(expectedBlankSpaces, acsResponse.substring(22, 26),
        "blank spaces substring is not as expected");
    assertEquals(expectedPostLocalTime, acsResponse.substring(32),
        "postLocalTime substring is not as expected");
  }

  @Test
  @DisplayName("Test /admin/health endpoint")
  public void testHealthCheck(
      Vertx vertx, VertxTestContext testContext) {

    HttpClient client = vertx.createHttpClient();
    client.request(HttpMethod.GET, 8081, "localhost", "/admin/health")
        .compose(HttpClientRequest::send)
        .onComplete(testContext.succeeding(response -> {
          assertEquals(200, response.statusCode());
          response.bodyHandler(body -> {
            assertEquals("OK", body.toString());
            testContext.completeNow();
          });
        }));
  }

  @Test
  @DisplayName("Test / endpoint")
  public void testHealthCheckWithWrongPath(
      Vertx vertx, VertxTestContext testContext) {

    HttpClient client = vertx.createHttpClient();
    client.request(HttpMethod.GET, 8081, "localhost", "/")
        .compose(HttpClientRequest::send)
        .onComplete(testContext.succeeding(response -> {
          assertEquals(404, response.statusCode());
          testContext.completeNow();
        }));
  }
}
