#!/bin/sh

# Building random Forest module
cd ../RandomForest
mvn compile assembly:single

# Copying 
cd ../../
cp RandomForest/randomforest-1.0-SNAPSHOT-jar-with-dependencies.jar exec/randomforest-feeder.jar

# Executing
cd exec/
java -jar randomforest-feeder.jar
