package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.BaseExtension
import com.android.builder.core.DefaultBuildType
import com.android.builder.core.DefaultProductFlavor

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

public class ConfigurationManagerTest {
  private ConfigurationManager mTarget
  private ConfigurationContainer mConfigurations

  @Before
  public void setUp() {
    DependencyInjector.setProvider(new MockProvider())
    mConfigurations = DependencyInjector.provideConfigurations()

    Logger.initialize(mock(org.gradle.api.logging.Logger.class))
    mTarget = new ConfigurationManager()
  }

  @Test
  public void testCreateNewConfigurations() {
    BaseExtension extension = DependencyInjector.provideAndroidExtension()
    Project project = ProjectBuilder.builder().build();
    NamedDomainObjectContainer<DefaultBuildType> buildTypes = project.container(DefaultBuildType)
    DefaultBuildType buildType = mock(DefaultBuildType.class)
    when(buildType.name).thenReturn("debug")
    buildTypes.add(buildType)
    when(extension.buildTypes).thenReturn(buildTypes)
    NamedDomainObjectContainer<DefaultProductFlavor> flavors = project.container(DefaultProductFlavor)
    DefaultProductFlavor flavor = mock(DefaultProductFlavor.class)
    when(flavor.name).thenReturn("flavor")
    flavors.add(flavor)
    when(extension.productFlavors).thenReturn(flavors)
    Configuration testCompileConfiguration = mock(Configuration.class)
    when(mConfigurations.create(ConfigurationManager.TEST_COMPILE)).thenReturn(testCompileConfiguration)
    Configuration compileConfiguration = mock(Configuration.class)
    when(mConfigurations.getByName('compile')).thenReturn(compileConfiguration)
    mTarget.createNewConfigurations()
    verify(testCompileConfiguration).extendsFrom compileConfiguration
    verify(mConfigurations).create("testDebugCompile")
    verify(mConfigurations).create("testFlavorCompile")
  }
}
