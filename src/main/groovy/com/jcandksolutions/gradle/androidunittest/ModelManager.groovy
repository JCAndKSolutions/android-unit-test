package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.BasePlugin
import com.android.build.gradle.api.BaseVariant
import com.android.builder.model.ArtifactMetaData

import org.gradle.api.artifacts.Configuration

/**
 * Class that handles the Modeling of the tests so the IDE can import it correctly.
 */
public class ModelManager {
  private static final String TEST_ARTIFACT_NAME = "_unit_test_"
  private static final String SOURCES_JAVADOC_ARTIFACT_NAME = "_sources_javadoc_"
  private final BasePlugin mAndroidPlugin
  private BaseVariant mDebugVariant
  private Configuration mJavadocSourcesConfiguration
  /**
   * Instantiates a ModelManager.
   * @param androidPlugin The AndroidPlugin.
   */
  public ModelManager(BasePlugin androidPlugin) {
    mAndroidPlugin = androidPlugin
  }

  /**
   * Registers with the Android plugin that there is a test ArtifactType of pure Java type.
   */
  public void register() {
    mAndroidPlugin.registerArtifactType(TEST_ARTIFACT_NAME, true, ArtifactMetaData.TYPE_JAVA)
    mAndroidPlugin.registerArtifactType(SOURCES_JAVADOC_ARTIFACT_NAME, true, ArtifactMetaData.TYPE_JAVA)
  }

  /**
   * Registers under the ArtifactType registered in {@link #register()} the artifact generated for
   * the variant. Including the base Android variant it was generated from, the test compilation
   * task name, the test configuration, the test compile destination dir and a testSourceProvider.
   * @param variantWrapper The wrapper for the variant we generated tests for.
   */
  public void registerArtifact(VariantWrapper variantWrapper) {
    mAndroidPlugin.registerJavaArtifact(TEST_ARTIFACT_NAME,
        variantWrapper.baseVariant,
        variantWrapper.sourceSet.compileJavaTaskName,
        variantWrapper.sourceSet.compileJavaTaskName,
        variantWrapper.configuration,
        variantWrapper.compileDestinationDir,
        new TestSourceProvider(variantWrapper))
    if (variantWrapper.baseVariant.name == "debug") {
      mDebugVariant = variantWrapper.baseVariant
      if (mJavadocSourcesConfiguration != null) {
        registerJavadocSourcesArtifact(mJavadocSourcesConfiguration)
      }
    }
  }

  /**
   * Registers the Javadoc and Sources configuration to the model.
   * @param configuration The Javadoc and Sources Configuration.
   */
  public void registerJavadocSourcesArtifact(Configuration configuration) {
    if (mDebugVariant != null) {
      mAndroidPlugin.registerJavaArtifact(SOURCES_JAVADOC_ARTIFACT_NAME,
          mDebugVariant,
          "dummyAssembleTaskName",
          "dummyJavaCompileTaskName",
          configuration,
          new File("dummyClassesFolder"),
          null)
    } else {
      mJavadocSourcesConfiguration = configuration
    }
  }
}
