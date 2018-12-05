name := "champ-wsman"


libraryDependencies ++= Seq(
  //  "com.tresata" %% "akka-http-spnego" % "0.2.0", in-line copy as not 2.12
  "com.ning" % "async-http-client" % "1.9.40",

  "org.apache.james" % "apache-mime4j-core" % "0.8.2",
  "org.apache.james" % "apache-mime4j-dom" % "0.8.2",
  "org.apache.mahout.commons" % "commons-cli" % "2.0-mahout"
)

