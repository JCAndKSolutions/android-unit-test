package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.android.builder.BuilderConstants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestReport

import static Logger.log

/**
 * Plugin implementation class */
class AndroidUnitTestPlugin implements Plugin<Project> {
  /**
   * Gets the package name from the android plugin default configuration
   * @param project the project with the android plugin
   * @return the main package name
   */
  private static String getPackageName(Project project) {
    String packageName = project.android.defaultConfig.packageName
    log("main package: $packageName")
    return packageName
  }

  /**
   * Checks that the android plugin has been applied. android-library plugin is not supported. Read the Readme file for more info
   * @param project the project that has to have the android plugin
   */
  private static void assertAndroidPluginExists(Project project) {
    // Assert that the Android App Plugin has been applied
    if (!project.plugins.withType(AppPlugin)) {
      throw new IllegalStateException("The 'android' plugin is required.")
    } else if (project.plugins.hasPlugin(LibraryPlugin)) {
      throw new IllegalStateException("'android-library' plugin is not supported. Create a dummy App Project that invokes the library to test it")
    }
  }

  /**
   * Create the report task that calls each test task and generates the reports afterwards
   * @param project the project to add the new report task to.
   * @return the test report task.
   */
  private static TestReport createTestReportTask(Project project) {
    TestReport testReportTask = project.tasks.create("test", TestReport)
    log("Created test task")
    testReportTask.destinationDir = project.file("$project.buildDir${File.separator}test-report")
    testReportTask.description = 'Runs all unit tests.'
    testReportTask.group = JavaBasePlugin.VERIFICATION_GROUP
    //Make the check task call this report task which will call the test tasks.
    project.tasks.check.dependsOn(testReportTask)
    return testReportTask
  }

  /**
   * Create the master testCompile configuration for dependencies that all tests have
   * @param project the project to add the configuration to.
   * @return the master configuration for tests "testCompile"
   */
  private static Configuration createTestCompileTaskConfiguration(Project project) {
    Configuration testCompileTaskConfiguration = project.configurations.create('testCompile')
    testCompileTaskConfiguration.extendsFrom project.configurations.getByName('compile')
    return testCompileTaskConfiguration
  }

  /**
   * Creates new test configurations for each flavor so the user can set dependencies for the different tests.
   * @param project the project to add the new configurations to.
   * @return the master configuration for tests "testCompile"
   */
  private static Configuration createNewConfigurations(Project project) {
    //Here we will save the list of available android configurations
    List<String> newConfigs = new ArrayList<String>()
    log("----------------------------------------")
    log("Found configurations:")
    for (Configuration configuration in project.configurations) {
      String configurationName = configuration.name
      log("$configurationName")
      //save only the configurations that end in Compile and that don't end in instrumentTest
      if (configurationName.endsWith("Compile") && !configurationName.startsWith("instrumentTest")) {
        //Extract the flavor name and append test at the beginning and Compile at the end
        newConfigs.add("test" + configurationName.substring(0, configurationName.length() - 7).capitalize() + "Compile")
      }
    }
    log("----------------------------------------")
    log("Creating new configurations:")
    for (String configName in newConfigs) {
      log("$configName")
      project.configurations.create(configName)
    }
    return createTestCompileTaskConfiguration(project)
  }

  /**
   * Applies the plugin to the project
   * @param project the project to apply the plugin to
   */
  public void apply(Project project) {
    assertAndroidPluginExists(project)
    Logger.initialize(project.logger)
    Configuration testCompileTaskConfiguration = createNewConfigurations(project)
    //The classpath of the android platform
    String bootClasspath = project.plugins.withType(AppPlugin).toList().get(0).getRuntimeJarList().join(File.pathSeparator)
    String packageName = getPackageName(project)
    TestReport testReportTask = createTestReportTask(project)
    //we use "all" instead of "each" because this set is empty until after project evaluated
    //with "all" it will execute the closure when the variants are getting created
    project.extensions.getByName('android').applicationVariants.all { variant ->
      log("----------------------------------------")
      if (variant.buildType.name.equals(BuilderConstants.RELEASE)) {
        log("Skipping release build type.")
        return;
      }
      VariantWrapper variantWrapper = new VariantWrapper(variant, project, testCompileTaskConfiguration)
      VariantTaskHandler variantTaskHandler = new VariantTaskHandler(variantWrapper, project, bootClasspath, packageName)
      Test variantTestTask = variantTaskHandler.createTestTask()
      testReportTask.reportOn(variantTestTask)
    }
    log("----------------------------------------")
    log("Applied plugin")
  }
}
