#!/bin/bash

# define pipeline version to use
groupId="eu.redzoo.ml"
artifactId="pipeline-houseprice"
version="1.0.3"

echo "loading pipeline component $groupId/$artifactId/$version"
pipeline_app_uri="https://github.com/grro/ml_deploy/blob/master/repo/lib-releases/""${groupId//.//}""/""${artifactId//.//}""/$version/""${artifactId//.//}""-$version-jar-with-dependencies.jar?raw=true"
echo $pipeline_app_uri
curl -L $pipeline_app_uri > pipeline.jar

echo "loading ingest component"
ingest_app_uri="https://github.com/grro/ml_deploy/blob/master/repo/lib-releases/eu/redzoo/ml/ingest-housedata/2.2.3/ingest-housedata-2.2.3-jar-with-dependencies.jar?raw=true"
curl -L $ingest_app_uri > ingest.jar

echo "loading house data"
train_data="https://github.com/grro/ml_deploy/blob/master/src/test/resources/train.csv?raw=true"
curl -L $train_data > train.csv

echo "perform ingest component"
java -jar ingest.jar train.csv

echo "create and train pipeline"
train_version=$(date +%s)
trained=$artifactId-$version-$train_version".ser"
java -jar pipeline.jar $trained

echo  "upload trained pipeline"
model_uri="https://github.com/grro/ml_deploy/blob/master/repo/model-releases/"${groupId//.//}/${artifactId//.//}/$version$train_version/$trained
echo "uploading $model_uri"

#rm pipeline.jar
#rm ingest.jar
#rm artifact.info
#rm train.csv
