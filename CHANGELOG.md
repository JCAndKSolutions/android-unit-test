ChangeLog
==========

Version 1.0.0
----------------------------
2013-09-30

- Initial release.

Version 1.0.1
----------------------------
2013-11-07

- Fixed test flags not working.

Version 1.1.0
-----------------------------
2014-05-29

- Fixed search pattern forcing an `*` at the end.
- Allow the use of plugins that extend the Android Gradle app plugin.
- Fix Android Stub! Exception because of ordering of classpath.
- Support for Gradle Android Plugin version 0.10.+ (The plug in no longer works with older version of Android's plugin).

Version 1.2.0
-----------------------------
2014-06-07

- Fixed java.lang.NoClassDefFoundError: com.android.builder.BuilderConstants.

Version 1.2.1
-----------------------------
2014-06-09

- Changed the source directory for resources since Gradle Android plugin changed its location.

Version 1.2.2
-----------------------------
2014-06-13

- Implement a model to communicate with a plugin.

Version 1.2.3
-----------------------------
2014-07-11

- Make use of the Android's plugin model to simplify code and also to fix the adding of erroneous configurations.

Version 1.3.0
-----------------------------
2014-07-30

- Allow the use of a plugin extension to configure if the non debugable build types (A.K.A. Release build type) should generate tests.

Version 1.4.0
-----------------------------
2014-08-13

- Add support for Android library projects.

Version 1.5.0
-----------------------------
2014-08-29

- Switch to Android's Plugin modeling API to avoid having an extra dependency.
- Refactored the code completely to a more OO design with unit tests and integration with Travis CI.

Version 1.5.1
-----------------------------
2014-09-13

- Fix bug that only happened in Multi-Projects.

Version 1.6.0
-----------------------------
2014-09-22

- Add the option to download Javadoc and Sources for the dependencies. Both project and test dependencies. It will only download the sources by default.

Version 1.6.1
-----------------------------
2014-09-22

- Fix an exception when there where no javadoc or sources in the repo.

Version 1.6.2
-----------------------------
2014-10-02

- Fix resources not being copied because of wrong order of task dependencies on a clean test.

Version 1.6.3
-----------------------------
2014-10-17

- Add check to not include a dependency more than once to download sources or javadoc.

Version 2.0.0
-----------------------------
2014-11-03

- Update to Gradle 2.1, Android plugin 0.14.0, build tools 21.1.0, android API 21 and coveralls plugin 2.0.0.

Version 2.0.1
-----------------------------
2014-11-04

- Fix Unsupported major.minor version 52.0 exception for Java 7.

Version 2.0.2
-----------------------------
2014-11-20

- Added support for new split API in Android plugin.

Version 2.1.0
-----------------------------
2014-12-16

- Added ability to configure source sets and test tasks.
- Removed the resource copying task. This forces to use Robolectric 2.3 and superior or hack the copy task yourself for Robolectric 2.2 and lower.

Version 2.1.1
-----------------------------
2014-12-16

- Fixed critical bug that prevented the plugin to work on Java 7.
