package org.folio.edge.sip2.repositories.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScStationConfig {

  private Integer retriesAllowed;
  private Integer timeoutPeriod;
  private Boolean checkinOk;
  private Boolean acsRenewalPolicy;
  private Boolean checkoutOk;
  private String libraryName;
  private String terminalLocation;

  /**
   * Sets retriesAllowed for {@link ScStationConfig} and returns {@link ScStationConfig}.
   *
   * @return this {@link ScStationConfig} with new retriesAllowed value
   */
  public ScStationConfig retriesAllowed(Integer retriesAllowed) {
    this.retriesAllowed = retriesAllowed;
    return this;
  }

  /**
   * Sets timeoutPeriod for {@link ScStationConfig} and returns {@link ScStationConfig}.
   *
   * @return this {@link ScStationConfig} with new timeoutPeriod value
   */
  public ScStationConfig timeoutPeriod(Integer timeoutPeriod) {
    this.timeoutPeriod = timeoutPeriod;
    return this;
  }

  /**
   * Sets checkinOk for {@link ScStationConfig} and returns {@link ScStationConfig}.
   *
   * @return this {@link ScStationConfig} with new checkinOk value
   */
  public ScStationConfig checkinOk(Boolean checkinOk) {
    this.checkinOk = checkinOk;
    return this;
  }

  /**
   * Sets acsRenewalPolicy for {@link ScStationConfig} and returns {@link ScStationConfig}.
   *
   * @return this {@link ScStationConfig} with new acsRenewalPolicy value
   */
  public ScStationConfig acsRenewalPolicy(Boolean acsRenewalPolicy) {
    this.acsRenewalPolicy = acsRenewalPolicy;
    return this;
  }

  /**
   * Sets checkoutOk for {@link ScStationConfig} and returns {@link ScStationConfig}.
   *
   * @return this {@link ScStationConfig} with new checkoutOk value
   */
  public ScStationConfig checkoutOk(Boolean checkoutOk) {
    this.checkoutOk = checkoutOk;
    return this;
  }

  /**
   * Sets libraryName for {@link ScStationConfig} and returns {@link ScStationConfig}.
   *
   * @return this {@link ScStationConfig} with new libraryName value
   */
  public ScStationConfig libraryName(String libraryName) {
    this.libraryName = libraryName;
    return this;
  }

  /**
   * Sets terminalLocation for {@link ScStationConfig} and returns {@link ScStationConfig}.
   *
   * @return this {@link ScStationConfig} with new terminalLocation value
   */
  public ScStationConfig terminalLocation(String terminalLocation) {
    this.terminalLocation = terminalLocation;
    return this;
  }
}
