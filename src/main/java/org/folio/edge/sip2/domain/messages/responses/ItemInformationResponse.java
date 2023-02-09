package org.folio.edge.sip2.domain.messages.responses;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.folio.edge.sip2.domain.messages.enumerations.CirculationStatus;
import org.folio.edge.sip2.domain.messages.enumerations.CurrencyType;
import org.folio.edge.sip2.domain.messages.enumerations.FeeType;
import org.folio.edge.sip2.domain.messages.enumerations.MediaType;
import org.folio.edge.sip2.domain.messages.enumerations.SecurityMarker;

/**
 * Represents the Item Information Response message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * The ACS must send this message in response to the Item Information message.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class ItemInformationResponse {
  /** The circulation status of the item. */
  private final CirculationStatus circulationStatus;
  /** The security marker on the item. */
  private final SecurityMarker securityMarker;
  /** The type of fee associated with checking out this item. */
  private final FeeType feeType;
  /** The date and time the patron checked out the item at the SC. */
  private final OffsetDateTime transactionDate;
  /** Number of patrons requesting this item. */
  private final Integer holdQueueLength;
  /** The due date given to item. */
  private final OffsetDateTime dueDate;
  /** The date that the recall was issued. */
  private final OffsetDateTime recallDate;
  /** The date the hold expires. */
  private final OffsetDateTime holdPickupDate;
  /** The ID of the item being checked out. */
  private final String itemIdentifier;
  /** The ID of the title being checked out. */
  private final String titleIdentifier;
  /** The name of the institution or library that owns the time. */
  private final String owner;
  /** The fee currency type. */
  private final CurrencyType currencyType;
  /** The amount of the fee to check out the time. */
  private final String feeAmount;
  /** The media type of the item. */
  private final MediaType mediaType;
  /** The location where the item is normally stored after being checked in. */
  private final String permanentLocation;
  /** The current location of the item. */
  private final String currentLocation;
  /** Specific item information that can be user for identification. */
  private final String itemProperties;
  /** The patron ID on the next hold for this item. */
  private final List<String> screenMessage;
  /** A message to print via the SC's printer. */
  private final List<String> printLine;

  /**
   * Construct a {@code ItemInformationResponse} based on a
   * {@code ItemInformationResponseBuilder} object.
   * @param builder The basis for creating the item information response.
   */
  private ItemInformationResponse(ItemInformationResponseBuilder builder) {
    this.circulationStatus = builder.circulationStatus;
    this.securityMarker = builder.securityMarker;
    this.feeType = builder.feeType;
    this.transactionDate = builder.transactionDate;
    this.holdQueueLength = builder.holdQueueLength;
    this.dueDate = builder.dueDate;
    this.recallDate = builder.recallDate;
    this.holdPickupDate = builder.holdPickupDate;
    this.itemIdentifier = builder.itemIdentifier;
    this.titleIdentifier = builder.titleIdentifier;
    this.owner = builder.owner;
    this.currencyType = builder.currencyType;
    this.feeAmount = builder.feeAmount;
    this.mediaType = builder.mediaType;
    this.permanentLocation = builder.permanentLocation;
    this.currentLocation = builder.currentLocation;
    this.itemProperties = builder.itemProperties;
    this.screenMessage = builder.screenMessage == null ? null
      : Collections.unmodifiableList(new ArrayList<>(builder.screenMessage));
    this.printLine = builder.printLine == null ? null
      : Collections.unmodifiableList(new ArrayList<>(builder.printLine));
  }

  /**
   * Returns a builder used to construct a {@code ItemInformationResponse}.
   * @return An item information response builder.
   */
  public static ItemInformationResponseBuilder builder() {
    return new ItemInformationResponseBuilder();
  }

  public CirculationStatus getCirculationStatus() {
    return circulationStatus;
  }

  public SecurityMarker getSecurityMarker() {
    return securityMarker;
  }

  public FeeType getFeeType() {
    return feeType;
  }

  public OffsetDateTime getTransactionDate() {
    return transactionDate;
  }

  public Integer getHoldQueueLength() {
    return holdQueueLength;
  }

  public OffsetDateTime getDueDate() {
    return dueDate;
  }

  public OffsetDateTime getRecallDate() {
    return recallDate;
  }

  public OffsetDateTime getHoldPickupDate() {
    return holdPickupDate;
  }

  public String getItemIdentifier() {
    return itemIdentifier;
  }

  public String getTitleIdentifier() {
    return titleIdentifier;
  }

  public String getOwner() {
    return owner;
  }

  public CurrencyType getCurrencyType() {
    return currencyType;
  }

  public String getFeeAmount() {
    return feeAmount;
  }

  public MediaType getMediaType() {
    return mediaType;
  }

  public String getPermanentLocation() {
    return permanentLocation;
  }

  public String getCurrentLocation() {
    return currentLocation;
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
    return Objects.hash(circulationStatus, currencyType, currentLocation,
      dueDate, feeAmount, feeType, holdPickupDate, holdQueueLength,
      itemIdentifier, itemProperties, mediaType, owner, permanentLocation,
      printLine, recallDate,
      screenMessage, securityMarker, titleIdentifier, transactionDate);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof ItemInformationResponse)) {
      return false;
    }
    ItemInformationResponse other = (ItemInformationResponse) obj;
    return circulationStatus == other.circulationStatus
      && currencyType == other.currencyType
      && Objects.equals(currentLocation, other.currentLocation)
      && Objects.equals(dueDate, other.dueDate)
      && Objects.equals(feeAmount, other.feeAmount)
      && feeType == other.feeType
      && Objects.equals(holdPickupDate, other.holdPickupDate)
      && Objects.equals(holdQueueLength, other.holdQueueLength)
      && Objects.equals(itemIdentifier, other.itemIdentifier)
      && Objects.equals(itemProperties, other.itemProperties)
      && mediaType == other.mediaType
      && Objects.equals(owner, other.owner)
      && Objects.equals(permanentLocation, other.permanentLocation)
      && Objects.equals(printLine, other.printLine)
      && Objects.equals(recallDate, other.recallDate)
      && Objects.equals(screenMessage, other.screenMessage)
      && securityMarker == other.securityMarker
      && Objects.equals(titleIdentifier, other.titleIdentifier)
      && Objects.equals(transactionDate, other.transactionDate);
  }

  @Override
  public String toString() {
    return new StringBuilder()
      .append("ItemInformationResponse [circulationStatus=").append(circulationStatus)
      .append(", securityMarker=").append(securityMarker)
      .append(", feeType=").append(feeType)
      .append(", transactionDate=").append(transactionDate)
      .append(", holdQueueLength=").append(holdQueueLength)
      .append(", dueDate=").append(dueDate)
      .append(", recallDate=").append(recallDate)
      .append(", holdPickupDate=").append(holdPickupDate)
      .append(", itemIdentifier=").append(itemIdentifier)
      .append(", titleIdentifier=").append(titleIdentifier)
      .append(", owner=").append(owner)
      .append(", currencyType=").append(currencyType)
      .append(", feeAmount=").append(feeAmount)
      .append(", mediaType=").append(mediaType)
      .append(", permanentLocation=").append(permanentLocation)
      .append(", currentLocation=").append(currentLocation)
      .append(", itemProperties=").append(itemProperties)
      .append(", screenMessage=").append(screenMessage)
      .append(", printLine=").append(printLine)
      .append(']').toString();
  }

  /**
   * Builder for {@code ItemInformationResponse}.
   */
  public static class ItemInformationResponseBuilder {
    private CirculationStatus circulationStatus;
    private SecurityMarker securityMarker;
    private FeeType feeType;
    private OffsetDateTime transactionDate;
    private Integer holdQueueLength;
    private OffsetDateTime dueDate;
    private OffsetDateTime recallDate;
    private OffsetDateTime holdPickupDate;
    private String itemIdentifier;
    private String titleIdentifier;
    private String owner;
    private CurrencyType currencyType;
    private String feeAmount;
    private MediaType mediaType;
    private String permanentLocation;
    private String currentLocation;
    private String itemProperties;
    private List<String> screenMessage;
    private List<String> printLine;

    private ItemInformationResponseBuilder() {
      super();
    }

    public ItemInformationResponseBuilder circulationStatus(
        CirculationStatus circulationStatus) {
      this.circulationStatus = circulationStatus;
      return this;
    }

    public ItemInformationResponseBuilder securityMarker(
        SecurityMarker securityMarker) {
      this.securityMarker = securityMarker;
      return this;
    }

    public ItemInformationResponseBuilder feeType(FeeType feeType) {
      this.feeType = feeType;
      return this;
    }

    public ItemInformationResponseBuilder transactionDate(
        OffsetDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return this;
    }

    public ItemInformationResponseBuilder holdQueueLength(
        Integer holdQueueLength) {
      this.holdQueueLength = holdQueueLength;
      return this;
    }

    public ItemInformationResponseBuilder dueDate(OffsetDateTime dueDate) {
      this.dueDate = dueDate;
      return this;
    }

    public ItemInformationResponseBuilder recallDate(
        OffsetDateTime recallDate) {
      this.recallDate = recallDate;
      return this;
    }

    public ItemInformationResponseBuilder holdPickupDate(
        OffsetDateTime holdPickupDate) {
      this.holdPickupDate = holdPickupDate;
      return this;
    }

    public ItemInformationResponseBuilder itemIdentifier(
        String itemIdentifier) {
      this.itemIdentifier = itemIdentifier;
      return this;
    }

    public ItemInformationResponseBuilder titleIdentifier(
        String titleIdentifier) {
      this.titleIdentifier = titleIdentifier;
      return this;
    }

    public ItemInformationResponseBuilder owner(String owner) {
      this.owner = owner;
      return this;
    }

    public ItemInformationResponseBuilder currencyType(
        CurrencyType currencyType) {
      this.currencyType = currencyType;
      return this;
    }

    public ItemInformationResponseBuilder feeAmount(String feeAmount) {
      this.feeAmount = feeAmount;
      return this;
    }

    public ItemInformationResponseBuilder mediaType(MediaType mediaType) {
      this.mediaType = mediaType;
      return this;
    }

    public ItemInformationResponseBuilder permanentLocation(
        String permanentLocation) {
      this.permanentLocation = permanentLocation;
      return this;
    }

    public ItemInformationResponseBuilder currentLocation(String currentLocation) {
      this.currentLocation = currentLocation;
      return this;
    }

    public ItemInformationResponseBuilder itemProperties(String itemProperties) {
      this.itemProperties = itemProperties;
      return this;
    }

    public ItemInformationResponseBuilder screenMessage(List<String> screenMessage) {
      this.screenMessage = screenMessage;
      return this;
    }

    public ItemInformationResponseBuilder printLine(List<String> printLine) {
      this.printLine = printLine;
      return this;
    }

    public ItemInformationResponse build() {
      return new ItemInformationResponse(this);
    }
  }
}
