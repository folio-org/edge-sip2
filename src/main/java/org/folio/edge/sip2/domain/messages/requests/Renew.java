package org.folio.edge.sip2.domain.messages.requests;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Represents the Renew message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * This message is used to renew an item. The ACS should respond with a Renew
 * Response message. Either or both of the "item identifier" and
 * "title identifier" fields must be present for the message to be useful.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class Renew {
  /**
   * {@code FALSE} if the ACS should not allow third party renewals.
   */
  private final Boolean thirdPartyAllowed;
  /**
   * When {@code TRUE} the ACS should not block this transaction because it has
   * already been executed. This happens when the ACS is off-line while the SC
   * performs transactions. These transactions are stored and sent to the ACS
   * when it comes on-line.
   */
  private final Boolean noBlock;
  /** The date and time the patron renewed the item at the SC. */
  private final OffsetDateTime transactionDate;
  /** The no block due date given to items during off-line operation. */
  private final OffsetDateTime nbDueDate;
  /** The ID of the institution making the request. */
  private final String institutionId;
  /** The ID of the patron making the request. */
  private final String patronIdentifier;
  /** The password used by the patron. */
  private final String patronPassword;
  /** The ID of the item being renewed. */
  private final String itemIdentifier;
  /** The ID of the title being renewed. */
  private final String titleIdentifier;
  /** The password used by the terminal. */
  private final String terminalPassword;
  /** Specific item information that can be user for identification. */
  private final String itemProperties;
  /**
   * {@code FALSE} indicates that if there is a fee associated with renewing
   * the item, the ACS should tell the SC in the renewal response that there is
   * a fee and refuse to renew the item.<br>
   * {@code TRUE} indicates that the SC and patron interact and the patron
   * agrees to pay the fee so the renewal should not be refused because of the
   * fee.
   */
  private final Boolean feeAcknowledged;

  /**
   * Construct a {@code Renew} based on a {@code RenewBuilder} object.
   * @param builder The basis for creating the renew.
   */
  private Renew(RenewBuilder builder) {
    this.thirdPartyAllowed = builder.thirdPartyAllowed;
    this.noBlock = builder.noBlock;
    this.transactionDate = builder.transactionDate;
    this.nbDueDate = builder.nbDueDate;
    this.institutionId = builder.institutionId;
    this.patronIdentifier = builder.patronIdentifier;
    this.patronPassword = builder.patronPassword;
    this.itemIdentifier = builder.itemIdentifier;
    this.titleIdentifier = builder.titleIdentifier;
    this.terminalPassword = builder.terminalPassword;
    this.itemProperties = builder.itemProperties;
    this.feeAcknowledged = builder.feeAcknowledged;
  }

  /**
   * Returns a builder used to construct a {@code Renew}.
   * @return A renew builder.
   */
  public static RenewBuilder builder() {
    return new RenewBuilder();
  }

  public Boolean getThirdPartyAllowed() {
    return thirdPartyAllowed;
  }

  public Boolean getNoBlock() {
    return noBlock;
  }

  public OffsetDateTime getTransactionDate() {
    return transactionDate;
  }

  public OffsetDateTime getNbDueDate() {
    return nbDueDate;
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

  public String getItemProperties() {
    return itemProperties;
  }

  public Boolean getFeeAcknowledged() {
    return feeAcknowledged;
  }

  @Override
  public int hashCode() {
    return Objects.hash(feeAcknowledged, institutionId, itemIdentifier,
        itemProperties, nbDueDate, noBlock, patronIdentifier, patronPassword,
        terminalPassword, thirdPartyAllowed, titleIdentifier, transactionDate);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Renew)) {
      return false;
    }
    Renew other = (Renew) obj;
    return Objects.equals(feeAcknowledged, other.feeAcknowledged)
        && Objects.equals(institutionId, other.institutionId)
        && Objects.equals(itemIdentifier, other.itemIdentifier)
        && Objects.equals(itemProperties, other.itemProperties)
        && Objects.equals(nbDueDate, other.nbDueDate)
        && Objects.equals(noBlock, other.noBlock)
        && Objects.equals(patronIdentifier, other.patronIdentifier)
        && Objects.equals(patronPassword, other.patronPassword)
        && Objects.equals(terminalPassword, other.terminalPassword)
        && Objects.equals(thirdPartyAllowed, other.thirdPartyAllowed)
        && Objects.equals(titleIdentifier, other.titleIdentifier)
        && Objects.equals(transactionDate, other.transactionDate);
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("Renew [thirdPartyAllowed=").append(thirdPartyAllowed)
        .append(", noBlock=").append(noBlock)
        .append(", transactionDate=").append(transactionDate)
        .append(", nbDueDate=").append(nbDueDate)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", patronPassword=").append(patronPassword)
        .append(", itemIdentifier=").append(itemIdentifier)
        .append(", titleIdentifier=").append(titleIdentifier)
        .append(", terminalPassword=").append(terminalPassword)
        .append(", itemProperties=").append(itemProperties)
        .append(", feeAcknowledged=").append(feeAcknowledged)
        .append(']').toString();
  }

  /**
   * Builder for {@code Renew}.
   */
  public static class RenewBuilder {
    private Boolean thirdPartyAllowed;
    private Boolean noBlock;
    private OffsetDateTime transactionDate;
    private OffsetDateTime nbDueDate;
    private String institutionId;
    private String patronIdentifier;
    private String itemIdentifier;
    private String titleIdentifier;
    private String terminalPassword;
    private String itemProperties;
    private String patronPassword;
    private Boolean feeAcknowledged;

    private RenewBuilder() {
      super();
    }

    public RenewBuilder thirdPartyAllowed(Boolean thirdPartyAllowed) {
      this.thirdPartyAllowed = thirdPartyAllowed;
      return this;
    }

    public RenewBuilder noBlock(Boolean noBlock) {
      this.noBlock = noBlock;
      return this;
    }

    public RenewBuilder transactionDate(OffsetDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return this;
    }

    public RenewBuilder nbDueDate(OffsetDateTime nbDueDate) {
      this.nbDueDate = nbDueDate;
      return this;
    }

    public RenewBuilder institutionId(String institutionId) {
      this.institutionId = institutionId;
      return this;
    }

    public RenewBuilder patronIdentifier(String patronIdentifier) {
      this.patronIdentifier = patronIdentifier;
      return this;
    }

    public RenewBuilder patronPassword(String patronPassword) {
      this.patronPassword = patronPassword;
      return this;
    }

    public RenewBuilder itemIdentifier(String itemIdentifier) {
      this.itemIdentifier = itemIdentifier;
      return this;
    }

    public RenewBuilder titleIdentifier(String titleIdentifier) {
      this.titleIdentifier = titleIdentifier;
      return this;
    }

    public RenewBuilder terminalPassword(String terminalPassword) {
      this.terminalPassword = terminalPassword;
      return this;
    }

    public RenewBuilder itemProperties(String itemProperties) {
      this.itemProperties = itemProperties;
      return this;
    }

    public RenewBuilder feeAcknowledged(Boolean feeAcknowledged) {
      this.feeAcknowledged = feeAcknowledged;
      return this;
    }

    public Renew build() {
      return new Renew(this);
    }
  }
}
