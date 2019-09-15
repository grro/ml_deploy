#!/bin/bash

# define pipeline to train
groupId="eu.redzoo.ml"
artifactId="pipeline-houseprice"
version="1.0.3"

echo "loading copmoents"
pipeline_app_uri="https://github.com/grro/ml_deploy/blob/master/example-repo/lib-releases/""${groupId//.//}""/""${artifactId//.//}""/$version/""${artifactId//.//}""-$version-jar-with-dependencies.jar?raw=true"
curl -L $pipeline_app_uri > pipeline.jar

ingest_app_uri="https://github.com/grro/ml_deploy/blob/master/example-repo/lib-releases/eu/redzoo/ml/ingest-housedata/2.2.3/ingest-housedata-2.2.3-jar-with-dependencies.jar?raw=true"
curl -L $ingest_app_uri > ingest.jar

train_data="https://github.com/grro/ml_deploy/blob/master/src/test/resources/train.csv?raw=true"
curl -L $train_data > train.csv

echo "perform ingest component"
java -jar ingest.jar train.csv

echo "create and train pipeline"
train_version=$(date +%s)
trained=$artifactId-$version-$train_version".ser"
java -jar pipeline.jar $trained

echo  "upload trained pipeline"
model_uri="https://github.com/grro/ml_deploy/blob/master/example-repo/model-releases/"${groupId//.//}/${artifactId//.//}/$version-$train_version/$trained
echo "uploading $model_uri"

rm $trained
rm pipeline.jar
rm ingest.jar
rm train.csv
rm labels.json
rm records.json