package org.folio.edge.sip2.domain.messages.responses;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents the Renew All Response message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * The ACS should send this message in response to a Renew All message from the
 * SC.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class RenewAllResponse {
  /**
   * {@code TRUE} if the ACS renewed all items for the patron. {@code FALSE}
   * if the ACS did not renew all items for the patron.
   */
  private final Boolean ok;
  /** A count of the number of items that were renewed. */
  private final Integer renewedCount;
  /** A count of the number of items that were not renewed. */
  private final Integer unrenewedCount;
  /** The date of this transaction. */
  private final ZonedDateTime transactionDate;
  /** The ID of the institution making the request. */
  private final String institutionId;
  /** List of items that were renewed. */
  private final List<String> renewedItems;
  /**
   * List of items that were not renewed. Each entry could also include a
   * reason that the item was not renewed.
   */
  private final List<String> unrenewedItems;
  /** A message to show to the patron on the SC screen. */
  private final List<String> screenMessage;
  /** A message to print for the patron on the SC printer. */
  private final List<String> printLine;

  /**
   * Construct a {@code RenewAllResponse} based on a
   * {@code RenewAllResponseBuilder} object.
   * @param builder The basis for creating the renew all response.
   */
  private RenewAllResponse(RenewAllResponseBuilder builder) {
    this.ok = builder.ok;
    this.renewedCount = builder.renewedCount;
    this.unrenewedCount = builder.unrenewedCount;
    this.transactionDate = builder.transactionDate;
    this.institutionId = builder.institutionId;
    this.renewedItems = builder.renewedItems == null
        ? emptyList()
        : unmodifiableList(new ArrayList<>(builder.renewedItems));
    this.unrenewedItems = builder.unrenewedItems == null
        ? emptyList()
        : unmodifiableList(new ArrayList<>(builder.unrenewedItems));
    this.screenMessage = builder.screenMessage == null ? null
            : Collections.unmodifiableList(new ArrayList<>(builder.screenMessage));
    this.printLine = builder.printLine == null ? null
            : Collections.unmodifiableList(new ArrayList<>(builder.printLine));
  }

  /**
   * Returns a builder used to construct a {@code RenewAllResponse}.
   * @return A renew all response builder.
   */
  public static RenewAllResponseBuilder builder() {
    return new RenewAllResponseBuilder();
  }

  public Boolean getOk() {
    return ok;
  }

  public Integer getRenewedCount() {
    return renewedCount;
  }

  public Integer getUnrenewedCount() {
    return unrenewedCount;
  }

  public ZonedDateTime getTransactionDate() {
    return transactionDate;
  }

  public String getInstitutionId() {
    return institutionId;
  }

  public List<String> getRenewedItems() {
    return renewedItems;
  }

  public List<String> getUnrenewedItems() {
    return unrenewedItems;
  }

  public List<String> getScreenMessage() {
    return screenMessage;
  }

  public List<String> getPrintLine() {
    return printLine;
  }

  @Override
  public int hashCode() {
    return Objects.hash(institutionId, ok, printLine, renewedCount,
        renewedItems, screenMessage, transactionDate, unrenewedCount,
        unrenewedItems);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof RenewAllResponse)) {
      return false;
    }
    RenewAllResponse other = (RenewAllResponse) obj;
    return Objects.equals(institutionId, other.institutionId)
        && Objects.equals(ok, other.ok)
        && Objects.equals(printLine, other.printLine)
        && Objects.equals(renewedCount, other.renewedCount)
        && Objects.equals(renewedItems, other.renewedItems)
        && Objects.equals(screenMessage, other.screenMessage)
        && Objects.equals(transactionDate, other.transactionDate)
        && Objects.equals(unrenewedCount, other.unrenewedCount)
        && Objects.equals(unrenewedItems, other.unrenewedItems);
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("RenewAllResponse [ok=").append(ok)
        .append(", renewedCount=").append(renewedCount)
        .append(", unrenewedCount=").append(unrenewedCount)
        .append(", transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", renewedItems=").append(renewedItems)
        .append(", unrenewedItems=").append(unrenewedItems)
        .append(", screenMessage=").append(screenMessage)
        .append(", printLine=").append(printLine)
        .append(']').toString();
  }

  /**
   * Builder for {@code RenewAllResponse}.
   */
  public static class RenewAllResponseBuilder {
    private Boolean ok;
    private Integer renewedCount;
    private Integer unrenewedCount;
    private ZonedDateTime transactionDate;
    private String institutionId;
    private List<String> renewedItems;
    private List<String> unrenewedItems;
    private List<String> screenMessage;
    private List<String> printLine;

    private RenewAllResponseBuilder() {
      super();
    }

    public RenewAllResponseBuilder ok(Boolean ok) {
      this.ok = ok;
      return this;
    }

    public RenewAllResponseBuilder renewedCount(Integer renewedCount) {
      this.renewedCount = renewedCount;
      return this;
    }

    public RenewAllResponseBuilder unrenewedCount(Integer unrenewedCount) {
      this.unrenewedCount = unrenewedCount;
      return this;
    }

    public RenewAllResponseBuilder transactionDate(
        ZonedDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return this;
    }

    public RenewAllResponseBuilder institutionId(String institutionId) {
      this.institutionId = institutionId;
      return this;
    }

    public RenewAllResponseBuilder renewedItems(List<String> renewedItems) {
      this.renewedItems = renewedItems;
      return this;
    }

    public RenewAllResponseBuilder unrenewedItems(List<String> unrenewedItems) {
      this.unrenewedItems = unrenewedItems;
      return this;
    }

    public RenewAllResponseBuilder screenMessage(List<String> screenMessage) {
      this.screenMessage = screenMessage;
      return this;
    }

    public RenewAllResponseBuilder printLine(List<String> printLine) {
      this.printLine = printLine;
      return this;
    }

    public RenewAllResponse build() {
      return new RenewAllResponse(this);
    }
  }
}
