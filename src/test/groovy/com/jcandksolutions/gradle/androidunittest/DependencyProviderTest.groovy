package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.LibraryVariant
import com.android.build.gradle.internal.ProductFlavorData

import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.internal.DefaultDomainObjectSet
import org.gradle.api.internal.plugins.DefaultPluginCollection
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.PluginCollection
import org.gradle.api.plugins.PluginContainer
import org.junit.Before
import org.junit.Test

import static org.fest.assertions.api.Assertions.assertThat
import static org.fest.assertions.api.Assertions.fail
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

public class DependencyProviderTest {
  private Project mProject
  private DependencyProvider mTarget
  private AppPlugin mAppPlugin
  private PluginContainer mPlugins
  private BaseExtension mAndroidExtension
  private ConfigurationContainer mConfigurations
  private AndroidUnitTestPluginExtension mExtension
  private File mReportDestinationDir
  private Logger mLogger

  @Before
  public void setUp() {
    mProject = mock(Project.class)
    mPlugins = mock(PluginContainer)
    mAndroidExtension = mock(AppExtension)
    mConfigurations = mock(ConfigurationContainer)
    PluginCollection<AppPlugin> appPlugins = new DefaultPluginCollection<>(AppPlugin)
    mAppPlugin = mock(AppPlugin)
    appPlugins.add(mAppPlugin)
    ExtensionContainer extensions = mock(ExtensionContainer.class)
    mExtension = mock(AndroidUnitTestPluginExtension)
    mReportDestinationDir = new File("reportDestinationDir")
    mLogger = mock(Logger.class)
    List<String> bootClasspath = ["1", "2", "3"]
    when(mProject.plugins).thenReturn(mPlugins)
    when(mProject.configurations).thenReturn(mConfigurations)
    when(mProject.extensions).thenReturn(extensions)
    when(mProject.buildDir).thenReturn(new File("build"))
    when(mProject.file("build${File.separator}test-report")).thenReturn(mReportDestinationDir)
    when(mProject.logger).thenReturn(mLogger)
    when(mPlugins.withType(AppPlugin)).thenReturn(appPlugins)
    when(mAppPlugin.extension).thenReturn(mAndroidExtension)
    when(extensions.create("androidUnitTest", AndroidUnitTestPluginExtension)).thenReturn(mExtension)
    when(mAppPlugin.bootClasspath).thenReturn(bootClasspath)
    mTarget = new DependencyProvider(mProject)
  }

  @Test
  public void testProvideProject() {
    assertThat(mTarget.provideProject()).isEqualTo(mProject)
  }

  @Test
  public void testProvideExtension() {
    assertThat(mTarget.provideExtension()).isEqualTo(mExtension)
  }

  @Test
  public void testProvideModelManager() {
    assertThat(mTarget.provideModelManager()).isExactlyInstanceOf(ModelManager)
  }

  @Test
  public void testProvideConfigurationManager() {
    assertThat(mTarget.provideConfigurationManager()).isExactlyInstanceOf(ConfigurationManager)
  }

  @Test
  public void testProvideTaskManager() {
    assertThat(mTarget.provideTaskManager()).isExactlyInstanceOf(TaskManager)
  }

  @Test
  public void testProvideDefaultConfigData() {
    ProductFlavorData defaultConfigData = mock(ProductFlavorData)
    when(mAppPlugin.defaultConfigData).thenReturn(defaultConfigData)
    assertThat(mTarget.provideDefaultConfigData()).isEqualTo(defaultConfigData)
  }

  @Test
  public void testIsAppPluginWhenAppPluginProvided() {
    assertThat(mTarget.appPlugin).isTrue()
  }

  @Test
  public void testIsAppPluginWhenLibraryPluginProvided() {
    PluginCollection<LibraryPlugin> libraryPlugins = mock(PluginCollection)
    when(mPlugins.withType(AppPlugin)).thenReturn(null)
    when(mPlugins.withType(LibraryPlugin)).thenReturn(libraryPlugins)
    assertThat(mTarget.appPlugin).isFalse()
  }

  @Test
  public void testIsAppPluginWhenNoAndroidPluginProvided() {
    when(mPlugins.withType(AppPlugin)).thenReturn(null)
    try {
      mTarget.appPlugin
      fail("IllegalStateException should've been thrown")
    } catch (IllegalStateException ignored) {
    }
  }

  @Test
  public void testProvideAndroidPluginWithAppPlugin() {
    assertThat(mTarget.provideAndroidPlugin()).isEqualTo(mAppPlugin)
  }

