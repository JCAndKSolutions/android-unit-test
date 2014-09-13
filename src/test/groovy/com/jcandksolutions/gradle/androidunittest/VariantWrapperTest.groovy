package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.tasks.ManifestProcessorTask
import com.android.build.gradle.tasks.MergeAssets
import com.android.builder.core.DefaultBuildType
import com.android.builder.core.DefaultProductFlavor

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.collections.SimpleFileCollection
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.internal.tasks.DefaultSourceSetContainer
import org.gradle.api.plugins.Convention
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.internal.reflect.Instantiator
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

import static org.fest.assertions.api.Assertions.assertThat
import static org.mockito.Matchers.any
import static org.mockito.Matchers.anyString
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

public class VariantWrapperTest {
  private BaseVariant mVariant
  private VariantWrapper mTarget
  private File mMergedManifest
  private Project mProject
  private SourceSet mSourceSet
  private SourceDirectorySet mResources
  private SourceDirectorySet mJava
  private ConfigurationContainer mConfigurations
  private Configuration mConfiguration
  private Configuration mDummyConfiguration
  private FileCollection mClasspath
  private FileCollection mRunpath
  private FileCollection mMergedClasspathAndResources
  private String mClassesTaskName
  private File mMergeAssetsOutputDir
  private FileCollection mTestClasspath
  private MockProvider mProvider

  @Before
  public void setUp() {
    mProvider = new MockProvider()
    mProject = mProvider.provideProject()
    mConfigurations = mProvider.provideConfigurations()
    String bootClasspathString = mProvider.provideBootClasspath()
    Convention convention = mock(Convention.class)
    SourceSetContainer sourceSets = mock(DefaultSourceSetContainer.class)
    Instantiator instantiator = mock(Instantiator.class)
    when(instantiator.newInstance(DefaultSourceSetContainer.class, null, null, instantiator)).thenReturn(sourceSets)
    JavaPluginConvention javaConvention = new JavaPluginConvention(mock(ProjectInternal.class), instantiator);
    mSourceSet = mock(SourceSet.class)
    mVariant = mock(ApplicationVariant.class)
    mMergedManifest = mock(File.class)
    ManifestProcessorTask manTask = mock(ManifestProcessorTask.class)
    DefaultBuildType buildType = mock(DefaultBuildType.class)
    mResources = mock(SourceDirectorySet.class)
    mJava = mock(SourceDirectorySet.class)
    DefaultProductFlavor free = mock(DefaultProductFlavor.class)
    DefaultProductFlavor paid = mock(DefaultProductFlavor.class)
    List<DefaultProductFlavor> productFlavors = [free, paid]
    mConfiguration = mock(Configuration.class)
    mDummyConfiguration = mock(Configuration.class)
    JavaCompile androidJavaCompileTask = mock(JavaCompile.class)
    File javaCompileDestinationDir = new File("javaCompileDestinationDir")
    FileCollection javaCompileClasspath = mock(FileCollection.class)
    ConfigurableFileCollection mergedDestDirAndClassPath = mock(ConfigurableFileCollection.class)
    mClasspath = mock(FileCollection.class)
    File buildDir = new File("build")
    ConfigurableFileCollection resourcesDir = mock(ConfigurableFileCollection.class)
    mMergedClasspathAndResources = mock(FileCollection.class)
    mRunpath = mock(FileCollection.class)
    mClassesTaskName = "classesTaskName"
    MergeAssets mergeAssets = mock(MergeAssets.class)
    mMergeAssetsOutputDir = mock(File.class)
    mTestClasspath = mock(FileCollection.class)
    ConfigurableFileCollection bootClasspath = mock(ConfigurableFileCollection.class)
    when(mProject.file(anyString())).thenAnswer(new Answer<File>() {
      public File answer(InvocationOnMock invocation) {
        return new File(invocation.arguments[0] as String)
      }
    })
    when(mProject.convention).thenReturn(convention)
    when(mProject.buildDir).thenReturn(buildDir)
    when(mProject.files(javaCompileDestinationDir, javaCompileClasspath)).thenReturn(mergedDestDirAndClassPath)
    when(mProject.files("build${File.separator}resources${File.separator}testFreePaidDebug")).thenReturn(resourcesDir)
    when(mProject.files(bootClasspathString)).thenReturn(bootClasspath)
    when(convention.getPlugin(JavaPluginConvention)).thenReturn(javaConvention)
    when(free.name).thenReturn("free")
    when(paid.name).thenReturn("paid")
    when(mVariant.productFlavors).thenReturn(productFlavors)
    when(mVariant.processManifest).thenReturn(manTask)
    when(mVariant.buildType).thenReturn(buildType)
    when(mVariant.javaCompile).thenReturn(androidJavaCompileTask)
    when(mVariant.dirName).thenReturn("variantDirName")
    when(mVariant.mergeAssets).thenReturn(mergeAssets)
    when(sourceSets.create("testFreePaidDebug")).thenReturn(mSourceSet)
    when(mSourceSet.resources).thenReturn(mResources)
    when(mSourceSet.java).thenReturn(mJava)
    when(mSourceSet.classesTaskName).thenReturn(mClassesTaskName)
    when(manTask.manifestOutputFile).thenReturn(mMergedManifest)
    when(buildType.name).thenReturn("debug")
    when(mConfigurations.create("_testFreePaidDebugCompile")).thenReturn(mConfiguration)
    when(mConfigurations.findByName(anyString())).thenReturn(mDummyConfiguration)
    when(androidJavaCompileTask.destinationDir).thenReturn(javaCompileDestinationDir)
    when(androidJavaCompileTask.classpath).thenReturn(javaCompileClasspath)
    when(mClasspath.plus(resourcesDir)).thenReturn(mMergedClasspathAndResources)
    when(mMergedClasspathAndResources.plus(any(SimpleFileCollection.class))).thenReturn(mRunpath)
    when(mRunpath.plus(bootClasspath)).thenReturn(mTestClasspath)
    when(mConfiguration.plus(mergedDestDirAndClassPath)).thenReturn(mClasspath)
    when(mergeAssets.outputDir).thenReturn(mMergeAssetsOutputDir)
    mTarget = new VariantWrapper(mVariant, mProject, mConfigurations, bootClasspathString, mProvider.provideLogger(), null) {
      @Override
      protected String createRealMergedResourcesDirName() {
        return "mergedResourcesDir"
      }

      @Override
      Task getAndroidCompileTask() {
        return null
      }
    }
  }

