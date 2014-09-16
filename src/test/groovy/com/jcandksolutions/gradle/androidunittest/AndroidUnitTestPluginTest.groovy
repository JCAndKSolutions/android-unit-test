package com.jcandksolutions.gradle.androidunittest

import org.gradle.api.Project
import org.junit.Before
import org.junit.Test

import static org.fest.assertions.api.Assertions.assertThat
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

public class AndroidUnitTestPluginTest {
  private Project mProject
  private AndroidUnitTestPlugin mTarget
  private DependencyProvider mDependencyProvider
  private MainHandler mHandler

  @Before
  public void setUp() {
    mProject = mock(Project.class)
  }

  @Test
  public void testApplyRunsHandler() {
    mDependencyProvider = mock(DependencyProvider.class)
    mHandler = mock(MainHandler.class)
    when(mDependencyProvider.provideHandler()).thenReturn(mHandler)
    mTarget = new AndroidUnitTestPlugin() {
      @Override
      protected DependencyProvider createDependencyProvider(Project project) {
        return mDependencyProvider
      }
    }
    mTarget.apply(mProject)
    verify(mHandler).run()
  }

  @Test
  public void testCreateDependencyProvider() {
    mTarget = new AndroidUnitTestPlugin() {
      @Override
      public void apply(Project project) {
        mDependencyProvider = createDependencyProvider(project)
      }
    }
    mTarget.apply(mProject)
    assertThat(mDependencyProvider).isExactlyInstanceOf(DependencyProvider.class)
  }
}
