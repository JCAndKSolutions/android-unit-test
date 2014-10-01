package com.jcandksolutions.gradle.androidunittest

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.collections.DefaultConfigurableFileCollection
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.reporting.DirectoryReport
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskInputs
import org.gradle.api.tasks.compile.CompileOptions
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.TestReport
import org.gradle.api.tasks.testing.TestTaskReports
import org.junit.Before
import org.junit.Test

import static org.fest.assertions.api.Assertions.assertThat
import static org.fest.assertions.api.Assertions.entry
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

public class TaskManagerTest {
  private static final String FLAVOR_DEBUG = "FlavorDebug"
  private TaskManager mTarget
  private VariantWrapper mVariant
  private Task mClassesTask
  private org.gradle.api.tasks.testing.Test mTestTask
  private Task mTestClassesTask
  private Set<Object> mFrom
  private JavaCompile mTestCompileTask
  private Task mAndroidCompileTask
  private FileCollection mClasspath
  private SourceDirectorySet mJava
  private File mCompileDestinationDir
  private CompileOptions mOptions
  private TaskInputs mInputs
  private FileTree mSource
  private Copy mResourcesCopyTask
  private File mMergedResourcesDir
  private Task mProcessResourcesTask
  private Task mProcessTestResourcesTask
  private FileCollection mTestClasspath
  private DirectoryReport mHTML
  private File mVariantReportDestination
  private File mMergedManifest
  private File mMergedAssetsDir
  private PackageExtractor mPackageExtractor
  private HashMap<String, Object> mSystemProperties
  private TestReport mTestReportTask
  private File mReportDestinationDir
  private Task mCheckTask
  private MockProvider mProvider

  @Before
  public void setUp() {
    mProvider = new MockProvider()
    Project project = mProvider.provideProject()
    mPackageExtractor = mProvider.providePackageExtractor()
    mReportDestinationDir = mProvider.provideReportDestinationDir()
    TaskContainer tasks = mock(TaskContainer.class)
    mTestTask = mock(org.gradle.api.tasks.testing.Test.class)
    mTestClassesTask = mock(Task.class)
    mClassesTask = mock(Task.class)
    mVariant = mock(VariantWrapper.class)
    SourceSet sourceSet = mock(SourceSet.class)
    mInputs = mock(TaskInputs.class)
    DefaultConfigurableFileCollection files = mock(DefaultConfigurableFileCollection.class)
    mFrom = mock(Set.class)
    mAndroidCompileTask = mock(Task.class)
    mTestCompileTask = mock(JavaCompile.class)
    mClasspath = mock(FileCollection.class)
    mJava = mock(SourceDirectorySet.class)
    mCompileDestinationDir = new File("destinationDir")
    mOptions = mock(CompileOptions.class)
    mSource = mock(FileTree.class)
    mResourcesCopyTask = mock(Copy.class)
    mMergedResourcesDir = new File("mergedResourcesDir")
    mProcessResourcesTask = mock(Task.class)
    mProcessTestResourcesTask = mock(Task.class)
    mTestClasspath = mock(FileCollection.class)
    TestTaskReports reports = mock(TestTaskReports.class)
    mHTML = mock(DirectoryReport.class)
    mVariantReportDestination = new File("reportDestination")
    mSystemProperties = new HashMap<>()
    mMergedManifest = new File("mergedManifest")
    mMergedAssetsDir = new File("mergedAssetsDir")
    mTestReportTask = mock(TestReport.class)
    mCheckTask = mock(Task.class)
    when(tasks.create("testFlavorDebug", org.gradle.api.tasks.testing.Test)).thenReturn(mTestTask)
    when(tasks.create("testClasses")).thenReturn(mTestClassesTask)
    when(tasks.create("test", TestReport)).thenReturn(mTestReportTask)
    when(tasks.create("resourcesCopyTaskName", Copy.class)).thenReturn(mResourcesCopyTask)
    when(tasks.getByName("classesTaskName")).thenReturn(mClassesTask)
    when(tasks.getByName("compileJavaTaskName")).thenReturn(mTestCompileTask)
    when(tasks.getByName("processResourcesTaskName")).thenReturn(mProcessResourcesTask)
    when(tasks.getByName("processTestResourcesTaskName")).thenReturn(mProcessTestResourcesTask)
    when(tasks.getByName("check")).thenReturn(mCheckTask)
    when(project.tasks).thenReturn(tasks)
    when(mVariant.sourceSet).thenReturn(sourceSet)
    when(mVariant.completeName).thenReturn("$FLAVOR_DEBUG")
    when(mVariant.classpath).thenReturn(mClasspath)
    when(mVariant.androidCompileTask).thenReturn(mAndroidCompileTask)
    when(mVariant.compileDestinationDir).thenReturn(mCompileDestinationDir)
    when(mVariant.resourcesCopyTaskName).thenReturn("resourcesCopyTaskName")
    when(mVariant.realMergedResourcesDir).thenReturn("realMergedResourcesDir")
    when(mVariant.mergedResourcesDir).thenReturn(mMergedResourcesDir)
    when(mVariant.processResourcesTaskName).thenReturn("processResourcesTaskName")
    when(mVariant.processTestResourcesTaskName).thenReturn("processTestResourcesTaskName")
    when(mVariant.testClasspath).thenReturn(mTestClasspath)
    when(mVariant.variantReportDestination).thenReturn(mVariantReportDestination)
    when(mVariant.mergedManifest).thenReturn(mMergedManifest)
    when(mVariant.mergedAssetsDir).thenReturn(mMergedAssetsDir)
    when(sourceSet.classesTaskName).thenReturn("classesTaskName")
    when(sourceSet.java).thenReturn(mJava)
    when(sourceSet.compileJavaTaskName).thenReturn("compileJavaTaskName")
    when(mTestTask.inputs).thenReturn(mInputs)
    when(mTestTask.reports).thenReturn(reports)
    when(mTestTask.systemProperties).thenReturn(mSystemProperties)
    when(reports.html).thenReturn(mHTML)
    when(mInputs.sourceFiles).thenReturn(files)
    when(files.from).thenReturn(mFrom)
    when(mTestCompileTask.options).thenReturn(mOptions)
    when(mTestCompileTask.source).thenReturn(mSource)
    when(mTestCompileTask.destinationDir).thenReturn(mCompileDestinationDir)
    when(mPackageExtractor.packageName).thenReturn("packageName")
    mTarget = new TaskManager(mProvider.provideProject(), mProvider.provideBootClasspath(), mProvider.providePackageExtractor(), mReportDestinationDir, mProvider.provideLogger())
  }

