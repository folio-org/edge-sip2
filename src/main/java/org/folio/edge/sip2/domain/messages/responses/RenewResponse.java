package org.folio.edge.sip2.domain.messages.responses;

import java.time.ZonedDateTime;
import java.util.Objects;

import org.folio.edge.sip2.domain.messages.enumerations.CurrencyType;
import org.folio.edge.sip2.domain.messages.enumerations.FeeType;
import org.folio.edge.sip2.domain.messages.enumerations.MediaType;

/**
 * Represents the Renew Response message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * This message must be sent by the ACS in response to a Renew message by the
 * SC.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class RenewResponse {
  /**
   * {@code TRUE} if the ACS renewed the item for the patron. {@code FALSE}
   * if the ACS did not renew the item for the patron.
   */
  private final Boolean ok;
  /**
   * {@code TRUE} if the patron requesting to renew the item already has
   * the item checked out. {@code FALSE} if the item is not already checked
   * out to the requesting patron.
   */
  private final Boolean renewalOk;
  /**
   * {@code TRUE} if the article is magnetic media so that the SC can handle
   * the security discharge accordingly. {@code FALSE} if the article is not
   * magnetic media. {@code null} if the ACS does not identify magnetic media
   * articles.
   */
  private final Boolean magneticMedia;
  /**
   * {@code TRUE} if the SC should desensitize the article. {@code FALSE} if
   * the SC should not desensitize the article, e.g. a closed reserve book or
   * the renewal refused. 
   */
  private final Boolean desensitize;
  /** The date and time the patron renewed the item at the SC. */
  private final ZonedDateTime transactionDate;
  /** The ID of the institution making the request. */
  private final String institutionId;
  /** The ID of the patron making the request. */
  private final String patronIdentifier;
  /** The ID of the item being renewed. */
  private final String itemIdentifier;
  /** The ID of the title being renewed. */
  private final String titleIdentifier;
  /** The due date given to item. */
  private final ZonedDateTime dueDate;
  /** The type of fee associated with renewing this item. */
  private final FeeType feeType;
  /**
   * {@code TRUE} if the SC should ignore the security status of the item.
   */
  private final Boolean securityInhibit;
  /** The fee currency type. */
  private final CurrencyType currencyType;
  /** The amount of the fee to renew the item. */
  private final String feeAmount;
  /** The media type of the item. */
  private final MediaType mediaType;
  /** Specific item information that can be user for identification. */
  private final String itemProperties;
  /** May be assigned by the ACS when renewing the item involves a fee. */
  private final String transactionId;
  /** A message to show the patron. */
  private final String screenMessage;
  /** A message to print via the SC's printer. */
  private final String printLine;

  /**
   * Construct a {@code RenewResponse} based on a
   * {@code RenewResponseBuilder} object.
   * @param builder The basis for creating the renew response.
   */
  private RenewResponse(RenewResponseBuilder builder) {
    this.ok = builder.ok;
    this.renewalOk = builder.renewalOk;
    this.magneticMedia = builder.magneticMedia;
    this.desensitize = builder.desensitize;
    this.transactionDate = builder.transactionDate;
    this.institutionId = builder.institutionId;
    this.patronIdentifier = builder.patronIdentifier;
    this.itemIdentifier = builder.itemIdentifier;
    this.titleIdentifier = builder.titleIdentifier;
    this.dueDate = builder.dueDate;
    this.feeType = builder.feeType;
    this.securityInhibit = builder.securityInhibit;
    this.currencyType = builder.currencyType;
    this.feeAmount = builder.feeAmount;
    this.mediaType = builder.mediaType;
    this.itemProperties = builder.itemProperties;
    this.transactionId = builder.transactionId;
    this.screenMessage = builder.screenMessage;
    this.printLine = builder.printLine;
  }

  /**
   * Returns a builder used to construct a {@code RenewResponse}.
   * @return A renew response builder.
   */
  public static RenewResponseBuilder builder() {
    return new RenewResponseBuilder();
  }

  public Boolean getOk() {
    return ok;
  }

  public Boolean getRenewalOk() {
    return renewalOk;
  }

  public Boolean getMagneticMedia() {
    return magneticMedia;
  }

  public Boolean getDesensitize() {
    return desensitize;
  }

  public ZonedDateTime getTransactionDate() {
    return transactionDate;
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

  public ZonedDateTime getDueDate() {
    return dueDate;
  }

  public FeeType getFeeType() {
    return feeType;
  }

  public Boolean getSecurityInhibit() {
    return securityInhibit;
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

  public String getItemProperties() {
    return itemProperties;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public String getScreenMessage() {
    return screenMessage;
  }

  public String getPrintLine() {
    return printLine;
  }

  @Override
  public int hashCode() {
    return Objects.hash(currencyType, desensitize, dueDate, feeAmount, feeType,
        institutionId, itemIdentifier, itemProperties, magneticMedia, mediaType,
        ok, patronIdentifier, printLine, renewalOk, screenMessage,
        securityInhibit, titleIdentifier, transactionDate, transactionId);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof RenewResponse)) {
      return false;
    }
    RenewResponse other = (RenewResponse) obj;
    return currencyType == other.currencyType
        && Objects.equals(desensitize, other.desensitize)
        && Objects.equals(dueDate, other.dueDate)
        && Objects.equals(feeAmount, other.feeAmount)
        && feeType == other.feeType
        && Objects.equals(institutionId, other.institutionId)
        && Objects.equals(itemIdentifier, other.itemIdentifier)
        && Objects.equals(itemProperties, other.itemProperties)
        && Objects.equals(magneticMedia, other.magneticMedia)
        && mediaType == other.mediaType
        && Objects.equals(ok, other.ok)
        && Objects.equals(patronIdentifier, other.patronIdentifier)
        && Objects.equals(printLine, other.printLine)
        && Objects.equals(renewalOk, other.renewalOk)
        && Objects.equals(screenMessage, other.screenMessage)
        && Objects.equals(securityInhibit, other.securityInhibit)
        && Objects.equals(titleIdentifier, other.titleIdentifier)
        && Objects.equals(transactionDate, other.transactionDate)
        && Objects.equals(transactionId, other.transactionId);
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("RenewResponse [ok=").append(ok)
        .append(", renewalOk=").append(renewalOk)
        .append(", magneticMedia=").append(magneticMedia)
        .append(", desensitize=").append(desensitize)
        .append(", transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", itemIdentifier=").append(itemIdentifier)
        .append(", titleIdentifier=").append(titleIdentifier)
        .append(", dueDate=").append(dueDate)
        .append(", feeType=").append(feeType)
        .append(", securityInhibit=").append(securityInhibit)
        .append(", currencyType=").append(currencyType)
        .append(", feeAmount=").append(feeAmount)
        .append(", mediaType=").append(mediaType)
        .append(", itemProperties=").append(itemProperties)
        .append(", transactionId=").append(transactionId)
        .append(", screenMessage=").append(screenMessage)
        .append(", printLine=").append(printLine)
        .append(']').toString();
  }

  /**
   * Builder for {@code RenewResponse}.
   */
  public static class RenewResponseBuilder {
    private Boolean ok;
    private Boolean renewalOk;
    private Boolean magneticMedia;
    private Boolean desensitize;
    private ZonedDateTime transactionDate;
    private String institutionId;
    private String patronIdentifier;
    private String itemIdentifier;
    private String titleIdentifier;
    private ZonedDateTime dueDate;
    private FeeType feeType;
    private Boolean securityInhibit;
    private CurrencyType currencyType;
    private String feeAmount;
    private MediaType mediaType;
    private String itemProperties;
    private String transactionId;
    private String screenMessage;
    private String printLine;

    private RenewResponseBuilder() {
      super();
    }

    public RenewResponseBuilder ok(Boolean ok) {
      this.ok = ok;
      return this;
    }

    public RenewResponseBuilder renewalOk(Boolean renewalOk) {
      this.renewalOk = renewalOk;
      return this;
    }

    public RenewResponseBuilder magneticMedia(Boolean magneticMedia) {
      this.magneticMedia = magneticMedia;
      return this;
    }

    public RenewResponseBuilder desensitize(Boolean desensitize) {
      this.desensitize = desensitize;
      return this;
    }

    public RenewResponseBuilder transactionDate(ZonedDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return this;
    }

    public RenewResponseBuilder institutionId(String institutionId) {
      this.institutionId = institutionId;
      return this;
    }

    public RenewResponseBuilder patronIdentifier(String patronIdentifier) {
      this.patronIdentifier = patronIdentifier;
      return this;
    }

    public RenewResponseBuilder itemIdentifier(String itemIdentifier) {
      this.itemIdentifier = itemIdentifier;
      return this;
    }

    public RenewResponseBuilder titleIdentifier(String titleIdentifier) {
      this.titleIdentifier = titleIdentifier;
      return this;
    }

    public RenewResponseBuilder dueDate(ZonedDateTime dueDate) {
      this.dueDate = dueDate;
      return this;
    }

    public RenewResponseBuilder feeType(FeeType feeType) {
      this.feeType = feeType;
      return this;
    }

    public RenewResponseBuilder securityInhibit(Boolean securityInhibit) {
      this.securityInhibit = securityInhibit;
      return this;
    }

    public RenewResponseBuilder currencyType(CurrencyType currencyType) {
      this.currencyType = currencyType;
      return this;
    }

    public RenewResponseBuilder feeAmount(String feeAmount) {
      this.feeAmount = feeAmount;
      return this;
    }

    public RenewResponseBuilder mediaType(MediaType mediaType) {
      this.mediaType = mediaType;
      return this;
    }

    public RenewResponseBuilder itemProperties(String itemProperties) {
      this.itemProperties = itemProperties;
      return this;
    }

    public RenewResponseBuilder transactionId(String transactionId) {
      this.transactionId = transactionId;
      return this;
    }

    public RenewResponseBuilder screenMessage(String screenMessage) {
      this.screenMessage = screenMessage;
      return this;
    }

    public RenewResponseBuilder printLine(String printLine) {
      this.printLine = printLine;
      return this;
    }

    public RenewResponse build() {
      return new RenewResponse(this);
    }
  }
}
