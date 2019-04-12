package org.folio.edge.sip2.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.folio.edge.sip2.domain.messages.enumerations.PWDAlgorithm;
import org.folio.edge.sip2.domain.messages.enumerations.UIDAlgorithm;
import org.folio.edge.sip2.domain.messages.requests.Login;
import org.folio.edge.sip2.repositories.IRequestData;
import org.folio.edge.sip2.repositories.IResourceProvider;
import org.folio.edge.sip2.repositories.LoginRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class LoginHandlerTests {
  @Test
  public void canExecuteASampleLoginUsingHandlersFactory(
      Vertx vertx,
      VertxTestContext testContext) {
    final Login login = Login.builder()
        .uidAlgorithm(UIDAlgorithm.NO_ENCRYPTION)
        .pwdAlgorithm(PWDAlgorithm.NO_ENCRYPTION)
        .loginUserId("test")
        .loginPassword("xyzzy")
        .locationCode("library")
        .build();

    @SuppressWarnings("unchecked")
    final IResourceProvider<IRequestData> mockFolioProvider =
        mock(IResourceProvider.class);
    when(mockFolioProvider.createResource(any()))
      .thenReturn(Future.succeededFuture(new JsonObject()));

    final LoginHandler handler = ((LoginHandler) HandlersFactory
        .getLoginHandlerInstance(null, mockFolioProvider, null));

    handler.execute(login).setHandler(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          final String expectedString = "941";

          assertEquals(expectedString, sipMessage);

          testContext.completeNow();
        })));
  }

  @Test
  public void canExecuteASampleFailedLoginUsingHandlersFactory(
      Vertx vertx,
      VertxTestContext testContext) {
    final Login login = Login.builder()
        .uidAlgorithm(UIDAlgorithm.NO_ENCRYPTION)
        .pwdAlgorithm(PWDAlgorithm.NO_ENCRYPTION)
        .loginUserId("test")
        .loginPassword("xyzzy")
        .locationCode("library")
        .build();

    @SuppressWarnings("unchecked")
    final IResourceProvider<IRequestData> mockFolioProvider =
        mock(IResourceProvider.class);
    when(mockFolioProvider.createResource(any()))
      .thenReturn(Future.succeededFuture(null));

    final LoginHandler handler = ((LoginHandler) HandlersFactory
        .getLoginHandlerInstance(null, mockFolioProvider, null));

    handler.execute(login).setHandler(
        testContext.succeeding(sipMessage -> testContext.verify(() -> {
          final String expectedString = "940";

          assertEquals(expectedString, sipMessage);

          testContext.completeNow();
        })));
  }

  @Test
  public void cannotCreateHandlerDueToMissingLoginRepository() {
    final NullPointerException thrown = assertThrows(
        NullPointerException.class,
        () -> new LoginHandler(null, null));

    assertEquals("LoginRepository cannot be null", thrown.getMessage());
  }

  @Test
  public void cannotCreateHandlerDueToMissingTemplate() {
    @SuppressWarnings("unchecked")
    final IResourceProvider<IRequestData> mockFolioProvider =
        mock(IResourceProvider.class);
    when(mockFolioProvider.createResource(any()))
      .thenReturn(Future.succeededFuture(new JsonObject()));
    final LoginRepository loginRepository =
        new LoginRepository(mockFolioProvider);

    final NullPointerException thrown = assertThrows(
        NullPointerException.class,
        () -> new LoginHandler(loginRepository, null));

    assertEquals("Template cannot be null", thrown.getMessage());
  }
}
