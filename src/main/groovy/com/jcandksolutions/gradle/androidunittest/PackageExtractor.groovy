package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.internal.ProductFlavorData
import com.android.builder.core.VariantConfiguration
import org.gradle.api.logging.Logger

/**
 * Class that handles the extraction of the Application ID.
 */
public class PackageExtractor {
  private final ProductFlavorData mData
  private final Logger mLogger
  private String mPackageName
  /**
   * Instantiates a new PackageExtractor.
   * @param data The data for the Default configuration of the project.
   * @param logger The logger.
   */
  public PackageExtractor(ProductFlavorData data, Logger logger) {
    mLogger = logger
    mData = data
  }

  /**
   * Retrieves the package name from the Android plugin's default configuration. If not configured,
   * it will try to extract it from the manifest.
   * @return The Application ID which usually is the package name.
   */
  public String getPackageName() {
    if (mPackageName == null) {
      mPackageName = mData.productFlavor.applicationId
      if (mPackageName == null) {
        mPackageName = VariantConfiguration.getManifestPackage(mData.sourceSet.manifestFile)
      }
      mLogger.info("main package: $mPackageName")
    }
    return mPackageName
  }
}
