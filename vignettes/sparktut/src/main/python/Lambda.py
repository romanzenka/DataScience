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

#Getting the header lines from the file using a lambda!
headerLines = lines.filter(lambda line: "##" in line)

for line in headerLines.collect():
    print line


print "finished!"