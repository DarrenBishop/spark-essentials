package part2.dataframes

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

/**
 * Exercise:
 *
 * 1) Create a manual DF describing smart-phones
 *   - make
 *   - model
 *   - screen dimension
 *   - camera mega-pixels
 *
 * 2) Read another file from the `data/` folder, e.g. movies.json
 *   - print its schema
 *   - count the number of rows, call `count()`
 */
object DataFramesBasicsExercises {
  lazy val spark = SparkSession.builder()
    .appName("DataFramesBasics - Exercises")
    .config("spark.master", "local")
    .getOrCreate()

  import spark.implicits._

  val smartPhonesSchema = StructType(Array(
    StructField("make", StringType),
    StructField("model", StringType),
    StructField("screen dimension",  StructType(Array(
      StructField("height", IntegerType),
      StructField("width", IntegerType),
    ))),
    StructField("mega pixels", IntegerType),
  ))

  val smartPhones = Seq(
    ("Samsung", "Galaxy S12", (100, 200), 12),
    ("Apple", "iPhone 18Pro Max", (200, 400), 25),
    ("Google Pixel", "One", (150, 300), 20),
  )

  lazy val smartPhonesDF = smartPhones.toDF("Make", "Model", "Screen Dimension", "Mega Pixels")

  def main(args: Array[String]): Unit = {
    smartPhonesDF.show()
    smartPhonesDF.printSchema()
    smartPhonesDF.take(5)

    val moviesDF = spark.read
      .format("json")
      .option("inferSchema", "true")
      .load("src/main/resources/data/movies.json")

    moviesDF.printSchema()
    val movieCount = moviesDF.count()
    println(s"There are $movieCount movies")
  }
}
