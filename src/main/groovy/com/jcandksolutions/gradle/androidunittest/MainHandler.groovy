package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.api.BaseVariant

import org.gradle.api.internal.DefaultDomainObjectSet
import org.gradle.api.logging.Logger

/**
 * Base class that coordinates the configuration of the project. This class should trigger the
 * creation of the Configurations, SourceSets, Extension, Tasks and Model.
 */
public abstract class MainHandler {
  protected final ModelManager mModelManager
  protected final TaskManager mTaskManager
  protected final AndroidUnitTestPluginExtension mExtension
  protected final Logger mLogger
  protected final DependencyProvider mProvider
  private final ConfigurationManager mConfigurationManager
  private final DefaultDomainObjectSet<BaseVariant> mVariants
  /**
   * Instantiates a MainHandler.
   * @param provider The Dependency Provider for the plugin.
   */
  public MainHandler(DependencyProvider provider) {
    mProvider = provider
    mModelManager = mProvider.provideModelManager()
    mTaskManager = mProvider.provideTaskManager()
    mExtension = mProvider.provideExtension()
    mConfigurationManager = mProvider.provideConfigurationManager()
    mLogger = mProvider.provideLogger()
    mVariants = mProvider.provideVariants()
  }

  /**
   * Executes the handler. It will trigger the creation of the Configurations, SourceSets,
   * Extension, Tasks and Model.
   */
  public void run() {
    mModelManager.register()
    mConfigurationManager.createNewConfigurations()

    //we use "all" instead of "each" because this set is empty until after project evaluated
    //with "all" it will execute the closure when the variants are getting created
    mVariants.all { BaseVariant variant ->
      owner.mLogger.info("----------------------------------------")
      if (variant.buildType.debuggable || owner.mExtension.testReleaseBuildType) {
        if (isVariantInvalid(variant)) {
          return
        }
        VariantWrapper variantWrapper = createVariantWrapper(variant)
        variantWrapper.configureSourceSet()
        owner.mTaskManager.createTestTask(variantWrapper)
        owner.mModelManager.registerArtifact(variantWrapper)
      } else {
        owner.mLogger.info("skipping non-debuggable variant: ${variant.name}")
      }
    }
    mLogger.info("----------------------------------------")
    mLogger.info("Applied plugin")
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
