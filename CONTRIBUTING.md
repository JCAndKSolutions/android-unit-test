Contributing
============

Thanks
------

Thanks for contributing to the project! This doc will guide you on to how to contribute to the project so the evolution goes faster and smooth.

Setup
-----

After cloning correctly our repo, to be able to run this project, you will need Java 7 or 8, IntelliJ IDEA IDE and Android's SDK. This project uses Gradle as a build tool so it will download itself thanks to the wrapper and all the dependencies of the project except for Android's which have to come from the SDK Manager.

It is recommended that you set your `JAVA_HOME` and `ANDROID_HOME` environment variables pointing to the correct paths before opening the project.

You will requiere to download through the Android SDK Manager the following:

- SDK Tools.
- SDK Platform-tools.
- SDK Build tools (check the example project to know which version).
- Android API (check the example project to know which version).
- Android support repository.
- Google repository.

With everything setup, you should be able to just open the project in IntelliJ IDEA.

Style Guide-lines
-----------------

TODO. Just try to follow the style already present.

Testing
-------

All contributions should pass all the tests which include unit tests of the plugin and integration tests in the example project. To run all the tests, you must run this simple script:

```bash
./scripts/run-tests.sh 
```

If you are on Windows, you can run the script through an emulator like Cygwin, or with git bash that normally comes shipped with Git but that is out of the scope of this guide. Otherwise, you can submit the PR and wait for Travis results but that would take considerably more time.

Submitting PRs
--------------

After you have made the changes following the style guidelines and testing correctly, you can commit your changes in git. Preferably, squash all the commits into one commit if you have made previous commits, unless the changes were big enough or require more organization in which case you can use mre than one commit. Just try to keep the commits as an coherent set of changes.

Please don't submit things like:

- Fixed issue 43
- Fixed typo
- Fixed styles
- Fixed issue 43 (again)
- Fixed failing tests after fixing issue 43

Finally, push your commits to your fork and submit a PR for review. If after the submittion the team suggests more changes, this changes should be also squashed to the previous commits and force pushed to the same branch. Github is intelligent enough to understand that anything under the branch selected during the creation of the PR is considered as what is being submitted, so you don't need to close the PR and make a new one, just squash and force pushIf it is a work in progress, you can push several commits until the work is done and reviewed by the team and then, just before merging, squash into coherent commits.

Thanks again and happy coding!
