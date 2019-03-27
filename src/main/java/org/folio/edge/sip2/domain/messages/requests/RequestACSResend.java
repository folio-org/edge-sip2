package org.folio.edge.sip2.domain.messages.requests;

/**
 * Represents the Request ACS Resend message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * This message requests the ACS to re-transmit its last message. It is sent by
 * the SC to the ACS when the checksum in a received message does not match the
 * value calculated by the SC. The ACS should respond by re-transmitting its
 * last message, This message should never include a "sequence number" field,
 * even when error detection is enabled, but would include a "checksum" field
 * since checksums are in use.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class RequestACSResend {
  /**
   * Construct a {@code RequestACSResend}.
   */
  private RequestACSResend() {
    super();
  }

  /**
   * Returns a builder used to construct a {@code RequestACSResend}.
   * @return A request ACS resend builder.
   */
  public static RequestACSResendBuilder builder() {
    return new RequestACSResendBuilder();
  }

  @Override
  public String toString() {
    return "RequestACSResend []";
  }

  /**
   * Builder for {@code RequestACSResend}.
   */
  public static class RequestACSResendBuilder {
    private RequestACSResendBuilder() {
      super();
    }

    public RequestACSResend build() {
      return new RequestACSResend();
    }
  }
}
