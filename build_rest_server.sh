#!/bin/bash

# define pipeline to train
groupId="eu.redzoo.ml"
artifactId="pipeline-houseprice"
version="1.0.3"


mkdir build
cd build


# build server
git clone https://github.com/grro/ml_deploy.git
cd ml_deploy/module-framework-rest

pom=$(<pom.xml)
additional_dependency="<dependency><groupId>"$groupId"</groupId><artifactId>"$artifactId"</artifactId><version>"$version"</version></dependency>"
new_pom=${pom/"<!-- PLACEHOLDER -->"/$additional_dependency}
echo $new_pom > pom.xml

mvn clean install




# download
# downloading   "uploadin trained pipeline https://github.com/grro/ml_deploy/blob/master/example-repo/model-releases/"${groupId//.//}/${artifactId//.//}/$version-$train_version/$trained


