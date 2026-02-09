package org.folio.edge.sip2.support.response;

import java.util.ArrayList;
import java.util.EnumSet;
import org.folio.edge.sip2.domain.messages.enumerations.CurrencyType;
import org.folio.edge.sip2.domain.messages.enumerations.Language;
import org.folio.edge.sip2.domain.messages.enumerations.PatronStatus;
import org.folio.edge.sip2.domain.messages.responses.PatronInformationResponse;
import org.folio.edge.sip2.parser.LanguageMapper;

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
    builder.patronStatus(parsePatronStatus(messageChars, 14));
    builder.language(parseLanguage(messageChars));
    builder.transactionDate(parseDateTime(messageChars));
    builder.holdItemsCount(parseInteger(messageChars, 4));
    builder.overdueItemsCount(parseInteger(messageChars, 4));
    builder.chargedItemsCount(parseInteger(messageChars, 4));
    builder.fineItemsCount(parseInteger(messageChars, 4));
    builder.recallItemsCount(parseInteger(messageChars, 4));
    builder.unavailableHoldsCount(parseInteger(messageChars, 4));

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
        case "BH" -> builder.currencyType(CurrencyType.fromStringSafe(fieldValue));
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
        case "BL" -> builder.validPatron(parseBoolean(fieldValue));
        case "CQ" -> builder.validPatronPassword(parseBoolean(fieldValue));
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

  private Language parseLanguage(char[] messageChars) {
    var langCode = parseString(messageChars, 3);
    return LanguageMapper.find(langCode).getLanguage();
  }
}
