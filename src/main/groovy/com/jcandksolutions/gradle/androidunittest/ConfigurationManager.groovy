package com.jcandksolutions.gradle.androidunittest

import com.android.build.gradle.BaseExtension
import com.android.builder.core.DefaultBuildType
import com.android.builder.core.DefaultProductFlavor
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.query.ArtifactResolutionQuery
import org.gradle.api.component.Artifact
import org.gradle.api.internal.artifacts.component.DefaultModuleComponentIdentifier
import org.gradle.api.logging.Logger
import org.gradle.language.base.artifact.SourcesArtifact
import org.gradle.language.java.artifact.JavadocArtifact
import org.gradle.runtime.jvm.JvmLibrary

/**
 * Class that manages the creation of the Configurations for the different source sets.
 */
public class ConfigurationManager {
  public static final String TEST_COMPILE = 'testCompile'
  public static final String SOURCES_JAVADOC = '_SourcesJavadoc_'
  public static final String COMPILE = 'compile'
  private final BaseExtension mAndroidExtension
  private final ConfigurationContainer mConfigurations
  private final Logger mLogger
  private final AndroidUnitTestPluginExtension mPluginExtension
  private final Project mProject
  private final ModelManager mModelManager
  /**
   * Instantiates a ConfigurationManager.
   * @param androidExtension The AndroidExtension.
   * @param configurations The Configurations of the project.
   * @param project The Project.
   * @param pluginExtension The Plugin extension.
   * @param modelManager The Model Manager.
   * @param logger The Logger.
   */
  public ConfigurationManager(BaseExtension androidExtension, ConfigurationContainer configurations, Project project, AndroidUnitTestPluginExtension pluginExtension, ModelManager modelManager, Logger logger) {
    mAndroidExtension = androidExtension
    mConfigurations = configurations
    mProject = project
    mPluginExtension = pluginExtension
    mModelManager = modelManager
    mLogger = logger
  }

  /**
   * Creates new test configurations for each flavor so the user can set dependencies for the
   * different source sets.
   */
  public void createNewConfigurations() {
    mLogger.info("----------------------------------------")
    mLogger.info("Found configurations:")
    List<String> buildTypeConfigNames = buildTypeConfigList
    List<String> flavorConfigNames = flavorConfigList
    mLogger.info("----------------------------------------")
    mLogger.info("Creating new configurations:")
    List<String> buildTypeTestConfigNames = createTestConfigurations(buildTypeConfigNames)
    List<String> flavorTestConfigNames = createTestConfigurations(flavorConfigNames)
    createTestCompileTaskConfiguration()
    mProject.afterEvaluate {
      createSourcesJavadocConfiguration(buildTypeConfigNames, flavorConfigNames, buildTypeTestConfigNames, flavorTestConfigNames)
    }
  }

  private void createTestCompileTaskConfiguration() {
    Configuration testCompileTaskConfiguration = mConfigurations.create(TEST_COMPILE)
    testCompileTaskConfiguration.extendsFrom mConfigurations.getByName(COMPILE)
    mLogger.info(TEST_COMPILE)
  }

  private List<String> createTestConfigurations(final List<String> configNames) {
    List<String> testConfigNames = []
    configNames.each { String configName ->
      String testConfigName = "test${configName.capitalize()}"
      mLogger.info(testConfigName)
      mConfigurations.create(testConfigName)
      testConfigNames.add(testConfigName)
    }
    return testConfigNames
  }

