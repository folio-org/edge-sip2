package org.folio.edge.sip2.handlers;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.folio.edge.sip2.parser.Command.LOGIN_RESPONSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.HashMap;
import java.util.Map;
import org.folio.edge.sip2.domain.messages.enumerations.PWDAlgorithm;
import org.folio.edge.sip2.domain.messages.enumerations.UIDAlgorithm;
import org.folio.edge.sip2.domain.messages.requests.Login;
import org.folio.edge.sip2.domain.messages.responses.ACSStatus;
import org.folio.edge.sip2.domain.messages.responses.LoginResponse;
import org.folio.edge.sip2.handlers.freemarker.FormatDateTimeMethodModel;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerUtils;
import org.folio.edge.sip2.repositories.ConfigurationRepository;
import org.folio.edge.sip2.repositories.LoginRepository;
import org.folio.edge.sip2.session.SessionData;
import org.folio.okapi.common.refreshtoken.client.ClientException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
public class LoginHandlerTests {
  @Test
  public void canExecuteASampleLoginUsingHandler(
      @Mock LoginRepository mockLoginRepository,
      @Mock ConfigurationRepository mockConfigurationRepository,
      Vertx vertx,
      VertxTestContext testContext) {
    final Login login = Login.builder()
        .uidAlgorithm(UIDAlgorithm.NO_ENCRYPTION)
        .pwdAlgorithm(PWDAlgorithm.NO_ENCRYPTION)
        .loginUserId("test")
        .loginPassword("xyzzy")
        .locationCode("library")
        .build();

    when(mockLoginRepository.login(any(), any()))
        .thenReturn(Future.succeededFuture(LoginResponse.builder().ok(TRUE).build()));

    when(mockConfigurationRepository.getACSStatus(any()))
        .thenReturn(Future.succeededFuture(ACSStatus.builder().checkinOk(true).build()));

    final LoginHandler handler = new LoginHandler(mockLoginRepository,
        mockConfigurationRepository,
        FreemarkerRepository.getInstance().getFreemarkerTemplate(LOGIN_RESPONSE));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");
    sessionData.setConfigurationLoaded(true);

    handler.execute(login, sessionData).onComplete(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          final String expectedString = "941";

          assertEquals(expectedString, sipMessage);

          testContext.completeNow();
        })));
  }


  @Test
  public void canExecuteASampleLoginUsingHandlerWithFailedConfig(
      @Mock LoginRepository mockLoginRepository,
      @Mock ConfigurationRepository mockConfigurationRepository,
      Vertx vertx,
      VertxTestContext testContext) {
    final Login login = Login.builder()
        .uidAlgorithm(UIDAlgorithm.NO_ENCRYPTION)
        .pwdAlgorithm(PWDAlgorithm.NO_ENCRYPTION)
        .loginUserId("test")
        .loginPassword("xyzzy")
        .locationCode("library")
        .build();

    when(mockLoginRepository.login(any(), any()))
        .thenReturn(Future.succeededFuture(LoginResponse.builder().ok(TRUE).build()));

    when(mockConfigurationRepository.getACSStatus(any()))
        .thenReturn(Future.failedFuture("Unable to load config"));

    final LoginHandler handler = new LoginHandler(mockLoginRepository,
        mockConfigurationRepository,
        FreemarkerRepository.getInstance().getFreemarkerTemplate(LOGIN_RESPONSE));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    handler.execute(login, sessionData).onComplete(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          final String expectedString = "941";

          assertEquals(expectedString, sipMessage);

          testContext.completeNow();
        })));
  }



  @Test
  public void canExecuteASampleFailedLoginUsingHandler(
      @Mock LoginRepository mockLoginRepository,
      @Mock ConfigurationRepository mockConfigurationRepository,
      Vertx vertx,
      VertxTestContext testContext) {
    final Login login = Login.builder()
        .uidAlgorithm(UIDAlgorithm.NO_ENCRYPTION)
        .pwdAlgorithm(PWDAlgorithm.NO_ENCRYPTION)
        .loginUserId("test")
        .loginPassword("xyzzy")
        .locationCode("library")
        .build();

    when(mockLoginRepository.login(any(), any()))
        .thenReturn(Future.succeededFuture(LoginResponse.builder().ok(FALSE).build()));

    when(mockConfigurationRepository.getACSStatus(any()))
        .thenReturn(Future.succeededFuture(ACSStatus.builder().checkinOk(true).build()));

    final LoginHandler handler = new LoginHandler(mockLoginRepository,
        mockConfigurationRepository,
        FreemarkerRepository.getInstance().getFreemarkerTemplate(LOGIN_RESPONSE));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    handler.execute(login, sessionData).onComplete(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          final String expectedString = "940";

          assertEquals(expectedString, sipMessage);

          testContext.completeNow();
        })));
  }

  @Test
   void canExecuteFailedLoginUsingHandler(
      @Mock LoginRepository mockLoginRepository,
      @Mock ConfigurationRepository mockConfigurationRepository,
      Vertx vertx,
      VertxTestContext testContext) {
    final Login login = Login.builder()
        .uidAlgorithm(UIDAlgorithm.NO_ENCRYPTION)
        .pwdAlgorithm(PWDAlgorithm.NO_ENCRYPTION)
        .loginUserId("test")
        .loginPassword("xyzzy")
        .locationCode("library")
        .build();

    when(mockLoginRepository.login(any(), any()))
        .thenReturn(Future.failedFuture(new ClientException("Incorrect username")));

    final LoginHandler handler = new LoginHandler(mockLoginRepository,
        mockConfigurationRepository,
        FreemarkerRepository.getInstance().getFreemarkerTemplate(LOGIN_RESPONSE));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    LoginResponse loginResponse = LoginResponse.builder().ok(FALSE).build();
    sessionData.setErrorResponseMessage(constructLoginResponse(loginResponse));
    handler.execute(login, sessionData).onComplete(
        testContext.failing(sipMessage -> testContext.verify(() -> {
          final String expectedString = "Incorrect username";

          assertEquals(expectedString, sipMessage.getMessage());

          testContext.completeNow();
        })));
  }

  private String constructLoginResponse(LoginResponse loginResponse) {
    final Map<String, Object> root = new HashMap<>();
    root.put("formatDateTime", new FormatDateTimeMethodModel());
    root.put("delimiter", "|");
    root.put("loginResponse", loginResponse);

    final String response = FreemarkerUtils
        .executeFreemarkerTemplate(null, root,
        FreemarkerRepository
          .getInstance()
          .getFreemarkerTemplate(LOGIN_RESPONSE));
    return response;
  }


  @Test
  public void cannotCreateHandlerDueToMissingLoginRepository() {
    final NullPointerException thrown = assertThrows(
        NullPointerException.class,
        () -> new LoginHandler(null, null,null));

    assertEquals("LoginRepository cannot be null", thrown.getMessage());
  }

  @Test
  public void cannotCreateHandlerDueToMissingTemplate() {
    final NullPointerException thrown = assertThrows(NullPointerException.class,
        () -> new LoginHandler(mock(LoginRepository.class), mock(ConfigurationRepository.class),
            null));

    assertEquals("Template cannot be null", thrown.getMessage());
  }
}
