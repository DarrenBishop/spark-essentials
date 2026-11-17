import org.apache.spark.sql.SparkSession

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
}
