package org.folio.edge.sip2.repositories;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.folio.edge.sip2.domain.messages.enumerations.Language.UNKNOWN;
import static org.folio.edge.sip2.domain.messages.enumerations.PatronStatus.HOLD_PRIVILEGES_DENIED;
import static org.folio.edge.sip2.domain.messages.enumerations.PatronStatus.RECALL_PRIVILEGES_DENIED;
import static org.folio.edge.sip2.domain.messages.enumerations.PatronStatus.RENEWAL_PRIVILEGES_DENIED;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.CHARGED_ITEMS;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.EXTENDED_FEES;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.FINE_ITEMS;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.HOLD_ITEMS;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.OVERDUE_ITEMS;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.RECALL_ITEMS;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.UNAVAILABLE_HOLDS;
import static org.folio.edge.sip2.utils.JsonUtils.getChildString;
import static org.folio.edge.sip2.utils.Utils.DEFAULT_USER_LOANS_LIMIT;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.folio.edge.sip2.domain.messages.PatronAccountInfo;
import org.folio.edge.sip2.domain.messages.enumerations.CurrencyType;
import org.folio.edge.sip2.domain.messages.enumerations.PatronStatus;
import org.folio.edge.sip2.domain.messages.requests.EndPatronSession;
import org.folio.edge.sip2.domain.messages.requests.PatronInformation;
import org.folio.edge.sip2.domain.messages.requests.PatronStatusRequest;
import org.folio.edge.sip2.domain.messages.responses.EndSessionResponse;
import org.folio.edge.sip2.domain.messages.responses.PatronInformationResponse;
import org.folio.edge.sip2.domain.messages.responses.PatronInformationResponse.PatronInformationResponseBuilder;
import org.folio.edge.sip2.domain.messages.responses.PatronStatusResponse;
import org.folio.edge.sip2.domain.messages.responses.PatronStatusResponse.PatronStatusResponseBuilder;
import org.folio.edge.sip2.repositories.domain.Address;
import org.folio.edge.sip2.repositories.domain.ExtendedUser;
import org.folio.edge.sip2.repositories.domain.PatronPasswordVerificationRecords;
import org.folio.edge.sip2.repositories.domain.Personal;
import org.folio.edge.sip2.repositories.domain.User;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.Sip2LogAdapter;
import org.folio.okapi.common.refreshtoken.client.ClientException;

/**
 * Provides interaction with the patron required services. This repository is a go-between for
 * patron related handlers and the services required to retrieve the data.
 *
 * @author mreno-EBSCO
 *
 */
public class PatronRepository {
  private static final String FIELD_ACCOUNTS = "accounts";
  private static final String FIELD_REMAINING = "remaining";
  private static final String FIELD_REQUESTS = "requests";
  private static final String FIELD_STATUS = "status";
  private static final String FIELD_TITLE = "title";
  private static final String FIELD_TOTAL_RECORDS = "totalRecords";
  private static final String FIELD_ITEM = "item";
  private static final String FIELD_LOANS = "loans";
  private static final String FIELD_BARCODE = "barcode";
  private static final Sip2LogAdapter log = Sip2LogAdapter.getLogger(PatronRepository.class);
  // These really should come from FOLIO
  static final String MESSAGE_INVALID_PATRON =
      "Your library card number cannot be located. Please see a staff member for assistance.";
  static final String MESSAGE_BLOCKED_PATRON =
      "There are unresolved issues with your account. Please see a staff member for assistance.";
  static final String NULL_CLOCK_MSG = "Clock cannot be null";
  static final String NULL_CIRC_REPO_MSG = "Circulation repository cannot be null";
  static final String NULL_FEE_REPO_MSG = "FeeFines repository cannot be null";
  static final String NULL_PASS_VERIFY_MSG = "Password verifier cannot be null";
  static final String NULL_PATRON_INFO_MSG = "PatronInformation cannot be null";
  static final String NULL_SESSION_DATA_MSG = "SessionData cannot be null";
  static final String NULL_END_PATRON_SESSION_MSG = "EndPatronSession cannot be null";
  static final String NULL_PATRON_STATUS_MSG = "PatronStatus cannot be null";
  static final String NULL_USER_REPO_MSG = "Users repository cannot be null";

  private final CirculationRepository circulationRepository;
  private final FeeFinesRepository feeFinesRepository;
  private final PasswordVerifier passwordVerifier;
  private final UsersRepository usersRepository;
  private final Clock clock;

