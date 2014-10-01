package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.api.ApplicationVariant

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
  }

  @Test
  public void testGetAndroidCompileTask() {
    JavaCompile javaCompile = mock(JavaCompile.class)
    when(mVariant.javaCompile).thenReturn(javaCompile)
    assertThat(mTarget.androidCompileTask).isEqualTo(javaCompile)
  }
}
