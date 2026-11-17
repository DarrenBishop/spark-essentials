package part2.dataframes

import org.apache.spark.sql.SparkSession

import common._

object ColumnsAndExpressions {

  debug()

  lazy val spark = SparkSession.builder()
    .appName("Columns and Expressions")
    .getOrCreateLocal()

  def main(args: Array[String]): Unit = {
    val carsDF = spark.read
      .option("inferSchema", "true")
      .json("src/main/resources/data/cars.json")

    carsDF.five()

    // Columns
    val firstColumn = carsDF.col("name")

    // Selecting (Projecting)
    val carNamesDF = carsDF.select(firstColumn)

    carNamesDF.five()

    // Various select methods
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

    // Select with plain column names (strings)
    carsDF
      .select("Name", "Year")
      .five()

    // Expressions
    val simplestExpression = carsDF.col("Weight_in_lbs")
    val weightInLbsExpression = carsDF.col("Weight_in_lbs")
    val weightInKgExpression = (weightInLbsExpression / 2.2).name("Weight_in_kgs")
    //val weightInKgExpression = weightInLbsExpression / 2.2 as "Weight_in_kgs"
    //val weightInKgExpression = weightInLbsExpression / 2.2 := "Weight_in_kgs"
    //val weightInKgExpression = "Weight_in_kgs" := weightInLbsExpression / 2.2

    val carsWithWeightsDF = carsDF.select(
      col("Name"),
      col("Weight_in_lbs"),
      weightInKgExpression,
      $"Weight_in_lbs" / 2.2 as "Weight_in_kgs_2",
      "Weight_in_kgs_3" := $"Weight_in_lbs" / 2.2,
      "Weight_in_kgs_4" := expr("Weight_in_lbs / 2.2"),
    )

    carsWithWeightsDF.five()

    // selectExpr
    val carsWithSelectExprWeightDF = carsDF.selectExpr(
      "Name",
      "Weight_in_lbs",
      "Weight_in_lbs / 2.2"
    )

    carsWithSelectExprWeightDF.five()

    // DF processing

    //  Adding a column
    val carsWithKgDF = carsDF.withColumn("Weight_in_kgs", $"Weight_in_lbs" / 2.2)
    carsWithKgDF.two()

    //  Renaming a column
    val carsWithColumnRenamedDF = carsDF.withColumnRenamed("Weight_in_lbs", "Weight in pounds")
    carsWithColumnRenamedDF.two()

    // Be careful with column names with spaces and hyphens e.g. when using `selectExpr`
    carsWithColumnRenamedDF.selectExpr("`Weight in pounds`").two()

    // Remove a column
    carsWithColumnRenamedDF.drop("Cylinders", "Displacement").two()

    // Filtering
    val europeanCarsDF = carsDF.filter($"Origin" =!= "USA")
    europeanCarsDF.two()
    val europeanCarsDF2 = carsDF.where($"Origin" =!= "USA")
    europeanCarsDF2.two()

    // Filtering with expression strings
    val americanCarsDF = carsDF.filter($"Origin" === "USA").filter($"Horsepower" > 150)
    americanCarsDF.two()
    val americanCarsDF2 = carsDF.filter($"Origin" === "USA" and $"Horsepower" > 150)
    americanCarsDF2.two()
    val americanCarsDF3 = carsDF.filter("Origin = 'USA' and Horsepower > 150")
    americanCarsDF3.two()

    // Union = adding more rows
    val moreCarsDF = spark.read
      .option("inferSchema", "true")
      .json("src/main/resources/data/more_cars.json")

    val allCarsDF = carsDF.union(moreCarsDF)
    val carsCount = carsDF.count()
    val moreCarsCount = moreCarsDF.count()
    val allCarsCount = allCarsDF.count()
    println(s"$carsCount cars and $moreCarsCount cars =>> $allCarsCount total")

    // Distinct values
    val allCountriesDF = carsDF.select("Distinct Origins" := $"Origin").distinct()
    allCountriesDF.show()
  }
}
