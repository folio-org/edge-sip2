package org.folio.edge.sip2.support.response;

import java.util.ArrayList;
import java.util.EnumSet;
import org.folio.edge.sip2.domain.messages.enumerations.PatronStatus;
import org.folio.edge.sip2.domain.messages.responses.PatronStatusResponse;

public class PatronStatusResponseParser extends Sip2ResponseParser<PatronStatusResponse> {

  public PatronStatusResponseParser(char delimiter, String timezone) {
    super(delimiter, timezone);
  }

  @Override
  public PatronStatusResponse parse(String responseMessage) {
    if (responseMessage == null || responseMessage.length() < 2) {
      throw new IllegalArgumentException("Invalid response message");
    }

    position = 0;
    var messageChars = responseMessage.toCharArray();
    var statusCode = parseString(messageChars, 2);
    if (!"24".equals(statusCode)) {
      throw new IllegalArgumentException("Invalid message type: expected 24, got: " + statusCode);
    }

    var builder = PatronStatusResponse.builder();
    builder.patronStatus(parsePatronStatus(messageChars, 14));
    parseString(messageChars, 3);
    builder.transactionDate(parseDateTime(messageChars));

    var screenMessages = new ArrayList<String>();

    while (position < messageChars.length && messageChars[position] != delimiter) {
      var fieldCode = parseFieldCode(messageChars);
      var fieldValue = parseVariableLengthField(messageChars);

      switch (fieldCode) {
        case "AO" -> builder.institutionId(fieldValue);
        case "AA" -> builder.patronIdentifier(fieldValue);
        case "AE" -> builder.personalName(fieldValue);
        case "AF" -> screenMessages.add(fieldValue);
        case "BL" -> builder.validPatron(parseBoolean(fieldValue));
        case "CQ" -> builder.validPatronPassword(parseBoolean(fieldValue));
        case "BV" -> builder.feeAmount(fieldValue);
        default -> {
          // ignore unrecognized fields
        }
      }

      if (position < messageChars.length && messageChars[position] == delimiter) {
        position++;
      }
    }

    builder.screenMessage(screenMessages.isEmpty() ? null : screenMessages);
    return builder.build();
  }

  private EnumSet<PatronStatus> parsePatronStatus(char[] messageChars, int length) {
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
}
