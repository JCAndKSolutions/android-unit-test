package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.collections.SimpleFileCollection
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.JavaCompile

import static com.jcandksolutions.gradle.androidunittest.Logger.log

/**
 * Class that handles the info of the variant for easier retrieval*/
public class VariantWrapper {
  private ApplicationVariant variant
  private FileCollection classpath
  private File compileDestinationDir
  private GString completeName
  private SourceSet sourceSet
  private FileCollection runpath
  private File mergedResourcesDir
  private File mergedManifest
  private File mergedAssetsDir
  private String resourcesCopyTaskName
  private String realMergedResourcesDir
  private String processResourcesTaskName

  VariantWrapper(ApplicationVariant variant, Project project, Configuration fatherConfiguration) {
    this.variant = variant
    mergedManifest = initMergedManifest(variant)
    List<String> flavorList = initFlavorList(variant)
    String flavorName = initFlavorName(flavorList)
    String buildType = initBuildType(variant)
    completeName = initCompleteName(flavorName, buildType)
    //we create the sourceset first otherwise the needed configurations won't be available for the compile classpath
    sourceSet = createSourceSet(project, completeName)
    mergedResourcesDir = initMergedResourcesDir(project, completeName)
    mergedAssetsDir = initMergedAssetsDir(variant)
    compileDestinationDir = initCompileDestinationDir(project, variant)
    List<GString> configurationName = initConfigurationNames(flavorList, buildType)
    classpath = initClasspath(fatherConfiguration, project, androidCompileTask, configurationName)
    runpath = initRunpath(project, classpath, compileDestinationDir, completeName)
    ArrayList<File> testSourcepath = initTestSourcepath(project, buildType, flavorName, flavorList)
    //now we can configure the sourceset
    configureSourceSet(project, sourceSet, testSourcepath, classpath, runpath)
    resourcesCopyTaskName = initResourcesCopyTaskName(completeName)
    realMergedResourcesDir = initRealMergedResourcesDir(project, variant)
    processResourcesTaskName = initProcessResourcesTaskName(completeName)
    log("build type: $buildType")
    log("flavors: $flavorName")
    log("variant name: $completeName")
    log("manifest: $mergedManifest")
    log("resources: $mergedResourcesDir")
    log("assets: $mergedAssetsDir")
  }

  /**
   * Creates the test's SourceSet that will be compiled.
   * @param project project to add the sourceSet to.
   * @param completeName name of the corresponding variant
   * @return the source set
   */
  private static SourceSet createSourceSet(Project project, GString completeName) {
    JavaPluginConvention javaConvention = project.convention.getPlugin(JavaPluginConvention)
    SourceSet sourceSet = javaConvention.sourceSets.create("test$completeName")
    return sourceSet
  }

  /**
   * Configures the SourceSet with the Sourcepath, Classpath and Runpath
   * @param project the project where the SourceSet is
   * @param sourceSet the SourceSet to configure
   * @param testsSourcePath the path where the test's sources are
   * @param compileClasspath the path where the necessary libraries are for compilation
   * @param runClasspath the path with the runtime and libraries for running the tests
   */
  private static void configureSourceSet(Project project, SourceSet sourceSet, ArrayList<File> testsSourcePath, FileCollection compileClasspath, FileCollection runClasspath) {
    //Add standard resources directory
    sourceSet.resources.srcDirs(project.file("src${File.separator}test${File.separator}resources"))
    sourceSet.java.srcDirs = testsSourcePath
    sourceSet.compileClasspath = compileClasspath
    sourceSet.runtimeClasspath = runClasspath
    //Add this SourceSet to the classes task for compilation
    sourceSet.compiledBy(sourceSet.classesTaskName)
  }

  /**
   * Creates the sourcepath for this variant. It includes the standard test dir that has tests for all flavors, a dir for the buildType tests,
   * a dir for each flavor in the variant and a dir for the variant. For example, the variant FreeBetaDebug will have the following dirs:<br>
   * - src/test/java (main test dir)
   * - src/testDebug/java (debug build type test dir)
   * - src/testFree/java (free flavor tests dir)
   * - src/testBeta/java (beta flavor tests dir)
   * - src/testFreeBeta/java (variant tests dir)
   *
   * @param project the project
   * @param buildType the build type of the variant (Debug, Release, ...)
   * @param flavorName the variant name (FreeBeta, PaidNormal, ...)
   * @param flavorList the flavors of the variant (Free, Beta, ...)
   * @return the sourcePath
   */
  private static ArrayList<File> initTestSourcepath(Project project, String buildType, String flavorName, List<String> flavorList) {
    ArrayList<File> testSourcepath = []
    testSourcepath.add(project.file("src${File.separator}test${File.separator}java"))
    testSourcepath.add(project.file("src${File.separator}test$buildType${File.separator}java"))
    testSourcepath.add(project.file("src${File.separator}test$flavorName${File.separator}java"))
    testSourcepath.add(project.file("src${File.separator}test$flavorName$buildType${File.separator}java"))
    flavorList.each { String flavor ->
      testSourcepath.add(project.file("src${File.separator}test$flavor${File.separator}java"))
      testSourcepath.add(project.file("src${File.separator}test$flavor$buildType${File.separator}java"))
    }
    return testSourcepath
  }

