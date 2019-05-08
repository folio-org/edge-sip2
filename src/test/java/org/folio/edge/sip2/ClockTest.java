package org.folio.edge.sip2;

import java.time.Clock;
import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

public class ClockTest {

  @Test
  public void test() {
    OffsetDateTime dt = OffsetDateTime.parse("2019-05-08T10:15:30+01:00");
    System.out.println(dt);

    OffsetDateTime dt2 = OffsetDateTime.parse(dt.toString());
    System.out.println(dt2);

    
  }
}
