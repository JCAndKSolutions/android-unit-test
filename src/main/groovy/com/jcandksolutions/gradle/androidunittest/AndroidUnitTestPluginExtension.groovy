package com.jcandksolutions.gradle.androidunittest

/**
 * Class that handles the extension of the plugin for configuration.
 */
public class AndroidUnitTestPluginExtension {
  private boolean mTestReleaseBuildType;
  /**
   * Retrieves the TestReleaseBuildType property which enables testing if release build types. Only
   * works on App projects, not library projects.
   * @return {@code true} if property enabled, {@code false} otherwise.
   */
  public boolean getTestReleaseBuildType() {
    return mTestReleaseBuildType;
  }

  /**
   * Sets the TestReleaseBuildType property which enables testing if release build types. Only
   * works on App projects, not library projects.
   * @param value The value to set.
   */
  public void setTestReleaseBuildType(boolean value) {
    mTestReleaseBuildType = value;
  }
}
