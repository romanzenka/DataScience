# Where is Spark installed
sparkHome <- "/Users/m044910/dev/spark-1.6.1"

# Where is this demo installed
demoHome <- "/Users/m044910/dev/DataScience/vignettes/sparkR"

# Get Spark Context
Sys.setenv(SPARK_HOME = sparkHome)
library(SparkR, lib.loc = c(file.path(Sys.getenv("SPARK_HOME"), "R", "lib")))

# Create a debugging spark context
sc <- sparkR.init(master = "local[*]", 
                  sparkEnvir = list(spark.driver.memory="2g", 
                                    spark.driver.extraJavaOptions="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"),
                  sparkExecutorEnv = list(spark.executor.heartbeatInterval="600"))

sqlContext <- sparkRSQL.init(sc)

####################################
# Trivial example to show data.frame parallelization

a <- data.frame(x=c(1, 2, 3, 4, 5, 6, 7, 8), y=c(8,7,6,5,4,3,2,1))

d <- createDataFrame(sqlContext, a)

myFunc <- function(r) { return(r$x+2) }

e <- SparkR:::lapply(d, myFunc)

####################################################
# Sage example
logFile <- file.path(demoHome, "prod.log")
if(!file.exists(logFile)) {
	download.file("http://bsu-sage.mayo.edu/prod.log", destfile=logFile)
}

# Read DataFrame from the given json file
d <- read.df(sqlContext, logFile, "json")

# Drop @ from column names
colnames(d)  <- substring(colnames(d), 2)

# Display what we got
printSchema(d)
head(d)

# Turn into a table
registerTempTable(d, "data")

# List all "fields" portions into a separate table
allFields <- sql(sqlContext, "SELECT fields.* from data")
registerTempTable(allFields, "fields")

# Make a histogram
counts <- sql(sqlContext, "SELECT app, count(*) as count from fields group by app order by count desc")
system.time(collect(counts))

