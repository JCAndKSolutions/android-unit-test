package com.jcandksolutions.gradle.androidunittest

public class SourceSetConfig {
  private SourceDir mSourceDir = new SourceDir()
  private SourceDir mResourcesDir = new SourceDir()

  public SourceDir getJava() {
    return mSourceDir
  }

  public SourceDir getResources() {
    return mResourcesDir
  }
}
