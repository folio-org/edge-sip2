package org.folio.edge.sip2.domain.messages.requests;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Represents the Item Status Update message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * This message can be used to send item information to the ACS, without having
 * to do a Checkout or Checkin operation. The item properties could be stored
 * on the ACS’s database. The ACS should respond with an Item Status Update
 * Response message.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class ItemStatusUpdate {
  /** The date of this transaction. */
  private final ZonedDateTime transactionDate;
  /** The ID of the institution making the request. */
  private final String institutionId;
  /** The ID of the requested item. */
  private final String itemIdentifier;
  /** The password used by the terminal. */
  private final String terminalPassword;
  /** Specific item information that can be used it identify an item. */
  private final String itemProperties;

  /**
   * Construct a {@code ItemStatusUpdate} based on a
   * {@code ItemStatusUpdateBuilder} object.
   * @param builder The basis for creating the item status update.
   */
  private ItemStatusUpdate(ItemStatusUpdateBuilder builder) {
    this.transactionDate = builder.transactionDate;
    this.institutionId = builder.institutionId;
    this.itemIdentifier = builder.itemIdentifier;
    this.terminalPassword = builder.terminalPassword;
    this.itemProperties = builder.itemProperties;
  }

  /**
   * Returns a builder used to construct a {@code ItemStatusUpdate}.
   * @return A item status update builder.
   */
  public static ItemStatusUpdateBuilder builder() {
    return new ItemStatusUpdateBuilder();
  }

  public ZonedDateTime getTransactionDate() {
    return transactionDate;
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

  @Override
  public int hashCode() {
    return Objects.hash(institutionId, itemIdentifier, itemProperties,
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
    if (!(obj instanceof ItemStatusUpdate)) {
      return false;
    }
    ItemStatusUpdate other = (ItemStatusUpdate) obj;
    return Objects.equals(institutionId, other.institutionId)
        && Objects.equals(itemIdentifier, other.itemIdentifier)
        && Objects.equals(itemProperties, other.itemProperties)
        && Objects.equals(terminalPassword, other.terminalPassword)
        && Objects.equals(transactionDate, other.transactionDate);
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("ItemStatusUpdate [transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", itemIdentifier=").append(itemIdentifier)
        .append(", terminalPassword=").append(terminalPassword)
        .append(", itemProperties=").append(itemProperties)
        .append(']').toString();
  }

  /**
   * Builder for {@code ItemStatusUpdate}.
   */
  public static class ItemStatusUpdateBuilder {
    private ZonedDateTime transactionDate;
    private String institutionId;
    private String itemIdentifier;
    private String terminalPassword;
    private String itemProperties;

    private ItemStatusUpdateBuilder() {
      super();
    }

    public ItemStatusUpdateBuilder transactionDate(ZonedDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return this;
    }

    public ItemStatusUpdateBuilder institutionId(String institutionId) {
      this.institutionId = institutionId;
      return this;
    }

    public ItemStatusUpdateBuilder itemIdentifier(String itemIdentifier) {
      this.itemIdentifier = itemIdentifier;
      return this;
    }

    public ItemStatusUpdateBuilder terminalPassword(String terminalPassword) {
      this.terminalPassword = terminalPassword;
      return this;
    }

    public ItemStatusUpdateBuilder itemProperties(String itemProperties) {
      this.itemProperties = itemProperties;
      return this;
    }

    public ItemStatusUpdate build() {
      return new ItemStatusUpdate(this);
    }
  }
}
