package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.api.LibraryVariant

import org.gradle.api.Task

/**
 * Class that wraps specially a LibraryVariant.
 */
public class LibraryVariantWrapper extends VariantWrapper {
  /**
   * Instantiates a new LibraryVariantWrapper.
   * @param libraryVariant The LibraryVariant to wrap.
   */
  public LibraryVariantWrapper(LibraryVariant libraryVariant) {
    super(libraryVariant)
    mTestVariant = libraryVariant.testVariant
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
}
