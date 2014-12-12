package com.jcandksolutions.gradle.androidunittest

import org.junit.Before
import org.junit.Test

import static org.fest.assertions.api.Assertions.assertThat

public class SourceSetCreatorMapTest {
  private SourceSetCreatorMap mTarget

  @Before
  public void setUp() {
    mTarget = new SourceSetCreatorMap()
  }

  @Test
  public void testNewInstance() {
    assertThat(mTarget.createNewInstance()).isExactlyInstanceOf(SourceSetConfig.class)
  }
}
