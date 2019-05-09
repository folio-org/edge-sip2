package org.folio.edge.sip2;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

public class ClockTest {

  @Test
  public void test() {
    OffsetDateTime dt = OffsetDateTime.parse("2019-05-08T10:15:30+01:00");
    System.out.println(dt);

    OffsetDateTime dt2 = OffsetDateTime.parse(dt.toString());
    System.out.println(dt2);

    OffsetDateTime dt3 = OffsetDateTime.now(Clock.systemUTC());
    System.out.println(dt3);

    System.out.println(ZoneOffset.getAvailableZoneIds());

    System.out.println(OffsetDateTime.now());

  }
}
