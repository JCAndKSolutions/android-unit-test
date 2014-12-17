package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.api.TestVariant

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.collections.SimpleFileCollection
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet

/**
 * Base class that wraps the info of the variant for easier retrieval of the actual data needed.
 */
public abstract class VariantWrapper {
  protected final Project mProject
  protected final ConfigurationContainer mConfigurations
  protected final BaseVariant mVariant
  protected final TestVariant mTestVariant
  protected final Logger mLogger
  protected Configuration mConfiguration
  private final String mBootClasspath
  private FileCollection mClasspath
  private File mCompileDestinationDir
  private GString mCompleteName
  private SourceSet mSourceSet
  private FileCollection mRunPath
  private File mMergedResourcesDir
  private File mMergedManifest
  private File mMergedAssetsDir
  private List<String> mFlavorList
  private String mFlavorName
  private String mBuildTypeName
  private FileCollection mTestClasspath
  private File mVariantReportDestination
  /**
   * Instantiates a new VariantWrapper.
   * @param variant The Variant to wrap.
   * @param project The project.
   * @param configurations The Project Configurations.
   * @param bootClasspath The bootClasspath.
   * @param logger The Logger.
   * @param testVariant The Test Variant of the variant. Can be null for library projects.
   */
  public VariantWrapper(BaseVariant variant, Project project, ConfigurationContainer configurations, String bootClasspath, Logger logger, TestVariant testVariant) {
    mVariant = variant
    mProject = project
    mConfigurations = configurations
    mBootClasspath = bootClasspath
    mTestVariant = testVariant
    mLogger = logger
  }

  /**
   * Configures the SourceSet with the Sourcepath, Classpath and Runpath.
   * @param config The SourceSets configurations to read from.
   */
  public void configureSourceSet(SourceSetCreatorMap config) {
    List<File> sources = []
    List<File> resources = []
    resources.addAll(extractSourceDirs(config["test"], "test", "resources"))
    sources.addAll(extractSourceDirs(config["test"], "test", "java"))
    resources.addAll(extractSourceDirs(config["test${buildTypeName}"], "test${buildTypeName}", "resources"))
    sources.addAll(extractSourceDirs(config["test${buildTypeName}"], "test${buildTypeName}", "java"))
    if (hasFlavors) {
      resources.addAll(extractSourceDirs(config["test${flavorName}"], "test${flavorName}", "resources"))
      sources.addAll(extractSourceDirs(config["test${flavorName}"], "test${flavorName}", "java"))
      resources.addAll(extractSourceDirs(config["test${flavorName}${buildTypeName}"], "test${flavorName}${buildTypeName}", "resources"))
      sources.addAll(extractSourceDirs(config["test${flavorName}${buildTypeName}"], "test${flavorName}${buildTypeName}", "java"))
      flavorList.each { String flavor ->
        resources.addAll(extractSourceDirs(config["test${flavor}"], "test${flavor}", "resources"))
        sources.addAll(extractSourceDirs(config["test${flavor}"], "test${flavor}", "java"))
        resources.addAll(extractSourceDirs(config["test${flavor}${buildTypeName}"], "test${flavor}${buildTypeName}", "resources"))
        sources.addAll(extractSourceDirs(config["test${flavor}${buildTypeName}"], "test${flavor}${buildTypeName}", "java"))
      }
    }
    sourceSet.resources.srcDirs = resources
    sourceSet.java.srcDirs = sources
    sourceSet.compileClasspath = classpath
    sourceSet.runtimeClasspath = runPath
    //Add this SourceSet to the classes task for compilation
    sourceSet.compiledBy(sourceSet.classesTaskName)
  }

  /**
   * Extracts the list of files of the source dir.
   * @param config The configuration to extract from.
   * @param key The configuration name. One of the different combinations from the variant.
   * @param type The type of info to extract, it can be {@code resources} or {@code java}.
   * @return A list of files of the source dir.
   */
  protected List<File> extractSourceDirs(SourceSetConfig config, String key, String type) {
    List<File> ret
    if (config["$type"].overWritten) {
      ret = []
    } else {
      ret = [mProject.file("src${File.separator}${key}${File.separator}${type}")]
    }
    config["$type"].srcDirs.each { String dir ->
      ret.add(mProject.file(dir))
    }
    return ret
  }

  /**
   * Retrieves whether the variant has flavors or just build type.
   * @return {@code true} if the variant has one or more flavors, {@code false} otherwise.
   */
  protected boolean isHasFlavors() {
    return mVariant.productFlavors.size() > 0
  }

  /**
   * Retrieves the dir name of the variant.<br/>
   * For example: freeBeta/debug.
   * @return The dir name of the variant.
   */
  public String getDirName() {
    return mVariant.dirName
  }

  /**
   * Retrieves the path of the merged manifest of the variant.
   * @return The path.
   */
  public File getMergedManifest() {
    if (mMergedManifest == null) {
      mMergedManifest = mVariant.outputs.first().processManifest.manifestOutputFile
    }
    return mMergedManifest
  }

  /**
   * Retrieves the path string where the resources are merged by the Android plugin.
   * @return The path string.
   */
  public File getMergedResourcesDir() {
    if (mMergedResourcesDir == null) {
      mMergedResourcesDir = mProject.file("$mProject.buildDir${File.separator}intermediates${File.separator}res${File.separator}$mVariant.dirName")
    }
    return mMergedResourcesDir
  }

