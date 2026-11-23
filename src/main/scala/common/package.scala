import org.apache.spark.sql.{Column, DataFrame, SparkSession}

import java.nio.file.Path
import java.util.Properties

package object common {
  def debug(): Unit = {
    println(s"Debug: detected and using ${LocalSession.host} for Spark bind-address...")

    sys.env.filter(_._1.startsWith("DYL")).foreach(println)

    println(s"Hadoop: ${org.apache.hadoop.util.VersionInfo.getVersion}")
    println(s"Architecture: ${System.getProperty("os.arch")}")
    println(s"Native Hadoop loaded: ${org.apache.hadoop.util.NativeCodeLoader.isNativeCodeLoaded}")
  }

  def noProps = new Properties()

  implicit class LocalSparkSession(val builder: SparkSession.Builder) extends AnyVal {
    def legacyDates(): SparkSession.Builder = builder
      .config("spark.sql.legacy.timeParserPolicy", "LEGACY")

    def getOrCreateLocal(): SparkSession = builder
      .config("spark.master", "local[*]")
      .config("spark.driver.bindAddress", LocalSession.host)
      .config("spark.driver.host", LocalSession.host)
      .getOrCreate()
  }

  implicit def pathToString(path: Path): String = path.toString

  implicit class PathOps(parent: Path) {
    def / (path: String): Path = Path.of(parent.toString, path)
  }

  object target {
    def / (path: String): Path = Path.of("target/spark/data") / path
  }

  type ColumnSpec = Either[String, Column]

  implicit class DataFrameOps(val df: DataFrame) extends AnyVal {
    def one(): Unit = df.show(1)
    def two(): Unit = df.show(2)
    def five(): Unit = df.show(5)
    def selectX(column: ColumnSpec, rest: ColumnSpec*): DataFrame = {
      df.select((column +: rest).map {
        case Left(string) => new Column(string)
        case Right(column) => column
      }: _*)
    }
  }

  implicit def stringToColumnSpec(string: String): ColumnSpec = Left(string)
  implicit def columnToColumnSpec(column: Column): ColumnSpec = Right(column)

  implicit class ColumnOps(val column: Column) extends AnyVal {
    def := (name: String): Column = column as name
  }

  implicit class StringColumnOps(val name: String) extends AnyVal {
    def := (column: Column): Column = column := name
  }

  implicit class ColumnInterpolator(val sc: StringContext) extends AnyVal {
    import org.apache.spark.sql.functions.{column, zeroifnull}

    /**
     * Money - wrap column in [[zeroifnull]]
     */
    def $$ (parts: Any*): Column = {
      val name = sc.s(parts: _*)
      s"zeroed($name)" := zeroifnull(column(name))
    }
  }
}
