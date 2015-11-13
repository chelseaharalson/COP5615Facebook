val sprayV = "1.3.3"

lazy val commonSettings = Seq(
  organization := "com.cop5615",
  version := "1.0",
  scalaVersion := "2.11.5",
  scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
  libraryDependencies := Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.3.11",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-json"    % "1.3.2",
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",
    "com.github.nscala-time" %% "nscala-time" % "2.4.0"
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
