package org.folio.edge.sip2.repositories;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.folio.edge.sip2.api.support.TestUtils.getJsonFromFile;
import static org.folio.edge.sip2.domain.messages.enumerations.Language.ENGLISH;
import static org.folio.edge.sip2.domain.messages.enumerations.Language.UNKNOWN;
import static org.folio.edge.sip2.domain.messages.enumerations.PatronStatus.HOLD_PRIVILEGES_DENIED;
import static org.folio.edge.sip2.domain.messages.enumerations.PatronStatus.RECALL_PRIVILEGES_DENIED;
import static org.folio.edge.sip2.domain.messages.enumerations.PatronStatus.RENEWAL_PRIVILEGES_DENIED;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.RECALL_ITEMS;
import static org.folio.edge.sip2.repositories.PatronRepository.MESSAGE_BLOCKED_PATRON;
import static org.folio.edge.sip2.repositories.PatronRepository.MESSAGE_INVALID_PATRON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.folio.edge.sip2.api.support.TestUtils;
import org.folio.edge.sip2.domain.messages.enumerations.PatronStatus;
import org.folio.edge.sip2.domain.messages.enumerations.Summary;
import org.folio.edge.sip2.domain.messages.requests.EndPatronSession;
import org.folio.edge.sip2.domain.messages.requests.PatronInformation;
import org.folio.edge.sip2.domain.messages.requests.PatronStatusRequest;
import org.folio.edge.sip2.repositories.domain.ExtendedUser;
import org.folio.edge.sip2.repositories.domain.PatronPasswordVerificationRecords;
import org.folio.edge.sip2.repositories.domain.Personal;
import org.folio.edge.sip2.repositories.domain.User;
import org.folio.edge.sip2.session.SessionData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({VertxExtension.class, MockitoExtension.class})
public class PatronRepositoryTests {
  @Test
  public void canCreatePatronRepository(
      @Mock UsersRepository mockUsersRepository,
      @Mock CirculationRepository mockCirculationRepository,
      @Mock FeeFinesRepository mockFeeFinesRepository,
      @Mock PasswordVerifier mockPasswordVerifier,
      @Mock Clock clock) {
    final PatronRepository patronRepository = new PatronRepository(mockUsersRepository,
        mockCirculationRepository, mockFeeFinesRepository, mockPasswordVerifier, clock);

    assertNotNull(patronRepository);
  }

  @Test
  public void cannotCreatePatronRepositoryCirculationRepositoryIsNull(
      @Mock UsersRepository mockUsersRepository,
      @Mock Clock mockClock) {
    final NullPointerException thrown = assertThrows(
        NullPointerException.class,
        () -> new PatronRepository(mockUsersRepository, null, null, null, mockClock));

    assertEquals("Circulation repository cannot be null", thrown.getMessage());
  }

  @Test
  public void cannotCreatePatronRepositoryFeeFinesRepositoryIsNull(
      @Mock UsersRepository mockUsersRepository,
      @Mock CirculationRepository mockCirculationRepository,
      @Mock Clock mockClock) {
    final NullPointerException thrown = assertThrows(
        NullPointerException.class,
        () -> new PatronRepository(mockUsersRepository, mockCirculationRepository, null, null,
            mockClock));

    assertEquals("FeeFines repository cannot be null", thrown.getMessage());
  }

  @Test
  public void cannotCreatePatronRepositoryPasswordVerifierIsNull(
      @Mock UsersRepository mockUsersRepository,
      @Mock CirculationRepository mockCirculationRepository,
      @Mock FeeFinesRepository mockFeeFinesRepository,
      @Mock Clock mockClock) {
    final NullPointerException thrown = assertThrows(
        NullPointerException.class,
        () -> new PatronRepository(mockUsersRepository, mockCirculationRepository,
            mockFeeFinesRepository, null, mockClock));

    assertEquals("Password verifier cannot be null", thrown.getMessage());
  }

