package com.jcandksolutions.gradle.androidunittest

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Plugin implementation class.
 */
public class AndroidUnitTestPlugin implements Plugin<Project> {
  /**
   * Applies the plugin to the project.
   * @param project The project to apply the plugin to.
   */
  public void apply(Project project) {
    DependencyProvider provider = createDependencyProvider(project)
    MainHandler handler = provider.provideHandler()
    handler.run()
  }

  /**
   * Creates a Dependency Provider.
   * @return A DependencyProvider.
   */
  protected DependencyProvider createDependencyProvider(Project project) {
    return new DependencyProvider(project)
  }
}
