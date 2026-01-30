package org.folio.edge.sip2.utils;

import static org.folio.edge.sip2.utils.LogUtils.runWithContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;
import org.folio.edge.sip2.session.SessionData;

public class Sip2LogAdapter {

  private final Logger logger;

  /**
   * Creates a new SessionLogger.
   */
  private Sip2LogAdapter(String className) {
    this.logger = LogManager.getLogger(className);
  }

  /**
   * Gets a logger instance for the calling class.
   *
   * @return a new Sip2LogAdapter instance
   */
  public static Sip2LogAdapter getLogger(Class<?> clazz) {
    return new Sip2LogAdapter(clazz.getName());
  }

  /**
   * Logs an informational message.
   *
   * @param message the message to log
   */
  public void info(String message) {
    logger.info(message);
  }

  /**
   * Logs an informational message with parameters.
   *
   * @param message       the message to log with placeholders
   * @param messageParams the parameters to substitute into the message
   */
  public void info(String message, Object... messageParams) {
    logger.info(message, messageParams);
  }

  /**
   * Logs an informational message.
   *
   * @param message the message to log
   * @param paramSuppliers the parameter suppliers to substitute into the message
   */
  public void info(String message, Supplier<?>... paramSuppliers) {
    logger.info(message, paramSuppliers);
  }

  /**
   * Logs an informational message with session context.
   *
   * @param sessionData connection data context
   * @param message     the message to log
   */
  public void info(SessionData sessionData, String message) {
    runWithContext(sessionData, () -> logger.info(message));
  }

  /**
   * Logs an informational message with session context and supplier parameters.
   *
   * @param sessionData    connection data context
   * @param message        the message to log with placeholders
   * @param paramSuppliers the parameter suppliers to substitute into the message
   */
  public void info(SessionData sessionData, String message, Supplier<?>... paramSuppliers) {
    runWithContext(sessionData, () -> logger.info(message, paramSuppliers));
  }

  /**
   * Logs an informational message with session context and parameters.
   *
   * @param sessionData connection data context
   * @param message     the message to log with placeholders
   * @param params      the parameters to substitute into the message
   */
  public void info(SessionData sessionData, String message, Object... params) {
    runWithContext(sessionData, () -> logger.info(message, params));
  }

  /**
   * Logs an error message with parameters.
   *
   * @param message       the error message to log with placeholders
   * @param messageParams the parameters to substitute into the message
   */
  public void error(String message, Object... messageParams) {
    logger.error(message, messageParams);
  }

  /**
   * Logs an error message with session context.
   *
   * @param sessionData connection data context
   * @param message     the error message to log
   */
  public void error(SessionData sessionData, String message) {
    runWithContext(sessionData, () -> logger.error(message));
  }

  /**
   * Logs an error message with session context and throwable.
   *
   * @param sessionData connection data context
   * @param message     the error message to log
   * @param throwable   the throwable associated with the error
   */
  public void error(SessionData sessionData, String message, Throwable throwable) {
    runWithContext(sessionData, () -> logger.error(message, throwable));
  }

  /**
   * Logs an error message with session context and throwable.
   *
   * @param sessionData    connection data context
   * @param message        the error message to log
   * @param paramSuppliers the parameter suppliers to substitute into the message
   */
  public void error(SessionData sessionData, String message, Supplier<?>... paramSuppliers) {
    runWithContext(sessionData, () -> logger.error(message, paramSuppliers));
  }

  /**
   * Logs an error message with session context and parameters.
   *
   * @param sessionData   connection data context
   * @param message       the error message to log with placeholders
   * @param messageParams the parameters to substitute into the message
   */
  public void error(SessionData sessionData, String message, Object... messageParams) {
    runWithContext(sessionData, () -> logger.error(message, messageParams));
  }

  /**
   * Logs a warning message.
   *
   * @param message the warning message to log
   */
  public void warn(String message) {
    logger.warn(message);
  }

  /**
   * Logs a warning message.
   *
   * @param message the warning message to log
   */
  public void warn(String message, Object... messageParams) {
    logger.warn(message, messageParams);
  }

  /**
   * Logs a warning message with session context.
   *
   * @param sessionData connection data context
   * @param message     the warning message to log
   */
  public void warn(SessionData sessionData, String message) {
    runWithContext(sessionData, () -> logger.warn(message));
  }

  /**
   * Logs a warning message with session context.
   *
   * @param sessionData   connection data context
   * @param message       the warning message to log
   * @param messageParams the parameters to substitute into the message
   */
  public void warn(SessionData sessionData, String message, Object... messageParams) {
    runWithContext(sessionData, () -> logger.warn(message, messageParams));
  }

  /**
   * Logs a debug message.
   *
   * @param message the debug message to log
   */
  public void debug(String message) {
    logger.debug(message);
  }

  /**
   * Logs a debug message with parameters.
   *
   * @param message       the debug message to log with placeholders
   * @param messageParams the parameters to substitute into the message
   */
  public void debug(String message, Object... messageParams) {
    logger.debug(message, messageParams);
  }

  /**
   * Logs a debug message with supplier parameters.
   *
   * @param message          the debug message to log with placeholders
   * @param messageSuppliers the parameter suppliers to substitute into the message
   */
  public void debug(String message, Supplier<?>... messageSuppliers) {
    logger.debug(message, messageSuppliers);
  }

  /**
   * Logs a debug message with session context.
   *
   * @param sessionData the session data context
   * @param message     the debug message to log
   */
  public void debug(SessionData sessionData, String message) {
    runWithContext(sessionData, () -> logger.debug(message));
  }

  /**
   * Logs a debug message with session context and supplier parameters.
   *
   * @param sessionData      connection data context
   * @param message          the debug message to log with placeholders
   * @param messageSuppliers the parameter suppliers to substitute into the message
   */
  public void debug(SessionData sessionData, String message, Supplier<?>... messageSuppliers) {
    runWithContext(sessionData, () -> logger.debug(message, messageSuppliers));
  }

  /**
   * Logs a debug message with session context and parameters.
   *
   * @param sessionData   connection data context
   * @param message       the debug message to log with placeholders
   * @param messageParams the parameters to substitute into the message
   */
  public void debug(SessionData sessionData, String message, Object... messageParams) {
    runWithContext(sessionData, () -> logger.debug(message, messageParams));
  }
}
