import sbt.Keys.publishArtifact

resolvers += "Kamon" at "https://dl.bintray.com/kamon-io/snapshots/"

name := "area51-kamon"
organization := "org.dmonix"
version := "0.0.0"
scalaVersion := "2.13.7"
publishArtifact := false
publishArtifact in (Compile, packageBin) := false
publishArtifact in (Compile, packageDoc) := false
publishArtifact in (Compile, packageSrc) := false


// -----------------------------------------------------
//  Shared/common settings
// -----------------------------------------------------
lazy val baseSettings = Seq(
  version := "0.0.0",
  organization := "org.dmonix",
  scalaVersion := "2.13.7",
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
    "-encoding", "utf8"),
  libraryDependencies ++= {
    Seq(
      `scala-logging`,
      `slf4j-simple`
    )
  }

)

lazy val kamonDependencies = Seq(
  libraryDependencies ++= {
    Seq(
      `scala-logging`,
      `kamon-bundle`,
      `kamon-prometheus`,
      `kamon-zipkin`,
      `kamon-opentelemetry`,
      `slf4j-simple`
    )
  }
)


lazy val akkaDependencies = Seq(
  libraryDependencies ++= {
    Seq(
      `scala-logging`,
      `akka-actor`,
      `akka-slf4j`,
      `akka-stream`,
      `kamon-bundle`,
      `kamon-prometheus`,
      `kamon-zipkin`,
      `kamon-opentelemetry`,
      `slf4j-simple`
    )
  }
)

lazy val akkaHttpDependencies = Seq(
  libraryDependencies ++= {
    Seq(
      `scala-logging`,
      `akka-actor`,
      `akka-slf4j`,
      `akka-stream`,
      `akka-http-core`,
      `akka-http`,
      `kamon-bundle`,
      `kamon-prometheus`,
      `kamon-zipkin`,
      `kamon-opentelemetry`,
      `slf4j-simple`
    )
  }
)



// -----------------------------------------------------
//  Shared/common code
// -----------------------------------------------------
lazy val common = project.in(file("common"))
  .settings(baseSettings)
  .settings(kamonDependencies)

lazy val instrumentation = project.in(file("instrumentation"))
  .settings(baseSettings)
  .settings(
    libraryDependencies ++= Seq(
      "io.kamon" % "kanela-agent" % "1.0.17" % "provided"
    )
  )
  .dependsOn(common)

lazy val reporters = project.in(file("reporters"))
  .settings(baseSettings)
  .settings(kamonDependencies)

// -----------------------------------------------------
//  AKKA
// -----------------------------------------------------
lazy val akka = project.in(file("akka"))
  .settings(baseSettings)
  .settings(akkaDependencies)
  .settings(
    mainClass in (Compile, run) := Some("org.dmonix.area51.kamon.akka.PingerPonger"),
    javaOptions ++= Seq(
      "-Dkamon.trace.random-sampler.probability=1",
      "-Dkamon.zipkin.host=127.0.0.1",
      "-Dkamon.environment.service=area51-akka"
    )
  ).dependsOn(common, reporters, instrumentation)

lazy val `akka-http-src` = project.in(file("akka-http/."))
  .settings(baseSettings)
  .settings(akkaDependencies)
  .settings(akkaHttpDependencies)
  .dependsOn(common)

lazy val server = project.in(file("akka-http/server"))
  .settings(baseSettings)
  .settings(akkaDependencies)
  .settings(akkaHttpDependencies)
  .settings(
    sourceDirectory := (sourceDirectory in `akka-http-src`).value,
    mainClass in (Compile, run) := Some("org.dmonix.area51.kamon.SimpleKamonServer"),
    javaOptions ++= Seq(
      "-Dkamon.prometheus.embedded-server.port=8000",
      "-Dkamon.trace.random-sampler.probability=1",
      "-Dkamon.zipkin.host=127.0.0.1",
      "-Dkamon.environment.service=area51-server",
      "-Dkanela.show-banner=false",
      "-XX:NativeMemoryTracking=detail"
    )
  ).dependsOn(common, reporters, instrumentation)

lazy val client = project.in(file("akka-http/client"))
  .settings(baseSettings)
  .settings(akkaDependencies)
  .settings(akkaHttpDependencies)
  .settings(
    sourceDirectory := (sourceDirectory in `akka-http-src`).value,
    mainClass in (Compile, run) := Some("org.dmonix.area51.kamon.SimpleKamonClient"),
    javaOptions ++= Seq(
      "-Dkamon.prometheus.embedded-server.port=9000",
      "-Dkamon.trace.random-sampler.probability=1",
      "-Dkamon.zipkin.host=127.0.0.1",
      "-Dkamon.environment.service=area51-client"
    )
  ).dependsOn(common, reporters)

lazy val debug = project.in(file("akka-http/debug"))
  .settings(baseSettings)
  .settings(kamonDependencies)
  .settings(akkaDependencies)
  .settings(akkaHttpDependencies)
  .settings(
    mainClass in (Compile, run) := Some("org.dmonix.area51.kamon.DefaultDebugKamonServer"),
    javaOptions ++= Seq(
      "-Dkamon.prometheus.embedded-server.port=9000",
      "-Dkamon.trace.random-sampler.probability=1",
      "-Dkamon.zipkin.host=127.0.0.1",
      "-Dkamon.environment.service=area51-kamon-debug-default-settings"
    )
  )

lazy val issues = project.in(file("issues"))
  .settings(baseSettings)
  .settings(
    mainClass in (Compile, run) := Some("org.dmonix.area51.kamon.TestApp"),
    javaOptions ++= Seq(
      "-Dkamon.prometheus.embedded-server.port=9000",
      "-Dkamon.trace.random-sampler.probability=1",
      "-Dkamon.zipkin.host=127.0.0.1",
      "-Dkamon.environment.service=area51-kamon-issues"
    ),
    libraryDependencies ++= {
      Seq(
        `kamon-bundle`,
        //`kamon-prometheus`,
        //`kamon-zipkin`,
        //`kamon-opentelemetry`,
      )
    }
  )
