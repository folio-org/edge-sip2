package org.folio.edge.sip2.parser;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.folio.edge.sip2.domain.messages.enumerations.CurrencyType.USD;
import static org.folio.edge.sip2.domain.messages.enumerations.FeeType.DAMAGE;
import static org.folio.edge.sip2.domain.messages.enumerations.HoldMode.ADD;
import static org.folio.edge.sip2.domain.messages.enumerations.HoldType.SPECIFIC_COPY_TITLE;
import static org.folio.edge.sip2.domain.messages.enumerations.Language.ENGLISH;
import static org.folio.edge.sip2.domain.messages.enumerations.PaymentType.CASH;
import static org.folio.edge.sip2.domain.messages.enumerations.StatusCode.SC_OK;
import static org.folio.edge.sip2.domain.messages.enumerations.Summary.HOLD_ITEMS;
import static org.folio.edge.sip2.parser.Command.BLOCK_PATRON;
import static org.folio.edge.sip2.parser.Command.CHECKIN;
import static org.folio.edge.sip2.parser.Command.CHECKOUT;
import static org.folio.edge.sip2.parser.Command.END_PATRON_SESSION;
import static org.folio.edge.sip2.parser.Command.FEE_PAID;
import static org.folio.edge.sip2.parser.Command.HOLD;
import static org.folio.edge.sip2.parser.Command.ITEM_INFORMATION;
import static org.folio.edge.sip2.parser.Command.ITEM_STATUS_UPDATE;
import static org.folio.edge.sip2.parser.Command.LOGIN;
import static org.folio.edge.sip2.parser.Command.PATRON_ENABLE;
import static org.folio.edge.sip2.parser.Command.PATRON_INFORMATION;
import static org.folio.edge.sip2.parser.Command.PATRON_STATUS_REQUEST;
import static org.folio.edge.sip2.parser.Command.RENEW;
import static org.folio.edge.sip2.parser.Command.RENEW_ALL;
import static org.folio.edge.sip2.parser.Command.REQUEST_ACS_RESEND;
import static org.folio.edge.sip2.parser.Command.SC_STATUS;
import static org.folio.edge.sip2.parser.Field.CN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.Charset;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import org.folio.edge.sip2.domain.messages.enumerations.PWDAlgorithm;
import org.folio.edge.sip2.domain.messages.enumerations.UIDAlgorithm;
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
import org.folio.edge.sip2.parser.exceptions.MissingDelimiterException;
import org.junit.jupiter.api.Test;

class ParserTests {
  @Test  void testParserWithAlternateDelimiter() {
    final Parser parser = Parser.builder()
        .delimiter(Character.valueOf('^'))
        .build();
    final Message<?> message = parser.parseMessage(
        "9300CNuser_id^COpassw0rd^");

    assertEquals(LOGIN, message.getCommand());
    assertTrue(message.getRequest() instanceof Login);

    final Login login = (Login) message.getRequest();

    assertEquals(UIDAlgorithm.NO_ENCRYPTION, login.getUIDAlgorithm());
    assertEquals(PWDAlgorithm.NO_ENCRYPTION, login.getPWDAlgorithm());
    assertEquals("user_id", login.getLoginUserId());
    assertEquals("passw0rd", login.getLoginPassword());
    assertNull(login.getLocationCode());
  }

  @Test
  void testParserWithUnknownDelimiterThrowsExpectedException() {
    final Exception exception = assertThrows(
        MissingDelimiterException.class, () -> {
          final Parser parser = Parser.builder()
              .delimiter(Character.valueOf('^'))
              .build();
          parser.parseMessage("9300CNuser_id|COpassw0rd|");
        });
    assertEquals("Field does not contain a valid delimiter: " + CN,
        exception.getMessage());
  }

