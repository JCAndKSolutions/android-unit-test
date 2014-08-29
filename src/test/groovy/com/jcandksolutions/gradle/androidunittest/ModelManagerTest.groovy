package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.BasePlugin
import com.android.build.gradle.api.BaseVariant
import com.android.builder.model.ArtifactMetaData

import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.SourceSet
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor

import static org.fest.assertions.api.Assertions.assertThat
import static org.mockito.Mockito.eq
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

public class ModelManagerTest {
  private ModelManager mTarget
  private BasePlugin mPlugin

  @Before
  public void setUp() {
    DependencyInjector.provider = new MockProvider()
    mPlugin = DependencyInjector.provideAndroidPlugin()
    mTarget = new ModelManager()
  }

  @Test
  public void testRegister() {
    mTarget.register()
    verify(mPlugin).registerArtifactType("_unit_test_", true, ArtifactMetaData.TYPE_JAVA)
  }

  @Test
  public void testRegisterArtifact() {
    VariantWrapper variant = mock(VariantWrapper.class)
    BaseVariant baseVariant = mock(BaseVariant.class)
    when(variant.baseVariant).thenReturn(baseVariant)
    SourceSet sourceSet = mock(SourceSet.class)
    when(variant.sourceSet).thenReturn(sourceSet)
    String javaCompileTaskName = "javaCompileTaskName"
    when(sourceSet.compileJavaTaskName).thenReturn(javaCompileTaskName)
    Configuration configuration = mock(Configuration.class)
    when(variant.configuration).thenReturn(configuration)
    File classesFolder = new File("classes")
    when(variant.compileDestinationDir).thenReturn(classesFolder)
    mTarget.registerArtifact(variant)
    ArgumentCaptor<TestSourceProvider> captor = ArgumentCaptor.forClass(TestSourceProvider.class)
    verify(mPlugin).registerJavaArtifact(eq("_unit_test_"), eq(baseVariant), eq(javaCompileTaskName), eq(javaCompileTaskName), eq(configuration), eq(classesFolder), captor.capture())
    assertThat(captor.value).isExactlyInstanceOf(TestSourceProvider.class)
  }
}
