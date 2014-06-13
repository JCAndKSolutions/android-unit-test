package com.jcandksolutions.gradle.androidunittest

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ProjectDependency

import me.tatarka.androidunittest.model.AndroidUnitTest
import me.tatarka.androidunittest.model.AndroidUnitTestModel
import me.tatarka.androidunittest.model.Variant
import me.tatarka.androidunittest.model.VariantModel

class ModelBuilder {
  private String RPackageName
  private List<VariantBuilder> variants = []

  public void RPackageName(String name) {
    RPackageName = name
  }

  public void addSourceSet(VariantWrapper variantWrapper) {
    variants.add(new VariantBuilder(variantWrapper))
  }

  public void addConfig(Set<String> names, Configuration config) {
    variants.find { it.matches(names) }?.addDependencies(config)
  }

  public void addConfig(Configuration config) {
    variants*.addDependencies(config)
  }

  public AndroidUnitTest build() {
    return new AndroidUnitTestModel(RPackageName, variants*.build())
  }

  private class VariantBuilder {
    private final List<String> flavors;
    private final String buildType;
    private final String name;
    private final File manifest;
    private final Set<File> sourceDirectories;
    private final File resourcesDirectory;
    private final File assetsDirectory;
    private final File compileDestinationDir;
    private final Set<File> javaDependencies = new HashSet<File>();
    private final Set<String> projectDependencies = new HashSet<String>();

    VariantBuilder(VariantWrapper v) {
      flavors = v.flavorNames
      buildType = v.buildType
      name = buildName(flavors, buildType)
      manifest = v.mergedManifest
      sourceDirectories = v.sourceDirs
      resourcesDirectory = v.mergedResourcesDir
      assetsDirectory = v.mergedAssetsDir
      compileDestinationDir = v.compileDestinationDir
    }

    boolean matches(Set<String> names) {
      for (String name : names) {
        if (!(buildType == name || flavors.contains(name))) {
          return false;
        }
      }
      return true;
    }

    void addDependencies(Configuration config) {
      javaDependencies.addAll(config.files)

      for (Dependency dependency : config.getAllDependencies()) {
        if (dependency instanceof ProjectDependency) {
          projectDependencies.add(dependency.dependencyProject.path);
        }
      }
    }

    private static String buildName(List<String> flavors, String buildType) {
      if (flavors.isEmpty()) {
        buildType;
      } else {
        Iterator<String> flavorItr = flavors.iterator();
        StringBuilder sb = new StringBuilder(flavorItr.next());
        while (flavorItr.hasNext()) {
          sb.append(flavorItr.next().capitalize());
        }
        sb.append(buildType.capitalize());
        sb.toString();
      }
    }

    Variant build() {
      return new VariantModel(name, manifest, sourceDirectories, resourcesDirectory, assetsDirectory,
          javaDependencies, projectDependencies, compileDestinationDir)
    }
  }
}
