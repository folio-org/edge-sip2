package org.folio.edge.sip2.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.micrometer.core.instrument.Timer;
import org.folio.edge.sip2.parser.Command;
import org.junit.jupiter.api.Test;

class MetricsTests {
  @Test
  void testGetMetrics() {
    final Metrics m = Metrics.getMetrics(1234);
    assertNotNull(m);
  }

  @Test
  void testSocketError() {
    final Metrics m = Metrics.getMetrics(1234);
    m.socketError();
    m.socketError();
    assertEquals(2, m.socketErrorCount());
  }

  @Test
  void testRequestError() {
    final Metrics m = Metrics.getMetrics(1234);
    m.requestError();
    m.requestError();
    m.requestError();
    assertEquals(3, m.requestErrorCount());
  }

  @Test
  void testResponseError() {
    final Metrics m = Metrics.getMetrics(1234);
    m.responseError();
    assertEquals(1, m.responseErrorCount());
  }

  @Test
  void testScResendError() {
    final Metrics m = Metrics.getMetrics(1234);
    m.scResendError();
    m.scResendError();
    m.scResendError();
    m.scResendError();
    m.scResendError();
    assertEquals(5, m.scResendErrorCount());
  }

  @Test
  void testInvalidMessageError() {
    final Metrics m = Metrics.getMetrics(1234);
    m.invalidMessageError();
    m.invalidMessageError();
    m.invalidMessageError();
    m.invalidMessageError();
    assertEquals(4, m.invalidMessageErrorCount());
  }

  @Test
  void testSample() {
    final Metrics m = Metrics.getMetrics(1234);
    final Timer.Sample sample = m.sample();
    assertNotNull(sample);
  }

  @Test
  void testCommandTimer() {
    final Metrics m = Metrics.getMetrics(1234);
    final Timer.Sample sample = m.sample();
    final Timer timer = m.commandTimer(Command.UNKNOWN);
    assertNotNull(timer);
    final long time = sample.stop(timer);
    assertTrue(time > 0L);
  }

  @Test
  void testStop() {
    final Metrics m = Metrics.getMetrics(1234);
    assertNotNull(m);
    m.stop();
  }
}
