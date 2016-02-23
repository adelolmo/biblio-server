#!/bin/sh

set -e

DEVELOPMENT_VERSION=$1
MVN=$(which mvn)

if [ -z $MVN ]; then
    echo "Error! Missing Apache Maven!"
    echo "Please visit https://maven.apache.org/download.cgi for installation help."
    echo
    exit 1
fi

usage(){
	echo "Usage: $0 [next_development_version]"
}

run(){
    echo "run! $1 $2"
	$MVN versions:set -DnewVersion="$1" > /dev/null 2>&1
	git commit -a -m "prepare to release v.$1"
	git push
	$MVN clean install -Prelease > /dev/null 2>&1
	$MVN versions:set -DnewVersion="$2" > /dev/null 2>&1
	git commit -a -m "prepare to develop v.$2"
	git push
	find -name "*.versionsBackup"| xargs -I file rm file
}

release_version=$(grep -oP '(?<=<version>).*(?=</version>)' pom.xml|sed -n "1p"|grep -oE "[0-9\.]+")

#if [ -z "$DEVELOPMENT_VERSION" ]; then
    current_mayor_version=$( echo $release_version | grep -Eo "(^[0-9]*)" )
    current_minor_version=$( echo $release_version | grep -Eo "([0-9]*$)")
    next_minor_version=$(( current_minor_version + 1 ))
	run $release_version "$current_mayor_version.$next_minor_version-SNAPSHOT"
#else
#    run $DEVELOPMENT_VERSION
#fi