  @Inject
  PatronRepository(UsersRepository usersRepository, CirculationRepository circulationRepository,
      FeeFinesRepository feeFinesRepository, PasswordVerifier passwordVerifier, Clock clock) {
    this.circulationRepository = Objects.requireNonNull(circulationRepository, NULL_CIRC_REPO_MSG);
    this.feeFinesRepository = Objects.requireNonNull(feeFinesRepository, NULL_FEE_REPO_MSG);
    this.passwordVerifier = Objects.requireNonNull(passwordVerifier, NULL_PASS_VERIFY_MSG);
    this.usersRepository = Objects.requireNonNull(usersRepository, NULL_USER_REPO_MSG);
    this.clock = Objects.requireNonNull(clock, NULL_CLOCK_MSG);
  }

  /**
   * Perform patron information.
   *
   * @param patronInformation the patron information domain object
   * @return the patron information response domain object
   */
  public Future<PatronInformationResponse> performPatronInformationCommand(
      PatronInformation patronInformation,
      SessionData sessionData) {
    Objects.requireNonNull(patronInformation, NULL_PATRON_INFO_MSG);
    Objects.requireNonNull(sessionData, NULL_SESSION_DATA_MSG);
    log.debug(sessionData, "performPatronInformationCommand patronIdentifier:{}",
        patronInformation.getPatronIdentifier());

    final String patronIdentifier = patronInformation.getPatronIdentifier();
    final String patronPassword = patronInformation.getPatronPassword();

    Future<PatronPasswordVerificationRecords> passwordVerificationFuture
        = verifyPinOrPassword(patronIdentifier, patronPassword, sessionData);
    log.debug(sessionData, "Verification return value is {}", passwordVerificationFuture);
    return passwordVerificationFuture
        .onFailure(throwable -> {
          if (throwable instanceof ClientException) {
            sessionData.setErrorResponseMessage(invalidPatron(patronInformation, FALSE).result());
          }
        })
        .compose(verification -> {
          log.debug(sessionData, "Password verification result is {}",
              verification.getPasswordVerified());
          log.debug(sessionData, "isPatronPasswordVerificationRequest == {}",
              sessionData.isPatronPasswordVerificationRequired());

          final Future<ExtendedUser> extendedUserFuture
              = Future.succeededFuture(verification.getExtendedUser());
          return extendedUserFuture.compose(extendedUser -> {
            User user = extendedUser != null ? extendedUser.getUser() : null;
            if (user == null || FALSE.equals(user.getActive())) {
              log.debug(sessionData, "User is null or inactive");
              return invalidPatron(patronInformation, null);
            } else {
              final String userId = user.getId();
              if (userId == null) {
                // Something is really messed up if the id is missing
                log.error(sessionData, "User with patron identifier {} is missing the \"id\" field",
                    patronIdentifier);
                return invalidPatron(patronInformation, verification.getPasswordVerified());
              }
              log.debug(sessionData, "Patron information valid");

              return validPatron(extendedUser, patronInformation, sessionData,
                  verification.getPasswordVerified());
            }
          });
        });
  }

  /**
   * Perform patron information.
   *
   * @param patronStatus the patron information domain object
   * @return the patron information response domain object
   */
  public Future<PatronStatusResponse> performPatronStatusCommand(
      PatronStatusRequest patronStatus,
      SessionData sessionData) {
    Objects.requireNonNull(patronStatus, NULL_PATRON_STATUS_MSG);
    Objects.requireNonNull(sessionData, NULL_SESSION_DATA_MSG);

    final String patronIdentifier = patronStatus.getPatronIdentifier();
    final String patronPassword = patronStatus.getPatronPassword();
    log.debug(sessionData, "IsPatronVerificationRequired: {}",
        sessionData.isPatronPasswordVerificationRequired());

    return verifyPinOrPassword(patronIdentifier, patronPassword, sessionData)
      .onFailure(throwable -> {
        if (throwable instanceof ClientException) {
          sessionData.setErrorResponseMessage(invalidPatron(patronStatus, FALSE).result());
        }
      })
        .compose(verification -> {
          if (FALSE.equals(verification.getPasswordVerified())) {
            return invalidPatron(patronStatus, FALSE);
          }
          //getUser is add in PasswordVerifier
          final Future<ExtendedUser> extendedUserFuture
              = Future.succeededFuture(verification.getExtendedUser());

          return extendedUserFuture.compose(extendedUser -> {
            User user = extendedUser != null ? extendedUser.getUser() : null;
            if (user == null || FALSE.equals(user.getActive())) {
              return invalidPatron(patronStatus, null);
            } else {
              final String userId = user.getId();
              if (userId == null) {
                // Something is really messed up if the id is missing
                log.error(sessionData, "User with patron identifier {} is missing the \"id\" field",
                    patronIdentifier);
                return invalidPatron(patronStatus, verification.getPasswordVerified());
              }

              return validPatron(extendedUser, patronStatus, sessionData,
                  verification.getPasswordVerified());
            }
          });
        });
  }

