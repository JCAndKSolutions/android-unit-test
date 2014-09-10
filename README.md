Android Unit Test
=================
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.jcandksolutions.gradle/android-unit-test/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.jcandksolutions.gradle/android-unit-test)
[![Build Status](https://travis-ci.org/JCAndKSolutions/android-unit-test.svg?branch=master)](https://travis-ci.org/JCAndKSolutions/android-unit-test)
[![Coverage Status](https://coveralls.io/repos/JCAndKSolutions/android-unit-test/badge.png?branch=master)](https://coveralls.io/r/JCAndKSolutions/android-unit-test?branch=master)

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

        classpath 'com.android.tools.build:gradle:0.12.+'
        classpath 'com.github.jcandksolutions.gradle:android-unit-test:1.5.+'
      }
    }
    ```
2.  Apply the `android-unit-test` plugin **AFTER** you declare the Android's plugin and configure it. Like this:

    ```groovy
    apply plugin: 'com.android.application'

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
5.  Preferably, Add the applicationId in the `android.defaultConfig` section. This is because the `R.java` file is always generated under this package name and Robolectric will try to read the resources from this package name. If you specify a different package name for your flavor, Robolectric would think the R.java class is under this package name. To solve this, the plugin reads the main package name and injects it as a system property so the custom runner can initialize Robolectric correctly. If you don't specify this ID, the plugin will try to read it from the main manifest. For example:

    ```groovy
    android {
      ...
      defaultConfig {
        applicationId "com.example"
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

    **Note:** Using any of this two flags will override the default pattern of `**/*Test.class` which would mean that your tests will not have to end in `*Test.java` to be recognized, so pay attention!

9.  Read the results. Gradle generates reports and results in `build/test-report/` and `build/test-results/` respectively. Each variant will have its independent report. For example `build/test-report/freebeta/debug/index.html`. But there will be a merged report with all tests in `build/test-report/index.html`.
10.  Optionally, you can use the plugin's extension to configure some options. For example:

    ```groovy
    apply plugin: 'android-unit-test'

    androidUnitTest {
      testReleaseBuildType true
    }
    ```
    The only current option available is `testReleaseBuildType` which will allow you to run tests for all the build types that are not debugable (like the Release build type). This only works for Application projects, not library projects. Library projects will execute with the Build Type specified to be used by the instrumentation tests of android. This is because we use the test variant from the android plugin to merge the library resources, something the application projects don't need.

Requirements
------------
- Gradle 1.10 or superior.
- Android's Gradle Plugin.
- An Android app or library that builds with Gradle.

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

The wrapper should download Gradle 1.12. The example depends on Android's plugin version `0.12.+` which it will also download. Finally the example needs Android platform 20 and build tools 20. If you don't have them, you can either download them from the SDK Manager, or you can modify the build.gradle file and put the platform and build tools you use.

Integrating with Android Studio
-------------------------------
There is an Intellij plugin to integrate with Android Studio. Otherwise, there are two hacks that will make Android Studio recognize the source paths, dependencies and the second one will also allow to use the JUnit integrated tests (Keep in mind that the integrated JUnit tests are not the same tests executed by gradle so results may vary):

**Plugin**:

- https://github.com/evant/android-studio-unit-test-plugin

**Scripted**:

- https://github.com/sababado/gradle-android-add-dir

**Manual**:

1.  Declare new dependencies to your project in the `.idea/libraries` directory, for example robolectric_2_3_SNAPSHOT_jar_with_dependencies.xml:

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

2.  Declare a false dependency in the same folder to the Android SDK to cheat the JUnit tests into reading the android SDK after the JDK, for example android.xml:

    ```xml
    <component name="libraryTable">
      <library name="android">
        <CLASSES>
          <root url="jar://$APPLICATION_HOME_DIR$/sdk/platforms/android-20/android.jar!/" />
        </CLASSES>
        <JAVADOC />
        <SOURCES />
      </library>
    </component>
    ```

3.  Open the `.iml` file of the project that uses the plugin.
4.  Add each source directory that you need inside the content tag. For example:

    ```xml
    <content url="file://$MODULE_DIR$">
      ...
      <sourceFolder url="file://$MODULE_DIR$/src/test/java" isTestSource="true" />
      ...
    </content>
    ```

5.  Register the dependencies for the test sourceSets inside the component tag like this (Notice the false android library dependency):

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
      <orderEntry type="library" scope="TEST" name="android" level="project" />
    </component>
    ```

6.  Modify the test output so it knows where to find the tests. Keep in mind that multiple flavors will have multiple outputs and you'll probably have to change this frequently. Assuming no flavor, add this under `component name="NewModuleRootManager"`:

    ```xml
    <output-test url="file://$MODULE_DIR$/build/test-classes/debug" />
    ```

7.  Be sure to have the `.iml` file under version control since every time you open Android Studio it will erase your changes. Having it under version control will allow you to simply revert the changes that Android Studio does instead of manually modifying the file each time.
8.  Open the Run/Debug Configurations dialog.
9.  Create a new Gradle Configuration. Name it however you want. In `Gradle project:` select the root project path. In tasks, add all the assemble tasks of any project dependency like a library. and then the `Classes` task depending on the flavors you have. For example:

    ```
    Gradle Project: C:/Sources/Android
    Tasks: androidLibrary1:assemble androidLibrary2:assemble androidApp:testDebugClasses (androidApp:testFlavor1DebugClasses if you have flavors)
    ```

10.  Create a new JUnit Configuration: in `Directory:` point to the source folder of the tests (you may have to create one configuration for each flavor). In `Working directory:` set to root project dir. In `Use classpath of module:` Choose the app module. Activate `Use alternate JRE:` and select a standard JDK or JRE. In `Before launch:` remove the build task and add the Gradle configuration you made in the previous step.
11.  You can run (even debug) your tests in two ways. The easiest way is to select the JUnit configuration made in the previous step and click `Run` (or `Debug`) button, do notice however that this tests are not being executed throw gradle, they are using the same class files however (That is why we executed the testDebugClasses task). The other way is to execute the actual gradle test task that the plugin makes. From the Gradle tab. Just select the `check` task, right click it, and select `Run 'Project [check]'` (or `Debug 'Project [check]'`). If you are debugging tests in this way, you will have to uncheck the `Use in-process build` in the Compiler > Gradle settings. Finally, you can also use the terminal tab to execute directly the task but this will not let you debug (unless you use yet another hack to attach the debugger which I will not cover here).

F.A.Q.
------

1.  Q: Why is there a `build/test-resources/VariantFullName/res/` directory that has exactly the same as `build/res/all/variantflavors/buildtype/`?

    A: Robolectric 2.0 to 2.1 had a bug that made paths with a keyword in them (like `menu`, `layout`, etc.) to parse incorrectly. In Robolectric 2.2 there was a patch to fix this that forced the resource path to finish with `res/` since Eclipse projects have that structure. The problem is that gradle projects don't have that structure. Because of this the plugin has to copy all the merged resources to a new folder that has a `res/` directory just before the actual resources. This restriction has since been fixed in Robolectric 2.3-SNAPSHOT, but to stay backward compatible, the plugin will have to keep copying the resources to `build/test-resources/`.

Thanks To
---------

- Robolectric team for making an awesome test framework.
- Square's plugin that inspired this plugin: https://github.com/square/gradle-android-test-plugin.
