package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.api.LibraryVariant

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.logging.Logger

/**
 * Class that wraps specially a LibraryVariant.
 */
public class LibraryVariantWrapper extends VariantWrapper {
  /**
   * Instantiates a new LibraryVariantWrapper.
   * @param libraryVariant The LibraryVariant to wrap.
   * @param project The project.
   * @param configurations The Project Configurations.
   * @param bootClasspath The bootClasspath.
   * @param logger The Logger.
   */
  public LibraryVariantWrapper(LibraryVariant libraryVariant, Project project, ConfigurationContainer configurations, String bootclasspath, Logger logger) {
    super(libraryVariant, project, configurations, bootclasspath, logger, libraryVariant.testVariant)
  }

  /**
   * Creates the path string for where the resources are being merged by the Library plugin during
   * the test App apk compilation.
   * @return The path string.
   */
  @Override
  protected String createRealMergedResourcesDirName() {
    return "$mProject.buildDir${File.separator}intermediates${File.separator}res${File.separator}$mTestVariant.dirName"
  }

  /**
   * Retrieves the task that merges the resources for the test apk so that the tests tasks can
   * depend on it and trigger the resources merging and also for the correct order of task execution
   * takes place.
   * @return The task.
   */
  @Override
  public Task getAndroidCompileTask() {
    return mTestVariant.mergeResources
  }

  /**
   * Retrieves the name of the task that process the test resources.<br/>
   * For example: processTestFreeBetaDebugResources.
   * @return The name of the task.
   */
  @Override
  public String getProcessResourcesTaskName() {
    if (mProcessResourcesTaskName == null) {
      mProcessResourcesTaskName = "processTest${completeName}Resources"
    }
    return mProcessResourcesTaskName
  }
}
