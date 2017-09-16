#! /bin/bash

# declare TEST_APP_DIR=/testApps

# for filename in $TEST_APP_DIR; do
# 	adb install -r "testApps/$(basename $filename).apk";
# done

echo "Installing DeviceMetrics"
adb install -r testApps/DeviceMetrics.apk
echo "Installing Imgur"
adb install -r testApps/Imgur.apk
echo "Installing JsPerfTest"
adb install -r testApps/JsPerfTest.apk
echo "Installing Quizlet"
adb install -r testApps/Quizlet.apk
