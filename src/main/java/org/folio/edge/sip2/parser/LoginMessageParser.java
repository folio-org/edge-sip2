package org.folio.edge.sip2.parser;

import static java.lang.Character.valueOf;
import static org.folio.edge.sip2.domain.messages.requests.Login.builder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.enumerations.PWDAlgorithm;
import org.folio.edge.sip2.domain.messages.enumerations.UIDAlgorithm;
import org.folio.edge.sip2.domain.messages.requests.Login;
import org.folio.edge.sip2.domain.messages.requests.Login.LoginBuilder;

/**
 * Parser for the Login message.
 *
 * @author mreno-EBSCO
 *
 */
public final class LoginMessageParser extends MessageParser {
  private static final Logger log = LogManager.getLogger();

  public LoginMessageParser(Character delimiter) {
    super(delimiter);
  }

  /**
   * Parses the Login message from a SIP string.
   *
   * @param message the SIP string.
   * @return the decoded Login message.
   */
  public Login parse(String message) {
    final LoginBuilder builder = builder();
    final char [] messageChars = message.toCharArray();

    // UID algorithm: 1-char, fixed-length required field
    final char uidAlgorithm = messageChars[position++];
    if (uidAlgorithm == '0') {
      builder.uidAlgorithm(UIDAlgorithm.NO_ENCRYPTION);
    } else {
      log.warn("Unknown UID algorithm {}", valueOf(uidAlgorithm));
    }

    // PWD algorithm: 1-char, fixed-length required field
    final char pwdAlgorithm = messageChars[position++];
    if (pwdAlgorithm == '0') {
      builder.pwdAlgorithm(PWDAlgorithm.NO_ENCRYPTION);
    } else {
      log.warn("Unknown PWD algorithm {}", valueOf(pwdAlgorithm));
    }

    // Variable length fields
    do {
      final Field field = parseFieldIdentifier(messageChars);
      final String valueString = parseVariableLengthField(messageChars, field);

      if (valueString == null) {
        // The field was unparsable, bail...
        return null;
      }

      switch (field) {
        case CN:
          // Login user id: variable-length required field
          builder.loginUserId(valueString);
          break;
        case CO:
          // Login password: variable-length required field
          builder.loginPassword(valueString);
          break;
        case CP:
          // Location code: variable-length optional field
          builder.locationCode(valueString);
          break;
        default:
          log.warn("Unknown Login field with value {}", valueString);
      }

      position++;
    } while (position != messageChars.length);

    return builder.build();
  }
}
