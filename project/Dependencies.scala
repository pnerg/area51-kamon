import sbt._


object Dependencies extends AutoPlugin {

  object autoImport {

    val `scala-logging` = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.3"

    val `akka-actor` = "com.typesafe.akka" %% "akka-actor" % "2.6.14"
    val `akka-slf4j` = "com.typesafe.akka" %% "akka-slf4j" % `akka-actor`.revision
    val `akka-stream` = "com.typesafe.akka" %% "akka-stream" % `akka-actor`.revision

    val `akka-http` = "com.typesafe.akka" %% "akka-http" % "10.2.4"
    val `akka-http-core` = "com.typesafe.akka" %% "akka-http-core" % `akka-http`.revision

    val `kamon-bundle` = "io.kamon" %% "kamon-bundle" % "2.2.0"
    val `kamon-prometheus` = "io.kamon" %% "kamon-prometheus" % `kamon-bundle`.revision
    val `kamon-zipkin` = "io.kamon" %% "kamon-zipkin" % `kamon-bundle`.revision

    val `slf4j-simple` = "org.slf4j" % "slf4j-simple" % "1.7.30"

  }

}