package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.internal.ProductFlavorData
import com.android.build.gradle.internal.api.DefaultAndroidSourceSet
import com.android.builder.core.DefaultProductFlavor

import org.junit.Before
import org.junit.Test

import static org.fest.assertions.api.Assertions.assertThat
import static org.fest.assertions.api.Fail.fail
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

public class PackageExtractorTest {
  private PackageExtractor mTarget

  @Before
  public void setUp() {
    DependencyInjector.setProvider(new MockProvider())
    Logger.initialize(mock(org.gradle.api.logging.Logger.class))
    mTarget = new PackageExtractor()
  }

  @Test
  public void testGetPackageNameWithProvidedAppId() {
    ProductFlavorData data = DependencyInjector.provideDefaultConfigData()
    DefaultProductFlavor flavor = data.productFlavor
    when(flavor.applicationId).thenReturn("package")
    assertThat(mTarget.packageName).isEqualTo("package")
  }

  @Test
  public void testGetPackageNameFromManifest() {
    ProductFlavorData data = DependencyInjector.provideDefaultConfigData()
    DefaultAndroidSourceSet source = data.sourceSet
    when(source.manifestFile).thenReturn(new File("package"))
    try {
      mTarget.packageName
      fail("Should throw FileNotFoundException")
    } catch (RuntimeException ignored) {
      assertThat(ignored).hasMessageStartingWith("java.io.FileNotFoundException: package")
    }
  }
}
