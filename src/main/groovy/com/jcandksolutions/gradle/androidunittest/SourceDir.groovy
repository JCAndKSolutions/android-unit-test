package com.jcandksolutions.gradle.androidunittest

public class SourceDir {
  private boolean mOverWritten
  List<Object> mSource = new ArrayList<>()

  public void srcDir(Object srcDir) {
    mSource.add(srcDir)
  }

  public void setSrcDirs(Iterable<?> srcDirs) {
    mOverWritten = true
    mSource.clear()
    for (Object srcDir : srcDirs) {
      mSource.add(srcDir);
    }
  }

  public boolean isOverWritten() {
    return mOverWritten
  }

  public List<Object> getSrcDirs() {
    return mSource
  }
}
