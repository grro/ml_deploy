#!/bin/bash

# define pipeline version to use
groupId="eu.redzoo.ml"
artifactId="pipeline-houseprice"
version="1.0.3"

echo "loading pipeline component $groupId/$artifactId/$version"
pipeline_app_uri="https://github.com/grro/ml_deploy/blob/master/repo/lib-releases/""${groupId//.//}""/""${artifactId//.//}""/$version/""${artifactId//.//}""-$version-jar-with-dependencies.jar?raw=true"
curl -L $pipeline_app_uri > pipeline.jar

echo "loading ingest component"
ingest_app_uri="https://github.com/grro/ml_deploy/blob/master/repo/lib-releases/eu/redzoo/ml/ingest-housedata/2.2.3/ingest-housedata-2.2.3-jar-with-dependencies.jar?raw=true"
curl -L $ingest_app_uri > ingest.jar


source_data="module-ingest-housedata/src/test/resources/train.csv"

echo "perform ingest component"
java -jar ingest.jar $source_data

echo "create and train pipeline"
java -jar pipeline.jar

echo  "package and upload trained pipeline"
echo "groupId=$groupId&artifactId=$artifactId&version=$version" > "artifact.info"
tar cfv $groupId"_"$artifactId"_"$version"_"$now.tar artifact.info trainedstate.ser
