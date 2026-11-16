import org.apache.spark.sql.SparkSession

package object common {
  def debug(): Unit = {
    println(s"Debug: detected and using ${LocalSession.host} for Spark bind-address...")

    sys.env.filter(_._1.startsWith("DYL")).foreach(println)

    println(s"Hadoop: ${org.apache.hadoop.util.VersionInfo.getVersion}")
    println(s"Architecture: ${System.getProperty("os.arch")}")
    println(s"Native Hadoop loaded: ${org.apache.hadoop.util.NativeCodeLoader.isNativeCodeLoaded}")
  }

  implicit class LocalSparkSession(val builder: SparkSession.Builder) extends AnyVal {
    def getOrCreateLocal(): SparkSession = builder
      .config("spark.master", "local[*]")
      .config("spark.driver.bindAddress", LocalSession.host)
      .config("spark.driver.host", LocalSession.host)
      .getOrCreate()
  }
}
