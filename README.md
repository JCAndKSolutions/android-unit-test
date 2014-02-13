Android Unit Test
=================
A Gradle plugin to add unit testing to the Android's plugin. Prepared for Robolectric.

Usage
-----
1.  Add the plugin to the buildscript's dependencies like this:

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
2.  Apply the `android-unit-test` plugin **AFTER** you declare the Android's plugin and configure it. Like this:

    ```groovy
    apply plugin: 'android'

    android {
      ...
    }

    apply plugin: 'android-unit-test'
    ```
3.  Add dependencies. The plugin adds several configurations. It adds a test configuration `testCompile`, one for each build type (Debug, ...) and one for each flavor declared in the Android extension. For example:

    ```groovy
    testCompile 'junit:junit:4.10'
    testCompile 'org.robolectric:robolectric:2.3.+'
    testDebugCompile 'org.debugonly.dependency'
    testFreeCompile 'Admob.jar'
    ```
4.  Add tests. The plugin adds several source sets. A master test source set `src/test/java`, a source set for each build type except release, a source set for each flavor, a source set for each combination of flavor and build type, a source set for each flavor group and a source set for each combination of flavor group and build type. For example:
    - Main tests: `src/test/java/*Test.java`
    - Debug build type tests: `src/testDebug/java/*Test.java`
    - Free flavor tests: `src/testFree/java/*Test.java`
    - FreeBeta grouped flavors (different flavor groups): `src/testFreeBeta/java/*Test.java`
    - Free flavor & Debug build type: `src/testFreeDebug/java/*Test.java`
    - FreeBeta grouped flavors & Debug build type: `src/testFreeBetaDebug/java/*Test.java`

    **Warning: All tests must end in `*Test.java`, otherwise, the plugin will not detect them as tests!!!**
5.  Add the main package name in the `android.defaultConfig` section. This is because the `R.java` file is always generated under this package name and Robolectric will try to read the resources from this package name. If you specify a different package name for your flavor, Robolectric would think the R.java class is under this package name. To solve this, The plugin reads the main package name and injects it as a system property so the custom runner can initialize Robolectric correctly. Like this:

    ```groovy
    android {
      ...
      defaultConfig {
        packageName "com.example"
      }
    }
    ```
