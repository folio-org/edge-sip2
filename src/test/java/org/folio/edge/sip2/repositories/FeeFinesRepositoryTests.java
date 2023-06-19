package org.folio.edge.sip2.repositories;

import static org.folio.edge.sip2.api.support.TestUtils.getJsonFromFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.impl.NoStackTraceThrowable;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.time.Clock;
import java.util.UUID;
import java.util.concurrent.FutureTask;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.requests.FeePaid;
import org.folio.edge.sip2.repositories.domain.ExtendedUser;
import org.folio.edge.sip2.repositories.domain.User;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
class FeeFinesRepositoryTests {
  private static final String FIELD_TOTAL_RECORDS = "totalRecords";
  private static final String FIELD_MANUALBLOCKS = "manualblocks";
  private static final String FIELD_ACCOUNT = "accounts";

  @Test
  void canCreateFeeFinesRepository(
      @Mock IResourceProvider<IRequestData> mockFolioResource,
      @Mock UsersRepository mockUsersRepository,
      @Mock Clock clock) {
    final FeeFinesRepository feesFineRepository =
        new FeeFinesRepository(mockFolioResource, mockUsersRepository, clock);

    assertNotNull(feesFineRepository);
  }

  @Test
  void cannotCreateFeeFinesRepositoryWhenResourceProviderIsNull() {
    final NullPointerException thrown = assertThrows(
        NullPointerException.class,
        () -> new FeeFinesRepository(null, null, null));

    assertEquals("Resource provider cannot be null", thrown.getMessage());
  }

  @Test
  void canGetManualBlocksByUserIdWithNoBlocksApplied(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock UsersRepository mockUsersRepository,
      @Mock Clock clock) {


    final String manualBlocksResponseJson
        = getJsonFromFile("json/no_manual_blocks_response.json");
    final JsonObject manualBlocksResponse = new JsonObject(manualBlocksResponseJson);

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(manualBlocksResponse,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final FeeFinesRepository feeFinesRepository
        = new FeeFinesRepository(mockFolioProvider, mockUsersRepository, clock);
    feeFinesRepository.getManualBlocksByUserId(UUID.randomUUID().toString(),
        sessionData).onComplete(
            testContext.succeeding(manualBlocks -> testContext.verify(() -> {
              assertNotNull(manualBlocks);
              assertNotNull(manualBlocks.getJsonArray(FIELD_MANUALBLOCKS));
              assertEquals(0, manualBlocks.getJsonArray(FIELD_MANUALBLOCKS).size());
              assertEquals(0, manualBlocks.getInteger(FIELD_TOTAL_RECORDS));

              testContext.completeNow();
            })));
  }

  @Test
  void cannotGetManualBlocksByUserId(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock UsersRepository mockUsersRepository,
      @Mock Clock clock) {
    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.failedFuture(new NoStackTraceThrowable("Test failure")));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final FeeFinesRepository feeFinesRepository
        = new FeeFinesRepository(mockFolioProvider, mockUsersRepository, clock);
    feeFinesRepository.getManualBlocksByUserId(UUID.randomUUID().toString(),
        sessionData).onComplete(
            testContext.succeeding(manualBlocks -> testContext.verify(() -> {
              assertNull(manualBlocks);

              testContext.completeNow();
            })));
  }

