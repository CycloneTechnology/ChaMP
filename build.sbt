import sbt.Keys.credentials
import sbt.{Credentials, file, project, _}

name := "champ"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8")
    sys.error("Java 8 is required for this project.")
}

val akkaVersion = "2.5.18"
val akkaStreamsVersion = akkaVersion
val akkaHttpVersion = "10.1.1"

lazy val commonSettings = Seq(
  organization := "com.cyclone",
  version := "0.9.0",
  scalaVersion := "2.12.7",
  crossScalaVersions := Seq("2.11.11", scalaVersion.value),
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaStreamsVersion withSources() withJavadoc(),
    "com.typesafe.akka" %% "akka-stream" % akkaStreamsVersion withSources() withJavadoc(),
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion withSources() withJavadoc(),
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion  % "test" withSources() withJavadoc(),
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion  % "test" withSources() withJavadoc(),
    "joda-time" % "joda-time" % "2.9.9" withSources() withJavadoc(),
    "com.google.guava" % "guava" % "23.0" withSources() withJavadoc(),
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
    "org.scalaz" %% "scalaz-core" % "7.2.26",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "org.scalatest" %% "scalatest" % "3.0.5" % "test",
    "org.scalacheck" %% "scalacheck" % "1.14.0" % "test",
    "junit" % "junit" % "4.12" % "test",
    "org.jmock" % "jmock" % "2.8.4" % "test",
    "org.jmock" % "jmock-legacy" % "2.8.4" % "test"
  ),
  dependencyOverrides += "org.scala-lang" % "scala-compiler" % scalaVersion.value,
  dependencyOverrides += "org.scala-lang" % "scala-library" % scalaVersion.value,
  dependencyOverrides += "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  parallelExecution in Test := false,
  resolvers ++= Seq(
    "Maven Central" at "https://repo1.maven.org/maven2",
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases"
  )
)

lazy val root = project.in(file(".")).
  settings(commonSettings: _*)
  .aggregate(ipmi, core, wsman)
 
lazy val ipmi = project.in(file("champ-ipmi")).
  settings(commonSettings: _*)
  .configs(IntegrationTest extend Test)
  .settings(Defaults.itSettings: _*)
  .dependsOn(
    core % "compile->compile;test->test;it->test"
  )

lazy val core = project.in(file("champ-core")).
  settings(commonSettings: _*)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)

lazy val wsman = project.in(file("champ-wsman")).
  settings(commonSettings: _*)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .dependsOn(
    core % "compile->compile;test->test"
  )

