package org.folio.edge.sip2.domain.messages.responses;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.folio.edge.sip2.domain.messages.enumerations.CurrencyType;
import org.folio.edge.sip2.domain.messages.enumerations.Language;
import org.folio.edge.sip2.domain.messages.enumerations.PatronStatus;

/**
 * Represents the Patron Status Response message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * The ACS must send this message in response to a Patron Status Request
 * message as well as in response to a Block Patron message.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class PatronStatusResponse {
  /** The current status of the patron. */
  private final Set<PatronStatus> patronStatus;
  /** The language requested for visible messages. */
  private final Language language;
  /** The date of this transaction. */
  private final OffsetDateTime transactionDate;
  /** The ID of the institution making the request. */
  private final String institutionId;
  /** The ID of the patron making the request. */
  private final String patronIdentifier;
  /** The name of the patron. */
  private final String personalName;
  /**
   * {@code TRUE} if the patron's barcode is valid, {@code FALSE} if not.
   */
  private final Boolean validPatron;
  /**
   * {@code TRUE} if the patron's password is valid, {@code FALSE} if not.
   */
  private final Boolean validPatronPassword;
  /** The fee currency type. */
  private final CurrencyType currencyType;
  /** The amount of fees owed by this patron. */
  private final String feeAmount;
  /** A message to show to the patron on the SC screen. */
  private final List<String> screenMessage;
  /** A message to print for the patron on the SC printer. */
  private final List<String> printLine;
  /** Extended field - the type of borrower. */
  private final String borrowerType;
  /** Extend field - the description of the borrower type. */
  private final String borrowerTypeDescription;

  /**
   * Construct a {@code PatronStatusResponse} based on a
   * {@code PatronStatusResponseBuilder} object.
   * @param builder The basis for creating the patron status response.
   */
  private PatronStatusResponse(PatronStatusResponseBuilder builder) {
    this.patronStatus = Collections.unmodifiableSet(
        builder.patronStatus == null ? EnumSet.noneOf(PatronStatus.class)
            : EnumSet.copyOf(builder.patronStatus));
    this.language = builder.language;
    this.transactionDate = builder.transactionDate;
    this.institutionId = builder.institutionId;
    this.patronIdentifier = builder.patronIdentifier;
    this.personalName = builder.personalName;
    this.validPatron = builder.validPatron;
    this.validPatronPassword = builder.validPatronPassword;
    this.currencyType = builder.currencyType;
    this.feeAmount = builder.feeAmount;
    this.screenMessage = builder.screenMessage == null ? null
        : Collections.unmodifiableList(new ArrayList<>(builder.screenMessage));
    this.printLine = builder.printLine == null ? null
        : Collections.unmodifiableList(new ArrayList<>(builder.printLine));
    this.borrowerType = builder.borrowerType;
    this.borrowerTypeDescription = builder.borrowerTypeDescription;
  }

  /**
   * Returns a builder used to construct a {@code PatronStatusResponse}.
   * @return A patron status response builder.
   */
  public static PatronStatusResponseBuilder builder() {
    return new PatronStatusResponseBuilder();
  }

  public Set<PatronStatus> getPatronStatus() {
    return patronStatus;
  }

  public Language getLanguage() {
    return language;
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

  public String getPersonalName() {
    return personalName;
  }

  public Boolean getValidPatron() {
    return validPatron;
  }

  public Boolean getValidPatronPassword() {
    return validPatronPassword;
  }

  public CurrencyType getCurrencyType() {
    return currencyType;
  }

  public String getFeeAmount() {
    return feeAmount;
  }

  public List<String> getScreenMessage() {
    return screenMessage;
  }

  public List<String> getPrintLine() {
    return printLine;
  }

  public String getBorrowerType() {
    return borrowerType;
  }

  public String getBorrowerTypeDescription() {
    return borrowerTypeDescription;
  }

  @Override
  public int hashCode() {
    return Objects.hash(currencyType, feeAmount, institutionId, language,
        patronIdentifier, patronStatus, personalName,printLine, screenMessage,
        transactionDate, validPatron, validPatronPassword);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof PatronStatusResponse)) {
      return false;
    }
    PatronStatusResponse other = (PatronStatusResponse) obj;
    return currencyType == other.currencyType
        && Objects.equals(feeAmount, other.feeAmount)
        && Objects.equals(institutionId, other.institutionId)
        && language == other.language
        && Objects.equals(patronIdentifier, other.patronIdentifier)
        && Objects.equals(patronStatus, other.patronStatus)
        && Objects.equals(personalName, other.personalName)
        && Objects.equals(printLine, other.printLine)
        && Objects.equals(screenMessage, other.screenMessage)
        && Objects.equals(transactionDate, other.transactionDate)
        && Objects.equals(validPatron, other.validPatron)
        && Objects.equals(validPatronPassword, other.validPatronPassword);
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("PatronStatusResponse [patronStatus=").append(patronStatus)
        .append(", language=").append(language)
        .append(", transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", personalName=").append(personalName)
        .append(", validPatron=").append(validPatron)
        .append(", validPatronPassword=").append(validPatronPassword)
        .append(", currencyType=").append(currencyType)
        .append(", feeAmount=").append(feeAmount)
        .append(", screenMessage=").append(screenMessage)
        .append(", printLine=").append(printLine)
        .append(", borrowerType=").append(borrowerType)
        .append(", borrowerTypeDescription=").append(borrowerTypeDescription)
        .append(']').toString();
  }

  /**
   * Builder for {@code PatronStatusResponse}.
   */
  public static class PatronStatusResponseBuilder {
    private Set<PatronStatus> patronStatus;
    private Language language;
    private OffsetDateTime transactionDate;
    private String institutionId;
    private String patronIdentifier;
    private String personalName;
    private Boolean validPatron;
    private Boolean validPatronPassword;
    private CurrencyType currencyType;
    private String feeAmount;
    private List<String> screenMessage;
    private List<String> printLine;
    private String borrowerType;
    private String borrowerTypeDescription;

    private PatronStatusResponseBuilder() {
      super();
    }

    public PatronStatusResponseBuilder patronStatus(
        Set<PatronStatus> patronStatus) {
      this.patronStatus = patronStatus;
      return this;
    }

    public PatronStatusResponseBuilder language(Language language) {
      this.language = language;
      return this;
    }

    public PatronStatusResponseBuilder transactionDate(
        OffsetDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return this;
    }

    public PatronStatusResponseBuilder institutionId(String institutionId) {
      this.institutionId = institutionId;
      return this;
    }

    public PatronStatusResponseBuilder patronIdentifier(
        String patronIdentifier) {
      this.patronIdentifier = patronIdentifier;
      return this;
    }

    public PatronStatusResponseBuilder personalName(String personalName) {
      this.personalName = personalName;
      return this;
    }

    public PatronStatusResponseBuilder validPatron(Boolean validPatron) {
      this.validPatron = validPatron;
      return this;
    }

    public PatronStatusResponseBuilder validPatronPassword(
        Boolean validPatronPassword) {
      this.validPatronPassword = validPatronPassword;
      return this;
    }

    public PatronStatusResponseBuilder currencyType(CurrencyType currencyType) {
      this.currencyType = currencyType;
      return this;
    }

    public PatronStatusResponseBuilder feeAmount(String feeAmount) {
      this.feeAmount = feeAmount;
      return this;
    }

    public PatronStatusResponseBuilder screenMessage(List<String> screenMessage) {
      this.screenMessage = screenMessage;
      return this;
    }

    public PatronStatusResponseBuilder printLine(List<String> printLine) {
      this.printLine = printLine;
      return this;
    }

    public PatronStatusResponseBuilder borrowerType(String borrowerType) {
      this.borrowerType = borrowerType;
      return this;
    }

    public PatronStatusResponseBuilder borrowerTypeDescription(String borrowerTypeDescription) {
      this.borrowerTypeDescription = borrowerTypeDescription;
      return this;
    }

    public PatronStatusResponse build() {
      return new PatronStatusResponse(this);
    }
  }
}
