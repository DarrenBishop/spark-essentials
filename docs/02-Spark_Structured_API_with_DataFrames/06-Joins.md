# Joins
```scala mdoc:invisible
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

import common._

val spark = SparkSession.builder()
    .appName("Aggregations and Grouping")
    .getOrCreateLocal()

import spark.implicits._
```

## Combine data from multiple `DataFrame`s

 - one (or more) columns from table-1 (left) is compared with one (or more) columns from table-2 (right)
 - if the condition passes, rows re combined
 - non-matching rows are discarded

> [!WARNING]
> Joins are *wide-transformations* (read: **expensive!**)

:::warning
Joins are *wide-transformations* (read: **expensive!**)
::: 