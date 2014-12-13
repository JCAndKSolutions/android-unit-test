package com.jcandksolutions.gradle.androidunittest

import org.gradle.api.Action

/**
 * Class that handles the extension of the plugin for configuration.
 */
public class AndroidUnitTestPluginExtension {
  private boolean mTestReleaseBuildType
  private boolean mDownloadTestDependenciesSources
  private boolean mDownloadTestDependenciesJavadoc
  private boolean mDownloadDependenciesJavadoc
  private boolean mDownloadDependenciesSources
  private Map<String, SourceSetConfig> mSourceSets = new SourceSetCreatorMap()
  private Map<String, TestTaskConfig> mTestTasks = new TestTaskCreatorMap()
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
   * Retrieves the SourceSets property which enables the configuration of source directories for
   * tests.
   * @return A map containing the sourceSets.
   */
  public Map<String, SourceSetConfig> getSourceSets() {
    return mSourceSets
  }

  /**
   * Configures the sourceSets through an Action received from the build file.
   * @param action The action to configure the sourceSets.
   */
  public void sourceSets(Action<Map<String, SourceSetConfig>> action) {
    action.execute(mSourceSets)
  }

  /**
   * Retrieves the TestTasks property which enables the configuration of the test tasks.
   * @return A map containing the testTasks.
   */
  public Map<String, TestTaskConfig> getTestTasks() {
    return mTestTasks
  }

  /**
   * Configures the testTasks through an Action received from the build file.
   * @param action The action to configure the testTasks.
   */
  public void testTasks(Action<Map<String, TestTaskConfig>> action) {
    action.execute(mTestTasks)
  }
}
