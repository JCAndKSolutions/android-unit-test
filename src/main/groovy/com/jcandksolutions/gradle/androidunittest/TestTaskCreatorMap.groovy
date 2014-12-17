package com.jcandksolutions.gradle.androidunittest

/**
 * CreatorMap implementation that simply creates TestTaskConfig on demand.
 */
public class TestTaskCreatorMap extends CreatorMap<TestTaskConfig> {
  @Override
  protected TestTaskConfig createNewInstance() {
    return new TestTaskConfig()
  }
}
