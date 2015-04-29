#!/bin/sh
#TODO add error support
echo `travis encrypt GOOGLE_ACCOUNT_USERNAME=$1`
echo `travis encrypt GOOGLE_ACCOUNT_PASSWORD=$2`
echo `travis encrypt GOOGLE_ACCOUNT_FOLDER=$3`