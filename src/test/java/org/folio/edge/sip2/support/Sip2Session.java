package org.folio.edge.sip2.support;

import groovy.util.logging.Log4j2;
import java.io.IOException;
import java.net.Socket;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.folio.edge.sip2.support.model.Sip2Command;
import org.folio.edge.sip2.support.model.Sip2CommandResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Log4j2
public class Sip2Session implements AutoCloseable {

  private static final Logger log = LoggerFactory.getLogger(Sip2Session.class);
  public static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

  private final AtomicInteger messageSequenceCounter = new AtomicInteger(1);
  private final String connectionId = "sip2-conn-" + UUID.randomUUID();
  private final Sip2SessionConfiguration config;

  private Socket sip2Socket;
  private Exception connectionException;

  /**
   * Constructs a new Sip2Session with the specified configuration.
   * Initializes the SIP2 socket connection immediately.
   *
   * @param configuration the session configuration containing connection parameters
   */
  public Sip2Session(Sip2SessionConfiguration configuration) {
    this.config = configuration;
    initSocket();
  }

  /**
   * Initializes the SIP2 socket connection based on the session configuration.
   * Handles both plain and SSL/TLS connections, sets socket options, and performs
   * server capability detection. If the connection fails, the exception is stored
   * for later retrieval.
   */
  public void initSocket() {
    log.debug("Initializing new connection: {}", connectionId);
    var hostname = config.getHostname();
    var port = config.getPort();
    try {
      log.debug("Creating socket connection: {}:{}", hostname, port);
      var socket = new Socket(hostname, port);
      socket.setSoTimeout((int) config.getSocketTimeout().toMillis());
      socket.setTcpNoDelay(true);
      socket.setKeepAlive(true);
      this.sip2Socket = socket;

      if (config.isUseSsl()) {
        log.debug("Initializing SSL context for connection {}:{}", hostname, port);
        var sslContext = SSLContext.getInstance("TLS");
        var trustAllCerts = getTrustManagerAllowingAllCerts();
        sslContext.init(null, trustAllCerts, new SecureRandom());
        var sslSocketFactory = sslContext.getSocketFactory();
        this.sip2Socket = sslSocketFactory.createSocket(socket, hostname, port, true);
        if (this.sip2Socket instanceof SSLSocket sslSocket) {
          sslSocket.setUseClientMode(true);
          sslSocket.startHandshake();
          log.debug("SSL handshake completed successfully: {}", connectionId);
        }
      } else {
        if (detectTlsServer(socket)) {
          close();
          log.debug("Plain connection attempt when server requires TLS: {}", connectionId);
          this.connectionException = new IllegalStateException(
              "Server requires SSL/TLS but client is configured for plain connection");
          return;
        }
      }

      log.debug("SIP2 Connection is open: {}", connectionId);
    } catch (Exception exception) {
      close();
      log.error("Failed to open socket: {}", connectionId, exception);
      this.connectionException = exception;
    }
  }

  /**
   * Closes the SIP2 session, releasing any associated resources.
   * If the socket is already closed, this method has no effect.
   */
  @Override
  public void close() {
    try {
      if (hasConnection()) {
        this.sip2Socket.close();
        log.debug("Connection closed: {}", connectionId);
      } else {
        log.debug("Socket already closed: {}", connectionId);
      }
    } catch (Exception e) {
      log.error("Error closing connection for thread: {}", connectionId, e);
    } finally {
      this.sip2Socket = null;
    }
  }

  /**
   * Checks if the SIP2 session has an active connection.
   *
   * @return - true if the connection is active, false otherwise
   */
  public boolean hasConnection() {
    return sip2Socket != null && !sip2Socket.isClosed();
  }

  /**
   * Executes the given SIP2 command by preparing the message, sending it over the socket,
   * and reading the response. The result of the command execution, including any errors,
   * is encapsulated in a {@link Sip2CommandResult} object.
   *
   * @param command - the SIP2 command to execute
   * @return - the result of the command execution
   */
  public Sip2CommandResult executeCommand(Sip2Command command) {
    if (!hasConnection()) {
      return Sip2CommandResult.builder()
          .isMessageSent(false)
          .isMessageReceived(false)
          .errorMessage("Connection is not available")
          .exception(this.connectionException)
          .build();
    }

    var commandMessage = prepareMessage(command);
    var resultBuilder = Sip2CommandResult.builder().requestMessage(commandMessage);
    try {
      resultBuilder = resultBuilder.messageSendStartTime(OffsetDateTime.now(UTC_ZONE_ID));
      sendCommand(commandMessage);
      resultBuilder = resultBuilder
          .isMessageSent(true)
          .messageSentEndTime(OffsetDateTime.now(UTC_ZONE_ID));
    } catch (IOException exception) {
      return resultBuilder
          .isMessageSent(false)
          .isMessageReceived(false)
          .messageSentEndTime(OffsetDateTime.now(UTC_ZONE_ID))
          .exception(exception)
          .build();
    }

    try {
      resultBuilder = resultBuilder.messageReadStartTime(OffsetDateTime.now(UTC_ZONE_ID));
      var responseMessage = readResponse();
      return buildSuccessResult(resultBuilder, responseMessage);
    } catch (IOException exception) {
      return Sip2CommandResult.builder()
          .isMessageReceived(true)
          .messageSentEndTime(OffsetDateTime.now(UTC_ZONE_ID))
          .exception(exception)
          .build();
    }
  }