6.  If you are using Robolectric 2.0 to 2.2 (2.3 no longer needs it), you will need a custom Robolectric runner and AndroidManifest classes:
    - RobolectricGradleTestRunner:

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
          final AndroidManifestExt a = new AndroidManifestExt(Fs.fileFromPath(manifestProperty),
              Fs.fileFromPath(resProperty), Fs.fileFromPath(assetsProperty));
          a.setPackageName(packageProperty);
          return a;
        }
        return super.getAppManifest(config);
      }
    }
    ```
    - AndroidManifestExt:

    ```groovy
    package org.robolectric;

    import org.robolectric.res.FsFile;

    public class AndroidManifestExt extends AndroidManifest {
      private static final String R = ".R";
      private String mPackageName;
      private boolean isPackageSet;

      public AndroidManifestExt(final FsFile androidManifestFile, final FsFile resDirectory,
          final FsFile assetsDirectory) {
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
7.  Annotate your tests that need Robolectric with either `@RunWith(RobolectricTestRunner.class)` if using Robolectric 2.3, `@RunWith(RobolectricGradleTestRunner.class)` if using Robolectric 2.0 to 2.2, or whatever custom runner you use.
8.  Run your tests:
    - Run `gradlew test` to run the JUnit tests only.
    - Run `gradlew check` to run both JUnit tests and instrumentation tests.
    - Run `gradlew testPaidNormalDebug` to run a single variant tests.

    Optionally you can use system properties to select a subset of the tests to run:
    - `-Dtest.single` property will set a include pattern for all variants.
    - `-DtestPaidNormalDebug` property will set a include pattern only for that specific variant.

    For example:
    - `gradlew test -Dtest.single=NormalTest` would run all variants but only the variants with the Normal flavor would find this test and run it.
    - `gradlew testPaidNormalDebug -DtestPaidNormalDebug.single=NormalTest` would only run the PaidNormalDebug variant and only the test NormalTest.
    - `gradlew testPaidNormalDebug -Dtest.single=NormalTest` would achieve the same of the above one.
    - `gradlew testPaidNormalDebug -Dtest.single=BetaTest` would not find any test to run and pass with 0 errors.
    - `gradlew test -Dtest.single=NormalTest -DtestPaidNormalDebug.single=PaidTest` would run the NormalTest in all variants that has it and PaidTest only in PaidNormalDebug variant.
    
    **Note:** Using any of this two flags will override the default patter of `**/*Test.class` which would mean that your tests will not have to end in `*Test.java` to be recognized, so pay attention!

9.  Read the results. Gradle generates reports and results in `build/test-report/` and `build/test-results/` respectively. Each variant will have its independent report. For example `build/test-report/freebeta/debug/index.html`. But there will be a merged report with all tests in `build/test-report/index.html`.

Requirements
------------
- Gradle 1.6 or superior.
- Android's Gradle Plugin.
- An Android app that builds with Gradle.

Android Library
---------------
Currently, the `android-library` plugin is not supported directly. This is because the Android Library project doesn't implement resource merging. If resources aren't merged, then, Robolectric will not be able to find them and will crash.

There is a work around however:

1. Make a new app project.
2. Add a simple manifest.
3. Follow the instructions 1 to 3 of the Usage section.
4. Add the library project as a dependency of the app project.
5. Add the tests in the app project.
6. Follow the instructions 5 to 7 of the Usage section.
7. Run the `gradlew test` or `gradlew check` command from the app project, not the library project.

If you are like me and:

- Have several app projects using your library.
- Have separate repositories for library and apps. And,
- Want to run the library tests with the apps test but don't want to duplicate the library tests.

Then you may put the tests in the library project's repository, and add the path to the app's test source set so it gets compiled and tested when the app is tested. For example:

```groovy
// Be sure to modify the source sets after projects are evaluated, otherwise they won't exist yet.
gradle.projectsEvaluated {
  sourceSets.each { set ->
    // Look for all the source sets you want to run the tests. There is one for each variant.
    if (set.name.equals('testFreeBetaDebug')) {
      // Add the path to your library tests
      set.java.srcDir '/library/src/test/java'
    }
    // Repeat
    if (set.name.equals('testPaidBetaDebug')) {
      set.java.srcDir '/library/src/test/java'
    }
    ...
    // do all customization you want
  }
}
```

Kind of like a hack but at least you can test your library at the same time as your app and test interactions if you want.

Running the Sample App
-------------------
To run the sample app you'll need to first install the SNAPSHOT version of this plugin. This is easily done with a simple instruction:

```bash
cd /pathToProject
gradlew install
```

After the installation of the SNAPSHOT version you can run the example with another simple command:

```bash
cd /pathToProject/example
../gradlew check
```

As you can notice, the example is run with the gradle wrapper of the main project. Hence the need of `../` (`..\` on Windows) to run the wrapper inside the example dir.

The wrapper should download Gradle 1.8. The example depends on Android's plugin version 0.6.+ which it will also download. Finally the example needs Android platform 19 and build tools 19. If you don't have them, you can either download them from the SDK Manager, or you can modify the build.gradle file and put the platform and build tools you use.

Integrating with Android Studio
-------------------------------
There is currently no way to automatically integrate with Android Studio. There is a hack however:

1.  Open the `.iml` file of the project that uses the plugin.
2.  Add each source directory that you need inside the content tag. For example:

    ```xml
    <content url="file://$MODULE_DIR$">
      ...
      <sourceFolder url="file://$MODULE_DIR$/src/test/java" isTestSource="true" />
      ...
    </content>
    ```
4.  Declare new dependencies to your project in the `.idea/libraries` directory, for example robolectric_2_3_SNAPSHOT_jar_with_dependencies.xml:

    ```xml
    <component name="libraryTable">
      <library name="robolectric-2.3-SNAPSHOT-jar-with-dependencies">
        <CLASSES>
          <root url="jar://$MAVEN_REPOSITORY$/org/robolectric/robolectric/2.3-SNAPSHOT/robolectric-2.3-SNAPSHOT-jar-with-dependencies.jar!/" />
        </CLASSES>
        <JAVADOC />
        <SOURCES>
          <root url="jar://$MAVEN_REPOSITORY$/org/robolectric/robolectric/2.3-SNAPSHOT/robolectric-2.3-SNAPSHOT-jar-with-dependencies.jar!/" />
          <root url="jar://$MAVEN_REPOSITORY$/org/robolectric/robolectric/2.3-SNAPSHOT/robolectric-2.3-SNAPSHOT-sources.jar!/" />
        </SOURCES>
      </library>
    </component>
    ```
4.  Register the dependencies for the test sourceSets inside the component tag like this:

    ```xml
    <component name="NewModuleRootManager" inherit-compiler-output="false">
      <content url="file://$MODULE_DIR$">
      ...
      </content>
      ...
      <orderEntry type="library" scope="TEST" name="fest-android-1.0.7" level="project" />
      <orderEntry type="library" scope="TEST" name="fest-assert-core-2.0M10" level="project" />
      <orderEntry type="library" scope="TEST" name="junit-4.10" level="project" />
      <orderEntry type="library" scope="TEST" name="mockito-core-1.9.5" level="project" />
      <orderEntry type="library" scope="TEST" name="robolectric-2.3-SNAPSHOT-jar-with-dependencies" level="project" />
    </component>
    ```
5.  Be sure to have the `.iml` file under version control since every time you open Android Studio it will erase your changes. Having it under version control will allow you to simply revert the changes that Android Studio does instead of manually modifying the file each time.
6.  You can run (even debug) your tests from the gradle tab. Just select the `check` task, right click it, and select `Run 'Project [check]'` (or `Debug 'Project [check]'`). You can also use the terminal tab to execute directly.

F.A.Q.
------

1.  Q: Why is there a `build/test-resources/VariantFullName/res/` directory that has exactly the same as `build/res/all/variantflavors/buildtype/`?

    A: Robolectric 2.0 to 2.1 had a bug that made paths with a keyword in them (like `menu`, `layout`, etc.) to parse incorrectly. In Robolectric 2.2 there was a patch to fix this that forced the resource path to finish with `res/` since Eclipse projects have that structure. The problem is that gradle projects don't have that structure. Because of this the plugin has to copy all the merged resources to a new folder that has a `res/` directory just before the actual resources. This restriction has since been fixed in Robolectric 2.3-SNAPSHOT, but to stay backward compatible, the plugin will have to keep copying the resources to `build/test-resources/`.

Thanks To
---------

- Robolectric team for making an awesome test framework.
- Square's plugin that inspired this plugin: https://github.com/square/gradle-android-test-plugin.
