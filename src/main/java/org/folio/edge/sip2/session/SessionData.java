package org.folio.edge.sip2.session;

import static java.lang.String.format;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.PreviousMessage;


public class SessionData {
  private final char fieldDelimiter;
  private final String tenant;
  private final boolean errorDetectionEnabled;
  private final String charset;
  private final String requestId;
  private String loginErrorMessage;
  private Object errorResponseMessage;
  private String scLocation;
  private String authenticationToken;
  private int maxPrintWidth = -1; // since 0 is valid
  private String username;
  private String password; // should we really save this?
  private PreviousMessage previousMessage;
  private String timeZone;
  private String currency;
  private boolean patronPasswordVerificationRequired;
  private List<String> rejectedCheckinStatusList;
  private boolean configurationLoaded;
  private boolean usePinForPatronVerification;
  private boolean alwaysCheckPatronPassword;

  private static final Logger log = LogManager.getLogger();
  private static final String DEFAULT_CURRENCY = "USD";
  private static final String DEFAULT_TIMEZONE = "Etc/UTC";


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

  public String getScLocation() {
    return scLocation;
  }

  public void setScLocation(String scLocation) {
    this.scLocation = scLocation;
  }

  public String getAuthenticationToken() {
    return authenticationToken;
  }

  public void setAuthenticationToken(String authenticationToken) {
    this.authenticationToken = authenticationToken;
  }

  public int getMaxPrintWidth() {
    return maxPrintWidth;
  }

  public void setMaxPrintWidth(int maxPrintWidth) {
    this.maxPrintWidth = maxPrintWidth;
  }

  public String getUsername() {
    return username;
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

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public char getFieldDelimiter() {
    return fieldDelimiter;
  }

  public String getTenant() {
    return tenant;
  }

  public boolean isErrorDetectionEnabled() {
    return errorDetectionEnabled;
  }

  public String getCharset() {
    return charset;
  }

  public PreviousMessage getPreviousMessage() {
    return previousMessage;
  }

  public void setPreviousMessage(PreviousMessage message) {
    this.previousMessage = message;
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

  public boolean isPatronPasswordVerificationRequired() {
    return patronPasswordVerificationRequired;
  }

  /**
   * Are we to use patron pin instead of the password for verification.
   * @return boolean true or false
   */
  public boolean isUsePinForPatronVerification() {
    return usePinForPatronVerification;
  }

  public void setPatronPasswordVerificationRequired(boolean patronPasswordVerificationRequired) {
    this.patronPasswordVerificationRequired = patronPasswordVerificationRequired;
  }

  public void setUsePinForPatronVerification(boolean usePinForPatronVerification) {
    this.usePinForPatronVerification = usePinForPatronVerification;
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

  public String getLoginErrorMessage() {
    return loginErrorMessage;
  }

  public void setLoginErrorMessage(String loginErrorMessage) {
    this.loginErrorMessage = loginErrorMessage;
  }

  public void setErrorResponseMessage(Object errorResponseMessage) {
    this.errorResponseMessage = errorResponseMessage;
  }

  public Object getErrorResponseMessage() {
    return this.errorResponseMessage;
  }

  public String getCurrency() {
    return currency != null ? currency : DEFAULT_CURRENCY;
  }

  /**
   * Setter for currency.
   *
   * @param currency The currency value to set the session to
   */
  public void setCurrency(String currency) {
    if (currency == null) {
      log.warn("Null currency value, therefore default value {} will be used",
          DEFAULT_CURRENCY);
    } else {
      this.currency = currency;
    }
  }

  public void setAlwaysCheckPatronPassword(boolean flag) {
    this.alwaysCheckPatronPassword = flag;
  }

  public boolean isAlwaysCheckPatronPassword() {
    return alwaysCheckPatronPassword;
  }

  public void setConfigurationLoaded(boolean loaded) {
    this.configurationLoaded = loaded;
  }

  public boolean isConfigurationLoaded() {
    return this.configurationLoaded;
  }

  public String getRequestId()  {
    return this.requestId;
  }

  private static String generateRequestId() {
    var random = new SecureRandom();
    return format("%06d%s", random.nextInt(1000000), "/sip2");
  }
}
