#!/bin/bash

# define the pipeline version to train
groupId=eu.redzoo.ml
artifactId=pipeline-estimate-houseprice
version=1.0.3

echo copying ingest jar to local dir
ingest_app_uri="https://github.com/grro/ml_deploy/blob/master/example-repo/lib-releases/eu/redzoo/ml/ingest-housedata/2.2.3/ingest-housedata-2.2.3-jar-with-dependencies.jar?raw=true"
curl -s -L $ingest_app_uri --output ingest2.jar

echo copying pipeline jar to local dir
pipeline_app_uri="https://github.com/grro/ml_deploy/blob/master/example-repo/lib-releases/${groupId//.//}/${artifactId//.//}/$version/${artifactId//.//}-$version-jar-with-dependencies.jar?raw=true"
curl -s -L $pipeline_app_uri --output pipeline.jar

echo copying source data to local dir
train_data="https://github.com/grro/ml_deploy/blob/master/src/test/resources/train.csv?raw=true"
curl -s -L $train_data --output train.csv

echo performing ingest jar consuming source data and producing houses2.json and prices2.json
java -jar ingest2.jar train.csv houses2.json prices2.json

echo performing pipeline jar to create and train a pipeline consuming houses2.json and prices2.json
enhanced_version=$version-$(date +%s)
trained=$artifactId-$enhanced_version.ser
java -jar pipeline.jar houses2.json prices2.json $trained

echo uploading trained pipeline
echo curl -X PUT --data-binary "@$trained" "https://github.com/grro/ml_deploy/blob/master/example-repo/model-releases/${groupId//.//}/${artifactId//.//}/$enhanced_version/$trained"

rm $trained
rm pipeline.jar
rm ingest2.jar
rm train.csv
rm prices2.json
rm houses2.json