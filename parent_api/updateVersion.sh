#!/bin/bash

properties=(
	"contentprocessingframework.version"
	"fr-common-db.version"
	"fr-common-utils.version"
	"frameworkcommonutils.version"
	"frsolrextension.version"
	"frsearchdb.version"
)

if [[ $# -ne 2 ]] ; then
    echo 'USAGE: ./updateVersion.sh NEW_VERSION dependecies_version'
    exit 1
fi

echo "Updating project version to $1 and dependecies to $2"

mvn -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn versions:set -DnewVersion=$1
for prop in "${properties[@]}" 
do : 
   mvn -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn versions:set-property -Dproperty=$prop -DnewVersion=$2
done