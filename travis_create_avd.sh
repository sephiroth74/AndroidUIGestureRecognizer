#!/usr/bin/env bash

set -e
set -x

echo "TEST_TYPE=$TEST_TYPE"
echo "EMULATOR_TAG=$EMULATOR_TAG"
echo "EMULATOR_API=$EMULATOR_API"
echo "ABI=$ABI"

echo "Running android update sdk"
echo y | android update sdk --no-ui --all --filter "android-$EMULATOR_API"

android-update-sdk --components="sys-img-$ABI-$EMULATOR_TAG-$EMULATOR_API" --accept-licenses='android-sdk-license-[0-9a-f]{8}'

echo "Running sdkmanager"

if [[ "$EMULATOR_TAG" = "google_apis" ]]; then
    EMULATOR_SYS="system-images;android-$EMULATOR_API;$EMULATOR_TAG;$ABI"
else
    EMULATOR_SYS="system-images;$EMULATOR_TAG-$EMULATOR_API;default;$ABI"
fi

echo "Emulator system image: $EMULATOR_SYS"

${ANDROID_HOME}/tools/bin/sdkmanager "$EMULATOR_SYS"

echo "Running android list target"
android list target
# echo no | android create avd --force -n test -k $EMULATOR_TAG-$EMULATOR_API --abi $ABI --skin QVGA

echo "Creating the emulator"
echo no | ${ANDROID_HOME}/tools/bin/avdmanager create avd --force -n test -k "$EMULATOR_SYS" -d pixel

echo "Starting the emulator"
${ANDROID_HOME}/emulator/emulator -avd test -no-audio -netfast -no-window &

exit 0
