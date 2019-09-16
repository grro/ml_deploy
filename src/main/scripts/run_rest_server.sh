#!/bin/bash

groupId="eu.redzoo.ml"
artifactId="pipeline-houseprice"
version="1.0-SNAPSHOT"
train_version="1568611516"

docker run -p 8080:8080 $groupId"/"$artifactId":"$version"-"$train_version
