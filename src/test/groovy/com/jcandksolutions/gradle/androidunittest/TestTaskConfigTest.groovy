package com.jcandksolutions.gradle.androidunittest

import org.junit.Before
import org.junit.Test

import static org.fest.assertions.api.Assertions.assertThat
import static org.fest.assertions.api.Assertions.entry
import static org.fest.assertions.api.Assertions.fail

public class TestTaskConfigTest {
  private TestTaskConfig mTarget

  @Before
  public void setUp() {
    mTarget = new TestTaskConfig()
  }

  @Test
  public void testSetMaxParallelForks() {
    try {
      mTarget.maxParallelForks = 0
      fail("Expected exception to be thrown");
    } catch (IllegalArgumentException ignore) {
    }
  }

  @Test
  public void testSetForkEvery() {
    try {
      mTarget.forkEvery = -1
      fail("Expected exception to be thrown");
    } catch (IllegalArgumentException ignore) {
    }
  }

  @Test
  public void testSystemProperty() {
    mTarget.systemProperty("key", "val")
    assertThat(mTarget.systemProperties).hasSize(1).contains(entry("key", "val"))
  }

  @Test
  public void testSystemProperties() {
    mTarget.systemProperties(["key1": "val1", "key2": "val2"])
    assertThat(mTarget.systemProperties).hasSize(2).contains(entry("key1", "val1"), entry("key2", "val2"))
  }

  @Test
  public void testJvmArgs() {
    mTarget.jvmArgs(["val1", "val2"])
    assertThat(mTarget.jvmArgs).hasSize(2).contains("val1", "val2")
    mTarget.jvmArgs.clear()
    assertThat(mTarget.jvmArgs).isEmpty()
    mTarget.jvmArgs("val1", "val2")
    assertThat(mTarget.jvmArgs).hasSize(2).contains("val1", "val2")
  }

  @Test
  public void testExcludes() {
    mTarget.exclude(["val1", "val2"])
    assertThat(mTarget.excludes).hasSize(2).contains("val1", "val2")
    mTarget.excludes.clear()
    assertThat(mTarget.excludes).isEmpty()
    mTarget.exclude("val1", "val2")
    assertThat(mTarget.excludes).hasSize(2).contains("val1", "val2")
  }

  @Test
  public void testIncludes() {
    mTarget.include(["val1", "val2"])
    assertThat(mTarget.includes).hasSize(2).contains("val1", "val2")
    mTarget.includes.clear()
    assertThat(mTarget.includes).isEmpty()
    mTarget.include("val1", "val2")
    assertThat(mTarget.includes).hasSize(2).contains("val1", "val2")
  }
}
