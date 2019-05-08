package org.folio.edge.sip2.domain.messages.requests;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Represents the Block Patron message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * This message requests that the patron card be blocked by the ACS. This is,
 * for example, sent when the patron is detected tampering with the SC or when
 * a patron forgets to take their card. The ACS should invalidate the patron’s
 * card and respond with a Patron Status Response message. The ACS could also
 * notify the library staff that the card has been blocked.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class BlockPatron {
  /**
   * {@code TRUE} if the patron's library card has been retained by the SC.
   */
  private final Boolean cardRetained;
  /** The date and time the patron checked out the item at the SC. */
  private final OffsetDateTime transactionDate;
  /** The ID of the institution making the request. */
  private final String institutionId;
  /** Indicates the reason why the patron's card was blocked. */
  private final String blockedCardMsg;
  /** The ID of the patron making the request. */
  private final String patronIdentifier;
  /** The password used by the terminal. */
  private final String terminalPassword;

  /**
   * Construct a {@code BlockPatron} based on a {@code BlockPatronBuilder} object.
   * @param builder The basis for creating the block patron.
   */
  private BlockPatron(BlockPatronBuilder builder) {
    this.cardRetained = builder.cardRetained;
    this.transactionDate = builder.transactionDate;
    this.institutionId = builder.institutionId;
    this.blockedCardMsg = builder.blockedCardMsg;
    this.patronIdentifier = builder.patronIdentifier;
    this.terminalPassword = builder.terminalPassword;
  }

  /**
   * Returns a builder used to construct a {@code BlockPatron}.
   * @return A block patron builder.
   */
  public static BlockPatronBuilder builder() {
    return new BlockPatronBuilder();
  }

  public Boolean getCardRetained() {
    return cardRetained;
  }

  public OffsetDateTime getTransactionDate() {
    return transactionDate;
  }

  public String getInstitutionId() {
    return institutionId;
  }

  public String getBlockedCardMsg() {
    return blockedCardMsg;
  }

  public String getPatronIdentifier() {
    return patronIdentifier;
  }

  public String getTerminalPassword() {
    return terminalPassword;
  }

  @Override
  public int hashCode() {
    return Objects.hash(blockedCardMsg, cardRetained, institutionId,
        patronIdentifier, terminalPassword, transactionDate);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof BlockPatron)) {
      return false;
    }
    BlockPatron other = (BlockPatron) obj;
    return Objects.equals(blockedCardMsg, other.blockedCardMsg)
        && Objects.equals(cardRetained, other.cardRetained)
        && Objects.equals(institutionId, other.institutionId)
        && Objects.equals(patronIdentifier, other.patronIdentifier)
        && Objects.equals(terminalPassword, other.terminalPassword)
        && Objects.equals(transactionDate, other.transactionDate);
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("BlockPatron [cardRetained=").append(cardRetained)
        .append(", transactionDate=").append(transactionDate)
        .append(", institutionId=").append(institutionId)
        .append(", blockedCardMsg=").append(blockedCardMsg)
        .append(", patronIdentifier=").append(patronIdentifier)
        .append(", terminalPassword=").append(terminalPassword)
        .append(']').toString();
  }

  /**
   * Builder for {@code BlockPatron}.
   */
  public static class BlockPatronBuilder {
    private Boolean cardRetained;
    private OffsetDateTime transactionDate;
    private String institutionId;
    private String blockedCardMsg;
    private String patronIdentifier;
    private String terminalPassword;

    private BlockPatronBuilder() {
      super();
    }

    public BlockPatronBuilder cardRetained(Boolean cardRetained) {
      this.cardRetained = cardRetained;
      return this;
    }

    public BlockPatronBuilder transactionDate(OffsetDateTime transactionDate) {
      this.transactionDate = transactionDate;
      return this;
    }

    public BlockPatronBuilder institutionId(String institutionId) {
      this.institutionId = institutionId;
      return this;
    }

    public BlockPatronBuilder blockedCardMsg(String blockedCardMsg) {
      this.blockedCardMsg = blockedCardMsg;
      return this;
    }

    public BlockPatronBuilder patronIdentifier(String patronIdentifier) {
      this.patronIdentifier = patronIdentifier;
      return this;
    }

    public BlockPatronBuilder terminalPassword(String terminalPassword) {
      this.terminalPassword = terminalPassword;
      return this;
    }

    public BlockPatron build() {
      return new BlockPatron(this);
    }
  }
}
