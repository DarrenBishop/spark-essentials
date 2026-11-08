package common

import org.apache.spark.sql.SparkSession

import java.net.NetworkInterface
import scala.jdk.CollectionConverters._

object LocalSession {
  /** Current non-loopback, site-local IPv4 (192.168.x / 10.x / 172.16–31.x).
   * Recomputed on every call, so it tracks DHCP. Falls back to loopback when
   * nothing suitable is found (e.g. offline). */
  def siteLocalIPv4(): String =
    NetworkInterface.getNetworkInterfaces.asScala
      .filter { ni => ni.isUp && !ni.isLoopback && !ni.isPointToPoint && !ni.isVirtual }
      .toList
      .sortBy { ni => if (ni.getName.startsWith("en")) 0 else 1 } // prefer physical NICs
      .flatMap(_.getInetAddresses.asScala.filter(_.isSiteLocalAddress))
      .collectFirst(_.getHostAddress)
      .getOrElse("127.0.0.1")

  lazy val host: String = siteLocalIPv4()

  implicit class LocalSparkSession(val builder: SparkSession.Builder) extends AnyVal {
    def getOrCreateLocal(): SparkSession = builder
      .config("spark.master", "local[*]")
      .config("spark.driver.bindAddress", host)
      .config("spark.driver.host", host)
      .getOrCreate()
  }
}
