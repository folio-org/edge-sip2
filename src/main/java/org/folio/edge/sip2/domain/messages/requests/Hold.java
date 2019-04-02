package org.folio.edge.sip2.domain.messages.requests;

import java.time.ZonedDateTime;
import java.util.Objects;

import org.folio.edge.sip2.domain.messages.enumerations.HoldMode;
import org.folio.edge.sip2.domain.messages.enumerations.HoldType;

/**
 * Represents the Hold message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * This message is used to create, modify, or delete a hold. The ACS should
 * respond with a Hold Response message. Either or both of the
 * "item identifier" and "title identifier" fields must be present for
 * the message to be useful.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class Hold {
  /** Indicates the mode of the hold operation to perform. */
  private final HoldMode holdMode;
  /** The date and time the patron placed the hold on the item at the SC. */
  private final ZonedDateTime transactionDate;
  /** The date and time the request will expire. */
  private final ZonedDateTime expirationDate;
  /** The location where the item will be picked up. */
  private final String pickupLocation;
  /** The type of the hold. */
  private final HoldType holdType;
  /** The ID of the institution making the request. */
  private final String institutionId;
  /** The ID of the patron making the request. */
  private final String patronIdentifier;
  /** The password used by the patron. */
  private final String patronPassword;
  /** The ID of the item being held. */
  private final String itemIdentifier;
  /** The ID of the title being held. */
  private final String titleIdentifier;
  /** The password used by the terminal. */
  private final String terminalPassword;
  /**
   * {@code FALSE} indicates that if there is a fee associated with holding
   * the item, the ACS should tell the SC in the hold response that there is
   * a fee and refuse to hold the item.<br>
   * {@code TRUE} indicates that the SC and patron interact and the patron
   * agrees to pay the fee so the hold should not be refused because of the
   * fee.
   */
  private final Boolean feeAcknowledged;

  /**
   * Construct a {@code Hold} based on a {@code HoldBuilder} object.
   * @param builder The basis for creating the hold.
   */
  private Hold(HoldBuilder builder) {
    this.holdMode = builder.holdMode;
    this.transactionDate = builder.transactionDate;
    this.expirationDate = builder.expirationDate;
    this.pickupLocation = builder.pickupLocation;
    this.holdType = builder.holdType;
    this.institutionId = builder.institutionId;
    this.patronIdentifier = builder.patronIdentifier;
    this.patronPassword = builder.patronPassword;
    this.itemIdentifier = builder.itemIdentifier;
    this.titleIdentifier = builder.titleIdentifier;
    this.terminalPassword = builder.terminalPassword;
    this.feeAcknowledged = builder.feeAcknowledged;
  }

  /**
   * Returns a builder used to construct a {@code Hold}.
   * @return A hold builder.
   */
  public static HoldBuilder builder() {
    return new HoldBuilder();
  }

  public HoldMode getHoldMode() {
    return holdMode;
  }

  public ZonedDateTime getTransactionDate() {
    return transactionDate;
  }

  public ZonedDateTime getExpirationDate() {
    return expirationDate;
  }

  public String getPickupLocation() {
    return pickupLocation;
  }

  public HoldType getHoldType() {
    return holdType;
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

  public String getItemIdentifier() {
    return itemIdentifier;
  }

  public String getTitleIdentifier() {
    return titleIdentifier;
  }

  public String getTerminalPassword() {
    return terminalPassword;
  }

  public Boolean getFeeAcknowledged() {
    return feeAcknowledged;
  }

  @Override
  public int hashCode() {
    return Objects.hash(expirationDate, feeAcknowledged, holdMode, holdType,
        institutionId, itemIdentifier, patronIdentifier, patronPassword,
        pickupLocation, terminalPassword, titleIdentifier, transactionDate);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Hold)) {
      return false;
    }
    Hold other = (Hold) obj;
    return Objects.equals(expirationDate, other.expirationDate)
        && Objects.equals(feeAcknowledged, other.feeAcknowledged)
        && holdMode == other.holdMode
        && holdType == other.holdType
        && Objects.equals(institutionId, other.institutionId)
        && Objects.equals(itemIdentifier, other.itemIdentifier)
        && Objects.equals(patronIdentifier, other.patronIdentifier)
        && Objects.equals(patronPassword, other.patronPassword)
        && Objects.equals(pickupLocation, other.pickupLocation)
        && Objects.equals(terminalPassword, other.terminalPassword)
        && Objects.equals(titleIdentifier, other.titleIdentifier)
        && Objects.equals(transactionDate, other.transactionDate);
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("Hold [holdMode=").append(holdMode)
        .append(", transactionDate=").append(transactionDate)
        .append(", expirationDate=").append(expirationDate)
        .append(", pickupLocation=").append(pickupLocation)
        .append(", holdType=").append(holdType)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", patronPassword=").append(patronPassword)
        .append(", itemIdentifier=").append(itemIdentifier)
        .append(", titleIdentifier=").append(titleIdentifier)
        .append(", terminalPassword=").append(terminalPassword)
        .append(", feeAcknowledged=").append(feeAcknowledged)
        .append(']').toString();
  }

  /**
   * Builder for {@code Hold}.
   */
  public static class HoldBuilder {
    private HoldMode holdMode;
    private ZonedDateTime transactionDate;
    private ZonedDateTime expirationDate;
    private String pickupLocation;
    private HoldType holdType;
    private String institutionId;
    private String patronIdentifier;
    private String patronPassword;
    private String itemIdentifier;
    private String titleIdentifier;
    private String terminalPassword;
    private Boolean feeAcknowledged;

    private HoldBuilder() {
      super();
    }

    public HoldBuilder holdMode(HoldMode holdMode) {
      this.holdMode = holdMode;
      return this;
    }

    public HoldBuilder transactionDate(ZonedDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return this;
    }

    public HoldBuilder expirationDate(ZonedDateTime expirationDate) {
      this.expirationDate = expirationDate;
      return this;
    }

    public HoldBuilder pickupLocation(String pickupLocation) {
      this.pickupLocation = pickupLocation;
      return this;
    }

    public HoldBuilder holdType(HoldType holdType) {
      this.holdType = holdType;
      return this;
    }

    public HoldBuilder institutionId(String institutionId) {
      this.institutionId = institutionId;
      return this;
    }

    public HoldBuilder patronIdentifier(String patronIdentifier) {
      this.patronIdentifier = patronIdentifier;
      return this;
    }

    public HoldBuilder patronPassword(String patronPassword) {
      this.patronPassword = patronPassword;
      return this;
    }

    public HoldBuilder itemIdentifier(String itemIdentifier) {
      this.itemIdentifier = itemIdentifier;
      return this;
    }

    public HoldBuilder titleIdentifier(String titleIdentifier) {
      this.titleIdentifier = titleIdentifier;
      return this;
    }

    public HoldBuilder terminalPassword(String terminalPassword) {
      this.terminalPassword = terminalPassword;
      return this;
    }

    public HoldBuilder feeAcknowledged(Boolean feeAcknowledged) {
      this.feeAcknowledged = feeAcknowledged;
      return this;
    }

    public Hold build() {
      return new Hold(this);
    }
  }
}
