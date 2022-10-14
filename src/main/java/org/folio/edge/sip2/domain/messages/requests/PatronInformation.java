package org.folio.edge.sip2.domain.messages.requests;

import java.time.OffsetDateTime;
import java.util.Objects;
import org.folio.edge.sip2.domain.messages.enumerations.Language;
import org.folio.edge.sip2.domain.messages.enumerations.Summary;

/**
 * Represents the Patron Information message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * This message is a superset of the Patron Status Request message. It should
 * be used to request patron information. The ACS should respond with the
 * Patron Information Response message.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class PatronInformation {
  /** The language requested for visible messages. */
  private final Language language;
  /** The date of this transaction. */
  private final OffsetDateTime transactionDate;
  /**
   * Indicates that detailed as well as summary information about the
   * corresponding category of items can be sent in the response.
   */
  private final Summary summary;
  /** The ID of the institution making the request. */
  private final String institutionId;
  /** The ID of the patron making the request. */
  private final String patronIdentifier;
  /** The password used by the terminal. */
  private final String terminalPassword;
  /** The password used by the patron. */
  private final String patronPassword;
  /** The number of the first item to be returned. */
  private final Integer startItem;
  /** The number of the last item to be returned. */
  private final Integer endItem;

  /**
   * Construct a {@code PatronInformation} based on a
   * {@code PatronInformationBuilder} object.
   * @param builder The basis for creating the patron information.
   */
  private PatronInformation(PatronInformationBuilder builder) {
    this.language = builder.language;
    this.transactionDate = builder.transactionDate;
    this.summary = builder.summary;
    this.institutionId = builder.institutionId;
    this.patronIdentifier = builder.patronIdentifier;
    this.terminalPassword = builder.terminalPassword;
    this.patronPassword = builder.patronPassword;
    this.startItem = builder.startItem;
    this.endItem = builder.endItem;
  }

  /**
   * Returns a builder used to construct a {@code PatronInformation}.
   * @return A patron information builder.
   */
  public static PatronInformationBuilder builder() {
    return new PatronInformationBuilder();
  }

  public Language getLanguage() {
    return language;
  }

  public OffsetDateTime getTransactionDate() {
    return transactionDate;
  }

  public Summary getSummary() {
    return summary;
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

  public Integer getStartItem() {
    return startItem;
  }

  public Integer getEndItem() {
    return endItem;
  }

  @Override
  public int hashCode() {
    return Objects.hash(endItem, institutionId, language, patronIdentifier,
        patronPassword, startItem, summary, terminalPassword, transactionDate);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof PatronInformation)) {
      return false;
    }
    PatronInformation other = (PatronInformation) obj;
    return Objects.equals(endItem, other.endItem)
        && Objects.equals(institutionId, other.institutionId)
        && language == other.language
        && Objects.equals(patronIdentifier, other.patronIdentifier)
        && Objects.equals(patronPassword, other.patronPassword)
        && Objects.equals(startItem, other.startItem)
        && summary == other.summary
        && Objects.equals(terminalPassword, other.terminalPassword)
        && Objects.equals(transactionDate, other.transactionDate);
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("PatronInformation [language=").append(language)
        .append(", transactionDate=").append(transactionDate)
        .append(", summary=").append(summary)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", terminalPassword=").append(terminalPassword)
        .append(", patronPassword=").append(patronPassword)
        .append(", startItem=").append(startItem)
        .append(", endItem=").append(endItem)
        .append(']').toString();
  }

  /**
   * Returns Formatted Log Message.
   * @return String.
   */
  public String getPatronLogInfo() {
    return new StringBuilder()
      .append("PatronInformation [language=").append(language)
      .append(", transactionDate=").append(transactionDate)
      .append(", summary=").append(summary)
      .append(", institutionId=").append(institutionId)
      .append(", patronIdentifier=").append(patronIdentifier)
      .append(", startItem=").append(startItem)
      .append(", endItem=").append(endItem)
      .append(']').toString();
  }

  /**
   * Builder for {@code PatronInformation}.
   */
  public static class PatronInformationBuilder {
    private Language language;
    private OffsetDateTime transactionDate;
    private Summary summary;
    private String institutionId;
    private String patronIdentifier;
    private String terminalPassword;
    private String patronPassword;
    private Integer startItem;
    private Integer endItem;

    private PatronInformationBuilder() {
      super();
    }

    public PatronInformationBuilder language(Language language) {
      this.language = language;
      return this;
    }

    public PatronInformationBuilder transactionDate(OffsetDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return this;
    }

    public PatronInformationBuilder summary(Summary summary) {
      this.summary = summary;
      return this;
    }

    public PatronInformationBuilder institutionId(String institutionId) {
      this.institutionId = institutionId;
      return this;
    }

    public PatronInformationBuilder patronIdentifier(String patronIdentifier) {
      this.patronIdentifier = patronIdentifier;
      return this;
    }

    public PatronInformationBuilder terminalPassword(String terminalPassword) {
      this.terminalPassword = terminalPassword;
      return this;
    }

    public PatronInformationBuilder patronPassword(String patronPassword) {
      this.patronPassword = patronPassword;
      return this;
    }

    public PatronInformationBuilder startItem(Integer startItem) {
      this.startItem = startItem;
      return this;
    }

    public PatronInformationBuilder endItem(Integer endItem) {
      this.endItem = endItem;
      return this;
    }

    public PatronInformation build() {
      return new PatronInformation(this);
    }
  }
}
