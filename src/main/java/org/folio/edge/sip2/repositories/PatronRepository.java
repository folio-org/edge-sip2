package org.folio.edge.sip2.repositories;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.folio.edge.sip2.domain.messages.enumerations.Language.UNKNOWN;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.enumerations.Language;
import org.folio.edge.sip2.domain.messages.enumerations.PatronStatus;
import org.folio.edge.sip2.domain.messages.requests.Login;
import org.folio.edge.sip2.domain.messages.requests.PatronInformation;
import org.folio.edge.sip2.domain.messages.responses.LoginResponse;
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
  private static final Logger log = LogManager.getLogger();

  private final UsersRepository usersRepository;
  private final Clock clock;

  @Inject
  PatronRepository(UsersRepository usersRepository, Clock clock) {
    this.usersRepository = Objects.requireNonNull(usersRepository,
        "Users repository cannot be null");
    this.clock = Objects.requireNonNull(clock, "Clock cannot be null");
  }

  /**
   * Perform patron information.
   *
   * @param patronInformation the patron information domain object
   * @return the patron information response domain object
   */
  public Future<PatronInformationResponse> patronInformation(
      PatronInformation patronInformation,
      SessionData sessionData) {
    Objects.requireNonNull(patronInformation, "patronInformation cannot be null");
    Objects.requireNonNull(sessionData, "sessionData cannot be null");

    final String barcode = patronInformation.getPatronIdentifier();

    final Future<JsonObject> result = usersRepository.getUserByBarcode(barcode, sessionData);
    return result.compose(user -> {
      if (user == null || user.getBoolean("active", FALSE) == FALSE) {
        return invalidPatron(patronInformation);
      } else {
        final PatronInformationResponseBuilder builder = PatronInformationResponse.builder();
        final String userId = user.getString("id");
        if (userId == null ) {
          // Something is really messed up if the id is missing
          log.error("User with barcode {} is missing the \"id\" field", barcode);
          return invalidPatron(patronInformation);
        }
        return Future.succeededFuture(builder.build());
      }
    });
  }

  private Future<PatronInformationResponse> invalidPatron(PatronInformation patronInformation) {
    return Future.succeededFuture(PatronInformationResponse.builder()
        .patronStatus(EnumSet.noneOf(PatronStatus.class))
        .language(UNKNOWN)
        .transactionDate(ZonedDateTime.now(clock)) // need tenant timezone
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
        .build()
        );
  }
}
