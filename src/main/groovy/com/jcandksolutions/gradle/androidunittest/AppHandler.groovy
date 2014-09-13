package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant

/**
 * Class that handles the extra configuration when the app plugin is applied.
 */
public class AppHandler extends MainHandler {
  /**
   * Instantiates a new AppHandler.
   * @param provider The Dependency Provider for the plugin.
   */
  public AppHandler(DependencyProvider provider) {
    super(provider)
  }

  /**
   * Creates a new VariantWrapper instance special for AppVariants.
   * @param variant The AppVariant to wrap.
   * @return The wrapper.
   */
  @Override
  protected VariantWrapper createVariantWrapper(final BaseVariant variant) {
    return mProvider.provideAppVariantWrapper(variant as ApplicationVariant)
  }

  /**
   * Checks if the variant is invalid. For AppVariants, they are always valid.
   * @param baseVariant The Variant to check.
   * @return Always {@code false}.
   */
  @Override
  protected boolean isVariantInvalid(final BaseVariant baseVariant) {
    return false
  }
}
