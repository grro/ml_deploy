# Deploying machine learning models example
 
 This repo includes non-productive example code for illustration purposes.
 
 
 | artifact     | description |
 | ------------ | ----------- |
 | [module-pipeline](/module-pipeline) | The module containing the common pipeline artefacts |
 | [module-pipeline-rest](/module-pipeline-rest) | The module providing a rest server to serve pipelines  |
 | [module-ingest-housedata](/module-ingest-housedata) | The module containing the ingest to generate the raw dataset used in the examples. Please consider that the required train.csv file (Ames Housing dataset) is not part of this project. The Ames Housing dataset may be downloaded from here: https://www.kaggle.com/c/house-prices-advanced-regression-techniques/data |
 | [module-pipeline-estimate-houseprice](/module-pipeline-estimate-houseprice) | The module containing the house pipeline model and transformers |
 | [bash scripts](/src/main/scripts) | The example bash scripts to train, build and run the house estimation pipleline | 