  private Sip2CommandResult buildSuccessResult(
      Sip2CommandResult.Sip2CommandResultBuilder sip2Builder, String responseMessage) {
    sip2Builder = sip2Builder
        .responseMessage(responseMessage)
        .isMessageReceived(true)
        .messageSentEndTime(OffsetDateTime.now(UTC_ZONE_ID));

    try {
      sip2Builder = sip2Builder.isChecksumValid(validateChecksum(responseMessage));
    } catch (Exception exception) {
      sip2Builder = sip2Builder
          .isChecksumValid(false)
          .exception(exception)
          .errorMessage(exception.getMessage());
    }

    return sip2Builder.build();
  }

  private void sendCommand(String command) throws IOException {
    log.debug("Sending SIP2 Command ({}): '{}'", messageSequenceCounter.get() - 1, command);
    var resultMessage = command + config.getMessageDelimiter();
    sip2Socket.getOutputStream().write(resultMessage.getBytes(config.getCharset()));
    sip2Socket.getOutputStream().flush();
  }

  private String readResponse() throws IOException {
    log.debug("Reading message in session: {}", connectionId);
    var buffer = new byte[config.getReadBufferSize()];
    var bytesRead = sip2Socket.getInputStream().read(buffer);
    if (bytesRead > 0) {
      var responseMessage = new String(buffer, 0, bytesRead, config.getCharset());
      log.debug("SIP2 Response Message: '{}'", responseMessage);
      return responseMessage.trim();
    }

    return null;
  }

  private String prepareMessage(Sip2Command command) {
    var rawMessage = command.getMessage(config.getFieldDelimiter());
    if (!config.isErrorProtectionEnabled() || command.ignoreErrorDetection()) {
      return rawMessage;
    }

    var msgSeqNumber = messageSequenceCounter.getAndIncrement() % 10;
    var newMessage = rawMessage + "AY" + msgSeqNumber;
    return newMessage + "AZ" + calculateChecksum(newMessage + "AZ");
  }

  private String calculateChecksum(String message) {
    var bytes = message.getBytes(config.getCharset());
    var checksum = 0;
    for (final byte b : bytes) {
      checksum += b & 0xff;
    }
    checksum = -checksum & 0xffff;
    return String.format("%04X", checksum);
  }

  private boolean validateChecksum(String message) {
    if (!config.isErrorProtectionEnabled()) {
      // nothing to validate, error protection is disabled
      return true;
    }

    try {
      if (!message.contains("AZ")) {
        throw new IllegalArgumentException("Message does not contain checksum identifier 'AZ'");
      }

      int azIndex = message.lastIndexOf("AZ");
      if (azIndex < 0 || azIndex + 6 > message.length()) {
        throw new IllegalArgumentException("Invalid checksum format or message is too short");
      }

      var messageWithoutChecksum = message.substring(0, azIndex + 2);
      var receivedChecksum = message.substring(azIndex + 2, azIndex + 6);

      if (!receivedChecksum.matches("^[0-9A-Fa-f]{4}$")) {
        throw new IllegalArgumentException("Invalid checksum format: " + receivedChecksum);
      }

      byte[] bytes = messageWithoutChecksum.getBytes(config.getCharset());
      int calculatedSum = 0;
      for (byte b : bytes) {
        calculatedSum += b & 0xff;
      }

      calculatedSum += Integer.parseUnsignedInt(receivedChecksum, 16);
      calculatedSum &= 0xffff;

      boolean isValid = (calculatedSum == 0);
      if (!isValid) {
        throw new IllegalArgumentException(
            "Checksum validation failed. Sum: " + calculatedSum + ", Expected: 0");
      }

    } catch (Exception e) {
      throw new IllegalArgumentException("Exception during checksum validation", e);
    }

    return true;
  }

  private static TrustManager[] getTrustManagerAllowingAllCerts() {
    return new TrustManager[] {
        new X509TrustManager() {
          public X509Certificate[] getAcceptedIssuers() {
            return null;
          }

          public void checkClientTrusted(X509Certificate[] certs, String authType) {
            // no implementation, trust all certificates
          }

          public void checkServerTrusted(X509Certificate[] certs, String authType) {
            // no implementation, trust all certificates
          }
        }
    };
  }

  private boolean detectTlsServer(Socket socket) {
    try {
      var originalTimeout = socket.getSoTimeout();
      var testMessage = "9900402.00";
      socket.setSoTimeout(5000);
      sendCommand(testMessage);

      var buffer = new byte[1000];
      var bytesRead = socket.getInputStream().read(buffer);
      socket.setSoTimeout(originalTimeout);

      // If we get -1, connection was closed (likely TLS server rejecting plain text)
      if (bytesRead == -1) {
        log.debug("Connection closed by server after plain text - likely requires TLS");
        return true;
      }

      // If we get data back, it's likely a plain text server
      return false;
    } catch (Exception e) {
      var msg = e.getMessage();
      log.debug("TLS detection failed", e);
      return msg != null && (msg.contains("reset") || msg.contains("closed"));
    }
  }
}
