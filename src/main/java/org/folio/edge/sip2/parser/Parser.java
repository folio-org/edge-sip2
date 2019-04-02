package org.folio.edge.sip2.parser;

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

  private final Charset charset;
  private final Character delimiter;

  private Parser(ParserBuilder builder) {
    if (builder.charset == null) {
      this.charset = DEFAULT_CHARSET;
    } else {
      this.charset = builder.charset;
    }
    if (builder.delimiter == null) {
      this.delimiter = DEFAULT_DELIMITER;
    } else {
      this.delimiter = builder.delimiter;
    }
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
    final ErrorDetection ed = validateChecksum(message);
    if (ed.valid) {
      if (ed.sequenceNumber != null) {
        // Remove the error detection chars before parsing the message
        message = message.substring(0, message.length() - 9);
      }

      final Command command = parseCommandIdentifier(message);
      // Remove the command identifier before parsing
      message = message.substring(2);
      final MessageBuilder<Object> builder =  Message.builder()
          .command(command)
          .sequenceNumber(ed.sequenceNumber)
          .valid(ed.valid)
          .checksumString(ed.checksum);

      switch (command) {
        case PATRON_STATUS_REQUEST:
          final PatronStatusRequest patronStatusRequest =
              new PatronStatusRequestMessageParser(delimiter).parse(message);
          builder.request(patronStatusRequest);
          break;
        case CHECKOUT:
          final Checkout checkout =
              new CheckoutMessageParser(delimiter).parse(message);
          builder.request(checkout);
          break;
        case CHECKIN:
          final Checkin checkin =
              new CheckinMessageParser(delimiter).parse(message);
          builder.request(checkin);
          break;
        case BLOCK_PATRON:
          final BlockPatron blockPatron =
              new BlockPatronMessageParser(delimiter).parse(message);
          builder.request(blockPatron);
          break;
        case SC_STATUS:
          final SCStatus scStatus =
              new SCStatusMessageParser(delimiter).parse(message);
          builder.request(scStatus);
          break;
        case REQUEST_ACS_RESEND:
          final RequestACSResend requestACSResend =
              new RequestACSResendMessageParser(delimiter).parse(message);
          builder.request(requestACSResend);
          break;
        case LOGIN:
          final Login login = new LoginMessageParser(delimiter).parse(message);
          builder.request(login);
          break;
        case PATRON_INFORMATION:
          final PatronInformation patronInformation =
              new PatronInformationMessageParser(delimiter).parse(message);
          builder.request(patronInformation);
          break;
        case END_PATRON_SESSION:
          final EndPatronSession endPatronSession =
              new EndPatronSessionMessageParser(delimiter).parse(message);
          builder.request(endPatronSession);
          break;
        case FEE_PAID:
          final FeePaid feePaid =
              new FeePaidMessageParser(delimiter).parse(message);
          builder.request(feePaid);
          break;
        case ITEM_INFORMATION:
          final ItemInformation itemInformation =
              new ItemInformationMessageParser(delimiter).parse(message);
          builder.request(itemInformation);
          break;
        case ITEM_STATUS_UPDATE:
          final ItemStatusUpdate itemStatusUpdate =
              new ItemStatusUpdateMessageParser(delimiter).parse(message);
          builder.request(itemStatusUpdate);
          break;
        case PATRON_ENABLE:
          final PatronEnable patronEnable =
              new PatronEnableMessageParser(delimiter).parse(message);
          builder.request(patronEnable);
          break;
        case HOLD:
          final Hold hold = new HoldMessageParser(delimiter).parse(message);
          builder.request(hold);
          break;
        case RENEW:
          final Renew renew = new RenewMessageParser(delimiter).parse(message);
          builder.request(renew);
          break;
        case RENEW_ALL:
          final RenewAll renewAll =
              new RenewAllMessageParser(delimiter).parse(message);
          builder.request(renewAll);
          break;
        default:
          log.info("Command not supported: {}", command);
          builder.valid(false);
      }

      return builder.build();
    } else {
      return Message.builder().valid(false).build();
    }
  }

  private ErrorDetection validateChecksum(String message) {
    ErrorDetection ed = new ErrorDetection();
    // 11 is the minimum length of a SIP2 message with error detection enabled
    // 2 char message code
    // 2 char sequence number code
    // 1 char sequence number
    // 2 char checksum code
    // 4 char checksum
    if (message.length() >= 11) {
      String tail = message.substring(message.length() - 9);
      if (tail.charAt(0) == 'A' && tail.charAt(1) == 'Y'
          && tail.charAt(3) == 'A' && tail.charAt(4) == 'Z') {
        log.debug("Error detection detected: {}", tail);

        char sequenceChar = tail.charAt(2);
        int sequenceNumber = Character.getNumericValue(sequenceChar);
        if (sequenceNumber < 0) {
          log.error("Sequence number is not 0-9: {}", sequenceChar);
          ed.valid = false;
          return ed;
        }

        // To validate the message, we total the byte values of each character
        // in the message including the checksum identifier, then we add the
        // checksum hex value. If the message is valid, the result will be 0.
        final byte [] bytes =
            message.substring(0, message.length() - 4).getBytes(charset);

        int value = 0;
        for (byte b : bytes) {
          value += b & 0xff;
        }

        final String checksumString = tail.substring(tail.length() - 4);
        final int checksum = Integer.parseUnsignedInt(checksumString, 16);

        value += checksum;
        value &= 0xffff;

        ed.valid = value == 0;
        ed.sequenceNumber = Integer.valueOf(sequenceNumber);
        ed.checksum = checksumString;
      } else {
        // No error detection
        log.debug("SC did not enable error detection");
        ed.valid = true;
      }
    } else {
      // No error detection
      log.debug("SC did not enable error detection");
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

    public Parser build() {
      return new Parser(this);
    }
  }
}
