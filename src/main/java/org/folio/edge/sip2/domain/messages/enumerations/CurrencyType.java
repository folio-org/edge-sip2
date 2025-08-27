package org.folio.edge.sip2.domain.messages.enumerations;

/**
 * The supported types of currency.
 *
 * <p>See <a href="https://en.wikipedia.org/wiki/ISO_4217">ISO 4217 (Wikipedia)</a>
 * for active and historical currency codes.
 *
 * <p>See also the currency lists in <a href=
 * "https://github.com/folio-org/mod-finance/blob/3904eebdb3e9407e93913d59f875b8d9b5a605b5/src/main/java/org/folio/services/exchange/handler/TreasuryGovCustomJsonHandler.java#L73-L224"
 * >mod-finance TreasuryGovCustomJsonHandler</a> and <a href=
 * "https://github.com/folio-org/mod-fqm-manager/blob/v4.0.0/src/main/java/org/folio/fqm/repository/DataRefreshRepository.java#L39-L71"
 * >mod-fqm-manager DataRefreshRepository</a>.
 */
public enum CurrencyType {
  /** United Arab Emirates-Dirham. */
  AED,
  /** Afghanistan-Afghani. */
  AFN,
  /** Albania-Lek. */
  ALL,
  /** Armenia-Dram. */
  AMD,
  /** Netherlands Antilles-Guilder. */
  ANG,
  /** Angola-Kwanza. */
  AOA,
  /** Argentina-Peso. */
  ARS,
  /** Australia-Dollar. */
  AUD,
  /** Azerbaijan-Manat. */
  AZN,
  /** Bosnia-Marka. */
  BAM,
  /** Barbados-Dollar. */
  BBD,
  /** Bangladesh-Taka. */
  BDT,
  /** Bulgaria-Lev New. */
  BGN,
  /** Bahrain-Dinar. */
  BHD,
  /** Burundi-Franc. */
  BIF,
  /** Bermuda-Dollar. */
  BMD,
  /** Brunei-Dollar. */
  BND,
  /** Bolivia-Boliviano. */
  BOB,
  /** Brazil-Real. */
  BRL,
  /** Bahamas-Dollar. */
  BSD,
  /** Botswana-Pula. */
  BWP,
  /** Belarus-New Ruble. */
  BYN,
  /** Belize-Dollar. */
  BZD,
  /** Canada-Dollar. */
  CAD,
  /** Democratic Republic Of Congo-Congolese Franc. */
  CDF,
  /** Switzerland-Franc. */
  CHF,
  /** Chile-Peso. */
  CLP,
  /** China-Renminbi. */
  CNY,
  /** Colombia-Peso. */
  COP,
  /** Costa Rica-Colon. */
  CRC,
  /** Cuba-Chavito. */
  CUC,
  /** Cuba-Peso. */
  CUP,
  /** Cape Verde-Escudo. */
  CVE,
  /** Czech Republic-Koruna. */
  CZK,
  /** Djibouti-Franc. */
  DJF,
  /** Denmark-Krone. */
  DKK,
  /** Dominican Republic-Peso. */
  DOP,
  /** Algeria-Dinar. */
  DZD,
  /** Egypt-Pound. */
  EGP,
  /** Eritrea-Nakfa. */
  ERN,
  /** Ethiopia-Birr. */
  ETB,
  /** Euro Zone-Euro. */
  EUR,
  /** Fiji-Dollar. */
  FJD,
  /** United Kingdom-Pound. */
  GBP,
  /** Georgia-Lari. */
  GEL,
  /** Ghana-Cedi. */
  GHS,
  /** Gambia-Dalasi. */
  GMD,
  /** Guinea-Franc. */
  GNF,
  /** Guatemala-Quetzal. */
  GTQ,
  /** Guyana-Dollar. */
  GYD,
  /** Hong Kong-Dollar. */
  HKD,
  /** Honduras-Lempira. */
  HNL,
  /** Croatia-Kuna. */
  HRK,
  /** Haiti-Gourde. */
  HTG,
  /** Hungary-Forint. */
  HUF,
  /** Indonesia-Rupiah. */
  IDR,
  /** Israel-Shekel. */
  ILS,
  /** India-Rupee. */
  INR,
  /** Iraq-Dinar. */
  IQD,
  /** Iran-Rial. */
  IRR,
  /** Iceland-Krona. */
  ISK,
  /** Jamaica-Dollar. */
  JMD,
  /** Jordan-Dinar. */
  JOD,
  /** Japan-Yen. */
  JPY,
  /** Kenya-Shilling. */
  KES,
  /** Kyrgyzstan-Som. */
  KGS,
  /** Cambodia-Riel. */
  KHR,
  /** Comoros-Franc. */
  KMF,
  /** Korea-Won. */
  KRW,
  /** Kuwait-Dinar. */
  KWD,
  /** Cayman Islands-Dollar. */
  KYD,
  /** Kazakhstan-Tenge. */
  KZT,
  /** Laos-Kip. */
  LAK,
  /** Lebanon-Pound. */
  LBP,
  /** Sri Lanka-Rupee. */
  LKR,
  /** Liberia-Dollar. */
  LRD,
  /** Lesotho-Maloti. */
  LSL,
  /** Libya-Dinar. */
  LYD,
  /** Morocco-Dirham. */
  MAD,
  /** Moldova-Leu. */
  MDL,
  /** Madagascar-Ariary. */
  MGA,
  /** Republic Of North Macedonia-Denar. */
  MKD,
  /** Myanmar-Kyat. */
  MMK,
  /** Mongolia-Tugrik. */
  MNT,
  /** Mauritania-Ouguiya. */
  MRU,
  /** Mauritius-Rupee. */
  MUR,
  /** Maldives-Rufiyaa. */
  MVR,
  /** Malawi-Kwacha. */
  MWK,
  /** Mexico-Peso. */
  MXN,
  /** Malaysia-Ringgit. */
  MYR,
  /** Mozambique-Metical. */
  MZN,
  /** Namibia-Dollar. */
  NAD,
  /** Nigeria-Naira. */
  NGN,
  /** Nicaragua-Cordoba. */
  NIO,
  /** Norway-Krone. */
  NOK,
  /** Nepal-Rupee. */
  NPR,
  /** New Zealand-Dollar. */
  NZD,
  /** Oman-Rial. */
  OMR,
  /** Panama-Balboa. */
  PAB,
  /** Peru-Sol. */
  PEN,
  /** Papua New Guinea-Kina. */
  PGK,
  /** Philippines-Peso. */
  PHP,
  /** Pakistan-Rupee. */
  PKR,
  /** Poland-Zloty. */
  PLN,
  /** Paraguay-Guarani. */
  PYG,
  /** Qatar-Riyal. */
  QAR,
  /** Romania-New Leu. */
  RON,
  /** Serbia-Dinar. */
  RSD,
  /** Russia-Ruble. */
  RUR,
  /** Rwanda-Franc. */
  RWF,
  /** Saudi Arabia-Riyal. */
  SAR,
  /** Solomon Islands-Dollar. */
  SBD,
  /** Seychelles-Rupee. */
  SCR,
  /** Sudan-Pound. */
  SDG,
  /** Sweden-Krona. */
  SEK,
  /** Singapore-Dollar. */
  SGD,
  /** Sierra Leone-Leone. */
  SLL,
  /** Somali-Shilling. */
  SOS,
  /** Suriname-Dollar. */
  SRD,
  /** South Sudan-Sudanese Pound. */
  SSP,
  /** Sao Tome & Principe-New Dobras. */
  STN,
  /** El Salvador-Dollar. */
  SVC,
  /** Syria-Pound. */
  SYP,
  /** Eswatini-Lilangeni. */
  SZL,
  /** Thailand-Baht. */
  THB,
  /** Tajikistan-Somoni. */
  TJS,
  /** Turkmenistan-New Manat. */
  TMT,
  /** Tunisia-Dinar. */
  TND,
  /** Tonga-Pa'Anga. */
  TOP,
  /** Turkey-New Lira. */
  TRY,
  /** Trinidad & Tobago-Dollar. */
  TTD,
  /** Taiwan-Dollar. */
  TWD,
  /** Tanzania-Shilling. */
  TZS,
  /** Ukraine-Hryvnia. */
  UAH,
  /** Uganda-Shilling. */
  UGX,
  /** United States of America-Dollar. */
  USD,
  /** Uruguay-Peso. */
  UYU,
  /** Uzbekistan-Som. */
  UZS,
  /** Venezuela-Fuerte. */
  VEF,
  /** Venezuela-Bolivar Soberano. */
  VES,
  /** Vietnam-Dong. */
  VND,
  /** Vanuatu-Vatu. */
  VUV,
  /** Western Samoa-Tala. */
  WST,
  /** Central African Republic-Cfa Franc. */
  XAF,
  /** Antigua & Barbuda-East Caribbean Dollar. */
  XCD,
  /** Equatorial Guinea-Cfa Franc. */
  XFA,
  /** Guinea Bissau-Cfa Franc. */
  XOF,
  /** Yemen-Rial. */
  YER,
  /** South Africa-Rand. */
  ZAR,
  /** Zambia-New Kwacha. */
  ZMW,
  /** Zimbabwe-Gold. */
  ZWG,
  /** Zimbabwe-Rtgs. */
  ZWL;

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
