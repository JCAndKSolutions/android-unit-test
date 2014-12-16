package com.jcandksolutions.gradle.androidunittest

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.collections.DefaultConfigurableFileCollection
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.reporting.DirectoryReport
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskInputs
import org.gradle.api.tasks.compile.CompileOptions
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.TestReport
import org.gradle.api.tasks.testing.TestTaskReports
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor

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
  private File mMergedResourcesDir
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
  private Map<String, TestTaskConfig> mTestTasks

  @Before
  public void setUp() {
    mProvider = new MockProvider()
    Project project = mProvider.provideProject()
    mPackageExtractor = mProvider.providePackageExtractor()
    mReportDestinationDir = mProvider.provideReportDestinationDir()
    mTestTasks = mProvider.provideExtension().testTasks
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
    mMergedResourcesDir = new File("mergedResourcesDir")
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
    when(tasks.getByName("classesTaskName")).thenReturn(mClassesTask)
    when(tasks.getByName("compileJavaTaskName")).thenReturn(mTestCompileTask)
    when(tasks.getByName("check")).thenReturn(mCheckTask)
    when(project.tasks).thenReturn(tasks)
    when(mVariant.sourceSet).thenReturn(sourceSet)
    when(mVariant.completeName).thenReturn("$FLAVOR_DEBUG")
    when(mVariant.classpath).thenReturn(mClasspath)
    when(mVariant.androidCompileTask).thenReturn(mAndroidCompileTask)
    when(mVariant.compileDestinationDir).thenReturn(mCompileDestinationDir)
    when(mVariant.mergedResourcesDir).thenReturn(mMergedResourcesDir)
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
  public void testCreateTestTaskByDefault() {
    System.properties.remove("test.single")
    System.properties.remove("test${FLAVOR_DEBUG}.single".toString())
    mTarget.createTestTask(mVariant, mTestTasks)
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
    verify(mTestTask).dependsOn(mAndroidCompileTask)
    verify(mTestTask).classpath = mTestClasspath
    verify(mTestTask).testClassesDir = mCompileDestinationDir
    verify(mTestTask).group = JavaBasePlugin.VERIFICATION_GROUP
    verify(mTestTask).description = "Run unit tests for Build '$FLAVOR_DEBUG'."
    verify(mHTML).destination = mVariantReportDestination
    verify(mTestTask).scanForTestClasses = false
    //TODO:missing pattern testing
    verify(mTestTask).setDebug(false)
    verify(mTestTask).setMaxParallelForks(1)
    verify(mTestTask).setForkEvery(0)
    verify(mTestTask).setMinHeapSize(null)
    verify(mTestTask).setMaxHeapSize(null)
    ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class)
    verify(mTestTask).setJvmArgs(listCaptor.capture())
    assertThat(listCaptor.value).isEmpty()
    verify(mTestTask).setExcludes(listCaptor.capture())
    assertThat(listCaptor.value).isEmpty()
    verify(mTestTask).setIncludes(listCaptor.capture())
    assertThat(listCaptor.value).isEmpty()
    verify(mTestTask).include("**${File.separator}*Test.class".toString())
    assertThat(mSystemProperties).contains(entry('android.manifest', mMergedManifest), entry('android.resources', mMergedResourcesDir), entry('android.assets', mMergedAssetsDir), entry('android.package', "packageName")).hasSize(4)
    verify(mTestReportTask).destinationDir = mReportDestinationDir
    verify(mTestReportTask).description = 'Runs all unit tests.'
    verify(mTestReportTask).group = JavaBasePlugin.VERIFICATION_GROUP
    verify(mCheckTask).dependsOn(mTestReportTask)
    verify(mTestReportTask).reportOn(mTestTask)
  }

  @Test
  public void testCreateTestTaskWithAll() {
    TestTaskConfig testTask = mTestTasks["all"]
    testTask.debug = true
    testTask.maxParallelForks = 2
    testTask.forkEvery = 3
    testTask.minHeapSize = "minHeap"
    testTask.maxHeapSize = "maxHeap"
    testTask.jvmArgs = ['arg1', 'arg2']
    testTask.excludes = ['exc1', 'exc2']
    testTask.includes = ['inc1', 'inc2']
    testTask.systemProperties = ['prop1': 'val1', 'prop2': 'val2']
    String pattern = "pattern"
    String pattern2 = "pattern2"
    System.properties.setProperty("test.single", pattern)
    System.properties.setProperty("test${FLAVOR_DEBUG}.single", pattern2)
    mTarget.createTestTask(mVariant, mTestTasks)
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
    verify(mTestTask).dependsOn(mAndroidCompileTask)
    verify(mTestTask).classpath = mTestClasspath
    verify(mTestTask).testClassesDir = mCompileDestinationDir
    verify(mTestTask).group = JavaBasePlugin.VERIFICATION_GROUP
    verify(mTestTask).description = "Run unit tests for Build '$FLAVOR_DEBUG'."
    verify(mHTML).destination = mVariantReportDestination
    verify(mTestTask).scanForTestClasses = false
    //TODO:missing pattern testing
    verify(mTestTask).setDebug(true)
    verify(mTestTask).setMaxParallelForks(2)
    verify(mTestTask).setForkEvery(3)
    verify(mTestTask).setMinHeapSize('minHeap')
    verify(mTestTask).setMaxHeapSize('maxHeap')
    ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class)
    verify(mTestTask).setJvmArgs(listCaptor.capture())
    assertThat(listCaptor.value).hasSize(2).contains('arg1', 'arg2')
    verify(mTestTask).setExcludes(listCaptor.capture())
    assertThat(listCaptor.value).hasSize(2).contains('exc1', 'exc2')
    verify(mTestTask).setIncludes(listCaptor.capture())
    assertThat(listCaptor.value).hasSize(2).contains('inc1', 'inc2')
    verify(mTestTask).include("**${File.separator}${pattern}.class".toString())
    verify(mTestTask).include("**${File.separator}${pattern2}.class".toString())
    assertThat(mSystemProperties).contains(entry('android.manifest', mMergedManifest), entry('android.resources', mMergedResourcesDir), entry('android.assets', mMergedAssetsDir), entry('android.package', "packageName")).hasSize(4)
    verify(mTestTask).systemProperties(['prop1': 'val1', 'prop2': 'val2'])
    verify(mTestReportTask).destinationDir = mReportDestinationDir
    verify(mTestReportTask).description = 'Runs all unit tests.'
    verify(mTestReportTask).group = JavaBasePlugin.VERIFICATION_GROUP
    verify(mCheckTask).dependsOn(mTestReportTask)
    verify(mTestReportTask).reportOn(mTestTask)
  }

  @Test
  public void testCreateTestTaskWithVariantAndAll() {
    TestTaskConfig testTask = mTestTasks["testFlavorDebug"]
    testTask.debug = true
    testTask.maxParallelForks = 2
    testTask.forkEvery = 3
    testTask.minHeapSize = "minHeap"
    testTask.maxHeapSize = "maxHeap"
    testTask.jvmArgs = ['arg1', 'arg2']
    testTask.excludes = ['exc1', 'exc2']
    testTask.includes = ['inc1', 'inc2']
    testTask.systemProperties = ['prop1': 'val1', 'prop2': 'val2']
    TestTaskConfig allTask = mTestTasks["all"]
    allTask.debug = false
    allTask.maxParallelForks = 3
    allTask.forkEvery = 4
    allTask.minHeapSize = "minH"
    allTask.maxHeapSize = "maxH"
    allTask.jvmArgs = ['arg3', 'arg4']
    allTask.excludes = ['exc3', 'exc4']
    allTask.includes = ['inc3', 'inc4']
    allTask.systemProperties = ['prop3': 'val3', 'prop4': 'val4']
    String pattern = "pattern"
    String pattern2 = "pattern2"
    System.properties.setProperty("test.single", pattern)
    System.properties.setProperty("test${FLAVOR_DEBUG}.single", pattern2)
    mTarget.createTestTask(mVariant, mTestTasks)
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
    verify(mTestTask).dependsOn(mAndroidCompileTask)
    verify(mTestTask).classpath = mTestClasspath
    verify(mTestTask).testClassesDir = mCompileDestinationDir
    verify(mTestTask).group = JavaBasePlugin.VERIFICATION_GROUP
    verify(mTestTask).description = "Run unit tests for Build '$FLAVOR_DEBUG'."
    verify(mHTML).destination = mVariantReportDestination
    verify(mTestTask).scanForTestClasses = false
    //TODO:missing pattern testing
    verify(mTestTask).setDebug(true)
    verify(mTestTask).setMaxParallelForks(2)
    verify(mTestTask).setForkEvery(3)
    verify(mTestTask).setMinHeapSize('minHeap')
    verify(mTestTask).setMaxHeapSize('maxHeap')
    ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class)
    verify(mTestTask).setJvmArgs(listCaptor.capture())
    assertThat(listCaptor.value).hasSize(2).contains('arg1', 'arg2')
    verify(mTestTask).setExcludes(listCaptor.capture())
    assertThat(listCaptor.value).hasSize(2).contains('exc1', 'exc2')
    verify(mTestTask).setIncludes(listCaptor.capture())
    assertThat(listCaptor.value).hasSize(2).contains('inc1', 'inc2')
    verify(mTestTask).include("**${File.separator}${pattern}.class".toString())
    verify(mTestTask).include("**${File.separator}${pattern2}.class".toString())
    assertThat(mSystemProperties).contains(entry('android.manifest', mMergedManifest), entry('android.resources', mMergedResourcesDir), entry('android.assets', mMergedAssetsDir), entry('android.package', "packageName")).hasSize(4)
    verify(mTestTask).systemProperties(['prop1': 'val1', 'prop2': 'val2'])
    verify(mTestReportTask).destinationDir = mReportDestinationDir
    verify(mTestReportTask).description = 'Runs all unit tests.'
    verify(mTestReportTask).group = JavaBasePlugin.VERIFICATION_GROUP
    verify(mCheckTask).dependsOn(mTestReportTask)
    verify(mTestReportTask).reportOn(mTestTask)
  }
}
