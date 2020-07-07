package org.folio.edge.sip2.domain.messages.requests;

import java.util.Objects;
import org.folio.edge.sip2.domain.messages.enumerations.PWDAlgorithm;
import org.folio.edge.sip2.domain.messages.enumerations.UIDAlgorithm;

/**
 * Represents the Login message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * This message can be used to login to an ACS server program. The ACS should
 * respond with the Login Response message. Whether to use this message or to
 * use some other mechanism to login to the ACS is configurable on the SC. When
 * this message is used, it will be the first message sent to the ACS.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class Login {
  /** The algorithm used to encrypt the user ID. */
  private final UIDAlgorithm uidAlgorithm;
  /** The algorithm used to encrypt the password. */
  private final PWDAlgorithm pwdAlgorithm;
  /** The user ID for the SC to use to login to the ACS. */
  private final String loginUserId;
  /** The password for the SC to use to login to the ACS. */
  private final String loginPassword;
  /** The SC location. */
  private final String locationCode;

  /**
   * Construct a {@code Login} based on a {@code LoginBuilder} object.
   * @param builder The basis for creating the login.
   */
  private Login(LoginBuilder builder) {
    this.uidAlgorithm = builder.uidAlgorithm;
    this.pwdAlgorithm = builder.pwdAlgorithm;
    this.loginUserId = builder.loginUserId;
    this.loginPassword = builder.loginPassword;
    this.locationCode = builder.locationCode;
  }

  /**
   * Returns a builder used to construct a {@code Login}.
   * @return A login builder.
   */
  public static LoginBuilder builder() {
    return new LoginBuilder();
  }

  public UIDAlgorithm getUIDAlgorithm() {
    return uidAlgorithm;
  }

  public PWDAlgorithm getPWDAlgorithm() {
    return pwdAlgorithm;
  }

  public String getLoginUserId() {
    return loginUserId;
  }

  public String getLoginPassword() {
    return loginPassword;
  }

  public String getLocationCode() {
    return locationCode;
  }

  @Override
  public int hashCode() {
    return Objects.hash(locationCode, loginPassword, loginUserId,
        pwdAlgorithm, uidAlgorithm);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Login)) {
      return false;
    }
    Login other = (Login) obj;
    return Objects.equals(locationCode, other.locationCode)
        && Objects.equals(loginPassword, other.loginPassword)
        && Objects.equals(loginUserId, other.loginUserId)
        && pwdAlgorithm == other.pwdAlgorithm
        && uidAlgorithm == other.uidAlgorithm;
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("Login [uidAlgorithm=").append(uidAlgorithm)
        .append(", pwdAlgorithm=").append(pwdAlgorithm)
        .append(", loginUserId=").append(loginUserId)
        .append(", loginPassword=").append(loginPassword)
        .append(", locationCode=").append(locationCode)
        .append(']').toString();
  }

  /**
   * Builder for {@code Login}.
   */
  public static class LoginBuilder {
    private UIDAlgorithm uidAlgorithm;
    private PWDAlgorithm pwdAlgorithm;
    private String loginUserId;
    private String loginPassword;
    private String locationCode;

    private LoginBuilder() {
      super();
    }

    public LoginBuilder uidAlgorithm(UIDAlgorithm uidAlgorithm) {
      this.uidAlgorithm = uidAlgorithm;
      return this;
    }

    public LoginBuilder pwdAlgorithm(PWDAlgorithm pwdAlgorithm) {
      this.pwdAlgorithm = pwdAlgorithm;
      return this;
    }

    public LoginBuilder loginUserId(String loginUserId) {
      this.loginUserId = loginUserId;
      return this;
    }

    public LoginBuilder loginPassword(String loginPassword) {
      this.loginPassword = loginPassword;
      return this;
    }

    public LoginBuilder locationCode(String locationCode) {
      this.locationCode = locationCode;
      return this;
    }

    public Login build() {
      return new Login(this);
    }
  }
}
