#!/bin/sh

# Building feeder module
cd ../feeder
mvn clean compile assembly:single

# Copying jar
cp target/feeder-1.0-SNAPSHOT-jar-with-dependencies.jar ../exec/feeder.jar
