package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BasePlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.api.LibraryVariant
import com.android.builder.core.DefaultBuildType
import com.android.builder.core.DefaultProductFlavor
import com.android.builder.core.VariantConfiguration
import com.android.builder.model.ArtifactMetaData

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestReport

import static Logger.log
import static Logger.logw
/**
 * Plugin implementation class */
class AndroidUnitTestPlugin implements Plugin<Project> {
  private static final String ARTIFACT_NAME = "_unit_test_"

  /**
   * Gets the package name from the android plugin default configuration or the manifest if not
   * defined.
   * @param project the project with the android plugin
   * @return the main package name
   */
  private static String getPackageName(BasePlugin androidPlugin) {
    String packageName = androidPlugin.defaultConfigData.productFlavor.applicationId
    if (packageName == null) {
      packageName = VariantConfiguration.getManifestPackage(androidPlugin.defaultConfigData.sourceSet.manifestFile)
    }
    log("main package: $packageName")
    return packageName
  }

  /**
   * Checks that the android plugin has been applied. android-library plugin is not supported. Read the Readme file for more info
   * @param project the project that has to have the android plugin
   */
  private static void assertAndroidPluginExists(Project project) {
    // Assert that the Android App Plugin has been applied
    if (!(project.plugins.withType(AppPlugin) || project.plugins.withType(LibraryPlugin))) {
      throw new IllegalStateException("The 'android' plugin is required.")
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

  private static Task createTestClassesTask(Project project) {
    Task task = project.tasks.create("testClasses")
    task.description = 'Assembles the test classes directory.'
    return task
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
  private static void createNewConfigurations(Project project, BasePlugin androidPlugin) {
    //Here we will save the list of available android configurations
    List<String> buildTypeConfigNames = []
    List<String> productFlavorConfigNames = []
    log("----------------------------------------")
    log("Found configurations:")
    androidPlugin.extension.buildTypes.each { DefaultBuildType buildType ->
      String compileConfigName = "${buildType.name}Compile"
      log(compileConfigName)
      String configName = "test${compileConfigName.capitalize()}"
      buildTypeConfigNames.add(configName)
    }

    androidPlugin.extension.productFlavors.each { DefaultProductFlavor productFlavor ->
      String compileConfigName = "${productFlavor.name}Compile"
      log(compileConfigName)
      String configName = "test${compileConfigName.capitalize()}"
      productFlavorConfigNames.add(configName)
    }

    log("----------------------------------------")
    log("Creating new configurations:")

    buildTypeConfigNames.each { String configName ->
      log(configName)
      project.configurations.create(configName)
    }

    productFlavorConfigNames.each { String configName ->
      log(configName)
      project.configurations.create(configName)
    }

    createTestCompileTaskConfiguration(project)
  }

  /**
   * Applies the plugin to the project
   * @param project the project to apply the plugin to
   */
  public void apply(Project project) {
    assertAndroidPluginExists(project)
    Logger.initialize(project.logger)
    project.extensions.create("androidUnitTest", AndroidUnitTestPluginExtension)

    BasePlugin androidPlugin = project.plugins.withType(AppPlugin) ?
        project.plugins.withType(AppPlugin).toList()[0] :
        project.plugins.withType(LibraryPlugin).toList()[0]

    androidPlugin.registerArtifactType(ARTIFACT_NAME, true, ArtifactMetaData.TYPE_JAVA)

    createNewConfigurations(project, androidPlugin)
    //The classpath of the android platform
    String bootClasspath = androidPlugin.getBootClasspath().join(File.pathSeparator)
    String packageName = getPackageName(androidPlugin)
    TestReport testReportTask = createTestReportTask(project)
    Task testClassesTask = createTestClassesTask(project)
    //we use "all" instead of "each" because this set is empty until after project evaluated
    //with "all" it will execute the closure when the variants are getting created

    def variants = (androidPlugin instanceof AppPlugin) ?
        ((AppExtension) androidPlugin.extension).applicationVariants :
        ((LibraryExtension) androidPlugin.extension).libraryVariants
    
    if (androidPlugin instanceof LibraryPlugin && project.androidUnitTest.testReleaseBuildType) {
      logw("'testReleaseBuildType' is not supported on library projects")
    }

    variants.all { BaseVariant variant ->
      log("----------------------------------------")
      if (variant.buildType.debuggable || project.androidUnitTest.testReleaseBuildType) {

        if (variant instanceof LibraryVariant && variant.testVariant == null) {
          // Can't test a library project if there is no test variant
          return
        }

        VariantWrapper variantWrapper = new VariantWrapper(variant, project)
        VariantTaskHandler variantTaskHandler = new VariantTaskHandler(variantWrapper, project, bootClasspath, packageName, testClassesTask)
        Test variantTestTask = variantTaskHandler.createTestTask()
        testReportTask.reportOn(variantTestTask)

        androidPlugin.registerJavaArtifact(
            ARTIFACT_NAME,
            variantWrapper.baseVariant,
            variantWrapper.sourceSet.compileJavaTaskName,
            variantWrapper.sourceSet.compileJavaTaskName,
            variantWrapper.configuration,
            variantWrapper.compileDestinationDir,
            new TestSourceProvider(variantWrapper)
        )
      } else {
        log("skipping non-debuggable variant: ${variant.name}")
      }
    }
    log("----------------------------------------")
    log("Applied plugin")
  }
}
