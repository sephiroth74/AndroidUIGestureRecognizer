#!/usr/bin/env bash

set -e
set -x

echo "TEST_TYPE=$TEST_TYPE"
echo "EMULATOR_TAG=$EMULATOR_TAG"
echo "EMULATOR_API=$EMULATOR_API"
echo "ABI=$ABI"

if [[ "$TEST_TYPE" == "unit" ]]; then
  echo "This is unit test run, skipping emulator set up."
elif [[ "$TEST_TYPE" == "instrumentation" ]]; then
  echo "Waiting for emulator setup..."
  android-wait-for-emulator
  adb shell settings put global window_animation_scale 0 &
  adb shell settings put global transition_animation_scale 0 &
  adb shell settings put global animator_duration_scale 0 &  
  adb devices
  adb shell input keyevent 82 &
  # Avoid having it lock itself again.
  adb shell svc power stayon true
  adb logcat "UIGestureRecognizer:*" "*:S" &
else
  echo "Unknown test type"
  exit 1
fi