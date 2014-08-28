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
    DependencyInjector.setProvider(createDependencyProvider(project))
    Logger.initialize(project.logger)
    MainHandler handler
    if (DependencyInjector.isAppPlugin()) {
      handler = createAppHandler()
    } else {
      handler = createLibraryHandler()
    }
    handler.run()
  }

  /**
   * Creates a Library Handler.
   * @return A LibraryHandler.
   */
  protected LibraryHandler createLibraryHandler() {
    return new LibraryHandler()
  }

  /**
   * Creates an App Handler.
   * @return An AppHandler.
   */
  protected AppHandler createAppHandler() {
    return new AppHandler()
  }

  /**
   * Creates a Dependency Provider.
   * @return A DependencyProvider.
   */
  protected DependencyProvider createDependencyProvider(Project project) {
    return new DependencyProvider(project)
  }
}
