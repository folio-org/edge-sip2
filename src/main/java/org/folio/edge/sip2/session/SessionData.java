package org.folio.edge.sip2.session;

import static java.lang.String.format;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.folio.edge.sip2.domain.PreviousMessage;
import org.folio.edge.sip2.domain.integration.login.FolioLoginResponse;
import org.folio.edge.sip2.domain.messages.enumerations.CurrencyType;
import org.folio.edge.sip2.repositories.SettingsRepository;
import org.folio.edge.sip2.utils.Sip2LogAdapter;

@Data
@EqualsAndHashCode
@RequiredArgsConstructor
public class SessionData {

  private static final Sip2LogAdapter log = Sip2LogAdapter.getLogger(SettingsRepository.class);

  public static final String DEFAULT_CURRENCY = "USD";
  public static final String DEFAULT_TIMEZONE = "Etc/UTC";

  private final char fieldDelimiter;
  private final boolean errorDetectionEnabled;
  private final String charset;
  private final String requestId;
  private String tenant;
  private String loginErrorMessage;
  private Object errorResponseMessage;
  private String scLocation;
  private int maxPrintWidth = -1; // since 0 is valid
  private String username;
  private String password; // should we really save this?
  private String authenticationToken;
  private PreviousMessage previousMessage;
  private String timeZone;
  private String currency;
  private boolean patronPasswordVerificationRequired;
  private List<String> rejectedCheckinStatusList;
  private boolean configurationLoaded;
  private boolean usePinForPatronVerification;
  private boolean alwaysCheckPatronPassword;
  private FolioLoginResponse loginResponse;

  private SessionData(String tenant, char fieldDelimiter,
                      boolean errorDetectionEnabled, String charset) {
    this.tenant = tenant;
    this.requestId = generateRequestId();
    this.fieldDelimiter = fieldDelimiter;
    this.errorDetectionEnabled = errorDetectionEnabled;
    this.charset = charset;
    this.rejectedCheckinStatusList = new ArrayList<>();
    this.configurationLoaded = false;
    this.usePinForPatronVerification = false;
    this.alwaysCheckPatronPassword = true;
  }

  /**
   * Check to see if a given status is invalid for checkin.
   *
   * @param status The status to check
   * @return True if the status is okay to checkin
   */
  public boolean isValidCheckinStatus(String status) {
    //Case-insensitive search of whether we have the status in question
    return this.rejectedCheckinStatusList.stream().noneMatch(status::equalsIgnoreCase);
  }

  /**
   * Set the list of item statuses to reject checkin on.
   * @param list The list of statuses
   */
  public void setInvalidCheckinStatusList(List<String> list) {
    this.rejectedCheckinStatusList = list;
  }

  public String getTimeZone() {
    return timeZone != null ? timeZone : DEFAULT_TIMEZONE;
  }

  /**
   * Set the time zone.
   * timeZone The timeZone value.
   */
  public void setTimeZone(String timeZone) {
    if (timeZone == null) {
      log.warn("The timezone value is null and therefore "
          + "default value {} will be used.", DEFAULT_TIMEZONE);
    }
    this.timeZone = timeZone;
  }

  /**
   * Initialize a new session.
   *
   * @param tenant The current tenant
   * @param fieldDelimiter The delimiter used
   * @param errorDetectionEnabled Whether or not error detection is enabled
   * @param charset The preferred charset
   * @return The new session object
   */
  public static SessionData createSession(String tenant, char fieldDelimiter,
                                          boolean errorDetectionEnabled, String charset) {
    log.debug("New session created");
    return new SessionData(tenant, fieldDelimiter, errorDetectionEnabled,
      charset);
  }

  /**
   * Gets the currency for the session.
   * Returns the session currency if set, otherwise returns the default currency.
   *
   * @return the currency code as a String
   */
  public String getCurrency() {
    return currency != null ? currency : DEFAULT_CURRENCY;
  }

  /**
   * Setter for currency.
   *
   * @param currency The currency value to set the session to
   */
  public void setCurrency(CurrencyType currency) {
    if (currency == null) {
      log.warn("Null currency value, therefore default value {} will be used", DEFAULT_CURRENCY);
      return;
    }

    this.currency = currency.name();
  }

  private static String generateRequestId() {
    var random = new SecureRandom();
    return format("%06d%s", random.nextInt(1000000), "/sip2");
  }

  /**
   * Sets the tenant for the session and logs the change.
   *
   * @param tenant the new tenant identifier
   */
  public void setTenant(String tenant) {
    log.debug(this, "Tenant is updated in session data: old='{}', new='{}'", this.tenant, tenant);
    this.tenant = tenant;
  }
}
