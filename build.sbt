import sbtassembly.AssemblyPlugin.autoImport.*
import sbtassembly.{MergeStrategy, PathList}

ThisBuild / scalaVersion := "2.13.18"
ThisBuild / version      := "0.1.0"

addCommandAlias("formatAll", ";scalafmtAll;scalafmtSbt")
addCommandAlias("formatCheck", ";scalafmtCheckAll;scalafmtSbtCheck")

run / fork         := true
run / connectInput := true

lazy val root = (project in file("."))
  .aggregate(
    generator,
    sparkStreaming
  )
  .settings(
    name           := "ecommerce-pulse",
    publish / skip := true
  )

lazy val generator = (project in file("generator"))
  .settings(
    name                             := "ecommerce-pulse-generator",
    assembly / mainClass             := Some("GeneratorApp"),
    assembly / assemblyMergeStrategy := {
      case "reference.conf"              => MergeStrategy.concat
      case "version.conf"                => MergeStrategy.first
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x                             => MergeStrategy.first
    },
    libraryDependencies ++= Seq(
      "org.apache.kafka"  % "kafka-clients"          % "4.3.1",
      "com.typesafe"      % "config"                 % "1.4.9",
      "org.apache.pekko" %% "pekko-actor"            % "1.7.1",
      "org.apache.pekko" %% "pekko-stream"           % "1.7.1",
      "org.apache.pekko" %% "pekko-connectors-kafka" % "1.2.0",
      "io.circe"         %% "circe-core"             % "0.14.16",
      "io.circe"         %% "circe-generic"          % "0.14.16",
      "io.circe"         %% "circe-parser"           % "0.14.16",
      "ch.qos.logback"    % "logback-classic"        % "1.6.6"
    )
  )

lazy val sparkStreaming = (project in file("spark-streaming"))
  .settings(
    name := "ecommerce-pulse-spark-streaming",
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core"           % "3.5.9",
      "org.apache.spark" %% "spark-sql"            % "3.5.9",
      "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.5.9"
    )
  )
