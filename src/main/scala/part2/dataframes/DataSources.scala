package part2.dataframes

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.types.{DateType, DoubleType, LongType, StringType, StructField, StructType}
import common._

object DataSources {
  debug()

  val spark = SparkSession.builder()
    .appName("Data Sources and Formats")
    .legacyDates()
    .getOrCreateLocal()


  def main(args: Array[String]): Unit = {

    // Schema
    val carsSchema = StructType(Array(
      StructField("Name", StringType),
      StructField("Miles_per_Gallon", DoubleType),
      StructField("Cylinders", LongType),
      StructField("Displacement", DoubleType),
      StructField("Horsepower", LongType),
      StructField("Weight_in_lbs", LongType),
      StructField("Acceleration", DoubleType),
      StructField("Year", DateType),
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

    carsDF.show()
    carsDFWithOptionMap.show()

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
      .option("path", target / "cars.json")
      .save()
      //.save(target / "cars.json")

    // JSON flags
    spark.read
      //.format("json")
      .schema(carsSchema)
      .option("dateFormat", "yyyy-MM-dd") // couple with schema; if Spark fails parsing, it will put null (i.e. permissive)
      .option("allowSingleQuotes", "true")
      .option("compression", "uncompressed") // bzip2, gzip, lz4 (7zip), snappy, and deflate
      //.load("src/main/resources/data/cars.json")
      .json("src/main/resources/data/cars.json")
      .show()

    // CSV flags
    val stocksSchema = StructType(Array(
      StructField("symbol", StringType),
      StructField("date", DateType),
      StructField("price", DoubleType),
    ))

    val stocksDF = spark.read
      .format("csv")
      .schema(stocksSchema)
      .option("dateFormat", "MMM dd yyyy")
      .option("header", "true")
      .option("sep", ",")
      .option("nullValue", "")
      .load("src/main/resources/data/stocks.csv")

    stocksDF.printSchema()
    stocksDF.show()

    // Parquet files
    carsDF.write
      .mode(SaveMode.Overwrite)
      //.format("parquet")
      //.parquet(target / "cars.parquet")
      .save(target / "cars.parquet")

    // Text files
    spark.read
      .text("src/main/resources/data/sampleTextFile.txt")
      .show()

    // Reading from a remote DB
    val employeesDF = spark.read
      .format("jdbc")
      .option("driver", DB.driver)
      .option("url", DB.url)
      .option("user", DB.user)
      .option("password", DB.password)
      .option("dbtable", "public.employees")
      .load()

    employeesDF.show()
  }
}