  @Test
  public void cannotCreatePatronRepositoryWhenClockIsNull(
      @Mock UsersRepository mockUsersRepository,
      @Mock CirculationRepository mockCirculationRepository,
      @Mock FeeFinesRepository mockFeeFinesRepository,
      @Mock PasswordVerifier mockPasswordVerifier) {
    final NullPointerException thrown = assertThrows(
        NullPointerException.class,
        () -> new PatronRepository(mockUsersRepository, mockCirculationRepository,
            mockFeeFinesRepository, mockPasswordVerifier, null));

    assertEquals("Clock cannot be null", thrown.getMessage());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void canPatronInformation(Vertx vertx,
      VertxTestContext testContext,
      @Mock UsersRepository mockUsersRepository,
      @Mock CirculationRepository mockCirculationRepository,
      @Mock FeeFinesRepository mockFeeFinesRepository,
      @Mock PasswordVerifier mockPasswordVerifier) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String patronIdentifier = "1234567890";
    final PatronInformation patronInformation = PatronInformation.builder()
        .language(ENGLISH)
        .transactionDate(OffsetDateTime.now())
        .summary(RECALL_ITEMS)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .terminalPassword("1234")
        .patronPassword("0989")
        .build();

    final String userResponseJson = getJsonFromFile("json/user_response.json");
    final User userResponse = Json.decodeValue(userResponseJson, User.class);
    final String manualBlocksResponseJson = getJsonFromFile("json/no_manual_blocks_response.json");
    final JsonObject manualBlocksResponse = new JsonObject(manualBlocksResponseJson);
    final String overdueResponseJson = getJsonFromFile("json/overdue_response.json");
    final JsonObject overdueResponse = new JsonObject(overdueResponseJson);
    final String holdsResponseJson = getJsonFromFile("json/holds_requests_response.json");
    final JsonObject holdsResponse = new JsonObject(holdsResponseJson);
    final String openLoansResponseJson = getJsonFromFile("json/open_loans_response.json");
    final JsonObject openLoansResponse = new JsonObject(openLoansResponseJson);
    final String recallsResponseJson = getJsonFromFile("json/recall_requests_response.json");
    final JsonObject recallsResponse = new JsonObject(recallsResponseJson);

    final String accountResponseJson = getJsonFromFile("json/account_request_response.json");
    final JsonObject accountResponse = new JsonObject(accountResponseJson);

    final ExtendedUser extendedUser = new ExtendedUser();
    extendedUser.setUser(userResponse);
    extendedUser.setPatronGroup("patrons","The Library Patrons", "12335");


    when(mockFeeFinesRepository.getManualBlocksByUserId(any(), any()))
        .thenReturn(Future.succeededFuture(manualBlocksResponse));
    when(mockFeeFinesRepository.getAccountDataByUserId(any(), any()))
        .thenReturn(Future.succeededFuture(accountResponse));
    when(mockCirculationRepository.getOverdueLoansByUserId(any(), any(), any(), any(), any()))
        .thenReturn(Future.succeededFuture(overdueResponse));
    when(mockCirculationRepository.getRequestsByUserId(
        any(), eq("Hold"), any(), any(), any()))
        .thenReturn(Future.succeededFuture(holdsResponse));
    when(mockCirculationRepository.getLoansByUserId(any(), any(), any(), any()))
        .thenReturn(Future.succeededFuture(openLoansResponse));
    when(mockCirculationRepository.getRequestsByItemId(
        any(), eq("Recall"), any(), any(), any()))
        .thenReturn(Future.succeededFuture(recallsResponse),
            Future.succeededFuture(new JsonObject().put("requests", new JsonArray())),
            Future.succeededFuture(new JsonObject().put("requests", new JsonArray())));
    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("0989"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords
        .builder().extendedUser(extendedUser).build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final PatronRepository patronRepository = new PatronRepository(mockUsersRepository,
        mockCirculationRepository, mockFeeFinesRepository, mockPasswordVerifier, clock);
    patronRepository.performPatronInformationCommand(patronInformation, sessionData).onComplete(
        testContext.succeeding(patronInformationResponse -> testContext.verify(() -> {
          assertNotNull(patronInformationResponse);
          assertNotNull(patronInformationResponse.getPatronStatus());
          assertTrue(patronInformationResponse.getPatronStatus().isEmpty());
          assertEquals(ENGLISH, patronInformationResponse.getLanguage());
          assertEquals(OffsetDateTime.now(clock), patronInformationResponse.getTransactionDate());
          assertEquals(2, patronInformationResponse.getHoldItemsCount());
          assertEquals(1, patronInformationResponse.getOverdueItemsCount());
          assertEquals(3,patronInformationResponse.getChargedItemsCount());
          assertEquals(1,patronInformationResponse.getFineItemsCount());
          assertEquals(1, patronInformationResponse.getRecallItemsCount());
          assertNull(patronInformationResponse.getUnavailableHoldsCount());
          assertEquals("diku", patronInformationResponse.getInstitutionId());
          assertEquals(patronIdentifier, patronInformationResponse.getPatronIdentifier());
          assertEquals("Darius Auer", patronInformationResponse.getPersonalName());
          assertNull(patronInformationResponse.getHoldItemsLimit());
          assertNull(patronInformationResponse.getOverdueItemsLimit());
          assertNull(patronInformationResponse.getChargedItemsLimit());
          assertTrue(patronInformationResponse.getValidPatron());
          assertNull(patronInformationResponse.getValidPatronPassword());
          assertNull(patronInformationResponse.getCurrencyType());
          assertNull(patronInformationResponse.getFeeAmount());
          assertNull(patronInformationResponse.getFeeLimit());
          assertNotNull(patronInformationResponse.getHoldItems());
          assertTrue(patronInformationResponse.getHoldItems().isEmpty());
          assertNotNull(patronInformationResponse.getOverdueItems());
          assertTrue(patronInformationResponse.getOverdueItems().isEmpty());
          assertNotNull(patronInformationResponse.getChargedItems());
          assertTrue(patronInformationResponse.getChargedItems().isEmpty());
          assertNotNull(patronInformationResponse.getFineItems());
          assertTrue(patronInformationResponse.getFineItems().isEmpty());
          assertNotNull(patronInformationResponse.getRecallItems());
          assertEquals(Arrays.asList("1990 to 2010"), patronInformationResponse.getRecallItems());
          assertNotNull(patronInformationResponse.getUnavailableHoldItems());
          assertTrue(patronInformationResponse.getUnavailableHoldItems().isEmpty());
          assertEquals("00430 Denis Parks, Indianapolis, FL 14654-6001 US",
              patronInformationResponse.getHomeAddress());
          assertEquals("earnestine@sipes-stokes-and-durgan.so",
              patronInformationResponse.getEmailAddress());
          assertEquals("(916)599-0326",
              patronInformationResponse.getHomePhoneNumber());
          assertNull(patronInformationResponse.getScreenMessage());
          assertNull(patronInformationResponse.getPrintLine());

          testContext.completeNow();
        })));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void canPerformPatronInformationWithNoUserName(VertxTestContext testContext,
                                            @Mock UsersRepository mockUsersRepository,
                                            @Mock CirculationRepository mockCirculationRepository,
                                            @Mock FeeFinesRepository mockFeeFinesRepository,
                                            @Mock PasswordVerifier mockPasswordVerifier) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String patronIdentifier = "1234567890";
    final PatronInformation patronInformation = PatronInformation.builder()
        .language(ENGLISH)
        .transactionDate(OffsetDateTime.now())
        .summary(RECALL_ITEMS)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .terminalPassword("1234")
        .patronPassword("0989")
        .build();

    final String userResponseJson = getJsonFromFile("json/user_response_wo_personal_names.json");
    final User userResponse = Json.decodeValue(userResponseJson, User.class);
    final String manualBlocksResponseJson = getJsonFromFile("json/no_manual_blocks_response.json");
    final JsonObject manualBlocksResponse = new JsonObject(manualBlocksResponseJson);
    final String overdueResponseJson = getJsonFromFile("json/overdue_response.json");
    final JsonObject overdueResponse = new JsonObject(overdueResponseJson);
    final String holdsResponseJson = getJsonFromFile("json/holds_requests_response.json");
    final JsonObject holdsResponse = new JsonObject(holdsResponseJson);
    final String openLoansResponseJson = getJsonFromFile("json/open_loans_response.json");
    final JsonObject openLoansResponse = new JsonObject(openLoansResponseJson);
    final String recallsResponseJson = getJsonFromFile("json/recall_requests_response.json");
    final JsonObject recallsResponse = new JsonObject(recallsResponseJson);
    final String accountResponseJson = getJsonFromFile("json/account_request_response.json");
    final JsonObject accountResponse = new JsonObject(accountResponseJson);
    final ExtendedUser extendedUser = new ExtendedUser();
    extendedUser.setUser(userResponse);
    extendedUser.setPatronGroup("patrons","The Library Patrons", "12335");

    when(mockFeeFinesRepository.getManualBlocksByUserId(any(), any()))
        .thenReturn(Future.succeededFuture(manualBlocksResponse));
    when(mockFeeFinesRepository.getAccountDataByUserId(any(), any()))
        .thenReturn(Future.succeededFuture(accountResponse));
    when(mockCirculationRepository.getOverdueLoansByUserId(any(), any(), any(), any(), any()))
        .thenReturn(Future.succeededFuture(overdueResponse));
    when(mockCirculationRepository.getRequestsByUserId(
      any(), eq("Hold"), any(), any(), any()))
        .thenReturn(Future.succeededFuture(holdsResponse));
    when(mockCirculationRepository.getLoansByUserId(any(), any(), any(), any()))
        .thenReturn(Future.succeededFuture(openLoansResponse));
    when(mockCirculationRepository.getRequestsByItemId(
        any(), eq("Recall"), any(), any(), any()))
          .thenReturn(Future.succeededFuture(recallsResponse),
            Future.succeededFuture(new JsonObject().put("requests", new JsonArray())),
            Future.succeededFuture(new JsonObject().put("requests", new JsonArray())));
    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("0989"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder()
        .extendedUser(extendedUser).build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final PatronRepository patronRepository = new PatronRepository(mockUsersRepository,
        mockCirculationRepository, mockFeeFinesRepository, mockPasswordVerifier, clock);
    patronRepository.performPatronInformationCommand(patronInformation, sessionData).onComplete(
        testContext.succeeding(patronInformationResponse -> testContext.verify(() -> {
          assertNotNull(patronInformationResponse);
          assertNotNull(patronInformationResponse.getPatronStatus());
          assertTrue(patronInformationResponse.getPatronStatus().isEmpty());
          assertEquals(ENGLISH, patronInformationResponse.getLanguage());
          assertEquals(OffsetDateTime.now(clock), patronInformationResponse.getTransactionDate());
          assertEquals(2, patronInformationResponse.getHoldItemsCount());
          assertEquals(1, patronInformationResponse.getOverdueItemsCount());
          assertEquals(3,patronInformationResponse.getChargedItemsCount());
          assertEquals(1,patronInformationResponse.getFineItemsCount());
          assertNotNull(patronInformationResponse.getChargedItemsCount());
          assertNotNull(patronInformationResponse.getFineItemsCount());
          assertEquals(1, patronInformationResponse.getRecallItemsCount());
          assertNull(patronInformationResponse.getUnavailableHoldsCount());
          assertEquals("diku", patronInformationResponse.getInstitutionId());
          assertEquals(patronIdentifier, patronInformationResponse.getPatronIdentifier());
          assertEquals(patronIdentifier, patronInformationResponse.getPersonalName());
          assertNull(patronInformationResponse.getHoldItemsLimit());
          assertNull(patronInformationResponse.getOverdueItemsLimit());
          assertNull(patronInformationResponse.getChargedItemsLimit());
          assertTrue(patronInformationResponse.getValidPatron());
          assertNull(patronInformationResponse.getValidPatronPassword());
          assertNull(patronInformationResponse.getCurrencyType());
          assertNull(patronInformationResponse.getFeeAmount());
          assertNull(patronInformationResponse.getFeeLimit());
          assertNotNull(patronInformationResponse.getHoldItems());
          assertTrue(patronInformationResponse.getHoldItems().isEmpty());
          assertNotNull(patronInformationResponse.getOverdueItems());
          assertTrue(patronInformationResponse.getOverdueItems().isEmpty());
          assertNotNull(patronInformationResponse.getChargedItems());
          assertTrue(patronInformationResponse.getChargedItems().isEmpty());
          assertNotNull(patronInformationResponse.getFineItems());
          assertTrue(patronInformationResponse.getFineItems().isEmpty());
          assertNotNull(patronInformationResponse.getRecallItems());
          assertEquals(Arrays.asList("1990 to 2010"), patronInformationResponse.getRecallItems());
          assertNotNull(patronInformationResponse.getUnavailableHoldItems());
          assertTrue(patronInformationResponse.getUnavailableHoldItems().isEmpty());
          assertEquals("00430 Denis Parks, Indianapolis, FL 14654-6001 US",
              patronInformationResponse.getHomeAddress());
          assertEquals("earnestine@sipes-stokes-and-durgan.so",
              patronInformationResponse.getEmailAddress());
          assertEquals("(916)599-0326",
              patronInformationResponse.getHomePhoneNumber());
          assertNull(patronInformationResponse.getScreenMessage());
          assertNull(patronInformationResponse.getPrintLine());

          testContext.completeNow();
        })));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void canPerformPatronInformationWithNoPersonalInfo(VertxTestContext testContext,
                                        @Mock UsersRepository mockUsersRepository,
                                        @Mock CirculationRepository mockCirculationRepository,
                                        @Mock FeeFinesRepository mockFeeFinesRepository,
                                        @Mock PasswordVerifier mockPasswordVerifier) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String patronIdentifier = "1234567890";
    final PatronInformation patronInformation = PatronInformation.builder()
        .language(ENGLISH)
        .transactionDate(OffsetDateTime.now())
        .summary(RECALL_ITEMS)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .terminalPassword("1234")
        .patronPassword("0989")
        .build();

    final String userResponseJson = getJsonFromFile("json/user_response_with_no_personal.json");
    final User userResponse = Json.decodeValue(userResponseJson, User.class);
    final String manualBlocksResponseJson = getJsonFromFile("json/no_manual_blocks_response.json");
    final JsonObject manualBlocksResponse = new JsonObject(manualBlocksResponseJson);
    final String overdueResponseJson = getJsonFromFile("json/overdue_response.json");
    final JsonObject overdueResponse = new JsonObject(overdueResponseJson);
    final String holdsResponseJson = getJsonFromFile("json/holds_requests_response.json");
    final JsonObject holdsResponse = new JsonObject(holdsResponseJson);
    final String openLoansResponseJson = getJsonFromFile("json/open_loans_response.json");
    final JsonObject openLoansResponse = new JsonObject(openLoansResponseJson);
    final String recallsResponseJson = getJsonFromFile("json/recall_requests_response.json");
    final JsonObject recallsResponse = new JsonObject(recallsResponseJson);
    final String accountResponseJson = getJsonFromFile("json/account_request_response.json");
    final JsonObject accountResponse = new JsonObject(accountResponseJson);
    final ExtendedUser extendedUser = new ExtendedUser();
    extendedUser.setUser(userResponse);
    extendedUser.setPatronGroup("patrons","The Library Patrons", "12335");

    when(mockFeeFinesRepository.getManualBlocksByUserId(any(), any()))
        .thenReturn(Future.succeededFuture(manualBlocksResponse));
    when(mockFeeFinesRepository.getAccountDataByUserId(any(), any()))
        .thenReturn(Future.succeededFuture(accountResponse));
    when(mockCirculationRepository.getOverdueLoansByUserId(any(), any(), any(), any(), any()))
        .thenReturn(Future.succeededFuture(overdueResponse));
    when(mockCirculationRepository.getRequestsByUserId(
      any(), eq("Hold"), any(), any(), any()))
        .thenReturn(Future.succeededFuture(holdsResponse));
    when(mockCirculationRepository.getLoansByUserId(any(), any(), any(), any()))
        .thenReturn(Future.succeededFuture(openLoansResponse));
    when(mockCirculationRepository.getRequestsByItemId(
        any(), eq("Recall"), any(), any(), any()))
          .thenReturn(Future.succeededFuture(recallsResponse),
            Future.succeededFuture(new JsonObject().put("requests", new JsonArray())),
            Future.succeededFuture(new JsonObject().put("requests", new JsonArray())));
    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("0989"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder()
        .extendedUser(extendedUser).build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final PatronRepository patronRepository = new PatronRepository(mockUsersRepository,
        mockCirculationRepository, mockFeeFinesRepository, mockPasswordVerifier, clock);
    patronRepository.performPatronInformationCommand(patronInformation, sessionData).onComplete(
        testContext.succeeding(patronInformationResponse -> testContext.verify(() -> {
          assertNotNull(patronInformationResponse);
          assertNotNull(patronInformationResponse.getPatronStatus());
          assertTrue(patronInformationResponse.getPatronStatus().isEmpty());
          assertEquals(ENGLISH, patronInformationResponse.getLanguage());
          assertEquals(OffsetDateTime.now(clock), patronInformationResponse.getTransactionDate());
          assertEquals(2, patronInformationResponse.getHoldItemsCount());
          assertEquals(1, patronInformationResponse.getOverdueItemsCount());
          assertEquals(3,patronInformationResponse.getChargedItemsCount());
          assertEquals(1,patronInformationResponse.getFineItemsCount());
          assertNotNull(patronInformationResponse.getChargedItemsCount());
          assertNotNull(patronInformationResponse.getFineItemsCount());
          assertEquals(1, patronInformationResponse.getRecallItemsCount());
          assertNull(patronInformationResponse.getUnavailableHoldsCount());
          assertEquals("diku", patronInformationResponse.getInstitutionId());
          assertEquals(patronIdentifier, patronInformationResponse.getPatronIdentifier());
          assertEquals(patronIdentifier, patronInformationResponse.getPersonalName());
          assertNull(patronInformationResponse.getHoldItemsLimit());
          assertNull(patronInformationResponse.getOverdueItemsLimit());
          assertNull(patronInformationResponse.getChargedItemsLimit());
          assertTrue(patronInformationResponse.getValidPatron());
          assertNull(patronInformationResponse.getValidPatronPassword());
          assertNull(patronInformationResponse.getCurrencyType());
          assertNull(patronInformationResponse.getFeeAmount());
          assertNull(patronInformationResponse.getFeeLimit());
          assertNotNull(patronInformationResponse.getHoldItems());
          assertTrue(patronInformationResponse.getHoldItems().isEmpty());
          assertNotNull(patronInformationResponse.getOverdueItems());
          assertTrue(patronInformationResponse.getOverdueItems().isEmpty());
          assertNotNull(patronInformationResponse.getChargedItems());
          assertTrue(patronInformationResponse.getChargedItems().isEmpty());
          assertNotNull(patronInformationResponse.getFineItems());
          assertTrue(patronInformationResponse.getFineItems().isEmpty());
          assertNotNull(patronInformationResponse.getRecallItems());
          assertEquals(Arrays.asList("1990 to 2010"), patronInformationResponse.getRecallItems());
          assertNotNull(patronInformationResponse.getUnavailableHoldItems());
          assertTrue(patronInformationResponse.getUnavailableHoldItems().isEmpty());
          assertEquals(null, patronInformationResponse.getHomeAddress());
          assertEquals(null, patronInformationResponse.getEmailAddress());
          assertEquals(null, patronInformationResponse.getHomePhoneNumber());
          assertNull(patronInformationResponse.getScreenMessage());
          assertNull(patronInformationResponse.getPrintLine());

          testContext.completeNow();
        })));
  }

  @SuppressWarnings("unchecked")
  @Test
  void canPatronInformationByExternalSystemIdAndPasswordRequired(Vertx vertx,
      VertxTestContext testContext,
      @Mock UsersRepository mockUsersRepository,
      @Mock CirculationRepository mockCirculationRepository,
      @Mock FeeFinesRepository mockFeeFinesRepository,
      @Mock PasswordVerifier mockPasswordVerifier) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String patronIdentifier = "abc123";
    final PatronInformation patronInformation = PatronInformation.builder()
        .language(ENGLISH)
        .transactionDate(OffsetDateTime.now())
        .summary(RECALL_ITEMS)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .terminalPassword("1234")
        .patronPassword("0989")
        .build();

    final String userResponseJson = getJsonFromFile("json/user_response.json");
    final User userResponse = Json.decodeValue(userResponseJson, User.class);
    final String manualBlocksResponseJson = getJsonFromFile("json/no_manual_blocks_response.json");
    final JsonObject manualBlocksResponse = new JsonObject(manualBlocksResponseJson);
    final String overdueResponseJson = getJsonFromFile("json/overdue_response.json");
    final JsonObject overdueResponse = new JsonObject(overdueResponseJson);
    final String holdsResponseJson = getJsonFromFile("json/holds_requests_response.json");
    final JsonObject holdsResponse = new JsonObject(holdsResponseJson);
    final String openLoansResponseJson = getJsonFromFile("json/open_loans_response.json");
    final JsonObject openLoansResponse = new JsonObject(openLoansResponseJson);
    final String recallsResponseJson = getJsonFromFile("json/recall_requests_response.json");
    final JsonObject recallsResponse = new JsonObject(recallsResponseJson);
    final String accountResponseJson = getJsonFromFile("json/account_request_response.json");
    final JsonObject accountResponse = new JsonObject(accountResponseJson);
    final ExtendedUser extendedUser = new ExtendedUser();
    extendedUser.setUser(userResponse);
    extendedUser.setPatronGroup("patrons","The Library Patrons", "12335");

    when(mockFeeFinesRepository.getManualBlocksByUserId(any(), any()))
        .thenReturn(Future.succeededFuture(manualBlocksResponse));
    when(mockFeeFinesRepository.getAccountDataByUserId(any(), any()))
        .thenReturn(Future.succeededFuture(accountResponse));
    when(mockCirculationRepository.getOverdueLoansByUserId(any(), any(), any(), any(), any()))
        .thenReturn(Future.succeededFuture(overdueResponse));
    when(mockCirculationRepository.getRequestsByUserId(
        any(), eq("Hold"), any(), any(), any()))
        .thenReturn(Future.succeededFuture(holdsResponse));
    when(mockCirculationRepository.getLoansByUserId(any(), any(), any(), any()))
        .thenReturn(Future.succeededFuture(openLoansResponse));
    when(mockCirculationRepository.getRequestsByItemId(
        any(), eq("Recall"), any(), any(), any()))
        .thenReturn(Future.succeededFuture(recallsResponse),
            Future.succeededFuture(new JsonObject().put("requests", new JsonArray())),
            Future.succeededFuture(new JsonObject().put("requests", new JsonArray())));
    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("0989"), any()))
        .thenReturn(Future.succeededFuture(
            PatronPasswordVerificationRecords.builder()
              .passwordVerified(TRUE)
              .extendedUser(extendedUser)
              .build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();
    sessionData.setPatronPasswordVerificationRequired(true);

    final PatronRepository patronRepository = new PatronRepository(mockUsersRepository,
        mockCirculationRepository, mockFeeFinesRepository, mockPasswordVerifier, clock);
    patronRepository.performPatronInformationCommand(patronInformation, sessionData).onComplete(
        testContext.succeeding(patronInformationResponse -> testContext.verify(() -> {
          assertNotNull(patronInformationResponse);
          assertNotNull(patronInformationResponse.getPatronStatus());
          assertTrue(patronInformationResponse.getPatronStatus().isEmpty());
          assertEquals(ENGLISH, patronInformationResponse.getLanguage());
          assertEquals(OffsetDateTime.now(clock), patronInformationResponse.getTransactionDate());
          assertEquals(2, patronInformationResponse.getHoldItemsCount());
          assertEquals(1, patronInformationResponse.getOverdueItemsCount());
          assertEquals(3,patronInformationResponse.getChargedItemsCount());
          assertEquals(1,patronInformationResponse.getFineItemsCount());
          assertNotNull(patronInformationResponse.getChargedItemsCount());
          assertNotNull(patronInformationResponse.getFineItemsCount());
          assertEquals(1, patronInformationResponse.getRecallItemsCount());
          assertNull(patronInformationResponse.getUnavailableHoldsCount());
          assertEquals("diku", patronInformationResponse.getInstitutionId());
          assertEquals(patronIdentifier, patronInformationResponse.getPatronIdentifier());
          assertEquals("Darius Auer", patronInformationResponse.getPersonalName());
          assertNull(patronInformationResponse.getHoldItemsLimit());
          assertNull(patronInformationResponse.getOverdueItemsLimit());
          assertNull(patronInformationResponse.getChargedItemsLimit());
          assertTrue(patronInformationResponse.getValidPatron());
          assertTrue(patronInformationResponse.getValidPatronPassword());
          assertNull(patronInformationResponse.getCurrencyType());
          assertNull(patronInformationResponse.getFeeAmount());
          assertNull(patronInformationResponse.getFeeLimit());
          assertNotNull(patronInformationResponse.getHoldItems());
          assertTrue(patronInformationResponse.getHoldItems().isEmpty());
          assertNotNull(patronInformationResponse.getOverdueItems());
          assertTrue(patronInformationResponse.getOverdueItems().isEmpty());
          assertNotNull(patronInformationResponse.getChargedItems());
          assertTrue(patronInformationResponse.getChargedItems().isEmpty());
          assertNotNull(patronInformationResponse.getFineItems());
          assertTrue(patronInformationResponse.getFineItems().isEmpty());
          assertNotNull(patronInformationResponse.getRecallItems());
          assertEquals(Arrays.asList("1990 to 2010"), patronInformationResponse.getRecallItems());
          assertNotNull(patronInformationResponse.getUnavailableHoldItems());
          assertTrue(patronInformationResponse.getUnavailableHoldItems().isEmpty());
          assertEquals("00430 Denis Parks, Indianapolis, FL 14654-6001 US",
              patronInformationResponse.getHomeAddress());
          assertEquals("earnestine@sipes-stokes-and-durgan.so",
              patronInformationResponse.getEmailAddress());
          assertEquals("(916)599-0326",
              patronInformationResponse.getHomePhoneNumber());
          assertNull(patronInformationResponse.getScreenMessage());
          assertNull(patronInformationResponse.getPrintLine());

          testContext.completeNow();
        })));
  }

  @Test
  void canPerformPatronStatus(Vertx vertx,
        VertxTestContext testContext,
        @Mock PasswordVerifier mockPasswordVerifier,
        @Mock FeeFinesRepository mockFeeFinesRepository,
        @Mock CirculationRepository mockCirculationRepository,
        @Mock UsersRepository mockUsersRepository) {
    final String patronIdentifier = "1029384756";
    final String patronPassword = "1234";
    final String institutionId = "diku";
    final String userId = "99a81cee-d439-42c8-9860-2bd1de881c4a";
    final String userBarcode = "2349871212";
    final Clock clock = TestUtils.getUtcFixedClock();
    final Float feeAmount = 34.50f;
    final Personal personal = new Personal.Builder()
        .firstName("Joe")
        .middleName("Zee")
        .lastName("Blow")
        .build();

    final User user = new User.Builder()
        .id(userId)
        .barcode(userBarcode)
        .personal(personal)
        .build();

    final ExtendedUser extendedUser = new ExtendedUser();
    extendedUser.setUser(user);
    extendedUser.setPatronGroup("patrons","The Library Patrons", "12335");

    final PatronStatusRequest patronStatus = PatronStatusRequest.builder()
        .patronIdentifier(patronIdentifier)
        .patronPassword(patronPassword)
        .institutionId(institutionId)
        .transactionDate(OffsetDateTime.now())
        .build();

    final JsonObject queryAccountResponse = new JsonObject()
        .put("accounts", new JsonArray()
        .add(new JsonObject()
          .put("remaining", feeAmount)
          .put("id", "2345")
        )
    );

    when(mockPasswordVerifier.verifyPatronPassword(anyString(), anyString(), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder()
            .extendedUser(extendedUser).build()));

    when(mockFeeFinesRepository.getFeeAmountByUserId(eq(userId), any()))
        .thenReturn(Future.succeededFuture(queryAccountResponse));

    PatronRepository patronRepository = new PatronRepository(mockUsersRepository,
        mockCirculationRepository, mockFeeFinesRepository, mockPasswordVerifier,
        clock);

    final SessionData sessionData = TestUtils.getMockedSessionData();

    patronRepository.performPatronStatusCommand(patronStatus, sessionData).onComplete(
        testContext.succeeding(patronStatusResponse -> testContext.verify(() -> {
          assertNotNull(patronStatusResponse);
          assertEquals(feeAmount.toString(), patronStatusResponse.getFeeAmount());
          assertEquals("Joe Zee Blow", patronStatusResponse.getPersonalName());
          assertEquals(true, patronStatusResponse.getValidPatron());
          assertEquals(null, patronStatusResponse.getScreenMessage());
          testContext.completeNow();
        }))
    );
  }

  @Test
  void cannotPerformPatronStatusWithBadUser(Vertx vertx,
        VertxTestContext testContext,
        @Mock PasswordVerifier mockPasswordVerifier,
        @Mock FeeFinesRepository mockFeeFinesRepository,
        @Mock CirculationRepository mockCirculationRepository,
        @Mock UsersRepository mockUsersRepository) {
    final String patronIdentifier = "1029384756";
    final String patronPassword = "1234";
    final String institutionId = "diku";
    final String userId = "99a81cee-d439-42c8-9860-2bd1de881c4a";
    final String userBarcode = "2349871212";
    final Clock clock = TestUtils.getUtcFixedClock();
    final Float feeAmount = 34.50f;
    final Personal personal = new Personal.Builder()
        .firstName("Joe")
        .middleName("Zee")
        .lastName("Blow")
        .build();


    final PatronStatusRequest patronStatus = PatronStatusRequest.builder()
        .patronIdentifier(patronIdentifier)
        .patronPassword(patronPassword)
        .institutionId(institutionId)
        .transactionDate(OffsetDateTime.now())
        .build();

    final JsonObject queryAccountResponse = new JsonObject()
        .put("accounts", new JsonArray()
        .add(new JsonObject()
          .put("remaining", feeAmount)
          .put("id", "2345")
        )
    );

    when(mockPasswordVerifier.verifyPatronPassword(anyString(), anyString(), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder()
            .extendedUser(null).build()));

    PatronRepository patronRepository = new PatronRepository(mockUsersRepository,
        mockCirculationRepository, mockFeeFinesRepository, mockPasswordVerifier,
        clock);

    final SessionData sessionData = TestUtils.getMockedSessionData();

    patronRepository.performPatronStatusCommand(patronStatus, sessionData).onComplete(
        testContext.succeeding(patronStatusResponse -> testContext.verify(() -> {
          assertNotNull(patronStatusResponse);
          assertEquals(false, patronStatusResponse.getValidPatron());
          testContext.completeNow();
        }))
    );
  }

  private static Stream<Arguments> providePatronInformationParams() {
    final List<String> l = Collections.emptyList();
    return Stream.of(
        Arguments.of(Summary.HOLD_ITEMS, Arrays.asList(Arrays.asList("Interesting Times",
            "Request title1556587200969"), l, l, l, l, l)),
        Arguments.of(Summary.OVERDUE_ITEMS, Arrays.asList(l, Arrays.asList("1990 to 2010"),
            l, l, l, l)),
        Arguments.of(Summary.CHARGED_ITEMS, Arrays.asList(l, l, l, l, l, l)),
        Arguments.of(Summary.FINE_ITEMS, Arrays.asList(l, l, l, l, l, l)),
        Arguments.of(Summary.RECALL_ITEMS,
            Arrays.asList(l, l, l, l, Arrays.asList("1990 to 2010"), l)),
        Arguments.of(Summary.UNAVAILABLE_HOLDS, Arrays.asList(l, l, l, l, l, l))
      );
  }

  /**
   * For some reason checkstyle wanted javadoc for this method.
   * @param summary Summary enum
   * @param expectedLists the result lists
   * @param vertx vertx instance
   * @param testContext the vertx test context
   * @param mockUsersRepository mock users repo
   * @param mockCirculationRepository mock circ repo
   */
  @SuppressWarnings("unchecked")
  @ParameterizedTest
  @MethodSource("providePatronInformationParams")
  public void canPatronInformationWithParams(Summary summary, List<List<String>> expectedLists,
      Vertx vertx, VertxTestContext testContext,
      @Mock UsersRepository mockUsersRepository,
      @Mock CirculationRepository mockCirculationRepository,
      @Mock FeeFinesRepository mockFeeFinesRepository,
      @Mock PasswordVerifier mockPasswordVerifier) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String patronIdentifier = "1234567890";
    final PatronInformation patronInformation = PatronInformation.builder()
        .language(ENGLISH)
        .transactionDate(OffsetDateTime.now())
        .summary(summary)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .terminalPassword("1234")
        .patronPassword("0989")
        .startItem(Integer.valueOf(1))
        .endItem(Integer.valueOf(10))
        .build();

    final String userResponseJson = getJsonFromFile("json/user_response.json");
    final User userResponse = Json.decodeValue(userResponseJson, User.class);
    final String manualBlocksResponseJson = getJsonFromFile("json/no_manual_blocks_response.json");
    final JsonObject manualBlocksResponse = new JsonObject(manualBlocksResponseJson);
    final String overdueResponseJson = getJsonFromFile("json/overdue_response.json");
    final JsonObject overdueResponse = new JsonObject(overdueResponseJson);
    final String holdsResponseJson = getJsonFromFile("json/holds_requests_response.json");
    final JsonObject holdsResponse = new JsonObject(holdsResponseJson);
    final String openLoansResponseJson = getJsonFromFile("json/open_loans_response.json");
    final JsonObject openLoansResponse = new JsonObject(openLoansResponseJson);
    final String recallsResponseJson = getJsonFromFile("json/recall_requests_response.json");
    final JsonObject recallsResponse = new JsonObject(recallsResponseJson);
    final String accountResponseJson = getJsonFromFile("json/account_request_response.json");
    final JsonObject accountResponse = new JsonObject(accountResponseJson);
    final ExtendedUser extendedUser = new ExtendedUser();
    extendedUser.setUser(userResponse);
    extendedUser.setPatronGroup("patrons","The Library Patrons", "12335");

    when(mockFeeFinesRepository.getManualBlocksByUserId(any(), any()))
        .thenReturn(Future.succeededFuture(manualBlocksResponse));
    when(mockFeeFinesRepository.getAccountDataByUserId(any(), any()))
        .thenReturn(Future.succeededFuture(accountResponse));
    when(mockCirculationRepository.getOverdueLoansByUserId(any(), any(), any(), any(), any()))
        .thenReturn(Future.succeededFuture(overdueResponse));
    when(mockCirculationRepository.getRequestsByUserId(
        any(), eq("Hold"), any(), any(), any()))
        .thenReturn(Future.succeededFuture(holdsResponse));
    when(mockCirculationRepository.getLoansByUserId(any(), any(), any(), any()))
        .thenReturn(Future.succeededFuture(openLoansResponse));
    when(mockCirculationRepository.getRequestsByItemId(
        any(), eq("Recall"), any(), any(), any()))
        .thenReturn(Future.succeededFuture(recallsResponse),
            Future.succeededFuture(new JsonObject().put("requests", new JsonArray())),
            Future.succeededFuture(new JsonObject().put("requests", new JsonArray())));
    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("0989"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder()
        .extendedUser(extendedUser).build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final PatronRepository patronRepository = new PatronRepository(mockUsersRepository,
        mockCirculationRepository, mockFeeFinesRepository, mockPasswordVerifier, clock);
    patronRepository.performPatronInformationCommand(patronInformation, sessionData).onComplete(
        testContext.succeeding(patronInformationResponse -> testContext.verify(() -> {
          assertNotNull(patronInformationResponse);
          assertNotNull(patronInformationResponse.getPatronStatus());
          assertTrue(patronInformationResponse.getPatronStatus().isEmpty());
          assertEquals(ENGLISH, patronInformationResponse.getLanguage());
          assertEquals(OffsetDateTime.now(clock), patronInformationResponse.getTransactionDate());
          assertEquals(2, patronInformationResponse.getHoldItemsCount());
          assertEquals(1, patronInformationResponse.getOverdueItemsCount());
          assertEquals(3,patronInformationResponse.getChargedItemsCount());
          assertEquals(1,patronInformationResponse.getFineItemsCount());
          assertNotNull(patronInformationResponse.getChargedItemsCount());
          assertNotNull(patronInformationResponse.getFineItemsCount());
          assertEquals(1, patronInformationResponse.getRecallItemsCount());
          assertNull(patronInformationResponse.getUnavailableHoldsCount());
          assertEquals("diku", patronInformationResponse.getInstitutionId());
          assertEquals(patronIdentifier, patronInformationResponse.getPatronIdentifier());
          assertEquals("Darius Auer", patronInformationResponse.getPersonalName());
          assertNull(patronInformationResponse.getHoldItemsLimit());
          assertNull(patronInformationResponse.getOverdueItemsLimit());
          assertNull(patronInformationResponse.getChargedItemsLimit());
          assertTrue(patronInformationResponse.getValidPatron());
          assertNull(patronInformationResponse.getValidPatronPassword());
          assertNull(patronInformationResponse.getCurrencyType());
          assertNull(patronInformationResponse.getFeeAmount());
          assertNull(patronInformationResponse.getFeeLimit());
          assertNotNull(patronInformationResponse.getHoldItems());
          assertEquals(expectedLists.get(0), patronInformationResponse.getHoldItems());
          assertNotNull(patronInformationResponse.getOverdueItems());
          assertEquals(expectedLists.get(1), patronInformationResponse.getOverdueItems());
          assertNotNull(patronInformationResponse.getChargedItems());
          assertEquals(expectedLists.get(2), patronInformationResponse.getChargedItems());
          assertNotNull(patronInformationResponse.getFineItems());
          assertEquals(expectedLists.get(3), patronInformationResponse.getFineItems());
          assertNotNull(patronInformationResponse.getRecallItems());
          assertEquals(expectedLists.get(4), patronInformationResponse.getRecallItems());
          assertNotNull(patronInformationResponse.getUnavailableHoldItems());
          assertEquals(expectedLists.get(5), patronInformationResponse.getUnavailableHoldItems());
          assertEquals("00430 Denis Parks, Indianapolis, FL 14654-6001 US",
              patronInformationResponse.getHomeAddress());
          assertEquals("earnestine@sipes-stokes-and-durgan.so",
              patronInformationResponse.getEmailAddress());
          assertEquals("(916)599-0326",
              patronInformationResponse.getHomePhoneNumber());
          assertNull(patronInformationResponse.getScreenMessage());
          assertNull(patronInformationResponse.getPrintLine());

          testContext.completeNow();
        })));
  }

  @Test
  public void canPatronInformationWithExtendedFees(Vertx vertx, VertxTestContext testContext,
       @Mock UsersRepository mockUsersRepository,
       @Mock CirculationRepository mockCirculationRepository,
       @Mock FeeFinesRepository mockFeeFinesRepository,
       @Mock PasswordVerifier mockPasswordVerifier) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String patronIdentifier = "1234567890";
    final Summary summary = Summary.EXTENDED_FEES;
    final PatronInformation patronInformation = PatronInformation.builder()
        .language(ENGLISH)
        .transactionDate(OffsetDateTime.now())
        .summary(summary)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .terminalPassword("1234")
        .patronPassword("0989")
        .startItem(Integer.valueOf(1))
        .endItem(Integer.valueOf(10))
        .build();

    final String userResponseJson = getJsonFromFile("json/user_response.json");
    final User userResponse = Json.decodeValue(userResponseJson, User.class);
    final String manualBlocksResponseJson = getJsonFromFile("json/no_manual_blocks_response.json");
    final JsonObject manualBlocksResponse = new JsonObject(manualBlocksResponseJson);
    final String overdueResponseJson = getJsonFromFile("json/overdue_response.json");
    final JsonObject overdueResponse = new JsonObject(overdueResponseJson);
    final String holdsResponseJson = getJsonFromFile("json/holds_requests_response.json");
    final JsonObject holdsResponse = new JsonObject(holdsResponseJson);
    final String openLoansResponseJson = getJsonFromFile("json/open_loans_response.json");
    final JsonObject openLoansResponse = new JsonObject(openLoansResponseJson);
    final String recallsResponseJson = getJsonFromFile("json/recall_requests_response.json");
    final JsonObject recallsResponse = new JsonObject(recallsResponseJson);
    final String accountResponseJson = getJsonFromFile(
        "json/account_multiple_with_feefines_request_response.json");
    final JsonObject accountResponse = new JsonObject(accountResponseJson);
    final ExtendedUser extendedUser = new ExtendedUser();
    extendedUser.setUser(userResponse);
    extendedUser.setPatronGroup("patrons","The Library Patrons", "12335");

    when(mockFeeFinesRepository.getManualBlocksByUserId(any(), any()))
        .thenReturn(Future.succeededFuture(manualBlocksResponse));
    when(mockFeeFinesRepository.getAccountDataByUserId(any(), any()))
        .thenReturn(Future.succeededFuture(accountResponse));
    when(mockCirculationRepository.getOverdueLoansByUserId(any(), any(), any(), any(), any()))
        .thenReturn(Future.succeededFuture(overdueResponse));
    when(mockCirculationRepository.getRequestsByUserId(
        any(), eq("Hold"), any(), any(), any()))
        .thenReturn(Future.succeededFuture(holdsResponse));
    when(mockCirculationRepository.getLoansByUserId(any(), any(), any(), any()))
        .thenReturn(Future.succeededFuture(openLoansResponse));
    when(mockCirculationRepository.getRequestsByItemId(
        any(), eq("Recall"), any(), any(), any()))
        .thenReturn(Future.succeededFuture(recallsResponse),
        Future.succeededFuture(new JsonObject().put("requests", new JsonArray())),
        Future.succeededFuture(new JsonObject().put("requests", new JsonArray())));
    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("0989"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder()
        .extendedUser(extendedUser).build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final PatronRepository patronRepository = new PatronRepository(mockUsersRepository,
        mockCirculationRepository, mockFeeFinesRepository, mockPasswordVerifier, clock);

    patronRepository.performPatronInformationCommand(patronInformation, sessionData).onComplete(
        testContext.succeeding(patronInformationResponse -> testContext.verify(() -> {
          assertNotNull(patronInformationResponse);
          assertNotNull(patronInformationResponse.getPatronStatus());
          assertEquals(3, patronInformationResponse.getPatronAccountList().size());
          assertEquals("Overdue fine",
              patronInformationResponse.getPatronAccountList().get(0).getFeeFineType());
          assertEquals(1.0,
              patronInformationResponse.getPatronAccountList().get(0).getFeeFineRemaining());
          assertEquals("Replacement processing fee",
              patronInformationResponse.getPatronAccountList().get(1).getFeeFineType());
          assertEquals(5.5,
              patronInformationResponse.getPatronAccountList().get(1).getFeeFineRemaining());
          testContext.completeNow();

        })));

  }

  @Test
  public void canPatronInformationWithRecallsPaged(Vertx vertx, VertxTestContext testContext,
      @Mock UsersRepository mockUsersRepository,
      @Mock CirculationRepository mockCirculationRepository,
      @Mock FeeFinesRepository mockFeeFinesRepository,
      @Mock PasswordVerifier mockPasswordVerifier) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String patronIdentifier = "1234567890";
    final PatronInformation patronInformation = PatronInformation.builder()
        .language(ENGLISH)
        .transactionDate(OffsetDateTime.now())
        .summary(Summary.RECALL_ITEMS)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .terminalPassword("1234")
        .patronPassword("0989")
        .startItem(Integer.valueOf(2))
        .endItem(Integer.valueOf(2))
        .build();

    final String userResponseJson = getJsonFromFile("json/user_response3.json");
    final User userResponse = Json.decodeValue(userResponseJson, User.class);
    final String manualBlocksResponseJson = getJsonFromFile("json/no_manual_blocks_response.json");
    final JsonObject manualBlocksResponse = new JsonObject(manualBlocksResponseJson);
    final String overdueResponseJson = getJsonFromFile("json/overdue_response.json");
    final JsonObject overdueResponse = new JsonObject(overdueResponseJson);
    final String holdsResponseJson = getJsonFromFile("json/holds_requests_response.json");
    final JsonObject holdsResponse = new JsonObject(holdsResponseJson);
    final String openLoansResponseJson = getJsonFromFile("json/open_loans_response.json");
    final JsonObject openLoansResponse = new JsonObject(openLoansResponseJson);
    final String recallsResponseJson = getJsonFromFile("json/recall_requests_response.json");
    final JsonObject recallsResponse = new JsonObject(recallsResponseJson);
    final String recallsResponse1Json = getJsonFromFile("json/recall_requests_response1.json");
    final JsonObject recallsResponse1 = new JsonObject(recallsResponse1Json);
    final String recallsResponse2Json = getJsonFromFile("json/recall_requests_response2.json");
    final JsonObject recallsResponse2 = new JsonObject(recallsResponse2Json);
    final String accountResponseJson = getJsonFromFile("json/account_request_response.json");
    final JsonObject accountResponse = new JsonObject(accountResponseJson);
    final ExtendedUser extendedUser = new ExtendedUser();
    extendedUser.setUser(userResponse);
    extendedUser.setPatronGroup("patrons","The Library Patrons", "12335");

    when(mockFeeFinesRepository.getManualBlocksByUserId(any(), any()))
        .thenReturn(Future.succeededFuture(manualBlocksResponse));
    when(mockFeeFinesRepository.getAccountDataByUserId(any(), any()))
        .thenReturn(Future.succeededFuture(accountResponse));
    when(mockCirculationRepository.getOverdueLoansByUserId(any(), any(), any(), any(), any()))
        .thenReturn(Future.succeededFuture(overdueResponse));
    when(mockCirculationRepository.getRequestsByUserId(
        any(), eq("Hold"), any(), any(), any()))
        .thenReturn(Future.succeededFuture(holdsResponse));
    when(mockCirculationRepository.getLoansByUserId(any(), any(), any(), any()))
        .thenReturn(Future.succeededFuture(openLoansResponse));
    when(mockCirculationRepository.getRequestsByItemId(
        eq("4593bdb8-f056-4a75-9c75-7b04c3a1dd64"), eq("Recall"), any(), any(), any()))
        .thenReturn(Future.succeededFuture(recallsResponse));
    when(mockCirculationRepository.getRequestsByItemId(
        eq("02114831-1c8f-4594-beb9-1bf23f65054c"), eq("Recall"), any(), any(), any()))
        .thenReturn(Future.succeededFuture(recallsResponse1));
    when(mockCirculationRepository.getRequestsByItemId(
        eq("c70f966b-435f-4879-a7d1-3f66e6699191"), eq("Recall"), any(), any(), any()))
        .thenReturn(Future.succeededFuture(recallsResponse2));
    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("0989"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder()
        .extendedUser(extendedUser).build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final PatronRepository patronRepository = new PatronRepository(mockUsersRepository,
        mockCirculationRepository, mockFeeFinesRepository, mockPasswordVerifier, clock);
    patronRepository.performPatronInformationCommand(patronInformation, sessionData).onComplete(
        testContext.succeeding(patronInformationResponse -> testContext.verify(() -> {
          assertNotNull(patronInformationResponse);
          assertNotNull(patronInformationResponse.getPatronStatus());
          assertTrue(patronInformationResponse.getPatronStatus().isEmpty());
          assertEquals(ENGLISH, patronInformationResponse.getLanguage());
          assertEquals(OffsetDateTime.now(clock), patronInformationResponse.getTransactionDate());
          assertEquals(2, patronInformationResponse.getHoldItemsCount());
          assertEquals(1, patronInformationResponse.getOverdueItemsCount());
          assertEquals(1, patronInformationResponse.getOverdueItemsCount());
          assertEquals(3,patronInformationResponse.getChargedItemsCount());
          assertNotNull(patronInformationResponse.getChargedItemsCount());
          assertNotNull(patronInformationResponse.getFineItemsCount());
          assertEquals(3, patronInformationResponse.getRecallItemsCount());
          assertNull(patronInformationResponse.getUnavailableHoldsCount());
          assertEquals("diku", patronInformationResponse.getInstitutionId());
          assertEquals(patronIdentifier, patronInformationResponse.getPatronIdentifier());
          assertEquals("Darius Auer", patronInformationResponse.getPersonalName());
          assertNull(patronInformationResponse.getHoldItemsLimit());
          assertNull(patronInformationResponse.getOverdueItemsLimit());
          assertNull(patronInformationResponse.getChargedItemsLimit());
          assertTrue(patronInformationResponse.getValidPatron());
          assertNull(patronInformationResponse.getValidPatronPassword());
          assertNull(patronInformationResponse.getCurrencyType());
          assertNull(patronInformationResponse.getFeeAmount());
          assertNull(patronInformationResponse.getFeeLimit());
          assertNotNull(patronInformationResponse.getHoldItems());
          assertTrue(patronInformationResponse.getHoldItems().isEmpty());
          assertNotNull(patronInformationResponse.getOverdueItems());
          assertTrue(patronInformationResponse.getOverdueItems().isEmpty());
          assertNotNull(patronInformationResponse.getChargedItems());
          assertTrue(patronInformationResponse.getChargedItems().isEmpty());
          assertNotNull(patronInformationResponse.getFineItems());
          assertTrue(patronInformationResponse.getFineItems().isEmpty());
          assertNotNull(patronInformationResponse.getRecallItems());
          assertEquals(Arrays.asList("Al Gore"), patronInformationResponse.getRecallItems());
          assertNotNull(patronInformationResponse.getUnavailableHoldItems());
          assertTrue(patronInformationResponse.getUnavailableHoldItems().isEmpty());
          assertNull(patronInformationResponse.getHomeAddress());
          assertEquals("earnestine@sipes-stokes-and-durgan.so",
              patronInformationResponse.getEmailAddress());
          assertEquals("(916)599-0326",
              patronInformationResponse.getHomePhoneNumber());
          assertNull(patronInformationResponse.getScreenMessage());
          assertNull(patronInformationResponse.getPrintLine());

          testContext.completeNow();
        })));
  }

  @Test
  public void patronInformationContainsInvalidPatronWhenUserNotFound(Vertx vertx,
      VertxTestContext testContext,
      @Mock UsersRepository mockUsersRepository,
      @Mock CirculationRepository mockCirculationRepository,
      @Mock FeeFinesRepository mockFeeFinesRepository,
      @Mock PasswordVerifier mockPasswordVerifier) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String patronIdentifier = "1234567890";
    final PatronInformation patronInformation = PatronInformation.builder()
        .language(ENGLISH)
        .transactionDate(OffsetDateTime.now())
        .summary(null)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .terminalPassword("1234")
        .patronPassword("0989")
        .build();

    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("0989"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder().build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final PatronRepository patronRepository = new PatronRepository(mockUsersRepository,
        mockCirculationRepository, mockFeeFinesRepository, mockPasswordVerifier, clock);
    patronRepository.performPatronInformationCommand(patronInformation, sessionData).onComplete(
        testContext.succeeding(patronInformationResponse -> testContext.verify(() -> {
          assertNotNull(patronInformationResponse);
          assertEquals(EnumSet.allOf(PatronStatus.class),
              patronInformationResponse.getPatronStatus());
          assertEquals(UNKNOWN, patronInformationResponse.getLanguage());
          assertEquals(OffsetDateTime.now(clock), patronInformationResponse.getTransactionDate());
          assertEquals(0, patronInformationResponse.getHoldItemsCount());
          assertEquals(0, patronInformationResponse.getOverdueItemsCount());
          assertEquals(0, patronInformationResponse.getChargedItemsCount());
          assertEquals(0, patronInformationResponse.getFineItemsCount());
          assertEquals(0, patronInformationResponse.getRecallItemsCount());
          assertEquals(0, patronInformationResponse.getUnavailableHoldsCount());
          assertEquals("diku", patronInformationResponse.getInstitutionId());
          assertEquals(patronIdentifier, patronInformationResponse.getPatronIdentifier());
          assertEquals(patronIdentifier, patronInformationResponse.getPersonalName());
          // Fall back to the patron identifier when there is no name (better than nothing)
          assertEquals(patronIdentifier, patronInformationResponse.getPersonalName());
          assertFalse(patronInformationResponse.getValidPatron());
          assertNull(patronInformationResponse.getValidPatronPassword());
          assertNull(patronInformationResponse.getCurrencyType());
          assertNull(patronInformationResponse.getFeeAmount());
          assertNull(patronInformationResponse.getFeeLimit());
          assertNotNull(patronInformationResponse.getHoldItems());
          assertTrue(patronInformationResponse.getHoldItems().isEmpty());
          assertNotNull(patronInformationResponse.getOverdueItems());
          assertTrue(patronInformationResponse.getOverdueItems().isEmpty());
          assertNotNull(patronInformationResponse.getChargedItems());
          assertTrue(patronInformationResponse.getChargedItems().isEmpty());
          assertNotNull(patronInformationResponse.getFineItems());
          assertTrue(patronInformationResponse.getFineItems().isEmpty());
          assertNotNull(patronInformationResponse.getRecallItems());
          assertTrue(patronInformationResponse.getRecallItems().isEmpty());
          assertNotNull(patronInformationResponse.getUnavailableHoldItems());
          assertTrue(patronInformationResponse.getUnavailableHoldItems().isEmpty());
          assertNull(patronInformationResponse.getHomeAddress());
          assertNull(patronInformationResponse.getEmailAddress());
          assertNull(patronInformationResponse.getHomePhoneNumber());
          assertEquals(Collections.singletonList(MESSAGE_INVALID_PATRON),
              patronInformationResponse.getScreenMessage());
          assertNull(patronInformationResponse.getPrintLine());

          testContext.completeNow();
        })));
  }

  @Test
  public void patronInvalidWhenPatronInformationContainsInactiveUser(Vertx vertx,
      VertxTestContext testContext,
      @Mock UsersRepository mockUsersRepository,
      @Mock CirculationRepository mockCirculationRepository,
      @Mock FeeFinesRepository mockFeeFinesRepository,
      @Mock PasswordVerifier mockPasswordVerifier) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String patronIdentifier = "1234567890";
    final PatronInformation patronInformation = PatronInformation.builder()
        .language(ENGLISH)
        .transactionDate(OffsetDateTime.now())
        .summary(null)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .terminalPassword("1234")
        .patronPassword("0989")
        .build();
    final ExtendedUser extendedUser = new ExtendedUser();
    extendedUser.setUser(new User.Builder().active(FALSE).build());

    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("0989"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder()
        .extendedUser(extendedUser).build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final PatronRepository patronRepository = new PatronRepository(mockUsersRepository,
        mockCirculationRepository, mockFeeFinesRepository, mockPasswordVerifier, clock);
    patronRepository.performPatronInformationCommand(patronInformation, sessionData).onComplete(
        testContext.succeeding(patronInformationResponse -> testContext.verify(() -> {
          assertNotNull(patronInformationResponse);
          assertEquals(EnumSet.allOf(PatronStatus.class),
              patronInformationResponse.getPatronStatus());
          assertEquals(UNKNOWN, patronInformationResponse.getLanguage());
          assertEquals(OffsetDateTime.now(clock), patronInformationResponse.getTransactionDate());
          assertEquals(0, patronInformationResponse.getHoldItemsCount());
          assertEquals(0, patronInformationResponse.getOverdueItemsCount());
          assertEquals(0, patronInformationResponse.getChargedItemsCount());
          assertEquals(0, patronInformationResponse.getFineItemsCount());
          assertEquals(0, patronInformationResponse.getRecallItemsCount());
          assertEquals(0, patronInformationResponse.getUnavailableHoldsCount());
          assertEquals("diku", patronInformationResponse.getInstitutionId());
          assertEquals(patronIdentifier, patronInformationResponse.getPatronIdentifier());
          assertEquals(patronIdentifier,patronInformationResponse.getPersonalName());
          // Fall back to the patron identifier when there is no name (better than nothing)
          assertEquals(patronIdentifier, patronInformationResponse.getPersonalName());
          assertFalse(patronInformationResponse.getValidPatron());
          assertNull(patronInformationResponse.getValidPatronPassword());
          assertNull(patronInformationResponse.getCurrencyType());
          assertNull(patronInformationResponse.getFeeAmount());
          assertNull(patronInformationResponse.getFeeLimit());
          assertNotNull(patronInformationResponse.getHoldItems());
          assertTrue(patronInformationResponse.getHoldItems().isEmpty());
          assertNotNull(patronInformationResponse.getOverdueItems());
          assertTrue(patronInformationResponse.getOverdueItems().isEmpty());
          assertNotNull(patronInformationResponse.getChargedItems());
          assertTrue(patronInformationResponse.getChargedItems().isEmpty());
          assertNotNull(patronInformationResponse.getFineItems());
          assertTrue(patronInformationResponse.getFineItems().isEmpty());
          assertNotNull(patronInformationResponse.getRecallItems());
          assertTrue(patronInformationResponse.getRecallItems().isEmpty());
          assertNotNull(patronInformationResponse.getUnavailableHoldItems());
          assertTrue(patronInformationResponse.getUnavailableHoldItems().isEmpty());
          assertNull(patronInformationResponse.getHomeAddress());
          assertNull(patronInformationResponse.getEmailAddress());
          assertNull(patronInformationResponse.getHomePhoneNumber());
          assertEquals(Collections.singletonList(MESSAGE_INVALID_PATRON),
              patronInformationResponse.getScreenMessage());
          assertNull(patronInformationResponse.getPrintLine());

          testContext.completeNow();
        })));
  }

  @Test
  public void patronInvalidWhenPatronInformationContainsUserWithNoId(Vertx vertx,
      VertxTestContext testContext,
      @Mock UsersRepository mockUsersRepository,
      @Mock CirculationRepository mockCirculationRepository,
      @Mock FeeFinesRepository mockFeeFinesRepository,
      @Mock PasswordVerifier mockPasswordVerifier) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String patronIdentifier = "1234567890";
    final PatronInformation patronInformation = PatronInformation.builder()
        .language(ENGLISH)
        .transactionDate(OffsetDateTime.now())
        .summary(null)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .terminalPassword("1234")
        .patronPassword("0989")
        .build();
    final ExtendedUser extendedUser = new ExtendedUser();
    extendedUser.setUser(new User.Builder().active(TRUE).build());

    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("0989"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder()
          .extendedUser(extendedUser).build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final PatronRepository patronRepository = new PatronRepository(mockUsersRepository,
        mockCirculationRepository, mockFeeFinesRepository, mockPasswordVerifier, clock);
    patronRepository.performPatronInformationCommand(patronInformation, sessionData).onComplete(
        testContext.succeeding(patronInformationResponse -> testContext.verify(() -> {
          assertNotNull(patronInformationResponse);
          assertEquals(EnumSet.allOf(PatronStatus.class),
              patronInformationResponse.getPatronStatus());
          assertEquals(UNKNOWN, patronInformationResponse.getLanguage());
          assertEquals(OffsetDateTime.now(clock), patronInformationResponse.getTransactionDate());
          assertEquals(0, patronInformationResponse.getHoldItemsCount());
          assertEquals(0, patronInformationResponse.getOverdueItemsCount());
          assertEquals(0, patronInformationResponse.getChargedItemsCount());
          assertEquals(0, patronInformationResponse.getFineItemsCount());
          assertEquals(0, patronInformationResponse.getRecallItemsCount());
          assertEquals(0, patronInformationResponse.getUnavailableHoldsCount());
          assertEquals("diku", patronInformationResponse.getInstitutionId());
          assertEquals(patronIdentifier, patronInformationResponse.getPatronIdentifier());
          // Fall back to the patron identifier when there is no name (better than nothing)
          assertEquals(patronIdentifier, patronInformationResponse.getPersonalName());
          assertFalse(patronInformationResponse.getValidPatron());
          assertNull(patronInformationResponse.getValidPatronPassword());
          assertNull(patronInformationResponse.getCurrencyType());
          assertNull(patronInformationResponse.getFeeAmount());
          assertNull(patronInformationResponse.getFeeLimit());
          assertNotNull(patronInformationResponse.getHoldItems());
          assertTrue(patronInformationResponse.getHoldItems().isEmpty());
          assertNotNull(patronInformationResponse.getOverdueItems());
          assertTrue(patronInformationResponse.getOverdueItems().isEmpty());
          assertNotNull(patronInformationResponse.getChargedItems());
          assertTrue(patronInformationResponse.getChargedItems().isEmpty());
          assertNotNull(patronInformationResponse.getFineItems());
          assertTrue(patronInformationResponse.getFineItems().isEmpty());
          assertNotNull(patronInformationResponse.getRecallItems());
          assertTrue(patronInformationResponse.getRecallItems().isEmpty());
          assertNotNull(patronInformationResponse.getUnavailableHoldItems());
          assertTrue(patronInformationResponse.getUnavailableHoldItems().isEmpty());
          assertNull(patronInformationResponse.getHomeAddress());
          assertNull(patronInformationResponse.getEmailAddress());
          assertNull(patronInformationResponse.getHomePhoneNumber());
          assertEquals(Collections.singletonList(MESSAGE_INVALID_PATRON),
              patronInformationResponse.getScreenMessage());
          assertNull(patronInformationResponse.getPrintLine());

          testContext.completeNow();
        })));
  }

  @Test
  public void canPatronInformationWithNoSummaryDetails(Vertx vertx, VertxTestContext testContext,
      @Mock UsersRepository mockUsersRepository,
      @Mock CirculationRepository mockCirculationRepository,
      @Mock FeeFinesRepository mockFeeFinesRepository,
      @Mock PasswordVerifier mockPasswordVerifier) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String patronIdentifier = "1234567890";
    final PatronInformation patronInformation = PatronInformation.builder()
        .language(ENGLISH)
        .transactionDate(OffsetDateTime.now())
        .summary(Summary.RECALL_ITEMS)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .terminalPassword("1234")
        .patronPassword("0989")
        .startItem(Integer.valueOf(2))
        .endItem(Integer.valueOf(2))
        .build();

    final String userResponseJson = getJsonFromFile("json/user_response2.json");
    final User userResponse = Json.decodeValue(userResponseJson, User.class);
    final ExtendedUser extendedUser = new ExtendedUser();
    extendedUser.setUser(userResponse);
    extendedUser.setPatronGroup("patrons","The Library Patrons", "12335");

    final String manualBlocksResponseJson = getJsonFromFile("json/no_manual_blocks_response.json");
    final JsonObject manualBlocksResponse = new JsonObject(manualBlocksResponseJson);
    when(mockFeeFinesRepository.getManualBlocksByUserId(any(), any()))
        .thenReturn(Future.succeededFuture(manualBlocksResponse));
    when(mockFeeFinesRepository.getAccountDataByUserId(any(), any()))
        .thenReturn(Future.succeededFuture(null));
    when(mockCirculationRepository.getOverdueLoansByUserId(any(), any(), any(), any(), any()))
        .thenReturn(Future.succeededFuture(null));
    when(mockCirculationRepository.getRequestsByUserId(
        any(), eq("Hold"), any(), any(), any()))
        .thenReturn(Future.succeededFuture(null));
    when(mockCirculationRepository.getLoansByUserId(any(), any(), any(), any()))
        .thenReturn(Future.succeededFuture(null));
    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("0989"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder()
        .extendedUser(extendedUser).build()));


    final SessionData sessionData = TestUtils.getMockedSessionData();

    final PatronRepository patronRepository = new PatronRepository(mockUsersRepository,
        mockCirculationRepository, mockFeeFinesRepository, mockPasswordVerifier, clock);
    patronRepository.performPatronInformationCommand(patronInformation, sessionData).onComplete(
        testContext.succeeding(patronInformationResponse -> testContext.verify(() -> {
          assertNotNull(patronInformationResponse);
          assertEquals(EnumSet.noneOf(PatronStatus.class),
              patronInformationResponse.getPatronStatus());
          assertEquals(ENGLISH, patronInformationResponse.getLanguage());
          assertEquals(OffsetDateTime.now(clock), patronInformationResponse.getTransactionDate());
          assertEquals(0, patronInformationResponse.getHoldItemsCount());
          assertEquals(0, patronInformationResponse.getOverdueItemsCount());
          assertNotNull(patronInformationResponse.getChargedItemsCount());
          assertNotNull(patronInformationResponse.getFineItemsCount());
          assertEquals(0, patronInformationResponse.getRecallItemsCount());
          assertNull(patronInformationResponse.getUnavailableHoldsCount());
          assertEquals("diku", patronInformationResponse.getInstitutionId());
          assertEquals(patronIdentifier, patronInformationResponse.getPatronIdentifier());
          assertEquals("Darius Auer", patronInformationResponse.getPersonalName());
          assertTrue(patronInformationResponse.getValidPatron());
          assertNull(patronInformationResponse.getValidPatronPassword());
          assertNull(patronInformationResponse.getCurrencyType());
          assertNull(patronInformationResponse.getFeeAmount());
          assertNull(patronInformationResponse.getFeeLimit());
          assertNotNull(patronInformationResponse.getHoldItems());
          assertTrue(patronInformationResponse.getHoldItems().isEmpty());
          assertNotNull(patronInformationResponse.getOverdueItems());
          assertTrue(patronInformationResponse.getOverdueItems().isEmpty());
          assertNotNull(patronInformationResponse.getChargedItems());
          assertTrue(patronInformationResponse.getChargedItems().isEmpty());
          assertNotNull(patronInformationResponse.getFineItems());
          assertTrue(patronInformationResponse.getFineItems().isEmpty());
          assertNotNull(patronInformationResponse.getRecallItems());
          assertTrue(patronInformationResponse.getRecallItems().isEmpty());
          assertNotNull(patronInformationResponse.getUnavailableHoldItems());
          assertTrue(patronInformationResponse.getUnavailableHoldItems().isEmpty());
          assertNull(patronInformationResponse.getHomeAddress());
          assertEquals("earnestine@sipes-stokes-and-durgan.so",
              patronInformationResponse.getEmailAddress());
          assertEquals("(916)599-0326",
              patronInformationResponse.getHomePhoneNumber());
          assertNull(patronInformationResponse.getScreenMessage());
          assertNull(patronInformationResponse.getPrintLine());

          testContext.completeNow();
        })));
  }

