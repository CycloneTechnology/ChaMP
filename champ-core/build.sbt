name := "champ-core"

resolvers += "bintray" at "http://jcenter.bintray.com"

val akkaHttpVersion = "10.1.5"

libraryDependencies ++= Seq(
  "commons-codec" % "commons-codec" % "1.11",
  "org.apache.commons" % "commons-lang3" % "3.8",
  "org.scala-lang.modules" %% "scala-xml" % "1.1.0",
  "com.github.mkroli" %% "dns4s-akka" % "0.11",
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test"
)