  @Test
  void testLoginParsingWithErrorDetection() {
    final Parser parser = Parser.builder().errorDetectionEnaled(TRUE).build();
    final Message<?> message = parser.parseMessage(
        "9300CNuser_id|COpassw0rd|AY1AZF594");

    assertEquals(LOGIN, message.getCommand());
    assertTrue(message.getRequest() instanceof Login);

    final Login login = (Login) message.getRequest();

    assertEquals(UIDAlgorithm.NO_ENCRYPTION, login.getUIDAlgorithm());
    assertEquals(PWDAlgorithm.NO_ENCRYPTION, login.getPWDAlgorithm());
    assertEquals("user_id", login.getLoginUserId());
    assertEquals("passw0rd", login.getLoginPassword());
    assertNull(login.getLocationCode());
  }

  @Test
  void testLoginParsingWithErrorDetectionAndBadChecksum() {
    final Parser parser = Parser.builder().errorDetectionEnaled(TRUE).build();
    final Message<?> message = parser.parseMessage(
        "9300CNuser_id|COpassw0rd|AY1AZF595");

    assertFalse(message.isValid());
    assertNotNull(message.getChecksumsString());
    assertEquals(1, message.getSequenceNumber());
  }

  @Test
  void testLoginParsingWithoutErrorDetection() {
    final Parser parser = Parser.builder().build();
    final Message<?> message = parser.parseMessage(
        "9300CNuser_id|COpassw0rd|CPcirc_desk|");

    assertEquals(LOGIN, message.getCommand());
    assertTrue(message.getRequest() instanceof Login);

    final Login login = (Login) message.getRequest();

    assertEquals(UIDAlgorithm.NO_ENCRYPTION, login.getUIDAlgorithm());
    assertEquals(PWDAlgorithm.NO_ENCRYPTION, login.getPWDAlgorithm());
    assertEquals("user_id", login.getLoginUserId());
    assertEquals("passw0rd", login.getLoginPassword());
    assertEquals("circ_desk", login.getLocationCode());
  }

  @Test
  void testPatronStatusRequestParsingWithoutErrorDetection() {
    final Parser parser = Parser.builder().build();
    final ZonedDateTime now =
        ZonedDateTime.now().truncatedTo(SECONDS);
    final String date = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss")
        .format(now);
    final Message<?> message = parser.parseMessage(
        "23001" + date + "AApatron_id|AC|AD1234|AOuniversity_id|");

    assertEquals(PATRON_STATUS_REQUEST, message.getCommand());
    assertTrue(message.getRequest() instanceof PatronStatusRequest);

    final PatronStatusRequest patronStatusRequest =
        (PatronStatusRequest) message.getRequest();

    assertEquals(ENGLISH, patronStatusRequest.getLanguage());
    assertEquals(now.getOffset(),
        patronStatusRequest.getTransactionDate().getOffset());
    assertEquals("patron_id", patronStatusRequest.getPatronIdentifier());
    assertEquals("1234", patronStatusRequest.getPatronPassword());
    assertEquals("university_id", patronStatusRequest.getInstitutionId());
    assertEquals("", patronStatusRequest.getTerminalPassword());
  }

  @Test
  void testCheckoutParsingWithoutErrorDetection() {
    final Parser parser = Parser.builder().build();
    final ZonedDateTime transactionDate =
        ZonedDateTime.now().truncatedTo(SECONDS);
    final ZonedDateTime nbDueDate = transactionDate.plusDays(30);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    final String transactionDateString = formatter.format(transactionDate);
    final String nbDueDateString = formatter.format(nbDueDate);
    final Message<?> message = parser.parseMessage(
        "11YY" + transactionDateString + nbDueDateString
        + "AApatron_id|ABSomeBook|AC|CHAutographed"
        + "|AD1234|AOuniversity_id|BON|BIN|");

    assertEquals(CHECKOUT, message.getCommand());
    assertTrue(message.getRequest() instanceof Checkout);

    final Checkout checkout = (Checkout) message.getRequest();

    assertEquals(TRUE, checkout.getScRenewalPolicy());
    assertEquals(TRUE, checkout.getNoBlock());
    assertEquals(transactionDate.getOffset(),
        checkout.getTransactionDate().getOffset());
    assertEquals(nbDueDate.getOffset(),
        checkout.getNbDueDate().getOffset());
    assertEquals("university_id", checkout.getInstitutionId());
    assertEquals("patron_id", checkout.getPatronIdentifier());
    assertEquals("SomeBook", checkout.getItemIdentifier());
    assertEquals("", checkout.getTerminalPassword());
    assertEquals("Autographed", checkout.getItemProperties());
    assertEquals("1234", checkout.getPatronPassword());
    assertEquals(FALSE, checkout.getFeeAcknowledged());
    assertEquals(FALSE, checkout.getCancel());
  }

