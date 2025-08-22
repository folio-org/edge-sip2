package org.folio.edge.sip2.domain.messages.responses;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Represents the Login Response message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * The ACS should send this message in response to the Login message. When this
 * message is used, it will be the first message sent to the SC.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
@Data
@Builder
@RequiredArgsConstructor(staticName = "of")
public final class LoginResponse {

  private final Boolean ok;
}
