package com.jcandksolutions.gradle.androidunittest

import java.util.Map.Entry

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

  public boolean getDebug() {
    return mDebug
  }

  public void setDebug(boolean debug) {
    mDebug = debug
  }

  public int getMaxParallelForks() {
    return mMaxParallelForks
  }

  public void setMaxParallelForks(int maxParallelForks) {
    if (maxParallelForks < 1) {
      throw new IllegalArgumentException("maxParallelForks cannot be less than 1")
    }
    mMaxParallelForks = maxParallelForks
  }

  public long getForkEvery() {
    return mForkEvery
  }

  public void setForkEvery(long forkEvery) {
    if (forkEvery < 0) {
      throw new IllegalArgumentException("forkEvery cannot be less than 0")
    }
    mForkEvery = forkEvery
  }

  public String getMinHeapSize() {
    return mMinHeapSize
  }

  public void setMinHeapSize(String minHeapSize) {
    mMinHeapSize = minHeapSize
  }

  public String getMaxHeapSize() {
    return mMaxHeapSize
  }

  public void setMaxHeapSize(String maxHeapSize) {
    mMaxHeapSize = maxHeapSize
  }

  public List<String> getJvmArgs() {
    return mJvmArgs
  }

  public void setJvmArgs(List<String> jvmArgs) {
    mJvmArgs = jvmArgs
  }

  public List<String> getIncludes() {
    return mIncludes
  }

  public void setIncludes(List<String> includes) {
    mIncludes = includes
  }

  public List<String> getExcludes() {
    return mExcludes
  }

  public void setExcludes(List<String> excludes) {
    mExcludes = excludes
  }

  public Map<String, Object> getSystemProperties() {
    return mSystemProperties
  }

  public void setSystemProperties(Map<String, Object> systemProperties) {
    mSystemProperties = systemProperties
  }

  public void systemProperty(String key, Object property) {
    mSystemProperties[key] = property
  }

  public void systemProperties(Map<String, Object> map) {
    for (Entry<String, Object> entry : map.entrySet()) {
      systemProperty(entry.key, entry.value);
    }
  }

  public void jvmArgs(String... jvmArgs) {
    for (String jvmArg : jvmArgs) {
      mJvmArgs.add(jvmArg)
    }
  }

  public void jvmArgs(Iterable<String> jvmArgs) {
    for (String jvmArg : jvmArgs) {
      mJvmArgs.add(jvmArg)
    }
  }

  public void include(String... includes) {
    for (String include : includes) {
      mIncludes.add(include)
    }
  }

  public void include(Iterable<String> includes) {
    for (String include : includes) {
      mIncludes.add(include)
    }
  }

  public void exclude(String... excludes) {
    for (String exclude : excludes) {
      mExcludes.add(exclude)
    }
  }

  public void exclude(Iterable<String> excludes) {
    for (String exclude : excludes) {
      mExcludes.add(exclude)
    }
  }
}
