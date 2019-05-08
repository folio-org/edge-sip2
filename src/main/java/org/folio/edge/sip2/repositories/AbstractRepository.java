package org.folio.edge.sip2.repositories;

import java.time.Clock;
import java.time.OffsetDateTime;

public class AbstractRepository {

  protected Clock clock;

  public AbstractRepository(Clock clock) {
    this.clock = clock;
  }

  protected Clock getClock() {
    if (clock == null) {
      return Clock.systemUTC();
    } else {
      return clock; //mostly for unit test cases
    }
  }

  protected OffsetDateTime getTransactionTimestamp(String timeZone) {
    return Utils.convertDateTime(OffsetDateTime.now(getClock()), timeZone);
  }
}
