#!/usr/bin/env python

from pyspark import SparkConf, SparkContext

print "Starting Spark Connection"
conf = SparkConf().setMaster("local").setAppName("My App")
sc = SparkContext(conf = conf)
print "Hello Spark!"