  /**
   * Perform End Patron Session.
   * If PIN verification is required, then we will ensure that the patron password (PIN) is
   * validated before allowing a successful return.
   *
   * @param endPatronSession command data
   * @param sessionData session data
   * @return the end session response indicating whether or not the session was ended
   */
  public Future<EndSessionResponse> performEndPatronSessionCommand(
      EndPatronSession endPatronSession,
      SessionData sessionData) {
    Objects.requireNonNull(endPatronSession, NULL_END_PATRON_SESSION_MSG);
    Objects.requireNonNull(sessionData, NULL_SESSION_DATA_MSG);

    final String patronIdentifier = endPatronSession.getPatronIdentifier();
    final String patronPassword = endPatronSession.getPatronPassword();

    return verifyPinOrPassword(patronIdentifier, patronPassword, sessionData)
      .onFailure(throwable -> {
        if (throwable instanceof ClientException) {
          sessionData.setErrorResponseMessage(EndSessionResponse.builder()
              .endSession(FALSE)
              .transactionDate(OffsetDateTime.now(clock))
              .institutionId(endPatronSession.getInstitutionId())
              .patronIdentifier(endPatronSession.getPatronIdentifier())
              .build());
        }
      })
        .map(verification -> EndSessionResponse.builder()
          .endSession(!FALSE.equals(verification.getPasswordVerified()))
          .transactionDate(OffsetDateTime.now(clock))
          .institutionId(endPatronSession.getInstitutionId())
          .patronIdentifier(endPatronSession.getPatronIdentifier())
          .build());
  }

  private Future<PatronInformationResponse> validPatron(ExtendedUser extendedUser,
      PatronInformation patronInformation, SessionData sessionData, final Boolean validPassword) {
    log.debug(sessionData, "validPatron called for extended user {}", extendedUser);
    final String userId = extendedUser.getUser().getId();
    final Personal personal = extendedUser.getUser().getPersonal();
    if (personal != null) {
      log.debug(sessionData, "validPatron userId:{} firstName:{} lastName:{}",
          userId,personal.getFirstName(),personal.getLastName());
    }
    // Now that we have a valid patron, we can retrieve data from circulation
    final PatronInformationResponseBuilder builder = PatronInformationResponse.builder();
    // Store patron data in the builder
    addPersonalData(personal, patronInformation.getPatronIdentifier(), builder);
    final Integer startItem = patronInformation.getStartItem();
    final Integer endItem = patronInformation.getEndItem();
    // Get manual blocks data to build patron status
    final Future<PatronInformationResponseBuilder> manualBlocksFuture = feeFinesRepository
        .getManualBlocksByUserId(userId, sessionData)
        .map(blocks -> buildPatronStatus(blocks, builder));
    // Add fine count
    final Future<PatronInformationResponseBuilder> accountFuture = feeFinesRepository
        .getAccountDataByUserId(userId, sessionData)
        .map(accounts -> {
          totalAmount(sessionData, accounts, builder);
          populateFinesCount(accounts, builder);
          addFineItems(accounts, patronInformation.getSummary() == FINE_ITEMS, builder);
          return addExtendedAccountInfo(accounts,
              patronInformation.getSummary() == EXTENDED_FEES, builder);
        });

    // Add charged count
    final Future<PatronInformationResponseBuilder> loansFuture = circulationRepository
        .getLoansByUserId(userId, null, DEFAULT_USER_LOANS_LIMIT, sessionData)
        .map(loans -> {
          populateChargedCount(loans, builder);
          return addCharged(loans, patronInformation.getSummary() == CHARGED_ITEMS, builder);
        });

    // Get holds data (count and items) and store it in the builder
    final Future<PatronInformationResponseBuilder> holdsFuture = circulationRepository
        .getRequestsByUserId(userId, "Hold", startItem, endItem, sessionData).map(
            holds -> {
              addUnavailableHolds(holds, patronInformation.getSummary() == UNAVAILABLE_HOLDS,
                  builder);
              return addHolds(holds, patronInformation.getSummary() == HOLD_ITEMS, builder);
            });
    // Get overdue loans data (count and items) and store it in the builder
    // Due date needs to be UTC since it is being used in CQL for time comparison in the DB.
    final Future<PatronInformationResponseBuilder> overdueFuture =
        circulationRepository.getOverdueLoansByUserId(userId, OffsetDateTime.now(clock),
            startItem, endItem, sessionData).map(
                overdues -> addOverdueItems(overdues,
                    patronInformation.getSummary() == OVERDUE_ITEMS, builder));
    // Get recalled items data (count and items) and store it in the builder
    final Future<PatronInformationResponseBuilder> recallsFuture =
        getRecalls(userId, sessionData).compose(recalls -> addRecalls(recalls, startItem, endItem,
            patronInformation.getSummary() == RECALL_ITEMS, builder));
    // When all operations complete, build and return the final PatronInformationResponse
    return Future.all(manualBlocksFuture, accountFuture, holdsFuture,
        overdueFuture, recallsFuture, loansFuture)
        .map(result -> {
          log.info(sessionData, "validPatron language:{} institutionId:{}",
              patronInformation.getLanguage(),patronInformation.getInstitutionId());
          builder
              // Get tenant language from config along with the timezone
              .borrowerType(extendedUser.getPatronGroup().getGroup())
              .borrowerTypeDescription(extendedUser.getPatronGroup().getDesc())
              .language(patronInformation.getLanguage())
              .transactionDate(OffsetDateTime.now(clock))
              .unavailableHoldsCount(null)
              .institutionId(patronInformation.getInstitutionId())
              .patronIdentifier(patronInformation.getPatronIdentifier())
              .validPatron(TRUE)
              .currencyType(CurrencyType.fromStringSafe(sessionData.getCurrency()));
          if (sessionData.isAlwaysCheckPatronPassword()
              || sessionData.isPatronPasswordVerificationRequired()) {
            builder.validPatronPassword(validPassword);
          }
          return builder.build();
          }
        );
  }

