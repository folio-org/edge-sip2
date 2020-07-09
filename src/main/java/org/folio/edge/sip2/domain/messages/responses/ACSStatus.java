package org.folio.edge.sip2.domain.messages.responses;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.folio.edge.sip2.domain.messages.enumerations.Messages;

/**
 * Represents the ACS Status message.
 *
 * <blockquote
 *     cite="http://multimedia.3m.com/mws/media/355361O/sip2-protocol.pdf">
 * The ACS must send this message in response to a SC Status message. This
 * message will be the first message sent by the ACS to the SC, since it
 * establishes some of the rules to be followed by the SC and establishes some
 * parameters needed for further communication (exception: the Login Response
 * Message may be sent first to complete login of the SC).
 * </blockquote>
 *
 * @author mreno-EBSCO
 *
 */
public final class ACSStatus {
  /** Indicates whether the ACS is on or off-line. */
  private final Boolean onLineStatus;
  /** Indicates that the SC is allowed to check in items. */
  private final Boolean checkinOk;
  /** Indicates that the SC is allowed to check out items. */
  private final Boolean checkoutOk;
  /**
   * Indicates that the SC is allowed by the ACS to process patron renewal
   * requests as a policy.
   */
  private final Boolean acsRenewalPolicy;
  /** Indicates that patron status updating by the SC is allowed. */
  private final Boolean statusUpdateOk;
  /** Indicates whether the SC can operate while the ACS is off-line. */
  private final Boolean offLineOk;
  /**
   * The timeout period until a transaction is aborted. Should be a number
   * expressed in tenths of a second. {@code 0} indicates that the ACS is not
   * on-line. {@code 999} indicates that the timeout is unknown.
   */
  private final Integer timeoutPeriod;
  /**
   * Indicates the number of retries that are allowed for a specific
   * transaction. {@code 999} indicates that the retry number is unknown.
   */
  private final Integer retriesAllowed;
  /**
   * Date and time used to synchronize clocks. {@code null} indicates an
   * unsupported function.
   */
  private final OffsetDateTime dateTimeSync;
  /** Supported version of the SIP protocol. */
  private final String protocolVersion;
  /** The ID of the institution making the request. */
  private final String institutionId;
  /** The library's name. */
  private final String libraryName;
  /** Indicates which messages the ACS supports. */
  private final Set<Messages> supportedMessages;
  /** The location of the SC. */
  private final String terminalLocation;
  /** A message to show to the patron on the SC screen. */
  private final List<String> screenMessage;
  /** A message to print for the patron on the SC printer. */
  private final List<String> printLine;

  /**
   * Construct a {@code ACSStatus} based on a
   * {@code ACSStatusBuilder} object.
   * @param builder The basis for creating the ACS status.
   */
  private ACSStatus(ACSStatusBuilder builder) {
    this.onLineStatus = builder.onLineStatus;
    this.checkinOk = builder.checkinOk;
    this.checkoutOk = builder.checkoutOk;
    this.acsRenewalPolicy = builder.acsRenewalPolicy;
    this.statusUpdateOk = builder.statusUpdateOk;
    this.offLineOk = builder.offLineOk;
    this.timeoutPeriod = builder.timeoutPeriod;
    this.retriesAllowed = builder.retriesAllowed;
    this.dateTimeSync = builder.dateTimeSync;
    this.protocolVersion = builder.protocolVersion;
    this.institutionId = builder.institutionId;
    this.libraryName = builder.libraryName;
    this.supportedMessages = Collections.unmodifiableSet(
        builder.supportedMessages == null ? EnumSet.noneOf(Messages.class)
            : EnumSet.copyOf(builder.supportedMessages));
    this.terminalLocation = builder.terminalLocation;
    this.screenMessage = builder.screenMessage == null ? null
            : Collections.unmodifiableList(new ArrayList<>(builder.screenMessage));
    this.printLine = builder.printLine == null ? null
            : Collections.unmodifiableList(new ArrayList<>(builder.printLine));
  }

  /**
   * Returns a builder used to construct a {@code ACSStatus}.
   * @return An ACS status builder.
   */
  public static ACSStatusBuilder builder() {
    return new ACSStatusBuilder();
  }

  public Boolean getOnLineStatus() {
    return onLineStatus;
  }

  public Boolean getCheckinOk() {
    return checkinOk;
  }

  public Boolean getCheckoutOk() {
    return checkoutOk;
  }

  public Boolean getAcsRenewalPolicy() {
    return acsRenewalPolicy;
  }

  public Boolean getStatusUpdateOk() {
    return statusUpdateOk;
  }

  public Boolean getOffLineOk() {
    return offLineOk;
  }

  public Integer getTimeoutPeriod() {
    return timeoutPeriod;
  }

  public Integer getRetriesAllowed() {
    return retriesAllowed;
  }

  public OffsetDateTime getDateTimeSync() {
    return dateTimeSync;
  }

  public String getProtocolVersion() {
    return protocolVersion;
  }

  public String getInstitutionId() {
    return institutionId;
  }

  public String getLibraryName() {
    return libraryName;
  }

  public  Set<Messages>  getSupportedMessages() {
    return supportedMessages;
  }

  public String getTerminalLocation() {
    return terminalLocation;
  }

  public List<String> getScreenMessage() {
    return screenMessage;
  }

  public List<String> getPrintLine() {
    return printLine;
  }

