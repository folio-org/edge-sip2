package org.folio.edge.sip2.repositories;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.folio.edge.sip2.domain.messages.enumerations.Language.UNKNOWN;
import static org.folio.edge.sip2.domain.messages.enumerations.PatronStatus.HOLD_PRIVILEGES_DENIED;
import static org.folio.edge.sip2.domain.messages.enumerations.PatronStatus.RECALL_PRIVILEGES_DENIED;
import static org.folio.edge.sip2.domain.messages.enumerations.PatronStatus.RENEWAL_PRIVILEGES_DENIED;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.HOLD_ITEMS;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.OVERDUE_ITEMS;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.RECALL_ITEMS;
import static org.folio.edge.sip2.utils.JsonUtils.getChildString;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
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
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.folio.edge.sip2.repositories.domain.Personal;
import org.folio.edge.sip2.repositories.domain.User;
import org.folio.edge.sip2.session.SessionData;

/**
 * Provides interaction with the patron required services. This repository is a go-between for
 * patron related handlers and the services required to retrieve the data.
 *
 * @author mreno-EBSCO
 *
 */
public class PatronRepository {
  private static final String FIELD_REQUESTS = "requests";
  private static final String FIELD_TITLE = "title";
  private static final String FIELD_TOTAL_RECORDS = "totalRecords";
  private static final String FIELD_INSTANCE = "instance";
  private static final String FIELD_ITEM = "item";
  private static final Logger log = LogManager.getLogger();
  // These really should come from FOLIO
  static final String MESSAGE_INVALID_PATRON =
      "Your library card number cannot be located. Please see a staff member for assistance.";
  static final String MESSAGE_BLOCKED_PATRON =
      "There are unresolved issues with your account. Please see a staff member for assistance.";

  private final CirculationRepository circulationRepository;
  private final FeeFinesRepository feeFinesRepository;
  private final PasswordVerifier passwordVerifier;
  private final Clock clock;

