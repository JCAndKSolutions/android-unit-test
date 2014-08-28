package com.jcandksolutions.gradle.androidunittest

import org.gradle.api.Project
import org.junit.Before
import org.junit.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

public class AndroidUnitTestPluginTest {
  private Project mProject
  private AndroidUnitTestPlugin mTarget
  private DependencyProvider mDependencyProvider
  private AppHandler mAppHandler
  private LibraryHandler mLibraryHandler

  @Before
  public void setUp() {
    mProject = mock(Project.class)
    mDependencyProvider = mock(DependencyProvider.class)
    mAppHandler = mock(AppHandler.class)
    mLibraryHandler = mock(LibraryHandler.class)

    mTarget = new AndroidUnitTestPlugin() {
      @Override
      protected DependencyProvider createDependencyProvider(Project project) {
        return mDependencyProvider
      }

      @Override
      protected AppHandler createAppHandler() {
        return mAppHandler
      }

      @Override
      protected LibraryHandler createLibraryHandler() {
        return mLibraryHandler
      }
    }
  }

  @Test
  public void testApplyWithAppPluginRunsAppHandler() {
    when(mDependencyProvider.isAppPlugin()).thenReturn(true)
    mTarget.apply(mProject)
    verify(mAppHandler).run()
  }

  @Test
  public void testApplyWithLibraryPluginRunsLibraryHandler() {
    when(mDependencyProvider.isAppPlugin()).thenReturn(false)
    mTarget.apply(mProject)
    verify(mLibraryHandler).run()
  }
}
