package org.folio.edge.sip2.utils;

import java.util.concurrent.Callable;
import org.apache.logging.log4j.CloseableThreadContext;
import org.folio.edge.sip2.session.SessionData;

public class LogUtils {

  private static final String REQUEST_ID_LOGGING_VAR_NAME = "requestid";
  private static final String TENANT_ID_LOGGING_VAR_NAME = "tenantid";

  private LogUtils() {
    // Utility class, no instantiation allowed
  }

  /**
   * Executes the given {@link Callable} with the logging context set from the provided
   * {@link SessionData}. If {@code sessionData} is {@code null}, the callable is executed
   * without additional context.
   *
   * @param sessionData the session data containing logging context information, may be {@code null}
   * @param callable    the callable to execute
   * @param <R>         the result type of method call
   * @return the result of the callable
   * @throws Exception if the callable throws an exception
   */
  public static <R> R callWithContext(SessionData sessionData, Callable<R> callable)
      throws Exception {
    if (sessionData == null) {
      return callable.call();
    }

    try (var ignored = prepareThreadContext(sessionData)) {
      return callable.call();
    }
  }

  /**
   * Executes given {@link Runnable} with the logging context set from the provided data.
   * If {@code sessionData} is {@code null}, the runnable is executed without additional context.
   *
   * @param sessionData the session data containing logging context information, may be {@code null}
   * @param runnable    the runnable to execute
   */
  public static void runWithContext(SessionData sessionData, Runnable runnable) {
    if (sessionData == null) {
      runnable.run();
      return;
    }

    try (var ignored = prepareThreadContext(sessionData)) {
      runnable.run();
    }
  }

  @SuppressWarnings("resource")
  private static CloseableThreadContext.Instance prepareThreadContext(SessionData sessionData) {
    return CloseableThreadContext
        .put(TENANT_ID_LOGGING_VAR_NAME, sessionData.getTenant())
        .put(REQUEST_ID_LOGGING_VAR_NAME, sessionData.getRequestId());
  }
}
