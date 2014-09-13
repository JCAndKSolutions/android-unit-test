package org.robolectric;

import org.robolectric.res.FsFile;

public class AndroidManifestExt extends AndroidManifest {
  private static final String R = ".R";
  private String mPackageName;
  private boolean isPackageSet;

  public AndroidManifestExt(final FsFile androidManifestFile, final FsFile resDirectory, final FsFile assetsDirectory) {
    super(androidManifestFile, resDirectory, assetsDirectory);
  }

  @SuppressWarnings("ProhibitedExceptionDeclared")
  @Override
  public String getRClassName() throws Exception {
    if (isPackageSet) {
      parseAndroidManifest();
      return mPackageName + R;
    }
    return super.getRClassName();
  }

  @Override
  public String getPackageName() {
    if (isPackageSet) {
      parseAndroidManifest();
      return mPackageName;
    } else {
      return super.getPackageName();
    }
  }

  public void setPackageName(final String packageName) {
    mPackageName = packageName;
    isPackageSet = packageName != null;
  }
}
