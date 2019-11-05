#!/bin/bash

# define the pipeline version to train
groupId=eu.redzoo.ml
artifactId=pipeline-estimate-houseprice
version=1.0.3

echo copying ingestion jar to local dir
ingest_app_uri="https://github.com/grro/ml_deploy/raw/master/example-repo/lib-releases/eu/redzoo/ml/ingestion-housedata/2.2.3/ingestion-housedata-2.2.3-jar-with-dependencies.jar"
curl -s -L $ingest_app_uri --output ingestion.jar

echo copying pipeline jar to local dir
pipeline_app_uri="https://github.com/grro/ml_deploy/raw/master/example-repo/lib-releases/${groupId//.//}/${artifactId//.//}/$version/${artifactId//.//}-$version-jar-with-dependencies.jar"
curl -s -L $pipeline_app_uri --output pipeline.jar

echo performing ingestion jar to produce houses.json and prices.json. Internally http://jse.amstat.org/v19n3/decock/AmesHousing.xls will be fetched
java -jar ingestion.jar train.csv houses.json prices.json

echo performing pipeline jar to create and train a pipeline consuming houses.json and prices.json
version_with_timestamp=$version-$(date +%s)
pipeline_instance=$artifactId-$version_with_timestamp.ser
java -jar pipeline.jar houses.json prices.json $pipeline_instance

echo uploading trained pipeline
echo curl -X PUT --data-binary "@$pipeline_instance" "https://github.com/grro/ml_deploy/blob/master/example-repo/model-releases/${groupId//.//}/${artifactId//.//}/$version_with_timestamp/$trained"

rm $pipeline_instance
rm pipeline.jar
rm ingestion.jar
rm prices.json
rm houses.json