package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.api.LibraryVariant
import com.android.build.gradle.api.TestVariant
import com.android.build.gradle.tasks.MergeResources

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
  }

  @Test
  public void testGetAndroidCompileTask() {
    MergeResources mergeResources = mock(MergeResources.class)
    when(mTestVariant.mergeResources).thenReturn(mergeResources)
    assertThat(mTarget.androidCompileTask).isEqualTo(mergeResources)
  }
}