  @Test
  void testCheckinParsingWithoutErrorDetection() {
    final Parser parser = Parser.builder().build();
    final ZonedDateTime transactionDate =
        ZonedDateTime.now().truncatedTo(SECONDS);
    final ZonedDateTime returnDate = transactionDate.plusMinutes(5);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    final String transactionDateString = formatter.format(transactionDate);
    final String returnDateString = formatter.format(returnDate);
    final Message<?> message = parser.parseMessage(
        "09Y" + transactionDateString + returnDateString
        + "APcirc_desk|ABSomeBook|AC|CHAutographed|"
        + "AOuniversity_id|BIN|");

    assertEquals(CHECKIN, message.getCommand());
    assertTrue(message.getRequest() instanceof Checkin);

    final Checkin checkin = (Checkin) message.getRequest();

    assertEquals(TRUE, checkin.getNoBlock());
    assertEquals(transactionDate.getOffset(),
        checkin.getTransactionDate().getOffset());
    assertEquals(returnDate.getOffset(),
        checkin.getReturnDate().getOffset());
    assertEquals("circ_desk", checkin.getCurrentLocation());
    assertEquals("university_id", checkin.getInstitutionId());
    assertEquals("SomeBook", checkin.getItemIdentifier());
    assertEquals("", checkin.getTerminalPassword());
    assertEquals("Autographed", checkin.getItemProperties());
    assertEquals(FALSE, checkin.getCancel());
  }

  @Test
  void testBlockPatronParsingWithoutErrorDetection() {
    final Parser parser = Parser.builder().build();
    final ZonedDateTime transactionDate =
        ZonedDateTime.now().truncatedTo(SECONDS);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    final String transactionDateString = formatter.format(transactionDate);
    final Message<?> message = parser.parseMessage(
        "01N" + transactionDateString
        + "ALCard retained due to excessive fee violations|"
        + "AApatron_id|AC|AOuniversity_id|");


    assertEquals(BLOCK_PATRON, message.getCommand());
    assertTrue(message.getRequest() instanceof BlockPatron);

    final BlockPatron blockPatron = (BlockPatron) message.getRequest();

    assertEquals(FALSE, blockPatron.getCardRetained());
    assertEquals(transactionDate.getOffset(),
        blockPatron.getTransactionDate().getOffset());
    assertEquals("university_id", blockPatron.getInstitutionId());
    assertEquals("Card retained due to excessive fee violations",
        blockPatron.getBlockedCardMsg());
    assertEquals("patron_id", blockPatron.getPatronIdentifier());
    assertEquals("", blockPatron.getTerminalPassword());
  }

  @Test
  void testSCStatusParsingWithoutErrorDetection() {
    final Parser parser = Parser.builder().build();
    final Message<?> message = parser.parseMessage("9901202.00");

    assertEquals(SC_STATUS, message.getCommand());
    assertTrue(message.getRequest() instanceof SCStatus);

    final SCStatus scStatus = (SCStatus) message.getRequest();

    assertEquals(SC_OK, scStatus.getStatusCode());
    assertEquals(Integer.valueOf(120), scStatus.getMaxPrintWidth());
    assertEquals("2.00", scStatus.getProtocolVersion());
  }

