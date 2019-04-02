package org.folio.edge.sip2.domain.messages.requests;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Represents the Patron Enable message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * This message can be used by the SC to re-enable canceled patrons. It should
 * only be used for system testing and validation. The ACS should respond with
 * a Patron Enable Response message.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class PatronEnable {
  /** The date of this transaction. */
  private final ZonedDateTime transactionDate;
  /** The ID of the institution making the request. */
  private final String institutionId;
  /** The ID of the patron making the request. */
  private final String patronIdentifier;
  /** The password used by the terminal. */
  private final String terminalPassword;
  /** The password used by the patron. */
  private final String patronPassword;

  /**
   * Construct a {@code PatronEnable} based on a
   * {@code PatronEnableBuilder} object.
   * @param builder The basis for creating the patron enable.
   */
  private PatronEnable(PatronEnableBuilder builder) {
    this.transactionDate = builder.transactionDate;
    this.institutionId = builder.institutionId;
    this.patronIdentifier = builder.patronIdentifier;
    this.terminalPassword = builder.terminalPassword;
    this.patronPassword = builder.patronPassword;
  }

  /**
   * Returns a builder used to construct a {@code PatronEnable}.
   * @return A patron enable builder.
   */
  public static PatronEnableBuilder builder() {
    return new PatronEnableBuilder();
  }

  public ZonedDateTime getTransactionDate() {
    return transactionDate;
  }

  public String getInstitutionId() {
    return institutionId;
  }

  public String getPatronIdentifier() {
    return patronIdentifier;
  }

  public String getTerminalPassword() {
    return terminalPassword;
  }

  public String getPatronPassword() {
    return patronPassword;
  }

  @Override
  public int hashCode() {
    return Objects.hash(institutionId, patronIdentifier, patronPassword,
        terminalPassword, transactionDate);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof PatronEnable)) {
      return false;
    }
    PatronEnable other = (PatronEnable) obj;
    return Objects.equals(institutionId, other.institutionId)
        && Objects.equals(patronIdentifier, other.patronIdentifier)
        && Objects.equals(patronPassword, other.patronPassword)
        && Objects.equals(terminalPassword, other.terminalPassword)
        && Objects.equals(transactionDate, other.transactionDate);
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("PatronEnable [transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", terminalPassword=").append(terminalPassword)
        .append(", patronPassword=").append(patronPassword)
        .append(']').toString();
  }

  /**
   * Builder for {@code PatronEnableRequest}.
   */
  public static class PatronEnableBuilder {
    private ZonedDateTime transactionDate;
    private String institutionId;
    private String patronIdentifier;
    private String terminalPassword;
    private String patronPassword;

    private PatronEnableBuilder() {
      super();
    }

    public PatronEnableBuilder transactionDate(ZonedDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return this;
    }

    public PatronEnableBuilder institutionId(String institutionId) {
      this.institutionId = institutionId;
      return this;
    }

    public PatronEnableBuilder patronIdentifier(String patronIdentifier) {
      this.patronIdentifier = patronIdentifier;
      return this;
    }

    public PatronEnableBuilder terminalPassword(String terminalPassword) {
      this.terminalPassword = terminalPassword;
      return this;
    }

    public PatronEnableBuilder patronPassword(String patronPassword) {
      this.patronPassword = patronPassword;
      return this;
    }

    public PatronEnable build() {
      return new PatronEnable(this);
    }
  }
}
