export ANDROID_SDKS=android-19,sysimg-19
export ANDROID_TARGET=android-19
export ANDROID_ABI=armeabi-v7a
export GIT_BRANCH=master

#---------------------------------------------------------------------
#install android tools
export REAL_SCRIPTDIR=$( cd -P -- "$(dirname -- "$(command -v -- "$0")")" && pwd -P )
export ANDROID_HOME=$PWD/android-sdk-linux
echo "ANDROID HOME:${ANDROID_HOME}"
export PATH=${PATH}:${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools
$REAL_SCRIPTDIR/install_android_tools.sh
#---------------------------------------------------------------------

#---------------------------------------------------------------------
#TURNED OFF, INPROGRESS
#build gradle
#run_emulator.sh
#chmod +x ./scripts/run_emulator.sh
#./scripts/run_emulator.sh
#wait for emulator
#chmod +x ./scripts/wait_for_emulator.sh
#./scripts/wait_for_emulator.sh
#---------------------------------------------------------------------

#---------------------------------------------------------------------
#TURNED OFF, INPROGRESS
#git pull
#---------------------------------------------------------------------


#---------------------------------------------------------------------
#TURNED OFF, INPROGRESS
#build gradle
#gradlew
#chmod +x gradlew
#TERM=dumb ./gradlew assemble
#---------------------------------------------------------------------

#---------------------------------------------------------------------
#TURNED OFF, INPROGRESS
#prepare artefact
#ARTEFACT_FILE_NAME="test_test" #prefiks app name for artefact file
#BUILD_DIR="$PWD/app/build" #dir where to find artefacts: apk, mapping
#COMMIT_HASH=`git log --pretty=format:'%h' -n 1` #commit hash id
#chmod +x ./scripts/prepare_artefacts.sh
#. ./scripts/prepare_artefacts.sh
#prepareAndroidArtefacts $BUILD_DIR $ARTEFACT_FILE_NAME $COMMIT_HASH OUT_ARTEFACT_PATH
#---------------------------------------------------------------------

#---------------------------------------------------------------------
#upload
#export GOOGLE_ACCOUNT_USERNAME="a"
#export GOOGLE_ACCOUNT_PASSWORD="b"
#export GOOGLE_ACCOUNT_FOLDER="c"
#chmod +x ./scripts/google_disk_upload.sh
#./scripts/google_disk_upload.sh "$OUT_ARTEFACT_PATH" "$GOOGLE_ACCOUNT_USERNAME" "$GOOGLE_ACCOUNT_PASSWORD" "$GOOGLE_ACCOUNT_FOLDER"
#---------------------------------------------------------------------