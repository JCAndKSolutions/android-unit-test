package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.api.LibraryVariant
import com.android.build.gradle.api.TestVariant

import org.junit.Before
import org.junit.Test

import static org.fest.assertions.api.Assertions.assertThat
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

public class LibraryHandlerTest {
  private LibraryHandler mTarget
  private MockProvider mProvider
  private LibraryVariantWrapper mWrapper

  @Before
  public void setUp() {
    mProvider = new MockProvider()
    mWrapper = mProvider.provideLibraryVariantWrapper(null)
    mTarget = new LibraryHandler(mProvider)
  }

  @Test
  public void testIsVariantInvalid() {
    LibraryVariant variant = mock(LibraryVariant.class)
    when(variant.testVariant).thenReturn(null, mock(TestVariant.class))
    assertThat(mTarget.isVariantInvalid(variant)).isTrue()
    assertThat(mTarget.isVariantInvalid(variant)).isFalse()
  }

  @Test
  public void testCreateVariantWrapper() {
    LibraryVariant variant = mock(LibraryVariant.class)
    VariantWrapper wrapper = mTarget.createVariantWrapper(variant)
    assertThat(wrapper).isEqualTo(mWrapper)
  }
}
