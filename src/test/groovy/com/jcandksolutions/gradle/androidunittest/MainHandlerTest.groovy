package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.api.BaseVariant
import com.android.builder.core.DefaultBuildType

import org.gradle.api.internal.DefaultDomainObjectSet
import org.junit.Before
import org.junit.Test

import static org.mockito.Matchers.any
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.never
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

public class MainHandlerTest {
  private MainHandler mTarget
  private VariantWrapper mVariantWrapper
  private boolean mIsVariantInvalid
  private MockProvider mProvider
  private ModelManager mModelManager
  private ConfigurationManager mConfigurationManager
  private BaseVariant mVariant
  private DefaultBuildType mBuildType
  private AndroidUnitTestPluginExtension mExtension
  private TaskManager mTaskManager
  private SourceSetCreatorMap mSourceSets
  private TestTaskCreatorMap mTestTasks

  @Before
  public void setUp() {
    mProvider = new MockProvider()
    mModelManager = mProvider.provideModelManager()
    mConfigurationManager = mProvider.provideConfigurationManager()
    mTaskManager = mProvider.provideTaskManager()
    mExtension = mProvider.provideExtension()
    mSourceSets = mExtension.sourceSets
    mTestTasks = mExtension.testTasks
    DefaultDomainObjectSet<BaseVariant> variants = mProvider.provideVariants()
    mVariant = mock(BaseVariant.class)
    mBuildType = mock(DefaultBuildType)
    when(mVariant.buildType).thenReturn(mBuildType)
    variants.add(mVariant)
    mVariantWrapper = mock(VariantWrapper.class)
    mTarget = new MainHandler(mProvider) {
      @Override
      protected VariantWrapper createVariantWrapper(final BaseVariant variant) {
        return mVariantWrapper
      }

      @Override
      protected boolean isVariantInvalid(final BaseVariant baseVariant) {
        return mIsVariantInvalid
      }
    }
  }

  @Test
  public void testRunWithNonDebuggableVariantAndNoReleaseBuildTypeEnabledDoNothing() {
    when(mBuildType.debuggable).thenReturn(false)
    mExtension.testReleaseBuildType = false
    mTarget.run()
    verify(mModelManager).register()
    verify(mConfigurationManager).createNewConfigurations()
    verify(mTaskManager, never()).createTestTask(any(VariantWrapper.class), any(Map.class))
  }

  @Test
  public void testRunWithDebuggableVariant() {
    when(mBuildType.debuggable).thenReturn(true)
    mExtension.testReleaseBuildType = false
    mTarget.run()
    verify(mModelManager).register()
    verify(mConfigurationManager).createNewConfigurations()
    verify(mVariantWrapper).configureSourceSet(mSourceSets)
    verify(mTaskManager).createTestTask(mVariantWrapper, mTestTasks)
    verify(mModelManager).registerArtifact(mVariantWrapper)
  }

  @Test
  public void testRunWithNonDebuggableVariantAndReleaseBuildTypeEnabled() {
    when(mBuildType.debuggable).thenReturn(false)
    mExtension.testReleaseBuildType = true
    mTarget.run()
    verify(mModelManager).register()
    verify(mConfigurationManager).createNewConfigurations()
    verify(mVariantWrapper).configureSourceSet(mSourceSets)
    verify(mTaskManager).createTestTask(mVariantWrapper, mTestTasks)
    verify(mModelManager).registerArtifact(mVariantWrapper)
  }
}
