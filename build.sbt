name := "link-checker"

version := "1.0"

scalaVersion := "2.12.8"

libraryDependencies ++= {
  lazy val akkaVersion = "2.5.21"
  Seq(
    "org.slf4j"           % "slf4j-api"         % "1.7.25",
    "ch.qos.logback"      % "logback-classic"   % "1.2.3",
    "com.typesafe.akka"  %% "akka-slf4j"        % akkaVersion,
    "com.typesafe.akka"  %% "akka-actor"        % akkaVersion,
    "com.typesafe.akka"  %% "akka-testkit"      % akkaVersion,
    "org.asynchttpclient" % "async-http-client" % "2.8.1",
    "org.jsoup"           % "jsoup"             % "1.11.3",
    // test dependencies
    "org.scalatest"      %% "scalatest"         % "3.0.5" % Test
  )
}