  @Inject
  PatronRepository(UsersRepository usersRepository, CirculationRepository circulationRepository,
      FeeFinesRepository feeFinesRepository, PasswordVerifier passwordVerifier, Clock clock) {
    this.circulationRepository = Objects.requireNonNull(circulationRepository,
        "Circulation repository cannot be null");
    this.feeFinesRepository = Objects.requireNonNull(feeFinesRepository,
        "FeeFines repository cannot be null");
    this.passwordVerifier = Objects.requireNonNull(passwordVerifier,
        "Password verifier cannot be null");
    this.clock = Objects.requireNonNull(clock, "Clock cannot be null");
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
    Objects.requireNonNull(patronInformation, "patronInformation cannot be null");
    Objects.requireNonNull(sessionData, "sessionData cannot be null");
    log.debug("performPatronInformationCommand patronIdentifier:{}",
        patronInformation.getPatronIdentifier());

    final String patronIdentifier = patronInformation.getPatronIdentifier();
    final String patronPassword = patronInformation.getPatronPassword();

    return passwordVerifier.verifyPatronPassword(patronIdentifier, patronPassword, sessionData)
        .compose(verification -> {
          if (FALSE.equals(verification.getPasswordVerified())) {
            return invalidPatron(patronInformation, FALSE);
          }
          //getUser is add in PasswordVerifier
          final Future<User> userFuture = Future.succeededFuture(verification.getUser());

          return userFuture.compose(user -> {
            if (user == null || FALSE.equals(user.getActive())) {
              return invalidPatron(patronInformation, null);
            } else {
              final String userId = user.getId();
              if (userId == null) {
                // Something is really messed up if the id is missing
                log.error("User with patron identifier {} is missing the \"id\" field",
                    patronIdentifier);
                return invalidPatron(patronInformation, verification.getPasswordVerified());
              }

              return validPatron(userId, user.getPersonal(), patronInformation, sessionData,
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
    Objects.requireNonNull(patronStatus, "patronStatus cannot be null");
    Objects.requireNonNull(sessionData, "sessionData cannot be null");

    final String patronIdentifier = patronStatus.getPatronIdentifier();
    final String patronPassword = patronStatus.getPatronPassword();
    log.debug("IsPatronVerificationRequired just before forcing it: {}", 
        sessionData.isPatronPasswordVerificationRequired());
    sessionData.setPatronPasswordVerificationRequired(TRUE);

    return passwordVerifier.verifyPatronPassword(patronIdentifier, patronPassword, sessionData)
        .compose(verification -> {
          if (FALSE.equals(verification.getPasswordVerified())) {
            return invalidPatron(patronStatus, FALSE);
          }
          //getUser is add in PasswordVerifier
          final Future<User> userFuture = Future.succeededFuture(verification.getUser());

          return userFuture.compose(user -> {
            if (user == null || FALSE.equals(user.getActive())) {
              return invalidPatron(patronStatus, null);
            } else {
              final String userId = user.getId();
              if (userId == null) {
                // Something is really messed up if the id is missing
                log.error("User with patron identifier {} is missing the \"id\" field",
                    patronIdentifier);
                return invalidPatron(patronStatus, verification.getPasswordVerified());
              }

              return validPatron(userId, user.getPersonal(), patronStatus, sessionData,
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
    Objects.requireNonNull(endPatronSession, "endPatronSession cannot be null");
    Objects.requireNonNull(sessionData, "sessionData cannot be null");

    final String patronIdentifier = endPatronSession.getPatronIdentifier();
    final String patronPassword = endPatronSession.getPatronPassword();

    return passwordVerifier.verifyPatronPassword(patronIdentifier, patronPassword, sessionData)
        .map(verification -> EndSessionResponse.builder()
          .endSession(!FALSE.equals(verification.getPasswordVerified()))
          .transactionDate(OffsetDateTime.now(clock))
          .institutionId(endPatronSession.getInstitutionId())
          .patronIdentifier(endPatronSession.getPatronIdentifier())
          .build());
  }

  private Future<PatronInformationResponse> validPatron(String userId, Personal personal,
      PatronInformation patronInformation, SessionData sessionData, Boolean validPassword) {
    if (personal != null) {
      log.debug("validPatron userId:{} firstName:{} lastName:{}",
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
        .map(accounts -> populateFinesCount(accounts, builder));

    // Add charged count
    final Future<PatronInformationResponseBuilder> loansFuture = circulationRepository
        .getLoansByUserId(userId, null, null, sessionData)
        .map(loans -> populateChargedCount(loans, builder));

    // Get holds data (count and items) and store it in the builder
    final Future<PatronInformationResponseBuilder> holdsFuture = circulationRepository
        .getRequestsByUserId(userId, "Hold", startItem, endItem, sessionData).map(
            holds -> addHolds(holds, patronInformation.getSummary() == HOLD_ITEMS, builder));
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
    return CompositeFuture.all(manualBlocksFuture, accountFuture, holdsFuture,
        overdueFuture, recallsFuture, loansFuture)
        .map(result -> {
          log.info("validPatron language:{} institutionId:{}",
              patronInformation.getLanguage(),patronInformation.getInstitutionId());
          return builder
            // Get tenant language from config along with the timezone
            .language(patronInformation.getLanguage())
            .transactionDate(OffsetDateTime.now(clock))
            .unavailableHoldsCount(null)
            .institutionId(patronInformation.getInstitutionId())
            .patronIdentifier(patronInformation.getPatronIdentifier())
            .validPatron(TRUE)
            .validPatronPassword(validPassword)
            .build();
          }
        );
  }

  private Future<PatronStatusResponse> validPatron(String userId, Personal personal,
          PatronStatusRequest patronStatus, SessionData sessionData, Boolean validPassword) {
    // Now that we have a valid patron, we can retrieve data from circulation
    final PatronStatusResponseBuilder builder = PatronStatusResponse.builder();
    // Store patron data in the builder
    final String personalName = getPatronPersonalName(personal, patronStatus.getPatronIdentifier());
    builder.personalName(personalName);
    // When all operations complete, build and return the final PatronInformationResponse
    
    final Future<PatronStatusResponseBuilder> getFeeAmountFuture = feeFinesRepository
        .getFeeAmountByUserId(userId, sessionData)
        .map(accounts -> totalAmount(accounts, builder));
        
    return getFeeAmountFuture.map(result -> {
          return builder
            .patronStatus(EnumSet.allOf(PatronStatus.class))
            .language(patronStatus.getLanguage())
            .transactionDate(OffsetDateTime.now(clock))
            .institutionId(patronStatus.getInstitutionId())
            .patronIdentifier(patronStatus.getPatronIdentifier())
            .validPatron(TRUE)
            .validPatronPassword(validPassword)
            .build();
        }
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

  private PatronStatusResponseBuilder totalAmount(
      JsonObject jo,
      PatronStatusResponseBuilder builder) {
    
    final JsonArray arr = jo.getJsonArray("accounts");
    Float total = 0.0f;
    for (int i = 0;i < arr.size();i++) {
      total += arr.getJsonObject(i).getFloat("remaining");
    }
    log.debug("Total is {}", total.toString());
    return builder.feeAmount(total.toString());
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

  private PatronInformationResponseBuilder addOverdueItems(JsonObject overdues, boolean details,
      PatronInformationResponseBuilder builder) {
    final int overdueItemsCount;
    final List<String> overdueItems;

    if (overdues != null) {
      overdueItemsCount = Math.min(getTotalRecords(overdues), 9999);
      if (details) {
        overdueItems = getOverdueItems(overdues);
      } else {
        overdueItems = null;
      }
    } else {
      overdueItemsCount = 0;
      overdueItems = null;
    }

    return builder.overdueItemsCount(Integer.valueOf(overdueItemsCount)).overdueItems(overdueItems);
  }

  @SuppressWarnings("rawtypes")
  private Future<PatronInformationResponseBuilder> addRecalls(List<Future<JsonObject>> recalls,
      Integer startItem, Integer endItem, boolean details,
      PatronInformationResponseBuilder builder) {
    return CompositeFuture.all(new ArrayList<Future>(recalls)).map(result -> {
      final int recallItemsCount = Math.min(countRecallItems(recalls), 9999);
      final List<String> recallItems;

      if (details) {
        recallItems = getRecallItems(recalls, startItem, endItem);
      } else {
        recallItems = null;
      }

      return builder.recallItemsCount(Integer.valueOf(recallItemsCount)).recallItems(recallItems);
    });
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

  private List<String> getTitlesForRequests(JsonArray items) {
    return getTitles(items, FIELD_INSTANCE);
  }

  private List<String> getTitlesForLoans(JsonArray loans) {
    return getTitles(loans, FIELD_ITEM);
  }

  private List<String> getTitles(JsonArray items, String childField) {
    return items.stream()
        .map(o -> (JsonObject) o)
        .map(jo -> getChildString(jo, childField, FIELD_TITLE))
        .collect(Collectors.toList());
  }

  private List<String> getHoldItems(JsonObject requests) {
    // All items in the response are holds
    final JsonArray requestArray = requests.getJsonArray(FIELD_REQUESTS, new JsonArray());
    return getTitlesForRequests(requestArray);
  }

  private List<String> getOverdueItems(JsonObject loans) {
    // All items in the response are overdue loans
    final JsonArray loanArray = loans.getJsonArray("loans", new JsonArray());
    return getTitlesForLoans(loanArray);
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
            .map(jsonObject -> getChildString(jsonObject, FIELD_INSTANCE, FIELD_TITLE)))
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
        circulationRepository.getLoansByUserId(userId, null, null, sessionData);

    return loansFuture.map(jo -> {
      final JsonArray loans = jo == null ? new JsonArray()
          : jo.getJsonArray("loans", new JsonArray());
      return loans.stream()
          .map(o -> (JsonObject) o)
          .map(loan -> circulationRepository.getRequestsByItemId(loan.getString("itemId"),
              "Recall", null, null, sessionData))
          .collect(Collectors.toList());
    });
  }
}
