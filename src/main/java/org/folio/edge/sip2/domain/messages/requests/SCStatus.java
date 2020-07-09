package org.folio.edge.sip2.domain.messages.requests;

import java.util.Objects;
import org.folio.edge.sip2.domain.messages.enumerations.StatusCode;

/**
 * Represents the SC Status message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * The SC status message sends SC status to the ACS. It requires an ACS Status
 * Response message reply from the ACS. This message will be the first message
 * sent by the SC to the ACS once a connection has been established (exception:
 * the Login Message may be sent first to login to an ACS server program). The
 * ACS will respond with a message that establishes some of the rules to be
 * followed by the SC and establishes some parameters needed for further
 * communication.
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class SCStatus {
  /** The status of the SC. */
  private final StatusCode statusCode;
  /** The maximum number of character that the SC can print in one line. */
  private final Integer maxPrintWidth;
  /** The version of the SIP protocol used by the SC. */
  private final String protocolVersion;

  /**
   * Construct a {@code SCStatus} based on a {@code SCStatusBuilder} object.
   * @param builder The basis for creating the SC status.
   */
  private SCStatus(SCStatusBuilder builder) {
    this.statusCode = builder.statusCode;
    this.maxPrintWidth = builder.maxPrintWidth;
    this.protocolVersion = builder.protocolVersion;
  }

  /**
   * Returns a builder used to construct a {@code SCStatus}.
   * @return A SC status builder.
   */
  public static SCStatusBuilder builder() {
    return new SCStatusBuilder();
  }

  public StatusCode getStatusCode() {
    return statusCode;
  }

  public Integer getMaxPrintWidth() {
    return maxPrintWidth;
  }

  public String getProtocolVersion() {
    return protocolVersion;
  }

  @Override
  public int hashCode() {
    return Objects.hash(maxPrintWidth, protocolVersion, statusCode);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof SCStatus)) {
      return false;
    }
    SCStatus other = (SCStatus) obj;
    return Objects.equals(maxPrintWidth, other.maxPrintWidth)
        && Objects.equals(protocolVersion, other.protocolVersion)
        && statusCode == other.statusCode;
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("SCStatus [statusCode=").append(statusCode)
        .append(", maxPrintWidth=").append(maxPrintWidth)
        .append(", protocolVersion=").append(protocolVersion)
        .append(']').toString();
  }

  /**
   * Builder for {@code SCStatus}.
   */
  public static class SCStatusBuilder {
    private StatusCode statusCode;
    private Integer maxPrintWidth;
    private String protocolVersion;

    private SCStatusBuilder() {
      super();
    }

    public SCStatusBuilder statusCode(StatusCode statusCode) {
      this.statusCode = statusCode;
      return this;
    }

    public SCStatusBuilder maxPrintWidth(Integer maxPrintWidth) {
      this.maxPrintWidth = maxPrintWidth;
      return this;
    }

    public SCStatusBuilder protocolVersion(String protocolVersion) {
      this.protocolVersion = protocolVersion;
      return this;
    }

    public SCStatus build() {
      return new SCStatus(this);
    }
  }
}
