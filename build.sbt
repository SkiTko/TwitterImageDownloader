name := """TwitterImageDownloader"""

version := "1.0"

scalaVersion := "2.11.8"



val akkaVersion = "2.4.12"

// Uncomment to use Akka
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion

// https://mvnrepository.com/artifact/com.typesafe.akka/akka-stream_2.11
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion

libraryDependencies += "com.typesafe.akka" %% "akka-http-experimental" % "2.4.11"

// https://mvnrepository.com/artifact/com.typesafe.scala-logging/scala-logging_2.11
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"

// https://mvnrepository.com/artifact/ch.qos.logback/logback-core
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7"

// https://mvnrepository.com/artifact/org.twitter4j/twitter4j-core
libraryDependencies += "org.twitter4j" % "twitter4j-core" % "4.0.5"

// https://mvnrepository.com/artifact/org.twitter4j/twitter4j-stream
libraryDependencies += "org.twitter4j" % "twitter4j-stream" % "4.0.5"

// https://mvnrepository.com/artifact/com.typesafe/config
libraryDependencies += "com.typesafe" % "config" % "1.3.1"

// https://mvnrepository.com/artifact/com.iheart/ficus_2.11
libraryDependencies += "com.iheart" %% "ficus" % "1.3.0"


// Change this to another test framework if you prefer
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

// https://mvnrepository.com/artifact/com.typesafe.akka/akka-testkit_2.11
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"

libraryDependencies += "org.mockito" % "mockito-core" % "2.2.11" % "test"