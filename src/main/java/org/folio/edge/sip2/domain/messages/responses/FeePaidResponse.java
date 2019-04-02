package org.folio.edge.sip2.domain.messages.responses;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Represents the Fee Paid Response message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * The ACS must send this message in response to the Fee Paid message.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class FeePaidResponse {
  /**
   * {@code TRUE} indicates that the ACS has accepted the payment from the
   * patron and the patron's account will be adjusted accordingly.
   */
  private final Boolean paymentAccepted;
  /** The date of this transaction. */
  private final ZonedDateTime transactionDate;
  /** The ID of the institution making the request. */
  private final String institutionId;
  /** The ID of the patron making the request. */
  private final String patronIdentifier;
  /** May be assigned by the ACS to acknowledge that payment was received. */
  private final String transactionId;
  /** A message to show to the patron on the SC screen. */
  private final String screenMessage;
  /** A message to print for the patron on the SC printer. */
  private final String printLine;

  /**
   * Construct a {@code FeePaidResponse} based on a
   * {@code FeePaidResponseBuilder} object.
   * @param builder The basis for creating the fee paid response.
   */
  private FeePaidResponse(FeePaidResponseBuilder builder) {
    this.paymentAccepted = builder.paymentAccepted;
    this.transactionDate = builder.transactionDate;
    this.institutionId = builder.institutionId;
    this.patronIdentifier = builder.patronIdentifier;
    this.transactionId = builder.transactionId;
    this.screenMessage = builder.screenMessage;
    this.printLine = builder.printLine;
  }

  /**
   * Returns a builder used to construct a {@code FeePaidResponse}.
   * @return An fee paid response builder.
   */
  public static FeePaidResponseBuilder builder() {
    return new FeePaidResponseBuilder();
  }

  public Boolean getPaymentAccepted() {
    return paymentAccepted;
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
    return Objects.hash(institutionId, patronIdentifier, paymentAccepted,
        printLine, screenMessage, transactionDate, transactionId);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof FeePaidResponse)) {
      return false;
    }
    FeePaidResponse other = (FeePaidResponse) obj;
    return Objects.equals(institutionId, other.institutionId)
        && Objects.equals(patronIdentifier, other.patronIdentifier)
        && Objects.equals(paymentAccepted, other.paymentAccepted)
        && Objects.equals(printLine, other.printLine)
        && Objects.equals(screenMessage, other.screenMessage)
        && Objects.equals(transactionDate, other.transactionDate)
        && Objects.equals(transactionId, other.transactionId);
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("FeePaidResponse [paymentAccepted=").append(paymentAccepted)
        .append(", transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", transactionId=").append(transactionId)
        .append(", screenMessage=").append(screenMessage)
        .append(", printLine=").append(printLine)
        .append(']').toString();
  }

  /**
   * Builder for {@code FeePaidResponse}.
   */
  public static class FeePaidResponseBuilder {
    private Boolean paymentAccepted;
    private ZonedDateTime transactionDate;
    private String institutionId;
    private String patronIdentifier;
    private String transactionId;
    private String screenMessage;
    private String printLine;

    private FeePaidResponseBuilder() {
      super();
    }

    public FeePaidResponseBuilder paymentAccepted(Boolean paymentAccepted) {
      this.paymentAccepted = paymentAccepted;
      return this;
    }

    public FeePaidResponseBuilder transactionDate(
        ZonedDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return this;
    }

    public FeePaidResponseBuilder institutionId(String institutionId) {
      this.institutionId = institutionId;
      return this;
    }

    public FeePaidResponseBuilder patronIdentifier(String patronIdentifier) {
      this.patronIdentifier = patronIdentifier;
      return this;
    }

    public FeePaidResponseBuilder transactionId(String transactionId) {
      this.transactionId = transactionId;
      return this;
    }

    public FeePaidResponseBuilder screenMessage(String screenMessage) {
      this.screenMessage = screenMessage;
      return this;
    }

    public FeePaidResponseBuilder printLine(String printLine) {
      this.printLine = printLine;
      return this;
    }

    public FeePaidResponse build() {
      return new FeePaidResponse(this);
    }
  }
}
