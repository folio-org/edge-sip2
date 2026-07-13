package org.folio.edge.sip2.support.model;

import static java.time.OffsetDateTime.now;
import static java.util.Locale.ROOT;
import static org.folio.edge.sip2.api.support.TestUtils.getFormattedLocalDateTime;
import static org.folio.edge.sip2.api.support.TestUtils.getUtcFixedClock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.folio.edge.sip2.domain.messages.enumerations.CurrencyType;
import org.folio.edge.sip2.domain.messages.enumerations.FeeType;
import org.folio.edge.sip2.domain.messages.enumerations.PaymentType;
import org.jspecify.annotations.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeePaidCommand implements Sip2Command {

  private Float feeAmount;
  private FeeType feeType;
  private PaymentType paymentType;
  private CurrencyType currencyType;
  private String institutionId;
  private String patronIdentifier;
  private String terminalPassword;
  private String patronPassword;
  private String feeIdentifier;
  private String transactionId;

  @Override
  public String getMessage(char fieldDelimiter) {
    var clock = getUtcFixedClock();
    return new Sip2MessageBuilder(37, fieldDelimiter)
        .withValue(getFormattedLocalDateTime(now(clock)))
        .withValue(getFeeTypeString())
        .withValue(getPaymentTypeString())
        .withValue(currencyType.name())
        .withFieldValue("BV", toFormatterCurrencyValue())
        .withFieldValue("CG", feeIdentifier, true)
        .withOptFieldValue("AO", institutionId, true)
        .withOptFieldValue("AA", patronIdentifier, true)
        .withOptFieldValue("AD", patronPassword, true)
        .withOptFieldValue("BK", transactionId, true)
        .build();
  }

  private @NonNull String toFormatterCurrencyValue() {
    return String.format(ROOT, "%.2f", feeAmount);
  }

  @SuppressWarnings("UnnecessaryDefault")
  private String getPaymentTypeString() {
    return switch (paymentType) {
      case CASH -> "00";
      case VISA -> "01";
      case CREDIT_CARD -> "02";
      default -> "99";
    };
  }

  @SuppressWarnings("UnnecessaryDefault")
  private String getFeeTypeString() {
    return switch (feeType) {
      case OTHER_UNKNOWN -> "01";
      case ADMINISTRATIVE -> "02";
      case DAMAGE -> "03";
      case OVERDUE -> "04";
      case PROCESSING -> "05";
      case RENTAL -> "06";
      case REPLACEMENT -> "07";
      case COMPUTER_ACCESS_CHARGE -> "08";
      case HOLD_FEE -> "09";
      default -> "00";
    };
  }
}
