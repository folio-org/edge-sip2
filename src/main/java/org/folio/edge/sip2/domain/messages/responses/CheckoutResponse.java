package org.folio.edge.sip2.domain.messages.responses;

/**
 * Represents the Checkout Response message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * This message must be sent by the ACS in response to a Checkout message from
 * the SC.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class CheckoutResponse extends BaseCheckoutRenewResponse {
  /**
   * Construct a {@code CheckoutResponse} based on a
   * {@code CheckoutResponseBuilder} object.
   * @param builder The basis for creating the check out response.
   */
  private CheckoutResponse(CheckoutResponseBuilder builder) {
    super(builder);
  }

  /**
   * Returns a builder used to construct a {@code CheckoutResponse}.
   * @return A checkout response builder.
   */
  public static CheckoutResponseBuilder builder() {
    return new CheckoutResponseBuilder();
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("CheckoutResponse [")
        .append(super.toString())
        .append(']').toString();
  }

  /**
   * Builder for {@code CheckoutResponse}.
   */
  public static class CheckoutResponseBuilder extends
      BaseCheckoutRenewResponseBuilder<CheckoutResponse,
          CheckoutResponseBuilder> {
    private CheckoutResponseBuilder() {
      super();
    }

    @Override
    public CheckoutResponse build() {
      return new CheckoutResponse(this);
    }

    @Override
    protected CheckoutResponseBuilder builder() {
      return this;
    }
  }
}