  /**
   * Creates the Runpath which includes the classpath, the processsed resources and the destination dir of the compilation, that is, where the tests' class files are.
   * @param project the project
   * @param classpath the classpath
   * @param compileDestinationDir the compilation destination dir
   * @param completeName the variant name to find the processed resources of the variant
   * @return the runpath
   */
  private static FileCollection initRunpath(Project project, FileCollection classpath, File compileDestinationDir, GString completeName) {
    return classpath.plus(project.files("$project.buildDir${File.separator}resources${File.separator}test$completeName")).plus(new SimpleFileCollection(compileDestinationDir))
  }

  /**
   * Creates the Classpath which includes the testCompile configuration, the app's class files, the app's classpath and a configuration for each flavor
   * @param fatherConfiguration the "testCompile" configuration for all tests
   * @param project the project
   * @param androidCompileTask the app's compilation task that has the app's classpath and class files.
   * @param configurationNames the different configuration names for each flavor.
   * @return the classpath
   */
  private static FileCollection initClasspath(Configuration fatherConfiguration, Project project, JavaCompile androidCompileTask, List<GString> configurationNames) {
    FileCollection classpath = fatherConfiguration.plus(project.files(androidCompileTask.destinationDir, androidCompileTask.classpath))
    configurationNames.each { GString configurationName ->
      classpath.plus(project.configurations.getByName(configurationName))
    }
    return classpath
  }

  /**
   * Creates the path for the destination of the test's compilation which is "$buildDir/test-classes/$variantDirName". For example: build/test-classes/freeBeta/debug
   * @param project the project
   * @param variant the variant
   * @return the destination dir.
   */
  private static File initCompileDestinationDir(Project project, ApplicationVariant variant) {
    return new File("$project.buildDir${File.separator}test-classes${File.separator}$variant.dirName")
  }

  /**
   * Creates the name of the task that process the test resources
   * @param completeName the name of the variant
   * @return the name of the task that process the test resources
   */
  private static String initProcessResourcesTaskName(String completeName) {
    return "processTest${completeName}Resources"
  }

  /**
   * Creates a list of the configuration names that this variant uses. which is the build tpe configuration and a configuration for each flavor.<br>
   * For example: testDebugCompile, testFreeCompile and testBetaCompile
   * @param flavorList the flavor list
   * @return the list of configuration names.
   */
  private static ArrayList<GString> initConfigurationNames(List<String> flavorList, String buildType) {
    ArrayList<GString> configurationNames = ["test${buildType}Compile"]
    flavorList.each { String flavor ->
      configurationNames.add("test${flavor}Compile")
      log("Reading configuration: test${flavor}Compile")
    }
    return configurationNames
  }

  /**
   * Get the output dir of the mergeAssets task. This has the merged assets of the variant.
   * @param variant the variant
   * @return the merged assets dir
   */
  private static File initMergedAssetsDir(ApplicationVariant variant) {
    return variant.mergeAssets.outputDir
  }

  /**
   * Gets the path where the merged resources are copied. Usually in build/test-resources/$variantName/res
   * @param project the project
   * @param completeName variant name
   * @return the dir with the copied merged resources
   */
  private static File initMergedResourcesDir(Project project, GString completeName) {
    return project.file("$project.buildDir${File.separator}test-resources${File.separator}$completeName${File.separator}res")
  }

  /**
   * Creates the path where the resources are merged by android plugin
   * @param project the project
   * @param variant the variant
   * @return the path where the resources are merged by android plugin
   */
  private static String initRealMergedResourcesDir(Project project, ApplicationVariant variant) {
    return "$project.buildDir${File.separator}intermediates${File.separator}res${File.separator}$variant.dirName"
  }

  /**
   * initiates the ResourcesCopyTask name
   * @return the ResourcesCopyTaskName
   */
  private static String initResourcesCopyTaskName(String completeName) {
    return "copy${completeName}TestResources"
  }

  /**
   * The complete name of the variant which is the concatenation of the flavors plus the buildType. For example FreeBetaDebug
   * @param flavorName the concatenated flavor names
   * @param buildType the build type
   * @return the complete name
   */
  private static GString initCompleteName(String flavorName, String buildType) {
    return "$flavorName$buildType"
  }

