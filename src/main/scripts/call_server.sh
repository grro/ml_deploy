#!/bin/bash

curl -XPOST -H"content-type: application/json" -d@../../test/resources/houses.json http://localhost:8080/predictions
