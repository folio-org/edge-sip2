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
import org.folio.edge.sip2.domain.messages.requests.PatronInformation;
import org.folio.edge.sip2.domain.messages.responses.PatronInformationResponse;
import org.folio.edge.sip2.domain.messages.responses.PatronInformationResponse.PatronInformationResponseBuilder;
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
  private static final Logger log = LogManager.getLogger();
  // These really should come from FOLIO
  static final String MESSAGE_INVALID_PATRON =
      "Your library card number cannot be located.  Please see a staff member for assistance.";
  static final String MESSAGE_BLOCKED_PATRON =
      "There are unresolved issues with your account.  Please see a staff member for assistance.";

  private final UsersRepository usersRepository;
  private final CirculationRepository circulationRepository;
  private final FeeFinesRepository feeFinesRepository;
  private final Clock clock;

  @Inject
  PatronRepository(UsersRepository usersRepository, CirculationRepository circulationRepository,
      FeeFinesRepository feeFinesRepository, Clock clock) {
    this.usersRepository = Objects.requireNonNull(usersRepository,
        "Users repository cannot be null");
    this.circulationRepository = Objects.requireNonNull(circulationRepository,
        "Circulation repository cannot be null");
    this.feeFinesRepository = Objects.requireNonNull(feeFinesRepository,
        "FeeFines repository cannot be null");
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

    final String barcode = patronInformation.getPatronIdentifier();

    // Look up the patron by barcode
    final Future<JsonObject> result = usersRepository.getUserByBarcode(barcode, sessionData);
    return result.compose(user -> {
      if (user == null || !user.getBoolean("active", FALSE).booleanValue()) {
        return invalidPatron(patronInformation);
      } else {
        final String userId = user.getString("id");
        if (userId == null) {
          // Something is really messed up if the id is missing
          log.error("User with barcode {} is missing the \"id\" field", barcode);
          return invalidPatron(patronInformation);
        }
        return validPatron(userId, user.getJsonObject("personal", new JsonObject()),
            patronInformation, sessionData);
      }
    });
  }

  private Future<PatronInformationResponse> validPatron(String userId, JsonObject personal,
      PatronInformation patronInformation, SessionData sessionData) {
    // Now that we have a valid patron, we can retrieve data from circulation
    final PatronInformationResponseBuilder builder = PatronInformationResponse.builder();
    // Store patron data in the builder
    addPersonalData(personal, builder);
    final Integer startItem = patronInformation.getStartItem();
    final Integer endItem = patronInformation.getEndItem();
    // Get manual blocks data to build patron status
    final Future<PatronInformationResponseBuilder> manualBlocksFuture = feeFinesRepository
        .getManualBlocksByUserId(userId, sessionData)
        .map(blocks -> buildPatronStatus(blocks, builder));
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
    return CompositeFuture.all(manualBlocksFuture, holdsFuture, overdueFuture, recallsFuture)
        .map(result -> builder
            // Get tenant language from config along with the timezone
            .language(patronInformation.getLanguage())
            .transactionDate(OffsetDateTime.now(clock))
            .chargedItemsCount(null)
            .fineItemsCount(null)
            .unavailableHoldsCount(null)
            .institutionId(patronInformation.getInstitutionId())
            .patronIdentifier(patronInformation.getPatronIdentifier())
            .validPatron(TRUE)
            .build()
        );
  }

  private Future<PatronInformationResponse> invalidPatron(PatronInformation patronInformation) {
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
        .personalName(null) // Just being explicit here as this is a required field
        .validPatron(FALSE)
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

  private PatronInformationResponseBuilder addPersonalData(JsonObject personal,
      PatronInformationResponseBuilder builder) {
    final String personalName = getPatronPersonalName(personal);
    final String homeAddress = getPatronHomeAddress(personal);
    final String emailAddress = personal.getString("email");
    final String homePhoneNumber = personal.getString("phone");

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

  private String getPatronPersonalName(JsonObject personal) {
    final Optional<String> firstName = Optional.ofNullable(personal.getString("firstName"));
    final Optional<String> middleName = Optional.ofNullable(personal.getString("middleName"));
    final Optional<String> lastName = Optional.ofNullable(personal.getString("lastName"));

    return Stream.of(firstName, middleName, lastName)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.joining(" "));
  }

  private String getPatronHomeAddress(JsonObject personal) {
    final JsonArray addresses = personal.getJsonArray("addresses", new JsonArray());
    // For now, we will do the following:
    // 1. if the address list > 0, pick the "primaryAddress"
    // 2. if no "primaryAddress", pick the first address
    // 3. no addresses, return null (home address is an optional field)
    // In the future, we should use the UUID of the home address type to select the home address
    // and if no match, then return null so as to not expose other addresses.
    // This can be cleaned up a bit when we get to Java 11.
    Optional<String> addressString = addresses.stream()
        .map(o -> (JsonObject) o)
        .filter(address -> address.getBoolean("primaryAddress", FALSE).booleanValue())
        .findFirst()
        .map(Optional::of)
        .orElseGet(() -> addresses.stream()
            .map(o -> (JsonObject) o)
            .findFirst()
            .map(Optional::of)
            .orElse(Optional.empty()))
        .map(this::toHomeAddress);

    return addressString.orElse(null);
  }

  private List<String> getTitles(JsonArray items) {
    return items.stream()
        .map(o -> (JsonObject) o)
        .map(jo -> getChildString(jo, "item", FIELD_TITLE))
        .collect(Collectors.toList());
  }

  private List<String> getHoldItems(JsonObject requests) {
    // All items in the response are holds
    final JsonArray requestArray = requests.getJsonArray(FIELD_REQUESTS, new JsonArray());
    return getTitles(requestArray);
  }

  private List<String> getOverdueItems(JsonObject loans) {
    // All items in the response are overdue loans
    final JsonArray requestArray = loans.getJsonArray("loans", new JsonArray());
    return getTitles(requestArray);
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
            .map(jsonObject -> getChildString(jsonObject, "item", FIELD_TITLE)))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .sorted(Comparator.naturalOrder())
        .skip(skip)
        .limit(maxSize)
        .collect(Collectors.toList());
  }

  private String toHomeAddress(JsonObject address) {
    final Optional<String> addressLine1 = Optional.ofNullable(address.getString("addressLine1"));
    final Optional<String> addressLine2 = Optional.ofNullable(address.getString("addressLine2"));
    final Optional<String> city = Optional.ofNullable(address.getString("city"));
    final Optional<String> region = Optional.ofNullable(address.getString("region"));
    final Optional<String> postalCode = Optional.ofNullable(address.getString("postalCode"));
    final Optional<String> countryId = Optional.ofNullable(address.getString("countryId"));

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
