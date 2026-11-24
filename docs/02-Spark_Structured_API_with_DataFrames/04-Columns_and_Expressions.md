# Columns and Expressions
# `DataFrame` Processing
```scala mdoc:invisible
import org.apache.spark.sql.SparkSession

import common._
```
```scala mdoc
val spark = SparkSession.builder()
    .appName("Columns and Expressions")
    .getOrCreateLocal()
```

```scala mdoc
val carsDF = spark.read
  .option("inferSchema", "true")
  .json("path/to/cars.json")

carsDF.five()
```

## Manipulating `Column`s
### Creating `Column` objects with `df.col()`
Define a column directly from a `DataFrame`
```scala mdoc
val firstColumn = carsDF.col("name")
```

### `select` with a `Column` object

```scala mdoc
carsDF
  .select(firstColumn)
  .five()
```

### Various _column_ methods
```scala mdoc
import org.apache.spark.sql.functions.{col, column, expr}
import spark.implicits._
carsDF
  .select(
    carsDF.col("Name"),
    col("Acceleration"),
    column("Weight_in_lbs"),
    'Year, // Scala symbol, auto-converted to column
    $"Horsepower", // fancier interpolated string, returns a Column object
    expr("Origin") // EXPRESSION
  )
  .five()
```

### Select with plain (`String`) column names
```scala mdoc
carsDF
  .select("Name", "Year")
  .five()
```

### Expressions with `selectExpr`
```scala mdoc
val weightInLbsExpression = carsDF.col("Weight_in_lbs")
val weightInKgExpression = (weightInLbsExpression / 2.2).name("Weight_in_kgs")

val carsWithWeightsDF = carsDF.select(
  col("Name"),
  col("Weight_in_lbs"),
  weightInKgExpression,
  $"Weight_in_lbs" / 2.2 as "Weight_in_kgs_2",
  "Weight_in_kgs_3" := $"Weight_in_lbs" / 2.2,
  "Weight_in_kgs_4" := expr("Weight_in_lbs / 2.2"),
)

carsWithWeightsDF.five()
```

> Note here that `:=` is extension syntax for `String`; it is an _operator_ alias for (delegates to) `Column.name/as`.

Rather than defining a `Column` object and selecting it, we can use `selectExpr` to do the above in _one_ step:
```scala mdoc
val carsWithSelectExprWeightDF =
    carsDF.selectExpr("Name", "Weight_in_lbs", "Weight_in_lbs / 2.2")

carsWithSelectExprWeightDF.five()
```

### Adding a column
```scala mdoc
val carsWithKgDF =
  carsDF.withColumn("Weight_in_kgs", $"Weight_in_lbs" / 2.2)

carsWithKgDF.two()
```

### Renaming a column
```scala mdoc
val carsWithColumnRenamedDF =
  carsDF.withColumnRenamed("Weight_in_lbs", "Weight in pounds")

carsWithColumnRenamedDF.two()
```

Be careful with column names with spaces and hyphens e.g. when using `selectExpr`:
```scala mdoc
carsWithColumnRenamedDF
  .selectExpr("`Weight in pounds`")
  .two()
```

### Removing a column
```scala mdoc
carsWithColumnRenamedDF
  .drop("Cylinders", "Displacement")
  .two()
```

## Manipulating Row data

### Filtering

`filter` and `where` are aliases:
```scala mdoc
val europeanCarsDF = carsDF.filter($"Origin" =!= "USA")
europeanCarsDF.two()

val europeanCarsDF2 = carsDF.where($"Origin" =!= "USA")
europeanCarsDF2.two()
```

Filtering with expression strings
```scala mdoc
carsDF
  .filter($"Origin" === "USA").filter($"Horsepower" > 150)
  .two()

carsDF
  .filter($"Origin" === "USA" and $"Horsepower" > 150)
  .two()

carsDF
  .filter("Origin = 'USA' and Horsepower > 150")
  .two()
```

### Union i.e. adding more rows
```scala mdoc
val carsCount = carsDF.count()

val moreCarsDF = spark.read
  .option("inferSchema", "true")
  .json("path/to/more_cars.json")

val moreCarsCount = moreCarsDF.count()

val allCarsDF = carsDF.union(moreCarsDF)
val allCarsCount = allCarsDF.count()

println(s"$carsCount cars and $moreCarsCount cars =>> $allCarsCount total")
```

### Distinct values
```scala mdoc
carsDF
  .select("Distinct Origins" := $"Origin")
  .distinct()
  .show()
```
