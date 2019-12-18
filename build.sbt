import sbt.Keys.publishArtifact

resolvers += "Kamon" at "https://dl.bintray.com/kamon-io/snapshots/"
enablePlugins(JavaAgent)


name := "area51-kamon"
organization := "org.dmonix"
version := "0.0.0"
scalaVersion := "2.12.10"
publishArtifact := false
publishArtifact in (Compile, packageBin) := false
publishArtifact in (Compile, packageDoc) := false
publishArtifact in (Compile, packageSrc) := false


val akkaVersion = "2.5.25"
val akkaHttpVersion = "10.1.9"

// -----------------------------------------------------
//  Shared/common settings
// -----------------------------------------------------
lazy val baseSettings = Seq(
  version := "0.0.0",
  organization := "org.dmonix",
  scalaVersion := "2.12.10",
  fork := true, //needed to get the java-options set properly during sbt run
  publishArtifact := false,
  publishArtifact in (Compile, packageBin) := false,
  publishArtifact in (Compile, packageDoc) := false,
  publishArtifact in (Compile, packageSrc) := false,
  scalacOptions := Seq("-feature",
    "-language:postfixOps",
    "-language:implicitConversions",
    "-unchecked",
    "-deprecation",
    "-encoding", "utf8")
)

// -----------------------------------------------------
//  Shared/common code
// -----------------------------------------------------
lazy val common = project.in(file("common"))
  .settings(baseSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.2",
      "com.typesafe.akka" %% "akka-actor"   % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j"   % akkaVersion,
      "com.typesafe.akka" %% "akka-stream"  % akkaVersion,
      "com.typesafe.akka" %% "akka-http-core"  % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http"       % akkaHttpVersion,
      //"io.kamon" %% "kamon-instrumentation-common" % "2.0.1",
      //"io.kamon" %% "kamon-system-metrics" % "2.0.1",
      "io.kamon" %% "kamon-bundle" % "2.0.5",
      "io.kamon" %% "kamon-prometheus" % "2.0.0",
      "io.kamon" %% "kamon-zipkin" % "2.0.0",
      "org.slf4j" % "slf4j-simple" % "1.7.29"
    ),
  )

// -----------------------------------------------------
//  AKKA-HTTP
// -----------------------------------------------------
lazy val `akka-http-src` = project.in(file("akka-http/."))
  .settings(baseSettings)
  .dependsOn(common)

lazy val server = project.in(file("akka-http/server"))
  .settings(baseSettings)
  .settings(
    sourceDirectory := (sourceDirectory in `akka-http-src`).value,
    mainClass in (Compile, run) := Some("org.dmonix.area51.kamon.SimpleKamonServer"),
    javaOptions ++= Seq(
      "-Dkamon.prometheus.embedded-server.port=8000",
      "-Dkamon.trace.random-sampler.probability=1",
      "-Dkamon.zipkin.host=127.0.0.1",
      "-Dkamon.environment.service=area51-server"
    )
  ).dependsOn(common)

lazy val client = project.in(file("akka-http/client"))
  .settings(baseSettings)
  .settings(
    sourceDirectory := (sourceDirectory in `akka-http-src`).value,
    mainClass in (Compile, run) := Some("org.dmonix.area51.kamon.SimpleKamonClient"),
    javaOptions ++= Seq(
      "-Dkamon.prometheus.embedded-server.port=9000",
      "-Dkamon.trace.random-sampler.probability=1",
      "-Dkamon.zipkin.host=127.0.0.1",
      "-Dkamon.environment.service=area51-client"
    )
  ).dependsOn(common)
