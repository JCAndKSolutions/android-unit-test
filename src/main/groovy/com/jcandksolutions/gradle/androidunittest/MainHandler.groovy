package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.api.BaseVariant

import org.gradle.api.internal.DefaultDomainObjectSet

import static com.jcandksolutions.gradle.androidunittest.Logger.logi

/**
 * Base class that coordinates the configuration of the project. This class should trigger the
 * creation of the Configurations, SourceSets, Extension, Tasks and Model.
 */
public abstract class MainHandler {
  protected final ModelManager mModelManager = DependencyInjector.provideModelManager()
  protected final TaskManager mTaskManager = DependencyInjector.provideTaskManager()
  protected final AndroidUnitTestPluginExtension mExtension = DependencyInjector.provideExtension()
  private final ConfigurationManager mConfigurationManager = DependencyInjector.provideConfigurationManager()
  /**
   * Executes the handler. It will trigger the creation of the Configurations, SourceSets,
   * Extension, Tasks and Model.
   */
  public void run() {
    mModelManager.register()
    mConfigurationManager.createNewConfigurations()
    DefaultDomainObjectSet<BaseVariant> variants = DependencyInjector.provideVariants()

    //we use "all" instead of "each" because this set is empty until after project evaluated
    //with "all" it will execute the closure when the variants are getting created
    variants.all { BaseVariant variant ->
      logi("----------------------------------------")
      if (variant.buildType.debuggable || mExtension.testReleaseBuildType) {
        if (isVariantInvalid(variant)) {
          return
        }
        VariantWrapper variantWrapper = createVariantWrapper(variant)
        variantWrapper.configureSourceSet()
        owner.mTaskManager.createTestTask(variantWrapper)
        owner.mModelManager.registerArtifact(variantWrapper)
      } else {
        logi("skipping non-debuggable variant: ${variant.name}")
      }
    }
    logi("----------------------------------------")
    logi("Applied plugin")
  }

  /**
   * Creates a new VariantWrapper instance. Inheritors must implement this method.
   * @param variant The Variant to wrap.
   * @return The wrapper.
   */
  protected abstract VariantWrapper createVariantWrapper(final BaseVariant variant)

  /**
   * Checks if the variant is invalid and should not process it.
   * @param baseVariant The Variant to check.
   * @return {@code true] if invalid, {@code false} otherwise.
   */
  protected abstract boolean isVariantInvalid(final BaseVariant baseVariant)
}
