#!/bin/sh

java -jar feeder.jar -u Data/unknown.csv Data/UnknownCuts/

python RF_Predict.py
