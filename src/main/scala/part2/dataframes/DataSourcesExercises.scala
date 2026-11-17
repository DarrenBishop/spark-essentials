package part2.dataframes

import common._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{SaveMode, SparkSession}

/**
 * Exercises:
 *  - read the movies DF, then write it as
 *    - tab-separated values file
 *    - snappy parquet
 *    - table `public.movies` in the Postgres DB
 */
object DataSourcesExercises {
  debug()

  val spark = SparkSession.builder()
    .appName("Data Sources and Formats")
    .legacyDates()
    .getOrCreateLocal()

  def main(args: Array[String]): Unit = {
    val moviesSchema = StructType(Array(
      StructField("Creative_Type", StringType),
      StructField("Director", StringType),
      StructField("Distributor", StringType),
      StructField("IMDB_Rating", DoubleType),
      StructField("IMDB_Votes", LongType),
      StructField("MPAA_Rating", StringType),
      StructField("Major_Genre", StringType),
      StructField("Production_Budget", LongType),
      StructField("Release_Date", DateType),
      StructField("Rotten_Tomatoes_Rating", LongType),
      StructField("Running_Time_min", LongType),
      StructField("Source", StringType),
      StructField("Title", StringType),
      StructField("US_DVD_Sales", LongType),
      StructField("US_Gross", LongType),
      StructField("Worldwide_Gross", LongType),
    ))

    val moviesDF = spark.read
      .schema(moviesSchema)
      .option("dateFormat", "dd-MMM-yy")
      .option("allowSingleQuotes", "true")
      .option("compression", "uncompressed") // bzip2, gzip, lz4 (7zip), snappy, and deflate
      .json("src/main/resources/data/movies.json")

    moviesDF.printSchema()
    moviesDF.show()

    moviesDF.write
      .option("header", "true")
      .option("sep", "\t")
      .option("nullValue", "")
      .mode(SaveMode.Overwrite)
      .csv(target / "movies.tsv")

    moviesDF.write
      .option("compression", "snappy")
      .mode(SaveMode.Overwrite)
      .parquet(target / "movies.parquet")

    moviesDF.write
      .format("jdbc")
      .option("driver", DB.driver)
      //.option("url", DB.url)
      .option("user", DB.user)
      .option("password", DB.password)
      //.option("dbtable", "public.movies")
      .mode(SaveMode.Overwrite)
      //.save()
      .jdbc(DB.url, "public.movies", noProps)
  }
}
