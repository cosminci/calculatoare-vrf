ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.7"

lazy val root = (project in file("."))
  .settings(
    name := "calculatoare-vrf",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "squants" % "1.8.3"
    )
  )
