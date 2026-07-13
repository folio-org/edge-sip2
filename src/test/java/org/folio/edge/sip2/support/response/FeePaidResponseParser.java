package org.folio.edge.sip2.support.response;

import java.util.ArrayList;
import org.folio.edge.sip2.domain.messages.responses.FeePaidResponse;

public class FeePaidResponseParser extends Sip2ResponseParser<FeePaidResponse> {

  public FeePaidResponseParser(char delimiter, String timezone) {
    super(delimiter, timezone);
  }

  @Override
  public FeePaidResponse parseBody(char[] messageChars) {
    var builder = FeePaidResponse.builder();
    builder.paymentAccepted(parseBoolean(messageChars));
    builder.transactionDate(parseDateTime(messageChars));

    var screenMessages = new ArrayList<String>();
    var printLines = new ArrayList<String>();
    parseVariableLengthFields(messageChars, (fieldCode, fieldValue) -> {
      switch (fieldCode) {
        case "AO" -> builder.institutionId(fieldValue);
        case "AA" -> builder.patronIdentifier(fieldValue);
        case "AE" -> builder.transactionId(fieldValue);
        case "AF" -> screenMessages.add(fieldValue);
        case "BL" -> printLines.add(fieldValue);
        default -> doNothing();
      }
    });

    builder.screenMessage(screenMessages);
    builder.printLine(printLines);
    return builder.build();
  }

  @Override
  public int getCommandCode() {
    return 38;
  }
}
