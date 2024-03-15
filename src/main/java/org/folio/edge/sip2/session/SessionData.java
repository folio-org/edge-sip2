package org.folio.edge.sip2.session;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.PreviousMessage;


public class SessionData {
  private final char fieldDelimiter;
  private final String tenant;
  private final boolean errorDetectionEnabled;
  private final String charset;
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

  private static final Logger log = LogManager.getLogger();
  private static final String DEFAULT_CURRENCY = "USD";
  private static final String DEFAULT_TIMEZONE = "Etc/UTC";


  private SessionData(String tenant, char fieldDelimiter,
                      boolean errorDetectionEnabled, String charset) {
    this.tenant = tenant;
    this.fieldDelimiter = fieldDelimiter;
    this.errorDetectionEnabled = errorDetectionEnabled;
    this.charset = charset;
    this.rejectedCheckinStatusList = new ArrayList<>();
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

  public boolean isValidCheckinStatus(String status) {
    if (this.rejectedCheckinStatusList.contains(status)) {
      return false;
    }
    return true;
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

  public void setPatronPasswordVerificationRequired(boolean patronPasswordVerificationRequired) {
    this.patronPasswordVerificationRequired = patronPasswordVerificationRequired;
  }

  /**
   * Initialize a new session.
   *
   * @param tenant The current tenant
   * @param fieldDelimiter The delimeter used
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
}
