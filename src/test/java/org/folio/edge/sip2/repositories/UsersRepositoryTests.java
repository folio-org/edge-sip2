package org.folio.edge.sip2.repositories;

import static org.folio.edge.sip2.api.support.TestUtils.getJsonFromFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
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
import org.folio.edge.sip2.utils.Utils;
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
    usersRepository.getUserById("997383903573496", sessionData).onComplete(
        testContext.succeeding(user -> testContext.verify(() -> {
          assertNotNull(user);
          assertEquals("997383903573496", user.getBarcode());

          testContext.completeNow();
        })));
  }

  @Test
  public void canGetUserByUsername(Vertx vertx,
                                  VertxTestContext testContext,
                                  @Mock IResourceProvider<IRequestData> mockFolioProvider) {

    final String userResponseJson = getJsonFromFile("json/users_response.json");
    final JsonObject userResponse = new JsonObject(userResponseJson);

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(userResponse,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    final UsersRepository usersRepository = new UsersRepository(mockFolioProvider);
    usersRepository.getUserById("leslie", sessionData).onComplete(
        testContext.succeeding(user -> testContext.verify(() -> {
          assertNotNull(user);
          assertEquals("leslie", user.getUsername());

          testContext.completeNow();
        })));
  }

  /**
   * In the unlikely case of multiple user records returning for the same externalSystemId
   * because it's not unique, the service should only return the first record.
   * @param vertx vertx object
   * @param testContext test context object
   * @param mockFolioProvider a mock provider simulating backend FOLIO
   */
  @Test
  public void canGetOnlyOneUserByExternalSystemId(Vertx vertx,
                                   VertxTestContext testContext,
                                   @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    final String userResponseJson = getJsonFromFile("json/multiple_users_response.json");
    final JsonObject userResponse = new JsonObject(userResponseJson);

    final String extSystemId = "4f0e711c-d583-41e0-9555-b62f1725023f";
    final String expectedPath = "/users?limit=1&query="
        + Utils.encode("(barcode==" + extSystemId
        + " or externalSystemId==" + extSystemId
        + " or username==" + extSystemId + ')');

    when(mockFolioProvider.retrieveResource(
        argThat((IRequestData data) -> data.getPath().equals(expectedPath))))
        .thenReturn(Future.succeededFuture(new FolioResource(userResponse,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    final UsersRepository usersRepository = new UsersRepository(mockFolioProvider);
    usersRepository.getUserById(extSystemId, sessionData).onComplete(
        testContext.succeeding(user -> testContext.verify(() -> {
          assertNotNull(user);
          assertEquals(extSystemId, user.getExtSystemId());
          assertEquals("adarius1", user.getUsername());
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
    usersRepository.getUserById("1234667", sessionData).onComplete(
        testContext.succeeding(user -> testContext.verify(() -> {
          assertNull(user);

          testContext.completeNow();
        })));
  }
}
