package org.folio.edge.sip2.support.model;

import static java.util.Objects.requireNonNullElse;

import com.networknt.schema.utils.StringUtils;

public class Sip2MessageBuilder {

  private final char fieldDelimiter;
  private final StringBuilder builder;

  /**
   * Constructor for building a SIP2 message.
   *
   * @param commandCode    - SIP2 command code
   * @param fieldDelimiter - delimiter character for fields
   */
  public Sip2MessageBuilder(int commandCode, char fieldDelimiter) {
    this.fieldDelimiter = fieldDelimiter;
    this.builder = new StringBuilder()
        .append(String.format("%02d", commandCode));
  }

  /**
   * Appends a message part if it is not null.
   *
   * @param messagePart - the message part to append
   * @return the current Sip2MessageBuilder instance for method chaining
   */
  public Sip2MessageBuilder withValue(Object messagePart) {
    if (messagePart == null) {
      return this;
    }

    if (messagePart instanceof Boolean booleanMsgPart) {
      this.builder.append(booleanMsgPart ? "Y" : "N");
      return this;
    }

    this.builder.append(messagePart);
    return this;
  }

  /**
   * Appends a field code and its value to the message if both are not blank.
   *
   * @param fieldCode  - the field code to append
   * @param fieldValue - the field value to append
   * @return the current Sip2MessageBuilder instance for method chaining
   */
  public Sip2MessageBuilder withFieldValue(String fieldCode, Object fieldValue) {
    return withFieldValue(fieldCode, fieldValue, false);
  }

  /**
   * Appends a field code and its value to the message with an optional delimiter.
   *
   * @param code              - field code
   * @param value             - field value
   * @param withLeadDelimiter - whether to prepend the field with a delimiter
   * @return the current Sip2MessageBuilder instance for method chaining
   */
  public Sip2MessageBuilder withFieldValue(String code, Object value, boolean withLeadDelimiter) {
    if (withLeadDelimiter) {
      this.builder.append(fieldDelimiter);
    }

    this.builder.append(requireNonNullElse(code, ""));

    if (value instanceof Boolean boolValue) {
      this.builder.append(boolValue ? "Y" : "N");
      return this;
    }

    this.builder.append(requireNonNullElse(value, ""));
    return this;
  }

  /**
   * Appends a field code and its value to the message with an optional delimiter.
   *
   * @param code  - field code
   * @param value - field value
   * @return the current Sip2MessageBuilder instance for method chaining
   */
  public Sip2MessageBuilder withOptFieldValue(String code, Object value) {
    return withOptFieldValue(code, value, false);
  }

  /**
   * Appends a field code and its value to the message with an optional delimiter.
   *
   * @param code          - field code
   * @param value         - field value
   * @param withDelimiter - whether to prepend the field with a delimiter
   * @return the current Sip2MessageBuilder instance for method chaining
   */
  public Sip2MessageBuilder withOptFieldValue(String code, Object value, boolean withDelimiter) {
    if (StringUtils.isBlank(code) || value == null) {
      return this;
    }

    if (withDelimiter) {
      this.builder.append(fieldDelimiter);
    }

    return withFieldValue(code, value);
  }

  /**
   * Builds and returns the final SIP2 message string.
   *
   * @return String - the constructed SIP2 message
   */
  public String build() {
    return this.builder
        .append(fieldDelimiter)
        .toString();
  }
}
