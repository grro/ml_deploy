#!/bin/bash

# define pipeline to train
groupId="eu.redzoo.ml"
artifactId="pipeline-houseprice"
version="1.0.3"

echo "loading components"
pipeline_app_uri="https://github.com/grro/ml_deploy/blob/master/example-repo/lib-releases/""${groupId//.//}""/""${artifactId//.//}""/$version/""${artifactId//.//}""-$version-jar-with-dependencies.jar?raw=true"
curl -L $pipeline_app_uri > pipeline.jar

ingest_app_uri="https://github.com/grro/ml_deploy/blob/master/example-repo/lib-releases/eu/redzoo/ml/ingest-housedata/2.2.3/ingest-housedata-2.2.3-jar-with-dependencies.jar?raw=true"
curl -L $ingest_app_uri > ingest.jar

train_data="https://github.com/grro/ml_deploy/blob/master/src/test/resources/train.csv?raw=true"
curl -L $train_data > train.csv

echo "perform ingest component to generate houses.json and prices.json files"
java -jar ingest.jar train.csv houses.json prices.json

echo "create and train pipeline with houses.json and prices.json"
train_version=$(date +%s)
trained=$artifactId-$version-$train_version".ser"
java -jar pipeline.jar houses.json prices.json $trained

echo  "upload trained pipeline $trained"
model_uri="https://github.com/grro/ml_deploy/blob/master/example-repo/model-releases/"${groupId//.//}/${artifactId//.//}/$version-$train_version/$trained
echo "uploading $model_uri"

#rm $trained
rm pipeline.jar
rm ingest.jar
rm train.csv
rm prices.json
rm houses.json