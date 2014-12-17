package com.jcandksolutions.gradle.androidunittest

import com.jcandksolutions.gradle.androidunittest.CreatorMap.Configurator

import org.gradle.api.internal.ClosureBackedAction
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor

import static org.fest.assertions.api.Assertions.assertThat
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

public class CreatorMapTest {
  private CreatorMap<String> mTarget
  private ClosureBackedAction mAction

  @Before
  public void setUp() {
    mAction = mock(ClosureBackedAction.class)
    mTarget = new CreatorMap<String>() {
      @Override
      protected String createNewInstance() {
        return "test"
      }

      @Override
      protected ClosureBackedAction createAction(Closure cl) {
        return CreatorMapTest.this.mAction
      }
    }
  }

  @Test
  public void testGetCreatesNewInstanceInsteadOfNull() {
    assertThat(mTarget["lol"]).isEqualTo("test")
  }

  @Test
  public void testConfigure() throws Exception {
    Closure cl = mock(Closure.class)
    Object owner = mock(Object.class)
    when(cl.owner).thenReturn(owner)
    mTarget.configure(cl)
    ArgumentCaptor<Configurator> captor = ArgumentCaptor.forClass(Configurator.class)
    verify(mAction).execute(captor.capture())
    Configurator configurator = captor.value
    Object[] params = [cl]
    assertThat(configurator._isConfigureMethod("lol", params)).isTrue()
    assertThat((String) configurator._configure("lol", params)).isEqualTo("test")
    verify(mAction).execute("test")
    params = []
    assertThat((String) configurator._configure("lol", params)).isEqualTo("test")
  }

  @Test
  public void testCreateAction() {
    mTarget = new CreatorMap<String>() {
      @Override
      protected String createNewInstance() {
        return null
      }
    }
    assertThat(mTarget.createAction(null)).isExactlyInstanceOf(ClosureBackedAction.class)
  }
}
