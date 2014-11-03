package com.jcandksolutions.gradle.androidunittest

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.SourceSet
import org.junit.Before
import org.junit.Test

import static org.fest.assertions.api.Assertions.assertThat
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

public class TestSourceProviderTest {
  private TestSourceProvider mTarget
  private File mMergedManifest
  private Set<File> mJavaDirectories
  private HashSet mResourcesDirectories
  private File mMergedResourcesDir
  private File mMergedAssetsDir

  @Before
  public void setUp() {
    VariantWrapper wrapper = mock(VariantWrapper.class)
    SourceSet sourceSet = mock(SourceSet.class)
    SourceDirectorySet java = mock(SourceDirectorySet.class)
    mJavaDirectories = new HashSet<>()
    mMergedManifest = new File("mergedManifest")
    SourceDirectorySet resources = mock(SourceDirectorySet.class)
    mResourcesDirectories = new HashSet<>()
    mMergedResourcesDir = new File("mergedResourcesDir")
    mMergedAssetsDir = new File("mergedAssetsDir")
    when(java.srcDirs).thenReturn(mJavaDirectories)
    when(resources.srcDirs).thenReturn(mResourcesDirectories)
    when(wrapper.sourceSet).thenReturn(sourceSet)
    when(wrapper.mergedManifest).thenReturn(mMergedManifest)
    when(wrapper.mergedResourcesDir).thenReturn(mMergedResourcesDir)
    when(wrapper.mergedAssetsDir).thenReturn(mMergedAssetsDir)
    when(sourceSet.name).thenReturn("name")
    when(sourceSet.java).thenReturn(java)
    when(sourceSet.resources).thenReturn(resources)
    mTarget = new TestSourceProvider(wrapper)
  }

  @Test
  public void testGetName() {
    assertThat(mTarget.name).isEqualTo("name")
  }

  @Test
  public void testGetManifestFile() {
    assertThat(mTarget.manifestFile).isEqualTo(mMergedManifest)
  }

  @Test
  public void testGetJavaDirectories() {
    assertThat(mTarget.javaDirectories).isEqualTo(mJavaDirectories)
  }

  @Test
  public void testGetResourcesDirectories() {
    assertThat(mTarget.resourcesDirectories).isEqualTo(mResourcesDirectories)
  }

  @Test
  public void testGetAidlDirectories() {
    assertThat(mTarget.aidlDirectories).isEqualTo(Collections.emptyList())
  }

  @Test
  public void testGetRenderscriptDirectories() {
    assertThat(mTarget.renderscriptDirectories).isEqualTo(Collections.emptyList())
  }

  @Test
  public void testGetCDirectories() {
    assertThat(mTarget.CDirectories).isEqualTo(Collections.emptyList())
  }

  @Test
  public void testGetCppDirectories() {
    assertThat(mTarget.cppDirectories).isEqualTo(Collections.emptyList())
  }

  @Test
  public void testGetResDirectories() {
    assertThat(mTarget.resDirectories).containsExactly(mMergedResourcesDir)
  }

  @Test
  public void testGetAssetsDirectories() {
    assertThat(mTarget.assetsDirectories).containsExactly(mMergedAssetsDir)
  }

  @Test
  public void testGetJniLibsDirectories() {
    assertThat(mTarget.jniLibsDirectories).isEqualTo(Collections.emptyList())
  }
}
