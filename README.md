Android Unit Test
==================================

A Gradle plugin to add unit testing to the android plugin. Prepared for Robolectric.

Usage
-----

1.- Add the plugin to the buildscript's dependencies. For example:

```groovy
buildscript {
  dependencies {
    repositories {
      mavenCentral()
    }

    classpath 'com.android.tools.build:gradle:0.5.+'
    classpath 'com.github.jcandksolutions.gradle:android-unit-test:1.0.+'
  }
}
```

2.- Apply the `android-unit-test` plugin AFTER you declare the android plugin and configure it:

```groovy
apply plugin: 'android'

android {
  ...
}

apply plugin: 'android-unit-test'
```

3.- Add dependencies. The plugin adds several configurations. It adds a test configuration `testCompile`, one for each build type (Debug, ...) and one for each flavor declared in the android extension. For example:

```groovy
testCompile 'junit:junit:4.10'
testCompile 'org.robolectric:robolectric:2.1.+'
testDebugCompile 'org.debugonly.dependency'
testFreeflavorCompile 'Admob.jar'
```

4.- Add tests. The plugin adds several source sets. A master test source set `src/test/java`, a source set for each build type except release, a source set for each flavor, a source set for each combination of flavor and build type, a source set for each flavor group and a source set for each combination of flavor group and build type. Example:

```
src/test/java/MainTest.java  //main test source set
src/testDebug/java/DebugOnlyTest.java  //for tests that require to be in debug
src/testFree/java/FreeOnlyTest.java  //for the free flavor tests
src/testFreeBeta/java/FreeBetaVariantOnlyTest.java  // for the FreeBeta flavors of different flavor groups
src/testFreeDebug/java/FreeDebugTest.java  //for free flavor tests that requiere to be in debug
src/testFreeBetaDebug/java/FreeBetaDebugTest.java  //for the FreeBeta flavors that requiere to be in debug
...
```

5.- Add the main package name in the android.defaultConfig section. This is because the R.java file is always generated under this package name and robolectric will try to read the resources from this package name. If you specify a different package name for your flavor, robolectric would think the R.java class is under this package name. to Solve this, The plugin reads the main package name and injects it as a system property so the custom runner can initialize robolectric correctly. Example:

```groovy
android {
  ...
  defaultConfig {
    packageName "com.example"
  }
}
```

6.- Use or extend the custom Robolectric runner and AndroidManifest classes:

RobolectricGradleTestRunner:

```groovy
package com.example;

import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.AndroidManifestExt;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;

public class RobolectricGradleTestRunner extends RobolectricTestRunner {
  public RobolectricGradleTestRunner(final Class<?> testClass) throws InitializationError {
    super(testClass);
  }

  @Override
  protected AndroidManifest getAppManifest(final Config config) {
    final String manifestProperty = System.getProperty("android.manifest");
    if (config.manifest().equals(Config.DEFAULT) && manifestProperty != null) {
      final String resProperty = System.getProperty("android.resources");
      final String assetsProperty = System.getProperty("android.assets");
      final String packageProperty = System.getProperty("android.package");
      final AndroidManifestExt a = new AndroidManifestExt(Fs.fileFromPath(manifestProperty), Fs.fileFromPath(resProperty), Fs.fileFromPath(assetsProperty));
      a.setPackageName(packageProperty);
      return a;
    }
    return super.getAppManifest(config);
  }
}

```

AndroidManifestExt:

```groovy
package org.robolectric;

import org.robolectric.res.FsFile;

public class AndroidManifestExt extends AndroidManifest {
  private static final String R = ".R";
  private String mPackageName;
  private boolean isPackageSet;

  public AndroidManifestExt(final FsFile androidManifestFile, final FsFile resDirectory, final FsFile assetsDirectory) {
    super(androidManifestFile, resDirectory, assetsDirectory);
  }

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

```

7.- Annotate your tests that need Robolectric with `@RunWith(RobolectricGradleTestRunner.class)` or a subclass if you extended it.

Thanks
-------

To Robolectric team for making an awesome tool

To Square's plugin that inspired this plugin: https://github.com/square/gradle-android-test-plugin
