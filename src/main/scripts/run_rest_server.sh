#!/bin/bash

groupId="eu.redzoo.ml"
artifactId="pipeline-estimate-houseprice"
version="1.0.3"
train_version="1568611516"

echo docker run -p 9090:8080 $groupId"/"$artifactId":"$version"-"$train_version
docker run -p 9090:8080 $groupId"/"$artifactId":"$version"-"$train_version