  @Test
  public void testCreateTestTask() {
    mTarget.createTestTask(mVariant)
    verify(mClassesTask).group = null
    verify(mClassesTask).description = null
    verify(mTestTask).dependsOn(mClassesTask)
    verify(mTestClassesTask).description = "Assembles the test classes directory."
    verify(mTestClassesTask).dependsOn(mClassesTask)
    verify(mFrom).clear()
    verify(mTestCompileTask).dependsOn(mAndroidCompileTask)
    verify(mTestCompileTask).group = null
    verify(mTestCompileTask).description = null
    verify(mTestCompileTask).classpath = mClasspath
    verify(mTestCompileTask).source = mJava
    verify(mTestCompileTask).destinationDir = mCompileDestinationDir
    verify(mOptions).bootClasspath = "bootClasspath"
    verify(mInputs).source(mSource)
    verify(mResourcesCopyTask).from("realMergedResourcesDir")
    verify(mResourcesCopyTask).into(mMergedResourcesDir)
    verify(mProcessTestResourcesTask).dependsOn(mProcessResourcesTask)
    verify(mResourcesCopyTask).dependsOn(mProcessTestResourcesTask)
    verify(mTestTask).dependsOn(mResourcesCopyTask)
    verify(mTestTask).classpath = mTestClasspath
    verify(mTestTask).testClassesDir = mCompileDestinationDir
    verify(mTestTask).group = JavaBasePlugin.VERIFICATION_GROUP
    verify(mTestTask).description = "Run unit tests for Build '$FLAVOR_DEBUG'."
    verify(mHTML).destination = mVariantReportDestination
    verify(mTestTask).scanForTestClasses = false
    //TODO:missing pattern testing
    assertThat(mSystemProperties).contains(entry('android.manifest', mMergedManifest), entry('android.resources', mMergedResourcesDir), entry('android.assets', mMergedAssetsDir), entry('android.package', "packageName"))
    verify(mTestReportTask).destinationDir = mReportDestinationDir
    verify(mTestReportTask).description = 'Runs all unit tests.'
    verify(mTestReportTask).group = JavaBasePlugin.VERIFICATION_GROUP
    verify(mCheckTask).dependsOn(mTestReportTask)
    verify(mTestReportTask).reportOn(mTestTask)
  }
}
