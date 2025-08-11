package org.folio.edge.sip2.domain.messages.enumerations;

/**
 * The supported types of currency.
 *
 * @author mreno-EBSCO
 *
 */
public enum CurrencyType {
  /** US Dollar. */
  USD,
  /** Canadian Dollar. */
  CAD,
  /** Pound Streling. */
  GBP,
  /** French Franc. */
  FRF,
  /** Deutsche Mark. */
  DEM,
  /** Italian Lira. */
  ITL,
  /** Spanish Peseta. */
  ESP,
  /** Yen. */
  JPY,
  /** Euro. */
  EUR,
  /** Danish Krone. */
  DKK,
  /** Australian Dollar. */
  AUD,
  /** Colombian Peso. */
  COP,
  /** South African rand. **/
  ZAR,
  /** Malaysian Malaysian ringgit. */
  MYR;

  /**
   * Returns the corresponding {@link CurrencyType} for the given string value.
   * If the value does not match any enum constant, returns {@code null}.
   *
   * @param value the string representation of the currency type
   * @return the matching {@code CurrencyType}, or {@code null} if not found
   */
  public static CurrencyType fromStringSafe(String value) {
    for (CurrencyType type : CurrencyType.values()) {
      if (type.name().equals(value)) {
        return type;
      }
    }
    return null;
  }
}
