package com.jcandksolutions.gradle.androidunittest

/**
 * CreatorMap implementation that simply creates TestTaskConfig on demand.
 */
public class TestTaskCreatorMap extends CreatorMap<String, TestTaskConfig> {
  @Override
  protected TestTaskConfig createNewInstance() {
    return new TestTaskConfig()
  }
}
