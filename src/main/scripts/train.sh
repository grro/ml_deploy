#!/bin/bash

# define pipeline to train
groupId="eu.redzoo.ml"
artifactId="pipeline-estimate-houseprice"
version="1.0.3"

echo "copying pipeline jar to local dir"
pipeline_app_uri="https://github.com/grro/ml_deploy/blob/master/example-repo/lib-releases/""${groupId//.//}""/""${artifactId//.//}""/$version/""${artifactId//.//}""-$version-jar-with-dependencies.jar?raw=true"
echo $pipeline_app_uri
curl -s -L $pipeline_app_uri --output pipeline.jar

echo "copying ingest jar to local dir"
ingest_app_uri="https://github.com/grro/ml_deploy/blob/master/example-repo/lib-releases/eu/redzoo/ml/ingest-housedata/2.2.3/ingest-housedata-2.2.3-jar-with-dependencies.jar?raw=true"
curl -s -L $ingest_app_uri --output ingest.jar

echo "copying source data to local dir"
train_data="https://github.com/grro/ml_deploy/blob/master/src/test/resources/train.csv?raw=true"
curl -s -L $train_data --output train.csv

echo "performing ingest jar consuming source data to generate houses.json and prices.json"
java -jar ingest.jar train.csv houses.json prices.json

echo "performing pipeline jar to create and train a pipeline consuming houses.json and prices.json"
train_version=$(date +%s)
trained=$artifactId-$version-$train_version".ser"
java -jar pipeline.jar houses.json prices.json $trained

echo  "uploading trained pipeline https://github.com/grro/ml_deploy/blob/master/example-repo/model-releases/"${groupId//.//}/${artifactId//.//}/$version-$train_version/$trained

rm $trained
rm pipeline.jar
rm ingest.jar
rm train.csv
rm prices.json
rm houses.json