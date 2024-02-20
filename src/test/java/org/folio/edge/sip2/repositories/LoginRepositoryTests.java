package org.folio.edge.sip2.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.impl.NoStackTraceThrowable;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.Collections;
import org.folio.edge.sip2.domain.messages.enumerations.PWDAlgorithm;
import org.folio.edge.sip2.domain.messages.enumerations.UIDAlgorithm;
import org.folio.edge.sip2.domain.messages.requests.Login;
import org.folio.edge.sip2.session.SessionData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
public class LoginRepositoryTests {

  @Test
  public void canCreateLoginRepository(
      @Mock IResourceProvider<IRequestData> mockFolioResource) {
    final LoginRepository loginRepository =
        new LoginRepository(mockFolioResource);

    assertNotNull(loginRepository);
  }

  @Test
  public void cannotCreateLoginRepositoryWhenResourceProviderIsNull() {
    final NullPointerException thrown = assertThrows(
        NullPointerException.class,
        () -> new LoginRepository(null));

    assertEquals("Resource provider cannot be null", thrown.getMessage());
  }

  @Test
  public void canLogin(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    final Login login = Login.builder()
        .uidAlgorithm(UIDAlgorithm.NO_ENCRYPTION)
        .pwdAlgorithm(PWDAlgorithm.NO_ENCRYPTION)
        .loginUserId("test")
        .loginPassword("xyzzy")
        .locationCode("library")
        .build();

    when(mockFolioProvider.loginWithSupplier(any(), any(), any(), anyBoolean()))
        .thenReturn(Future.succeededFuture("tok"));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    final LoginRepository loginRepository = new LoginRepository(mockFolioProvider);
    loginRepository.login(login, sessionData).onComplete(
        testContext.succeeding(loginResponse -> testContext.verify(() -> {
          assertNotNull(loginResponse);
          assertTrue(loginResponse.getOk());

          testContext.completeNow();
        })));
  }

  @Test
  public void cannotLogin(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    final Login login = Login.builder()
        .uidAlgorithm(UIDAlgorithm.NO_ENCRYPTION)
        .pwdAlgorithm(PWDAlgorithm.NO_ENCRYPTION)
        .loginUserId("test")
        .loginPassword("xyzzy")
        .locationCode("library")
        .build();

    when(mockFolioProvider.loginWithSupplier(any(), any(), any(), anyBoolean()))
        .thenReturn(Future.succeededFuture(null));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    final LoginRepository loginRepository = new LoginRepository(mockFolioProvider);
    loginRepository.login(login, sessionData).onComplete(
        testContext.succeeding(loginResponse -> testContext.verify(() -> {
          assertNotNull(loginResponse);
          assertFalse(loginResponse.getOk());

          testContext.completeNow();
        })));
  }

  @Test
  public void canPatronLogin(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    final String username = "test";
    final String password = "xyzzy";

    when(mockFolioProvider.loginWithSupplier(any(), any(), any(), anyBoolean()))
        .thenReturn(Future.succeededFuture("tok"));
    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    final LoginRepository loginRepository = new LoginRepository(mockFolioProvider);
    loginRepository.patronLogin(username, password, sessionData).onComplete(
        testContext.succeeding(loginResponse -> testContext.verify(() -> {
          assertNotNull(loginResponse);
          testContext.completeNow();
        })));
  }

  @Test
  public void cannotPatronLogin(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    final String username = "test";
    final String password = "xyzzy";

    when(mockFolioProvider.loginWithSupplier(any(), any(), any(), anyBoolean()))
        .thenReturn(Future.succeededFuture(null));
    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    final LoginRepository loginRepository = new LoginRepository(mockFolioProvider);
    loginRepository.patronLogin(username, password, sessionData).onComplete(
        testContext.succeeding(loginResponse -> testContext.verify(() -> {
          assertNull(loginResponse);
          testContext.completeNow();
        })));
  }

  @Test
  public void canPatronLoginNoCache(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    final String username = "test";
    final String password = "xyzzy";

    when(mockFolioProvider.loginWithSupplier(any(), any(), any(), anyBoolean()))
        .thenReturn(Future.succeededFuture("tok"));
    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    final LoginRepository loginRepository = new LoginRepository(mockFolioProvider);
    loginRepository.patronLoginNoCache(username, password, sessionData).onComplete(
        testContext.succeeding(loginResponse -> testContext.verify(() -> {
          assertNotNull(loginResponse);
          testContext.completeNow();
        })));
  }

  @Test
  public void cannotPatronLoginNoCache(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    final String username = "test";
    final String password = "xyzzy";

    when(mockFolioProvider.loginWithSupplier(any(), any(), any(), anyBoolean()))
        .thenReturn(Future.succeededFuture(null));
    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    final LoginRepository loginRepository = new LoginRepository(mockFolioProvider);
    loginRepository.patronLoginNoCache(username, password, sessionData).onComplete(
        testContext.succeeding(loginResponse -> testContext.verify(() -> {
          assertNull(loginResponse);
          testContext.completeNow();
        })));
  }
}