  @Test
  public void testConfigureSourceSet() {
    mTarget.configureSourceSet()
    ArgumentCaptor fileCaptor = ArgumentCaptor.forClass(File.class)
    verify(mResources).srcDirs(fileCaptor.capture())
    assertThat(fileCaptor.value).isEqualTo(new File("src${File.separator}test${File.separator}resources"))
    ArgumentCaptor fileArrayCaptor = ArgumentCaptor.forClass(ArrayList.class)
    verify(mJava).setSrcDirs(fileArrayCaptor.capture())
    assertThat(fileArrayCaptor.value).contains(new File("src${File.separator}test${File.separator}java"), new File("src${File.separator}testDebug${File.separator}java"), new File("src${File.separator}testFreePaid${File.separator}java"), new File("src${File.separator}testFreePaidDebug${File.separator}java"), new File("src${File.separator}testFree${File.separator}java"), new File("src${File.separator}testFreeDebug${File.separator}java"), new File("src${File.separator}testPaid${File.separator}java"), new File("src${File.separator}testPaidDebug${File.separator}java"))
    verify(mConfiguration, times(4)).extendsFrom(mDummyConfiguration)
    verify(mSourceSet).compileClasspath = mClasspath
    ArgumentCaptor fileCollectionCaptor = ArgumentCaptor.forClass(FileCollection.class)
    verify(mMergedClasspathAndResources).plus(fileCollectionCaptor.capture())
    assertThat(fileCollectionCaptor.value.asPath).isEqualTo("build${File.separator}test-classes${File.separator}variantDirName".toString())
    verify(mSourceSet).runtimeClasspath = mRunpath
    verify(mSourceSet).compiledBy(mClassesTaskName)
  }

  @Test
  public void testGetVariantReportDestination() {
    assertThat(mTarget.variantReportDestination).isEqualTo(new File("build${File.separator}test-report${File.separator}variantDirName"))
  }

  @Test
  public void testGetMergedManifest() {
    assertThat(mTarget.mergedManifest).isEqualTo(mMergedManifest)
  }

  @Test
  public void testGetMergedResourcesDir() {
    assertThat(mTarget.mergedResourcesDir).isEqualTo(new File("build${File.separator}test-resources${File.separator}FreePaidDebug${File.separator}res"))
  }

  @Test
  public void testGetMergedAssetsDir() {
    assertThat(mTarget.mergedAssetsDir).isEqualTo(mMergeAssetsOutputDir)
  }

  @Test
  public void testGetTestClasspath() {
    assertThat(mTarget.testClasspath).isEqualTo(mTestClasspath)
  }

  @Test
  public void testGetResourcesCopyTaskName() {
    assertThat(mTarget.resourcesCopyTaskName).isEqualTo("copyFreePaidDebugTestResources")
  }

  @Test
  public void testGetProcessResourcesTaskName() {
    assertThat(mTarget.processResourcesTaskName).isEqualTo("processTestFreePaidDebugResources")
  }

  @Test
  public void testGetBaseVariant() {
    assertThat(mTarget.baseVariant).isEqualTo(mVariant)
  }
}