  @Test
  void canGetManualBlocksByUserIdWithBlocksApplied(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock UsersRepository mockUsersRepository,
      @Mock Clock clock) {

    final String manualBlocksResponseJson = getJsonFromFile("json/manual_blocks_response.json");
    final JsonObject manualBlocksResponse = new JsonObject(manualBlocksResponseJson);

    final String userId = "a23eac4b-955e-451c-b4ff-6ec2f5e63e23";

    when(mockFolioProvider.retrieveResource(
        argThat(arg -> arg.getPath().endsWith(Utils.encode("userId==" + userId)))))
        .thenReturn(Future.succeededFuture(new FolioResource(manualBlocksResponse,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final FeeFinesRepository feeFinesRepository
        = new FeeFinesRepository(mockFolioProvider, mockUsersRepository, clock);
    feeFinesRepository.getManualBlocksByUserId(userId, sessionData).onComplete(
        testContext.succeeding(manualBlocks -> testContext.verify(() -> {
          assertNotNull(manualBlocks);
          assertEquals(1, manualBlocks.getInteger(FIELD_TOTAL_RECORDS));

          final JsonArray manualBlocksArray = manualBlocks.getJsonArray(FIELD_MANUALBLOCKS);
          assertNotNull(manualBlocksArray);
          assertEquals(1, manualBlocksArray.size());

          final JsonObject manualBlock = manualBlocksArray.getJsonObject(0);
          assertNotNull(manualBlock);
          assertEquals(userId, manualBlock.getString("userId"));
          assertEquals("Manual", manualBlock.getString("type"));
          assertEquals("test block", manualBlock.getString("desc"));
          assertEquals("Collect money", manualBlock.getString("staffInformation"));
          assertEquals("Pay your fines!", manualBlock.getString("patronMessage"));
          assertTrue(manualBlock.getBoolean("borrowing"));
          assertTrue(manualBlock.getBoolean("renewals"));
          assertTrue(manualBlock.getBoolean("requests"));

          testContext.completeNow();
        })));
  }

  @Test
  void canGetAccountByUserIdWithNoBlocksApplied(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock UsersRepository mockUsersRepository) {

    Clock clock = mock(Clock.class);

    final String accountResponseJson = getJsonFromFile("json/no_account_response.json");
    final JsonObject manualBlocksResponse = new JsonObject(accountResponseJson);

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(manualBlocksResponse,
          MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final FeeFinesRepository feeFinesRepository
        = new FeeFinesRepository(mockFolioProvider, mockUsersRepository, clock);
    feeFinesRepository.getAccountDataByUserId(UUID.randomUUID().toString(),
        sessionData).onComplete(
          testContext.succeeding(account -> testContext.verify(() -> {
            assertNotNull(account);
            assertNotNull(account.getJsonArray(FIELD_ACCOUNT));
            assertEquals(0, account.getJsonArray(FIELD_ACCOUNT).size());
            assertEquals(0, account.getInteger(FIELD_TOTAL_RECORDS));

            testContext.completeNow();
          })));
  }

  @Test
  void canAccountByUserIdWithBlocksApplied(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock UsersRepository mockUsersRepository,
      @Mock Clock clock) {

    final String accountResponseJson = getJsonFromFile("json/account_request_response.json");
    final JsonObject accountResponse = new JsonObject(accountResponseJson);

    final String userId = "2205005b-ca51-4a04-87fd-938eefa8f6de";

    when(mockFolioProvider.retrieveResource(
        argThat(arg -> arg.getPath().equals("/accounts?query=(userId==" + userId
          + ")&limit=1000"))))
        .thenReturn(Future.succeededFuture(new FolioResource(accountResponse,
          MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final FeeFinesRepository feeFinesRepository = new FeeFinesRepository(
        mockFolioProvider, mockUsersRepository, clock);
    feeFinesRepository.getAccountDataByUserId(userId, sessionData).onComplete(
        testContext.succeeding(account -> testContext.verify(() -> {
          assertNotNull(account);
          assertEquals(1, account.getInteger(FIELD_TOTAL_RECORDS));
          assertNotNull(account.getJsonArray(FIELD_ACCOUNT));
          assertEquals(1, account.getJsonArray(FIELD_ACCOUNT).size());
          testContext.completeNow();
        })));
  }

  @Test
  void canPerformFeePaidCommand(Vertx vertx,
      VertxTestContext testContext
  ) {

    UsersRepository mockUsersRepository
        = mock(UsersRepository.class, withSettings().verboseLogging());
    IResourceProvider<IRequestData> mockFolioProvider
        = mock(IResourceProvider.class, withSettings().verboseLogging());

    final Clock clock = TestUtils.getUtcFixedClock();
    final String patronIdentifier = "1029384756";
    final String feeIdentifier = "c78489bd-4d1b-4e4f-87d3-caa915946aa4";
    final String transactionId = "7e15ba2d-cc85-4226-963d-d6c7d5c03f26";
    final String accountId = "4bf0339e-8d4c-46ff-92c2-8a8f8735c30b";
    final String userId = "62628aed-f753-462c-88ca-3def9f870e7a";
    final SessionData sessionData = TestUtils.getMockedSessionData();
    final String feeAmount = "20.43";
    final JsonObject queryAccountResponse = new JsonObject()
        .put("accounts", new JsonArray()
        .add(new JsonObject()
          .put("remaining", 20.43)
          .put("id", accountId)
        )
      );
    final JsonObject accountPayResponse = new JsonObject()
        .put("accountId", accountId)
        .put("amount", feeAmount)
        .put("remainingAmount", "0");

    final FeePaid feePaid = FeePaid.builder()
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .transactionId(transactionId)
        .feeAmount(feeAmount)
        .feeIdentifier(feeIdentifier)
        .build();

    final User user = new User.Builder().id(userId).build();

    final ExtendedUser extendedUser = new ExtendedUser();
    extendedUser.setUser(user);

    when(mockUsersRepository.getUserById(anyString(), any()))
        .thenReturn(Future.succeededFuture(extendedUser));

    when(mockFolioProvider.retrieveResource(
        argThat(arg -> arg.getPath()
          .endsWith(Utils.encode("userId==" + userId + "  and status.name==Open)")))))
        .thenReturn(Future.succeededFuture(new FolioResource(queryAccountResponse,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    when(mockFolioProvider.createResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(accountPayResponse,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final FeeFinesRepository feeFinesRepository = new FeeFinesRepository(
        mockFolioProvider, mockUsersRepository, clock);

    feeFinesRepository.performFeePaidCommand(feePaid, sessionData).onComplete(
        testContext.succeeding(feePaidResponse -> testContext.verify(() -> {
          assertNotNull(feePaidResponse);
          assertTrue(feePaidResponse.getPaymentAccepted());
          assertNull(feePaidResponse.getScreenMessage());
          assertEquals(transactionId, feePaidResponse.getTransactionId());
          testContext.completeNow();
        }))
    );

  }

  @Test
  void canGetFeeAmountByUserId(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock UsersRepository mockUsersRepository) {

    final Clock clock = TestUtils.getUtcFixedClock();
    final String userId = "658d7aa7-0dce-4428-a1d0-fd287bbc8476";
    final String accountId = "f6174778-e537-40ac-9389-eef982b4c179";
    final JsonObject queryAccountResponse = new JsonObject()
        .put("accounts", new JsonArray()
        .add(new JsonObject()
          .put("remaining", 20.43)
          .put("id", accountId)
        )
      );

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(queryAccountResponse,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));
    final FeeFinesRepository feeFinesRepository = new FeeFinesRepository(
        mockFolioProvider, mockUsersRepository, clock);
    final SessionData sessionData = TestUtils.getMockedSessionData();
    feeFinesRepository.getFeeAmountByUserId(userId, sessionData).onComplete(
        testContext.succeeding(feeAmount -> testContext.verify(() -> {
          testContext.completeNow();
        }))
    );
  }

  @Test
  void cannotPerformFeePaidCommandWithOverpay(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider,
      @Mock UsersRepository mockUsersRepository
  ) {
    final Clock clock = TestUtils.getUtcFixedClock();
    final String patronIdentifier = "1029384756";
    final String feeIdentifier = "c78489bd-4d1b-4e4f-87d3-caa915946aa4";
    final String transactionId = "7e15ba2d-cc85-4226-963d-d6c7d5c03f26";
    final String accountId = "4bf0339e-8d4c-46ff-92c2-8a8f8735c30b";
    final String userId = "62628aed-f753-462c-88ca-3def9f870e7a";
    final SessionData sessionData = TestUtils.getMockedSessionData();
    final String feeAmount = "35.00";
    final JsonObject queryAccountResponse = new JsonObject()
        .put("accounts", new JsonArray()
        .add(new JsonObject()
          .put("remaining", 20.43)
          .put("id", accountId)
        )
      );

    final FeePaid feePaid = FeePaid.builder()
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .transactionId(transactionId)
        .feeAmount(feeAmount)
        .feeIdentifier(feeIdentifier)
        .build();

    final User user = new User.Builder().id(userId).build();
    final ExtendedUser extendedUser = new ExtendedUser();
    extendedUser.setUser(user);

    when(mockUsersRepository.getUserById(anyString(), any()))
        .thenReturn(Future.succeededFuture(extendedUser));

    when(mockFolioProvider.retrieveResource(
        argThat(arg -> arg.getPath()
            .endsWith(Utils.encode("userId==" + userId + "  and status.name==Open)")))))
        .thenReturn(Future.succeededFuture(new FolioResource(queryAccountResponse,
        MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));


    final FeeFinesRepository feeFinesRepository = new FeeFinesRepository(
        mockFolioProvider, mockUsersRepository, clock);

    feeFinesRepository.performFeePaidCommand(feePaid, sessionData).onComplete(
        testContext.succeeding(feePaidResponse -> testContext.verify(() -> {
          assertNotNull(feePaidResponse);
          assertFalse(feePaidResponse.getPaymentAccepted());
          testContext.completeNow();
        }))
    );
  }
}
