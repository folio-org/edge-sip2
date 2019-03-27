package org.folio.edge.sip2.domain.messages.responses;

import java.util.Objects;

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
public final class LoginResponse {
  private final Boolean ok;

  /**
   * Construct a {@code LoginResponseResponse} based on a
   * {@code LoginResponseResponseBuilder} object.
   * @param builder The basis for creating the login response.
   */
  private LoginResponse(LoginResponseBuilder builder) {
    this.ok = builder.ok;
  }

  /**
   * Returns a builder used to construct a {@code LoginResponseResponse}.
   * @return A login response builder.
   */
  public static LoginResponseBuilder builder() {
    return new LoginResponseBuilder();
  }

  public Boolean getOk() {
    return ok;
  }

  @Override
  public int hashCode() {
    return Objects.hash(ok);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof LoginResponse)) {
      return false;
    }
    LoginResponse other = (LoginResponse) obj;
    return Objects.equals(ok, other.ok);
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("LoginResponse [ok=").append(ok)
        .append(']').toString();
  }

  /**
   * Builder for {@code LoginResponseResponse}.
   */
  public static class LoginResponseBuilder {
    private Boolean ok;

    private LoginResponseBuilder() {
      super();
    }

    public LoginResponseBuilder ok(Boolean ok) {
      this.ok = ok;
      return this;
    }

    public LoginResponse build() {
      return new LoginResponse(this);
    }
  }
}
