package part2.dataframes

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.types.{DoubleType, LongType, StringType, StructField, StructType}
import common.LocalSession._

object DataSources {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Data Sources and Formats")
      .getOrCreateLocal()

    // Schema
    val carsSchema = StructType(Array(
      StructField("Name", StringType),
      StructField("Miles_per_Gallon", DoubleType),
      StructField("Cylinders", LongType),
      StructField("Displacement", DoubleType),
      StructField("Horsepower", LongType),
      StructField("Weight_in_lbs", LongType),
      StructField("Acceleration", DoubleType),
      StructField("Year", StringType),
      StructField("Origin", StringType)
    ))

    /*
      Reading a DF:
        - format
        - schema (optional)
        - zero or more options
     */
    val carsDF = spark.read
      .format("json")
      .schema(carsSchema)
      //.option("inferSchema", "true")
      .option("mode", "failFast") // dropMalformed, permissive (default)
      //.option("path", "src/main/resources/data/cars.json").load()
      .load("src/main/resources/data/cars.json")

    val carsDFWithOptionMap = spark.read
      .format("json")
      .options(Map(
        "mode"-> "failFast",
        "path"-> "src/main/resources/data/cars.json",
        "inferSchema" -> "true",
      ))
      .load()

    val carsDFWithJson = spark.read
      .options(Map(
        "mode"-> "failFast",
        "inferSchema" -> "true",
      ))
      .json("src/main/resources/data/cars.json")

    carsDF.show()
    carsDFWithOptionMap.show()
    carsDFWithJson.show()

    /*
      Writing DFs
        - format
        - save mode = overwrite, append, ignore, errorIfExists
        - path
        - zero or more options
     */

    carsDF.write
      .format("json")
      .mode(SaveMode.Overwrite)
      .option("path", "target/spark/data/cars.json")
      .save()
      //.save("target/spark/data/cars.json")
  }
}