  @Test
  public void testProvideAndroidPluginWithLibraryPlugin() {
    PluginCollection<LibraryPlugin> libraryPlugins = new DefaultPluginCollection<>(LibraryPlugin)
    LibraryPlugin libraryPlugin = mock(LibraryPlugin)
    libraryPlugins.add(libraryPlugin)
    when(mPlugins.withType(AppPlugin)).thenReturn(null)
    when(mPlugins.withType(LibraryPlugin)).thenReturn(libraryPlugins)
    assertThat(mTarget.provideAndroidPlugin()).isEqualTo(libraryPlugin)
  }

  @Test
  public void testProvidePackageExtractor() {
    assertThat(mTarget.providePackageExtractor()).isExactlyInstanceOf(PackageExtractor)
  }

  @Test
  public void testProvideAndroidExtension() {
    assertThat(mTarget.provideAndroidExtension()).isEqualTo(mAndroidExtension)
  }

  @Test
  public void testProvideConfigurations() {
    assertThat(mTarget.provideConfigurations()).isEqualTo(mConfigurations)
  }

  @Test
  public void testProvideBootClasspath() {
    assertThat(mTarget.provideBootClasspath()).contains("1${File.pathSeparator}2${File.pathSeparator}3")
  }

  @Test
  public void testProvideVariantsWithAppPlugin() {
    DefaultDomainObjectSet<ApplicationVariant> variants = new DefaultDomainObjectSet<>(ApplicationVariant)
    when(((AppExtension) mAndroidExtension).applicationVariants).thenReturn(variants)
    assertThat(mTarget.provideVariants()).isEqualTo(variants)
  }

  @Test
  public void testProvideVariantsWithLibraryPlugin() {
    PluginCollection<LibraryPlugin> libraryPlugins = new DefaultPluginCollection<>(LibraryPlugin)
    LibraryPlugin libraryPlugin = mock(LibraryPlugin)
    libraryPlugins.add(libraryPlugin)
    DefaultDomainObjectSet<LibraryVariant> variants = new DefaultDomainObjectSet<>(LibraryVariant)
    mAndroidExtension = mock(LibraryExtension)
    when(mPlugins.withType(AppPlugin)).thenReturn(null)
    when(mPlugins.withType(LibraryPlugin)).thenReturn(libraryPlugins)
    when(libraryPlugin.extension).thenReturn(mAndroidExtension)
    when(((LibraryExtension) mAndroidExtension).libraryVariants).thenReturn(variants)
    assertThat(mTarget.provideVariants()).isEqualTo(variants)
  }

  @Test
  public void testProvideReportDestinationDir() {
    assertThat(mTarget.provideReportDestinationDir()).isEqualTo(mReportDestinationDir)
  }

  @Test
  public void testProvideLogger() {
    assertThat(mTarget.provideLogger()).isEqualTo(mLogger)
  }

  @Test
  public void testProvideAppHandlerWithAppPlugin() {
    assertThat(mTarget.provideHandler()).isInstanceOf(AppHandler.class)
  }

  @Test
  public void testProvideLibraryHandlerWithLibraryPlugin() {
    List<String> bootClasspath = ["1", "2", "3"]
    PluginCollection<LibraryPlugin> libraryPlugins = new DefaultPluginCollection<>(LibraryPlugin)
    LibraryPlugin libraryPlugin = mock(LibraryPlugin)
    libraryPlugins.add(libraryPlugin)
    when(mPlugins.withType(AppPlugin)).thenReturn(null)
    when(mPlugins.withType(LibraryPlugin)).thenReturn(libraryPlugins)
    when(libraryPlugin.bootClasspath).thenReturn(bootClasspath)
    LibraryExtension extension = mock(LibraryExtension.class)
    when(libraryPlugin.extension).thenReturn(extension)
    assertThat(mTarget.provideHandler()).isInstanceOf(LibraryHandler.class)
  }

  @Test
  public void testProvideAppVariantWrapper() {
    ApplicationVariant variant = mock(ApplicationVariant.class)
    assertThat(mTarget.provideAppVariantWrapper(variant)).isInstanceOf(AppVariantWrapper.class)
  }

  @Test
  public void testProvideLibraryVariantWrapper() {
    LibraryVariant variant = mock(LibraryVariant.class)
    assertThat(mTarget.provideLibraryVariantWrapper(variant)).isInstanceOf(LibraryVariantWrapper.class)
  }
}
