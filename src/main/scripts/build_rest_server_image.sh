#!/bin/bash
# export DOCKER_HOST=localhost:2375

groupId=eu.redzoo.ml
artifactId=pipeline-estimate-houseprice
version=1.0.3
train_version=1568611516

mkdir build
cd build

echo copying framework-rest source to local dir
git clone --quiet https://github.com/grro/ml_deploy.git
cd ml_deploy/module-pipeline-rest

echo download trained pipeline to pipeline-rest/src/main/resources dir
pipeline_instance=$artifactId-$version-$train_version".ser"
pipeline_instance_uri="https://github.com/grro/ml_deploy/blob/master/example-repo/model-releases/"${groupId//.//}/${artifactId//.//}/$version-$train_version/$pipeline_instance"?raw=true"
mkdir src/main/resources
curl -s -L $pipeline_instance_uri --output src/main/resources/$pipeline_instance
echo "filename: $pipeline_instance" > src/main/resources/application.yml

echo adding the pipeline artefact id to framework-rest pom.xml file
pom=$(<pom.xml)
additional_dependency="<dependency><groupId>"$groupId"</groupId><artifactId>"$artifactId"</artifactId><version>"$version"</version></dependency>"
new_pom=${pom/"<!-- PLACEHOLDER -->"/$additional_dependency}
echo $new_pom > pom.xml

echo build rest server jar including the specific pipeline artifacts
mvn -q clean install package

echo copying the newly created jar file into the root of the build dir
server_jar=server-$artifactId"-"$version"-"$train_version.jar
cp target/pipeline-rest-1.0.3.jar ../../$server_jar
cd ../../..

echo build docker image
docker build --build-arg arg_server_jar=$server_jar -t $groupId"/"$artifactId":"$version"-"$train_version .

rm -rf build