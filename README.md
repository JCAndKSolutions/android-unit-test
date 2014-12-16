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

        classpath 'com.android.tools.build:gradle:1.0.0'
        classpath 'com.github.jcandksolutions.gradle:android-unit-test:2.1.0'
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
    testCompile 'org.robolectric:robolectric:2.4'
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
6.  Annotate your tests that need Robolectric with `@RunWith(RobolectricTestRunner.class)` or whatever custom runner you use.
7.  Run your tests:
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
      testReleaseBuildType true //Run tests for all the build types including non-debuggable (like the Release build type). Only works for Application projects, not Library projects.
      downloadDependenciesSources false //Download the sources.jar for the production dependencies. `true` by default.
      downloadDependenciesJavadoc true //Download the javadoc.jar for the production dependencies. `false` by default.
      downloadTestDependenciesSources false //Download the sources.jar for the test dependencies. `true` by default.
      downloadTestDependenciesJavadoc true //Download the javadoc.jar for the test dependencies. `false` by default.
      sourceSets { //Configures the test source sets.
        testBeta { //Specifies which source set to modify.
          java.srcDirs = ["src/testBeta/java"] //Sets the srcDirs for testBeta source files.
          resources.srcDirs(["src/testBeta/resources"]) //Adds to the srcDirs for testBeta resources.
        }
      }
      testTasks { //Configures the test tasks.
        all { //Configures all the test tasks with the same values.
          maxParallelForks = 4 //Allows to run tests in parallel by specifying how many threads to run tests on.
          forkEvery = 5 //Allow to group a number of tests for each thread to run.
          minHeapSize = '128m' //Specifies the minHeapSize.
          maxHeapSize = '1024m' //Specifies the maxHeapSize.
          jvmArgs = ['-XX:MaxPermSize=256m'] //Sets the jvmArgs of the test JVM.
          jvmArgs '-XX:MaxPermSize=256m' //Adds to the jvmArgs of the test JVM.
          excludes = 'src/notests/**' //Sets the filter to exclude tests.
          exclude 'src/notests/**' //Adds a filter to exclude tests.
          systemProperties = ['test.prop2': 'value2', 'test.prop3': 'value3'] //Sets the system properties for the test JVM.
          systemProperties(['test.prop2': 'value2', 'test.prop3': 'value3']) //Adds several system properties for the test JVM.
          systemProperty 'test.prop1', 'value1' //Adds one system property for the test JVM.
        }
        testFreeBetaDebug { //Configure the specific variant test JVM. Overwrites any values set by the all configuration.
          debug = true //Marks the test JVM for debugger attaching. This will pause the execution until a debugger is attached.
          include '**/*Test.class' //Adds a filter to include tests.
        }
      }
    }
    ```

Requirements
------------
- Gradle 2.1 or superior.
- Android's Gradle Plugin 0.14.0 or superior.
- If using Robolectric, it should be 2.3 or superior.
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

The wrapper should download Gradle 2.2.1. The example depends on Android's plugin version `1.0.0` which it will also download. Finally the example needs Android platform 21 and build tools 21.1.2. If you don't have them, you can either download them from the SDK Manager, or you can modify the build.gradle file and put the platform and build tools you use.

Integrating with Android Studio
-------------------------------
There is an Intellij plugin to integrate with Android Studio. Follow the instructions in their README.

- https://github.com/evant/android-studio-unit-test-plugin

Thanks To
---------

- Robolectric team for making an awesome test framework.
- Square's plugin that inspired this plugin: https://github.com/square/gradle-android-test-plugin.
- Evant's Android Studio plugin for making life easier.
- All contributors for helping this plugin grow.