  @Test
  public void canPatronInformationWithNoSummaryRecallDetails(Vertx vertx,
      VertxTestContext testContext,
      @Mock UsersRepository mockUsersRepository,
      @Mock CirculationRepository mockCirculationRepository,
      @Mock FeeFinesRepository mockFeeFinesRepository,
      @Mock PasswordVerifier mockPasswordVerifier) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String patronIdentifier = "1234567890";
    final PatronInformation patronInformation = PatronInformation.builder()
        .language(ENGLISH)
        .transactionDate(OffsetDateTime.now())
        .summary(Summary.RECALL_ITEMS)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .terminalPassword("1234")
        .patronPassword("0989")
        .startItem(Integer.valueOf(2))
        .endItem(Integer.valueOf(2))
        .build();

    final String userResponseJson = getJsonFromFile("json/user_response1.json");
    final User userResponse = Json.decodeValue(userResponseJson, User.class);
    final String manualBlocksResponseJson = getJsonFromFile("json/no_manual_blocks_response.json");
    final JsonObject manualBlocksResponse = new JsonObject(manualBlocksResponseJson);
    final ExtendedUser extendedUser = new ExtendedUser();
    extendedUser.setUser(userResponse);
    extendedUser.setPatronGroup("patrons","The Library Patrons", "12335");

