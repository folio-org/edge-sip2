package org.folio.edge.sip2.repositories;

import static org.folio.edge.sip2.api.support.TestUtils.getJsonFromFile;
import static org.folio.util.PercentCodec.encode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.impl.NoStackTraceThrowable;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.folio.edge.sip2.api.support.TestUtils;
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
    final String userBlResponseJson = getJsonFromFile("json/bl_user_response.json");
    final JsonObject userResponse = new JsonObject(userResponseJson);
    final JsonObject userBlResponse = new JsonObject(userBlResponseJson);

    final String barcode = "997383903573496";
    final String userId = "4f0e711c-d583-41e0-9555-b62f1725023f";

    final String expectedUsersBlQueryPath = "/bl-users/by-id/" + userId;

    doReturn(Future.succeededFuture(new FolioResource(userBlResponse,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))))
        .when(mockFolioProvider).retrieveResource(
            argThat((IRequestData data2) -> data2.getPath().equals(expectedUsersBlQueryPath)));


    doReturn(Future.succeededFuture(new FolioResource(userResponse,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))))
        .when(mockFolioProvider).retrieveResource(usersQueryPath(barcode));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    final UsersRepository usersRepository = new UsersRepository(mockFolioProvider);
    usersRepository.getUserById(barcode, sessionData).onComplete(
        testContext.succeeding(extendedUser -> testContext.verify(() -> {
          assertNotNull(extendedUser);
          assertEquals("997383903573496", extendedUser.getUser().getBarcode());

          testContext.completeNow();
        })));
  }

  @Test
  public void canGetUserByUsername(Vertx vertx,
                                  VertxTestContext testContext,
                                  @Mock IResourceProvider<IRequestData> mockFolioProvider) {

    final String userResponseJson = getJsonFromFile("json/users_response.json");
    final JsonObject userResponse = new JsonObject(userResponseJson);
    final String userBlResponseJson = getJsonFromFile("json/bl_user_response.json");
    final JsonObject userBlResponse = new JsonObject(userBlResponseJson);

    final String username = "leslie";
    final String userId = "4f0e711c-d583-41e0-9555-b62f1725023f";

    final String expectedUsersBlQueryPath = "/bl-users/by-id/" + userId;

    doReturn(Future.succeededFuture(new FolioResource(userBlResponse,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))))
        .when(mockFolioProvider).retrieveResource(
            argThat((IRequestData data2) -> data2.getPath().equals(expectedUsersBlQueryPath)));

    doReturn(Future.succeededFuture(new FolioResource(userResponse,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))))
        .when(mockFolioProvider).retrieveResource(usersQueryPath(username));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    final UsersRepository usersRepository = new UsersRepository(mockFolioProvider);
    usersRepository.getUserById(username, sessionData).onComplete(
        testContext.succeeding(extendedUser -> testContext.verify(() -> {
          assertNotNull(extendedUser);
          assertEquals(username, extendedUser.getUser().getUsername());

          testContext.completeNow();
        })));
  }

  @Test
  void canGetUserByUsernameWithoutPatronGroup(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {

    final String userResponseJson = getJsonFromFile("json/users_response.json");
    final JsonObject userResponse = new JsonObject(userResponseJson);
    final String userBlResponseJson = getJsonFromFile("json/bl_user_response_no_patrongroup.json");
    final JsonObject userBlResponse = new JsonObject(userBlResponseJson);

    final String username = "leslie";
    final String userId = "4f0e711c-d583-41e0-9555-b62f1725023f";

    final String expectedUsersBlQueryPath = "/bl-users/by-id/" + userId;

    doReturn(Future.succeededFuture(new FolioResource(userBlResponse,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))))
        .when(mockFolioProvider).retrieveResource(
        argThat((IRequestData data2) -> data2.getPath().equals(expectedUsersBlQueryPath)));

    doReturn(Future.succeededFuture(new FolioResource(userResponse,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))))
        .when(mockFolioProvider).retrieveResource(usersQueryPath(username));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    final UsersRepository usersRepository = new UsersRepository(mockFolioProvider);
    usersRepository.getUserById(username, sessionData).onComplete(
        testContext.succeeding(extendedUser -> testContext.verify(() -> {
          assertNotNull(extendedUser);
          assertEquals(username, extendedUser.getUser().getUsername());
          assertNull(extendedUser.getPatronGroup());
          testContext.completeNow();
        })));
  }

  /**
   * In the unlikely case of multiple user records returning for the same externalSystemId
   * because it's not unique, the service should only return the first record.
   * @param vertx vertx object
   * @param testContext test context object
   */
  @Test
  public void canGetOnlyOneUserByExternalSystemId(Vertx vertx,
                                   VertxTestContext testContext
  ) {
    final String userResponseJson = getJsonFromFile("json/multiple_users_response.json");
    final String userBlResponseJson = getJsonFromFile("json/bl_user_response.json");
    assertNotNull(userBlResponseJson);
    assertTrue(!userBlResponseJson.isEmpty());
    final JsonObject userResponse = new JsonObject(userResponseJson);
    final JsonObject userBlResponse = new JsonObject(userBlResponseJson);
    assertTrue(!userBlResponse.isEmpty());

    final String extSystemId = "4f0e711c-d583-41e0-9555-b62f1725023f";
    final String userId = "4f0e711c-d583-41e0-9555-b62f1725023f";

    final String expectedUsersBlQueryPath = "/bl-users/by-id/" + userId;
    IResourceProvider<IRequestData> mockFolioProvider
        = mock(FolioResourceProvider.class, withSettings().verboseLogging().lenient());

    doReturn(Future.succeededFuture(new FolioResource(userBlResponse,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))))
        .when(mockFolioProvider).retrieveResource(
            argThat((IRequestData data2) -> data2.getPath().equals(expectedUsersBlQueryPath)));

    doReturn(Future.succeededFuture(new FolioResource(userResponse,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))))
        .when(mockFolioProvider).retrieveResource(usersQueryPath(extSystemId));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    final UsersRepository usersRepository = new UsersRepository(mockFolioProvider);
    usersRepository.getUserById(extSystemId, sessionData).onComplete(
        testContext.succeeding(extendedUser -> testContext.verify(() -> {
          assertNotNull(extendedUser);
          assertEquals(extSystemId, extendedUser.getUser().getExtSystemId());
          assertEquals("adarius1", extendedUser.getUser().getUsername());
          testContext.completeNow();
        })));
  }

  @Test
  public void cannotGetUserByBarcode(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {

    final String barcode = "1234667";

    doReturn(Future.failedFuture(new NoStackTraceThrowable("Test failure")))
        .when(mockFolioProvider).retrieveResource(usersQueryPath(barcode));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    final UsersRepository usersRepository = new UsersRepository(mockFolioProvider);
    usersRepository.getUserById(barcode, sessionData).onComplete(
        testContext.succeeding(extendedUser -> testContext.verify(() -> {
          assertNull(extendedUser);

          testContext.completeNow();
        })));
  }

  @Test
  public void cannotVerifyUserPin(Vertx vertx, VertxTestContext vertxTestContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    final String userId = "4f0e711c-d583-41e0-9555-b62f1725023f";
    final String pin = "1234";

    doReturn(Future.failedFuture(new NoStackTraceThrowable("Test failure")))
            .when(mockFolioProvider).createResource(any());

    final SessionData sessionData = TestUtils.getMockedSessionData();
    final UsersRepository usersRepository = new UsersRepository(mockFolioProvider);

    usersRepository.checkPatronPin(userId, pin, sessionData).onComplete(
        vertxTestContext.succeeding(pinResult -> vertxTestContext.verify(() -> {
          assertNotNull(pinResult);
          assertFalse(pinResult);
          vertxTestContext.completeNow();
        })));
  }


  @Test
  public void canVerifyUserPin(Vertx vertx, VertxTestContext vertxTestContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    final String userId = "4f0e711c-d583-41e0-9555-b62f1725023f";
    final String pin = "1234";

    doReturn(Future.succeededFuture(
        new FolioResource(null, MultiMap.caseInsensitiveMultiMap()
            .add("x-okapi-token", "4321"))))
            .when(mockFolioProvider).createResource(any());

    final SessionData sessionData = TestUtils.getMockedSessionData();
    final UsersRepository usersRepository = new UsersRepository(mockFolioProvider);

    usersRepository.checkPatronPin(userId, pin, sessionData).onComplete(
        vertxTestContext.succeeding(pinResult -> vertxTestContext.verify(() -> {
          assertNotNull(pinResult);
          assertTrue(pinResult);
          vertxTestContext.completeNow();
        })));
  }


  @Test
  public void canDoPatronPinVerification(Vertx vertx, VertxTestContext vertxTestContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    final String userId = "4f0e711c-d583-41e0-9555-b62f1725023f";
    final String pin = "1234";
    final String barcode = "997383903573496";
    final String userResponseJson = getJsonFromFile("json/users_response.json");
    final JsonObject userResponse = new JsonObject(userResponseJson);
    final String userBlResponseJson = getJsonFromFile("json/bl_user_response.json");
    final JsonObject userBlResponse = new JsonObject(userBlResponseJson);

    final String expectedUsersBlQueryPath = "/bl-users/by-id/" + userId;

    doReturn(Future.succeededFuture(
        Boolean.TRUE))
            .when(mockFolioProvider).doPinCheck(any());

    doReturn(Future.succeededFuture(new FolioResource(userBlResponse,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))))
        .when(mockFolioProvider).retrieveResource(
            argThat((IRequestData data2) -> data2.getPath().equals(expectedUsersBlQueryPath)));

    doReturn(Future.succeededFuture(new FolioResource(userResponse,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))))
          .when(mockFolioProvider).retrieveResource(usersQueryPath(barcode));

    final SessionData sessionData = TestUtils.getMockedSessionData();
    final UsersRepository usersRepository = new UsersRepository(mockFolioProvider);

    usersRepository.doPatronPinVerification(barcode, pin, sessionData).onComplete(
        vertxTestContext.succeeding(pinResult -> vertxTestContext.verify(() -> {
          assertNotNull(pinResult);
          assertTrue(pinResult.getPasswordVerified());
          vertxTestContext.completeNow();
        })));


  }

  @Test
  public void cannotDoPatronPinVerification(Vertx vertx, VertxTestContext vertxTestContext,
        @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    final String userId = "4f0e711c-d583-41e0-9555-b62f1725023f";
    final String pin = "1234";
    final String barcode = "997383903573496";
    final String userResponseJson = getJsonFromFile("json/users_response.json");
    final JsonObject userResponse = new JsonObject(userResponseJson);
    final String userBlResponseJson = getJsonFromFile("json/bl_user_response.json");
    final JsonObject userBlResponse = new JsonObject(userBlResponseJson);

    final String expectedUsersBlQueryPath = "/bl-users/by-id/" + userId;

    //doReturn(Future.failedFuture(new NoStackTraceThrowable("Test failure")))
    doReturn(Future.failedFuture("Failed"))
            .when(mockFolioProvider).doPinCheck(any());

    doReturn(Future.succeededFuture(new FolioResource(userBlResponse,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))))
        .when(mockFolioProvider).retrieveResource(
            argThat((IRequestData data2) -> data2.getPath().equals(expectedUsersBlQueryPath)));

    doReturn(Future.succeededFuture(new FolioResource(userResponse,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))))
        .when(mockFolioProvider).retrieveResource(usersQueryPath(barcode));

    final SessionData sessionData = TestUtils.getMockedSessionData();
    sessionData.setPatronPasswordVerificationRequired(true);
    final UsersRepository usersRepository = new UsersRepository(mockFolioProvider);

    usersRepository.doPatronPinVerification(barcode, pin, sessionData).onComplete(
        vertxTestContext.succeeding(pinResult -> vertxTestContext.verify(() -> {
          assertNotNull(pinResult);
          assertFalse(pinResult.getPasswordVerified());
          vertxTestContext.completeNow();
        })));
  }

  @Test
  public void canVerifyPatronPin(Vertx vertx, VertxTestContext vertxTestContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    final String userId = "4f0e711c-d583-41e0-9555-b62f1725023f";
    final String pin = "1234";
    final String barcode = "997383903573496";
    final String userResponseJson = getJsonFromFile("json/users_response.json");
    final JsonObject userResponse = new JsonObject(userResponseJson);
    final String userBlResponseJson = getJsonFromFile("json/bl_user_response.json");
    final JsonObject userBlResponse = new JsonObject(userBlResponseJson);

    final String expectedUsersBlQueryPath = "/bl-users/by-id/" + userId;

    doReturn(Future.succeededFuture(
        Boolean.TRUE))
            .when(mockFolioProvider).doPinCheck(any());

    doReturn(Future.succeededFuture(new FolioResource(userBlResponse,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))))
        .when(mockFolioProvider).retrieveResource(
            argThat((IRequestData data2) -> data2.getPath().equals(expectedUsersBlQueryPath)));

    doReturn(Future.succeededFuture(new FolioResource(userResponse,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))))
          .when(mockFolioProvider).retrieveResource(usersQueryPath(barcode));

    final SessionData sessionData = TestUtils.getMockedSessionData();
    sessionData.setPatronPasswordVerificationRequired(true);
    final UsersRepository usersRepository = new UsersRepository(mockFolioProvider);

    usersRepository.verifyPatronPin(barcode, pin, sessionData).onComplete(
        vertxTestContext.succeeding(pinResult -> vertxTestContext.verify(() -> {
          assertNotNull(pinResult);
          assertTrue(pinResult.getPasswordVerified());
          vertxTestContext.completeNow();
        })));
  }

  @Test
  public void canVerifyPatronPinNoVerifyRequired(Vertx vertx, VertxTestContext vertxTestContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    final String userId = "4f0e711c-d583-41e0-9555-b62f1725023f";
    final String pin = "1234";
    final String barcode = "997383903573496";
    final String userResponseJson = getJsonFromFile("json/users_response.json");
    final JsonObject userResponse = new JsonObject(userResponseJson);
    final String userBlResponseJson = getJsonFromFile("json/bl_user_response.json");
    final JsonObject userBlResponse = new JsonObject(userBlResponseJson);

    final String expectedUsersBlQueryPath = "/bl-users/by-id/" + userId;

    doReturn(Future.succeededFuture(new FolioResource(userBlResponse,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))))
        .when(mockFolioProvider).retrieveResource(
            argThat((IRequestData data2) -> data2.getPath().equals(expectedUsersBlQueryPath)));

    doReturn(Future.succeededFuture(new FolioResource(userResponse,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))))
          .when(mockFolioProvider).retrieveResource(usersQueryPath(barcode));

    final SessionData sessionData = TestUtils.getMockedSessionData();
    sessionData.setPatronPasswordVerificationRequired(false);
    final UsersRepository usersRepository = new UsersRepository(mockFolioProvider);

    usersRepository.verifyPatronPin(barcode, pin, sessionData).onComplete(
        vertxTestContext.succeeding(pinResult -> vertxTestContext.verify(() -> {
          assertNotNull(pinResult);
          assertNull(pinResult.getPasswordVerified());
          vertxTestContext.completeNow();
        })));
  }

  @Test
  public void doPatronPinVerifyNullUser(Vertx vertx, VertxTestContext vertxTestContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {

    final String barcode = "997383903573496";

    doReturn(Future.failedFuture(new NoStackTraceThrowable("Null User or Something")))
        .when(mockFolioProvider).retrieveResource(usersQueryPath(barcode));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    final UsersRepository usersRepository = new UsersRepository(mockFolioProvider);

    usersRepository.doPatronPinVerification(barcode, "1234", sessionData).onComplete(
        vertxTestContext.succeeding(pinResult -> vertxTestContext.verify(() -> {
          assertNotNull(pinResult);
          assertFalse(pinResult.getPasswordVerified());
          vertxTestContext.completeNow();
        })));
  }

  @Test
  public void cannotGetUserWithFailedExtendLookup(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {

    final String userResponseJson = getJsonFromFile("json/users_response.json");
    final String userBlResponseJson = getJsonFromFile("json/bl_user_response.json");
    final JsonObject userResponse = new JsonObject(userResponseJson);
    final JsonObject userBlResponse = new JsonObject(userBlResponseJson);

    final String barcode = "997383903573496";
    final String userId = "4f0e711c-d583-41e0-9555-b62f1725023f";

    final String expectedUsersBlQueryPath = "/bl-users/by-id/" + userId;

    doReturn(Future.failedFuture(new NoStackTraceThrowable("bl-users lookup failed")))
        .when(mockFolioProvider).retrieveResource(
        argThat((IRequestData data2) -> data2.getPath().equals(expectedUsersBlQueryPath)));

    doReturn(Future.succeededFuture(new FolioResource(userResponse,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))))
        .when(mockFolioProvider).retrieveResource(usersQueryPath(barcode));

    final SessionData sessionData = SessionData.createSession("diku", '|', false, "IBM850");

    final UsersRepository usersRepository = new UsersRepository(mockFolioProvider);
    usersRepository.getUserById(barcode, sessionData).onComplete(
        testContext.succeeding(extendedUser -> testContext.verify(() -> {
          assertNull(extendedUser);

          testContext.completeNow();
        })));
  }

  @Test
  public void testPinRequestData() {

  }

  private IRequestData usersQueryPath(final String searchString) {
    final String expectedUsersQueryPath = "/users?limit=1&query="
        + encode("barcode==\"" + searchString + "\""
        + " or externalSystemId==\"" + searchString + "\""
        + " or username==\"" + searchString + "\"");
    return argThat((IRequestData data) -> data.getPath().equals(expectedUsersQueryPath));
  }
}
