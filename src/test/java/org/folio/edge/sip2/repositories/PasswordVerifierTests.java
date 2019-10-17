package org.folio.edge.sip2.repositories;

import static org.folio.edge.sip2.api.support.TestUtils.getJsonFromFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.Collections;
import java.util.List;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.repositories.domain.User;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
class PasswordVerifierTests {
  @Test
  void canVerifyPassword(
      Vertx vertx,
      VertxTestContext testContext,
      @Mock UsersRepository mockUsersRepository,
      @Mock LoginRepository mockLoginRepository) {
    final String patronIdentifier = "1234567890";

    final String userResponseJson = getJsonFromFile("json/user_response.json");
    final User userResponse = Json.decodeValue(userResponseJson, User.class);
    when(mockUsersRepository.getUserById(eq(patronIdentifier), any()))
        .thenReturn(Future.succeededFuture(userResponse));
    when(mockLoginRepository.patronLogin(eq("leslie"), eq("0989"), any()))
        .thenReturn(Future.succeededFuture(() -> new JsonObject()));

    final SessionData sessionData = TestUtils.getMockedSessionData();
    sessionData.setPatronPasswordVerificationRequired(true);

    final PasswordVerifier passwordVerifier = new PasswordVerifier(mockUsersRepository,
        mockLoginRepository);
    passwordVerifier.verifyPatronPassword(patronIdentifier, "0989", sessionData).setHandler(
        testContext.succeeding(verification -> testContext.verify(() -> {
          assertNotNull(verification);
          assertEquals("997383903573496", verification.getUser().getBarcode());
          assertEquals("leslie", verification.getUser().getUsername());
          assertTrue(verification.getPasswordVerified());
          assertNull(verification.getErrorMessages());

          testContext.completeNow();
        })));
  }

  @Test
  void canVerifyPasswordPatronNotFound(
      Vertx vertx,
      VertxTestContext testContext,
      @Mock UsersRepository mockUsersRepository,
      @Mock LoginRepository mockLoginRepository) {
    final String patronIdentifier = "1234567890";

    when(mockUsersRepository.getUserById(eq(patronIdentifier), any()))
        .thenReturn(Future.succeededFuture(null));

    final SessionData sessionData = TestUtils.getMockedSessionData();
    sessionData.setPatronPasswordVerificationRequired(true);

    final PasswordVerifier passwordVerifier = new PasswordVerifier(mockUsersRepository,
        mockLoginRepository);
    passwordVerifier.verifyPatronPassword(patronIdentifier, "0989", sessionData).setHandler(
        testContext.succeeding(verification -> testContext.verify(() -> {
          assertNotNull(verification);
          assertNull(verification.getUser());
          assertFalse(verification.getPasswordVerified());
          assertNull(verification.getErrorMessages());

          testContext.completeNow();
        })));
  }

  @Test
  void canVerifyPasswordBadPassword(
      Vertx vertx,
      VertxTestContext testContext,
      @Mock UsersRepository mockUsersRepository,
      @Mock LoginRepository mockLoginRepository) {
    final String patronIdentifier = "1234567890";

    final String userResponseJson = getJsonFromFile("json/user_response.json");
    final User userResponse = Json.decodeValue(userResponseJson, User.class);
    when(mockUsersRepository.getUserById(eq(patronIdentifier), any()))
        .thenReturn(Future.succeededFuture(userResponse));
    when(mockLoginRepository.patronLogin(eq("leslie"), eq("0989"), any()))
        .thenReturn(Future.succeededFuture(Utils.handleErrors(new RequestThrowable(null) {
          private static final long serialVersionUID = -9126223501276281006L;
          public List<String> getErrorMessages() {
            return Collections.singletonList("Password does not match");
          }
        })));

    final SessionData sessionData = TestUtils.getMockedSessionData();
    sessionData.setPatronPasswordVerificationRequired(true);

    final PasswordVerifier passwordVerifier = new PasswordVerifier(mockUsersRepository,
        mockLoginRepository);
    passwordVerifier.verifyPatronPassword(patronIdentifier, "0989", sessionData).setHandler(
        testContext.succeeding(verification -> testContext.verify(() -> {
          assertNotNull(verification);
          assertEquals("997383903573496", verification.getUser().getBarcode());
          assertEquals("leslie", verification.getUser().getUsername());
          assertFalse(verification.getPasswordVerified());
          assertEquals(Collections.singletonList("Password does not match"),
              verification.getErrorMessages());

          testContext.completeNow();
        })));
  }

  @Test
  void canVerifyPasswordNotRequired(
      Vertx vertx,
      VertxTestContext testContext,
      @Mock UsersRepository mockUsersRepository,
      @Mock LoginRepository mockLoginRepository) {
    final String patronIdentifier = "1234567890";

    final SessionData sessionData = TestUtils.getMockedSessionData();
    sessionData.setPatronPasswordVerificationRequired(false);

    final PasswordVerifier passwordVerifier = new PasswordVerifier(mockUsersRepository,
        mockLoginRepository);
    passwordVerifier.verifyPatronPassword(patronIdentifier, "0989", sessionData).setHandler(
        testContext.succeeding(verification -> testContext.verify(() -> {
          assertNotNull(verification);
          assertNull(verification.getUser());
          assertNull(verification.getPasswordVerified());
          assertNull(verification.getErrorMessages());

          testContext.completeNow();
        })));
  }
}
