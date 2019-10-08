#!/bin/bash

curl -XPOST -H"content-type: application/json" -d@house.json http://localhost:9090/predictions