  @Override
  public int hashCode() {
    return Objects.hash(acsRenewalPolicy, checkinOk, checkoutOk, dateTimeSync, institutionId,
        libraryName, offLineOk, onLineStatus, printLine, protocolVersion, retriesAllowed,
        screenMessage, statusUpdateOk, supportedMessages, terminalLocation, timeoutPeriod);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof ACSStatus)) {
      return false;
    }
    ACSStatus other = (ACSStatus) obj;
    return Objects.equals(acsRenewalPolicy, other.acsRenewalPolicy)
        && Objects.equals(checkinOk, other.checkinOk)
        && Objects.equals(checkoutOk, other.checkoutOk)
        && Objects.equals(dateTimeSync, other.dateTimeSync)
        && Objects.equals(institutionId, other.institutionId)
        && Objects.equals(libraryName, other.libraryName)
        && Objects.equals(offLineOk, other.offLineOk)
        && Objects.equals(onLineStatus, other.onLineStatus)
        && Objects.equals(printLine, other.printLine)
        && Objects.equals(protocolVersion, other.protocolVersion)
        && Objects.equals(retriesAllowed, other.retriesAllowed)
        && Objects.equals(screenMessage, other.screenMessage)
        && Objects.equals(statusUpdateOk, other.statusUpdateOk)
        && Objects.equals(supportedMessages, other.supportedMessages)
        && Objects.equals(terminalLocation, other.terminalLocation)
        && Objects.equals(timeoutPeriod, other.timeoutPeriod);
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("ACSStatus [onLineStatus=").append(onLineStatus)
        .append(", checkinOk=").append(checkinOk)
        .append(", checkoutOk=").append(checkoutOk)
        .append(", acsRenewalPolicy=").append(acsRenewalPolicy)
        .append(", statusUpdateOk=").append(statusUpdateOk)
        .append(", offLineOk=").append(offLineOk)
        .append(", timeoutPeriod=").append(timeoutPeriod)
        .append(", retriesAllowed=").append(retriesAllowed)
        .append(", dateTimeSync=").append(dateTimeSync)
        .append(", protocolVersion=").append(protocolVersion)
        .append(", institutionId=").append(institutionId)
        .append(", libraryName=").append(libraryName)
        .append(", supportedMessages=").append(supportedMessages)
        .append(", terminalLocation=").append(terminalLocation)
        .append(", screenMessage=").append(screenMessage)
        .append(", printLine=").append(printLine)
        .append(']').toString();
  }

  /**
   * Builder for {@code ACSStatus}.
   */
  public static class ACSStatusBuilder {
    private Boolean onLineStatus;
    private Boolean checkinOk;
    private Boolean checkoutOk;
    private Boolean acsRenewalPolicy;
    private Boolean statusUpdateOk;
    private Boolean offLineOk;
    private Integer timeoutPeriod;
    private Integer retriesAllowed;
    private OffsetDateTime dateTimeSync;
    private String protocolVersion;
    private String institutionId;
    private String libraryName;
    private Set<Messages>  supportedMessages;
    private String terminalLocation;
    private List<String> screenMessage;
    private List<String> printLine;

    private ACSStatusBuilder() {
      super();
    }

    public ACSStatusBuilder onLineStatus(Boolean onLineStatus) {
      this.onLineStatus = onLineStatus;
      return this;
    }

    public ACSStatusBuilder checkinOk(Boolean checkinOk) {
      this.checkinOk = checkinOk;
      return this;
    }

    public ACSStatusBuilder checkoutOk(Boolean checkoutOk) {
      this.checkoutOk = checkoutOk;
      return this;
    }

    public ACSStatusBuilder acsRenewalPolicy(Boolean acsRenewalPolicy) {
      this.acsRenewalPolicy = acsRenewalPolicy;
      return this;
    }

    public ACSStatusBuilder statusUpdateOk(Boolean statusUpdateOk) {
      this.statusUpdateOk = statusUpdateOk;
      return this;
    }

    public ACSStatusBuilder offLineOk(Boolean offLineOk) {
      this.offLineOk = offLineOk;
      return this;
    }

    public ACSStatusBuilder timeoutPeriod(Integer timeoutPeriod) {
      this.timeoutPeriod = timeoutPeriod;
      return this;
    }

    public ACSStatusBuilder retriesAllowed(Integer retriesAllowed) {
      this.retriesAllowed = retriesAllowed;
      return this;
    }

    public ACSStatusBuilder dateTimeSync(OffsetDateTime dateTimeSync) {
      this.dateTimeSync = dateTimeSync;
      return this;
    }

    public ACSStatusBuilder protocolVersion(String protocolVersion) {
      this.protocolVersion = protocolVersion;
      return this;
    }

    public ACSStatusBuilder institutionId(String institutionId) {
      this.institutionId = institutionId;
      return this;
    }

    public ACSStatusBuilder libraryName(String libraryName) {
      this.libraryName = libraryName;
      return this;
    }

    public ACSStatusBuilder supportedMessages(Set<Messages>  supportedMessages) {
      this.supportedMessages = supportedMessages;
      return this;
    }

    public ACSStatusBuilder terminalLocation(String terminalLocation) {
      this.terminalLocation = terminalLocation;
      return this;
    }

    public ACSStatusBuilder screenMessage(List<String> screenMessage) {
      this.screenMessage = screenMessage;
      return this;
    }

    public ACSStatusBuilder printLine(List<String> printLine) {
      this.printLine = printLine;
      return this;
    }

    public ACSStatus build() {
      return new ACSStatus(this);
    }
  }
}
