package org.folio.edge.sip2.api;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.folio.edge.sip2.api.support.BaseTest;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;

import org.folio.edge.sip2.domain.messages.enumerations.PWDAlgorithm;
import org.folio.edge.sip2.domain.messages.enumerations.UIDAlgorithm;
import org.junit.jupiter.api.Test;

public class MainVerticleTests extends BaseTest {

  @Test
  public void canStartMainVerticle() {
    assertNotNull(myVerticle.deploymentID());
  }

  @Test
  public void canMakeARequest(Vertx vertex, VertxTestContext testContext) {
    callService("9300CNMartin|COpassword|",
        testContext, vertex, result -> {
          final String expectedString = new StringBuilder()
              .append("Logged ")
              .append("Login [uidAlgorithm=").append(UIDAlgorithm.NO_ENCRYPTION)
              .append(", pwdAlgorithm=").append(PWDAlgorithm.NO_ENCRYPTION)
              .append(", loginUserId=").append("Martin")
              .append(", loginPassword=").append("password")
              .append(", locationCode=").append((String) null)
              .append(']').append(" in")
              .toString();
          assertEquals(expectedString, result);
        });
  }

  @Test
  public void canStartMainVericleInjectingSip2RequestHandlers(Vertx vertex, VertxTestContext testContext) {

    final ZonedDateTime now = ZonedDateTime.now();
    final String transactionDateString = getFormattedLocalDateTime(now);
    final String nbDueDateString = getFormattedLocalDateTime(now.plusDays(30));
    String title = "Angry Planet";
    String sipMessage =
        "11YY" + transactionDateString + nbDueDateString
        + "AOinstitution_id|AApatron_id|AB" + title + "|AC1234|";

    callService(sipMessage, testContext, vertex, result -> {
      final String expectedString = new StringBuilder()
          .append("Successfully checked out ")
          .append("Checkout [scRenewalPolicy=true")
          .append(", noBlock=true")
          // need a better way to do dates, this could fail in rare cases
          // due to offset changes such as DST.
          .append(", transactionDate=")
          .append(now.truncatedTo(SECONDS).toOffsetDateTime())
          .append(", nbDueDate=")
          .append(now.plusDays(30).truncatedTo(SECONDS).toOffsetDateTime())
          .append(", institutionId=institution_id")
          .append(", patronIdentifier=patron_id")
          .append(", itemIdentifier=").append(title)
          .append(", terminalPassword=1234")
          .append(", itemProperties=null")
          .append(", patronPassword=null")
          .append(", feeAcknowledged=null")
          .append(", cancel=null")
          .append(']').toString();
      assertEquals(expectedString, result);
    });
  }

  @Test
  public void cannotCheckoutWithInvalidCommandCode(Vertx vertex, VertxTestContext testContext) {
    callService("blablabalb", testContext, vertex, result -> {
      assertTrue(result.contains("Problems handling the request"));
    });
  }

  @Test
  public void canMakeValidSCStatusRequest(Vertx vertex, VertxTestContext testContext) {
    callService("9900401.00AY1AZFCA5",
      testContext, vertex, result -> {
        String expectedPreLocalTime = "98YYNYNN53" + getFormattedDateString();
        String expectedPostLocalTime = "1.23|AOfs00000010test|AMChalmers|BXYNNNYNYNNNNNNNYN|ANTL01|AFscreenMessages|AGline|\n";
        String expectedBlankSpaces = "    ";

        assertEquals(result.substring(0, 18), expectedPreLocalTime);
        assertEquals(result.substring(18, 22), expectedBlankSpaces);
        assertEquals(result.substring(28), expectedPostLocalTime);
    });
  }

  @Test
  public void canMakeInvalidStatusRequestAndGetExpectedErrorMessage(Vertx vertex, VertxTestContext testContext) {
    callService("990231.23", testContext, vertex, result -> {
      assertTrue(result.contains("Problems handling the request"));
    });
  }

  private String getFormattedDateString(){
    String pattern = "YYYYMMdd";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    return simpleDateFormat.format(new Date());
  }
}
