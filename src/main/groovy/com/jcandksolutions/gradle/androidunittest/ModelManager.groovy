package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.BasePlugin
import com.android.builder.model.ArtifactMetaData

/**
 * Class that handles the Modeling of the tests so the IDE can import it correctly.
 */
public class ModelManager {
  private static final String ARTIFACT_NAME = "_unit_test_"
  private final BasePlugin mAndroidPlugin = DependencyInjector.provideAndroidPlugin()
  /**
   * Registers with the Android plugin that there is a test ArtifactType of pure Java type.
   */
  public void register() {
    mAndroidPlugin.registerArtifactType(ARTIFACT_NAME, true, ArtifactMetaData.TYPE_JAVA)
  }

  /**
   * Registers under the ArtifactType registered in {@link #register()} the artifact generated for
   * the variant. Including the base Android variant it was generated from, the test compilation
   * task name, the test configuration, the test compile destination dir and a testSourceProvider.
   * @param variantWrapper The wrapper for the variant we generated tests for.
   */
  public void registerArtifact(VariantWrapper variantWrapper) {
    mAndroidPlugin.registerJavaArtifact(ARTIFACT_NAME,
        variantWrapper.baseVariant,
        variantWrapper.sourceSet.compileJavaTaskName,
        variantWrapper.sourceSet.compileJavaTaskName,
        variantWrapper.configuration,
        variantWrapper.compileDestinationDir,
        new TestSourceProvider(variantWrapper))
  }
}