  private Future<PatronStatusResponse> validPatron(ExtendedUser extendedUser,
          PatronStatusRequest patronStatus, SessionData sessionData, Boolean validPassword) {
    // Now that we have a valid patron, we can retrieve data from circulation
    final PatronStatusResponseBuilder builder = PatronStatusResponse.builder();
    final String userId = extendedUser.getUser().getId();
    final Personal personal = extendedUser.getUser().getPersonal();
    // Store patron data in the builder
    final String personalName = getPatronPersonalName(personal, patronStatus.getPatronIdentifier());
    builder.personalName(personalName);
    log.debug(sessionData, "Populating borrower info with patron group {}",
        extendedUser.getPatronGroup() != null ? extendedUser.getPatronGroup().getId()
        : null);
    // When all operations complete, build and return the final PatronInformationResponse

    final Future<PatronStatusResponseBuilder> getFeeAmountFuture = feeFinesRepository
        .getFeeAmountByUserId(userId, sessionData)
        .map(accounts -> totalAmount(sessionData, accounts, builder));

    return getFeeAmountFuture.map(result -> builder
            .patronStatus(EnumSet.noneOf(PatronStatus.class))
            .language(patronStatus.getLanguage())
            .transactionDate(OffsetDateTime.now(clock))
            .institutionId(patronStatus.getInstitutionId())
            .patronIdentifier(patronStatus.getPatronIdentifier())
            .validPatron(TRUE)
            .validPatronPassword(validPassword)
            .build()

    );
  }





  private PatronInformationResponseBuilder populateChargedCount(JsonObject loans,
                                      PatronInformationResponseBuilder builder) {
    final int chargedItemsCount;
    if (loans != null) {
      // Get minimum of total loan count & 9999
      chargedItemsCount = Math.min(getTotalRecords(loans), 9999);
    } else {
      chargedItemsCount = 0;
    }
    return builder.chargedItemsCount(chargedItemsCount);
  }

  private PatronInformationResponseBuilder populateFinesCount(JsonObject accounts,
                                                PatronInformationResponseBuilder builder) {
    final int fineItemsCount;
    if (accounts != null) {
      // Get minimum of total fine count & 9999
      fineItemsCount = Math.min(getTotalRecords(accounts), 9999);
    } else {
      fineItemsCount = 0;
    }
    return builder.fineItemsCount(fineItemsCount);
  }


