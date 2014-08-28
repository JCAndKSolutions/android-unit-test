package com.jcandksolutions.gradle.androidunittest

/**
 * Class that handles the logging of the plugin. It delegates to the gradle logger supplied.
 */
public class Logger {
  private static org.gradle.api.logging.Logger mLogger
  /**
   * Initializes the Logger with the gradle logger instance to delegate to.
   * @param logger The logger to delegate to.
   */
  public static void initialize(org.gradle.api.logging.Logger logger) {
    mLogger = logger
  }

  /**
   * Logs a info message.
   * @param message The message to log.
   */
  public static void logi(String message) {
    mLogger.info(message)
  }

  /**
   * Logs a warning message.
   * @param message The message to log.
   */
  public static void logw(String message) {
    mLogger.warn(message)
  }
}
