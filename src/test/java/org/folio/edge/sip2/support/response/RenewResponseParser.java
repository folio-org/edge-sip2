package org.folio.edge.sip2.support.response;

import java.util.ArrayList;
import org.folio.edge.sip2.domain.messages.enumerations.CurrencyType;
import org.folio.edge.sip2.domain.messages.enumerations.FeeType;
import org.folio.edge.sip2.domain.messages.enumerations.MediaType;
import org.folio.edge.sip2.domain.messages.responses.RenewResponse;

public class RenewResponseParser extends Sip2ResponseParser<RenewResponse> {

  public RenewResponseParser(char delimiter, String timezone) {
    super(delimiter, timezone);
  }

  @Override
  public RenewResponse parse(String responseMessage) {
    if (responseMessage == null || responseMessage.length() < 2) {
      throw new IllegalArgumentException("Invalid response message");
    }

    position = 0;
    var messageChars = responseMessage.toCharArray();
    var statusCode = parseString(messageChars, 2);
    if (!"30".equals(statusCode)) {
      throw new IllegalArgumentException("Invalid message type: expected 30, got: " + statusCode);
    }

    var builder = RenewResponse.builder();
    builder.ok(parseBoolean(messageChars));
    builder.renewalOk(parseBoolean(messageChars));
    builder.magneticMedia(parseBoolean(messageChars));
    builder.desensitize(parseBoolean(messageChars));
    builder.transactionDate(parseDateTime(messageChars));

    var screenMessages = new ArrayList<String>();
    var printLines = new ArrayList<String>();

    while (position < messageChars.length && messageChars[position] != delimiter) {
      var fieldCode = parseFieldCode(messageChars);
      var fieldValue = parseVariableLengthField(messageChars);

      switch (fieldCode) {
        case "AO" -> builder.institutionId(fieldValue);
        case "AA" -> builder.patronIdentifier(fieldValue);
        case "AB" -> builder.itemIdentifier(fieldValue);
        case "AJ" -> builder.titleIdentifier(fieldValue);
        case "AH" -> builder.dueDate(parseDateTime(fieldValue));
        case "BT" -> builder.feeType(FeeType.valueOf(fieldValue));
        case "BH" -> builder.currencyType(CurrencyType.fromStringSafe(fieldValue));
        case "BV" -> builder.feeAmount(fieldValue);
        case "CK" -> builder.mediaType(MediaType.valueOf(fieldValue));
        case "CH" -> builder.itemProperties(fieldValue);
        case "CI" -> builder.securityInhibit(parseBoolean(fieldValue));
        case "BU" -> builder.transactionId(fieldValue);
        case "AF" -> screenMessages.add(fieldValue);
        case "AG" -> printLines.add(fieldValue);
        default -> {
          // Ignore unrecognized field codes
        }
      }

      if (position < messageChars.length && messageChars[position] == delimiter) {
        position++;
      }
    }

    builder.screenMessage(screenMessages);
    builder.printLine(printLines);

    return builder.build();
  }
}