  @Test
  void testRequestACSResendParsingWithoutErrorDetection() {
    final Parser parser = Parser.builder().build();
    final Message<?> message = parser.parseMessage("97");

    assertEquals(REQUEST_ACS_RESEND, message.getCommand());
    assertTrue(message.getRequest() instanceof RequestACSResend);

    final RequestACSResend requestACSResend =
        (RequestACSResend) message.getRequest();

    assertNotNull(requestACSResend);
  }

  @Test
  void testPatronInformationParsingWithoutErrorDetection() {
    final Parser parser = Parser.builder().build();
    final ZonedDateTime transactionDate =
        ZonedDateTime.now().truncatedTo(SECONDS);
    final String transactionDateString = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss")
        .format(transactionDate);
    final Message<?> message = parser.parseMessage(
        "63001" + transactionDateString + "Y         "
        + "AApatron_id|AD1234|AC|"
        + "AOuniversity_id|BP1|BQ10|");

    assertEquals(PATRON_INFORMATION, message.getCommand());
    assertTrue(message.getRequest() instanceof PatronInformation);

    final PatronInformation patronInformation =
        (PatronInformation) message.getRequest();

    assertEquals(ENGLISH, patronInformation.getLanguage());
    assertEquals(transactionDate.getOffset(),
        patronInformation.getTransactionDate().getOffset());
    assertEquals(HOLD_ITEMS, patronInformation.getSummary());
    assertEquals("university_id", patronInformation.getInstitutionId());
    assertEquals("patron_id", patronInformation.getPatronIdentifier());
    assertEquals("", patronInformation.getTerminalPassword());
    assertEquals("1234", patronInformation.getPatronPassword());
    assertEquals(Integer.valueOf(1), patronInformation.getStartItem());
    assertEquals(Integer.valueOf(10), patronInformation.getEndItem());
  }

  @Test
  void testEndPatronSessionParsingWithoutErrorDetection() {
    final Parser parser = Parser.builder().build();
    final ZonedDateTime transactionDate =
        ZonedDateTime.now().truncatedTo(SECONDS);
    final String transactionDateString = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss")
        .format(transactionDate);
    final Message<?> message = parser.parseMessage(
        "35" + transactionDateString
        + "AApatron_id|AD1234|AC|AOuniversity_id|");

    assertEquals(END_PATRON_SESSION, message.getCommand());
    assertTrue(message.getRequest() instanceof EndPatronSession);

    final EndPatronSession endPatronSession =
        (EndPatronSession) message.getRequest();

    assertEquals(transactionDate.getOffset(),
        endPatronSession.getTransactionDate().getOffset());
    assertEquals("university_id", endPatronSession.getInstitutionId());
    assertEquals("patron_id", endPatronSession.getPatronIdentifier());
    assertEquals("", endPatronSession.getTerminalPassword());
    assertEquals("1234", endPatronSession.getPatronPassword());
  }

  @Test
  void testFeePaidParsingWithoutErrorDetection() {
    final Parser parser = Parser.builder().build();
    final ZonedDateTime transactionDate =
        ZonedDateTime.now().truncatedTo(SECONDS);
    final String transactionDateString = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss")
        .format(transactionDate);
    final Message<?> message = parser.parseMessage(
        "37" + transactionDateString + "0300USD"
        + "BV100.25|AApatron_id|AD1234|AC|"
        + "AOuniversity_id|CGTorn page|BKa1b2c3d4e5|");

    assertEquals(FEE_PAID, message.getCommand());
    assertTrue(message.getRequest() instanceof FeePaid);

    final FeePaid feePaid = (FeePaid) message.getRequest();

    assertEquals(transactionDate.getOffset(),
        feePaid.getTransactionDate().getOffset());
    assertEquals(DAMAGE, feePaid.getFeeType());
    assertEquals(CASH, feePaid.getPaymentType());
    assertEquals(USD, feePaid.getCurrencyType());
    assertEquals("100.25", feePaid.getFeeAmount());
    assertEquals("university_id", feePaid.getInstitutionId());
    assertEquals("patron_id", feePaid.getPatronIdentifier());
    assertEquals("", feePaid.getTerminalPassword());
    assertEquals("1234", feePaid.getPatronPassword());
    assertEquals("Torn page", feePaid.getFeeIdentifier());
    assertEquals("a1b2c3d4e5", feePaid.getTransactionId());
  }

