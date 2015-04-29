#!/bin/sh
#author AWA

#check params
if [ "$#" -ne 4 ]; then
    echo "Illegal number of parameters"
    exit 0;
fi

#params
ARTEFACT_PATH=$1
ARTEFACT_FILE_NAME=$(basename "$ARTEFACT_PATH")
echo "====================== Google Drive Upload ===================\n"
echo $ARTEFACT_PATH
echo "=============================================================="

[ ! -z "$2" ] && GOOGLE_ACCOUNT_USERNAME=$2 || echo "!!!!!!!!!!!!!! Enter account !!!!!!!!!!!!!!"
[ ! -z "$3" ] && GOOGLE_ACCOUNT_PASSWORD=$3 || echo "!!!!!!!!!!!!!!  Enter pass !!!!!!!!!!!!!! "
[ ! -z "$4" ] && GOOGLE_ACCOUNT_FOLDER=$4 || echo " !!!!!!!!!!!!!! Enter folder !!!!!!!!!!!!!! "

#upload
GOOGLE_ACCOUNT_TYPE="GOOGLE" #gooApps = HOSTED , gmail=GOOGLE
MIME_TYPE=`file -b --mime-type "$ARTEFACT_PATH"`

curl -v --data-urlencode Email=$GOOGLE_ACCOUNT_USERNAME --data-urlencode Passwd=$GOOGLE_ACCOUNT_PASSWORD -d accountType=$GOOGLE_ACCOUNT_TYPE -d service=writely -d source=cURL "https://www.google.com/accounts/ClientLogin" > /tmp/login.txt
token=`cat /tmp/login.txt | grep Auth | cut -d \= -f 2`
uploadlink=`/usr/bin/curl -Sv -k --request POST -H "Content-Length: 0" -H "Authorization: GoogleLogin auth=${token}" -H "GData-Version: 3.0" -H "Content-Type: $MIME_TYPE" -H "Slug: $ARTEFACT_FILE_NAME" "https://docs.google.com/feeds/upload/create-session/default/private/full/folder:$GOOGLE_ACCOUNT_FOLDER/contents?convert=false" -D /dev/stdout | grep "Location:" | sed s/"Location: "//`
curl -Sv -k --request POST --data-binary "@$ARTEFACT_PATH" -H "Authorization: GoogleLogin auth=${token}" -H "GData-Version: 3.0" -H "Content-Type: $mime_type" -H "Slug: $ARTEFACT_PATH" "$uploadlink" > /tmp/goolog.upload.txt
