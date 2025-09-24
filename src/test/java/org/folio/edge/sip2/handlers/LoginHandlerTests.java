package org.folio.edge.sip2.handlers;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.folio.edge.sip2.domain.TenantResolutionContext.createContextForLoginPhase;
import static org.folio.edge.sip2.parser.Command.LOGIN_RESPONSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import freemarker.template.Template;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.folio.edge.sip2.domain.messages.enumerations.PWDAlgorithm;
import org.folio.edge.sip2.domain.messages.enumerations.UIDAlgorithm;
import org.folio.edge.sip2.domain.messages.requests.Login;
import org.folio.edge.sip2.domain.messages.responses.ACSStatus;
import org.folio.edge.sip2.domain.messages.responses.LoginResponse;
import org.folio.edge.sip2.exception.TenantNotResolvedException;
import org.folio.edge.sip2.handlers.freemarker.FormatDateTimeMethodModel;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerRepository;
import org.folio.edge.sip2.handlers.freemarker.FreemarkerUtils;
import org.folio.edge.sip2.repositories.ConfigurationRepository;
import org.folio.edge.sip2.repositories.LoginRepository;
import org.folio.edge.sip2.service.config.TenantConfigurationService;
import org.folio.edge.sip2.service.tenant.Sip2TenantResolver;
import org.folio.edge.sip2.session.SessionData;
import org.folio.okapi.common.refreshtoken.client.ClientException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({ VertxExtension.class, MockitoExtension.class })
public class LoginHandlerTests {

  @InjectMocks private LoginHandler handler;
  @Mock private LoginRepository mockLoginRepository;
  @Mock private Sip2TenantResolver sip2TenantResolver;
  @Mock private ConfigurationRepository mockConfigurationRepository;
  @Mock private TenantConfigurationService mockTenantConfigurationService;
  @Spy private Template template =
      FreemarkerRepository.getInstance().getFreemarkerTemplate(LOGIN_RESPONSE);
  @Captor private ArgumentCaptor<SessionData> sessionDataCaptor;

