package org.folio.edge.sip2.support.model;

import static java.time.OffsetDateTime.now;
import static org.folio.edge.sip2.api.support.TestUtils.getFormattedLocalDateTime;
import static org.folio.edge.sip2.api.support.TestUtils.getUtcFixedClock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.folio.edge.sip2.parser.LanguageMapper;

@Data
@Builder
@AllArgsConstructor
public class PatronInformationCommand implements Sip2Command {

  private final LanguageMapper languageCode;
  private final PatronInfoSummaryType summary;
  private final String institutionId;
  private final String patronIdentifier;
  private final String terminalPassword;
  private final String patronPassword;
  private final Integer startItem;
  private final Integer endItem;

  @Override
  public String getMessage(char fieldDelimiter) {
    return new Sip2MessageBuilder(63, fieldDelimiter)
        .withValue(languageCode.code()) // 3-char Language code
        .withValue(getFormattedLocalDateTime(now(getUtcFixedClock()))) // current date/time
        .withValue(getPatronInfoSummaryPart(summary)) // 1-char Summary type
        .withFieldValue("AO", institutionId) // institution id
        .withFieldValue("AA", patronIdentifier, true) // patron identifier
        .withOptFieldValue("AC", terminalPassword, true) // terminal password
        .withOptFieldValue("AD", patronPassword, true) // patron password / 'PIN' number
        .withOptFieldValue("BP", startItem, true) // Number of the first item to be sent to SC
        .withOptFieldValue("BQ", endItem, true) // Number of the last item to be sent to SC
        .build();
  }

  private static String getPatronInfoSummaryPart(PatronInfoSummaryType summary) {
    if (summary == null) {
      return " ".repeat(10);
    }

    int position = summary.getPosition();
    return " ".repeat(position) + "Y" + " ".repeat(9 - position);
  }

  @Getter
  @RequiredArgsConstructor
  public enum PatronInfoSummaryType {

    HOLD_ITEMS(0),
    OVERDUE_ITEMS(1),
    CHARGED_ITEMS(2),
    FINE_ITEMS(3),
    RECALL_ITEMS(4),
    UNAVAILABLE_HOLDS(5);

    private final int position;
  }
}
