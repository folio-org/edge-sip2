package org.folio.edge.sip2.domain.messages.responses;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.folio.edge.sip2.domain.messages.enumerations.CurrencyType;
import org.folio.edge.sip2.domain.messages.enumerations.Language;
import org.folio.edge.sip2.domain.messages.enumerations.PatronStatus;

/**
 * Represents the Patron Information Response message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * The ACS must send this message in response to the Patron Information message.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class PatronInformationResponse {
  /** The current status of the patron. */
  private final Set<PatronStatus> patronStatus;
  /** The language requested for visible messages. */
  private final Language language;
  /** The date of this transaction. */
  private final OffsetDateTime transactionDate;
  /** The ID of the institution making the request. */
  private final String institutionId;
  /**
   * The number of held items for this patron.<br>
   * Valid range: {@code 0-9999}
   */
  private final Integer holdItemsCount;
  /**
   * The number of overdue items for this patron.<br>
   * Valid range: {@code 0-9999}
   */
  private final Integer overdueItemsCount;
  /**
   * The number of charged items for this patron.<br>
   * Valid range: {@code 0-9999}
   */
  private final Integer chargedItemsCount;
  /**
   * The number of fine items for this patron.<br>
   * Valid range: {@code 0-9999}
   */
  private final Integer fineItemsCount;
  /**
   * The number of items checked out to this patron that have been recalled.<br>
   * Valid range: {@code 0-9999}.
   */
  private final Integer recallItemsCount;
  /**
   * The number of unavailable holds for this patron.<br>
   * Valid range: {@code 0-9999}
   */
  private final Integer unavailableHoldsCount;
  /** The ID of the patron making the request. */
  private final String patronIdentifier;
  /** The name of the patron. */
  private final String personalName;
  /**
   * The hold items limit for this patron.<br>
   * Valid range: {@code 0-9999}
   */
  private final Integer holdItemsLimit;
  /**
   * The overdue items limit for this patron.<br>
   * Valid range: {@code 0-9999}
   */
  private final Integer overdueItemsLimit;
  /**
   * The charged items limit for this patron.<br>
   * Valid range: {@code 0-9999}
   */
  private final Integer chargedItemsLimit;
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
  /**
   * The limit for fees and fines that the patron is allowed to accumulate in
   * their account. It is a money amount in whatever currency type is
   * specified by the {@code currencyType} field, i.e. "50.00" could specify
   * $50.00 if {@code currencyType} was {@code CurrencyType.USD}.
   */
  private final String feeLimit;
  /** A list of hold items for this patron. */
  private final List<String> holdItems;
  /** A list of overdue items for this patron. */
  private final List<String> overdueItems;
  /** A list of charged items for this patron. */
  private final List<String> chargedItems;
  /** A list of fine items for this patron. */
  private final List<String> fineItems;
  /** A list of recalled items for this patron. */
  private final List<String> recallItems;
  /** A list of unavailable hold items for this patron. */
  private final List<String> unavailableHoldItems;
  /** The home address for the patron. */
  private final String homeAddress;
  /** The email address for the patron. */
  private final String emailAddress;
  /** The home phone number for the patron. */
  private final String homePhoneNumber;
  /** A message to show to the patron on the SC screen. */
  private final List<String> screenMessage;
  /** A message to print for the patron on the SC printer. */
  private final List<String> printLine;

  /**
   * Construct a {@code PatronInformationResponse} based on a
   * {@code PatronInformationResponseBuilder} object.
   * @param builder The basis for creating the patron information response.
   */
  private PatronInformationResponse(PatronInformationResponseBuilder builder) {
    this.patronStatus =
        builder.patronStatus == null
          ? emptySet()
          : unmodifiableSet(builder.patronStatus);
    this.language = builder.language;
    this.transactionDate = builder.transactionDate;
    this.institutionId = builder.institutionId;
    this.holdItemsCount = builder.holdItemsCount;
    this.overdueItemsCount = builder.overdueItemsCount;
    this.chargedItemsCount = builder.chargedItemsCount;
    this.fineItemsCount = builder.fineItemsCount;
    this.recallItemsCount = builder.recallItemsCount;
    this.unavailableHoldsCount = builder.unavailableHoldsCount;
    this.patronIdentifier = builder.patronIdentifier;
    this.personalName = builder.personalName;
    this.holdItemsLimit = builder.holdItemsLimit;
    this.overdueItemsLimit = builder.overdueItemsLimit;
    this.chargedItemsLimit = builder.chargedItemsLimit;
    this.validPatron = builder.validPatron;
    this.validPatronPassword = builder.validPatronPassword;
    this.currencyType = builder.currencyType;
    this.feeAmount = builder.feeAmount;
    this.feeLimit = builder.feeLimit;
    this.holdItems =
        builder.holdItems == null
          ? emptyList()
          : unmodifiableList(new ArrayList<>(builder.holdItems));
    this.overdueItems =
        builder.overdueItems == null
          ? emptyList()
          : unmodifiableList(new ArrayList<>(builder.overdueItems));
    this.chargedItems =
        builder.chargedItems == null
          ? emptyList()
          : unmodifiableList(new ArrayList<>(builder.chargedItems));
    this.fineItems =
        builder.fineItems == null
          ? emptyList()
          : unmodifiableList(new ArrayList<>(builder.fineItems));
    this.recallItems =
        builder.recallItems == null
          ? emptyList()
          : unmodifiableList(new ArrayList<>(builder.recallItems));
    this.unavailableHoldItems =
        builder.unavailableHoldItems == null
          ? emptyList()
          : unmodifiableList(new ArrayList<>(builder.unavailableHoldItems));
    this.homeAddress = builder.homeAddress;
    this.emailAddress = builder.emailAddress;
    this.homePhoneNumber = builder.homePhoneNumber;
    this.screenMessage = builder.screenMessage == null ? null
        : Collections.unmodifiableList(new ArrayList<>(builder.screenMessage));
    this.printLine = builder.printLine == null ? null
        : Collections.unmodifiableList(new ArrayList<>(builder.printLine));
  }

  /**
   * Returns a builder used to construct a {@code PatronInformationResponse}.
   * @return A patron information response builder.
   */
  public static PatronInformationResponseBuilder builder() {
    return new PatronInformationResponseBuilder();
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

  public Integer getHoldItemsCount() {
    return holdItemsCount;
  }

  public Integer getOverdueItemsCount() {
    return overdueItemsCount;
  }

  public Integer getChargedItemsCount() {
    return chargedItemsCount;
  }

  public Integer getFineItemsCount() {
    return fineItemsCount;
  }

  public Integer getRecallItemsCount() {
    return recallItemsCount;
  }

  public Integer getUnavailableHoldsCount() {
    return unavailableHoldsCount;
  }

  public String getPatronIdentifier() {
    return patronIdentifier;
  }

  public String getPersonalName() {
    return personalName;
  }

  public Integer getHoldItemsLimit() {
    return holdItemsLimit;
  }

  public Integer getOverdueItemsLimit() {
    return overdueItemsLimit;
  }

  public Integer getChargedItemsLimit() {
    return chargedItemsLimit;
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

  public String getFeeLimit() {
    return feeLimit;
  }

  public List<String> getHoldItems() {
    return holdItems;
  }

  public List<String> getOverdueItems() {
    return overdueItems;
  }

  public List<String> getChargedItems() {
    return chargedItems;
  }

  public List<String> getFineItems() {
    return fineItems;
  }

  public List<String> getRecallItems() {
    return recallItems;
  }

  public List<String> getUnavailableHoldItems() {
    return unavailableHoldItems;
  }

  public String getHomeAddress() {
    return homeAddress;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public String getHomePhoneNumber() {
    return homePhoneNumber;
  }

  public List<String> getScreenMessage() {
    return screenMessage;
  }

  public List<String> getPrintLine() {
    return printLine;
  }

  @Override
  public int hashCode() {
    return Objects.hash(chargedItems, chargedItemsCount, chargedItemsLimit,
        currencyType, emailAddress, feeAmount, feeLimit, fineItems,
        fineItemsCount, holdItems, holdItemsCount, holdItemsLimit,
        homeAddress, homePhoneNumber, institutionId, language, overdueItems,
        overdueItemsCount, overdueItemsLimit, patronIdentifier, patronStatus,
        personalName, printLine, recallItems, recallItemsCount, screenMessage,
        transactionDate, unavailableHoldItems, unavailableHoldsCount,
        validPatron, validPatronPassword);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof PatronInformationResponse)) {
      return false;
    }
    PatronInformationResponse other = (PatronInformationResponse) obj;
    return Objects.equals(chargedItems, other.chargedItems)
        && Objects.equals(chargedItemsCount, other.chargedItemsCount)
        && Objects.equals(chargedItemsLimit, other.chargedItemsLimit)
        && currencyType == other.currencyType
        && Objects.equals(emailAddress, other.emailAddress)
        && Objects.equals(feeAmount, other.feeAmount)
        && Objects.equals(feeLimit, other.feeLimit)
        && Objects.equals(fineItems, other.fineItems)
        && Objects.equals(fineItemsCount, other.fineItemsCount)
        && Objects.equals(holdItems, other.holdItems)
        && Objects.equals(holdItemsCount, other.holdItemsCount)
        && Objects.equals(holdItemsLimit, other.holdItemsLimit)
        && Objects.equals(homeAddress, other.homeAddress)
        && Objects.equals(homePhoneNumber, other.homePhoneNumber)
        && Objects.equals(institutionId, other.institutionId)
        && language == other.language
        && Objects.equals(overdueItems, other.overdueItems)
        && Objects.equals(overdueItemsCount, other.overdueItemsCount)
        && Objects.equals(overdueItemsLimit, other.overdueItemsLimit)
        && Objects.equals(patronIdentifier, other.patronIdentifier)
        && Objects.equals(patronStatus, other.patronStatus)
        && Objects.equals(personalName, other.personalName)
        && Objects.equals(printLine, other.printLine)
        && Objects.equals(recallItems, other.recallItems)
        && Objects.equals(recallItemsCount, other.recallItemsCount)
        && Objects.equals(screenMessage, other.screenMessage)
        && Objects.equals(transactionDate, other.transactionDate)
        && Objects.equals(unavailableHoldItems, other.unavailableHoldItems)
        && Objects.equals(unavailableHoldsCount, other.unavailableHoldsCount)
        && Objects.equals(validPatron, other.validPatron)
        && Objects.equals(validPatronPassword, other.validPatronPassword);
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("PatronInformationResponse [patronStatus=").append(patronStatus)
        .append(", language=").append(language)
        .append(", transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", holdItemsCount=").append(holdItemsCount)
        .append(", overdueItemsCount=").append(overdueItemsCount)
        .append(", chargedItemsCount=").append(chargedItemsCount)
        .append(", fineItemsCount=").append(fineItemsCount)
        .append(", recallItemsCount=").append(recallItemsCount)
        .append(", unavailableHoldsCount=").append(unavailableHoldsCount)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", personalName=").append(personalName)
        .append(", holdItemsLimit=").append(holdItemsLimit)
        .append(", overdueItemsLimit=").append(overdueItemsLimit)
        .append(", chargedItemsLimit=").append(chargedItemsLimit)
        .append(", validPatron=").append(validPatron)
        .append(", validPatronPassword=").append(validPatronPassword)
        .append(", currencyType=").append(currencyType)
        .append(", feeAmount=").append(feeAmount)
        .append(", feeLimit=").append(feeLimit)
        .append(", holdItems=").append(holdItems)
        .append(", overdueItems=").append(overdueItems)
        .append(", chargedItems=").append(chargedItems)
        .append(", fineItems=").append(fineItems)
        .append(", recallItems=").append(recallItems)
        .append(", unavailableHoldItems=").append(unavailableHoldItems)
        .append(", homeAddress=").append(homeAddress)
        .append(", emailAddress=").append(emailAddress)
        .append(", homePhoneNumber=").append(homePhoneNumber)
        .append(", screenMessage=").append(screenMessage)
        .append(", printLine=").append(printLine)
        .append(']').toString();
  }

  /**
   * Builder for {@code PatronInformationResponse}.
   */
  public static class PatronInformationResponseBuilder {
    private Set<PatronStatus> patronStatus;
    private Language language;
    private OffsetDateTime transactionDate;
    private String institutionId;
    private Integer holdItemsCount;
    private Integer overdueItemsCount;
    private Integer chargedItemsCount;
    private Integer fineItemsCount;
    private Integer recallItemsCount;
    private Integer unavailableHoldsCount;
    private String patronIdentifier;
    private String personalName;
    private Integer holdItemsLimit;
    private Integer overdueItemsLimit;
    private Integer chargedItemsLimit;
    private Boolean validPatron;
    private Boolean validPatronPassword;
    private CurrencyType currencyType;
    private String feeAmount;
    private String feeLimit;
    private List<String> holdItems;
    private List<String> overdueItems;
    private List<String> chargedItems;
    private List<String> fineItems;
    private List<String> recallItems;
    private List<String> unavailableHoldItems;
    private String homeAddress;
    private String emailAddress;
    private String homePhoneNumber;
    private List<String> screenMessage;
    private List<String> printLine;

    private PatronInformationResponseBuilder() {
      super();
    }

    public PatronInformationResponseBuilder patronStatus(
        Set<PatronStatus> patronStatus) {
      this.patronStatus = patronStatus;
      return this;
    }

    public PatronInformationResponseBuilder language(Language language) {
      this.language = language;
      return this;
    }

    public PatronInformationResponseBuilder transactionDate(
        OffsetDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return this;
    }

    public PatronInformationResponseBuilder holdItemsCount(
        Integer holdItemsCount) {
      this.holdItemsCount = holdItemsCount;
      return this;
    }

    public PatronInformationResponseBuilder overdueItemsCount(
        Integer overdueItemsCount) {
      this.overdueItemsCount = overdueItemsCount;
      return this;
    }

    public PatronInformationResponseBuilder chargedItemsCount(
        Integer chargedItemsCount) {
      this.chargedItemsCount = chargedItemsCount;
      return this;
    }

    public PatronInformationResponseBuilder fineItemsCount(
        Integer fineItemsCount) {
      this.fineItemsCount = fineItemsCount;
      return this;
    }

    public PatronInformationResponseBuilder recallItemsCount(
        Integer recallItemsCount) {
      this.recallItemsCount = recallItemsCount;
      return this;
    }

    public PatronInformationResponseBuilder unavailableHoldsCount(
        Integer unavailableHoldsCount) {
      this.unavailableHoldsCount = unavailableHoldsCount;
      return this;
    }

    public PatronInformationResponseBuilder institutionId(
        String institutionId) {
      this.institutionId = institutionId;
      return this;
    }

    public PatronInformationResponseBuilder patronIdentifier(
        String patronIdentifier) {
      this.patronIdentifier = patronIdentifier;
      return this;
    }

    public PatronInformationResponseBuilder personalName(String personalName) {
      this.personalName = personalName;
      return this;
    }

    public PatronInformationResponseBuilder holdItemsLimit(
        Integer holdItemsLimit) {
      this.holdItemsLimit = holdItemsLimit;
      return this;
    }

    public PatronInformationResponseBuilder overdueItemsLimit(
        Integer overdueItemsLimit) {
      this.overdueItemsLimit = overdueItemsLimit;
      return this;
    }

    public PatronInformationResponseBuilder chargedItemsLimit(
        Integer chargedItemsLimit) {
      this.chargedItemsLimit = chargedItemsLimit;
      return this;
    }

    public PatronInformationResponseBuilder validPatron(Boolean validPatron) {
      this.validPatron = validPatron;
      return this;
    }

    public PatronInformationResponseBuilder validPatronPassword(
        Boolean validPatronPassword) {
      this.validPatronPassword = validPatronPassword;
      return this;
    }

    public PatronInformationResponseBuilder currencyType(
        CurrencyType currencyType) {
      this.currencyType = currencyType;
      return this;
    }

    public PatronInformationResponseBuilder feeAmount(String feeAmount) {
      this.feeAmount = feeAmount;
      return this;
    }

    public PatronInformationResponseBuilder feeLimit(String feeLimit) {
      this.feeLimit = feeLimit;
      return this;
    }

    public PatronInformationResponseBuilder holdItems(List<String> holdItems) {
      this.holdItems = holdItems;
      return this;
    }

    public PatronInformationResponseBuilder overdueItems(
        List<String> overdueItems) {
      this.overdueItems = overdueItems;
      return this;
    }

    public PatronInformationResponseBuilder chargedItems(
        List<String> chargedItems) {
      this.chargedItems = chargedItems;
      return this;
    }

    public PatronInformationResponseBuilder fineItems(List<String> fineItems) {
      this.fineItems = fineItems;
      return this;
    }

    public PatronInformationResponseBuilder recallItems(
        List<String> recallItems) {
      this.recallItems = recallItems;
      return this;
    }

    public PatronInformationResponseBuilder unavailableHoldItems(
        List<String> unavailableHoldItems) {
      this.unavailableHoldItems = unavailableHoldItems;
      return this;
    }

    public PatronInformationResponseBuilder homeAddress(String homeAddress) {
      this.homeAddress = homeAddress;
      return this;
    }

    public PatronInformationResponseBuilder emailAddress(String emailAddress) {
      this.emailAddress = emailAddress;
      return this;
    }

    public PatronInformationResponseBuilder homePhoneNumber(
        String homePhoneNumber) {
      this.homePhoneNumber = homePhoneNumber;
      return this;
    }

    public PatronInformationResponseBuilder screenMessage(List<String> screenMessage) {
      this.screenMessage = screenMessage;
      return this;
    }

    public PatronInformationResponseBuilder printLine(List<String> printLine) {
      this.printLine = printLine;
      return this;
    }

    public PatronInformationResponse build() {
      return new PatronInformationResponse(this);
    }
  }
}