  @Test
  public void canExecuteASampleLoginUsingHandler(VertxTestContext testContext) throws Exception {
    final Login login = Login.builder()
        .uidAlgorithm(UIDAlgorithm.NO_ENCRYPTION)
        .pwdAlgorithm(PWDAlgorithm.NO_ENCRYPTION)
        .loginUserId("test")
        .loginPassword("xyzzy")
        .locationCode("library")
        .build();

    var sessionData = SessionData.createSession("diku", '|', false, "IBM850");
    var tenantResolutionContext = createContextForLoginPhase(sip2TenantConfig(), sessionData);
    when(mockTenantConfigurationService.getConfiguration()).thenReturn(sip2TenantConfig());
    when(sip2TenantResolver.resolve(tenantResolutionContext)).thenReturn(Optional.empty());

    when(mockLoginRepository.login(any(), any()))
        .thenReturn(Future.succeededFuture(LoginResponse.builder().ok(TRUE).build()));

    when(mockConfigurationRepository.getACSStatus(any()))
        .thenReturn(Future.succeededFuture(ACSStatus.builder().checkinOk(true).build()));

    sessionData.setConfigurationLoaded(true);

    handler.execute(login, sessionData).onComplete(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          final String expectedString = "941";

          assertEquals(expectedString, sipMessage);

          testContext.completeNow();
        })));

    verify(template).process(any(), any());
  }

  @Test
  public void canExecuteASampleLoginUsingHandlerWithNewTenant(VertxTestContext testContext) {
    final Login login = Login.builder()
        .uidAlgorithm(UIDAlgorithm.NO_ENCRYPTION)
        .pwdAlgorithm(PWDAlgorithm.NO_ENCRYPTION)
        .loginUserId("test")
        .loginPassword("xyzzy")
        .locationCode("library")
        .build();

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");
    var tenantContext = createContextForLoginPhase(sip2TenantConfig(), sessionData);
    when(mockTenantConfigurationService.getConfiguration()).thenReturn(sip2TenantConfig());
    when(sip2TenantResolver.resolve(tenantContext)).thenReturn(Optional.of(newTenantConfig()));

    when(mockLoginRepository.login(any(), sessionDataCaptor.capture()))
        .thenReturn(Future.succeededFuture(LoginResponse.builder().ok(TRUE).build()));

    when(mockConfigurationRepository.getACSStatus(any()))
        .thenReturn(Future.succeededFuture(ACSStatus.builder().checkinOk(true).build()));

    sessionData.setConfigurationLoaded(true);

    handler.execute(login, sessionData).onComplete(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          final String expectedString = "941";

          assertEquals(expectedString, sipMessage);

          testContext.completeNow();
        })));

    var capturedSessionData = sessionDataCaptor.getValue();
    assertEquals("test_tenant", capturedSessionData.getTenant());
  }

  @Test
  public void cannotExecuteASampleLoginUsingHandlerWhenTenantIsNull(VertxTestContext testContext) {
    final Login login = Login.builder()
        .uidAlgorithm(UIDAlgorithm.NO_ENCRYPTION)
        .pwdAlgorithm(PWDAlgorithm.NO_ENCRYPTION)
        .loginUserId("test")
        .loginPassword("xyzzy")
        .locationCode("library")
        .build();

    var sessionData = SessionData.createSession(null, '|', false, "IBM850");
    var tenantContext = createContextForLoginPhase(sip2TenantConfig(), sessionData);
    when(mockTenantConfigurationService.getConfiguration()).thenReturn(sip2TenantConfig());
    when(sip2TenantResolver.resolve(tenantContext)).thenReturn(Optional.empty());

    handler.execute(login, sessionData).onComplete(
        testContext.failing(error -> testContext.verify(() -> {
          var expectedMessage = "Tenant configuration is not resolved for session";
          assertInstanceOf(TenantNotResolvedException.class, error);
          assertTrue(error.getMessage().startsWith(expectedMessage));

          testContext.completeNow();
        })));
  }

  @Test
  public void canExecuteASampleLoginUsingHandlerWithFailedConfig(VertxTestContext testContext) {
    final Login login = Login.builder()
        .uidAlgorithm(UIDAlgorithm.NO_ENCRYPTION)
        .pwdAlgorithm(PWDAlgorithm.NO_ENCRYPTION)
        .loginUserId("test")
        .loginPassword("xyzzy")
        .locationCode("library")
        .build();

    var sessionData = SessionData.createSession("diku", '|', false, "IBM850");
    var tenantResolutionContext = createContextForLoginPhase(sip2TenantConfig(), sessionData);
    when(mockTenantConfigurationService.getConfiguration()).thenReturn(sip2TenantConfig());
    when(sip2TenantResolver.resolve(tenantResolutionContext)).thenReturn(Optional.empty());

    when(mockLoginRepository.login(any(), any()))
        .thenReturn(Future.succeededFuture(LoginResponse.builder().ok(TRUE).build()));

    when(mockConfigurationRepository.getACSStatus(any()))
        .thenReturn(Future.failedFuture("Unable to load config"));

    handler.execute(login, sessionData).onComplete(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          final String expectedString = "941";

          assertEquals(expectedString, sipMessage);

          testContext.completeNow();
        })));
  }

  @Test
  public void canExecuteASampleFailedLoginUsingHandler(VertxTestContext testContext) {
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

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    handler.execute(login, sessionData).onComplete(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          final String expectedString = "940";

          assertEquals(expectedString, sipMessage);

          testContext.completeNow();
        })));
  }

  @Test
  void canExecuteFailedLoginUsingHandler(VertxTestContext testContext) {
    final Login login = Login.builder()
        .uidAlgorithm(UIDAlgorithm.NO_ENCRYPTION)
        .pwdAlgorithm(PWDAlgorithm.NO_ENCRYPTION)
        .loginUserId("test")
        .loginPassword("xyzzy")
        .locationCode("library")
        .build();

    var sessionData = SessionData.createSession("diku", '|', false, "IBM850");
    var tenantResolutionContext = createContextForLoginPhase(sip2TenantConfig(), sessionData);
    when(mockTenantConfigurationService.getConfiguration()).thenReturn(sip2TenantConfig());
    when(sip2TenantResolver.resolve(tenantResolutionContext)).thenReturn(Optional.empty());

    when(mockLoginRepository.login(any(), any()))
        .thenReturn(Future.failedFuture(new ClientException("Incorrect username")));

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
        () -> new LoginHandler(null, null, null, null, null));

    assertEquals("LoginRepository cannot be null", thrown.getMessage());
  }

  @Test
  public void cannotCreateHandlerDueToMissingTemplate() {
    final NullPointerException thrown = assertThrows(NullPointerException.class,
        () -> new LoginHandler(mockLoginRepository, mockConfigurationRepository,
            sip2TenantResolver, mockTenantConfigurationService, null));

    assertEquals("Template cannot be null", thrown.getMessage());
  }

  private static JsonObject sip2TenantConfig() {
    return new JsonObject().put("tenant", "diku");
  }

  private static JsonObject newTenantConfig() {
    return new JsonObject().put("tenant", "test_tenant");
  }
}
