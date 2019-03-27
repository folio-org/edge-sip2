package org.folio.edge.sip2.domain.messages.requests;

import java.time.ZonedDateTime;
import java.util.Objects;

import org.folio.edge.sip2.domain.messages.enumerations.Language;

/**
 * Represents the Patron Status Request message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * This message is used by the SC to request patron information from the ACS.
 * The ACS must respond to this command with a Patron Status Response message.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class PatronStatusRequest {
  /** The language requested for visible messages. */
  private final Language language;
  /** The date of this transaction. */
  private final ZonedDateTime transactionDate;
  /** The ID of the institution making the request. */
  private final String institutionId;
  /** The ID of the patron making the request. */
  private final String patronIdentifier;
  /** The password used by the terminal. */
  private final String terminalPassword;
  /** The password used by the patron. */
  private final String patronPassword;

  /**
   * Construct a {@code PatronStatusRequest} based on a
   * {@code PatronStatusRequestBuilder} object.
   * @param builder The basis for creating the patron status request.
   */
  private PatronStatusRequest(PatronStatusRequestBuilder builder) {
    this.language = builder.language;
    this.transactionDate = builder.transactionDate;
    this.institutionId = builder.institutionId;
    this.patronIdentifier = builder.patronIdentifier;
    this.terminalPassword = builder.terminalPassword;
    this.patronPassword = builder.patronPassword;
  }

  /**
   * Returns a builder used to construct a {@code PatronStatusRequest}.
   * @return A patron status request builder.
   */
  public static PatronStatusRequestBuilder builder() {
    return new PatronStatusRequestBuilder();
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

  public String getTerminalPassword() {
    return terminalPassword;
  }

  public String getPatronPassword() {
    return patronPassword;
  }

  @Override
  public int hashCode() {
    return Objects.hash(institutionId, language, patronIdentifier,
        patronPassword, terminalPassword, transactionDate);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof PatronStatusRequest)) {
      return false;
    }
    PatronStatusRequest other = (PatronStatusRequest) obj;
    return Objects.equals(institutionId, other.institutionId)
        && language == other.language
        && Objects.equals(patronIdentifier, other.patronIdentifier)
        && Objects.equals(patronPassword, other.patronPassword)
        && Objects.equals(terminalPassword, other.terminalPassword)
        && Objects.equals(transactionDate, other.transactionDate);
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("PatronStatusRequest [language=").append(language)
        .append(", transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", terminalPassword=").append(terminalPassword)
        .append(", patronPassword=").append(patronPassword)
        .append(']').toString();
  }

  /**
   * Builder for {@code PatronStatusRequest}.
   */
  public static class PatronStatusRequestBuilder {
    private Language language;
    private ZonedDateTime transactionDate;
    private String institutionId;
    private String patronIdentifier;
    private String terminalPassword;
    private String patronPassword;

    private PatronStatusRequestBuilder() {
      super();
    }

    public PatronStatusRequestBuilder language(Language language) {
      this.language = language;
      return this;
    }

    public PatronStatusRequestBuilder transactionDate(ZonedDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return this;
    }

    public PatronStatusRequestBuilder institutionId(String institutionId) {
      this.institutionId = institutionId;
      return this;
    }

    public PatronStatusRequestBuilder patronIdentifier(String patronIdentifier) {
      this.patronIdentifier = patronIdentifier;
      return this;
    }

    public PatronStatusRequestBuilder terminalPassword(String terminalPassword) {
      this.terminalPassword = terminalPassword;
      return this;
    }

    public PatronStatusRequestBuilder patronPassword(String patronPassword) {
      this.patronPassword = patronPassword;
      return this;
    }

    public PatronStatusRequest build() {
      return new PatronStatusRequest(this);
    }
  }
}
