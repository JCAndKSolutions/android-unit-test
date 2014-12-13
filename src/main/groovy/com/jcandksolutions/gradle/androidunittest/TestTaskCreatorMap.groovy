package com.jcandksolutions.gradle.androidunittest

public class TestTaskCreatorMap extends CreatorMap<String, TestTaskConfig> {
  @Override
  protected TestTaskConfig createNewInstance() {
    return new TestTaskConfig()
  }
}
