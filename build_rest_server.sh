#!/bin/bash

# define pipeline to train
groupId="eu.redzoo.ml"
artifactId="pipeline-houseprice"
version="1.0-SNAPSHOT"


mkdir build
cd build



echo "copying $artifactId jar to local dir"
trained_uri="https://github.com/grro/ml_deploy/blob/master/example-repo/model-releases/"${groupId//.//}/${artifactId//.//}/$version-$train_version/$trained"
curl -s -L $trained_uri --output trained.ser


# build server
git clone https://github.com/grro/ml_deploy.git
cd ml_deploy/module-framework-rest

pom=$(<pom.xml)
additional_dependency="<dependency><groupId>"$groupId"</groupId><artifactId>"$artifactId"</artifactId><version>"$version"</version></dependency>"
new_pom=${pom/"<!-- PLACEHOLDER -->"/$additional_dependency}
echo $new_pom > pom.xml

mvn clean install

java -jar target/module-framework-rest-1.0-SNAPSHOT.jar --estimatorFilename=/mnt/c/workspace/ml_deploy/pipeline-houseprice-1.0.3-1568611516.ser


# download
# downloading   "uploadin trained pipeline