    when(mockFeeFinesRepository.getManualBlocksByUserId(any(), any()))
        .thenReturn(Future.succeededFuture(manualBlocksResponse));
    when(mockFeeFinesRepository.getAccountDataByUserId(any(), any()))
        .thenReturn(Future.succeededFuture(null));
    when(mockCirculationRepository.getOverdueLoansByUserId(any(), any(), any(), any(), any()))
        .thenReturn(Future.succeededFuture(null));
    when(mockCirculationRepository.getRequestsByUserId(
        any(), eq("Hold"), any(), any(), any()))
        .thenReturn(Future.succeededFuture(null));
    when(mockCirculationRepository.getLoansByUserId(any(), any(), any(), any()))
        .thenReturn(Future.succeededFuture(new JsonObject().put("loans",
            new JsonArray().add(new JsonObject().put("itemId", "1234")))));
    when(mockCirculationRepository.getRequestsByItemId(
        any(), eq("Recall"), any(), any(), any()))
        .thenReturn(Future.succeededFuture(null));
    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("0989"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder()
        .extendedUser(extendedUser).build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final PatronRepository patronRepository = new PatronRepository(mockUsersRepository,
        mockCirculationRepository, mockFeeFinesRepository, mockPasswordVerifier, clock);
    patronRepository.performPatronInformationCommand(patronInformation, sessionData).onComplete(
        testContext.succeeding(patronInformationResponse -> testContext.verify(() -> {
          assertNotNull(patronInformationResponse);
          assertEquals(EnumSet.noneOf(PatronStatus.class),
              patronInformationResponse.getPatronStatus());
          assertEquals(ENGLISH, patronInformationResponse.getLanguage());
          assertEquals(OffsetDateTime.now(clock), patronInformationResponse.getTransactionDate());
          assertEquals(0, patronInformationResponse.getHoldItemsCount());
          assertEquals(0, patronInformationResponse.getOverdueItemsCount());
          assertEquals(0, patronInformationResponse.getOverdueItemsCount());
          assertEquals(0,patronInformationResponse.getChargedItemsCount());
          assertNotNull(patronInformationResponse.getChargedItemsCount());
          assertNotNull(patronInformationResponse.getFineItemsCount());
          assertEquals(0, patronInformationResponse.getRecallItemsCount());
          assertNull(patronInformationResponse.getUnavailableHoldsCount());
          assertEquals("diku", patronInformationResponse.getInstitutionId());
          assertEquals(patronIdentifier, patronInformationResponse.getPatronIdentifier());
          assertEquals("Darius Auer", patronInformationResponse.getPersonalName());
          assertTrue(patronInformationResponse.getValidPatron());
          assertNull(patronInformationResponse.getValidPatronPassword());
          assertNull(patronInformationResponse.getCurrencyType());
          assertNull(patronInformationResponse.getFeeAmount());
          assertNull(patronInformationResponse.getFeeLimit());
          assertNotNull(patronInformationResponse.getHoldItems());
          assertTrue(patronInformationResponse.getHoldItems().isEmpty());
          assertNotNull(patronInformationResponse.getOverdueItems());
          assertTrue(patronInformationResponse.getOverdueItems().isEmpty());
          assertNotNull(patronInformationResponse.getChargedItems());
          assertTrue(patronInformationResponse.getChargedItems().isEmpty());
          assertNotNull(patronInformationResponse.getFineItems());
          assertTrue(patronInformationResponse.getFineItems().isEmpty());
          assertNotNull(patronInformationResponse.getRecallItems());
          assertTrue(patronInformationResponse.getRecallItems().isEmpty());
          assertNotNull(patronInformationResponse.getUnavailableHoldItems());
          assertTrue(patronInformationResponse.getUnavailableHoldItems().isEmpty());
          assertEquals("2123 Blah Street, Boston, MA 12345-1234 US",
              patronInformationResponse.getHomeAddress());
          assertEquals("earnestine@sipes-stokes-and-durgan.so",
              patronInformationResponse.getEmailAddress());
          assertEquals("(916)599-0326",
              patronInformationResponse.getHomePhoneNumber());
          assertNull(patronInformationResponse.getScreenMessage());
          assertNull(patronInformationResponse.getPrintLine());

          testContext.completeNow();
        })));
  }

