package org.folio.edge.sip2.support.response;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import org.folio.edge.sip2.domain.messages.enumerations.CurrencyType;
import org.folio.edge.sip2.domain.messages.enumerations.Language;
import org.folio.edge.sip2.domain.messages.enumerations.PatronStatus;
import org.folio.edge.sip2.domain.messages.responses.PatronInformationResponse;

public class PatronInformationResponseParser extends Sip2ResponseParser<PatronInformationResponse> {

  public PatronInformationResponseParser(char delimiter, String timezone) {
    super(delimiter, timezone);
  }

  @Override
  public PatronInformationResponse parse(String responseMessage) {
    if (responseMessage == null || responseMessage.length() < 2) {
      throw new IllegalArgumentException("Invalid response message");
    }

    position = 0;
    var messageChars = responseMessage.toCharArray();
    var statusCode = parseString(messageChars, 2);
    if (!"64".equals(statusCode)) {
      throw new IllegalArgumentException("Invalid message type: expected 64, got: " + statusCode);
    }

    var builder = PatronInformationResponse.builder();

    // Parse patron status (14 characters)
    builder.patronStatus(parsePatronStatus(messageChars, 14));

    // Language (3 characters)
    builder.language(parseLanguage(messageChars, 3));

    // Transaction date (18 characters)
    builder.transactionDate(parseDateTime(messageChars));

    // Hold items count (4 characters)
    builder.holdItemsCount(parseInteger(messageChars, 4));

    // Overdue items count (4 characters)
    builder.overdueItemsCount(parseInteger(messageChars, 4));

    // Charged items count (4 characters)
    builder.chargedItemsCount(parseInteger(messageChars, 4));

    // Fine items count (4 characters)
    builder.fineItemsCount(parseInteger(messageChars, 4));

    // Recall items count (4 characters)
    builder.recallItemsCount(parseInteger(messageChars, 4));

    // Unavailable holds count (4 characters)
    var unavailableHoldsStr = parseString(messageChars, 4);
    if (unavailableHoldsStr != null && !unavailableHoldsStr.trim().isEmpty()) {
      try {
        builder.unavailableHoldsCount(Integer.valueOf(unavailableHoldsStr.trim()));
      } catch (NumberFormatException e) {
        // Invalid number
      }
    }

    var holdItems = new ArrayList<String>();
    var overdueItems = new ArrayList<String>();
    var chargedItems = new ArrayList<String>();
    var fineItems = new ArrayList<String>();
    var recallItems = new ArrayList<String>();
    var unavailableHoldItems = new ArrayList<String>();
    var screenMessages = new ArrayList<String>();
    var printLines = new ArrayList<String>();

    while (position < messageChars.length && messageChars[position] != delimiter) {
      var fieldCode = parseFieldCode(messageChars);
      var fieldValue = parseVariableLengthField(messageChars);

      switch (fieldCode) {
        case "AO" -> builder.institutionId(fieldValue);
        case "AA" -> builder.patronIdentifier(fieldValue);
        case "AE" -> builder.personalName(fieldValue);
        case "BH" -> builder.currencyType(parseCurrencyType(fieldValue));
        case "BV" -> builder.feeAmount(fieldValue);
        case "CC" -> builder.feeLimit(fieldValue);
        case "AS" -> holdItems.add(fieldValue);
        case "AT" -> overdueItems.add(fieldValue);
        case "AU" -> chargedItems.add(fieldValue);
        case "AV" -> fineItems.add(fieldValue);
        case "BU" -> recallItems.add(fieldValue);
        case "CD" -> unavailableHoldItems.add(fieldValue);
        case "BD" -> builder.homeAddress(fieldValue);
        case "BE" -> builder.emailAddress(fieldValue);
        case "BF" -> builder.homePhoneNumber(fieldValue);
        case "AF" -> screenMessages.add(fieldValue);
        case "AG" -> printLines.add(fieldValue);
        case "BL" -> builder.validPatron(parseYesNo(fieldValue));
        case "CQ" -> builder.validPatronPassword(parseYesNo(fieldValue));
        case "FU" -> builder.borrowerType(fieldValue);
        case "FV" -> builder.borrowerTypeDescription(fieldValue);
        default -> {
          // Ignore unrecognized field codes
        }
      }

      if (position < messageChars.length && messageChars[position] == delimiter) {
        position++;
      }
    }

    builder.holdItems(holdItems);
    builder.overdueItems(overdueItems);
    builder.chargedItems(chargedItems);
    builder.fineItems(fineItems);
    builder.recallItems(recallItems);
    builder.unavailableHoldItems(unavailableHoldItems);
    builder.screenMessage(screenMessages);
    builder.printLine(printLines);

    return builder.build();
  }

  private EnumSet<PatronStatus> parsePatronStatus(char[] messageChars, int length) {
    if (position + length > messageChars.length) {
      return EnumSet.noneOf(PatronStatus.class);
    }

    var statuses = EnumSet.noneOf(PatronStatus.class);
    var statusValues = PatronStatus.values();

    for (int i = 0; i < length && i < statusValues.length; i++) {
      if (messageChars[position + i] == 'Y') {
        statuses.add(statusValues[i]);
      }
    }

    position += length;
    return statuses;
  }

  private Language parseLanguage(char[] messageChars, int length) {
    var langCode = parseString(messageChars, length);
    if (langCode == null) {
      return Language.UNKNOWN;
    }

    return switch (langCode.trim()) {
      case "000" -> Language.UNKNOWN;
      case "001" -> Language.ENGLISH;
      case "002" -> Language.FRENCH;
      case "003" -> Language.GERMAN;
      case "004" -> Language.ITALIAN;
      case "005" -> Language.DUTCH;
      case "006" -> Language.SWEDISH;
      case "007" -> Language.FINNISH;
      case "008" -> Language.SPANISH;
      case "009" -> Language.DANISH;
      case "010" -> Language.PORTUGUESE;
      case "011" -> Language.CANADIAN_FRENCH;
      case "012" -> Language.NORWEGIAN;
      case "013" -> Language.HEBREW;
      case "014" -> Language.JAPANESE;
      case "015" -> Language.RUSSIAN;
      case "016" -> Language.ARABIC;
      case "017" -> Language.POLISH;
      case "018" -> Language.GREEK;
      case "019" -> Language.CHINESE;
      case "020" -> Language.KOREAN;
      case "021" -> Language.NORTH_AMERICAN_SPANISH;
      case "022" -> Language.TAMIL;
      case "023" -> Language.MALAY;
      case "024" -> Language.UNITED_KINGDOM;
      case "025" -> Language.ICELANDIC;
      case "026" -> Language.BELGIAN;
      case "027" -> Language.TAIWANESE;
      default -> Language.UNKNOWN;
    };
  }

  private CurrencyType parseCurrencyType(String value) {
    if (value == null || value.isEmpty()) {
      return null;
    }
    try {
      return CurrencyType.valueOf(value);
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  private Boolean parseYesNo(String value) {
    if (value == null || value.isEmpty()) {
      return null;
    }
    return "Y".equals(value);
  }
}
