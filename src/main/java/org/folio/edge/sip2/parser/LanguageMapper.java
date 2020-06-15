package org.folio.edge.sip2.parser;

import java.util.Arrays;
import org.folio.edge.sip2.domain.messages.enumerations.Language;

/**
 * Valid SIP commands.
 * @author mreno-EBSCO
 *
 */
public enum LanguageMapper {
  UNKNOWN("000", Language.UNKNOWN),
  ENGLISH("001", Language.ENGLISH),
  FRENCH("002", Language.FRENCH),
  GERMAN("003", Language.GERMAN),
  ITALIAN("004", Language.ITALIAN),
  DUTCH("005", Language.DUTCH),
  SWEDISH("006", Language.SWEDISH),
  FINNISH("007", Language.FINNISH),
  SPANISH("008", Language.SPANISH),
  DANISH("009", Language.DANISH),
  PORTUGUESE("010", Language.PORTUGUESE),
  CANADIAN_FRENCH("011", Language.CANADIAN_FRENCH),
  NORWEGIAN("012", Language.NORWEGIAN),
  HEBREW("013", Language.HEBREW),
  JAPANESE("014", Language.JAPANESE),
  RUSSIAN("015", Language.RUSSIAN),
  ARABIC("016", Language.ARABIC),
  POLISH("017", Language.POLISH),
  GREEK("018", Language.GREEK),
  CHINESE("019", Language.CHINESE),
  KOREAN("020", Language.KOREAN),
  NORTH_AMERICAN_SPANISH("021", Language.NORTH_AMERICAN_SPANISH),
  TAMIL("022", Language.TAMIL),
  MALAY("023", Language.MALAY),
  UNITED_KINGDOM("024", Language.UNITED_KINGDOM),
  ICELANDIC("025", Language.ICELANDIC),
  BELGIAN("026", Language.BELGIAN),
  TAIWANESE("027", Language.TAIWANESE);

  private final String code;
  private final Language language;

  private LanguageMapper(String code, Language language) {
    this.code = code;
    this.language = language;
  }

  public Language getLanguage() {
    return language;
  }

  public String code() {
    return code;
  }

  /**
   * Find an enum based on the language code.
   *
   * @param code the language code.
   * @return the found mapper enum or {@code UNKNOWN} if not found.
   */
  public static LanguageMapper find(String code) {
    return Arrays.stream(values())
        .filter(status -> status.code.equals(code))
        .findFirst()
        .orElse(UNKNOWN);
  }

  /**
   * Find an enum based on the language enum.
   *
   * @param language the language enum value.
   * @return the found mapper enum or {@code UNKNOWN} if not found.
   */
  public static LanguageMapper find(Language language) {
    return Arrays.stream(values())
        .filter(e -> e.language == language)
        .findFirst()
        .orElse(UNKNOWN);
  }
}
