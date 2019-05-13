package org.folio.edge.sip2.parser;

import static java.lang.Boolean.FALSE;
import static org.folio.edge.sip2.parser.Command.REQUEST_ACS_RESEND;

import java.nio.charset.Charset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.edge.sip2.domain.messages.requests.BlockPatron;
import org.folio.edge.sip2.domain.messages.requests.Checkin;
import org.folio.edge.sip2.domain.messages.requests.Checkout;
import org.folio.edge.sip2.domain.messages.requests.EndPatronSession;
import org.folio.edge.sip2.domain.messages.requests.FeePaid;
import org.folio.edge.sip2.domain.messages.requests.Hold;
import org.folio.edge.sip2.domain.messages.requests.ItemInformation;
import org.folio.edge.sip2.domain.messages.requests.ItemStatusUpdate;
import org.folio.edge.sip2.domain.messages.requests.Login;
import org.folio.edge.sip2.domain.messages.requests.PatronEnable;
import org.folio.edge.sip2.domain.messages.requests.PatronInformation;
import org.folio.edge.sip2.domain.messages.requests.PatronStatusRequest;
import org.folio.edge.sip2.domain.messages.requests.Renew;
import org.folio.edge.sip2.domain.messages.requests.RenewAll;
import org.folio.edge.sip2.domain.messages.requests.RequestACSResend;
import org.folio.edge.sip2.domain.messages.requests.SCStatus;
import org.folio.edge.sip2.parser.Message.MessageBuilder;
import org.folio.edge.sip2.utils.Utils;

/**
 * A SIP parser that supports v2 of the protocol.
 *
 * @author mreno-EBSCO
 *
 */
public final class Parser {
  private static final Logger log = LogManager.getLogger();

  private static final Charset DEFAULT_CHARSET = Charset.forName("IBM850");
  private static final Character DEFAULT_DELIMITER = Character.valueOf('|');
  private static final Boolean DEFAULT_ERROR_DETECTION_ENABLED = FALSE;

  private final Charset charset;
  private final Character delimiter;
  private final Boolean errorDetectionEnabled;
  private final String timezone;

  private Parser(ParserBuilder builder) {
    charset = builder.charset == null ? DEFAULT_CHARSET : builder.charset;
    delimiter = builder.delimiter == null
        ? DEFAULT_DELIMITER : builder.delimiter;
    errorDetectionEnabled = builder.errorDetectionEnabled == null
        ? DEFAULT_ERROR_DETECTION_ENABLED : builder.errorDetectionEnabled;
    timezone = Utils.isStringNullOrEmpty(builder.timezone) ? "Etc/UTC" : builder.timezone;
  }

  public static ParserBuilder builder() {
    return new ParserBuilder();
  }

