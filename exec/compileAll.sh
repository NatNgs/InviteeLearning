#!/bin/sh

# Building folder tree
mkdir Data Data/LearnCuts Data/TestsCuts Data/UnknownCuts
mkdir Data/LearnCuts/0 Data/LearnCuts/1 Data/LearnCuts/2 Data/LearnCuts/3
mkdir Data/TestsCuts/0 Data/TestsCuts/1 Data/TestsCuts/2 Data/TestsCuts/3


# Building feeder module
cd ../feeder
mvn clean compile assembly:single

# Copying jar
cp target/feeder-1.0-SNAPSHOT-jar-with-dependencies.jar ../exec/feeder.jar
