package com.jcandksolutions.gradle.androidunittest

import com.android.builder.model.SourceProvider

class TestSourceProvider implements SourceProvider {
  private VariantWrapper variantWrapper;

  TestSourceProvider(VariantWrapper variantWrapper) {
    this.variantWrapper = variantWrapper
  }

  @Override
  String getName() {
    return variantWrapper.sourceSet.name
  }

  @Override
  public File getManifestFile() {
    return variantWrapper.mergedManifest
  }

  @Override
  Collection<File> getJavaDirectories() {
    return variantWrapper.sourceSet.java.srcDirs
  }

  @Override
  Collection<File> getResourcesDirectories() {
    return variantWrapper.sourceSet.resources.srcDirs
  }

  @Override
  Collection<File> getAidlDirectories() {
    return Collections.emptyList()
  }

  @Override
  Collection<File> getRenderscriptDirectories() {
    return Collections.emptyList()
  }

  @Override
  Collection<File> getJniDirectories() {
    return Collections.emptyList()
  }

  @Override
  public Collection<File> getResDirectories() {
    return Collections.singleton(variantWrapper.mergedResourcesDir)
  }

  @Override
  public Collection<File> getAssetsDirectories() {
    return Collections.singleton(variantWrapper.mergedAssetsDir)
  }

  @Override
  Collection<File> getJniLibsDirectories() {
    return Collections.emptyList()
  }
}
