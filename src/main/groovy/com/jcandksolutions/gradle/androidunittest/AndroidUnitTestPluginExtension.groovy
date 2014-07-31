package com.jcandksolutions.gradle.androidunittest

/**
 * Class that handles the extension of the plugin for configuration
 */
class AndroidUnitTestPluginExtension {
  private boolean testReleaseBuildType = false;

  public boolean getTestReleaseBuildType() {
    return testReleaseBuildType;
  }

  public void setTestReleaseBuildType(boolean value) {
    testReleaseBuildType = value;
  }
}
