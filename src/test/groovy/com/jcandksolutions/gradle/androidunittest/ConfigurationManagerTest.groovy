package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.BaseExtension
import com.android.builder.core.DefaultBuildType
import com.android.builder.core.DefaultProductFlavor

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.DependencyArtifact
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ResolveException
import org.gradle.api.internal.DefaultDomainObjectSet
import org.gradle.api.internal.artifacts.DefaultDependencySet
import org.gradle.api.internal.artifacts.dependencies.DefaultDependencyArtifact
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

import static org.fest.assertions.api.Assertions.assertThat
import static org.mockito.Matchers.any
import static org.mockito.Matchers.anyString
import static org.mockito.Mockito.doAnswer
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

public class ConfigurationManagerTest {
  private ConfigurationManager mTarget
  private ConfigurationContainer mConfigurations
  private BaseExtension mAndroidExtension
  private Project mProject
  private AndroidUnitTestPluginExtension mExtension
  private ModelManager mModelManager

  @Before
  public void setUp() {
    MockProvider provider = new MockProvider()
    mConfigurations = provider.provideConfigurations()
    mAndroidExtension = provider.provideAndroidExtension()
    mExtension = provider.provideExtension()
    mProject = provider.provideProject()
    mModelManager = provider.provideModelManager()
    mTarget = new ConfigurationManager(mAndroidExtension, mConfigurations, mProject, mExtension, mModelManager, provider.provideLogger())
  }

  @Test
  public void testCreateNewConfigurations() {
    Project project = ProjectBuilder.builder().build();
    NamedDomainObjectContainer<DefaultBuildType> buildTypes = project.container(DefaultBuildType)
    DefaultBuildType buildType = mock(DefaultBuildType.class)
    when(buildType.name).thenReturn("debug")
    buildTypes.add(buildType)
    when(mAndroidExtension.buildTypes).thenReturn(buildTypes)
    NamedDomainObjectContainer<DefaultProductFlavor> flavors = project.container(DefaultProductFlavor)
    DefaultProductFlavor flavor = mock(DefaultProductFlavor.class)
    when(flavor.name).thenReturn("flavor")
    flavors.add(flavor)
    when(mAndroidExtension.productFlavors).thenReturn(flavors)
    Configuration testCompileConfiguration = mock(Configuration.class)
    DependencySet dependencies = new DefaultDependencySet("lol", new DefaultDomainObjectSet<Dependency>(Dependency.class))
    Dependency dependency = mock(ExternalModuleDependency.class)
    when(dependency.copy()).thenReturn(dependency)
    when(dependency.name).thenReturn("dependency")
    dependencies.add(dependency)
    when(testCompileConfiguration.dependencies).thenReturn(dependencies)
    Configuration tmpConf = mock(Configuration.class)
    DependencySet tmpDependencies = mock(DependencySet.class)
    when(tmpConf.dependencies).thenReturn(tmpDependencies)
    when(tmpConf.files).thenReturn(null).thenThrow(ResolveException.class)
    when(mConfigurations.create(anyString())).thenReturn(tmpConf)
    when(mConfigurations.create(ConfigurationManager.TEST_COMPILE)).thenReturn(testCompileConfiguration)
    when(mConfigurations.getByName(ConfigurationManager.TEST_COMPILE)).thenReturn(testCompileConfiguration)
    Configuration compileConfiguration = mock(Configuration.class)
    when(compileConfiguration.dependencies).thenReturn(dependencies)
    when(mConfigurations.getByName(ConfigurationManager.COMPILE)).thenReturn(compileConfiguration)
    Configuration sourcesConfiguration = mock(Configuration.class)
    when(mConfigurations.create(ConfigurationManager.SOURCES_JAVADOC)).thenReturn(sourcesConfiguration)
    Configuration debugConfiguration = mock(Configuration.class)
    when(debugConfiguration.dependencies).thenReturn(dependencies)
    when(mConfigurations.getByName("debugCompile")).thenReturn(debugConfiguration)
    Configuration flavorConfiguration = mock(Configuration.class)
    when(flavorConfiguration.dependencies).thenReturn(dependencies)
    when(mConfigurations.getByName("flavorCompile")).thenReturn(flavorConfiguration)
    Configuration testDebugConfiguration = mock(Configuration.class)
    when(testDebugConfiguration.dependencies).thenReturn(dependencies)
    when(mConfigurations.getByName("testDebugCompile")).thenReturn(testDebugConfiguration)
    Configuration testFlavorConfiguration = mock(Configuration.class)
    when(testFlavorConfiguration.dependencies).thenReturn(dependencies)
    when(mConfigurations.getByName("testFlavorCompile")).thenReturn(testFlavorConfiguration)
    doAnswer(new Answer<Void>() {
      @Override
      Void answer(final InvocationOnMock invocation) throws Throwable {
        Closure clo = invocation.arguments[0] as Closure
        clo.run()
        return null
      }
    } as Answer).when(mProject).afterEvaluate(any(Closure.class) as Closure)
    mExtension.downloadDependenciesJavadoc = true
    mExtension.downloadDependenciesSources = true
    mExtension.downloadTestDependenciesJavadoc = true
    mExtension.downloadTestDependenciesSources = true
    mTarget.createNewConfigurations()
    verify(testCompileConfiguration).extendsFrom compileConfiguration
    verify(mConfigurations).create("testDebugCompile")
    verify(mConfigurations).create("testFlavorCompile")
    ArgumentCaptor<DependencyArtifact> captor = ArgumentCaptor.forClass(DependencyArtifact.class)
    verify(dependency, times(12)).addArtifact(captor.capture())
    for (DependencyArtifact value in captor.allValues) {
      assertThat(value).isIn(new DefaultDependencyArtifact("dependency", "jar", "jar", "sources", null), new DefaultDependencyArtifact("dependency", "jar", "jar", "javadoc", null))
    }
    verify(tmpDependencies, times(6)).add(dependency)
    ArgumentCaptor<Configuration> confCaptor = ArgumentCaptor.forClass(Configuration.class)
    verify(sourcesConfiguration, times(1)).extendsFrom(confCaptor.capture())
    assertThat(confCaptor.value).isEqualTo(tmpConf)
    verify(mModelManager).registerJavadocSourcesArtifact(sourcesConfiguration)
  }
}
