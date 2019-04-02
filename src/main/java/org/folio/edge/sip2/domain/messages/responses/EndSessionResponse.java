package org.folio.edge.sip2.domain.messages.responses;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Represents the End Session Response message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * The ACS must send this message in response to the End Patron Session message.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class EndSessionResponse {
  /**
   * {@code TRUE} indicates the ACS has ended the patron's session.
   */
  private final Boolean endSession;
  /** The date of this transaction. */
  private final ZonedDateTime transactionDate;
  /** The ID of the institution making the request. */
  private final String institutionId;
  /** The ID of the patron making the request. */
  private final String patronIdentifier;
  /** A message to show to the patron on the SC screen. */
  private final String screenMessage;
  /** A message to print for the patron on the SC printer. */
  private final String printLine;

  /**
   * Construct a {@code EndSessionResponse} based on a
   * {@code EndSessionResponseBuilder} object.
   * @param builder The basis for creating the end session response.
   */
  private EndSessionResponse(EndSessionResponseBuilder builder) {
    this.endSession = builder.endSession;
    this.transactionDate = builder.transactionDate;
    this.institutionId = builder.institutionId;
    this.patronIdentifier = builder.patronIdentifier;
    this.screenMessage = builder.screenMessage;
    this.printLine = builder.printLine;
  }

  /**
   * Returns a builder used to construct a {@code EndSessionResponse}.
   * @return An end session response builder.
   */
  public static EndSessionResponseBuilder builder() {
    return new EndSessionResponseBuilder();
  }

  public Boolean getEndSession() {
    return endSession;
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

  public String getScreenMessage() {
    return screenMessage;
  }

  public String getPrintLine() {
    return printLine;
  }

  @Override
  public int hashCode() {
    return Objects.hash(endSession, institutionId, patronIdentifier, printLine,
        screenMessage, transactionDate);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof EndSessionResponse)) {
      return false;
    }
    EndSessionResponse other = (EndSessionResponse) obj;
    return Objects.equals(endSession, other.endSession)
        && Objects.equals(institutionId, other.institutionId)
        && Objects.equals(patronIdentifier, other.patronIdentifier)
        && Objects.equals(printLine, other.printLine)
        && Objects.equals(screenMessage, other.screenMessage)
        && Objects.equals(transactionDate, other.transactionDate);
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("EndSessionResponse [endSession=").append(endSession)
        .append(", transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", screenMessage=").append(screenMessage)
        .append(", printLine=").append(printLine)
        .append(']').toString();
  }

  /**
   * Builder for {@code EndSessionResponse}.
   */
  public static class EndSessionResponseBuilder {
    private Boolean endSession;
    private ZonedDateTime transactionDate;
    private String institutionId;
    private String patronIdentifier;
    private String screenMessage;
    private String printLine;

    private EndSessionResponseBuilder() {
      super();
    }

    public EndSessionResponseBuilder endSession(Boolean endSession) {
      this.endSession = endSession;
      return this;
    }

    public EndSessionResponseBuilder transactionDate(
        ZonedDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return this;
    }

    public EndSessionResponseBuilder institutionId(String institutionId) {
      this.institutionId = institutionId;
      return this;
    }

    public EndSessionResponseBuilder patronIdentifier(
        String patronIdentifier) {
      this.patronIdentifier = patronIdentifier;
      return this;
    }

    public EndSessionResponseBuilder screenMessage(String screenMessage) {
      this.screenMessage = screenMessage;
      return this;
    }

    public EndSessionResponseBuilder printLine(String printLine) {
      this.printLine = printLine;
      return this;
    }

    public EndSessionResponse build() {
      return new EndSessionResponse(this);
    }
  }
}
