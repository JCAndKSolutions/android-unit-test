package com.jcandksolutions.gradle.androidunittest

/**
 * POGO class with the sourceDir and resourceDir.
 */
public class SourceSetConfig {
  private SourceDir mSourceDir = new SourceDir()
  private SourceDir mResourcesDir = new SourceDir()
  /**
   * Retrieves the Java SourceDir.
   * @return The Java SourceDir.
   */
  public SourceDir getJava() {
    return mSourceDir
  }

  /**
   * Retrieves the Resources SourceDir.
   * @return The Resources SourceDir.
   */
  public SourceDir getResources() {
    return mResourcesDir
  }
}