  private void createSourcesJavadocConfiguration(final List<String> buildTypeConfigNames,
                                                 final List<String> flavorConfigNames,
                                                 final List<String> buildTypeTestConfigNames,
                                                 final List<String> flavorTestConfigNames) {
    if (mPluginExtension.downloadTestDependenciesSources || mPluginExtension.downloadTestDependenciesJavadoc || mPluginExtension.downloadDependenciesSources || mPluginExtension.downloadDependenciesJavadoc) {
      Configuration testSourcesJavadocConfiguration = mConfigurations.detachedConfiguration()
      copyDependencies(testSourcesJavadocConfiguration, [COMPILE], mPluginExtension.downloadDependenciesSources, mPluginExtension.downloadDependenciesJavadoc)
      copyDependencies(testSourcesJavadocConfiguration, buildTypeConfigNames, mPluginExtension.downloadDependenciesSources, mPluginExtension.downloadDependenciesJavadoc)
      copyDependencies(testSourcesJavadocConfiguration, flavorConfigNames, mPluginExtension.downloadDependenciesSources, mPluginExtension.downloadDependenciesJavadoc)
      copyDependencies(testSourcesJavadocConfiguration, [TEST_COMPILE], mPluginExtension.downloadTestDependenciesSources, mPluginExtension.downloadTestDependenciesJavadoc)
      copyDependencies(testSourcesJavadocConfiguration, buildTypeTestConfigNames, mPluginExtension.downloadTestDependenciesSources, mPluginExtension.downloadTestDependenciesJavadoc)
      copyDependencies(testSourcesJavadocConfiguration, flavorTestConfigNames, mPluginExtension.downloadTestDependenciesSources, mPluginExtension.downloadTestDependenciesJavadoc)
      testSourcesJavadocConfiguration.resolve()
      mModelManager.registerJavadocSourcesArtifact(testSourcesJavadocConfiguration)
    }
  }

  private void copyDependencies(Configuration testConfiguration, List<String> configNames, boolean sources, boolean javadoc) {
    Class<? extends Artifact>[] artifactTypes = getArtifactTypes(sources, javadoc)

    if (artifactTypes.length == 0) {
      return
    }

    configNames.each { String configName ->
      Configuration conf = mConfigurations.getByName(configName)
      Set<ModuleComponentIdentifier> componentIdentifiers = getExternalArtifacts(conf).collect {
        artifact -> toComponentIdentifier(artifact.moduleVersion.id)
      }

      ArtifactResolutionQuery query = mProject.dependencies.createArtifactResolutionQuery();
      query.forComponents(componentIdentifiers)
      query.withArtifacts(JvmLibrary.class, artifactTypes)

      query.execute().resolvedComponents.each { component ->
        component.getArtifacts(SourcesArtifact.class).each { artifact ->
          if (artifact instanceof ResolvedArtifact) {
            testConfiguration.add(mProject.files(artifact.file))
          }
        }
        component.getArtifacts(JavadocArtifact.class).each { artifact ->
          if (artifact instanceof ResolvedArtifact) {
            testConfiguration.add(mProject.files(artifact.file))
          }
        }
      }
    }
  }
  
  private static Class<? extends Artifact>[] getArtifactTypes(boolean sources, boolean javadoc) {
    List<Class<? extends Artifact>> artifactTypes = new ArrayList<Class<? extends Artifact>>(2);
    if (sources) {
      artifactTypes.add(SourcesArtifact.class);
    }

    if (javadoc) {
      artifactTypes.add(JavadocArtifact.class);
    }
    @SuppressWarnings("unchecked") 
    Class<? extends Artifact>[] artifactTypesArray = (Class<? extends Artifact>[]) new Class<?>[artifactTypes.size()];
    return artifactTypes.toArray(artifactTypesArray)
  }

  private static Set<ResolvedArtifact> getExternalArtifacts(Configuration configuration) {
    return configuration.resolvedConfiguration.firstLevelModuleDependencies*.moduleArtifacts.flatten()
  }

  private static ModuleComponentIdentifier toComponentIdentifier(ModuleVersionIdentifier id) {
    return new DefaultModuleComponentIdentifier(id.group, id.name, id.version);
  }

  private List<String> getFlavorConfigList() {
    List<String> flavorConfigNames = []
    mAndroidExtension.productFlavors.each { DefaultProductFlavor flavor ->
      String configName = "${flavor.name}Compile"
      mLogger.info(configName)
      flavorConfigNames.add(configName)
    }
    return flavorConfigNames
  }

  private List<String> getBuildTypeConfigList() {
    List<String> buildTypeConfigNames = []
    mAndroidExtension.buildTypes.each { DefaultBuildType buildType ->
      String configName = "${buildType.name}Compile"
      mLogger.info(configName)
      buildTypeConfigNames.add(configName)
    }
    return buildTypeConfigNames
  }
}
