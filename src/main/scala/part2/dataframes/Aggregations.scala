package part2.dataframes

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

import common._

object Aggregations {

  debug()

  lazy val spark = SparkSession.builder()
    .appName("Aggregations and Grouping")
    .getOrCreateLocal()

  import spark.implicits._

  def main(args: Array[String]): Unit = {

    val moviesDF = spark.read
      .option("inferSchema", "true")
      .json("src/main/resources/data/movies.json")

    // Count
    moviesDF.select(count($"Major_Genre")).show()

    // Count All
    moviesDF.select(count("*")).show()
    moviesDF.selectExpr("count(*)").show()

    // Count Distinct
    moviesDF.select(count_distinct($"Major_Genre")).show()

    // Approximate Count
    moviesDF.select(approx_count_distinct($"Major_Genre")).show()

    // Min & Max
    moviesDF.select(min($"IMDB_Rating")).show()
    moviesDF.selectExpr("min(IMDB_Rating)").show()

    // Sum
    moviesDF.select(sum($"US_Gross")).show()
    moviesDF.selectExpr("sum(US_Gross)").show()

    // Average (avg)
    moviesDF.select(avg($"Rotten_Tomatoes_Rating")).show()
    moviesDF.selectExpr("avg(Rotten_Tomatoes_Rating)").show()

    // Data Science e.g. Std. Deviation
    moviesDF.select(
      mean($"Rotten_Tomatoes_Rating"),
      stddev($"Rotten_Tomatoes_Rating")
    ).show()

    // Grouping
    moviesDF
      .groupBy($"Major_Genre")
      .count() // select count(*) from moviesDF group by Major_Genre
      .show()

    moviesDF
      .groupBy($"Major_Genre")
      .avg("IMDB_Rating")
      .show()

    moviesDF
      .groupBy($"Major_Genre")
      .agg(
        "Movies" := count("*"),
        "Avg Rating" := avg("IMDB_Rating")
      )
      .orderBy($"Avg Rating")
      .show()
  }
}
