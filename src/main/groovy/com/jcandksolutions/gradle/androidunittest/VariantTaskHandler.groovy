package com.jcandksolutions.gradle.androidunittest

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test

import static com.jcandksolutions.gradle.androidunittest.Logger.log

/**
 * Class that handles the creation of the tasks that each variant needs*/
class VariantTaskHandler {
  private VariantWrapper variant
  private String bootClasspath
  private Project project
  private String packageName
  private Task testClassesTask

  public VariantTaskHandler(VariantWrapper variant, Project project, String bootClasspath, String packageName, Task testClassesTask) {
    this.variant = variant
    this.project = project
    this.bootClasspath = bootClasspath
    this.packageName = packageName
    this.testClassesTask = testClassesTask
  }

  /**
   * Configures the classes task which handles the java compilation of tests and the processing of resources
   * @return the classes task
   */
  private Task configureClassesTask() {
    Task classesTask = project.tasks.getByName(variant.sourceSet.classesTaskName)
    log("classTask: $classesTask.name")
    // Clear out the group/description of the classes plugin so it's not top-level.
    classesTask.group = null
    classesTask.description = null
    return classesTask
  }

  /**
   * Configures the compileJava task that compiles the tests. Not to confuse with compileTestJava
   * @return the compileJava task
   */
  private JavaCompile configureTestCompileTask() {
    JavaCompile testCompileTask = project.tasks.getByName(variant.sourceSet.compileJavaTaskName) as JavaCompile
    testCompileTask.dependsOn(variant.androidCompileTask)
    testCompileTask.group = null
    testCompileTask.description = null
    testCompileTask.classpath = variant.classpath
    testCompileTask.source = variant.sourceSet.java
    testCompileTask.destinationDir = variant.compileDestinationDir
    testCompileTask.options.bootClasspath = bootClasspath
    return testCompileTask
  }

  /**
   * Creates a task that copies the merged resources the android plugin generated to a dir for the tests to use.
   * @return the Copy task.
   */
  private Copy createResourcesCopyTask() {
    Copy resourcesCopyTask = project.tasks.create(variant.resourcesCopyTaskName, Copy)
    resourcesCopyTask.from(variant.realMergedResourcesDir)
    resourcesCopyTask.into(variant.mergedResourcesDir)
    return resourcesCopyTask
  }

  /**
   * Creates and configures the test task that runs the tests
   * @return the test task
   */
  public Test createTestTask() {
    Test testTask = project.tasks.create("test$variant.completeName", Test)
    Task classesTask = configureClassesTask()
    //make the test depend on the classesTask that handles the compilation and resources of tests
    testTask.dependsOn(classesTask)
    testClassesTask.dependsOn(classesTask)
    //Clear the inputs because JavaBasePlugin adds an empty dir which makes it crash.
    testTask.inputs.sourceFiles.from.clear()
    JavaCompile testCompileTask = configureTestCompileTask()
    //Add the same sources of testCompile to the test task. not needed really
    testTask.inputs.source(testCompileTask.source)
    Copy copyTask = createResourcesCopyTask()
    Task processTestResourcesTask = project.tasks.getByName(variant.processResourcesTaskName)
    processTestResourcesTask.dependsOn(copyTask)
    testTask.classpath = variant.runPath.plus(project.files(bootClasspath))
    //set the location of the class files of the tests to run
    testTask.testClassesDir = testCompileTask.destinationDir
    testTask.group = JavaBasePlugin.VERIFICATION_GROUP
    testTask.description = "Run unit tests for Build '$variant.completeName'."
    double version = Double.parseDouble(project.gradle.gradleVersion)
    //configure the report directory depending on gradle version
    if (version >= 1.7) {
      testTask.reports.html.destination = project.file("$project.buildDir${File.separator}test-report${File.separator}$variant.dirName")
    } else {
      testTask.testReportDir = project.file("$project.buildDir${File.separator}test-report${File.separator}$variant.dirName")
    }
    //Include all the class files that end in Test
    testTask.scanForTestClasses = false
    String pattern = System.properties.getProperty("test.single")
    String pattern2 = System.properties.getProperty("test${variant.completeName}.single")
    if (pattern != null) {
      testTask.include("**${File.separator}${pattern}.class")
    }
    if (pattern2 != null) {
      testTask.include("**${File.separator}${pattern2}.class")
    }
    if (pattern == null && pattern2 == null) {
      testTask.include("**${File.separator}*Test.class")
    }
    // Add the path to the merged manifest, resources and assets as well as the main package name as system properties.
    testTask.systemProperties['android.manifest'] = variant.mergedManifest
    testTask.systemProperties['android.resources'] = variant.mergedResourcesDir
    testTask.systemProperties['android.assets'] = variant.mergedAssetsDir
    testTask.systemProperties['android.package'] = packageName
    return testTask
  }
}

