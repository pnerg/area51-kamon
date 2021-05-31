package org.dmonix.area51.kamon

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import com.typesafe.config.ConfigFactory
import kamon.Kamon

import scala.util.{Failure, Success}

/**
  * A very simple akka-http based client
  * @author Peter Nerg
  */
object SimpleKamonClient extends App {

  Kamon.init(ConfigFactory.defaultOverrides()
    .withFallback(ConfigFactory.defaultApplication)
    .withFallback(ConfigFactory.defaultReference())
    .resolve())

  private implicit val system = ActorSystem("simple-kamon-client")
  private implicit val executionContext = system.dispatcher

  val port = 9696
  while(true){
    Http().singleRequest(HttpRequest(uri = s"http://localhost:$port/forward"))
    Http().singleRequest(HttpRequest(uri = s"http://localhost:$port/forward"))
    Http().singleRequest(HttpRequest(uri = s"http://localhost:$port/forward"))
    Http().singleRequest(HttpRequest(uri = s"http://localhost:$port/forward"))
    Http().singleRequest(HttpRequest(uri = s"http://localhost:$port/forward"))
  }

  Http()
    .singleRequest(HttpRequest(uri = s"http://localhost:$port/api/execute/do-it"))
    .onComplete {
      case Success(response) =>
        response.headers.foreach(println)
        System.exit(0)
      case Failure(ex) =>
        ex.printStackTrace()
        System.exit(1)
    }

}
