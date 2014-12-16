#!/bin/sh

echo "Running plugin tests."
./gradlew clean check install --stacktrace
temp="$?"
if [ "$temp" -ne 0 ]
then
 echo "Error during the plugin tests. Gradle returned $temp."
 exit 1
fi
echo "Success in running the plugin tests. Now running the plugin in a dummy multi-project."
cd example
../gradlew clean test --stacktrace
temp="$?"
if [ "$temp" -ne 0 ]
then
 echo "Error during the dummy multi-project tests. Gradle returned $temp."
 exit 1
fi
echo "Plugin tested succesfully"
