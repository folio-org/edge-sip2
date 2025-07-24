package org.folio.edge.sip2.repositories;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import io.vertx.core.Future;
import java.util.Collections;
import java.util.Objects;
import javax.inject.Inject;
import org.folio.edge.sip2.repositories.domain.PatronPasswordVerificationRecords;
import org.folio.edge.sip2.session.SessionData;
import org.folio.edge.sip2.utils.Sip2LogAdapter;

/**
 * Verifies passwords sent via SIP2.
 *
 * @author mreno-EBSCO
 */
public class PasswordVerifier {

  private static final Sip2LogAdapter log = Sip2LogAdapter.getLogger(PasswordVerifier.class);
  private final UsersRepository usersRepository;
  private final LoginRepository loginRepository;

  @Inject
  PasswordVerifier(UsersRepository usersRepository, LoginRepository loginRepository) {
    this.usersRepository = Objects.requireNonNull(usersRepository,
        "Users repository cannot be null");
    this.loginRepository = Objects.requireNonNull(loginRepository,
        "Login repository cannot be null");
  }

  /**
   * Verifies a patron password if required to do so, otherwise it simple returns an empty
   * {@code PatronPasswordVerificationRecords} object.
   * @param patronIdentifier the patron identifier
   * @param patronPassword the patron password
   * @param sessionData session data
   * @return info about the user and whether or not the password was valid
   */
  public Future<PatronPasswordVerificationRecords> verifyPatronPassword(
      String patronIdentifier,
      String patronPassword,
      SessionData sessionData) {
    Objects.requireNonNull(patronIdentifier, "patronIdentifier cannot be null");
    Objects.requireNonNull(sessionData, "sessionData cannot be null");

    final Future<PatronPasswordVerificationRecords> loginFuture;

    if (sessionData.isPatronPasswordVerificationRequired()) {
      loginFuture = doPatronPasswordVerification(patronIdentifier, patronPassword, sessionData);
    } else {
      loginFuture = usersRepository.getUserById(patronIdentifier,
          sessionData).compose(extendedUser -> {
            if (extendedUser != null) {
              return Future.succeededFuture(PatronPasswordVerificationRecords.builder()
                  .extendedUser(extendedUser).build());
            }
            return Future.succeededFuture(PatronPasswordVerificationRecords.builder().build());
          });
    }

    return loginFuture;
  }

  /**
   * Verifies a patron password
   * {@code PatronPasswordVerificationRecords} object.
   * @param patronIdentifier the patron identifier
   * @param patronPassword the patron password
   * @param sessionData session data
   * @return info about the user and whether or not the password was valid
   */
  public Future<PatronPasswordVerificationRecords> doPatronPasswordVerification(
      String patronIdentifier,
      String patronPassword,
      SessionData sessionData) {
    Objects.requireNonNull(patronIdentifier, "patronIdentifier cannot be null");
    Objects.requireNonNull(sessionData, "sessionData cannot be null");

    log.debug(sessionData, "Calling doPatronPasswordVerification");
    Future<PatronPasswordVerificationRecords> loginFuture;

    loginFuture = usersRepository.getUserById(patronIdentifier, sessionData)
      .compose(extendedUser -> {
        if (extendedUser == null) {
          return Future.succeededFuture(PatronPasswordVerificationRecords.builder()
              .passwordVerified(FALSE)
              .build());
        }
        log.debug(sessionData, "Got extendedUser {}", extendedUser);
        log.debug(sessionData, "Calling patronLoginNoCache with username {}",
            extendedUser.getUser().getUsername());
        return loginRepository.patronLoginNoCache(extendedUser.getUser().getUsername(),
            patronPassword, sessionData)
          .compose(token -> {
            if (token != null) {
              log.debug(sessionData, "Valid token");
              return Future.succeededFuture(PatronPasswordVerificationRecords.builder()
                  .extendedUser(extendedUser)
                  .passwordVerified(TRUE)
                  .build());
            } else {
              log.debug(sessionData, "Null token");
              return Future.succeededFuture(PatronPasswordVerificationRecords.builder()
                  .extendedUser(extendedUser)
                  .errorMessages(Collections.singletonList(sessionData.getLoginErrorMessage()))
                  .passwordVerified(FALSE)
                  .build());
            }
          }, throwable -> { //If there was an error, return false for verification
              return Future.succeededFuture(PatronPasswordVerificationRecords.builder()
                  .extendedUser(extendedUser)
                  .errorMessages(Collections.singletonList("Unable to login"))
                  .passwordVerified(FALSE)
                  .build());
            });
      });

    return loginFuture;
  }
}
