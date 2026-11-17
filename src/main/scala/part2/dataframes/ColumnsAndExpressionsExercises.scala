package part2.dataframes

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{column, expr, zeroifnull}
import common.{DataFrameOps, _}

/**
 * Exercises:
 *  1. Read movies DF and select 2 columns of your choice
 *  2. Create another column summing-up the total of the movies = US_Gross + Worldwide_Gross + US_DVD_Sales
 *  3. Select all the COMEDY movies with IMDB rating above 6
 *
 *  Use as many versions as possible
 */
object ColumnsAndExpressionsExercises {
  debug()

  val spark = SparkSession.builder()
    .appName("Data Sources and Formats")
    .legacyDates()
    .getOrCreateLocal()

  import spark.implicits._

  def main(args: Array[String]): Unit = {

    // Answer 1
    val moviesDF = spark.read
      .option("inferSchema", "true")
      .json("src/main/resources/data/movies.json")

    moviesDF
      .select(
        "Title",
        "US_Gross",
        "Worldwide_Gross",
        "US_DVD_Sales",
      )
      .two()

    moviesDF
      .select(
        moviesDF.col("Title"),
        $"US_Gross",
        column("Worldwide_Gross"),
        expr("US_DVD_Sales"),
      )
      .two()

    moviesDF
      .selectX(
        "Title",
        $"US_Gross",
        column("Worldwide_Gross"),
        expr("US_DVD_Sales"),
      )
      .two()

    moviesDF
      .selectExpr(
        "Title",
        "US_Gross",
        "Worldwide_Gross",
        "US_DVD_Sales",
      )
      .two()

    // Answer 2
    moviesDF
      .withColumn("Total_Sales", zeroifnull($"US_Gross") + zeroifnull($"Worldwide_Gross") + zeroifnull($"US_DVD_Sales"))
      .five()

    moviesDF
      .withColumn("Total_Sales", expr("zeroifnull(US_Gross) + zeroifnull(Worldwide_Gross) + zeroifnull(US_DVD_Sales)"))
      .five()

    moviesDF
      .selectExpr("US_Gross", "Worldwide_Gross", "US_DVD_Sales", "zeroifnull(US_Gross) + zeroifnull(Worldwide_Gross) + zeroifnull(US_DVD_Sales) as Total_Sales")
      .five()

    val totalSales = zeroifnull($"US_Gross") + zeroifnull($"Worldwide_Gross") + zeroifnull($"US_DVD_Sales")

    // Answer 3
    moviesDF
      .where($"Major_Genre" ilike "%COMEDY")
      .filter(zeroifnull($"IMDB_Rating") > 6)
      .withColumn("Total_Sales", totalSales)
      .show(5)

    moviesDF
      .where($"Major_Genre" ilike "%COMEDY" and zeroifnull($"IMDB_Rating") > 6)
      .withColumn("Total_Sales", totalSales)
      .show(5)

    moviesDF
      .where("Major_Genre ilike '%COMEDY' and zeroifnull(IMDB_Rating) > 6")
      .withColumn("Total_Sales", totalSales)
      .show(5)
  }
}
