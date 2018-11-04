#!/bin/sh

sbt assembly

if [ "x$TRAVIS_COMMIT" != "x" ];then
VERSION=$TRAVIS_TAG
else
VERSION=unknown
fi

DEPLOY=target/deploy/JSONLinesViewer-$VERSION
mkdir -p $DEPLOY
cp target/scala-2.12/JSONLinesViewer-assembly-0.1.jar $DEPLOY/jsonlinesviewer.jar
cp LICENSE $DEPLOY/
cp README.md $DEPLOY/
cd `dirname $DEPLOY`
zip -r JSONLinesViewer.zip JSONLinesViewer-$VERSION
