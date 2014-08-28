#!/bin/sh

echo "Running plugin tests."
./gradlew clean check install
if [ "$?" -ne 0 ]
then
 echo "Error during the plugin tests."
 exit 1
fi
echo "Success in running the plugin tests. Now running the plugin in a dummy application project."
cd example
../gradlew clean test
if [ "$?" -ne 0 ]
then
 echo "Error during the dummy application project tests."
 exit 1
fi
echo "Success in running the dummy application tests. Now running the plugin in a dummy library project."
cd ../example-library
../gradlew clean test
if [ "$?" -ne 0 ]
then
 echo "Error during the dummy library project tests."
 exit 1
fi
echo "Plugin tested succesfully"