  @Test
  void testItemInformationParsingWithoutErrorDetection() {
    final Parser parser = Parser.builder().build();
    final ZonedDateTime transactionDate =
        ZonedDateTime.now().truncatedTo(SECONDS);
    final String transactionDateString = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss")
        .format(transactionDate);
    final Message<?> message = parser.parseMessage(
        "17" + transactionDateString + "ABSomeBook|AOuniversity_id|");

    assertEquals(ITEM_INFORMATION, message.getCommand());
    assertTrue(message.getRequest() instanceof ItemInformation);

    final ItemInformation itemInformation =
        (ItemInformation) message.getRequest();

    assertEquals(transactionDate.getOffset(),
        itemInformation.getTransactionDate().getOffset());
    assertEquals("university_id", itemInformation.getInstitutionId());
    assertEquals("SomeBook", itemInformation.getItemIdentifier());
    assertNull(itemInformation.getTerminalPassword());
  }

  @Test
  void testItemStatusUpdateParsingWithoutErrorDetection() {
    final Parser parser = Parser.builder().build();
    final ZonedDateTime transactionDate =
        ZonedDateTime.now().truncatedTo(SECONDS);
    final String transactionDateString = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss")
        .format(transactionDate);
    final Message<?> message = parser.parseMessage(
        "19" + transactionDateString
        + "ABSomeBook|AOuniversity_id|CHSpilled coffee on the book|");

    assertEquals(ITEM_STATUS_UPDATE, message.getCommand());
    assertTrue(message.getRequest() instanceof ItemStatusUpdate);

    final ItemStatusUpdate itemStatusUpdate =
        (ItemStatusUpdate) message.getRequest();

    assertEquals(transactionDate.getOffset(),
        itemStatusUpdate.getTransactionDate().getOffset());
    assertEquals("university_id", itemStatusUpdate.getInstitutionId());
    assertEquals("SomeBook", itemStatusUpdate.getItemIdentifier());
    assertNull(itemStatusUpdate.getTerminalPassword());
    assertEquals("Spilled coffee on the book",
        itemStatusUpdate.getItemProperties());
  }

  @Test
  void testPatronEnableParsingWithoutErrorDetection() {
    final Parser parser = Parser.builder().build();
    final ZonedDateTime transactionDate =
        ZonedDateTime.now().truncatedTo(SECONDS);
    final String transactionDateString = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss")
        .format(transactionDate);
    final Message<?> message = parser.parseMessage(
        "25" + transactionDateString
        + "AApatron_id|AD1234|AC|AOuniversity_id|");

    assertEquals(PATRON_ENABLE, message.getCommand());
    assertTrue(message.getRequest() instanceof PatronEnable);

    final PatronEnable patronEnable = (PatronEnable) message.getRequest();

    assertEquals(transactionDate.getOffset(),
        patronEnable.getTransactionDate().getOffset());
    assertEquals("university_id", patronEnable.getInstitutionId());
    assertEquals("patron_id", patronEnable.getPatronIdentifier());
    assertEquals("", patronEnable.getTerminalPassword());
    assertEquals("1234", patronEnable.getPatronPassword());
  }

