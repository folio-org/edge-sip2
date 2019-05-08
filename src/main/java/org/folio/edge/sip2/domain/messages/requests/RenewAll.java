package org.folio.edge.sip2.domain.messages.requests;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Represents the Renew All message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * This message is used to renew all items that the patron has checked out.
 * The ACS should respond with a Renew All Response message.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class RenewAll {
  /** The date and time the patron renewed all checked out items at the SC. */
  private final OffsetDateTime transactionDate;
  /** The ID of the institution making the request. */
  private final String institutionId;
  /** The ID of the patron making the request. */
  private final String patronIdentifier;
  /** The password used by the patron. */
  private final String patronPassword;
  /** The password used by the terminal. */
  private final String terminalPassword;
  /**
   * {@code FALSE} indicates that if there is a fee associated with renewing
   * an item, the ACS should tell the SC in the renewal response that there is
   * a fee and refuse to renew the item.<br>
   * {@code TRUE} indicates that the SC and patron interact and the patron
   * agrees to pay the fee so the renewal should not be refused because of the
   * fee.
   */
  private final Boolean feeAcknowledged;

  /**
   * Construct a {@code RenewAll} based on a {@code RenewAllBuilder} object.
   * @param builder The basis for creating the renew all.
   */
  private RenewAll(RenewAllBuilder builder) {
    this.transactionDate = builder.transactionDate;
    this.institutionId = builder.institutionId;
    this.patronIdentifier = builder.patronIdentifier;
    this.patronPassword = builder.patronPassword;
    this.terminalPassword = builder.terminalPassword;
    this.feeAcknowledged = builder.feeAcknowledged;
  }

  /**
   * Returns a builder used to construct a {@code RenewAll}.
   * @return A renew all builder.
   */
  public static RenewAllBuilder builder() {
    return new RenewAllBuilder();
  }

  public OffsetDateTime getTransactionDate() {
    return transactionDate;
  }

  public String getInstitutionId() {
    return institutionId;
  }

  public String getPatronIdentifier() {
    return patronIdentifier;
  }

  public String getPatronPassword() {
    return patronPassword;
  }

  public String getTerminalPassword() {
    return terminalPassword;
  }

  public Boolean getFeeAcknowledged() {
    return feeAcknowledged;
  }

  @Override
  public int hashCode() {
    return Objects.hash(feeAcknowledged, institutionId, patronIdentifier,
        patronPassword, terminalPassword, transactionDate);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof RenewAll)) {
      return false;
    }
    RenewAll other = (RenewAll) obj;
    return Objects.equals(feeAcknowledged, other.feeAcknowledged)
        && Objects.equals(institutionId, other.institutionId)
        && Objects.equals(patronIdentifier, other.patronIdentifier)
        && Objects.equals(patronPassword, other.patronPassword)
        && Objects.equals(terminalPassword, other.terminalPassword)
        && Objects.equals(transactionDate, other.transactionDate);
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("RenewAll [transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", patronPassword=").append(patronPassword)
        .append(", terminalPassword=").append(terminalPassword)
        .append(", feeAcknowledged=").append(feeAcknowledged)
        .append(']').toString();
  }

  /**
   * Builder for {@code RenewAll}.
   */
  public static class RenewAllBuilder {
    private OffsetDateTime transactionDate;
    private String institutionId;
    private String patronIdentifier;
    private String terminalPassword;
    private String patronPassword;
    private Boolean feeAcknowledged;

    private RenewAllBuilder() {
      super();
    }

    public RenewAllBuilder transactionDate(OffsetDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return this;
    }

    public RenewAllBuilder institutionId(String institutionId) {
      this.institutionId = institutionId;
      return this;
    }

    public RenewAllBuilder patronIdentifier(String patronIdentifier) {
      this.patronIdentifier = patronIdentifier;
      return this;
    }

    public RenewAllBuilder patronPassword(String patronPassword) {
      this.patronPassword = patronPassword;
      return this;
    }

    public RenewAllBuilder terminalPassword(String terminalPassword) {
      this.terminalPassword = terminalPassword;
      return this;
    }

    public RenewAllBuilder feeAcknowledged(Boolean feeAcknowledged) {
      this.feeAcknowledged = feeAcknowledged;
      return this;
    }

    public RenewAll build() {
      return new RenewAll(this);
    }
  }
}
