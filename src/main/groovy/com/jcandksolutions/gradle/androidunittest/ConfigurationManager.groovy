package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.BaseExtension
import com.android.builder.core.DefaultBuildType
import com.android.builder.core.DefaultProductFlavor

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer

import static com.jcandksolutions.gradle.androidunittest.Logger.logi

/**
 * Class that manages the creation of the Configurations for the different source sets.
 */
public class ConfigurationManager {
  public static final String TEST_COMPILE = 'testCompile'
  private BaseExtension mExtension = DependencyInjector.provideAndroidExtension()
  private ConfigurationContainer mConfigurations = DependencyInjector.provideConfigurations()
  /**
   * Creates new test configurations for each flavor so the user can set dependencies for the
   * different source sets.
   */
  public void createNewConfigurations() {
    logi("----------------------------------------")
    logi("Found configurations:")
    List<String> buildTypeConfigNames = getBuildTypeConfigList()
    List<String> productFlavorConfigNames = getFlavorConfigList()
    logi("----------------------------------------")
    logi("Creating new configurations:")
    createTestConfigurationsForBuildTypes(buildTypeConfigNames)
    createTestConfigurationsForFlavors(productFlavorConfigNames)
    createTestCompileTaskConfiguration()
  }

  private void createTestCompileTaskConfiguration() {
    Configuration testCompileTaskConfiguration = mConfigurations.create(TEST_COMPILE)
    testCompileTaskConfiguration.extendsFrom mConfigurations.getByName('compile')
    logi(TEST_COMPILE)
  }

  private void createTestConfigurationsForFlavors(final List<String> productFlavorConfigNames) {
    productFlavorConfigNames.each { String configName ->
      logi(configName)
      mConfigurations.create(configName)
    }
  }

  private void createTestConfigurationsForBuildTypes(final List<String> buildTypeConfigNames) {
    buildTypeConfigNames.each { String configName ->
      logi(configName)
      mConfigurations.create(configName)
    }
  }

  private List<String> getFlavorConfigList() {
    List<String> productFlavorConfigNames = []
    mExtension.productFlavors.each { DefaultProductFlavor productFlavor ->
      String compileConfigName = "${productFlavor.name}Compile"
      logi(compileConfigName)
      String configName = "test${compileConfigName.capitalize()}"
      productFlavorConfigNames.add(configName)
    }
    return productFlavorConfigNames
  }

  private List<String> getBuildTypeConfigList() {
    List<String> buildTypeConfigNames = []
    mExtension.buildTypes.each { DefaultBuildType buildType ->
      String compileConfigName = "${buildType.name}Compile"
      logi(compileConfigName)
      String configName = "test${compileConfigName.capitalize()}"
      buildTypeConfigNames.add(configName)
    }
    return buildTypeConfigNames
  }
}