  private PatronInformationResponseBuilder totalAmount(
      SessionData sessionData, JsonObject jo,
      PatronInformationResponseBuilder builder) {
    Float total;
    if (jo != null) {
      final JsonArray arr = jo.getJsonArray(FIELD_ACCOUNTS);
      total = getTotalRemaining(arr);
      log.debug(sessionData, "Total is {}", total);
      return builder.feeAmount(total.toString());
    }
    return null;
  }

  private PatronStatusResponseBuilder totalAmount(
      SessionData sessionData, JsonObject jo,
      PatronStatusResponseBuilder builder) {

    final JsonArray arr = jo.getJsonArray(FIELD_ACCOUNTS);
    Float total = getTotalRemaining(arr);
    log.debug(sessionData, "Total is {}", total);
    return builder.feeAmount(total.toString());
  }

  protected static Float getTotalRemaining(JsonArray accounts) {
    BigDecimal total = BigDecimal.ZERO;
    for (int i = 0; i < accounts.size(); i++) {
      BigDecimal bdValue = BigDecimal.valueOf(accounts.getJsonObject(i)
          .getFloat(FIELD_REMAINING));
      total = total.add(bdValue);
    }
    return total.round(MathContext.DECIMAL32).floatValue();
  }



  private Future<PatronInformationResponse> invalidPatron(
      PatronInformation patronInformation,
      Boolean validPassword) {
    return Future.succeededFuture(PatronInformationResponse.builder()
        .patronStatus(EnumSet.allOf(PatronStatus.class))
        .language(UNKNOWN)
        .transactionDate(OffsetDateTime.now(clock))
        .holdItemsCount(Integer.valueOf(0))
        .overdueItemsCount(Integer.valueOf(0))
        .chargedItemsCount(Integer.valueOf(0))
        .fineItemsCount(Integer.valueOf(0))
        .recallItemsCount(Integer.valueOf(0))
        .unavailableHoldsCount(Integer.valueOf(0))
        .institutionId(patronInformation.getInstitutionId())
        .patronIdentifier(patronInformation.getPatronIdentifier())
        .personalName(patronInformation.getPatronIdentifier()) // required, using patron id for now
        .validPatron(FALSE)
        .validPatronPassword(validPassword)
        .screenMessage(Collections.singletonList(MESSAGE_INVALID_PATRON))
        .build());
  }

  private Future<PatronStatusResponse> invalidPatron(
      PatronStatusRequest patronStatus,
      Boolean validPassword) {
    return Future.succeededFuture(PatronStatusResponse.builder()
        .patronStatus(EnumSet.allOf(PatronStatus.class))
        .language(UNKNOWN)
        .transactionDate(OffsetDateTime.now(clock))
        .institutionId(patronStatus.getInstitutionId())
        .patronIdentifier(patronStatus.getPatronIdentifier())
        .personalName(patronStatus.getPatronIdentifier()) // required, using patron id for now
        .validPatron(FALSE)
        .validPatronPassword(validPassword)
        .screenMessage(Collections.singletonList(MESSAGE_INVALID_PATRON))
        .build());
  }


  private PatronInformationResponseBuilder buildPatronStatus(JsonObject blocks,
      PatronInformationResponseBuilder builder) {
    final EnumSet<PatronStatus> patronStatus = EnumSet.noneOf(PatronStatus.class);

    if (blocks != null && getTotalRecords(blocks) > 0) {
      blocks.getJsonArray("manualblocks", new JsonArray()).stream()
          .map(o -> (JsonObject) o)
          .forEach(jo -> {
            if (jo.getBoolean("borrowing", FALSE)) {
              // Block everything
              patronStatus.addAll(EnumSet.allOf(PatronStatus.class));
            } else {
              if (jo.getBoolean("renewals", FALSE)) {
                patronStatus.add(RENEWAL_PRIVILEGES_DENIED);
              }
              if (jo.getBoolean(FIELD_REQUESTS, FALSE)) {
                patronStatus.add(HOLD_PRIVILEGES_DENIED);
                patronStatus.add(RECALL_PRIVILEGES_DENIED);
              }
            }
          });
    }

    if (!patronStatus.isEmpty()) {
      builder.screenMessage(Collections.singletonList(MESSAGE_BLOCKED_PATRON));
    }

    return builder.patronStatus(patronStatus);
  }

