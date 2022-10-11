package org.folio.edge.sip2.domain.messages.requests;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Represents the Checkout message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * This message is used by the SC to request to check out an item, and also to
 * cancel a Checkin request that did not successfully complete. The ACS must
 * respond to this command with a Checkout Response message.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class Checkout {
  /**
   * {@code TRUE} if the SC has been configured by the library staff to do
   * renewals. {@code FALSE} if the SC has been configured to not do renewals.
   */
  private final Boolean scRenewalPolicy;
  /**
   * When {@code TRUE} the ACS should not block this transaction because it has
   * already been executed. This happens when the ACS is off-line while the SC
   * performs transactions. These transactions are stored and sent to the ACS
   * when it comes on-line.
   */
  private final Boolean noBlock;
  /** The date and time the patron checked out the item at the SC. */
  private final OffsetDateTime transactionDate;
  /** The no block due date given to items during off-line operation. */
  private final OffsetDateTime nbDueDate;
  /** The ID of the institution making the request. */
  private final String institutionId;
  /** The ID of the patron making the request. */
  private final String patronIdentifier;
  /** The ID of the item being checked out. */
  private final String itemIdentifier;
  /** The password used by the terminal. */
  private final String terminalPassword;
  /** Specific item information that can be user for identification. */
  private final String itemProperties;
  /** The password used by the patron. */
  private final String patronPassword;
  /**
   * {@code FALSE} indicates that if there is a fee associated with checking out
   * the item, the ACS should tell the SC in the checkout response that there is
   * a fee and refuse to check out the item.<br>
   * {@code TRUE} indicates that the SC and patron interact and the patron
   * agrees to pay the fee so the checkout should not be refused because of the
   * fee.
   */
  private final Boolean feeAcknowledged;
  /**
   * {@code TRUE} to cancel a failed checkin command. {@code FALSE} for all
   * other situations.
   */
  private final Boolean cancel;

  /**
   * Construct a {@code Checkout} based on a {@code CheckoutBuilder} object.
   * @param builder The basis for creating the check out.
   */
  private Checkout(CheckoutBuilder builder) {
    this.scRenewalPolicy = builder.scRenewalPolicy;
    this.noBlock = builder.noBlock;
    this.transactionDate = builder.transactionDate;
    this.nbDueDate = builder.nbDueDate;
    this.institutionId = builder.institutionId;
    this.patronIdentifier = builder.patronIdentifier;
    this.itemIdentifier = builder.itemIdentifier;
    this.terminalPassword = builder.terminalPassword;
    this.itemProperties = builder.itemProperties;
    this.patronPassword = builder.patronPassword;
    this.feeAcknowledged = builder.feeAcknowledged;
    this.cancel = builder.cancel;
  }

  /**
   * Returns a builder used to construct a {@code Checkout}.
   * @return A checkout builder.
   */
  public static CheckoutBuilder builder() {
    return new CheckoutBuilder();
  }

  public Boolean getScRenewalPolicy() {
    return scRenewalPolicy;
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

  public String getItemIdentifier() {
    return itemIdentifier;
  }

  public String getTerminalPassword() {
    return terminalPassword;
  }

  public String getItemProperties() {
    return itemProperties;
  }

  public String getPatronPassword() {
    return patronPassword;
  }

  public Boolean getFeeAcknowledged() {
    return feeAcknowledged;
  }

  public Boolean getCancel() {
    return cancel;
  }

  @Override
  public int hashCode() {
    return Objects.hash(cancel, feeAcknowledged, institutionId, itemIdentifier,
        itemProperties, nbDueDate, noBlock, patronIdentifier, patronPassword,
        scRenewalPolicy, terminalPassword, transactionDate);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Checkout)) {
      return false;
    }
    Checkout other = (Checkout) obj;
    return Objects.equals(cancel, other.cancel)
        && Objects.equals(feeAcknowledged, other.feeAcknowledged)
        && Objects.equals(institutionId, other.institutionId)
        && Objects.equals(itemIdentifier, other.itemIdentifier)
        && Objects.equals(itemProperties, other.itemProperties)
        && Objects.equals(nbDueDate, other.nbDueDate)
        && Objects.equals(noBlock, other.noBlock)
        && Objects.equals(patronIdentifier, other.patronIdentifier)
        && Objects.equals(patronPassword, other.patronPassword)
        && Objects.equals(scRenewalPolicy, other.scRenewalPolicy)
        && Objects.equals(terminalPassword, other.terminalPassword)
        && Objects.equals(transactionDate, other.transactionDate);
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("Checkout [scRenewalPolicy=").append(scRenewalPolicy)
        .append(", noBlock=").append(noBlock)
        .append(", transactionDate=").append(transactionDate)
        .append(", nbDueDate=").append(nbDueDate)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", itemIdentifier=").append(itemIdentifier)
        .append(", terminalPassword=").append(terminalPassword)
        .append(", itemProperties=").append(itemProperties)
        .append(", patronPassword=").append(patronPassword)
        .append(", feeAcknowledged=").append(feeAcknowledged)
        .append(", cancel=").append(cancel)
        .append(']').toString();
  }

  /**
   * Returns Formatted Log Message.
   * @return String.
   */
  public String getCheckOutLogInfo() {
    return new StringBuilder()
      .append("Checkout [scRenewalPolicy=").append(scRenewalPolicy)
      .append(", noBlock=").append(noBlock)
      .append(", transactionDate=").append(transactionDate)
      .append(", nbDueDate=").append(nbDueDate)
      .append(", institutionId=").append(institutionId)
      .append(", itemIdentifier=").append(itemIdentifier)
      .append(", patronIdentifier=").append(patronIdentifier)
      .append(", itemProperties=").append(itemProperties)
      .append(", feeAcknowledged=").append(feeAcknowledged)
      .append(", cancel=").append(cancel)
      .append(']').toString();
  }

  /**
   * Builder for {@code Checkout}.
   */
  public static class CheckoutBuilder {
    private Boolean scRenewalPolicy;
    private Boolean noBlock;
    private OffsetDateTime transactionDate;
    private OffsetDateTime nbDueDate;
    private String institutionId;
    private String patronIdentifier;
    private String itemIdentifier;
    private String terminalPassword;
    private String itemProperties;
    private String patronPassword;
    private Boolean feeAcknowledged;
    private Boolean cancel;

    private CheckoutBuilder() {
      super();
    }

    public CheckoutBuilder scRenewalPolicy(Boolean scRenewalPolicy) {
      this.scRenewalPolicy = scRenewalPolicy;
      return this;
    }

    public CheckoutBuilder noBlock(Boolean noBlock) {
      this.noBlock = noBlock;
      return this;
    }

    public CheckoutBuilder transactionDate(OffsetDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return this;
    }

    public CheckoutBuilder nbDueDate(OffsetDateTime nbDueDate) {
      this.nbDueDate = nbDueDate;
      return this;
    }

    public CheckoutBuilder institutionId(String institutionId) {
      this.institutionId = institutionId;
      return this;
    }

    public CheckoutBuilder patronIdentifier(String patronIdentifier) {
      this.patronIdentifier = patronIdentifier;
      return this;
    }

    public CheckoutBuilder itemIdentifier(String itemIdentifier) {
      this.itemIdentifier = itemIdentifier;
      return this;
    }

    public CheckoutBuilder terminalPassword(String terminalPassword) {
      this.terminalPassword = terminalPassword;
      return this;
    }

    public CheckoutBuilder itemProperties(String itemProperties) {
      this.itemProperties = itemProperties;
      return this;
    }

    public CheckoutBuilder patronPassword(String patronPassword) {
      this.patronPassword = patronPassword;
      return this;
    }

    public CheckoutBuilder feeAcknowledged(Boolean feeAcknowledged) {
      this.feeAcknowledged = feeAcknowledged;
      return this;
    }

    public CheckoutBuilder cancel(Boolean cancel) {
      this.cancel = cancel;
      return this;
    }

    public Checkout build() {
      return new Checkout(this);
    }
  }
}
