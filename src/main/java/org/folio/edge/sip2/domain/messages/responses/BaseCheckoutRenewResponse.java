package org.folio.edge.sip2.domain.messages.responses;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.folio.edge.sip2.domain.messages.enumerations.CurrencyType;
import org.folio.edge.sip2.domain.messages.enumerations.FeeType;
import org.folio.edge.sip2.domain.messages.enumerations.MediaType;

/**
 * Represents both the Checkout and the Renew responses. These messages are
 * identical, so the code has been abstracted into a base class for these two
 * messages.
 *
 * @author mreno-EBSCO
 *
 */
public abstract class BaseCheckoutRenewResponse {
  /**
   * {@code TRUE} if the ACS checked out the item to the patron. {@code FALSE}
   * if the ACS did not check out the item to the patron.
   */
  private final Boolean ok;
  /**
   * {@code TRUE} if the patron requesting to check out the item already has
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
   * the checkout was refused. 
   */
  private final Boolean desensitize;
  /** The date and time the patron checked out the item at the SC. */
  private final OffsetDateTime transactionDate;
  /** The ID of the institution making the request. */
  private final String institutionId;
  /** The ID of the patron making the request. */
  private final String patronIdentifier;
  /** The ID of the item being checked out. */
  private final String itemIdentifier;
  /** The ID of the title being checked out. */
  private final String titleIdentifier;
  /** The due date given to item. */
  private final OffsetDateTime dueDate;
  /** The type of fee associated with checking out this item. */
  private final FeeType feeType;
  /**
   * {@code TRUE} if the SC should ignore the security status of the item.
   */
  private final Boolean securityInhibit;
  /** The fee currency type. */
  private final CurrencyType currencyType;
  /** The amount of the fee to check out the item. */
  private final String feeAmount;
  /** The media type of the item. */
  private final MediaType mediaType;
  /** Specific item information that can be user for identification. */
  private final String itemProperties;
  /** May be assigned by the ACS when checking out the item involves a fee. */
  private final String transactionId;
  /** A message to show the patron. */
  private final List<String> screenMessage;
  /** A message to print via the SC's printer. */
  private final List<String> printLine;

  /**
   * Construct a {@code BaseCheckoutRenewResponse} based on a
   * {@code BaseCheckoutRenewResponseBuilder} object.
   * @param builder The basis for creating the base check out/renew response.
   */
  protected <T, B extends BaseCheckoutRenewResponseBuilder<T, B>>
      BaseCheckoutRenewResponse(
          BaseCheckoutRenewResponseBuilder<T, B> builder) {
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
    this.screenMessage = builder.screenMessage == null ? null
        : Collections.unmodifiableList(new ArrayList<>(builder.screenMessage));
    this.printLine = builder.printLine == null ? null
        : Collections.unmodifiableList(new ArrayList<>(builder.printLine));
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

  public OffsetDateTime getTransactionDate() {
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

  public OffsetDateTime getDueDate() {
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

  public List<String> getScreenMessage() {
    return screenMessage;
  }

  public List<String> getPrintLine() {
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
    if (!(obj instanceof BaseCheckoutRenewResponse)) {
      return false;
    }
    BaseCheckoutRenewResponse other = (BaseCheckoutRenewResponse) obj;
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
        .append("ok=").append(ok)
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
        .toString();
  }

  /**
   * Builder for {@code BaseCheckoutRenewResponse}.
   */
  protected abstract static class BaseCheckoutRenewResponseBuilder<T,
      B extends BaseCheckoutRenewResponseBuilder<T, B>> {
    private Boolean ok;
    private Boolean renewalOk;
    private Boolean magneticMedia;
    private Boolean desensitize;
    private OffsetDateTime transactionDate;
    private String institutionId;
    private String patronIdentifier;
    private String itemIdentifier;
    private String titleIdentifier;
    private OffsetDateTime dueDate;
    private FeeType feeType;
    private Boolean securityInhibit;
    private CurrencyType currencyType;
    private String feeAmount;
    private MediaType mediaType;
    private String itemProperties;
    private String transactionId;
    private List<String> screenMessage;
    private List<String> printLine;

    protected BaseCheckoutRenewResponseBuilder() {
      super();
    }

    public abstract T build();

    protected abstract B builder();

    public B ok(Boolean ok) {
      this.ok = ok;
      return builder();
    }

    public B renewalOk(Boolean renewalOk) {
      this.renewalOk = renewalOk;
      return builder();
    }

    public B magneticMedia(Boolean magneticMedia) {
      this.magneticMedia = magneticMedia;
      return builder();
    }

    public B desensitize(Boolean desensitize) {
      this.desensitize = desensitize;
      return builder();
    }

    public B transactionDate(OffsetDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return builder();
    }

    public B institutionId(String institutionId) {
      this.institutionId = institutionId;
      return builder();
    }

    public B patronIdentifier(String patronIdentifier) {
      this.patronIdentifier = patronIdentifier;
      return builder();
    }

    public B itemIdentifier(String itemIdentifier) {
      this.itemIdentifier = itemIdentifier;
      return builder();
    }

    public B titleIdentifier(String titleIdentifier) {
      this.titleIdentifier = titleIdentifier;
      return builder();
    }

    public B dueDate(OffsetDateTime dueDate) {
      this.dueDate = dueDate;
      return builder();
    }

    public B feeType(FeeType feeType) {
      this.feeType = feeType;
      return builder();
    }

    public B securityInhibit(Boolean securityInhibit) {
      this.securityInhibit = securityInhibit;
      return builder();
    }

    public B currencyType(CurrencyType currencyType) {
      this.currencyType = currencyType;
      return builder();
    }

    public B feeAmount(String feeAmount) {
      this.feeAmount = feeAmount;
      return builder();
    }

    public B mediaType(MediaType mediaType) {
      this.mediaType = mediaType;
      return builder();
    }

    public B itemProperties(String itemProperties) {
      this.itemProperties = itemProperties;
      return builder();
    }

    public B transactionId(String transactionId) {
      this.transactionId = transactionId;
      return builder();
    }

    public B screenMessage(List<String> screenMessage) {
      this.screenMessage = screenMessage;
      return builder();
    }

    public B printLine(List<String> printLine) {
      this.printLine = printLine;
      return builder();
    }
  }
}
