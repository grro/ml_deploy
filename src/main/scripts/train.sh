#!/bin/bash

# define the pipeline version to train
groupId=eu.redzoo.ml
artifactId=pipeline-estimate-houseprice
version=1.0.3

echo copying ingest jar to local dir
ingest_app_uri="https://github.com/grro/ml_deploy/raw/master/example-repo/lib-releases/eu/redzoo/ml/ingest-housedata/2.2.3/ingest-housedata-2.2.3-jar-with-dependencies.jar"
curl -s -L $ingest_app_uri --output ingest.jar

echo copying pipeline jar to local dir
pipeline_app_uri="https://github.com/grro/ml_deploy/raw/master/example-repo/lib-releases/${groupId//.//}/${artifactId//.//}/$version/${artifactId//.//}-$version-jar-with-dependencies.jar"
curl -s -L $pipeline_app_uri --output pipeline.jar

echo performing ingest jar to produce houses.json and prices.json. Internally http://jse.amstat.org/v19n3/decock/AmesHousing.xls will be fetched
java -jar ingest.jar train.csv houses.json prices.json

echo performing pipeline jar to create and train a pipeline consuming houses.json and prices.json
enhanced_version=$version-$(date +%s)
pipeline_instance=$artifactId-$enhanced_version.ser
java -jar pipeline.jar houses.json prices.json $pipeline_instance

echo uploading trained pipeline
echo curl -X PUT --data-binary "@$pipeline_instance" "https://github.com/grro/ml_deploy/blob/master/example-repo/model-releases/${groupId//.//}/${artifactId//.//}/$enhanced_version/$trained"

rm $pipeline_instance
rm pipeline.jar
rm ingest.jar
rm prices.json
rm houses.json