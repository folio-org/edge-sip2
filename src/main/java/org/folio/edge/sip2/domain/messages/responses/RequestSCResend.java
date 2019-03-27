package org.folio.edge.sip2.domain.messages.responses;

/**
 * Represents the Request SC Resend message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * This message requests the SC to re-transmit its last message. It is sent by
 * the ACS to the SC when the checksum in a received message does not match the
 * value calculated by the ACS. The SC should respond by re-transmitting its
 * last message, This message should never include a "sequence number" field,
 * even when error detection is enabled, but would include a "checksum" field
 * since checksums are in use.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class RequestSCResend {
  /**
   * Construct a {@code RequestSCResend}.
   */
  private RequestSCResend() {
    super();
  }

  /**
   * Returns a builder used to construct a {@code RequestSCResend}.
   * @return A request SC resend builder.
   */
  public static RequestSCResendBuilder builder() {
    return new RequestSCResendBuilder();
  }

  @Override
  public String toString() {
    return "RequestSCResend []";
  }

  /**
   * Builder for {@code RequestSCResend}.
   */
  public static class RequestSCResendBuilder {
    private RequestSCResendBuilder() {
      super();
    }

    public RequestSCResend build() {
      return new RequestSCResend();
    }
  }
}
