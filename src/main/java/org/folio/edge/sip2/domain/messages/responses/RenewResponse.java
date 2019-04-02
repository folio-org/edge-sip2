package org.folio.edge.sip2.domain.messages.responses;

/**
 * Represents the Renew Response message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * This message must be sent by the ACS in response to a Renew message by the
 * SC.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class RenewResponse extends BaseCheckoutRenewResponse {
  /**
   * Construct a {@code RenewResponse} based on a
   * {@code RenewResponseBuilder} object.
   * @param builder The basis for creating the renew response.
   */
  private RenewResponse(RenewResponseBuilder builder) {
    super(builder);
  }

  /**
   * Returns a builder used to construct a {@code RenewResponse}.
   * @return A renew response builder.
   */
  public static RenewResponseBuilder builder() {
    return new RenewResponseBuilder();
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("RenewResponse [")
        .append(super.toString())
        .append(']').toString();
  }

  /**
   * Builder for {@code RenewResponse}.
   */
  public static class RenewResponseBuilder extends
      BaseCheckoutRenewResponseBuilder<RenewResponse, RenewResponseBuilder> {
    private RenewResponseBuilder() {
      super();
    }

    @Override
    public RenewResponse build() {
      return new RenewResponse(this);
    }

    @Override
    protected RenewResponseBuilder builder() {
      return this;
    }
  }
}
