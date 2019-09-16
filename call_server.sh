#!/bin/bash

curl -XPOST -H"content-type: application/json" -d@houses.json http://localhost:8080/predictions
