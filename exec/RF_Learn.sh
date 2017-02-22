#!/bin/sh

java -jar feeder.jar Data/donnees.csv Data/LearnCuts/0/ 0 Data/LearnCuts/1/ 1 Data/LearnCuts/2/ 2 Data/LearnCuts/3/ 3

java -jar feeder.jar Data/test.csv Data/TestsCuts/0/ 0 Data/TestsCuts/1/ 1 Data/TestsCuts/2/ 2 Data/TestsCuts/3/ 3

python RF_Learn.py
