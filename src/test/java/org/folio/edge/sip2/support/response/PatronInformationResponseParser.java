package org.folio.edge.sip2.support.response;

import java.util.ArrayList;
import org.folio.edge.sip2.domain.messages.enumerations.CurrencyType;
import org.folio.edge.sip2.domain.messages.responses.PatronInformationResponse;

public class PatronInformationResponseParser extends Sip2ResponseParser<PatronInformationResponse> {

  public PatronInformationResponseParser(char delimiter, String timezone) {
    super(delimiter, timezone);
  }

  @Override
  public PatronInformationResponse parseBody(char[] messageChars) {
    var builder = PatronInformationResponse.builder();
    builder.patronStatus(parsePatronStatuses(messageChars));
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

    parseVariableLengthFields(messageChars, (fieldCode, fieldValue) -> {
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
        default -> doNothing();
      }
    });

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

  @Override
  public int getCommandCode() {
    return 64;
  }
}