  /**
   * Parses a SIP string into a POJO message.
   *
   * @param message the POJO message.
   * @return the SIP string.
   */
  public Message<Object> parseMessage(String message) {
    log.debug("Message to parse: {}", message);

    // Try to get the command first so it can be used in error detection
    final Command command = parseCommandIdentifier(message);

    final ErrorDetection ed = validateChecksum(message, command);
    if (ed.valid) {
      if (ed.sequenceNumber != null) {
        // Remove the error detection chars before parsing the message
        message = message.substring(0, message.length() - (command == REQUEST_ACS_RESEND ? 6 : 9));
      }

      // Remove the command identifier before parsing
      message = message.substring(2);
      final MessageBuilder<Object> builder =  Message.builder()
          .command(command)
          .sequenceNumber(ed.sequenceNumber)
          .valid(ed.valid)
          .checksumString(ed.checksum);

      switch (command) {
        case PATRON_STATUS_REQUEST:
          PatronStatusRequestMessageParser messageParser =
              new PatronStatusRequestMessageParser(delimiter);
          messageParser.setTimezone(timezone);
          final PatronStatusRequest patronStatusRequest = messageParser.parse(message);
          builder.request(patronStatusRequest);
          break;
        case CHECKOUT:
          CheckoutMessageParser checkoutMessageParser = new CheckoutMessageParser(delimiter);
          checkoutMessageParser.setTimezone(timezone);
          final Checkout checkout = checkoutMessageParser.parse(message);
          builder.request(checkout);
          break;
        case CHECKIN:
          CheckinMessageParser checkinMessageParser = new CheckinMessageParser(delimiter);
          checkinMessageParser.setTimezone(timezone);
          final Checkin checkin = checkinMessageParser.parse(message);
          builder.request(checkin);
          break;
        case BLOCK_PATRON:
          BlockPatronMessageParser blockPatronMessageParser =
              new BlockPatronMessageParser(delimiter);
          blockPatronMessageParser.setTimezone(timezone);
          final BlockPatron blockPatron = blockPatronMessageParser.parse(message);
          builder.request(blockPatron);
          break;
        case SC_STATUS:
          SCStatusMessageParser scStatusMessageParser =
              new SCStatusMessageParser(delimiter);
          scStatusMessageParser.setTimezone(timezone);
          final SCStatus scStatus = scStatusMessageParser.parse(message);
          builder.request(scStatus);
          break;
        case REQUEST_ACS_RESEND:
          RequestACSResendMessageParser requestAcsResendMsgParser =
              new RequestACSResendMessageParser(delimiter);
          requestAcsResendMsgParser.setTimezone(timezone);
          final RequestACSResend requestACSResend = requestAcsResendMsgParser.parse(message);
          builder.request(requestACSResend);
          break;
        case LOGIN:
          LoginMessageParser loginMessageParser = new LoginMessageParser(delimiter);
          loginMessageParser.setTimezone(timezone);
          final Login login = loginMessageParser.parse(message);
          builder.request(login);
          break;
        case PATRON_INFORMATION:
          PatronInformationMessageParser patronInformationMessageParser =
              new PatronInformationMessageParser(delimiter);
          patronInformationMessageParser.setTimezone(timezone);
          final PatronInformation patronInformation = patronInformationMessageParser.parse(message);
          builder.request(patronInformation);
          break;
        case END_PATRON_SESSION:
          EndPatronSessionMessageParser endPatronSessionMessageParser =
              new EndPatronSessionMessageParser(delimiter);
          endPatronSessionMessageParser.setTimezone(timezone);
          final EndPatronSession endPatronSession = endPatronSessionMessageParser.parse(message);
          builder.request(endPatronSession);
          break;
        case FEE_PAID:
          FeePaidMessageParser feePaidMessageParser = new FeePaidMessageParser(delimiter);
          feePaidMessageParser.setTimezone(timezone);
          final FeePaid feePaid = feePaidMessageParser.parse(message);
          builder.request(feePaid);
          break;
        case ITEM_INFORMATION:
          ItemInformationMessageParser itemInformationMessageParser =
              new ItemInformationMessageParser(delimiter);
          itemInformationMessageParser.setTimezone(timezone);
          final ItemInformation itemInformation = itemInformationMessageParser.parse(message);
          builder.request(itemInformation);
          break;
        case ITEM_STATUS_UPDATE:
          ItemStatusUpdateMessageParser itemStatusUpdateMessageParser =
            new ItemStatusUpdateMessageParser(delimiter);
          itemStatusUpdateMessageParser.setTimezone(timezone);
          final ItemStatusUpdate itemStatusUpdate = itemStatusUpdateMessageParser.parse(message);
          builder.request(itemStatusUpdate);
          break;
        case PATRON_ENABLE:
          final PatronEnableMessageParser patronEnableMessageParser =
            new PatronEnableMessageParser(delimiter);
          patronEnableMessageParser.setTimezone(timezone);
          final PatronEnable patronEnable = patronEnableMessageParser.parse(message);
          builder.request(patronEnable);
          break;
        case HOLD:
          final HoldMessageParser holdMessageParser = new HoldMessageParser(delimiter);
          holdMessageParser.setTimezone(timezone);
          final Hold hold = holdMessageParser.parse(message);
          builder.request(hold);
          break;
        case RENEW:
          final RenewMessageParser renewMessageParser = new RenewMessageParser(delimiter);
          renewMessageParser.setTimezone(timezone);
          final Renew renew = renewMessageParser.parse(message);
          builder.request(renew);
          break;
        case RENEW_ALL:
          final RenewAllMessageParser renewAllMessageParser = new RenewAllMessageParser(delimiter);
          renewAllMessageParser.setTimezone(timezone);
          final RenewAll renewAll = renewAllMessageParser.parse(message);
          builder.request(renewAll);
          break;
        default:
          log.info("Command not supported: {}", command);
          builder.valid(false);
      }
      builder.timeZone(this.timezone);
      return builder.build();
    } else {
      return Message.builder()
        .valid(false)
        .checksumString(ed.checksum)
        .sequenceNumber(ed.sequenceNumber)
        .build();
    }
  }

