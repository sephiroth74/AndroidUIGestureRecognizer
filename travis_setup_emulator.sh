#!/usr/bin/env bash

set -e

echo "TEST_TYPE=$TEST_TYPE"
echo "EMULATOR_TAG=$EMULATOR_TAG"
echo "EMULATOR_API=$EMULATOR_API"
echo "ABI=$ABI"

if [ "$TEST_TYPE" == "unit" ]; then
  echo "This is unit test run, skipping emulator set up."
elif [ "$TEST_TYPE" == "instrumentation" ]; then
  echo "Waiting for emulator setup..."
  android-wait-for-emulator
  adb devices
  adb shell input keyevent 82 &
  # Avoid having it lock itself again.
  adb shell svc power stayon true
  adb logcat | grep -F "`adb shell ps | grep it.sephiroth.android.library.uigestures.test | cut -c10-15`" &
else
  echo "Unknown test type"
  exit 1
fi