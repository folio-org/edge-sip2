package org.folio.edge.sip2.domain.messages.responses;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Represents the Hold Response message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * The ACS should send this message in response to the Hold message from the SC.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class HoldResponse {
  /**
   * {@code TRUE} if the ACS held the item for the patron. {@code FALSE}
   * if the ACS did not hold the item for the patron.
   */
  private final Boolean ok;
  /**
   * {@code TRUE} indicates that the item is available and is not checked out
   * or on hold.
   */
  private final Boolean available;
  /** The date and time of the ACS response. */
  private final ZonedDateTime transactionDate;
  /** The date and time that the hold will expire. */
  private final ZonedDateTime expirationDate;
  /** The position the patron in the hold queue for this item. */
  private final Integer queuePosition;
  /** The location where the item will be picked up. */
  private final String pickupLocation;
  /** The ID of the institution making the request. */
  private final String institutionId;
  /** The ID of the patron making the request. */
  private final String patronIdentifier;
  /** The ID of the item being checked out. */
  private final String itemIdentifier;
  /** The ID of the title being checked out. */
  private final String titleIdentifier;
  /** A message to show the patron. */
  private final String screenMessage;
  /** A message to print via the SC's printer. */
  private final String printLine;

  /**
   * Construct a {@code HoldResponse} based on a
   * {@code HoldResponseBuilder} object.
   * @param builder The basis for creating the hold response.
   */
  private HoldResponse(HoldResponseBuilder builder) {
    this.ok = builder.ok;
    this.available = builder.available;
    this.transactionDate = builder.transactionDate;
    this.expirationDate = builder.expirationDate;
    this.queuePosition = builder.queuePosition;
    this.pickupLocation = builder.pickupLocation;
    this.institutionId = builder.institutionId;
    this.patronIdentifier = builder.patronIdentifier;
    this.itemIdentifier = builder.itemIdentifier;
    this.titleIdentifier = builder.titleIdentifier;
    this.screenMessage = builder.screenMessage;
    this.printLine = builder.printLine;
  }

  /**
   * Returns a builder used to construct a {@code HoldResponse}.
   * @return A hold response builder.
   */
  public static HoldResponseBuilder builder() {
    return new HoldResponseBuilder();
  }

  public Boolean getOk() {
    return ok;
  }

  public Boolean getAvailable() {
    return available;
  }

  public ZonedDateTime getTransactionDate() {
    return transactionDate;
  }

  public ZonedDateTime getExpirationDate() {
    return expirationDate;
  }

  public Integer getQueuePosition() {
    return queuePosition;
  }

  public String getPickupLocation() {
    return pickupLocation;
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

  public String getTitleIdentifier() {
    return titleIdentifier;
  }

  public String getScreenMessage() {
    return screenMessage;
  }

  public String getPrintLine() {
    return printLine;
  }

  @Override
  public int hashCode() {
    return Objects.hash(available, expirationDate, institutionId,
        itemIdentifier, ok, patronIdentifier, pickupLocation, printLine,
        queuePosition, screenMessage, titleIdentifier, transactionDate);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof HoldResponse)) {
      return false;
    }
    HoldResponse other = (HoldResponse) obj;
    return Objects.equals(available, other.available)
        && Objects.equals(expirationDate, other.expirationDate)
        && Objects.equals(institutionId, other.institutionId)
        && Objects.equals(itemIdentifier, other.itemIdentifier)
        && Objects.equals(ok, other.ok)
        && Objects.equals(patronIdentifier, other.patronIdentifier)
        && Objects.equals(pickupLocation, other.pickupLocation)
        && Objects.equals(printLine, other.printLine)
        && Objects.equals(queuePosition, other.queuePosition)
        && Objects.equals(screenMessage, other.screenMessage)
        && Objects.equals(titleIdentifier, other.titleIdentifier)
        && Objects.equals(transactionDate, other.transactionDate);
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("HoldResponse [ok=").append(ok)
        .append(", available=").append(available)
        .append(", transactionDate=").append(transactionDate)
        .append(", expirationDate=").append(expirationDate)
        .append(", queuePosition=").append(queuePosition)
        .append(", pickupLocation=").append(pickupLocation)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", itemIdentifier=").append(itemIdentifier)
        .append(", titleIdentifier=").append(titleIdentifier)
        .append(", screenMessage=").append(screenMessage)
        .append(", printLine=").append(printLine)
        .append(']').toString();
  }

  /**
   * Builder for {@code HoldResponse}.
   */
  public static class HoldResponseBuilder {
    private Boolean ok;
    private Boolean available;
    private ZonedDateTime transactionDate;
    private ZonedDateTime expirationDate;
    private Integer queuePosition;
    private String pickupLocation;
    private String institutionId;
    private String patronIdentifier;
    private String itemIdentifier;
    private String titleIdentifier;
    private String screenMessage;
    private String printLine;

    private HoldResponseBuilder() {
      super();
    }

    public HoldResponseBuilder ok(Boolean ok) {
      this.ok = ok;
      return this;
    }

    public HoldResponseBuilder available(Boolean available) {
      this.available = available;
      return this;
    }

    public HoldResponseBuilder transactionDate(ZonedDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return this;
    }

    public HoldResponseBuilder expirationDate(ZonedDateTime expirationDate) {
      this.expirationDate = expirationDate;
      return this;
    }

    public HoldResponseBuilder queuePosition(Integer queuePosition) {
      this.queuePosition = queuePosition;
      return this;
    }

    public HoldResponseBuilder pickupLocation(String pickupLocation) {
      this.pickupLocation = pickupLocation;
      return this;
    }

    public HoldResponseBuilder institutionId(String institutionId) {
      this.institutionId = institutionId;
      return this;
    }

    public HoldResponseBuilder patronIdentifier(String patronIdentifier) {
      this.patronIdentifier = patronIdentifier;
      return this;
    }

    public HoldResponseBuilder itemIdentifier(String itemIdentifier) {
      this.itemIdentifier = itemIdentifier;
      return this;
    }

    public HoldResponseBuilder titleIdentifier(String titleIdentifier) {
      this.titleIdentifier = titleIdentifier;
      return this;
    }

    public HoldResponseBuilder screenMessage(String screenMessage) {
      this.screenMessage = screenMessage;
      return this;
    }

    public HoldResponseBuilder printLine(String printLine) {
      this.printLine = printLine;
      return this;
    }

    public HoldResponse build() {
      return new HoldResponse(this);
    }
  }
}
