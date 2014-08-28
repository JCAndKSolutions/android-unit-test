package com.jcandksolutions.gradle.androidunittest

import com.android.builder.model.SourceProvider

/**
 * Class that implements the SourceProvider needed for the Android plugin to register the model.
 */
public class TestSourceProvider implements SourceProvider {
  private VariantWrapper mVariantWrapper;
  /**
   * Instantiates a new TestSourceProvider.
   * @param variantWrapper The variant for which we are providing the TestSource.
   */
  public TestSourceProvider(VariantWrapper variantWrapper) {
    mVariantWrapper = variantWrapper
  }

  @Override
  public String getName() {
    return mVariantWrapper.sourceSet.name
  }

  @Override
  public File getManifestFile() {
    return mVariantWrapper.mergedManifest
  }

  @Override
  public Collection<File> getJavaDirectories() {
    return mVariantWrapper.sourceSet.java.srcDirs
  }

  @Override
  public Collection<File> getResourcesDirectories() {
    return mVariantWrapper.sourceSet.resources.srcDirs
  }

  @Override
  public Collection<File> getAidlDirectories() {
    return Collections.emptyList()
  }

  @Override
  public Collection<File> getRenderscriptDirectories() {
    return Collections.emptyList()
  }

  @Override
  public Collection<File> getJniDirectories() {
    return Collections.emptyList()
  }

  @Override
  public Collection<File> getResDirectories() {
    return Collections.singleton(mVariantWrapper.mergedResourcesDir)
  }

  @Override
  public Collection<File> getAssetsDirectories() {
    return Collections.singleton(mVariantWrapper.mergedAssetsDir)
  }

  @Override
  public Collection<File> getJniLibsDirectories() {
    return Collections.emptyList()
  }
}