  @Test
  void testHoldParsingWithoutErrorDetection() {
    final Parser parser = Parser.builder().build();
    final ZonedDateTime transactionDate =
        ZonedDateTime.now().truncatedTo(SECONDS);
    final ZonedDateTime expirationDate = transactionDate.plusDays(30);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    final String transactionDateString = formatter.format(transactionDate);
    final String expirationDateString = formatter.format(expirationDate);

    final Message<?> message = parser.parseMessage(
        "15+" + transactionDateString + "BW" + expirationDateString
        + "|BScirc_desk|BY3|AApatron_id|AC|"
        + "AD1234|AOuniversity_id|ABSome Book|AJSome Title|BON|");


    assertEquals(HOLD, message.getCommand());
    assertTrue(message.getRequest() instanceof Hold);

    final Hold hold = (Hold) message.getRequest();

    assertEquals(ADD, hold.getHoldMode());
    assertEquals(transactionDate.getOffset(),
        hold.getTransactionDate().getOffset());
    assertEquals(expirationDate.getOffset(),
        hold.getExpirationDate().getOffset());
    assertEquals("circ_desk", hold.getPickupLocation());
    assertEquals(SPECIFIC_COPY_TITLE, hold.getHoldType());
    assertEquals("university_id", hold.getInstitutionId());
    assertEquals("patron_id", hold.getPatronIdentifier());
    assertEquals("1234", hold.getPatronPassword());
    assertEquals("Some Book", hold.getItemIdentifier());
    assertEquals("Some Title", hold.getTitleIdentifier());
    assertEquals("", hold.getTerminalPassword());
    assertEquals(FALSE, hold.getFeeAcknowledged());
  }

  @Test
  void testRenewParsingWithoutErrorDetection() {
    final Parser parser = Parser.builder().build();

    final ZonedDateTime transactionDate =
        ZonedDateTime.now().truncatedTo(SECONDS);
    final ZonedDateTime nbDueDate = transactionDate.plusDays(30);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    final String transactionDateString = formatter.format(transactionDate);
    final String nbDueDateString = formatter.format(nbDueDate);
    final Message<?> message = parser.parseMessage(
        "29YY" + transactionDateString + nbDueDateString
        + "AApatron_id|AC|AD1234|AOuniversity_id|ABSome Book|"
        + "AJSome Title|CHAutographed|BON|");

    assertEquals(RENEW, message.getCommand());
    assertTrue(message.getRequest() instanceof Renew);

    final Renew renew = (Renew) message.getRequest();

    assertEquals(TRUE, renew.getThirdPartyAllowed());
    assertEquals(TRUE, renew.getNoBlock());
    assertEquals(transactionDate.getOffset(),
        renew.getTransactionDate().getOffset());
    assertEquals(nbDueDate.getOffset(),
        renew.getNbDueDate().getOffset());
    assertEquals("university_id", renew.getInstitutionId());
    assertEquals("patron_id", renew.getPatronIdentifier());
    assertEquals("1234", renew.getPatronPassword());
    assertEquals("Some Book", renew.getItemIdentifier());
    assertEquals("Some Title", renew.getTitleIdentifier());
    assertEquals("", renew.getTerminalPassword());
    assertEquals("Autographed", renew.getItemProperties());
    assertEquals(FALSE, renew.getFeeAcknowledged());
  }

  @Test
  void testRenewAllParsingWithoutErrorDetection() {
    final Parser parser = Parser.builder().build();

    final ZonedDateTime transactionDate =
        ZonedDateTime.now().truncatedTo(SECONDS);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    final String transactionDateString = formatter.format(transactionDate);
    final Message<?> message = parser.parseMessage(
        "65" + transactionDateString
        + "AApatron_id|AC|AD1234|AOuniversity_id|BON|");

    assertEquals(RENEW_ALL, message.getCommand());
    assertTrue(message.getRequest() instanceof RenewAll);

    final RenewAll renewAll = (RenewAll) message.getRequest();

    assertEquals(transactionDate.getOffset(),
        renewAll.getTransactionDate().getOffset());
    assertEquals("university_id", renewAll.getInstitutionId());
    assertEquals("patron_id", renewAll.getPatronIdentifier());
    assertEquals("1234", renewAll.getPatronPassword());
    assertEquals("", renewAll.getTerminalPassword());
    assertEquals(FALSE, renewAll.getFeeAcknowledged());
  }