  /**
   * Creates the concatenated name of all the flavors. Example FreeBeta
   * @param flavorList the flavos of the variant.
   * @return the concatenation of the flavors.
   */
  private static String initFlavorName(List<String> flavorList) {
    return flavorList.join("")
  }

  /**
   * Creates a list of the flavors of the variant
   * @param variant the variant
   * @return the list of flavors. Empty if no flavors defined.
   */
  private static List<String> initFlavorList(ApplicationVariant variant) {
    List<String> flavorList = variant.productFlavors.collect { it.name.capitalize() }
    if (flavorList.empty) {
      flavorList = [""]
    }
    return flavorList
  }

  /**
   * Gets the path of the merged manifest of the variant
   * @param variant the variant
   * @return the path of the merged manifest of the variant
   */
  private static File initMergedManifest(ApplicationVariant variant) {
    return variant.processManifest.manifestOutputFile
  }

  /**
   * Gets the build type of the variant
   * @param variant the variant
   * @return the build type
   */
  private static String initBuildType(ApplicationVariant variant) {
    return variant.buildType.name.capitalize()
  }

  /**
   * Gets the dir name of the variant. Example: freeBeta/debug
   * @return the dir name of the variant.
   */
  public String getDirName() {
    return variant.dirName
  }

  /**
   * Returns the path of the merged manifest. See {@link #initMergedManifest(com.android.build.gradle.api.ApplicationVariant)}
   * @return the path of the merged manifest
   */
  public File getMergedManifest() {
    return mergedManifest
  }

  /**
   * Returns the path of the merged resources. See {@link #initMergedResourcesDir(org.gradle.api.Project, groovy.lang.GString)}
   * @return the path of the merged resources
   */
  public File getMergedResourcesDir() {
    return mergedResourcesDir
  }

  /**
   * Returns the path of the merged assets. See {@link #initMergedAssetsDir(com.android.build.gradle.api.ApplicationVariant)}
   * @return the path of the merged assets
   */
  public File getMergedAssetsDir() {
    return mergedAssetsDir
  }

  /**
   * Returns the classpath. See {@link #initClasspath(org.gradle.api.artifacts.Configuration, org.gradle.api.Project, org.gradle.api.tasks.compile.JavaCompile, java.util.List)}
   * @return the classpath
   */
  public FileCollection getClasspath() {
    return classpath
  }

  /**
   * Returns the compile destination dir. See {@link #initCompileDestinationDir(org.gradle.api.Project, com.android.build.gradle.api.ApplicationVariant)}
   * @return the compile destination dir.
   */
  public File getCompileDestinationDir() {
    return compileDestinationDir
  }

  /**
   * Returns the variant name. See {@link #initCompleteName(java.lang.String, java.lang.String)}
   * @return the variant name
   */
  public GString getCompleteName() {
    return completeName
  }

  /**
   * Returns the compile task of the app's sources
   * @return the compile task of the app's sources
   */
  public JavaCompile getAndroidCompileTask() {
    return variant.javaCompile
  }

  /**
   * Returns the SourceSet of this variant. See {@link #createSourceSet(org.gradle.api.Project, groovy.lang.GString)} and {@link #configureSourceSet(org.gradle.api.Project, org.gradle.api.tasks.SourceSet, java.util.ArrayList, org.gradle.api.file.FileCollection, org.gradle.api.file.FileCollection)}
   * @return the SourceSet of this variant
   */
  public SourceSet getSourceSet() {
    return sourceSet
  }

  /**
   * Returns the runpath. See {@link #initRunpath(org.gradle.api.Project, org.gradle.api.file.FileCollection, java.io.File, groovy.lang.GString)}
   * @return the runpath.
   */
  public FileCollection getRunpath() {
    return runpath
  }

  /**
   * Returns the resourcesCopyTask name. See {@link #initResourcesCopyTaskName(java.lang.String)}
   * @return the resourcesCopyTask name
   */
  public String getResourcesCopyTaskName() {
    return resourcesCopyTaskName
  }

  /**
   * Returns the real merged resources dir path. See {@link #initRealMergedResourcesDir(org.gradle.api.Project, com.android.build.gradle.api.ApplicationVariant)}
   * @return the real merged resources dir path
   */
  public String getRealMergedResourcesDir() {
    return realMergedResourcesDir
  }

  /**
   * Returns the ProcessResourcesTask name. See {@link #initProcessResourcesTaskName(java.lang.String)}
   * @return the ProcessResourcesTask name
   */
  public String getProcessResourcesTaskName() {
    return processResourcesTaskName
  }
}