  @ParameterizedTest
  @MethodSource("provideManualBlocks")
  void canPatronInformationWithManualBlocksDetails(JsonObject manualBlocksResponse,
      Set<PatronStatus> expectedPatronStatus, List<String> expectedScreenMessage, Vertx vertx,
      VertxTestContext testContext,
      @Mock UsersRepository mockUsersRepository,
      @Mock CirculationRepository mockCirculationRepository,
      @Mock FeeFinesRepository mockFeeFinesRepository,
      @Mock PasswordVerifier mockPasswordVerifier) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String patronIdentifier = "1234567890";
    final PatronInformation patronInformation = PatronInformation.builder()
        .language(ENGLISH)
        .transactionDate(OffsetDateTime.now())
        .summary(Summary.RECALL_ITEMS)
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .terminalPassword("1234")
        .patronPassword("0989")
        .startItem(Integer.valueOf(2))
        .endItem(Integer.valueOf(2))
        .build();

    final String userResponseJson = getJsonFromFile("json/user_response2.json");
    final User userResponse = Json.decodeValue(userResponseJson, User.class);
    final ExtendedUser extendedUser = new ExtendedUser();
    extendedUser.setUser(userResponse);
    extendedUser.setPatronGroup("patrons","The Library Patrons", "12335");

    when(mockFeeFinesRepository.getManualBlocksByUserId(any(), any()))
        .thenReturn(Future.succeededFuture(manualBlocksResponse));
    final String accountResponseJson = getJsonFromFile("json/account_request_response.json");
    final JsonObject accountResponse = new JsonObject(accountResponseJson);
    when(mockFeeFinesRepository.getAccountDataByUserId(any(), any()))
        .thenReturn(Future.succeededFuture(accountResponse));
    when(mockCirculationRepository.getOverdueLoansByUserId(any(), any(), any(), any(), any()))
        .thenReturn(Future.succeededFuture(null));
    when(mockCirculationRepository.getRequestsByUserId(
      any(), eq("Hold"), any(), any(), any()))
        .thenReturn(Future.succeededFuture(null));
    when(mockCirculationRepository.getLoansByUserId(any(), any(), any(), any()))
        .thenReturn(Future.succeededFuture(null));
    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("0989"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder()
        .extendedUser(extendedUser).build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final PatronRepository patronRepository = new PatronRepository(mockUsersRepository,
        mockCirculationRepository, mockFeeFinesRepository, mockPasswordVerifier, clock);
    patronRepository.performPatronInformationCommand(patronInformation, sessionData).onComplete(
        testContext.succeeding(patronInformationResponse -> testContext.verify(() -> {
          assertNotNull(patronInformationResponse);
          assertEquals(expectedPatronStatus,
              patronInformationResponse.getPatronStatus());
          assertEquals(ENGLISH, patronInformationResponse.getLanguage());
          assertEquals(OffsetDateTime.now(clock), patronInformationResponse.getTransactionDate());
          assertEquals(0, patronInformationResponse.getHoldItemsCount());
          assertEquals(0, patronInformationResponse.getOverdueItemsCount());
          assertEquals(0,patronInformationResponse.getChargedItemsCount());
          assertEquals(1,patronInformationResponse.getFineItemsCount());
          assertNotNull(patronInformationResponse.getChargedItemsCount());
          assertNotNull(patronInformationResponse.getFineItemsCount());
          assertEquals(0, patronInformationResponse.getRecallItemsCount());
          assertNull(patronInformationResponse.getUnavailableHoldsCount());
          assertEquals("diku", patronInformationResponse.getInstitutionId());
          assertEquals(patronIdentifier, patronInformationResponse.getPatronIdentifier());
          assertEquals("Darius Auer", patronInformationResponse.getPersonalName());
          assertTrue(patronInformationResponse.getValidPatron());
          assertNull(patronInformationResponse.getValidPatronPassword());
          assertNull(patronInformationResponse.getCurrencyType());
          assertNull(patronInformationResponse.getFeeAmount());
          assertNull(patronInformationResponse.getFeeLimit());
          assertNotNull(patronInformationResponse.getHoldItems());
          assertTrue(patronInformationResponse.getHoldItems().isEmpty());
          assertNotNull(patronInformationResponse.getOverdueItems());
          assertTrue(patronInformationResponse.getOverdueItems().isEmpty());
          assertNotNull(patronInformationResponse.getChargedItems());
          assertTrue(patronInformationResponse.getChargedItems().isEmpty());
          assertNotNull(patronInformationResponse.getFineItems());
          assertTrue(patronInformationResponse.getFineItems().isEmpty());
          assertNotNull(patronInformationResponse.getRecallItems());
          assertTrue(patronInformationResponse.getRecallItems().isEmpty());
          assertNotNull(patronInformationResponse.getUnavailableHoldItems());
          assertTrue(patronInformationResponse.getUnavailableHoldItems().isEmpty());
          assertNull(patronInformationResponse.getHomeAddress());
          assertEquals("earnestine@sipes-stokes-and-durgan.so",
              patronInformationResponse.getEmailAddress());
          assertEquals("(916)599-0326",
              patronInformationResponse.getHomePhoneNumber());
          assertEquals(expectedScreenMessage, patronInformationResponse.getScreenMessage());
          assertNull(patronInformationResponse.getPrintLine());

          testContext.completeNow();
        })));
  }

