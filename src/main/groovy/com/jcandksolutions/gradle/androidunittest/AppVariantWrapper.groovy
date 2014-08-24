package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.api.ApplicationVariant

import org.gradle.api.Task

/**
 * Class that wraps specially an AppVariant.
 */
public class AppVariantWrapper extends VariantWrapper {
  /**
   * Instantiates a new AppVariantWrapper.
   * @param applicationVariant The AppVariant to wrap.
   */
  public AppVariantWrapper(ApplicationVariant applicationVariant) {
    super(applicationVariant)
  }

  /**
   * Creates the path string for where the resources are being merged by the App plugin during the
   * App apk compilation.
   * @return The path string.
   */
  @Override
  protected String createRealMergedResourcesDirName() {
    return "$mProject.buildDir${File.separator}intermediates${File.separator}res${File.separator}$mVariant.dirName"
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
