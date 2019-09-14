#!/bin/bash

groupId=$1
artifactId=$2
version=$3
now="$(date +'%Y-%m-%dT%H.%M.%S')"

pipline_uri=$groupId"/"$artifactId"/"$version

echo $pipline_uri

ingest_jar="module-ingest/target/ingest-1.0-SNAPSHOT-jar-with-dependencies.jar"
source_data="module-ingest/src/test/resources/train.csv"
pipeline_jar="module-pipeline/target/pipeline-1.0-SNAPSHOT-jar-with-dependencies.jar"

java -jar $ingest_jar $source_data
java -jar $pipeline_jar

echo "groupId=$groupId&artifactId=$artifactId&version=$version" > "artifact.info"

tar cfv $groupId"_"$artifactId"_"$version"_"$now.tar artifact.info trainedstate.ser