  @Test
  void canPatronEndSession(
      Vertx vertx,
      VertxTestContext testContext,
      @Mock UsersRepository mockUsersRepository,
      @Mock CirculationRepository mockCirculationRepository,
      @Mock FeeFinesRepository mockFeeFinesRepository,
      @Mock PasswordVerifier mockPasswordVerifier) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String patronIdentifier = "1234567890";
    final EndPatronSession endPatronSession = EndPatronSession.builder()
        .transactionDate(OffsetDateTime.now())
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .terminalPassword("1234")
        .patronPassword("0989")
        .build();

    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("0989"), any()))
        .thenReturn(Future.succeededFuture(PatronPasswordVerificationRecords.builder().build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();

    final PatronRepository patronRepository = new PatronRepository(mockUsersRepository,
        mockCirculationRepository, mockFeeFinesRepository, mockPasswordVerifier, clock);
    patronRepository.performEndPatronSessionCommand(endPatronSession, sessionData).onComplete(
        testContext.succeeding(endSessionResponse -> testContext.verify(() -> {
          assertNotNull(endSessionResponse);
          assertTrue(endSessionResponse.getEndSession());
          assertEquals(OffsetDateTime.now(clock), endSessionResponse.getTransactionDate());
          assertEquals("diku", endSessionResponse.getInstitutionId());
          assertEquals(patronIdentifier, endSessionResponse.getPatronIdentifier());
          assertNull(endSessionResponse.getScreenMessage());
          assertNull(endSessionResponse.getPrintLine());

          testContext.completeNow();
        })));
  }

  @Test
  void canPatronEndSessionRequirePassword(
      Vertx vertx,
      VertxTestContext testContext,
      @Mock UsersRepository mockUsersRepository,
      @Mock CirculationRepository mockCirculationRepository,
      @Mock FeeFinesRepository mockFeeFinesRepository,
      @Mock PasswordVerifier mockPasswordVerifier) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String patronIdentifier = "1234567890";
    final EndPatronSession endPatronSession = EndPatronSession.builder()
        .transactionDate(OffsetDateTime.now())
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .terminalPassword("1234")
        .patronPassword("0989")
        .build();

    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("0989"), any()))
        .thenReturn(Future.succeededFuture(
            PatronPasswordVerificationRecords.builder().passwordVerified(TRUE).build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();
    sessionData.setPatronPasswordVerificationRequired(true);

    final PatronRepository patronRepository = new PatronRepository(mockUsersRepository,
        mockCirculationRepository, mockFeeFinesRepository, mockPasswordVerifier, clock);
    patronRepository.performEndPatronSessionCommand(endPatronSession, sessionData).onComplete(
        testContext.succeeding(endSessionResponse -> testContext.verify(() -> {
          assertNotNull(endSessionResponse);
          assertTrue(endSessionResponse.getEndSession());
          assertEquals(OffsetDateTime.now(clock), endSessionResponse.getTransactionDate());
          assertEquals("diku", endSessionResponse.getInstitutionId());
          assertEquals(patronIdentifier, endSessionResponse.getPatronIdentifier());
          assertNull(endSessionResponse.getScreenMessage());
          assertNull(endSessionResponse.getPrintLine());

          testContext.completeNow();
        })));
  }

  @Test
  void cannotPatronEndSessionRequirePasswordWithBadPassword(
      Vertx vertx,
      VertxTestContext testContext,
      @Mock UsersRepository mockUsersRepository,
      @Mock CirculationRepository mockCirculationRepository,
      @Mock FeeFinesRepository mockFeeFinesRepository,
      @Mock PasswordVerifier mockPasswordVerifier) {
    final Clock clock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
    final String patronIdentifier = "1234567890";
    final EndPatronSession endPatronSession = EndPatronSession.builder()
        .transactionDate(OffsetDateTime.now())
        .institutionId("diku")
        .patronIdentifier(patronIdentifier)
        .terminalPassword("1234")
        .patronPassword("0989")
        .build();

    when(mockPasswordVerifier.verifyPatronPassword(eq(patronIdentifier), eq("0989"), any()))
        .thenReturn(Future.succeededFuture(
            PatronPasswordVerificationRecords.builder()
              .passwordVerified(FALSE)
              .errorMessages(Collections.singletonList("Password does not match"))
              .build()));

    final SessionData sessionData = TestUtils.getMockedSessionData();
    sessionData.setPatronPasswordVerificationRequired(true);

    final PatronRepository patronRepository = new PatronRepository(mockUsersRepository,
        mockCirculationRepository, mockFeeFinesRepository, mockPasswordVerifier, clock);
    patronRepository.performEndPatronSessionCommand(endPatronSession, sessionData).onComplete(
        testContext.succeeding(endSessionResponse -> testContext.verify(() -> {
          assertNotNull(endSessionResponse);
          assertFalse(endSessionResponse.getEndSession());
          assertEquals(OffsetDateTime.now(clock), endSessionResponse.getTransactionDate());
          assertEquals("diku", endSessionResponse.getInstitutionId());
          assertEquals(patronIdentifier, endSessionResponse.getPatronIdentifier());
          assertNull(endSessionResponse.getScreenMessage());
          assertNull(endSessionResponse.getPrintLine());

          testContext.completeNow();
        })));
  }

  private static JsonObject getManualBlockJsonObject(boolean borrowing, boolean renewals,
      boolean requests) {
    return new JsonObject("{\n"
        + "  \"manualblocks\" : [ {\n"
        + "    \"type\" : \"Manual\",\n"
        + "    \"desc\" : \"test block\",\n"
        + "    \"staffInformation\" : \"Collect money\",\n"
        + "    \"patronMessage\" : \"Pay your fines!\",\n"
        + "    \"expirationDate\" : \"2019-06-22T04:00:00.000+0000\",\n"
        + "    \"borrowing\" : " + borrowing + ",\n"
        + "    \"renewals\" : " + renewals + ",\n"
        + "    \"requests\" : " + requests + ",\n"
        + "    \"userId\" : \"a23eac4b-955e-451c-b4ff-6ec2f5e63e23\",\n"
        + "    \"metadata\" : {\n"
        + "      \"createdDate\" : \"2019-05-16T19:43:47.262+0000\",\n"
        + "      \"createdByUserId\" : \"299318d2-51c0-5ddb-82e9-084e02ec756e\",\n"
        + "      \"updatedDate\" : \"2019-05-16T19:43:47.262+0000\",\n"
        + "      \"updatedByUserId\" : \"299318d2-51c0-5ddb-82e9-084e02ec756e\"\n"
        + "    },\n"
        + "    \"id\" : \"43ec8e43-4a78-45b0-a46c-88426346d3df\"\n"
        + "  } ],\n"
        + "  \"totalRecords\" : 1\n"
        + "}");
  }

  private static Stream<Arguments> provideManualBlocks() {
    return Stream.of(
        Arguments.of(getManualBlockJsonObject(true, true, true),
            EnumSet.allOf(PatronStatus.class),
            Collections.singletonList(MESSAGE_BLOCKED_PATRON)),
        Arguments.of(getManualBlockJsonObject(true, true, false),
            EnumSet.allOf(PatronStatus.class),
            Collections.singletonList(MESSAGE_BLOCKED_PATRON)),
        Arguments.of(getManualBlockJsonObject(false, true, true),
            EnumSet.of(RENEWAL_PRIVILEGES_DENIED,
                HOLD_PRIVILEGES_DENIED,
                RECALL_PRIVILEGES_DENIED),
            Collections.singletonList(MESSAGE_BLOCKED_PATRON)),
        Arguments.of(getManualBlockJsonObject(true, false, true),
            EnumSet.allOf(PatronStatus.class),
            Collections.singletonList(MESSAGE_BLOCKED_PATRON)),
        Arguments.of(getManualBlockJsonObject(true, false, false),
            EnumSet.allOf(PatronStatus.class),
            Collections.singletonList(MESSAGE_BLOCKED_PATRON)),
        Arguments.of(getManualBlockJsonObject(false, true, false),
            EnumSet.of(RENEWAL_PRIVILEGES_DENIED),
            Collections.singletonList(MESSAGE_BLOCKED_PATRON)),
        Arguments.of(getManualBlockJsonObject(false, false, true),
            EnumSet.of(HOLD_PRIVILEGES_DENIED,
                RECALL_PRIVILEGES_DENIED),
            Collections.singletonList(MESSAGE_BLOCKED_PATRON)),
        Arguments.of(getManualBlockJsonObject(false, false, false),
            EnumSet.noneOf(PatronStatus.class),
            null));
  }
}
