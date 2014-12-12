package com.jcandksolutions.gradle.androidunittest

import org.junit.Before
import org.junit.Test

import static org.fest.assertions.api.Assertions.assertThat

public class SourceDirTest {
  private SourceDir mTarget

  @Before
  public void setUp() {
    mTarget = new SourceDir()
  }

  @Test
  public void testSetting() {
    assertThat(mTarget.srcDirs).isEmpty()
    mTarget.srcDir("test1")
    assertThat(mTarget.srcDirs).isNotEmpty().hasSize(1).contains("test1")
    assertThat(mTarget.overWritten).isFalse()
    List<Object> dirs = ["test2"]
    mTarget.srcDirs = dirs
    assertThat(mTarget.srcDirs).isNotEmpty().hasSize(1).contains("test2")
    assertThat(mTarget.overWritten).isTrue()
  }
}
