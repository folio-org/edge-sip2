package org.folio.edge.sip2.support.response;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;
import org.folio.edge.sip2.domain.messages.enumerations.Messages;
import org.folio.edge.sip2.domain.messages.responses.ACSStatus;

public class ScStatusResponseParser extends Sip2ResponseParser<ACSStatus> {

  public ScStatusResponseParser(char delimiter, String timezone) {
    super(delimiter, timezone);
  }

  @Override
  public ACSStatus parseBody(char[] messageChars) {
    var builder = ACSStatus.builder();
    builder.onLineStatus(parseBoolean(messageChars));
    builder.checkinOk(parseBoolean(messageChars));
    builder.checkoutOk(parseBoolean(messageChars));
    builder.acsRenewalPolicy(parseBoolean(messageChars));
    builder.statusUpdateOk(parseBoolean(messageChars));
    builder.offLineOk(parseBoolean(messageChars));
    builder.timeoutPeriod(parseInteger(messageChars, 3));
    builder.retriesAllowed(parseInteger(messageChars, 3));
    builder.dateTimeSync(parseDateTime(messageChars));
    builder.protocolVersion(parseString(messageChars, 4));

    var printLines = new ArrayList<String>();
    var screenMessages = new ArrayList<String>();
    parseVariableLengthFields(messageChars, (fieldCode, fieldValue) -> {
      switch (fieldCode) {
        case "AO" -> builder.institutionId(fieldValue);
        case "AM" -> builder.libraryName(fieldValue);
        case "BX" -> builder.supportedMessages(parseSupportedMessages(fieldValue));
        case "AN" -> builder.terminalLocation(fieldValue);
        case "AF" -> screenMessages.add(fieldValue);
        case "AG" -> printLines.add(fieldValue);
        default -> doNothing();
      }
    });

    builder.screenMessage(screenMessages);
    builder.printLine(printLines);

    return builder.build();
  }

  @Override
  public int getCommandCode() {
    return 98;
  }

  private Set<Messages> parseSupportedMessages(String supportedMessagesStr) {
    Set<Messages> supportedMessages = EnumSet.noneOf(Messages.class);

    if (supportedMessagesStr.length() < 16) {
      throw new IllegalArgumentException(
          "Invalid message (BX): expected 16 chars but got: " + supportedMessagesStr.length());
    }

    var messageTypes = Messages.values();
    for (int i = 0; i < Math.min(messageTypes.length, supportedMessagesStr.length()); i++) {
      char flag = supportedMessagesStr.charAt(i);
      if (flag == 'Y') {
        supportedMessages.add(messageTypes[i]);
      }
    }

    return supportedMessages;
  }
}
