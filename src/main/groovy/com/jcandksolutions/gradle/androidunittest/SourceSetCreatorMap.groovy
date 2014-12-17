package com.jcandksolutions.gradle.androidunittest

/**
 * CreatorMap implementation that simply creates SourceSetConfig on demand.
 */
public class SourceSetCreatorMap extends CreatorMap<SourceSetConfig> {
  @Override
  protected SourceSetConfig createNewInstance() {
    return new SourceSetConfig()
  }
}
