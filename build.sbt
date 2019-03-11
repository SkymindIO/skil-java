import scala.sys.process._

name := "skil-java"
version := "1.0.0-SNAPSHOT"
description := "A Scala wrapper for skil-java, deep learning model life-cycle management for humans."

scalaVersion := "2.11.12"

resolvers in ThisBuild ++= Seq(
  Resolver.sonatypeRepo("snapshots")
)

cleanFiles += baseDirectory.value / "lib"
val mvnInstall = Seq("mvn", "install", "-q", "-f", "sbt-pom.xml")
val operatingSystem = sys.props("os.name").toLowerCase.substring(0, 3)
update := {
  operatingSystem match {
    case "win" => { Seq("cmd", "/C") ++ mvnInstall !; update.value }
    case _     => { mvnInstall !; update.value }
  }
}

libraryDependencies ++= {

  val dl4jVersion = "1.0.0-SNAPSHOT"
  val skilVersion = "1.0.0-SNAPSHOT"
  val logback = "1.2.3"
  val scalaCheck = "1.13.5"
  val scalaTest = "3.0.5"

  Seq(
    "org.slf4j" % "slf4j-api" % "1.7.25",
    "ch.qos.logback" % "logback-classic" % logback,
    "org.nd4j" % "nd4j-native" % dl4jVersion % "test",
    "org.scalacheck" %% "scalacheck" % scalaCheck % "test",
    "org.scalatest" %% "scalatest" % scalaTest % "test",
    "io.swagger" % "swagger-annotations" % "1.5.17",
    "com.squareup.okhttp" % "okhttp" % "2.7.5",
    "com.squareup.okhttp" % "logging-interceptor" % "2.7.5",
    "com.google.code.gson" % "gson" % "2.8.1",
    "org.threeten" % "threetenbp" % "1.3.5" % "compile",
    "io.gsonfire" % "gson-fire" % "1.8.0" % "compile",
    "junit" % "junit" % "4.12" % "test",
    "com.novocode" % "junit-interface" % "0.10" % "test"
  )
}

scalacOptions in ThisBuild ++= Seq("-language:postfixOps",
  "-language:implicitConversions",
  "-language:existentials",
  "-feature",
  "-deprecation")

lazy val standardSettings = Seq(
  organization := "ai.skymind",
  organizationName := "Skymind",
  startYear := Some(2016),
  licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html")),
  homepage := Some(url("https://github.com/SkymindIO/skil-java")),
  crossScalaVersions := Seq("2.11.12", "2.10.7"),
  scalacOptions ++= Seq(
    "-encoding",
    "UTF-8",
    "-Xlint",
    "-deprecation",
    "-Xfatal-warnings",
    "-feature",
    "-language:postfixOps",
    "-unchecked"
  )
)

//parallelExecution in Test := false
////scalafmtOnCompile in ThisBuild := true
////scalafmtTestOnCompile in ThisBuild := true
//test in assembly := {}
//assemblyMergeStrategy in assembly := {
//  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
//  case x                             => MergeStrategy.first
//}

lazy val root = (project in file("."))
  .settings(standardSettings)
  .settings(
    name := "skil-java",
    fork := true
  )