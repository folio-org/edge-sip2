package org.folio.edge.sip2.domain.messages.requests;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Represents the Checkin message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * This message is used by the SC to request to check in an item, and also to
 * cancel a Checkout request that did not successfully complete. The ACS must
 * respond to this command with a Checkin Response message.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class Checkin {
  /**
   * When {@code TRUE} the ACS should not block this transaction because it has
   * already been executed. This happens when the ACS is off-line while the SC
   * performs transactions. These transactions are stored and sent to the ACS
   * when it comes on-line.
   */
  private final Boolean noBlock;
  /** The date and time the patron checked in the item at the SC. */
  private final OffsetDateTime transactionDate;
  /** The date the item was returned to the library. */
  private final OffsetDateTime returnDate;
  /** The location of the SC terminal. */
  private final String currentLocation;
  /** The ID of the institution making the request. */
  private final String institutionId;
  /** The ID of the item being checked in. */
  private final String itemIdentifier;
  /** The password used by the terminal. */
  private final String terminalPassword;
  /** Specific item information that can be user for identification. */
  private final String itemProperties;
  /**
   * {@code TRUE} to cancel a failed checkin command. {@code FALSE} for all
   * other situations.
   */
  private final Boolean cancel;

  /**
   * Construct a {@code Checkin} based on a {@code CheckinBuilder} object.
   * @param builder The basis for creating the check in.
   */
  private Checkin(CheckinBuilder builder) {
    this.noBlock = builder.noBlock;
    this.transactionDate = builder.transactionDate;
    this.returnDate = builder.returnDate;
    this.currentLocation = builder.currentLocation;
    this.institutionId = builder.institutionId;
    this.itemIdentifier = builder.itemIdentifier;
    this.terminalPassword = builder.terminalPassword;
    this.itemProperties = builder.itemProperties;
    this.cancel = builder.cancel;
  }


  /**
   * Returns a builder used to construct a {@code Checkin}.
   * @return A checkin builder.
   */
  public static CheckinBuilder builder() {
    return new CheckinBuilder();
  }

  public Boolean getNoBlock() {
    return noBlock;
  }

  public OffsetDateTime getTransactionDate() {
    return transactionDate;
  }

  public OffsetDateTime getReturnDate() {
    return returnDate;
  }

  public String getCurrentLocation() {
    return currentLocation;
  }

  public String getInstitutionId() {
    return institutionId;
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

  public Boolean getCancel() {
    return cancel;
  }

  @Override
  public int hashCode() {
    return Objects.hash(cancel, currentLocation, institutionId,
        itemIdentifier, itemProperties, noBlock, returnDate,
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
    if (!(obj instanceof Checkin)) {
      return false;
    }
    Checkin other = (Checkin) obj;
    return Objects.equals(cancel, other.cancel)
        && Objects.equals(currentLocation, other.currentLocation)
        && Objects.equals(institutionId, other.institutionId)
        && Objects.equals(itemIdentifier, other.itemIdentifier)
        && Objects.equals(itemProperties, other.itemProperties)
        && Objects.equals(noBlock, other.noBlock)
        && Objects.equals(returnDate, other.returnDate)
        && Objects.equals(terminalPassword, other.terminalPassword)
        && Objects.equals(transactionDate, other.transactionDate);
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("Checkin [noBlock=").append(noBlock)
        .append(", transactionDate=").append(transactionDate)
        .append(", returnDate=").append(returnDate)
        .append(", currentLocation=").append(currentLocation)
        .append(", institutionId=").append(institutionId)
        .append(", itemIdentifier=").append(itemIdentifier)
        .append(", terminalPassword=").append(terminalPassword)
        .append(", itemProperties=").append(itemProperties)
        .append(", cancel=").append(cancel)
        .append(']').toString();
  }

  /**
   * Returns Formatted Log Message.
   * @return String.
   */
  public String getCheckInLogInfo() {
    return new StringBuilder()
      .append("Checkin [noBlock=").append(noBlock)
      .append(", transactionDate=").append(transactionDate)
      .append(", returnDate=").append(returnDate)
      .append(", currentLocation=").append(currentLocation)
      .append(", institutionId=").append(institutionId)
      .append(", itemIdentifier=").append(itemIdentifier)
      .append(", itemProperties=").append(itemProperties)
      .append(", cancel=").append(cancel)
      .append(']').toString();
  }

  /**
   * Builder for {@code Checkin}.
   */
  public static class CheckinBuilder {
    private Boolean noBlock;
    private OffsetDateTime transactionDate;
    private OffsetDateTime returnDate;
    private String currentLocation;
    private String institutionId;
    private String itemIdentifier;
    private String terminalPassword;
    private String itemProperties;
    private Boolean cancel;

    private CheckinBuilder() {
      super();
    }

    public CheckinBuilder noBlock(Boolean noBlock) {
      this.noBlock = noBlock;
      return this;
    }

    public CheckinBuilder transactionDate(OffsetDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return this;
    }

    public CheckinBuilder returnDate(OffsetDateTime returnDate) {
      this.returnDate = returnDate;
      return this;
    }

    public CheckinBuilder currentLocation(String currentLocation) {
      this.currentLocation = currentLocation;
      return this;
    }

    public CheckinBuilder institutionId(String institutionId) {
      this.institutionId = institutionId;
      return this;
    }

    public CheckinBuilder itemIdentifier(String itemIdentifier) {
      this.itemIdentifier = itemIdentifier;
      return this;
    }

    public CheckinBuilder terminalPassword(String terminalPassword) {
      this.terminalPassword = terminalPassword;
      return this;
    }

    public CheckinBuilder itemProperties(String itemProperties) {
      this.itemProperties = itemProperties;
      return this;
    }

    public CheckinBuilder cancel(Boolean cancel) {
      this.cancel = cancel;
      return this;
    }

    public Checkin build() {
      return new Checkin(this);
    }
  }
}
