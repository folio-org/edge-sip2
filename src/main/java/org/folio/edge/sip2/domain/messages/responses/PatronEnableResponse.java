package org.folio.edge.sip2.domain.messages.responses;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import org.folio.edge.sip2.domain.messages.enumerations.Language;
import org.folio.edge.sip2.domain.messages.enumerations.PatronStatus;

/**
 * Represents the Patron Enable Response message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * The ACS should send this message in response to the Patron Enable message
 * from the SC.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class PatronEnableResponse {
  /** The current status of the patron. */
  private final Set<PatronStatus> patronStatus;
  /** The language requested for visible messages. */
  private final Language language;
  /** The date of this transaction. */
  private final ZonedDateTime transactionDate;
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
  /** A message to show to the patron on the SC screen. */
  private final String screenMessage;
  /** A message to print for the patron on the SC printer. */
  private final String printLine;

  /**
   * Construct a {@code PatronEnableResponse} based on a
   * {@code PatronEnableResponseBuilder} object.
   * @param builder The basis for creating the patron enable response.
   */
  private PatronEnableResponse(PatronEnableResponseBuilder builder) {
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
    this.screenMessage = builder.screenMessage;
    this.printLine = builder.printLine;
  }

  /**
   * Returns a builder used to construct a {@code PatronEnableResponse}.
   * @return A patron enable response builder.
   */
  public static PatronEnableResponseBuilder builder() {
    return new PatronEnableResponseBuilder();
  }

  public Set<PatronStatus> getPatronStatus() {
    return patronStatus;
  }

  public Language getLanguage() {
    return language;
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

  public String getPersonalName() {
    return personalName;
  }

  public Boolean getValidPatron() {
    return validPatron;
  }

  public Boolean getValidPatronPassword() {
    return validPatronPassword;
  }

  public String getScreenMessage() {
    return screenMessage;
  }

  public String getPrintLine() {
    return printLine;
  }

  @Override
  public int hashCode() {
    return Objects.hash(institutionId, language, patronIdentifier, patronStatus,
        personalName,printLine, screenMessage, transactionDate, validPatron,
        validPatronPassword);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof PatronEnableResponse)) {
      return false;
    }
    PatronEnableResponse other = (PatronEnableResponse) obj;
    return Objects.equals(institutionId, other.institutionId)
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
        .append("PatronEnableResponse [patronStatus=").append(patronStatus)
        .append(", language=").append(language)
        .append(", transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", personalName=").append(personalName)
        .append(", validPatron=").append(validPatron)
        .append(", validPatronPassword=").append(validPatronPassword)
        .append(", screenMessage=").append(screenMessage)
        .append(", printLine=").append(printLine)
        .append(']').toString();
  }

  /**
   * Builder for {@code PatronEnableResponse}.
   */
  public static class PatronEnableResponseBuilder {
    private Set<PatronStatus> patronStatus;
    private Language language;
    private ZonedDateTime transactionDate;
    private String institutionId;
    private String patronIdentifier;
    private String personalName;
    private Boolean validPatron;
    private Boolean validPatronPassword;
    private String screenMessage;
    private String printLine;

    private PatronEnableResponseBuilder() {
      super();
    }

    public PatronEnableResponseBuilder patronStatus(
        Set<PatronStatus> patronStatus) {
      this.patronStatus = patronStatus;
      return this;
    }

    public PatronEnableResponseBuilder language(Language language) {
      this.language = language;
      return this;
    }

    public PatronEnableResponseBuilder transactionDate(
        ZonedDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return this;
    }

    public PatronEnableResponseBuilder institutionId(String institutionId) {
      this.institutionId = institutionId;
      return this;
    }

    public PatronEnableResponseBuilder patronIdentifier(
        String patronIdentifier) {
      this.patronIdentifier = patronIdentifier;
      return this;
    }

    public PatronEnableResponseBuilder personalName(String personalName) {
      this.personalName = personalName;
      return this;
    }

    public PatronEnableResponseBuilder validPatron(Boolean validPatron) {
      this.validPatron = validPatron;
      return this;
    }

    public PatronEnableResponseBuilder validPatronPassword(
        Boolean validPatronPassword) {
      this.validPatronPassword = validPatronPassword;
      return this;
    }

    public PatronEnableResponseBuilder screenMessage(String screenMessage) {
      this.screenMessage = screenMessage;
      return this;
    }

    public PatronEnableResponseBuilder printLine(String printLine) {
      this.printLine = printLine;
      return this;
    }

    public PatronEnableResponse build() {
      return new PatronEnableResponse(this);
    }
  }
}