  private int getTotalRecords(JsonObject records) {
    return records.getInteger(FIELD_TOTAL_RECORDS, Integer.valueOf(0)).intValue();
  }

  private PatronInformationResponseBuilder addPersonalData(Personal personal,
                                                     String patronIdentifier,
                                                     PatronInformationResponseBuilder builder) {
    final String personalName = getPatronPersonalName(personal, patronIdentifier);
    final String homeAddress = getPatronHomeAddress(personal);
    final String emailAddress = personal == null ? null : personal.getEmail();
    final String homePhoneNumber = personal == null ? null : personal.getPhone();

    return builder.personalName(personalName)
        .homeAddress(homeAddress)
        .emailAddress(emailAddress)
        .homePhoneNumber(homePhoneNumber);
  }

  private PatronInformationResponseBuilder addHolds(JsonObject holds, boolean details,
      PatronInformationResponseBuilder builder) {
    final int holdItemsCount;
    final List<String> holdItems;

    if (holds != null) {
      holdItemsCount = Math.min(getTotalRecords(holds), 9999);
      if (details) {
        holdItems = getHoldItems(holds);
      } else {
        holdItems = null;
      }
    } else {
      holdItemsCount = 0;
      holdItems = null;
    }

    return builder.holdItemsCount(Integer.valueOf(holdItemsCount)).holdItems(holdItems);
  }

  private PatronInformationResponseBuilder addUnavailableHolds(JsonObject holds,
      boolean details, PatronInformationResponseBuilder builder) {
    final List<String> unavailableHoldItems;

    if (holds != null) {
      if (details) {
        unavailableHoldItems = getUnavailableHoldItems(holds);
      } else {
        unavailableHoldItems = null;
      }
    } else {
      unavailableHoldItems = null;
    }
    return builder.unavailableHoldItems(unavailableHoldItems);
  }

  private PatronInformationResponseBuilder addCharged(JsonObject loans, boolean details,
      PatronInformationResponseBuilder builder) {
    final List<String> chargedItems;

    if (loans != null) {
      if (details) {
        chargedItems = getLoanItems(loans);
      } else {
        chargedItems = null;
      }
    } else {
      chargedItems = null;
    }

    return builder.chargedItems(chargedItems);
  }

  private PatronInformationResponseBuilder addFineItems(JsonObject accounts, boolean details,
      PatronInformationResponseBuilder builder) {
    final List<String> fineItems;

    if (accounts != null) {
      if (details) {
        fineItems = getFineItems(accounts);
      } else {
        fineItems = null;
      }
    } else {
      fineItems = null;
    }
    return builder.fineItems(fineItems);
  }

  private PatronInformationResponseBuilder addOverdueItems(JsonObject overdues, boolean details,
      PatronInformationResponseBuilder builder) {
    final int overdueItemsCount;
    final List<String> overdueItems;

    if (overdues != null) {
      overdueItemsCount = Math.min(getTotalRecords(overdues), 9999);
      if (details) {
        overdueItems = getLoanItems(overdues);
      } else {
        overdueItems = null;
      }
    } else {
      overdueItemsCount = 0;
      overdueItems = null;
    }

    return builder.overdueItemsCount(overdueItemsCount).overdueItems(overdueItems);
  }

  @SuppressWarnings("rawtypes")
  private Future<PatronInformationResponseBuilder> addRecalls(List<Future<JsonObject>> recalls,
      Integer startItem, Integer endItem, boolean details,
      PatronInformationResponseBuilder builder) {
    return Future.all(new ArrayList<>(recalls)).map(result -> {
      final int recallItemsCount = Math.min(countRecallItems(recalls), 9999);
      final List<String> recallItems;

      if (details) {
        recallItems = getRecallItems(recalls, startItem, endItem);
      } else {
        recallItems = null;
      }

      return builder.recallItemsCount(recallItemsCount).recallItems(recallItems);
    });
  }

  private PatronInformationResponseBuilder addExtendedAccountInfo(JsonObject accounts,
      boolean details, PatronInformationResponseBuilder builder) {
    List<PatronAccountInfo> patronAccountList;
    if (accounts != null) {
      if (details) {
        patronAccountList = getPatronAccountList(accounts);
      } else {
        patronAccountList = null;
      }
    } else {
      patronAccountList = null;
    }
    return builder.patronAccountList(patronAccountList);
  }


