package org.folio.edge.sip2.session;

import org.folio.edge.sip2.domain.PreviousMessage;

public class SessionData {
  private final char fieldDelimiter;
  private final String tenant;
  private final boolean errorDetectionEnabled;
  private final String charset;

  private String scLocation;
  private String authenticationToken;
  private int maxPrintWidth = -1; // since 0 is valid
  private String username;
  private String password; // should we really save this?
  private PreviousMessage previousMessage;
  private String timeZone;

  private SessionData(String tenant, char fieldDelimiter,
      boolean errorDetectionEnabled, String charset) {
    this.tenant = tenant;
    this.fieldDelimiter = fieldDelimiter;
    this.errorDetectionEnabled = errorDetectionEnabled;
    this.charset = charset;
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
    return timeZone;
  }

  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }

  public static SessionData createSession(String tenant, char fieldDelimiter,
      boolean errorDetectionEnabled, String charset) {
    return new SessionData(tenant, fieldDelimiter, errorDetectionEnabled,
        charset);
  }
}
