package com.jcandksolutions.gradle.androidunittest

/**
 * Class that handles the extension of the plugin for configuration.
 */
public class AndroidUnitTestPluginExtension {
  private boolean mTestReleaseBuildType
  private boolean mDownloadTestDependenciesSources
  private boolean mDownloadTestDependenciesJavadoc
  private boolean mDownloadDependenciesJavadoc
  private boolean mDownloadDependenciesSources
  private final List<String> jvmArgs = new LinkedList<>()

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

  /**
   * Retrieves the DownloadTestDependenciesSources property which enables the download of the
   * sources of the tests dependencies.
   * @return {@code true} if property enabled, {@code false} otherwise.
   */
  public boolean isDownloadTestDependenciesSources() {
    return mDownloadTestDependenciesSources
  }

  /**
   * Sets the DownloadTestDependenciesSources property which enables the download of the sources of
   * the tests dependencies.
   * @param value The value to set.
   */
  public void setDownloadTestDependenciesSources(boolean value) {
    mDownloadTestDependenciesSources = value
  }

  /**
   * Retrieves the DownloadTestDependenciesJavadoc property which enables the download of the
   * Javadoc of the tests dependencies.
   * @return {@code true} if property enabled, {@code false} otherwise.
   */
  public boolean isDownloadTestDependenciesJavadoc() {
    return mDownloadTestDependenciesJavadoc
  }

  /**
   * Sets the DownloadTestDependenciesJavadoc property which enables the download of the Javadoc of
   * the tests dependencies.
   * @param value The value to set.
   */
  public void setDownloadTestDependenciesJavadoc(boolean value) {
    mDownloadTestDependenciesJavadoc = value
  }

  /**
   * Retrieves the DownloadDependenciesSources property which enables the download of the
   * sources of the app dependencies.
   * @return {@code true} if property enabled, {@code false} otherwise.
   */
  public boolean isDownloadDependenciesSources() {
    return mDownloadDependenciesSources
  }

  /**
   * Sets the DownloadDependenciesSources property which enables the download of the sources of
   * the app dependencies.
   * @param value The value to set.
   */
  public void setDownloadDependenciesSources(boolean value) {
    mDownloadDependenciesSources = value
  }

  /**
   * Retrieves the DownloadDependenciesJavadoc property which enables the download of the
   * Javadoc of the app dependencies.
   * @return {@code true} if property enabled, {@code false} otherwise.
   */
  public boolean isDownloadDependenciesJavadoc() {
    return mDownloadDependenciesJavadoc
  }

  /**
   * Sets the DownloadDependenciesJavadoc property which enables the download of the Javadoc of
   * the app dependencies.
   * @param value The value to set.
   */
  public void setDownloadDependenciesJavadoc(boolean value) {
    mDownloadDependenciesJavadoc = value
  }

  /**
   * Retrieves the jvmArgs the Test Task should run with
   * @return List of JvmArgs
   */
  public List<String> getJvmArgs() {
    return jvmArgs
  }

  /**
   * Sets the jvmArgs the Test Task should run with
   * @param jvmArgs to be added to the list of jvmArgs
   */
  public void jvmArgs(String... jvmArgs) {
    this.jvmArgs.addAll jvmArgs
  }
}
