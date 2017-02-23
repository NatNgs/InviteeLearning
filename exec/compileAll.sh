#!/bin/sh

# Building folder tree
mkdir Data Data/LearnCuts Data/TestsCuts Data/UnknownCuts 2> /dev/null
mkdir Data/LearnCuts/0 Data/LearnCuts/1 Data/LearnCuts/2 Data/LearnCuts/3  2> /dev/null
mkdir Data/TestsCuts/0 Data/TestsCuts/1 Data/TestsCuts/2 Data/TestsCuts/3  2> /dev/null


# Building feeder module
cd ../randomforest
mvn clean package

# Copying jar
cp target/randomforest-feeder.jar ../exec/feeder.jar
