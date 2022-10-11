package org.folio.edge.sip2.domain.messages.requests;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Represents an End Patron Session message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * This message will be sent when a patron has completed all of their
 * transactions. The ACS may, upon receipt of this command, close any open
 * files or deallocate data structures pertaining to that patron. The ACS
 * should respond with an End Session Response message.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class EndPatronSession {
  /** The date of this transaction. */
  private final OffsetDateTime transactionDate;
  /** The ID of the institution making the request. */
  private final String institutionId;
  /** The ID of the patron making the request. */
  private final String patronIdentifier;
  /** The password used by the terminal. */
  private final String terminalPassword;
  /** The password used by the patron. */
  private final String patronPassword;

  /**
   * Construct a {@code EndPatronSession} based on a
   * {@code EndPatronSessionBuilder} object.
   * @param builder The basis for creating the end patron session.
   */
  private EndPatronSession(EndPatronSessionBuilder builder) {
    this.transactionDate = builder.transactionDate;
    this.institutionId = builder.institutionId;
    this.patronIdentifier = builder.patronIdentifier;
    this.terminalPassword = builder.terminalPassword;
    this.patronPassword = builder.patronPassword;
  }

  /**
   * Returns a builder used to construct a {@code EndPatronSession}.
   * @return An end patron session builder.
   */
  public static EndPatronSessionBuilder builder() {
    return new EndPatronSessionBuilder();
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

  public String getTerminalPassword() {
    return terminalPassword;
  }

  public String getPatronPassword() {
    return patronPassword;
  }

  @Override
  public int hashCode() {
    return Objects.hash(institutionId, patronIdentifier,
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
    if (!(obj instanceof EndPatronSession)) {
      return false;
    }
    EndPatronSession other = (EndPatronSession) obj;
    return Objects.equals(institutionId, other.institutionId)
        && Objects.equals(patronIdentifier, other.patronIdentifier)
        && Objects.equals(patronPassword, other.patronPassword)
        && Objects.equals(terminalPassword, other.terminalPassword)
        && Objects.equals(transactionDate, other.transactionDate);
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("EndPatronSession [transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", terminalPassword=").append(terminalPassword)
        .append(", patronPassword=").append(patronPassword)
        .append(']').toString();
  }

  /**
   * Returns Formatted Log Message.
   * @return String.
   */
  public String getPatronSessionLogInfo() {
    return new StringBuilder()
      .append("EndPatronSession [transactionDate=").append(transactionDate)
      .append(", institutionId=").append(institutionId)
      .append(", patronIdentifier=").append(patronIdentifier)
      .append(']').toString();
  }

  /**
   * Builder for {@code EndPatronSession}.
   */
  public static class EndPatronSessionBuilder {
    private OffsetDateTime transactionDate;
    private String institutionId;
    private String patronIdentifier;
    private String terminalPassword;
    private String patronPassword;

    private EndPatronSessionBuilder() {
      super();
    }

    public EndPatronSessionBuilder transactionDate(OffsetDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return this;
    }

    public EndPatronSessionBuilder institutionId(String institutionId) {
      this.institutionId = institutionId;
      return this;
    }

    public EndPatronSessionBuilder patronIdentifier(String patronIdentifier) {
      this.patronIdentifier = patronIdentifier;
      return this;
    }

    public EndPatronSessionBuilder terminalPassword(String terminalPassword) {
      this.terminalPassword = terminalPassword;
      return this;
    }

    public EndPatronSessionBuilder patronPassword(String patronPassword) {
      this.patronPassword = patronPassword;
      return this;
    }

    public EndPatronSession build() {
      return new EndPatronSession(this);
    }
  }
}
