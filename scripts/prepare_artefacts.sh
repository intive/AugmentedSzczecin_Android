#!/bin/sh
#author AWA

#prepare artefacts from andriod gradle build
function prepareAndroidArtefacts() {
    local  result=$4

    #params
    INPUTDIR=$1
    ARTEFACT_FILE_NAME=$2
    COMMIT_ID=$3
    RELEASE_DATE=`date '+%Y%m%d_%H%M'`
    OUTPUTDIR=$INPUTDIR/apk/tmp
    OBFUCATOR_MAP_DIR=$INPUTDIR/proguard/release
    OBFUSCATOR_MAP_NAME="mapping.txt"

    #clean outputdir
    rm -rf $OUTPUTDIR

    #create outpudir
    mkdir $OUTPUTDIR

    #copy APKs to outputdir with appropriate names
    for f in $INPUTDIR/apk/*.apk
    do
        filename=$(basename "$f")
        filename="${filename%.*}"
        FILE_NAME=${filename}-${RELEASE_DATE}-${COMMIT_ID}.apk
        cp $f $OUTPUTDIR/$FILE_NAME
    done

    #copy obfuscation map
    cp $OBFUCATOR_MAP_DIR/$OBFUSCATOR_MAP_NAME $OUTPUTDIR/$OBFUSCATOR_MAP_NAME

    #pack artefacts
    ZIP_OUTPUT_PATH=$OUTPUTDIR/${ARTEFACT_FILE_NAME}-${RELEASE_DATE}-${COMMIT_ID}.zip
    zip -rj $ZIP_OUTPUT_PATH $OUTPUTDIR

    echo $ZIP_OUTPUT_PATH
    eval $result="'$ZIP_OUTPUT_PATH'"
}
