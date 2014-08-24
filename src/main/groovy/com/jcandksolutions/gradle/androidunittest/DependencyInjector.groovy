package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.BasePlugin
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.ProductFlavorData

import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.internal.DefaultDomainObjectSet

/**
 * Class that injects the dependencies to other classes by delegating to a
 * {@link DependencyProvider}, allowing to inject different dependencies just by changing the
 * provider at runtime.
 */
public class DependencyInjector {
  private static DependencyProvider mProvider;

  private DependencyInjector() {
  }

  /**
   * Sets the provider to use for delegation.
   * @param provider The provider.
   */
  public static void setProvider(final DependencyProvider provider) {
    mProvider = provider;
  }

  /**
   * Provides the Project that is applying the plugin.
   * @return The Project.
   */
  public static Project provideProject() {
    return mProvider.provideProject()
  }

  /**
   * Provides the Android plugin that should've been applied.
   * @return The Android plugin.
   */
  public static BasePlugin provideAndroidPlugin() {
    return mProvider.provideAndroidPlugin()
  }

  /**
   * Provides the Extension used by this plugin.
   * @return The Extension.
   */
  public static AndroidUnitTestPluginExtension provideExtension() {
    return mProvider.provideExtension()
  }

  /**
   * Provides the Model Manager that registers the model of the test source sets.
   * @return The ModelManager.
   */
  public static ModelManager provideModelManager() {
    return mProvider.provideModelManager()
  }

  /**
   * Provides the Configuration Manager that creates the test configurations.
   * @return The ConfigurationManager.
   */
  public static ConfigurationManager provideConfigurationManager() {
    return mProvider.provideConfigurationManager()
  }

  /**
   * Provides the Task Manager that creates the tasks required to run the tests.
   * @return The TaskManager.
   */
  public static TaskManager provideTaskManager() {
    return mProvider.provideTaskManager()
  }

  /**
   * Provides the Default Configuration Data of the Android Plugin. Used to know the application id.
   * @return The Default Configuration.
   */
  public static ProductFlavorData provideDefaultConfigData() {
    return mProvider.provideDefaultConfigData()
  }

  /**
   * Returns whether the Android Plugin applied is the App plugin or de Library plugin.
   * @return {@code true} if the app plugin was applied. {@code false} otherwise.
   */
  public static boolean isAppPlugin() {
    return mProvider.isAppPlugin()
  }

  /**
   * Provides the Package Extractor used to know the application id.
   * @return The PackageExtractor.
   */
  public static PackageExtractor providePackageExtractor() {
    return mProvider.providePackageExtractor()
  }

  /**
   * Provides the Android plugin's Extension. Used to know the flavors and build types created by
   * the Android plugin.
   * @return The Android plugin's Extension.
   */
  public static BaseExtension provideAndroidExtension() {
    return mProvider.provideAndroidExtension()
  }

  /**
   * Provides the Configurations Container of the Project. Used by the {@link ConfigurationManager}.
   * @return The Configurations Container.
   */
  public static ConfigurationContainer provideConfigurations() {
    return mProvider.provideConfigurations()
  }

  /**
   * Provides the BootClasspath for the tests to run.
   * @return The BootClasspath.
   */
  public static String provideBootClasspath() {
    return mProvider.provideBootClasspath()
  }

  /**
   * Provides the Variants of the Android plugin.
   * @return The Variants.
   */
  public static DefaultDomainObjectSet<BaseVariant> provideVariants() {
    return mProvider.provideVariants()
  }

  /**
   * Provides the Report Destination Directory.
   * @return The Report Destination Directory.
   */
  public static File provideReportDestinationDir() {
    return mProvider.provideReportDestinationDir()
  }
}
