package org.folio.edge.sip2.domain.messages.responses;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.folio.edge.sip2.domain.messages.enumerations.MediaType;

/**
 * Represents the Checkin Response message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * This message must be sent by the ACS in response to a SC Checkin message.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class CheckinResponse {
  /**
   * {@code TRUE} if the ACS checked in the item. {@code FALSE} if the ACS did
   * not check in the item.
   */
  private final Boolean ok;
  /**
   * {@code TRUE} if the SC should resensitize the article. {@code FALSE} if
   * the SC should not resensitize the article, e.g. a closed reserve book or
   * the checkin was refused.
   */
  private final Boolean resensitize;
  /**
   * {@code TRUE} if the article is magnetic media so that the SC can handle
   * the security discharge accordingly. {@code FALSE} if the article is not
   * magnetic media. {@code null} if the ACS does not identify magnetic media
   * articles.
   */
  private final Boolean magneticMedia;
  /**
   * {@code TRUE} if the SC should generate an audible sound. {@code FALSE} if
   * the SC should not generate an audible sound. The alert signal will alert
   * the library staff to special article handling conditions during discharging
   * operations.
   */
  private final Boolean alert;
  /** The date and time the patron checked in the item at the SC. */
  private final OffsetDateTime transactionDate;
  /** The ID of the institution making the request. */
  private final String institutionId;
  /** The ID of the item being checked in. */
  private final String itemIdentifier;
  /** The location where the item is normally stored after being checked in. */
  private final String permanentLocation;
  /** The ID of the title being checked in. */
  private final String titleIdentifier;
  /** A bin number indicating how the item should be sorted.*/
  private final String sortBin;
  /** The ID of the patron making the request. */
  private final String patronIdentifier;
  /** The media type of the item. */
  private final MediaType mediaType;
  /** Specific item information that can be user for identification. */
  private final String itemProperties;
  /** A message to show the patron. */
  private final List<String> screenMessage;
  /** A message to print via the SC's printer. */
  private final List<String> printLine;
  /** The call number of the item -- Extended field. */
  private final String callNumber;
  /** The alert type code of the item -- Extended field. */
  private final String alertType;
  /** The pickup service point of the item, if in transit -- Extended field. */
  private final String pickupServicePoint;

  /**
   * Construct a {@code CheckintResponse} based on a
   * {@code CheckinResponseBuilder} object.
   * @param builder The basis for creating the check in response.
   */
  private CheckinResponse(CheckinResponseBuilder builder) {
    this.ok = builder.ok;
    this.resensitize = builder.resensitize;
    this.magneticMedia = builder.magneticMedia;
    this.alert = builder.alert;
    this.transactionDate = builder.transactionDate;
    this.institutionId = builder.institutionId;
    this.itemIdentifier = builder.itemIdentifier;
    this.permanentLocation = builder.permanentLocation;
    this.titleIdentifier = builder.titleIdentifier;
    this.sortBin = builder.sortBin;
    this.patronIdentifier = builder.patronIdentifier;
    this.mediaType = builder.mediaType;
    this.itemProperties = builder.itemProperties;
    this.screenMessage = builder.screenMessage == null ? null
        : Collections.unmodifiableList(new ArrayList<>(builder.screenMessage));
    this.printLine = builder.printLine == null ? null
        : Collections.unmodifiableList(new ArrayList<>(builder.printLine));
    this.callNumber = builder.callNumber;
    this.alertType = builder.alertType;
    this.pickupServicePoint = builder.pickupServicePoint;
  }

  /**
   * Returns a builder used to construct a {@code CheckinResponse}.
   * @return A checkin response builder.
   */
  public static CheckinResponseBuilder builder() {
    return new CheckinResponseBuilder();
  }

  public Boolean getOk() {
    return ok;
  }

  public Boolean getResensitize() {
    return resensitize;
  }

  public Boolean getMagneticMedia() {
    return magneticMedia;
  }

  public Boolean getAlert() {
    return alert;
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

  public String getPermanentLocation() {
    return permanentLocation;
  }

  public String getTitleIdentifier() {
    return titleIdentifier;
  }

  public String getSortBin() {
    return sortBin;
  }

  public String getPatronIdentifier() {
    return patronIdentifier;
  }

  public MediaType getMediaType() {
    return mediaType;
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

  public String getCallNumber() {
    return callNumber;
  }

  public String getAlertType() {
    return alertType;
  }

  public String getPickupServicePoint() {
    return pickupServicePoint;
  }

  @Override
  public int hashCode() {
    return Objects.hash(alert, institutionId, itemIdentifier, itemProperties,
        magneticMedia, mediaType, ok, patronIdentifier, permanentLocation,
        printLine, resensitize, screenMessage, sortBin, titleIdentifier,
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
    if (!(obj instanceof CheckinResponse)) {
      return false;
    }
    CheckinResponse other = (CheckinResponse) obj;
    return Objects.equals(alert, other.alert)
        && Objects.equals(institutionId, other.institutionId)
        && Objects.equals(itemIdentifier, other.itemIdentifier)
        && Objects.equals(itemProperties, other.itemProperties)
        && Objects.equals(magneticMedia, other.magneticMedia)
        && mediaType == other.mediaType
        && Objects.equals(ok, other.ok)
        && Objects.equals(patronIdentifier, other.patronIdentifier)
        && Objects.equals(permanentLocation, other.permanentLocation)
        && Objects.equals(printLine, other.printLine)
        && Objects.equals(resensitize, other.resensitize)
        && Objects.equals(screenMessage, other.screenMessage)
        && Objects.equals(sortBin, other.sortBin)
        && Objects.equals(titleIdentifier, other.titleIdentifier)
        && Objects.equals(transactionDate, other.transactionDate)
        && Objects.equals(callNumber, other.callNumber)
        && Objects.equals(alertType, other.alertType)
        && Objects.equals(pickupServicePoint, other.pickupServicePoint);
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("CheckinResponse [ok=").append(ok)
        .append(", resensitize=").append(resensitize)
        .append(", magneticMedia=").append(magneticMedia)
        .append(", alert=").append(alert)
        .append(", transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", itemIdentifier=").append(itemIdentifier)
        .append(", permanentLocation=").append(permanentLocation)
        .append(", titleIdentifier=").append(titleIdentifier)
        .append(", sortBin=").append(sortBin)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", mediaType=").append(mediaType)
        .append(", itemProperties=").append(itemProperties)
        .append(", screenMessage=").append(screenMessage)
        .append(", printLine=").append(printLine)
        .append(", callNumber=").append(callNumber)
        .append(", alertType=").append(alertType)
        .append(", pickupServicePoint=").append(pickupServicePoint)
        .append(']').toString();
  }

  /**
   * Builder for {@code CheckinResponse}.
   */
  public static class CheckinResponseBuilder {
    private Boolean ok;
    private Boolean resensitize;
    private Boolean magneticMedia;
    private Boolean alert;
    private OffsetDateTime transactionDate;
    private String institutionId;
    private String itemIdentifier;
    private String permanentLocation;
    private String titleIdentifier;
    private String sortBin;
    private String patronIdentifier;
    private MediaType mediaType;
    private String itemProperties;
    private List<String> screenMessage;
    private List<String> printLine;
    private String callNumber;
    private String alertType;
    private String pickupServicePoint;

    private CheckinResponseBuilder() {
      super();
    }

    public CheckinResponseBuilder ok(Boolean ok) {
      this.ok = ok;
      return this;
    }

    public CheckinResponseBuilder resensitize(Boolean resensitize) {
      this.resensitize = resensitize;
      return this;
    }

    public CheckinResponseBuilder magneticMedia(Boolean magneticMedia) {
      this.magneticMedia = magneticMedia;
      return this;
    }

    public CheckinResponseBuilder alert(Boolean alert) {
      this.alert = alert;
      return this;
    }

    public CheckinResponseBuilder transactionDate(OffsetDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return this;
    }

    public CheckinResponseBuilder institutionId(String institutionId) {
      this.institutionId = institutionId;
      return this;
    }

    public CheckinResponseBuilder itemIdentifier(String itemIdentifier) {
      this.itemIdentifier = itemIdentifier;
      return this;
    }

    public CheckinResponseBuilder permanentLocation(String permanentLocation) {
      this.permanentLocation = permanentLocation;
      return this;
    }

    public CheckinResponseBuilder titleIdentifier(String titleIdentifier) {
      this.titleIdentifier = titleIdentifier;
      return this;
    }

    public CheckinResponseBuilder sortBin(String sortBin) {
      this.sortBin = sortBin;
      return this;
    }

    public CheckinResponseBuilder patronIdentifier(String patronIdentifier) {
      this.patronIdentifier = patronIdentifier;
      return this;
    }

    public CheckinResponseBuilder mediaType(MediaType mediaType) {
      this.mediaType = mediaType;
      return this;
    }

    public CheckinResponseBuilder itemProperties(String itemProperties) {
      this.itemProperties = itemProperties;
      return this;
    }

    public CheckinResponseBuilder screenMessage(List<String> screenMessage) {
      this.screenMessage = screenMessage;
      return this;
    }

    public CheckinResponseBuilder printLine(List<String> printLine) {
      this.printLine = printLine;
      return this;
    }

    public CheckinResponseBuilder callNumber(String callNumber) {
      this.callNumber = callNumber;
      return this;
    }

    public CheckinResponseBuilder alertType(String alertType) {
      this.alertType = alertType;
      return this;
    }

    public CheckinResponseBuilder pickupServicePoint(String pickupServicePoint) {
      this.pickupServicePoint = pickupServicePoint;
      return this;
    }

    public CheckinResponse build() {
      return new CheckinResponse(this);
    }
  }
}
