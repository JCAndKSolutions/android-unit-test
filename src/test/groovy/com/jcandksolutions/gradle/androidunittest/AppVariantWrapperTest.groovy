package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.api.ApplicationVariant
import com.android.builder.core.DefaultBuildType
import com.android.builder.core.DefaultProductFlavor
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.junit.Before
import org.junit.Test

import static org.fest.assertions.api.Assertions.assertThat
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

public class AppVariantWrapperTest {
  private ApplicationVariant mVariant
  private AppVariantWrapper mTarget
  private Project mProject
  private MockProvider mProvider

  @Before
  public void setUp() {
    mProvider = new MockProvider()
    mProject = mProvider.provideProject()
    mVariant = mock(ApplicationVariant.class)
    mTarget = new AppVariantWrapper(mVariant, mProject, mProvider.provideConfigurations(), mProvider.provideBootClasspath(), mProvider.provideLogger())
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
    when(mVariant.dirName).thenReturn("mVariant")
    String resourcesDirName = mTarget.createRealMergedResourcesDirName()
    assertThat(resourcesDirName).isEqualTo("build${File.separator}intermediates${File.separator}res${File.separator}mVariant".toString())
  }

  @Test
  public void testGetAndroidCompileTask() {
    JavaCompile javaCompile = mock(JavaCompile.class)
    when(mVariant.javaCompile).thenReturn(javaCompile)
    assertThat(mTarget.androidCompileTask).isEqualTo(javaCompile)
  }

  @Test
  public void testGetProcessResourcesTaskName() {
    assertThat(mTarget.getProcessResourcesTaskName()).isEqualTo("processFreePaidDebugResources")
  }
}
