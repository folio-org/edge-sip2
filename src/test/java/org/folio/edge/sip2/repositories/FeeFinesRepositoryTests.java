package org.folio.edge.sip2.repositories;

import static org.folio.edge.sip2.api.support.TestUtils.getJsonFromFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.impl.NoStackTraceThrowable;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.UUID;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
public class FeeFinesRepositoryTests {
  private static final String FIELD_TOTAL_RECORDS = "totalRecords";
  private static final String FIELD_MANUALBLOCKS = "manualblocks";
  private static final String FIELD_ACCOUNT = "accounts";

  @Test
  public void canCreateFeeFinesRepository(
      @Mock IResourceProvider<IRequestData> mockFolioResource) {
    final FeeFinesRepository usersRepository =
        new FeeFinesRepository(mockFolioResource);

    assertNotNull(usersRepository);
  }

  @Test
  public void cannotCreateFeeFinesRepositoryWhenResourceProviderIsNull() {
    final NullPointerException thrown = assertThrows(
        NullPointerException.class,
        () -> new FeeFinesRepository(null));

    assertEquals("Resource provider cannot be null", thrown.getMessage());
  }

  @Test
  public void canGetManualBlocksByUserIdWithNoBlocksApplied(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {

    final String manualBlocksResponseJson = getJsonFromFile("json/no_manual_blocks_response.json");
    final JsonObject manualBlocksResponse = new JsonObject(manualBlocksResponseJson);

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(manualBlocksResponse,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final FeeFinesRepository feeFinesRepository = new FeeFinesRepository(mockFolioProvider);
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
  public void cannotGetManualBlocksByUserId(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {
    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.failedFuture(new NoStackTraceThrowable("Test failure")));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final FeeFinesRepository feeFinesRepository = new FeeFinesRepository(mockFolioProvider);
    feeFinesRepository.getManualBlocksByUserId(UUID.randomUUID().toString(),
        sessionData).onComplete(
            testContext.succeeding(manualBlocks -> testContext.verify(() -> {
              assertNull(manualBlocks);

              testContext.completeNow();
            })));
  }

  @Test
  public void canGetManualBlocksByUserIdWithBlocksApplied(Vertx vertx,
      VertxTestContext testContext,
      @Mock IResourceProvider<IRequestData> mockFolioProvider) {

    final String manualBlocksResponseJson = getJsonFromFile("json/manual_blocks_response.json");
    final JsonObject manualBlocksResponse = new JsonObject(manualBlocksResponseJson);

    final String userId = "a23eac4b-955e-451c-b4ff-6ec2f5e63e23";

    when(mockFolioProvider.retrieveResource(
        argThat(arg -> arg.getPath().endsWith(Utils.encode("userId==" + userId)))))
        .thenReturn(Future.succeededFuture(new FolioResource(manualBlocksResponse,
            MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final FeeFinesRepository feeFinesRepository = new FeeFinesRepository(mockFolioProvider);
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
  public void canGetAccountByUserIdWithNoBlocksApplied(Vertx vertx,
                                                            VertxTestContext testContext,
                              @Mock IResourceProvider<IRequestData> mockFolioProvider) {

    final String accountResponseJson = getJsonFromFile("json/no_account_response.json");
    final JsonObject manualBlocksResponse = new JsonObject(accountResponseJson);

    when(mockFolioProvider.retrieveResource(any()))
        .thenReturn(Future.succeededFuture(new FolioResource(manualBlocksResponse,
          MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final FeeFinesRepository feeFinesRepository = new FeeFinesRepository(mockFolioProvider);
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
  public void canAccountByUserIdWithBlocksApplied(Vertx vertx,
                                                          VertxTestContext testContext,
                            @Mock IResourceProvider<IRequestData> mockFolioProvider) {

    final String accountResponseJson = getJsonFromFile("json/account_request_response.json");
    final JsonObject accountResponse = new JsonObject(accountResponseJson);

    final String userId = "2205005b-ca51-4a04-87fd-938eefa8f6de";

    when(mockFolioProvider.retrieveResource(
        argThat(arg -> arg.getPath().equals("/accounts?query=(userId==" + userId
          + ")&limit=1000"))))
        .thenReturn(Future.succeededFuture(new FolioResource(accountResponse,
          MultiMap.caseInsensitiveMultiMap().add("x-okapi-token", "1234"))));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final FeeFinesRepository feeFinesRepository = new FeeFinesRepository(mockFolioProvider);
    feeFinesRepository.getAccountDataByUserId(userId, sessionData).onComplete(
        testContext.succeeding(account -> testContext.verify(() -> {
          assertNotNull(account);
          assertEquals(1, account.getInteger(FIELD_TOTAL_RECORDS));
          assertNotNull(account.getJsonArray(FIELD_ACCOUNT));
          assertEquals(1, account.getJsonArray(FIELD_ACCOUNT).size());
          testContext.completeNow();
        })));
  }
}
