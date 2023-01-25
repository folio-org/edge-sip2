package org.folio.edge.sip2.domain.messages.requests;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Represents the Item Information message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * This message may be used to request item information. The ACS should respond
 * with the Item Information Response message.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class ItemInformation {
  /** The date of this transaction. */
  private final OffsetDateTime transactionDate;
  /** The ID of the institution making the request. */
  private final String institutionId;
  /** The ID of the requested item. */
  private final String itemIdentifier;
  /** The password used by the terminal. */
  private final String terminalPassword;

  /**
   * Construct a {@code ItemInformation} based on a
   * {@code ItemInformationBuilder} object.
   * @param builder The basis for creating the item information.
   */
  private ItemInformation(ItemInformationBuilder builder) {
    this.transactionDate = builder.transactionDate;
    this.institutionId = builder.institutionId;
    this.itemIdentifier = builder.itemIdentifier;
    this.terminalPassword = builder.terminalPassword;
  }

  /**
   * Returns a builder used to construct a {@code ItemInformation}.
   * @return A item information builder.
   */
  public static ItemInformationBuilder builder() {
    return new ItemInformationBuilder();
  }

  public OffsetDateTime getTransactionDate() {
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

  @Override
  public int hashCode() {
    return Objects.hash(institutionId, itemIdentifier, terminalPassword,
      transactionDate);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof ItemInformation)) {
      return false;
    }
    ItemInformation other = (ItemInformation) obj;
    return Objects.equals(institutionId, other.institutionId)
      && Objects.equals(itemIdentifier, other.itemIdentifier)
      && Objects.equals(terminalPassword, other.terminalPassword)
      && Objects.equals(transactionDate, other.transactionDate);
  }

  @Override
  public String toString() {
    return new StringBuilder()
      .append("ItemInformation [transactionDate=").append(transactionDate)
      .append(", institutionId=").append(institutionId)
      .append(", itemIdentifier=").append(itemIdentifier)
      .append(", terminalPassword=").append(terminalPassword)
      .append(']').toString();
  }

  /**
   * Builder for {@code ItemInformation}.
   */
  public static class ItemInformationBuilder {
    private OffsetDateTime transactionDate;
    private String institutionId;
    private String itemIdentifier;
    private String terminalPassword;

    private ItemInformationBuilder() {
      super();
    }

    public ItemInformationBuilder transactionDate(OffsetDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return this;
    }

    public ItemInformationBuilder institutionId(String institutionId) {
      this.institutionId = institutionId;
      return this;
    }

    public ItemInformationBuilder itemIdentifier(String itemIdentifier) {
      this.itemIdentifier = itemIdentifier;
      return this;
    }

    public ItemInformationBuilder terminalPassword(String terminalPassword) {
      this.terminalPassword = terminalPassword;
      return this;
    }

    public ItemInformation build() {
      return new ItemInformation(this);
    }
  }
}
