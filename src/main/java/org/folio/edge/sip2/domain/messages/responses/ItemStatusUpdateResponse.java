package org.folio.edge.sip2.domain.messages.responses;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents the Item Status Update Response message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * The ACS must send this message in response to the Item Status Update message.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class ItemStatusUpdateResponse {
  /**
   * {@code TRUE} indicates that the item properties have been stored on the
   * ACS database. {@code FALSE} indicates that item properties were not stored.
   */
  private final Boolean itemPropertiesOk;
  /** The date and time the response was sent. */
  private final ZonedDateTime transactionDate;
  /** The ID of the item being checked out. */
  private final String itemIdentifier;
  /** The ID of the title being checked out. */
  private final String titleIdentifier;
  /** Specific item information that can be user for identification. */
  private final String itemProperties;
  /** A message to show the patron. */
  private final List<String> screenMessage;
  /** A message to print via the SC's printer. */
  private final List<String> printLine;

  /**
   * Construct a {@code ItemStatusUpdateResponse} based on a
   * {@code ItemStatusUpdateResponseBuilder} object.
   * @param builder The basis for creating the item status update response.
   */
  private ItemStatusUpdateResponse(ItemStatusUpdateResponseBuilder builder) {
    this.itemPropertiesOk = builder.itemPropertiesOk;
    this.transactionDate = builder.transactionDate;
    this.itemIdentifier = builder.itemIdentifier;
    this.titleIdentifier = builder.titleIdentifier;
    this.itemProperties = builder.itemProperties;
    this.screenMessage = builder.screenMessage == null ? null
        : Collections.unmodifiableList(new ArrayList<>(builder.screenMessage));
    this.printLine = builder.printLine == null ? null
        : Collections.unmodifiableList(new ArrayList<>(builder.printLine));
  }

  /**
   * Returns a builder used to construct an {@code ItemStatusUpdateResponse}.
   * @return A item status update response builder.
   */
  public static ItemStatusUpdateResponseBuilder builder() {
    return new ItemStatusUpdateResponseBuilder();
  }

  public Boolean getItemPropertiesOk() {
    return itemPropertiesOk;
  }

  public ZonedDateTime getTransactionDate() {
    return transactionDate;
  }

  public String getItemIdentifier() {
    return itemIdentifier;
  }

  public String getTitleIdentifier() {
    return titleIdentifier;
  }

  public String getItemProperties() {
    return itemProperties;
  }

  public List<String> getScreenMessage() {
    return screenMessage;
  }

  public List<String> getPrintLine() {
    return printLine;
  }

  @Override
  public int hashCode() {
    return Objects.hash(itemIdentifier, itemProperties, itemPropertiesOk,
        printLine, screenMessage, titleIdentifier, transactionDate);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof ItemStatusUpdateResponse)) {
      return false;
    }
    ItemStatusUpdateResponse other = (ItemStatusUpdateResponse) obj;
    return Objects.equals(itemIdentifier, other.itemIdentifier)
        && Objects.equals(itemProperties, other.itemProperties)
        && Objects.equals(itemPropertiesOk, other.itemPropertiesOk)
        && Objects.equals(printLine, other.printLine)
        && Objects.equals(screenMessage, other.screenMessage)
        && Objects.equals(titleIdentifier, other.titleIdentifier)
        && Objects.equals(transactionDate, other.transactionDate);
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("ItemStatusUpdateResponse [itemPropertiesOk=")
        .append(itemPropertiesOk)
        .append(", transactionDate=").append(transactionDate)
        .append(", itemIdentifier=").append(itemIdentifier)
        .append(", titleIdentifier=").append(titleIdentifier)
        .append(", itemProperties=").append(itemProperties)
        .append(", screenMessage=").append(screenMessage)
        .append(", printLine=").append(printLine)
        .append(']').toString();
  }

  /**
   * Builder for {@code CheckoutResponse}.
   */
  public static class ItemStatusUpdateResponseBuilder {
    private Boolean itemPropertiesOk;
    private ZonedDateTime transactionDate;
    private String itemIdentifier;
    private String titleIdentifier;
    private String itemProperties;
    private List<String> screenMessage;
    private List<String> printLine;

    private ItemStatusUpdateResponseBuilder() {
      super();
    }

    public ItemStatusUpdateResponseBuilder itemPropertiesOk(
        Boolean itemPropertiesOk) {
      this.itemPropertiesOk = itemPropertiesOk;
      return this;
    }

    public ItemStatusUpdateResponseBuilder transactionDate(ZonedDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return this;
    }

    public ItemStatusUpdateResponseBuilder itemIdentifier(String itemIdentifier) {
      this.itemIdentifier = itemIdentifier;
      return this;
    }

    public ItemStatusUpdateResponseBuilder titleIdentifier(String titleIdentifier) {
      this.titleIdentifier = titleIdentifier;
      return this;
    }

    public ItemStatusUpdateResponseBuilder itemProperties(String itemProperties) {
      this.itemProperties = itemProperties;
      return this;
    }

    public ItemStatusUpdateResponseBuilder screenMessage(List<String> screenMessage) {
      this.screenMessage = screenMessage;
      return this;
    }

    public ItemStatusUpdateResponseBuilder printLine(List<String> printLine) {
      this.printLine = printLine;
      return this;
    }

    public ItemStatusUpdateResponse build() {
      return new ItemStatusUpdateResponse(this);
    }
  }
}
