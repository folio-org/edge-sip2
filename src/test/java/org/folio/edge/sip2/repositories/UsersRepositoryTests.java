package org.folio.edge.sip2.repositories;

import static org.folio.edge.sip2.api.support.TestUtils.getJsonFromFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.impl.NoStackTraceThrowable;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.folio.edge.sip2.session.SessionData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
public class UsersRepositoryTests {

  @Test
  public void canCreateUsersRepository(
      @Mock IResourceProvider<IRequestData> mockFolioResource) {
    final UsersRepository usersRepository =
        new UsersRepository(mockFolioResource);

    assertNotNull(usersRepository);
  }

  @Test
  public void cannotCreateUsersRepositoryWhenResourceProviderIsNull() {
    final NullPointerException thrown = assertThrows(
        NullPointerException.class,
        () -> new UsersRepository(null));

    assertEquals("Resource provider cannot be null", thrown.getMessage());
  }

  @Test
  public void canGetUserByBarcode(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {

    final String userResponseJson = getJsonFromFile("json/users_response.json");
    final JsonObject userResponse = new JsonObject(userResponseJson);

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(userResponse,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    final UsersRepository usersRepository = new UsersRepository(mockFolioProvider);
    usersRepository.getUserByBarcode("997383903573496", sessionData).setHandler(
        testContext.succeeding(user -> testContext.verify(() -> {
          assertNotNull(user);
          assertEquals("997383903573496", user.getBarcode());

          testContext.completeNow();
        })));
  }

  @Test
  public void cannotGetUserByBarcode(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.failedFuture(new NoStackTraceThrowable("Test failure")));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    final UsersRepository usersRepository = new UsersRepository(mockFolioProvider);
    usersRepository.getUserByBarcode("1234667", sessionData).setHandler(
        testContext.succeeding(user -> testContext.verify(() -> {
          assertNull(user);

          testContext.completeNow();
        })));
  }
}
