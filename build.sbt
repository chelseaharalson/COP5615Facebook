import AssemblyKeys._

assemblySettings

jarName in assembly := "project1.jar"

val sprayV = "1.3.3"

lazy val commonSettings = Seq(
  organization := "com.cop5615",
  version := "1.0",
  scalaVersion := "2.11.5",
  scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
  resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  libraryDependencies := Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.3.11",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-json"    % "1.3.2",
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-client" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",
    "com.github.nscala-time" %% "nscala-time" % "2.4.0",
    "org.gnieh" %% "spray-session" % "0.1.0-SNAPSHOT"
  )
)

lazy val root = (project in file(".")).
  aggregate(fbapi, fbclient, common)

lazy val fbapi = (project in file("fbapi")).
  settings(commonSettings: _*).
  settings(
    name := "fbapi"
  ).dependsOn(common)

lazy val fbclient = (project in file("fbclient")).
  settings(commonSettings: _*).
  settings(
    name := "fbclient"
  ).dependsOn(common)

lazy val common = (project in file("common")).
  settings(commonSettings: _*).
  settings(
    name := "common"
  )
