#!/usr/bin/env python
import os
from pyspark import SparkConf, SparkContext

print "Connecting to Spark Cluster..."
conf = SparkConf().setMaster("local").setAppName("My App")
sc = SparkContext(conf = conf)
home = os.path.expandvars('$SPARK_TUT')

# An RDD in Spark is simply an immutable distributed collection of objects.
# Each RDD is split into multiple partitions, which may be computed on different nodes of the cluster.
# RDDs can contain any type of Python, Java, or Scala objects, including user-defined classes.
print "Loading Text File into an RDD of strings using sc.textFile():"
lines = sc.textFile(home + "/src/test/resources/example.vcf")

print "Performing an action on the new RDD, actions compute a result based on an RDD, and either return it to the driver program or save it to an external storage system (e.g., HDFS)."
first = lines.first()
print first
assert first.strip() == "##fileformat=VCFv4.0"

print "finished!"