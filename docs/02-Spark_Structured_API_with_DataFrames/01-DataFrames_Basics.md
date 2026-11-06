# Recap

## Creating a `SparkSession`

```scala mdoc:invisible
import org.apache.spark.sql.SparkSession
import common.LocalSession._
```
```scala mdoc
val spark = SparkSession.builder()
    .appName("My Application")
    .config("spark.master", "local")
    .getOrCreate()
```
 - allows creating, reading and writing DataFrames

## Reading a `DataFrame` from a file

```scala mdoc
val firstDF = spark.read
  .format("json")
  .option("inferSchema", "true") // ...discouraged in production
  .load("path/to/data.json")
```

```scala mdoc
firstDF.show()
firstDF.printSchema()
firstDF.take(5)
```

## Rows = unstructured data

## Schema = description of fields a.k.a. <u>columns</u> and their type

## Spark types e.g. `LongType`, `StringType`, etc