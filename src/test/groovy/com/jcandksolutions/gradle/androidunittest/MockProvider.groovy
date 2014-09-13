package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.BasePlugin
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.api.LibraryVariant
import com.android.build.gradle.internal.ProductFlavorData
import com.android.build.gradle.internal.api.DefaultAndroidSourceSet
import com.android.builder.core.DefaultProductFlavor

import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.internal.DefaultDomainObjectSet
import org.gradle.api.logging.Logger

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

public class MockProvider extends DependencyProvider {
  private ModelManager mModelManager = mock(ModelManager.class)
  private ConfigurationManager mConfigurationManager = mock(ConfigurationManager.class)
  private TaskManager mTaskManager = mock(TaskManager.class)
  private AndroidUnitTestPluginExtension mExtension = new AndroidUnitTestPluginExtension()
  private ProductFlavorData mDefaultConfigData = createDummyFlavorData()
  private boolean mIsAppPlugin
  private BasePlugin mPlugin = mock(BasePlugin.class)
  private PackageExtractor mPackageExtractor = mock(PackageExtractor.class)
  private BaseExtension mAndroidExtension = mock(BaseExtension.class)
  private ConfigurationContainer mConfigurations = mock(ConfigurationContainer.class)
  private String mBootClasspath = "bootClasspath"
  private DefaultDomainObjectSet<BaseVariant> mVariants = new DefaultDomainObjectSet<>(BaseVariant.class)
  private File mReportDestinationDir = new File("reportDestinationDir")
  private Logger mLogger = mock(Logger.class)
  private MainHandler mHandler = mock(MainHandler.class)
  private AppVariantWrapper mAppVariantWrapper = mock(AppVariantWrapper.class)
  private LibraryVariantWrapper mLibraryVariantWrapper = mock(LibraryVariantWrapper.class)

  public MockProvider() {
    super(createProjectMock())
  }

  private static Project createProjectMock() {
    return mock(Project.class)
  }

  private static ProductFlavorData createDummyFlavorData() {
    DefaultProductFlavor productFlavor = mock(DefaultProductFlavor.class)
    DefaultAndroidSourceSet sourceSet = mock(DefaultAndroidSourceSet.class)
    when(sourceSet.name).thenReturn("main")
    new ProductFlavorData(productFlavor, sourceSet, null, createProjectMock())
  }

  @Override
  public AndroidUnitTestPluginExtension provideExtension() {
    return mExtension
  }

  @Override
  public ModelManager provideModelManager() {
    return mModelManager
  }

  @Override
  public ConfigurationManager provideConfigurationManager() {
    return mConfigurationManager
  }

  @Override
  public TaskManager provideTaskManager() {
    return mTaskManager
  }

  @Override
  public ProductFlavorData provideDefaultConfigData() {
    return mDefaultConfigData
  }

  @Override
  public boolean isAppPlugin() {
    return mIsAppPlugin
  }

  @Override
  public BasePlugin provideAndroidPlugin() {
    return mPlugin
  }

  @Override
  public PackageExtractor providePackageExtractor() {
    return mPackageExtractor
  }

  @Override
  public BaseExtension provideAndroidExtension() {
    return mAndroidExtension
  }

  @Override
  public ConfigurationContainer provideConfigurations() {
    return mConfigurations
  }

  @Override
  public String provideBootClasspath() {
    return mBootClasspath
  }

  @Override
  public DefaultDomainObjectSet<BaseVariant> provideVariants() {
    return mVariants
  }

  @Override
  public File provideReportDestinationDir() {
    return mReportDestinationDir
  }

  @Override
  public Logger provideLogger() {
    return mLogger
  }

  @Override
  public MainHandler provideHandler() {
    return mHandler
  }

  @Override
  public AppVariantWrapper provideAppVariantWrapper(final ApplicationVariant applicationVariant) {
    return mAppVariantWrapper
  }

  @Override
  public LibraryVariantWrapper provideLibraryVariantWrapper(final LibraryVariant libraryVariant) {
    return mLibraryVariantWrapper
  }
}
