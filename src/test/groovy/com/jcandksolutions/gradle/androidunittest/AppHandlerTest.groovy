package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.api.BaseVariant

import org.junit.Before
import org.junit.Test

import static org.fest.assertions.api.Assertions.assertThat
import static org.mockito.Mockito.mock

public class AppHandlerTest {
  private AppHandler mTarget

  @Before
  public void setUp() {
    DependencyInjector.provider = new MockProvider()
    mTarget = new AppHandler()
  }

  @Test
  public void testIsVariantInvalid() {
    BaseVariant variant = mock(BaseVariant.class)
    assertThat(mTarget.isVariantInvalid(variant)).isFalse()
  }
}