  private String getPatronPersonalName(Personal personal, String defaultPersonalName) {
    if (personal != null) {
      return Stream.of(personal.getFirstName(), personal.getMiddleName(), personal.getLastName())
          .filter(Objects::nonNull)
          .collect(Collectors.collectingAndThen(Collectors.joining(" "),
              s -> s.isEmpty() ? defaultPersonalName : s));
    }
    return defaultPersonalName;
  }

  private String getPatronHomeAddress(Personal personal) {
    if (personal != null) {
      return Optional.ofNullable(personal.getAddresses())
          .map(addresses -> {
            // For now, we will do the following:
            // 1. if the address list > 0, pick the "primaryAddress"
            // 2. if no "primaryAddress", pick the first address
            // 3. no addresses, return null (home address is an optional field)
            // In the future, we should use the UUID of the home address type to select the home
            // address and if no match, then return null so as to not expose other addresses.
            // This can be cleaned up a bit when we get to Java 11.
            Optional<String> addressString = addresses.stream()
                .filter(address -> TRUE.equals(address.getPrimaryAddress()))
                .findFirst()
                .map(Optional::of)
                .orElseGet(() -> addresses.stream()
                    .findFirst()
                    .map(Optional::of)
                    .orElse(Optional.empty()))
                .map(this::toHomeAddress);

            return addressString.orElse(null);
          })
          .orElse(null);
    }

    return null;
  }

  private List<String> getBarcodesForRequests(JsonArray items) {
    return getBarcodes(items, FIELD_ITEM);
  }

  private List<String> getBarcodesForLoans(JsonArray loans) {
    return getBarcodes(loans, FIELD_ITEM);
  }

  protected static List<String> getBarcodesForOpenAccounts(JsonArray accounts) {
    return accounts.stream()
      .map(o -> (JsonObject) o)
      .filter(jo -> "Open".equals(getChildString(jo, FIELD_STATUS, "name")))
      .filter(jo -> jo.getDouble("remaining") > 0.0)
      .map(jo -> jo.getString(FIELD_BARCODE))
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }

  private List<String> getBarcodes(JsonArray items, String childField) {
    return items.stream()
        .map(o -> (JsonObject) o)
        .map(jo -> getChildString(jo, childField, FIELD_BARCODE))
        .filter(Objects::nonNull)
        .toList();
  }

  private List<PatronAccountInfo> getPatronAccountList(
      JsonObject accountsJson) {
    List<PatronAccountInfo> accountList = new ArrayList<>();
    final JsonArray accountArray = accountsJson.getJsonArray(FIELD_ACCOUNTS);
    for (Object ob : accountArray) {
      JsonObject jo = (JsonObject)ob;
      PatronAccountInfo patronAccount = new PatronAccountInfo();
      String status = jo.getJsonObject("status") != null
          ? jo.getJsonObject("status").getString("name") : null;
      Double feeFineRemaining = jo.getNumber(FIELD_REMAINING) != null
          ? jo.getNumber(FIELD_REMAINING).doubleValue() : null;
      if (!"Open".equalsIgnoreCase(status) || (feeFineRemaining != null && feeFineRemaining <= 0)) {
        continue;
      }
      Double feeFineAmount = jo.getNumber("amount") != null
          ? jo.getNumber("amount").doubleValue() : null;
      patronAccount.setFeeFineAmount(feeFineAmount);
      patronAccount.setFeeFineRemaining(feeFineRemaining);
      patronAccount.setItemBarcode(jo.getString(FIELD_BARCODE));
      patronAccount.setId(jo.getString("id"));
      patronAccount.setFeeFineId(jo.getString("feeFineId"));
      patronAccount.setFeeFineType(jo.getString("feeFineType"));
      patronAccount.setItemTitle(jo.getString(FIELD_TITLE));
      String accountDate = getDateFromAccountJson(jo);
      patronAccount.setFeeCreationDate(accountDate != null
          ? OffsetDateTime.parse(accountDate) : null);
      accountList.add(patronAccount);
    }
    return accountList;
  }

  private String getDateFromAccountJson(JsonObject accountJson) {
    if (accountJson.getString("dateCreated") != null) {
      return accountJson.getString("dateCreated");
    }
    if (accountJson.getJsonObject("metadata") != null) {
      return accountJson.getJsonObject("metadata").getString("createdDate");
    }
    return null;
  }

  private List<String> getHoldItems(JsonObject requests) {
    // All items in the response are holds
    final JsonArray requestArray = requests.getJsonArray(FIELD_REQUESTS, new JsonArray());
    return getBarcodesForRequests(requestArray);
  }

