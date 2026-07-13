package org.folio.edge.sip2.support.response;

import java.util.ArrayList;
import org.folio.edge.sip2.domain.messages.responses.PatronStatusResponse;

public class PatronStatusResponseParser extends Sip2ResponseParser<PatronStatusResponse> {

  public PatronStatusResponseParser(char delimiter, String timezone) {
    super(delimiter, timezone);
  }

  @Override
  public PatronStatusResponse parseBody(char[] messageChars) {
    var builder = PatronStatusResponse.builder();
    builder.patronStatus(parsePatronStatuses(messageChars));
    builder.language(parseLanguage(messageChars));
    builder.transactionDate(parseDateTime(messageChars));

    var screenMessages = new ArrayList<String>();
    parseVariableLengthFields(messageChars, (fieldCode, fieldValue) -> {
      switch (fieldCode) {
        case "AO" -> builder.institutionId(fieldValue);
        case "AA" -> builder.patronIdentifier(fieldValue);
        case "AE" -> builder.personalName(fieldValue);
        case "AF" -> screenMessages.add(fieldValue);
        case "BL" -> builder.validPatron(parseBoolean(fieldValue));
        case "CQ" -> builder.validPatronPassword(parseBoolean(fieldValue));
        case "BV" -> builder.feeAmount(fieldValue);
        default -> doNothing();
      }
    });

    builder.screenMessage(screenMessages.isEmpty() ? null : screenMessages);
    return builder.build();
  }

  @Override
  public int getCommandCode() {
    return 24;
  }
}
