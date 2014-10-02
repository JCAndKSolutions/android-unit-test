package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.api.LibraryVariant
import com.android.build.gradle.api.TestVariant
import com.android.build.gradle.tasks.MergeResources
import com.android.builder.core.DefaultBuildType
import com.android.builder.core.DefaultProductFlavor
import org.gradle.api.Project
import org.junit.Before
import org.junit.Test

import static org.fest.assertions.api.Assertions.assertThat
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

public class LibraryVariantWrapperTest {
  private LibraryVariant mVariant
  private LibraryVariantWrapper mTarget
  private Project mProject
  private TestVariant mTestVariant
  private MockProvider mProvider

  @Before
  public void setUp() {
    mProvider = new MockProvider()
    mProject = mProvider.provideProject()
    mVariant = mock(LibraryVariant.class)
    mTestVariant = mock(TestVariant.class)
    when(mVariant.testVariant).thenReturn(mTestVariant)
    mTarget = new LibraryVariantWrapper(mVariant, mProject, mProvider.provideConfigurations(), mProvider.provideBootClasspath(), mProvider.provideLogger())

    DefaultProductFlavor free = mock(DefaultProductFlavor.class)
    DefaultProductFlavor paid = mock(DefaultProductFlavor.class)
    List<DefaultProductFlavor> productFlavors = [free, paid]
    DefaultBuildType buildType = mock(DefaultBuildType.class)

    when(free.name).thenReturn("free")
    when(paid.name).thenReturn("paid")
    when(mVariant.productFlavors).thenReturn(productFlavors)
    when(mVariant.buildType).thenReturn(buildType)
    when(buildType.name).thenReturn("debug")
  }

  @Test
  public void testCreateRealMergedResourcesDirName() {
    when(mProject.buildDir).thenReturn(new File("build"))
    when(mTestVariant.dirName).thenReturn("mVariant")
    String resourcesDirName = mTarget.createRealMergedResourcesDirName()
    assertThat(resourcesDirName).isEqualTo("build${File.separator}intermediates${File.separator}res${File.separator}mVariant".toString())
  }

  @Test
  public void testGetAndroidCompileTask() {
    MergeResources mergeResources = mock(MergeResources.class)
    when(mTestVariant.mergeResources).thenReturn(mergeResources)
    assertThat(mTarget.androidCompileTask).isEqualTo(mergeResources)
  }

  @Test
  public void testGetProcessResourcesTaskName() {
    assertThat(mTarget.getProcessResourcesTaskName()).isEqualTo("processTestFreePaidDebugResources")
  }
}
