package org.folio.edge.sip2.repositories.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TenantLocaleConfig {

  private String locale;
  private String currency;
  private String timezone;

  /**
   * Sets locale for {@link TenantLocaleConfig} and returns {@link TenantLocaleConfig}.
   *
   * @return this {@link TenantLocaleConfig} with new locale value
   */
  public TenantLocaleConfig locale(String locale) {
    this.locale = locale;
    return this;
  }

  /**
   * Sets currency for {@link TenantLocaleConfig} and returns {@link TenantLocaleConfig}.
   *
   * @return this {@link TenantLocaleConfig} with new currency value
   */
  public TenantLocaleConfig currency(String currency) {
    this.currency = currency;
    return this;
  }

  /**
   * Sets timezone for {@link TenantLocaleConfig} and returns {@link TenantLocaleConfig}.
   *
   * @return this {@link TenantLocaleConfig} with new timezone value
   */
  public TenantLocaleConfig timezone(String timezone) {
    this.timezone = timezone;
    return this;
  }
}
