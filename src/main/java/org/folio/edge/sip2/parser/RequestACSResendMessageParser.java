package org.folio.edge.sip2.parser;

import static org.folio.edge.sip2.domain.messages.requests.RequestACSResend.builder;

import org.folio.edge.sip2.domain.messages.requests.RequestACSResend;
import org.folio.edge.sip2.domain.messages.requests.RequestACSResend.RequestACSResendBuilder;

/**
 * Parser for the Request ACS Resend message.
 *
 * @author mreno-EBSCO
 *
 */
public class RequestACSResendMessageParser extends MessageParser {
  public RequestACSResendMessageParser(Character delimiter) {
    super(delimiter);
  }

  /**
   * Parses the Request ACS Resend message from a SIP string.
   *
   * @param message the SIP string.
   * @return the decoded Request ACS Resend message.
   */
  public RequestACSResend parse(String message) {
    final RequestACSResendBuilder builder = builder();
    return builder.build();
  }
}
