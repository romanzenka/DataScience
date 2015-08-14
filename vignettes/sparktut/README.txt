This directory contains many example programs for learning Spark.  Everything from the basics to machine learning on top of spark is covered in simple easy to read examples.
The examples are provided in Java, Python, and Scala.  Users of Spark can use any of these languages to interact with the Spark cluster.  

Data Scientists, Bioinformaticians and the like will probably prefer using Python.  Rapid Prototypers will probably like Scala because it is less verbose than Java but has the same execution speed.  Engineers will probably want to use Java.  We don't judge here, they all work.


Executing the Python Examples:
Python examples should be executed from the command line.  To do this you need to setup your envoronment.  I downloaded the spark binary and then I put the following in my .bash_profile and then sourced .bash_profile:
# Add the PySpark classes to the Python path:
 export SPARK_HOME=~/tools/spark-1.3.1-bin-hadoop2.6/
 export PYTHONPATH=$SPARK_HOME/python/:$PYTHONPATH
 export PYTHONPATH=$SPARK_HOME/python/lib/py4j-0.8.2.1-src.zip:$PYTHONPATH
 export SPARK_TUT=/Users/m102417/workspace/sparktut/

Then I run the examples from the source directory by executing the file e.g. ./HelloSpark.py
$ python src/main/python/HelloSpark.py

Executing Scala/Java Examples.
These examples are all built with maven, standard maven development practices apply.  Just do 'mvn clean install' at the command line, or wire the maven goal into your IDE for execution.  This method allows you to use a debugger to go through the examples line by line.


Order of Examples:
1. HelloSpark - an example of how to connect to a local spark cluster (basically a spark daemon running on your local development box)
2. Lambda - an example of how Lambda's are created to pass logical operators to the spark infastructure
3. RDD - an introduction to RDDs (resilient distributed datasets)
