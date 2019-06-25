package org.folio.edge.sip2.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.vertx.micrometer.backends.BackendRegistries;
import java.util.Optional;
import org.folio.edge.sip2.parser.Command;

public final class Metrics {
  private static final String METRICS_PREFIX = "org.folio.edge.sip2.";
  private static final String ERRORS_SUFFIX = ".errors";

  private static final String COUNTER_SOCKET_ERRORS = METRICS_PREFIX + "socket" + ERRORS_SUFFIX;
  private static final String COUNTER_REQUEST_ERRORS = METRICS_PREFIX + "request" + ERRORS_SUFFIX;
  private static final String COUNTER_RESPONSE_ERRORS = METRICS_PREFIX + "response" + ERRORS_SUFFIX;
  private static final String COUNTER_SC_RESEND_ERRORS =
      METRICS_PREFIX + "scResend" + ERRORS_SUFFIX;
  private static final String COUNTER_INVALID_MESSAGE_ERRORS =
      METRICS_PREFIX + "invalidMessage" + ERRORS_SUFFIX;

  private static final String SIP2_COMMAND_TAG = "command";
  private static final String SIP2_COMMAND_TIMER_NAME = METRICS_PREFIX + "command.timer";

  private final MeterRegistry registry = Optional.ofNullable(BackendRegistries.getDefaultNow())
      .orElse(new SimpleMeterRegistry());
  private final Counter socketErrorCounter;
  private final Counter requestErrorCounter;
  private final Counter responseErrorCounter;
  private final Counter scResendErrorCounter;
  private final Counter invalidMessageErrorCounter;
  private final JvmGcMetrics jvmGcMetrics;

  Metrics(int port) {
    socketErrorCounter = Counter.builder(COUNTER_SOCKET_ERRORS)
        .tag("port", Integer.toString(port))
        .register(registry);
    requestErrorCounter = Counter.builder(COUNTER_REQUEST_ERRORS)
        .tag("port", Integer.toString(port))
        .register(registry);
    responseErrorCounter = Counter.builder(COUNTER_RESPONSE_ERRORS)
        .tag("port", Integer.toString(port))
        .register(registry);
    scResendErrorCounter = Counter.builder(COUNTER_SC_RESEND_ERRORS)
        .tag("port", Integer.toString(port))
        .register(registry);
    invalidMessageErrorCounter = Counter.builder(COUNTER_INVALID_MESSAGE_ERRORS)
        .tag("port", Integer.toString(port))
        .register(registry);

    // Load JVM instrumentation
    new ClassLoaderMetrics().bindTo(registry);
    new JvmMemoryMetrics().bindTo(registry);
    jvmGcMetrics = new JvmGcMetrics();
    jvmGcMetrics.bindTo(registry);
    new ProcessorMetrics().bindTo(registry);
    new JvmThreadMetrics().bindTo(registry);
  }

  public static Metrics getMetrics(int port) {
    return new Metrics(port);
  }

  public void socketError() {
    socketErrorCounter.increment();
  }

  double socketErrorCount() {
    return socketErrorCounter.count();
  }

  public void requestError() {
    requestErrorCounter.increment();
  }

  double requestErrorCount() {
    return requestErrorCounter.count();
  }

  public void responseError() {
    responseErrorCounter.increment();
  }

  double responseErrorCount() {
    return responseErrorCounter.count();
  }

  public void scResendError() {
    scResendErrorCounter.increment();
  }

  double scResendErrorCount() {
    return scResendErrorCounter.count();
  }

  public void invalidMessageError() {
    invalidMessageErrorCounter.increment();
  }

  double invalidMessageErrorCount() {
    return invalidMessageErrorCounter.count();
  }

  public Timer.Sample sample() {
    return Timer.start(registry);
  }

  public Timer commandTimer(Command command) {
    return registry.timer(SIP2_COMMAND_TIMER_NAME, SIP2_COMMAND_TAG, command.toString());
  }

  /**
   * Closes any metrics that need to be closed.
   */
  public void stop() {
    jvmGcMetrics.close();
    socketErrorCounter.close();
    requestErrorCounter.close();
    responseErrorCounter.close();
    scResendErrorCounter.close();
    invalidMessageErrorCounter.close();
  }
}
