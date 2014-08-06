package com.jcandksolutions.gradle.androidunittest

class Logger {
  private static org.gradle.api.logging.Logger logger

  static void initialize(org.gradle.api.logging.Logger logger) {
    this.logger = logger
  }

  static void log(String message) {
    logger.info(message)
  }

  static void log(GString message) {
    logger.info(message)
  }
  
  static void logw(String message) {
    logger.warn(message)
  }
  
  static void logw(GString message) {
    logger.warn(message)
  }
}
