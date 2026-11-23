package part2.dataframes

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

import common._

/**
 * Exercises
 *
 * 1. Sum up ALL the profits of ALL the movies in the DF
 * 2. Count how many distinct directors we have
 * 3. Show the mean and standard deviation of US gross revenue for the movies
 * 4. Compute the average IMDB rating and the average US gross revenue PER DIRECTOR
 */
object AggregationsExercises {

  debug()

  lazy val spark = SparkSession.builder()
    .appName("Aggregations and Grouping")
    .getOrCreateLocal()

  import spark.implicits._

  def main(args: Array[String]): Unit = {

    val moviesDF = spark.read
      .option("inferSchema", "true")
      .json("src/main/resources/data/movies.json")

    // Answer 1
    moviesDF
      .select(
        "Title",
        "US_Gross",
        "Worldwide_Gross",
        "US_DVD_Sales",
      )
      .withColumn("Total_Sales", $$"US_Gross" + $$"Worldwide_Gross" + $$"US_DVD_Sales")
      .agg("All_Sales" := sum($$"Total_Sales"))
      .show()

    moviesDF
      .agg("All_Sales" := sum($$"US_Gross" + $$"Worldwide_Gross" + $$"US_DVD_Sales"))
      .show()

    moviesDF
      .select("All_Sales" := sum($$"US_Gross" + $$"Worldwide_Gross" + $$"US_DVD_Sales"))
      .show()

    // Answer 2
    moviesDF
      .select("Directors (Occurred)" := count($"Director"), "Directors (Distinct)" := count_distinct($"Director"))
      .show()
    moviesDF
      .select("Directors" := count_distinct($"Director"))
      .show()

    // Answer 3
    moviesDF
      .select(
        "Avg. Revenue" := mean($$"US_Gross"),
        "Std.D. Revenue" := stddev($$"US_Gross"),
      )
      .show()

    // Answer 4
    moviesDF
      .where(isnotnull($"Director"))
      .groupBy($"Director")
      .agg(
        "Avg. IMDB Rating" := avg($$"IMDB_Rating"),
        "Avg. US Gross" := mean($$"US_Gross"),
        "Total. US Gross" := sum($$"US_Gross"),
        "#. of Movies" := count($$"US_Gross"),
      )
      .orderBy(
        $"`Avg. IMDB Rating`".desc_nulls_last,
        $"`Total. US Gross`".desc_nulls_last
      )
      .show()
  }
}
