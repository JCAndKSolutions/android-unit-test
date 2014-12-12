package com.jcandksolutions.gradle.androidunittest

class SourceSetCreatorMap extends CreatorMap<String, SourceSetConfig> {
  @Override
  protected SourceSetConfig createNewInstance() {
    return new SourceSetConfig()
  }
}