  private List<String> getUnavailableHoldItems(JsonObject requests) {
    final JsonArray requestsArray = requests.getJsonArray(FIELD_REQUESTS, new JsonArray());
    return requestsArray.stream()
      .map(o -> (JsonObject) o)
      .filter(jo -> ("Closed - Unfilled".equals(jo.getString(FIELD_STATUS))
            || "Closed - Canceled".equals(jo.getString(FIELD_STATUS))))
      .map(jo -> getChildString(jo, "item", FIELD_BARCODE))
      .collect(Collectors.toList());
  }

  private List<String> getLoanItems(JsonObject loans) {
    final JsonArray loanArray = loans.getJsonArray(FIELD_LOANS, new JsonArray());
    return getBarcodesForLoans(loanArray);
  }

  private List<String> getFineItems(JsonObject accounts) {
    final JsonArray accountArray = accounts.getJsonArray(FIELD_ACCOUNTS, new JsonArray());
    return getBarcodesForOpenAccounts(accountArray);
  }

  private int countRecallItems(List<Future<JsonObject>> recallItems) {
    return (int) recallItems.stream()
        .map(Future::result)
        .filter(Objects::nonNull)
        .filter(jo -> getTotalRecords(jo) > 0)
        .count();
  }

  private List<String> getRecallItems(List<Future<JsonObject>> recallItems, Integer startItem,
      Integer endItem) {
    final int skip = startItem == null ? 0 : startItem.intValue() - 1;
    final int maxSize = endItem == null ? 9999 : endItem.intValue() - skip;
    return recallItems.stream()
        .map(Future::result)
        .filter(Objects::nonNull)
        .filter(jo -> getTotalRecords(jo) > 0)
        .map(jo -> jo.getJsonArray(FIELD_REQUESTS, new JsonArray()).stream().findAny()
            .map(o -> (JsonObject) o)
            .map(jsonObject -> getChildString(jsonObject, FIELD_ITEM, FIELD_BARCODE)))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .sorted(Comparator.naturalOrder())
        .skip(skip)
        .limit(maxSize)
        .collect(Collectors.toList());
  }

  private String toHomeAddress(Address address) {
    final Optional<String> addressLine1 = Optional.ofNullable(address.getAddressLine1());
    final Optional<String> addressLine2 = Optional.ofNullable(address.getAddressLine2());
    final Optional<String> city = Optional.ofNullable(address.getCity());
    final Optional<String> region = Optional.ofNullable(address.getRegion());
    final Optional<String> postalCode = Optional.ofNullable(address.getPostalCode());
    final Optional<String> countryId = Optional.ofNullable(address.getCountryId());

    // Not to sure about this format. It is one line and looks like:
    // 123 Fake Street, Anytown, CA 12345-1234 US
    // If all fields are not set, we return null
    return Stream.of(
        Optional.of(Stream.of(addressLine1, addressLine2, city, region)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(((Predicate<String>) String::isEmpty).negate())
            .collect(Collectors.joining(", "))),
        postalCode,
        countryId)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(((Predicate<String>) String::isEmpty).negate())
        .collect(Collectors.collectingAndThen(
            Collectors.joining(" "), result -> result.isEmpty() ? null : result));
  }

  private Future<List<Future<JsonObject>>> getRecalls(String userId, SessionData sessionData) {
    final Future<JsonObject> loansFuture =
        circulationRepository.getLoansByUserId(userId, null, DEFAULT_USER_LOANS_LIMIT, sessionData);

    return loansFuture.map(jo -> {
      final JsonArray loans = jo == null ? new JsonArray()
          : jo.getJsonArray(FIELD_LOANS, new JsonArray());
      return loans.stream()
          .map(o -> (JsonObject) o)
          .map(loan -> circulationRepository.getRequestsByItemId(loan.getString("itemId"),
              "Recall", null, null, sessionData))
          .collect(Collectors.toList());
    });
  }

  private Future<PatronPasswordVerificationRecords> verifyPinOrPassword(
      String patronIdentifier,
      String patronPassword,
      SessionData sessionData
  ) {
    if (sessionData.isUsePinForPatronVerification()) {
      return usersRepository.verifyPatronPin(patronIdentifier, patronPassword, sessionData);
    }
    return passwordVerifier.verifyPatronPassword(patronIdentifier, patronPassword, sessionData);
  }
}


