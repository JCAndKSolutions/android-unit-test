package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.api.ApplicationVariant

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.logging.Logger

/**
 * Class that wraps specially an AppVariant.
 */
public class AppVariantWrapper extends VariantWrapper {
  /**
   * Instantiates a new AppVariantWrapper.
   * @param applicationVariant The AppVariant to wrap.
   * @param project The project.
   * @param configurations The Project Configurations.
   * @param bootClasspath The bootClasspath.
   * @param logger The Logger.
   */
  public AppVariantWrapper(ApplicationVariant applicationVariant, Project project, ConfigurationContainer configurations, String bootClasspath, Logger logger) {
    super(applicationVariant, project, configurations, bootClasspath, logger, null)
  }

  /**
   * Retrieves the task that compiles the Java sources of the android app so that the tests tasks
   * can depend on it and the correct order of task execution takes place.
   * @return The task.
   */
  @Override
  public Task getAndroidCompileTask() {
    return mVariant.javaCompile
  }
}
