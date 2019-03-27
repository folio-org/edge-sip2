package org.folio.edge.sip2.domain.messages.requests;

import java.time.ZonedDateTime;
import java.util.Objects;

import org.folio.edge.sip2.domain.messages.enumerations.CurrencyType;
import org.folio.edge.sip2.domain.messages.enumerations.FeeType;
import org.folio.edge.sip2.domain.messages.enumerations.PaymentType;

/**
 * Represents the Fee Paid message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * This message can be used to notify the ACS that a fee has been collected
 * from the patron. The ACS should record this information in their database
 * and respond with a Fee Paid Response message.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class FeePaid {
  /** The date of this transaction. */
  private final ZonedDateTime transactionDate;
  /** The type of the fee. */
  private final FeeType feeType;
  /** The type of payment. */
  private final PaymentType paymentType;
  /** The type of currency. */
  private final CurrencyType currencyType;
  /**
   * The amount of money in {@code currencyType} for the fee.
   */
  private final String feeAmount;
  /** The ID of the institution making the request. */
  private final String institutionId;
  /** The ID of the patron making the request. */
  private final String patronIdentifier;
  /** The password used by the terminal. */
  private final String terminalPassword;
  /** The password used by the patron. */
  private final String patronPassword;
  /** Identifies a specific fee to apply the payment to. */
  private final String feeIdentifier;
  /** A transaction ID assigned by the payment device. */
  private final String transactionId;

  /**
   * Construct a {@code FeePaid} based on a
   * {@code FeePaidBuilder} object.
   * @param builder The basis for creating the fee paid.
   */
  private FeePaid(FeePaidBuilder builder) {
    this.transactionDate = builder.transactionDate;
    this.feeType = builder.feeType;
    this.paymentType = builder.paymentType;
    this.currencyType = builder.currencyType;
    this.feeAmount = builder.feeAmount;
    this.institutionId = builder.institutionId;
    this.patronIdentifier = builder.patronIdentifier;
    this.terminalPassword = builder.terminalPassword;
    this.patronPassword = builder.patronPassword;
    this.feeIdentifier = builder.feeIdentifier;
    this.transactionId = builder.transactionId;
  }

  /**
   * Returns a builder used to construct a {@code FeePaid}.
   * @return A fee paid builder.
   */
  public static FeePaidBuilder builder() {
    return new FeePaidBuilder();
  }

  public ZonedDateTime getTransactionDate() {
    return transactionDate;
  }

  public FeeType getFeeType() {
    return feeType;
  }

  public PaymentType getPaymentType() {
    return paymentType;
  }

  public CurrencyType getCurrencyType() {
    return currencyType;
  }

  public String getFeeAmount() {
    return feeAmount;
  }

  public String getInstitutionId() {
    return institutionId;
  }

  public String getPatronIdentifier() {
    return patronIdentifier;
  }

  public String getTerminalPassword() {
    return terminalPassword;
  }

  public String getPatronPassword() {
    return patronPassword;
  }

  public String getFeeIdentifier() {
    return feeIdentifier;
  }

  public String getTransactionId() {
    return transactionId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(currencyType, feeAmount, feeIdentifier, feeType,
        institutionId, patronIdentifier, patronPassword, paymentType,
        terminalPassword, transactionDate, transactionId);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof FeePaid)) {
      return false;
    }
    FeePaid other = (FeePaid) obj;
    return currencyType == other.currencyType
        && Objects.equals(feeAmount, other.feeAmount)
        && Objects.equals(feeIdentifier, other.feeIdentifier)
        && feeType == other.feeType
        && Objects.equals(institutionId, other.institutionId)
        && Objects.equals(patronIdentifier, other.patronIdentifier)
        && Objects.equals(patronPassword, other.patronPassword)
        && paymentType == other.paymentType
        && Objects.equals(terminalPassword, other.terminalPassword)
        && Objects.equals(transactionDate, other.transactionDate)
        && Objects.equals(transactionId, other.transactionId);
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("FeePaid [transactionDate=").append(transactionDate)
        .append(", feeType=").append(feeType)
        .append(", paymentType=").append(paymentType)
        .append(", currencyType=").append(currencyType)
        .append(", feeAmount=").append(feeAmount)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", terminalPassword=").append(terminalPassword)
        .append(", patronPassword=").append(patronPassword)
        .append(", feeIdentifier=").append(feeIdentifier)
        .append(", transactionId=").append(transactionId)
        .append(']').toString();
  }

  /**
   * Builder for {@code FeePaid}.
   */
  public static class FeePaidBuilder {
    private ZonedDateTime transactionDate;
    private FeeType feeType;
    private PaymentType paymentType;
    private CurrencyType currencyType;
    private String feeAmount;
    private String institutionId;
    private String patronIdentifier;
    private String terminalPassword;
    private String patronPassword;
    private String feeIdentifier;
    private String transactionId;

    private FeePaidBuilder() {
      super();
    }

    public FeePaidBuilder transactionDate(ZonedDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return this;
    }

    public FeePaidBuilder feeType(FeeType feeType) {
      this.feeType = feeType;
      return this;
    }

    public FeePaidBuilder paymentType(PaymentType paymentType) {
      this.paymentType = paymentType;
      return this;
    }

    public FeePaidBuilder currencyType(CurrencyType currencyType) {
      this.currencyType = currencyType;
      return this;
    }

    public FeePaidBuilder feeAmount(String feeAmount) {
      this.feeAmount = feeAmount;
      return this;
    }

    public FeePaidBuilder institutionId(String institutionId) {
      this.institutionId = institutionId;
      return this;
    }

    public FeePaidBuilder patronIdentifier(String patronIdentifier) {
      this.patronIdentifier = patronIdentifier;
      return this;
    }

    public FeePaidBuilder terminalPassword(String terminalPassword) {
      this.terminalPassword = terminalPassword;
      return this;
    }

    public FeePaidBuilder patronPassword(String patronPassword) {
      this.patronPassword = patronPassword;
      return this;
    }

    public FeePaidBuilder feeIdentifier(String feeIdentifier) {
      this.feeIdentifier = feeIdentifier;
      return this;
    }

    public FeePaidBuilder transactionId(String transactionId) {
      this.transactionId = transactionId;
      return this;
    }

    public FeePaid build() {
      return new FeePaid(this);
    }
  }
}
