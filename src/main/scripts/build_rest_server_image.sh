#!/bin/bash

# export DOCKER_HOST=localhost:2375

# define pipeline to train
groupId="eu.redzoo.ml"
artifactId="pipeline-estimate-houseprice"
version="1.0.3"
train_version="1568611516"

mkdir build
cd build


# build server
echo "copying framework-rest source to local dir"
git clone --quiet https://github.com/grro/ml_deploy.git
cd ml_deploy/module-pipeline-rest

echo "download trained $artifactId pipeline to pipeline-rest/src/main/resources dir"
trained=$artifactId-$version-$train_version".ser"
trained_uri="https://github.com/grro/ml_deploy/blob/master/example-repo/model-releases/"${groupId//.//}/${artifactId//.//}/$version-$train_version/$trained"?raw=true"
curl -s -L $trained_uri --output $trained
cd src/main
mkdir resources
cd ../..
mv $trained src/main/resources

echo "adding $artifactId to framework-rest pom.xml file "
pom=$(<pom.xml)
additional_dependency="<dependency><groupId>"$groupId"</groupId><artifactId>"$artifactId"</artifactId><version>"$version"</version></dependency>"
new_pom=${pom/"<!-- PLACEHOLDER -->"/$additional_dependency}
echo $new_pom > pom.xml


echo "build rest server jar including the specific pipeline artifacts"
mvn -q clean install

cp target/pipeline-rest-1.0.3.jar ../../estimation-server.jar
cd ../../..

echo "build docker image $groupId"/"$artifactId":"$version"-"$train_version"
docker build --build-arg pipeline_filename=$trained -t $groupId"/"$artifactId":"$version"-"$train_version .

#rm -rf build

