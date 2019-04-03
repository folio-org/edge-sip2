package org.folio.edge.sip2.parser;

import java.util.Arrays;

/**
 * Valid SIP fields.
 * @author mreno-EBSCO
 *
 */
public enum Field {
  UNKNOWN(""),
  AA("AA"),
  AB("AB"),
  AC("AC"),
  AD("AD"),
  AJ("AJ"),
  AL("AL"),
  AO("AO"),
  AP("AP"),
  BI("BI"),
  BK("BK"),
  BO("BO"),
  BP("BP"),
  BQ("BQ"),
  BS("BS"),
  BV("BV"),
  BW("BW"),
  BY("BY"),
  CG("CG"),
  CH("CH"),
  CN("CN"),
  CO("CO"),
  CP("CP");

  private final String identifier;

  private Field(String identifier) {
    this.identifier = identifier;
  }

  /**
   * Find an enum base on the field identifier.
   *
   * @param identifier the field identifier.
   * @return the found field enum or {@code UNKNOWN} if not found.
   */
  public static Field find(String identifier) {
    return Arrays.stream(values())
        .filter(status -> status.identifier.equals(identifier))
        .findFirst()
        .orElse(UNKNOWN);
  }
}
