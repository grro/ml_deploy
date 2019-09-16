#!/bin/bash

# define pipeline to train
groupId="eu.redzoo.ml"
artifactId="pipeline-houseprice"
version="1.0.3"
train_version="1568611516"

mkdir build
cd build


# build server
git clone https://github.com/grro/ml_deploy.git
cd ml_deploy/module-framework-rest

pom=$(<pom.xml)
additional_dependency="<dependency><groupId>"$groupId"</groupId><artifactId>"$artifactId"</artifactId><version>"$version"</version></dependency>"
new_pom=${pom/"<!-- PLACEHOLDER -->"/$additional_dependency}
echo $new_pom > pom.xml

echo "copying $artifactId jar to local dir"
trained=$artifactId-$version-$train_version".ser"
trained_uri="https://github.com/grro/ml_deploy/blob/master/example-repo/model-releases/"${groupId//.//}/${artifactId//.//}/$version-$train_version/$trained"?raw=true"
echo $trained_uri
curl -s -L $trained_uri --output trained.ser
cd src/main
mkdir resources
cd ../..
mv trained.ser src/main/resources

echo "build rest server jar including the specific pipeline artifacts"
mvn clean install

cp target/module-framework-rest-1.0.3.jar ../../rest_server.jar
cd ../../..

echo "build docker image $groupId"/"$artifactId":"$version"-"$train_version"
docker build -t $groupId"/"$artifactId":"$version"-"$train_version .

rm -rf build

