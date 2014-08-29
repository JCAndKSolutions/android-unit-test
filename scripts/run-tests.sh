#!/bin/sh

echo "Running plugin tests."
./gradlew clean check install
temp="$?"
if [ "$temp" -ne 0 ]
then
 echo "Error during the plugin tests. Gradle returned $temp."
 exit 1
fi
echo "Success in running the plugin tests. Now running the plugin in a dummy application project."
cd example
../gradlew clean test
temp="$?"
if [ "$temp" -ne 0 ]
then
 echo "Error during the dummy application project tests. Gradle returned $temp."
 exit 1
fi
echo "Success in running the dummy application tests. Now running the plugin in a dummy library project."
cd ../example-library
../gradlew clean test
temp="$?"
if [ "$temp" -ne 0 ]
then
 echo "Error during the dummy library project tests. Gradle returned $temp."
 exit 1
fi
echo "Plugin tested succesfully"
