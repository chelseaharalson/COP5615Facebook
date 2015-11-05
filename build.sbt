lazy val commonSettings = Seq(
  organization := "com.cop5615",
  version := "1.0",
  scalaVersion := "2.11.5",
  libraryDependencies := Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.3.11",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
  )
)

lazy val root = (project in file(".")).
  aggregate(fbapi, fbclient)

lazy val fbapi = (project in file("fbapi")).
  settings(commonSettings: _*).
  settings(
    name := "fbapi"
  )

lazy val fbclient = (project in file("fbclient")).
  settings(commonSettings: _*).
  settings(
    name := "fbclient"
  )