  private ErrorDetection validateChecksum(String message, Command command) {
    final ErrorDetection ed = new ErrorDetection();

    if (errorDetectionEnabled) {
      // 8 is the minimum message length with SIP2 error detection enabled
      // The Request ACS Resend will not include a sequence number, but will
      // include a checksum.
      // 11 is the minimum message length for all the other SIP2 messages.
      // 2 char command code
      // 2 char sequence number code (non-Request ACS Resend messages)
      // 1 char sequence number (non-Request ACS Resend messages)
      // 2 char checksum code
      // 4 char checksum
      final int len = message.length();

      final int minLen = command == REQUEST_ACS_RESEND ? 8 : 11;
      if (len >= minLen && ((command != REQUEST_ACS_RESEND && message.charAt(len - 9) == 'A'
          && message.charAt(len - 8) == 'Y') || command == REQUEST_ACS_RESEND)
          && message.charAt(len - 6) == 'A'
          && message.charAt(len - 5) == 'Z') {
        final Integer sequenceNumber;
        if (command != REQUEST_ACS_RESEND) {
          final char sequenceChar = message.charAt(len - 7);
          sequenceNumber = Integer.valueOf(Character.getNumericValue(sequenceChar));
          if (sequenceNumber.intValue() < 0) {
            log.error("Sequence number is not 0-9: {}", sequenceChar);
            ed.valid = false;
            return ed;
          }
        } else {
          sequenceNumber = null;
        }

        // To validate the message, we total the byte values of each character
        // in the message including the checksum identifier, then we add the
        // checksum hex value. If the message is valid, the result will be 0.
        final byte [] bytes = message.substring(0, len - 4).getBytes(charset);

        int value = 0;
        for (byte b : bytes) {
          value += b & 0xff;
        }

        final String checksumString = message.substring(len - 4);
        final int checksum = Integer.parseUnsignedInt(checksumString, 16);

        value += checksum;
        value &= 0xffff;

        ed.valid = value == 0;
        ed.sequenceNumber = sequenceNumber;
        ed.checksum = checksumString;
      } else {
        // SC did not send error detection or something is really messed up
        log.error("Error detection enabled: SC did not send error detection");
        ed.valid = false;
      }
    } else {
      // Error detection is not enabled
      ed.valid = true;
    }

    return ed;
  }

  private Command parseCommandIdentifier(String message) {
    String commandString = message.substring(0, 2);
    Command command = Command.find(commandString);
    log.debug("Found command: {}", command);
    return command;
  }

  private class ErrorDetection {
    boolean valid;
    Integer sequenceNumber;
    String checksum;
  }

  public static class ParserBuilder {
    private Charset charset;
    private Character delimiter;
    private Boolean errorDetectionEnabled;
    private String timezone;

    private ParserBuilder() {
      super();
    }

    public ParserBuilder charset(Charset charset) {
      this.charset = charset;
      return this;
    }

    public ParserBuilder delimiter(Character delimiter) {
      this.delimiter = delimiter;
      return this;
    }

    public ParserBuilder errorDetectionEnaled(Boolean errorDetectionEnabled) {
      this.errorDetectionEnabled = errorDetectionEnabled;
      return this;
    }

    public ParserBuilder timeZone(String timezone) {
      this.timezone = timezone;
      return this;
    }

    public Parser build() {
      return new Parser(this);
    }
  }
}
