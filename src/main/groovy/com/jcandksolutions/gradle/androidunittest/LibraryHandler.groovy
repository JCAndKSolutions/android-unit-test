package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.api.LibraryVariant

/**
 * Class that handles the extra configuration when the library plugin is applied.
 */
public class LibraryHandler extends MainHandler {
  /**
   * Instantiates a new LibraryHandler.
   * @param provider The Dependency Provider for the plugin.
   */
  public LibraryHandler(DependencyProvider provider) {
    super(provider)
  }

  /**
   * Creates a new VariantWrapper instance special for LibraryVariants.
   * @param variant The LibraryVariant to wrap.
   * @return The wrapper.
   */
  @Override
  protected VariantWrapper createVariantWrapper(final BaseVariant variant) {
    return mProvider.provideLibraryVariantWrapper(variant as LibraryVariant)
  }

  /**
   * Checks if the variant is invalid. For LibraryVariants, they are invalid if there is no
   * TestVariant associated to it. This is because the TestVariant is used to trigger the resources
   * merge by the android plugin which only the App plugin does naturally.
   * @param baseVariant The Variant to check.
   * @return {@code true} if no TestVariant available. {@code false} otherwise.
   */
  @Override
  protected boolean isVariantInvalid(final BaseVariant variant) {
    if (((LibraryVariant) variant).testVariant == null) {
      // Can't test a library project if there is no test variant
      return true
    }
    return false
  }
}
