package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.BaseExtension
import com.android.builder.core.DefaultBuildType
import com.android.builder.core.DefaultProductFlavor

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.logging.Logger

/**
 * Class that manages the creation of the Configurations for the different source sets.
 */
public class ConfigurationManager {
  public static final String TEST_COMPILE = 'testCompile'
  private final BaseExtension mExtension
  private final ConfigurationContainer mConfigurations
  private final Logger mLogger
  /**
   * Instantiates a ConfigurationManager.
   * @param extension The AndroidExtension.
   * @param configurations The Configurations of the project.
   * @param logger The Logger.
   */
  public ConfigurationManager(BaseExtension extension, ConfigurationContainer configurations, Logger logger) {
    mExtension = extension
    mConfigurations = configurations
    mLogger = logger
  }

  /**
   * Creates new test configurations for each flavor so the user can set dependencies for the
   * different source sets.
   */
  public void createNewConfigurations() {
    mLogger.info("----------------------------------------")
    mLogger.info("Found configurations:")
    List<String> buildTypeConfigNames = buildTypeConfigList
    List<String> productFlavorConfigNames = flavorConfigList
    mLogger.info("----------------------------------------")
    mLogger.info("Creating new configurations:")
    createTestConfigurationsForBuildTypes(buildTypeConfigNames)
    createTestConfigurationsForFlavors(productFlavorConfigNames)
    createTestCompileTaskConfiguration()
  }

  private void createTestCompileTaskConfiguration() {
    Configuration testCompileTaskConfiguration = mConfigurations.create(TEST_COMPILE)
    testCompileTaskConfiguration.extendsFrom mConfigurations.getByName('compile')
    mLogger.info(TEST_COMPILE)
  }

  private void createTestConfigurationsForFlavors(final List<String> productFlavorConfigNames) {
    productFlavorConfigNames.each { String configName ->
      mLogger.info(configName)
      mConfigurations.create(configName)
    }
  }

  private void createTestConfigurationsForBuildTypes(final List<String> buildTypeConfigNames) {
    buildTypeConfigNames.each { String configName ->
      mLogger.info(configName)
      mConfigurations.create(configName)
    }
  }

  private List<String> getFlavorConfigList() {
    List<String> productFlavorConfigNames = []
    mExtension.productFlavors.each { DefaultProductFlavor productFlavor ->
      String compileConfigName = "${productFlavor.name}Compile"
      mLogger.info(compileConfigName)
      String configName = "test${compileConfigName.capitalize()}"
      productFlavorConfigNames.add(configName)
    }
    return productFlavorConfigNames
  }

  private List<String> getBuildTypeConfigList() {
    List<String> buildTypeConfigNames = []
    mExtension.buildTypes.each { DefaultBuildType buildType ->
      String compileConfigName = "${buildType.name}Compile"
      mLogger.info(compileConfigName)
      String configName = "test${compileConfigName.capitalize()}"
      buildTypeConfigNames.add(configName)
    }
    return buildTypeConfigNames
  }
}
