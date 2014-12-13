package com.jcandksolutions.gradle.androidunittest

import org.gradle.api.Action
import org.junit.Before
import org.junit.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify

public class AndroidUnitTestPluginExtensionTest {
  private AndroidUnitTestPluginExtension mTarget

  @Before
  public void setUp() {
    mTarget = new AndroidUnitTestPluginExtension()
  }

  @Test
  public void testSourceSets() {
    Action<Map<String, SourceSetConfig>> action = mock(Action.class)
    mTarget.sourceSets(action)
    verify(action).execute(mTarget.sourceSets)
  }

  @Test
  public void testTestTasks() {
    Action<Map<String, TestTaskConfig>> action = mock(Action.class)
    mTarget.testTasks(action)
    verify(action).execute(mTarget.testTasks)
  }
}
