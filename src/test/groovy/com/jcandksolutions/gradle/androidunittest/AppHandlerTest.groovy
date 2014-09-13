package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant

import org.junit.Before
import org.junit.Test

import static org.fest.assertions.api.Assertions.assertThat
import static org.mockito.Mockito.mock

public class AppHandlerTest {
  private AppHandler mTarget
  private MockProvider mProvider
  private VariantWrapper mWrapper

  @Before
  public void setUp() {
    mProvider = new MockProvider()
    mWrapper = mProvider.provideAppVariantWrapper(null)
    mTarget = new AppHandler(mProvider)
  }

  @Test
  public void testIsVariantInvalid() {
    BaseVariant variant = mock(BaseVariant.class)
    assertThat(mTarget.isVariantInvalid(variant)).isFalse()
  }

  @Test
  public void testCreateVariantWrapper() {
    ApplicationVariant variant = mock(ApplicationVariant.class)
    VariantWrapper wrapper = mTarget.createVariantWrapper(variant)
    assertThat(wrapper).isEqualTo(mWrapper)
  }
}
