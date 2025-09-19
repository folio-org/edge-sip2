package org.folio.edge.sip2.support;

import org.folio.edge.sip2.parser.LanguageMapper;
import org.folio.edge.sip2.support.model.CheckInCommand;
import org.folio.edge.sip2.support.model.EndSessionCommand;
import org.folio.edge.sip2.support.model.LoginCommand;
import org.folio.edge.sip2.support.model.PatronInformationCommand;
import org.folio.edge.sip2.support.model.PatronInformationCommand.PatronInfoSummaryType;
import org.folio.edge.sip2.support.model.RawCommand;
import org.folio.edge.sip2.support.model.ResendCommand;
import org.folio.edge.sip2.support.model.Sip2Command;
import org.folio.edge.sip2.support.model.StatusCommand;

public interface Sip2Commands {

  /**
   * Creates a {@link LoginCommand} with the specified username and password.
   *
   * @param username - the username to use for login
   * @param password - the password to use for login
   * @return a new {@link LoginCommand} instance
   */
  static LoginCommand login(String username, String password) {
    return login(username, password, null);
  }

  /**
   * Creates a {@link LoginCommand} with the specified username, password, and location code.
   *
   * @param username     - the username to use for login
   * @param password     - the password to use for login
   * @param locationCode - the location code to use for login (nullable)
   * @return a new {@link LoginCommand} instance
   */
  static LoginCommand login(String username, String password, String locationCode) {
    return LoginCommand.builder()
        .loginUserId(username)
        .loginPassword(password)
        .locationCode(locationCode)
        .build();
  }

  /**
   * Creates a new {@link StatusCommand}.
   *
   * @return a new {@link StatusCommand} instance
   */
  static StatusCommand status() {
    return new StatusCommand();
  }

  /**
   * Creates a {@link PatronInformationCommand} with the specified patron identifier and summary.
   *
   * @param patronIdentifier - the identifier of the patron
   * @param summary          - the summary type for patron information
   * @return a new {@link Sip2Command} instance representing the patron information command
   */
  static Sip2Command patronInformation(String patronIdentifier, PatronInfoSummaryType summary) {
    return PatronInformationCommand.builder()
        .patronIdentifier(patronIdentifier)
        .languageCode(LanguageMapper.ENGLISH)
        .summary(summary)
        .build();
  }

  /**
   * Creates a {@link CheckInCommand} with the specified item identifier.
   *
   * @param itemIdentifier - the identifier of the item to check in
   * @return a new {@link CheckInCommand} instance
   */
  static Sip2Command checkIn(String itemIdentifier) {
    return CheckInCommand.builder()
        .itemIdentifier(itemIdentifier)
        .build();
  }

  /**
   * Creates a {@link ResendCommand}.
   *
   * @return a new {@link ResendCommand} instance
   */
  static ResendCommand resend() {
    return ResendCommand.builder().build();
  }

  /**
   * Creates an {@link EndSessionCommand} for the given patron identifier.
   *
   * @param patronIdentifier - the identifier of the patron
   * @return a new {@link EndSessionCommand} instance
   */
  static EndSessionCommand endSession(String patronIdentifier) {
    return EndSessionCommand.of(null, patronIdentifier, null, null);
  }

  /**
   * Creates an {@link EndSessionCommand} for the given institution and patron identifiers.
   *
   * @param institutionId    - the institution identifier
   * @param patronIdentifier - the identifier of the patron
   * @return a new {@link EndSessionCommand} instance
   */
  static EndSessionCommand endSession(String institutionId, String patronIdentifier) {
    return EndSessionCommand.of(institutionId, patronIdentifier, null, null);
  }

  /**
   * Creates an {@link EndSessionCommand} with all parameters.
   *
   * @param institutionId    - the institution identifier
   * @param patronIdentifier - the identifier of the patron
   * @param terminalPassword - the terminal password
   * @param patronPassword   - the patron password
   * @return a new {@link EndSessionCommand} instance
   */
  static EndSessionCommand endSession(String institutionId, String patronIdentifier,
      String terminalPassword, String patronPassword) {
    return EndSessionCommand.of(institutionId, patronIdentifier, terminalPassword, patronPassword);
  }

  /**
   * Creates a {@link RawCommand} with the specified message and error detection flag.
   *
   * @param message              - the raw SIP2 message to send
   * @param ignoreErrorDetection - whether to ignore error detection for this command
   * @return a new {@link RawCommand} instance
   */
  static RawCommand raw(String message, boolean ignoreErrorDetection) {
    return RawCommand.of(message, ignoreErrorDetection);
  }
}
