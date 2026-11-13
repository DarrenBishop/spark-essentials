ThisBuild / version := "0.3"

ThisBuild / scalaVersion := "2.13.18"

ThisBuild / fork := true

lazy val scalacOptionsTask = Def.task {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((3, _))           => Seq("-source:3.2", "-java-output-version:8", "-explain")
    case Some((2, 12))          => Seq("-Xsource:2.12", "-target:8", "-explaintypes", "-Ypartial-unification", "-Ywarn-macros:after")
    case Some((2, n)) if n < 12 => Seq("-Xsource:2.12", "-target:8", "-explaintypes", "-Ypartial-unification")
    case Some((2, _))           => Seq("-Xsource:2.13", "-target:8", "-explaintypes", "-Ymacro-annotations")
    case _                      => Nil
  }
}

val sparkVersion = "4.1.1"
val postgresVersion = "42.7.7"
val log4jVersion = "2.25.4"

lazy val `spark-essentials` = (project in file("."))
  .settings(
    //mdoc / aggregate := false,
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % sparkVersion,
      "org.apache.spark" %% "spark-sql" % sparkVersion,
      // logging
      "org.apache.logging.log4j" % "log4j-api" % log4jVersion,
      "org.apache.logging.log4j" % "log4j-core" % log4jVersion,
      // postgres for DB connectivity
      "org.postgresql" % "postgresql" % postgresVersion
    ),
  )

lazy val docs = (project in file("site"))
  .dependsOn(`spark-essentials`)
  .settings(
    scalacOptions ++= scalacOptionsTask.value,
    javaOptions += "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
    mdocExtraArguments ++= Seq(
      "--in", s"${(ThisBuild / baseDirectory).value}/HadoopWindowsUserSetup.md",
      "--out", s"${target.value}/mdoc/01-Welcome/HadoopWindowsUserSetup.md",
    ),
  )
