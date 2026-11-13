# DataFrames
```scala mdoc:invisible
import org.apache.spark.sql.types.{DoubleType, LongType, StringType, StructField, StructType}
```

Distributed collections of `Rows` conforming to a schema

`Schema` = list describing the column names and types
 - types known to Spark, not at compile-time
   ```scala mdoc
    val carsSchema = StructType(Array(
      StructField("Name", StringType),
      StructField("Horsepower", LongType),
      StructField("Acceleration", DoubleType),
   ))
   ```
 - arbitrary number of columns
 - all rows have the same structure

## Need to be distributed
 - data too big for a single computer
 - too long to process the entire data on a single CPU

## Partitioning
 - splits the data into files, distributed between nodes in the cluster
 - impacts the processing parallelism

## Immutable
 - can't be changed once created
 - created other DFs via **transformations**

## Transformations
 - **narrow** = one input partition contributes to at most one output partition (e.g. map)
 - **wide** = input partitions (one or more) create many output partitions (e.g. sort)

## Shuffle = data exchange between cluster nodes
 - occurs in wide transformations
 - massive perf topic

## Lazy evaluation
 - Spark waits until the last moment to execute the DF transformations

## Planning
 - Spark compiles the DF transformations into the graph before running any code
 - **logical plan** = DF dependency graph + narrow/wide transformations sequence
 - **physical plan** = optimised sequence of steps for nodes in the cluster
 - optimisations

## Transformations vs Actions
 - transformations describe how the DFs are obtained
 - actions actually start executing Spark code