  @Test
  void testRenewAllParsingWithErrorDetection() {
    final Parser parser = Parser.builder().errorDetectionEnaled(TRUE).build();

    final ZonedDateTime transactionDate =
        ZonedDateTime.now().truncatedTo(SECONDS);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd    HHmmss");
    final String transactionDateString = formatter.format(transactionDate);
    final Message<?> message = parser.parseMessage(
        addErrorDetection("65" + transactionDateString
            + "AApatron_id|AC|AD1234|AOuniversity_id|BON|"));

    assertTrue(message.isValid());
    assertTrue(message.isErrorDetectionEnabled());
    assertEquals(RENEW_ALL, message.getCommand());
    assertTrue(message.getRequest() instanceof RenewAll);

    final RenewAll renewAll = (RenewAll) message.getRequest();

    assertEquals(transactionDate.getOffset(),
        renewAll.getTransactionDate().getOffset());
    assertEquals("university_id", renewAll.getInstitutionId());
    assertEquals("patron_id", renewAll.getPatronIdentifier());
    assertEquals("1234", renewAll.getPatronPassword());
    assertEquals("", renewAll.getTerminalPassword());
    assertEquals(FALSE, renewAll.getFeeAcknowledged());
  }

  @Test
  void testRenewAllParsingWithErrorDetectionAndKnownSequenceNumber() {
    final Parser parser = Parser.builder().errorDetectionEnaled(TRUE).build();

    final int sequenceNumber = 1;
    final ZonedDateTime transactionDate =
        ZonedDateTime.of(2019, 5, 1, 10, 30, 15, 0, ZoneOffset.UTC);
    final DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyyMMdd   'Z'HHmmss");
    final String transactionDateString = formatter.format(transactionDate);
    final Message<?> message = parser.parseMessage(
        addErrorDetection("65" + transactionDateString
            + "AApatron_id|AC|AD1234|AOuniversity_id|BON|", sequenceNumber));

    assertTrue(message.isValid());
    assertTrue(message.isErrorDetectionEnabled());
    assertEquals(1, message.getSequenceNumber());
    assertEquals("EB3B", message.getChecksumsString());
    assertEquals(RENEW_ALL, message.getCommand());
    assertTrue(message.getRequest() instanceof RenewAll);

    final RenewAll renewAll = (RenewAll) message.getRequest();

    assertEquals(transactionDate.getOffset(),
        renewAll.getTransactionDate().getOffset());
    assertEquals("university_id", renewAll.getInstitutionId());
    assertEquals("patron_id", renewAll.getPatronIdentifier());
    assertEquals("1234", renewAll.getPatronPassword());
    assertEquals("", renewAll.getTerminalPassword());
    assertEquals(FALSE, renewAll.getFeeAcknowledged());
  }

  private String addErrorDetection(String message) {
    return addErrorDetection(message, new Random().nextInt(10));
  }

  private String addErrorDetection(String message, int sequenceNumber) {
    final StringBuilder messageWithErrorDetection =
        new StringBuilder(message.length() + 9)
          .append(message)
          .append("AY")
          .append(sequenceNumber)
          .append("AZ");
    final byte [] bytes = messageWithErrorDetection.toString()
        .getBytes(Charset.forName("IBM850"));
    int checksum = 0;
    for (byte b : bytes) {
      checksum += b & 0xff;
    }
    checksum = -checksum & 0xffff; // 16 bit 2's compliment via negation
    final String checksumString = Integer.toHexString(checksum).toUpperCase();
    return messageWithErrorDetection.append(checksumString).toString();
  }
}
