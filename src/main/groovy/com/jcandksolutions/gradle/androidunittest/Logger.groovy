package com.jcandksolutions.gradle.androidunittest

class Logger {
  private static org.gradle.api.logging.Logger mLogger

  static void initialize(org.gradle.api.logging.Logger logger) {
    mLogger = logger
  }

  static void log(String message) {
    mLogger.info(message)
  }

  static void log(GString message) {
    mLogger.info(message)
  }
}
