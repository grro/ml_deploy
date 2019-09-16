#!/bin/bash

# define pipeline to train
groupId="eu.redzoo.ml"
artifactId="pipeline-houseprice"
version="1.0-SNAPSHOT"
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
curl -s -L $trained_uri --output src/main/resources/trained.ser

mvn clean install

java -jar target/module-framework-rest-1.0-SNAPSHOT.jar --estimatorFilename=trained.ser


# download
# downloading   "uploadin trained pipeline


