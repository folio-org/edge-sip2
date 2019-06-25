package org.folio.edge.sip2.parser;

/**
 * A parsed SIP message.
 *
 * @author mreno-EBSCO
 *
 */
public class Message<R> {
  private final Command command;
  private final R request;
  private final boolean valid;
  private final Integer sequenceNumber;
  private final String checksumString;
  private final String timezone;

  /**
   * Constructor for a message.
   * @param builder the message builder.
   */
  public Message(MessageBuilder<R> builder) {
    this.command = builder.command;
    this.request = builder.request;
    this.valid = builder.valid;
    this.sequenceNumber = builder.sequenceNumber;
    this.checksumString = builder.checksumString;
    this.timezone = builder.timezone;
  }

  public static <R> MessageBuilder<R> builder() {
    return new MessageBuilder<>();
  }

  public Command getCommand() {
    return command;
  }

  public R getRequest() {
    return request;
  }

  public boolean isValid() {
    return valid;
  }

  public Integer getSequenceNumber() {
    return sequenceNumber;
  }

  public String getChecksumsString() {
    return checksumString;
  }

  public String getTimezone() {
    return timezone;
  }

  public static class MessageBuilder<R> {
    private Command command;
    private R request;
    private boolean valid;
    private Integer sequenceNumber;
    private String checksumString;
    private String timezone;

    private MessageBuilder() {
      super();
    }

    public MessageBuilder<R> command(Command command) {
      this.command = command;
      return this;
    }

    public MessageBuilder<R> request(R request) {
      this.request = request;
      return this;
    }

    public MessageBuilder<R> valid(boolean valid) {
      this.valid = valid;
      return this;
    }

    public MessageBuilder<R> sequenceNumber(Integer sequenceNumber) {
      this.sequenceNumber = sequenceNumber;
      return this;
    }

    public MessageBuilder<R> checksumString(String checksumString) {
      this.checksumString = checksumString;
      return this;
    }

    public MessageBuilder<R> timeZone(String timezone) {
      this.timezone = timezone;
      return this;
    }

    public Message<R> build() {
      return new Message<>(this);
    }
  }
}
