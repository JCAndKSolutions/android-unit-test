package com.jcandksolutions.gradle.androidunittest

import java.util.Map.Entry

/**
 * Simple wrapper class that handles several properties of a TestTask.
 */
public class TestTaskConfig {
  private boolean mDebug
  private int mMaxParallelForks
  private long mForkEvery
  private String mMinHeapSize
  private String mMaxHeapSize
  private List<String> mJvmArgs = []
  private List<String> mIncludes = []
  private List<String> mExcludes = []
  private Map<String, Object> mSystemProperties = [:]
  /**
   * Retrieves whether the TestTask should be debugged by attaching a debugger.
   * @return {@code true} if it should debug, {@code false} otherwise.
   */
  public boolean getDebug() {
    return mDebug
  }

  /**
   * Sets the Debug property which enables the debugging of the tests.
   * @param debug The value to set.
   */
  public void setDebug(boolean debug) {
    mDebug = debug
  }

  /**
   * Retrieves the number of parallel forks to run the tests. Default value is 1.
   * @return The number of forks.
   */
  public int getMaxParallelForks() {
    return mMaxParallelForks
  }

  /**
   * Sets the Max Parallel forks property which enables to run tests in parallel.
   * @param maxParallelForks The number of forks.
   */
  public void setMaxParallelForks(int maxParallelForks) {
    if (maxParallelForks < 1) {
      throw new IllegalArgumentException("maxParallelForks cannot be less than 1")
    }
    mMaxParallelForks = maxParallelForks
  }

  /**
   * Retrieves the number of tests to run in each JVM. Default value is 0 (unlimited).
   * @return The number of tests per JVM.
   */
  public long getForkEvery() {
    return mForkEvery
  }

  /**
   * Sets the number of tests to run in each JVM.
   * @param forkEvery The number of tests per JVM.
   */
  public void setForkEvery(long forkEvery) {
    if (forkEvery < 0) {
      throw new IllegalArgumentException("forkEvery cannot be less than 0")
    }
    mForkEvery = forkEvery
  }

  /**
   * Retrieves the minimum heap size for the test JVM.
   * @return The minimum heap size.
   */
  public String getMinHeapSize() {
    return mMinHeapSize
  }

  /**
   * Sets the minimum heap size for the test JVM.
   * @param minHeapSize the minimum heap size for the test JVM.
   */
  public void setMinHeapSize(String minHeapSize) {
    mMinHeapSize = minHeapSize
  }

  /**
   * Retrieves the maximum heap size for the test JVM.
   * @return The maximum heap size.
   */
  public String getMaxHeapSize() {
    return mMaxHeapSize
  }

  /**
   * Sets the maximum heap size for the test JVM.
   * @param maxHeapSize the maximum heap size for the test JVM.
   */
  public void setMaxHeapSize(String maxHeapSize) {
    mMaxHeapSize = maxHeapSize
  }

  /**
   * Retrieves the extra JVM Arguments for the test JVM.
   * @return The extra JVM Arguments for the test JVM.
   */
  public List<String> getJvmArgs() {
    return mJvmArgs
  }

  /**
   * Sets the extra JVM Arguments for the test JVM.
   * @param jvmArgs the extra JVM Arguments for the test JVM.
   */
  public void setJvmArgs(List<String> jvmArgs) {
    mJvmArgs = jvmArgs
  }

  /**
   * Retrieves the include pattern for the tests.
   * @return The include pattern for the tests
   */
  public List<String> getIncludes() {
    return mIncludes
  }

  /**
   * Sets the include pattern for the tests.
   * @param includes the include pattern for the tests.
   */
  public void setIncludes(List<String> includes) {
    mIncludes = includes
  }

  /**
   * Retrieves the exclude pattern for the tests.
   * @return The exclude pattern for the tests
   */
  public List<String> getExcludes() {
    return mExcludes
  }

  /**
   * Sets the exclude pattern for the tests.
   * @param excludes the exclude pattern for the tests.
   */
  public void setExcludes(List<String> excludes) {
    mExcludes = excludes
  }

  /**
   * Retrieves the system properties for the test JVM.
   * @return The system properties for the test JVM.
   */
  public Map<String, Object> getSystemProperties() {
    return mSystemProperties
  }

  /**
   * Sets the system properties for the test JVM.
   * @param systemProperties The system properties for the test JVM.
   */
  public void setSystemProperties(Map<String, Object> systemProperties) {
    mSystemProperties = systemProperties
  }

  /**
   * Adds a system property for the test JVM.
   * @param key The key of the property to add.
   * @param property The value of the property to add.
   */
  public void systemProperty(String key, Object property) {
    mSystemProperties[key] = property
  }

  /**
   * Adds several system properties for the test JVM.
   * @param map The properties to add.
   */
  public void systemProperties(Map<String, Object> map) {
    for (Entry<String, Object> entry : map.entrySet()) {
      systemProperty(entry.key, entry.value);
    }
  }

  /**
   * Adds one or several arguments for the test JVM.
   * @param jvmArgs The argument(s) to add.
   */
  public void jvmArgs(String... jvmArgs) {
    for (String jvmArg : jvmArgs) {
      mJvmArgs.add(jvmArg)
    }
  }

  /**
   * Adds one or several arguments for the test JVM.
   * @param jvmArgs The argument(s) to add.
   */
  public void jvmArgs(Iterable<String> jvmArgs) {
    for (String jvmArg : jvmArgs) {
      mJvmArgs.add(jvmArg)
    }
  }

  /**
   * Adds one or several include filters for the test JVM.
   * @param includes The include filter(s) to add.
   */
  public void include(String... includes) {
    for (String include : includes) {
      mIncludes.add(include)
    }
  }

  /**
   * Adds one or several include filters for the test JVM.
   * @param includes The include filter(s) to add.
   */
  public void include(Iterable<String> includes) {
    for (String include : includes) {
      mIncludes.add(include)
    }
  }

  /**
   * Adds one or several exclude filters for the test JVM.
   * @param excludes The exclude filter(s) to add.
   */
  public void exclude(String... excludes) {
    for (String exclude : excludes) {
      mExcludes.add(exclude)
    }
  }

  /**
   * Adds one or several exclude filters for the test JVM.
   * @param excludes The exclude filter(s) to add.
   */
  public void exclude(Iterable<String> excludes) {
    for (String exclude : excludes) {
      mExcludes.add(exclude)
    }
  }
}