  /**
   * Retrieves the output dir of the mergeAssets task. This has the merged assets of the variant.
   * @return The merged assets dir.
   */
  public File getMergedAssetsDir() {
    if (mMergedAssetsDir == null) {
      mMergedAssetsDir = mVariant.mergeAssets.outputDir
    }
    return mMergedAssetsDir
  }

  /**
   * Retrieves a configuration for the test variant based on the list of configurations that this
   * variant uses. Which are the build type configuration and a configuration for each flavor.<br>
   * For example: testDebugCompile, testFreeCompile and testBetaCompile.
   * @return The configuration for the test variant.
   */
  public Configuration getConfiguration() {
    if (mConfiguration == null) {
      //we create the sourceset first otherwise the needed configurations won't be available for the compile classpath
      sourceSet
      ArrayList<GString> configurationNames = ["${ConfigurationManager.TEST_COMPILE}"]
      configurationNames.add("test${buildTypeName}Compile")
      flavorList.each { String flavor ->
        configurationNames.add("test${flavor}Compile")
        mLogger.info("Reading configuration: test${flavor}Compile")
      }
      mConfiguration = mConfigurations.create("_test${completeName.capitalize()}Compile")
      configurationNames.each { configName ->
        mConfiguration.extendsFrom(mConfigurations.findByName(configName))
      }
    }
    return mConfiguration
  }

  /**
   * Retrieves the Classpath used to compile the tests which includes the testCompile configuration,
   * the app's class files, the app's classpath and a configuration for each flavor.
   * @return The classpath.
   */
  public FileCollection getClasspath() {
    if (mClasspath == null) {
      mClasspath = configuration.plus(mProject.files(mVariant.javaCompile.destinationDir, mVariant.javaCompile.classpath))
    }
    return mClasspath
  }

  /**
   * Retrieves the classpath for the Test task. This includes the runPath plus the bootClasspath.
   * @return The testClasspath.
   */
  public FileCollection getTestClasspath() {
    if (mTestClasspath == null) {
      mTestClasspath = runPath.plus(mProject.files(mBootClasspath))
    }
    return mTestClasspath
  }

  /**
   * Retrieves the path for the destination of the test's compilation.<br/>
   * For example: build/test-classes/freeBeta/debug.
   * @return The destination dir.
   */
  public File getCompileDestinationDir() {
    if (mCompileDestinationDir == null) {
      mCompileDestinationDir = new File("$mProject.buildDir${File.separator}test-classes${File.separator}$mVariant.dirName")
    }
    return mCompileDestinationDir
  }

  /**
   * Retrieves the complete name of the variant which is the concatenation of the flavors plus the buildType.
   * For example: FreeBetaDebug.
   * @return The complete name.
   */
  public GString getCompleteName() {
    if (mCompleteName == null) {
      mCompleteName = "$flavorName$buildTypeName"
    }
    return mCompleteName
  }

  /**
   * Retrieves the build type of the variant.<br/>
   * For example: Debug.
   * @return The build type.
   */
  protected String getBuildTypeName() {
    if (mBuildTypeName == null) {
      mBuildTypeName = mVariant.buildType.name.capitalize()
    }
    return mBuildTypeName
  }

  /**
   * Retrieves a list of the flavors of the variant.
   * @return The list of flavors. Empty if no flavors defined.
   */
  protected List<String> getFlavorList() {
    if (mFlavorList == null) {
      mFlavorList = mVariant.productFlavors.collect { it.name.capitalize() }
      if (mFlavorList.empty) {
        mFlavorList = [""]
      }
    }
    return mFlavorList
  }

  /**
   * Retrieves the concatenated name of all the flavors.<br/>
   * For example: FreeBeta.
   * @return The flavor name.
   */
  protected String getFlavorName() {
    if (mFlavorName == null) {
      mFlavorName = flavorList.join("")
    }
    return mFlavorName
  }

  /**
   * Returns the compile task of the app's sources.
   * @return the compile task of the app's sources
   */
  public abstract Task getAndroidCompileTask();

  /**
   * Returns the test SourceSet for this variant.
   * @return The test SourceSet.
   */
  protected SourceSet getSourceSet() {
    if (mSourceSet == null) {
      JavaPluginConvention javaConvention = mProject.convention.getPlugin(JavaPluginConvention)
      mSourceSet = javaConvention.sourceSets.create("test$completeName")
    }
    return mSourceSet
  }

  /**
   * Retrieves the Runpath which includes the classpath, the processsed resources and the
   * destination dir of the compilation, that is, where the tests' class files are.
   * @return The Runpath.
   */
  protected FileCollection getRunPath() {
    if (mRunPath == null) {
      mRunPath = classpath.plus(mProject.files("$mProject.buildDir${File.separator}resources${File.separator}test$completeName")).plus(new SimpleFileCollection(compileDestinationDir))
    }
    return mRunPath
  }

  /**
   * Retrieves the Base variant of the Android plugin that this is wrapping.
   * @return The Base variant.
   */
  public BaseVariant getBaseVariant() {
    return mVariant;
  }

  /**
   * Retrieves the report destination dir where this variant's test results should go to.<br/>
   * For example: build/test-report/freeBeta/debug/.
   * @return The report destination.
   */
  public File getVariantReportDestination() {
    if (mVariantReportDestination == null) {
      mVariantReportDestination = mProject.file("$mProject.buildDir${File.separator}test-report${File.separator}$dirName")
    }
    return mVariantReportDestination
  }
}
