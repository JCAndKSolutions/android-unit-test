package com.jcandksolutions.gradle.androidunittest

/**
 * Simple wrapper class that handles a List of Objects representing a sourceDir.
 */
public class SourceDir {
  private boolean mOverWritten
  private List<Object> mSource = new ArrayList<>()
  /**
   * Add an object to the sourceDir.
   * @param srcDir Object to add to the sourceDir.
   */
  public void srcDir(Object srcDir) {
    mSource.add(srcDir)
  }

  /**
   * Add several objects to the sourceDir.
   * @param srcDirs Objects to add to the sourceDir.
   */
  public void setSrcDirs(Iterable<?> srcDirs) {
    mOverWritten = true
    mSource.clear()
    for (Object srcDir : srcDirs) {
      mSource.add(srcDir);
    }
  }

  /**
   * Retrieves the property OverWritten which tells if the sourceDir was overwritten or just added.
   * @return {@code true} if overwritten, {@code false} otherwise.
   */
  public boolean isOverWritten() {
    return mOverWritten
  }

  /**
   * Retrieves the list representing the sourceDir.
   * @return The sourceDir.
   */
  public List<Object> getSrcDirs() {
    return mSource
  }
}
