package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.internal.ProductFlavorData
import com.android.builder.core.VariantConfiguration

import static com.jcandksolutions.gradle.androidunittest.Logger.logi

/**
 * Class that handles the extraction of the Application ID.
 */
public class PackageExtractor {
  private String mPackageName
  /**
   * Retrieves the package name from the Android plugin's default configuration. If not configured,
   * it will try to extract it from the manifest.
   * @return The Application ID which usually is the package name.
   */
  public String getPackageName() {
    if (mPackageName == null) {
      ProductFlavorData data = DependencyInjector.provideDefaultConfigData()
      mPackageName = data.productFlavor.applicationId
      if (mPackageName == null) {
        mPackageName = VariantConfiguration.getManifestPackage(data.sourceSet.manifestFile)
      }
      logi("main package: $mPackageName")
    }
    return mPackageName
  }
}
