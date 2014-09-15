package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.BasePlugin
import com.android.build.gradle.api.BaseVariant
import com.android.builder.model.ArtifactMetaData
import com.android.builder.model.SourceProvider

import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.SourceSet
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor

import static org.fest.assertions.api.Assertions.assertThat
import static org.mockito.Matchers.isNull
import static org.mockito.Mockito.eq
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

public class ModelManagerTest {
  private ModelManager mTarget
  private BasePlugin mPlugin
  private MockProvider mProvider
  private VariantWrapper mVariantWrapper
  private BaseVariant mVariant
  private String mJavaCompileTaskName
  private Configuration mConfiguration
  private File mClassesFolder

  @Before
  public void setUp() {
    mProvider = new MockProvider()
    mPlugin = mProvider.provideAndroidPlugin()
    mTarget = new ModelManager(mPlugin)
    mVariantWrapper = mock(VariantWrapper.class)
    mVariant = mock(BaseVariant.class)
    when(mVariantWrapper.baseVariant).thenReturn(mVariant)
    when(mVariant.name).thenReturn("debug")
    SourceSet sourceSet = mock(SourceSet.class)
    when(mVariantWrapper.sourceSet).thenReturn(sourceSet)
    mJavaCompileTaskName = "mJavaCompileTaskName"
    when(sourceSet.compileJavaTaskName).thenReturn(mJavaCompileTaskName)
    mConfiguration = mock(Configuration.class)
    when(mVariantWrapper.configuration).thenReturn(mConfiguration)
    mClassesFolder = new File("classes")
    when(mVariantWrapper.compileDestinationDir).thenReturn(mClassesFolder)
  }

  @Test
  public void testRegister() {
    mTarget.register()
    verify(mPlugin).registerArtifactType("_unit_test_", true, ArtifactMetaData.TYPE_JAVA)
    verify(mPlugin).registerArtifactType("_sources_javadoc_", true, ArtifactMetaData.TYPE_JAVA)
  }

  @Test
  public void testRegisterArtifact() {
    mTarget.registerArtifact(mVariantWrapper)
    ArgumentCaptor<TestSourceProvider> captor = ArgumentCaptor.forClass(TestSourceProvider.class)
    verify(mPlugin).registerJavaArtifact(eq("_unit_test_"), eq(mVariant), eq(mJavaCompileTaskName), eq(mJavaCompileTaskName), eq(mConfiguration), eq(mClassesFolder), captor.capture())
    assertThat(captor.value).isExactlyInstanceOf(TestSourceProvider.class)
  }

  @Test
  public void testRegisterJavadocSourcesArtifact() {
    Configuration config = mock(Configuration.class)
    mTarget.registerArtifact(mVariantWrapper)
    mTarget.registerJavadocSourcesArtifact(config)
    ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class)
    verify(mPlugin).registerJavaArtifact(eq("_sources_javadoc_"), eq(mVariant), eq("dummyAssembleTaskName"), eq("dummyJavaCompileTaskName"), eq(config), fileCaptor.capture(), isNull(SourceProvider.class))
    assertThat(fileCaptor.value).isEqualTo(new File("dummyClassesFolder"))
  }
}
